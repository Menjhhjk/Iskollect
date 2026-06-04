package com.iskollect.service;

import com.iskollect.exception.InvalidInputException;
import com.iskollect.model.ReportResult;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    public ReportResult getBottleSummary(int studentId, LocalDate from, LocalDate to) {
        String type = "BOTTLE_SUMMARY";
        try {
            validateStudentAndDates(studentId, from, to);
            if (tooOld(from)) {
                return ReportResult.failure(type, "From date cannot be more than 2 years in the past.");
            }
            String sql = "SELECT student_id, COALESCE(SUM(bottles), 0) AS total_bottles, "
                    + "COALESCE(SUM(points), 0) AS total_points FROM transactions "
                    + "WHERE student_id = ? AND date BETWEEN ? AND ? GROUP BY student_id";
            List<Map<String, Object>> rows = query(sql, studentId, from, to);
            Map<String, Object> totals = rows.isEmpty() ? Map.of("total_bottles", 0, "total_points", 0) : rows.get(0);
            return ReportResult.success(type, rows, totals);
        } catch (InvalidInputException | SQLException e) {
            return ReportResult.failure(type, e.getMessage());
        }
    }

    public ReportResult getWeeklyLeaderboard() {
        String type = "WEEKLY_LEADERBOARD";
        try {
            String sql = "SELECT s.student_id, s.name, COALESCE(SUM(t.bottles), 0) AS weekly_bottles "
                    + "FROM students s LEFT JOIN transactions t ON s.student_id = t.student_id "
                    + "AND t.date >= DATE_TRUNC('week', CURRENT_DATE)::date "
                    + "GROUP BY s.student_id, s.name ORDER BY weekly_bottles DESC, s.name ASC";
            return ReportResult.success(type, query(sql), Map.of());
        } catch (SQLException e) {
            return ReportResult.failure(type, e.getMessage());
        }
    }

    public ReportResult getPointsLedger(int studentId, LocalDate from, LocalDate to) {
        String type = "POINTS_LEDGER";
        try {
            validateStudentAndDates(studentId, from, to);
            if (tooOld(from)) {
                return ReportResult.failure(type, "From date cannot be more than 2 years in the past.");
            }
            String sql = "SELECT 'SUBMISSION' AS entry_type, trans_id AS entry_id, date AS entry_date, points AS points_delta "
                    + "FROM transactions WHERE student_id = ? AND date BETWEEN ? AND ? "
                    + "UNION ALL SELECT 'REDEMPTION', redeem_id, redeem_date, -points_deducted "
                    + "FROM redeemed_rewards WHERE student_id = ? AND redeem_date BETWEEN ? AND ? "
                    + "ORDER BY entry_date DESC, entry_id DESC";
            return ReportResult.success(type, query(sql, studentId, from, to, studentId, from, to), Map.of());
        } catch (InvalidInputException | SQLException e) {
            return ReportResult.failure(type, e.getMessage());
        }
    }

    public ReportResult getRedemptionReport(Boolean fulfilledOnly) {
        String type = "REDEMPTION_REPORT";
        try {
            String sql = "SELECT rr.*, s.name AS student_name, r.name AS reward_name FROM redeemed_rewards rr "
                    + "JOIN students s ON rr.student_id = s.student_id "
                    + "JOIN rewards r ON rr.reward_id = r.reward_id "
                    + (fulfilledOnly == null ? "" : "WHERE rr.is_fulfilled = ? ")
                    + "ORDER BY rr.redeem_date DESC, rr.redeem_id DESC";
            List<Map<String, Object>> rows = fulfilledOnly == null ? query(sql) : query(sql, fulfilledOnly);
            return ReportResult.success(type, rows, Map.of("total_redemptions", rows.size()));
        } catch (SQLException e) {
            return ReportResult.failure(type, e.getMessage());
        }
    }

    public ReportResult getSystemSummary() {
        String type = "SYSTEM_SUMMARY";
        try {
            String sql = "SELECT "
                    + "(SELECT COALESCE(SUM(bottles), 0) FROM transactions) AS total_bottles_collected, "
                    + "(SELECT COALESCE(SUM(points), 0) FROM transactions) AS total_points_issued, "
                    + "(SELECT COUNT(*) FROM redeemed_rewards) AS total_redemptions";
            List<Map<String, Object>> rows = query(sql);
            return ReportResult.success(type, rows, rows.isEmpty() ? Map.of() : rows.get(0));
        } catch (SQLException e) {
            return ReportResult.failure(type, e.getMessage());
        }
    }

    private void validateStudentAndDates(int studentId, LocalDate from, LocalDate to) throws InvalidInputException {
        if (studentId <= 0) {
            throw new InvalidInputException("studentId must be greater than zero.");
        }
        if (from == null || to == null) {
            throw new InvalidInputException("from and to dates are required.");
        }
        if (from.isAfter(to)) {
            throw new InvalidInputException("from date must not be after to date.");
        }
    }

    private boolean tooOld(LocalDate from) {
        return from.isBefore(LocalDate.now().minusYears(2));
    }

    private List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof LocalDate) {
                    ps.setDate(i + 1, Date.valueOf((LocalDate) param));
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else {
                    ps.setObject(i + 1, param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    private List<Map<String, Object>> rows(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }
}
