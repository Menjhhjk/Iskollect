package com.iskollect.scheduler;

import com.iskollect.dao.UserDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.service.BadgeService;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeeklyResetScheduler {
    private static final String LAST_RESET_KEY = "last_weekly_reset";

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final UserDAO userDAO = new UserDAO();
    private final BadgeService badgeService = new BadgeService();

    public void start() {
        LocalDate lastReset = getLastResetDate();
        long initialDelay = 0;
        if (lastReset != null && ChronoUnit.DAYS.between(lastReset, LocalDate.now()) <= 7) {
            initialDelay = 7 - ChronoUnit.DAYS.between(lastReset, LocalDate.now());
        }
        scheduler.scheduleAtFixedRate(this::resetWeeklyData, initialDelay, 7, TimeUnit.DAYS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    public void resetWeeklyData() {
        try {
            List<Integer> userIds = userDAO.getAllUserIds();
            for (Integer userId : userIds) {
                badgeService.resetWeeklyData(userId);
            }
            updateLastResetDate(LocalDate.now());
        } catch (DatabaseException e) {
            System.err.println("Weekly reset failed: " + e.getMessage());
        }
    }

    private LocalDate getLastResetDate() {
        String sql = "SELECT value FROM system_config WHERE key = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, LAST_RESET_KEY);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return LocalDate.parse(rs.getString("value"));
                }
            }
        } catch (SQLException | RuntimeException e) {
            System.err.println("Could not read last weekly reset date: " + e.getMessage());
        }
        return null;
    }

    private void updateLastResetDate(LocalDate date) {
        String sql = "INSERT INTO system_config (key, value) VALUES (?, ?) "
                + "ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, LAST_RESET_KEY);
            ps.setString(2, date.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not update last weekly reset date: " + e.getMessage());
        }
    }

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
}
