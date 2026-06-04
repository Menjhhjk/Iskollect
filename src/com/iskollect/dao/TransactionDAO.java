package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Transaction;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public boolean insert(Transaction t) throws DatabaseException {
        String sql = "INSERT INTO transactions "
                + "(student_id, bottles, base_points, streak_bonus, badge_bonus, points, date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getStudentId());
            ps.setInt(2, t.getBottles());
            ps.setDouble(3, t.getBasePoints());
            ps.setDouble(4, t.getStreakBonus());
            ps.setDouble(5, t.getBadgeBonus());
            ps.setDouble(6, t.getPoints());
            ps.setDate(7, Date.valueOf(t.getDate()));
            boolean inserted = ps.executeUpdate() > 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setTransId(keys.getInt(1));
                }
            }
            return inserted;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert transaction.", e);
        }
    }

    public List<Transaction> getByStudentId(int studentId) throws DatabaseException {
        String sql = "SELECT * FROM transactions WHERE student_id = ? ORDER BY date DESC, trans_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return collect(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch transactions for student " + studentId, e);
        }
    }

    public List<Transaction> getByDateRange(int studentId, LocalDate from, LocalDate to) throws DatabaseException {
        String sql = "SELECT * FROM transactions WHERE student_id = ? AND date BETWEEN ? AND ? "
                + "ORDER BY date DESC, trans_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            return collect(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch transactions by date range.", e);
        }
    }

    public int getTotalBottles(int studentId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(bottles), 0) FROM transactions WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total bottles.", e);
        }
    }

    public double getTotalPoints(int studentId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(points), 0) FROM transactions WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total transaction points.", e);
        }
    }

    private List<Transaction> collect(PreparedStatement ps) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                transactions.add(map(rs));
            }
        }
        return transactions;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("trans_id"),
                rs.getInt("student_id"),
                rs.getInt("bottles"),
                rs.getDouble("base_points"),
                rs.getDouble("streak_bonus"),
                rs.getDouble("badge_bonus"),
                rs.getDouble("points"),
                rs.getDate("date").toLocalDate()
        );
    }
}
