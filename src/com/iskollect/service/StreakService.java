package com.iskollect.service;

import com.iskollect.dao.UserDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class StreakService {
    private final UserDAO userDAO;

    public StreakService() {
        this(new UserDAO());
    }

    public StreakService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public double evaluateStreak(User user, int bottles) throws DatabaseException {
        LocalDate today = LocalDate.now();
        int streak = currentConsecutiveDaysBeforeToday(user.getUserId(), today) + 1;

        double bonus = 0;
        if (streak == 5) {
            bonus = bottles * 1.0;
        } else if (streak == 3) {
            bonus = bottles * 0.50;
        }
        if (bonus > 0 && hasStreakBonusLogged(user.getUserId(), streak, today)) {
            bonus = 0;
        }

        user.setStreak(streak);
        user.setWeeklyBottles(user.getWeeklyBottles() + bottles);
        user.setLastSubmitDate(today);
        userDAO.updateWeeklyStats(user.getUserId(), user.getWeeklyBottles(), streak, today);
        if (bonus > 0) {
            logStreak(user.getUserId(), streak, bonus);
        }
        return bonus;
    }

    public int getStreakCount(int userId) {
        try {
            User user = userDAO.findById(userId);
            return user == null ? 0 : user.getStreak();
        } catch (DatabaseException e) {
            return 0;
        }
    }

    private void logStreak(int userId, int streakDays, double bonusPoints) throws DatabaseException {
        String sql = "INSERT INTO streaks (user_id, streak_days, bonus_points) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, streakDays);
            ps.setDouble(3, bonusPoints);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to log streak bonus.", e);
        }
    }

    private int currentConsecutiveDaysBeforeToday(int userId, LocalDate today) throws DatabaseException {
        String sql = "SELECT DISTINCT collection_date FROM bottle_records "
                + "WHERE user_id = ? AND collection_date < ? ORDER BY collection_date DESC";
        LocalDate expected = today.minusDays(1);
        int count = 0;
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("collection_date").toLocalDate();
                    if (date.equals(expected)) {
                        count++;
                        expected = expected.minusDays(1);
                    } else if (date.isBefore(expected)) {
                        break;
                    }
                }
            }
            return count;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate current streak.", e);
        }
    }

    private boolean hasStreakBonusLogged(int userId, int streakDays, LocalDate today) throws DatabaseException {
        String sql = "SELECT 1 FROM streaks WHERE user_id = ? AND streak_days = ? "
                + "AND date_logged::date = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, streakDays);
            ps.setDate(3, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check streak bonus history.", e);
        }
    }
}
