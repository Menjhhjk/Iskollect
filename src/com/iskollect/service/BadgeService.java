package com.iskollect.service;

import com.iskollect.dao.UserDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.util.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class BadgeService {
    private final UserDAO userDAO;

    public BadgeService() {
        this(new UserDAO());
    }

    public BadgeService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public BadgeResult evaluateBadge(int weeklyBottles) {
        if (weeklyBottles >= 31) {
            return new BadgeResult("Constellation", 10);
        }
        if (weeklyBottles >= 21) {
            return new BadgeResult("Gold", 5);
        }
        if (weeklyBottles >= 11) {
            return new BadgeResult("Emerald", 3);
        }
        if (weeklyBottles >= 6) {
            return new BadgeResult("Silver", 1);
        }
        return new BadgeResult("Bronze", 0);
    }

    public BadgeResult getCurrentBadge(int userId) {
        try {
            User user = userDAO.findById(userId);
            return user == null ? new BadgeResult("Bronze", 0) : evaluateBadge(user.getWeeklyBottles());
        } catch (DatabaseException e) {
            return new BadgeResult("Bronze", 0);
        }
    }

    public void resetWeeklyData(int userId) {
        try {
            userDAO.resetWeeklyStats(userId);
        } catch (DatabaseException e) {
            System.err.println("resetWeeklyData failed: " + e.getMessage());
        }
    }

    public boolean awardWeeklyBadge(int userId, BadgeResult badge) throws DatabaseException {
        String sql = "INSERT INTO user_badges (user_id, badge_id, date_awarded, week_start_date) "
                + "SELECT ?, badge_id, ?, DATE_TRUNC('week', ?::date)::date FROM badges WHERE badge_name = ? "
                + "ON CONFLICT DO NOTHING";
        LocalDate today = LocalDate.now();
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            ps.setDate(3, Date.valueOf(today));
            ps.setString(4, badge.getTierName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to award weekly badge.", e);
        }
    }

    public static final class BadgeResult {
        private final String tierName;
        private final double bonusPoints;

        public BadgeResult(String tierName, double bonusPoints) {
            this.tierName = tierName;
            this.bonusPoints = bonusPoints;
        }

        public String getTierName() { return tierName; }
        public double getBonusPoints() { return bonusPoints; }
    }
}
