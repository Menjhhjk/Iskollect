package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;
import com.iskollect.util.DBConnection;
import com.iskollect.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public boolean registerStudent(Student student) throws DatabaseException {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getWebmail());
            ps.setString(3, PasswordUtil.hashPassword(student.getPassword()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setUserID(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register student.", e);
        }
    }

    public Student searchStudent(String webmail) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, webmail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search student credential.", e);
        }
    }

    public Student findById(int studentId) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student " + studentId + ".", e);
        }
    }

    public List<Integer> getAllStudentIds() throws DatabaseException {
        String sql = "SELECT user_id FROM users ORDER BY user_id";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
            return ids;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student IDs.", e);
        }
    }

    public void updateSessionToken(int studentId, String token) throws DatabaseException {
        String sql = "UPDATE users SET session_token = ?, last_activity = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update token in database.", e);
        }
    }

    public void updateLastActivity(int studentId) throws DatabaseException {
        String sql = "UPDATE users SET last_activity = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update last activity.", e);
        }
    }

    public String getSessionTokenDB(int studentId) {
        String sql = "SELECT session_token FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("session_token") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying token against database.", e);
        }
    }

    public void updateProfile(int studentId, String name, String course, int yearLevel) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, course = ?, year_level = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, course);
            ps.setInt(3, yearLevel);
            ps.setInt(4, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update profile.", e);
        }
    }

    public void updatePoints(int studentId, double totalPoints) throws DatabaseException {
        String sql = "UPDATE users SET total_points = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, totalPoints);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update points.", e);
        }
    }

    public boolean deductPointsAtomic(int studentId, double amount) throws DatabaseException {
        String sql = "UPDATE users SET total_points = total_points - ? WHERE user_id = ? AND total_points >= ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, studentId);
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deduct points.", e);
        }
    }

    public void updateWeeklyStats(int studentId, int weeklyBottles, int streak, java.time.LocalDate lastSubmitDate)
            throws DatabaseException {
        String sql = "UPDATE users SET weekly_bottles = ?, streak = ?, last_submit_date = ?, "
                + "raw_bottle_count = raw_bottle_count + GREATEST(? - weekly_bottles, 0) WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, weeklyBottles);
            ps.setInt(2, streak);
            ps.setDate(3, lastSubmitDate == null ? null : Date.valueOf(lastSubmitDate));
            ps.setInt(4, weeklyBottles);
            ps.setInt(5, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update weekly stats.", e);
        }
    }

    public void resetWeeklyStats(int studentId) throws DatabaseException {
        String sql = "UPDATE users SET weekly_bottles = 0, streak = 0, last_submit_date = NULL WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to reset weekly stats.", e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setUserID(rs.getInt("user_id"));
        student.setUsername(rs.getString("username"));
        student.setName(readString(rs, "name", student.getUsername()));
        student.setWebmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setCourse(readString(rs, "course", ""));
        student.setYearLevel(readInt(rs, "year_level", 0));
        student.setAge(readInt(rs, "age", 0));
        student.setProfilePhoto(readString(rs, "profile_photo", null));
        student.setTotalPoints(readDouble(rs, "total_points", 0));
        student.setRawBottleCount(readInt(rs, "raw_bottle_count", 0));
        student.setWeeklyBottles(readInt(rs, "weekly_bottles", 0));
        student.setStreak(readInt(rs, "streak", 0));
        Date lastSubmitDate = readDate(rs, "last_submit_date");
        student.setLastSubmitDate(lastSubmitDate == null ? null : lastSubmitDate.toLocalDate());
        student.setAccountStatus(readString(rs, "account_status", "active"));
        student.setFailedLoginAttempts(readInt(rs, "failed_login_attempts", 0));
        student.setSessionToken(readString(rs, "session_token", null));
        java.sql.Timestamp activity = readTimestamp(rs, "last_activity");
        if (activity != null) {
            student.setLastActivity(activity.toLocalDateTime());
        }
        return student;
    }

    private boolean hasColumn(ResultSet rs, String column) throws SQLException {
        ResultSet meta = rs;
        for (int i = 1; i <= meta.getMetaData().getColumnCount(); i++) {
            if (column.equalsIgnoreCase(meta.getMetaData().getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    private String readString(ResultSet rs, String column, String fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getString(column) : fallback;
    }

    private int readInt(ResultSet rs, String column, int fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getInt(column) : fallback;
    }

    private double readDouble(ResultSet rs, String column, double fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getDouble(column) : fallback;
    }

    private Date readDate(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? rs.getDate(column) : null;
    }

    private java.sql.Timestamp readTimestamp(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? rs.getTimestamp(column) : null;
    }
}
