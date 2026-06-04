package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;
import com.iskollect.util.DBConnection;

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

    public boolean insert(Student s) throws DatabaseException {
        String sql = "INSERT INTO students (name, course, year_level, total_points, email, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCourse());
            ps.setInt(3, s.getYearLevel());
            ps.setDouble(4, s.getTotalPoints());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPasswordHash());
            boolean inserted = ps.executeUpdate() > 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setStudentId(keys.getInt(1));
                }
            }
            return inserted;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert student.", e);
        }
    }

    public Student findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by email.", e);
        }
    }

    public Student findById(int studentId) throws DatabaseException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by ID " + studentId, e);
        }
    }

    public void updatePoints(int studentId, double points) throws DatabaseException {
        String sql = "UPDATE students SET total_points = ? WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, points);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update points for student " + studentId, e);
        }
    }

    public boolean deductPointsAtomic(int studentId, double amount) throws DatabaseException {
        String sql = "UPDATE students SET total_points = total_points - ? "
                + "WHERE student_id = ? AND total_points >= ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, studentId);
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deduct points for student " + studentId, e);
        }
    }

    public void updateWeeklyStats(int studentId, int weeklyBottles, int streak, java.time.LocalDate lastDate)
            throws DatabaseException {
        String sql = "UPDATE students SET weekly_bottles = ?, streak = ?, last_submit_date = ? WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, weeklyBottles);
            ps.setInt(2, streak);
            if (lastDate == null) {
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setDate(3, Date.valueOf(lastDate));
            }
            ps.setInt(4, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update weekly stats for student " + studentId, e);
        }
    }

    public void updateProfile(int studentId, String name, String course, int yearLevel) throws DatabaseException {
        String sql = "UPDATE students SET name = ?, course = ?, year_level = ? WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, course);
            ps.setInt(3, yearLevel);
            ps.setInt(4, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update profile for student " + studentId, e);
        }
    }

    public void resetWeeklyStats(int studentId) throws DatabaseException {
        String sql = "UPDATE students SET weekly_bottles = 0, streak = 0, last_submit_date = NULL WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to reset weekly stats for student " + studentId, e);
        }
    }

    public void incrementFailedAttempts(String email) throws DatabaseException {
        String sql = "UPDATE students SET failed_attempts = failed_attempts + 1 WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to increment failed attempts.", e);
        }
    }

    public void lockAccount(String email) throws DatabaseException {
        String sql = "UPDATE students SET is_locked = TRUE WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to lock account.", e);
        }
    }

    public List<Integer> getAllStudentIds() throws DatabaseException {
        String sql = "SELECT student_id FROM students ORDER BY student_id";
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("student_id"));
            }
            return ids;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all student IDs.", e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Date lastDate = rs.getDate("last_submit_date");
        Student student = new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("course"),
                rs.getInt("year_level"),
                rs.getDouble("total_points"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getInt("streak"),
                rs.getInt("weekly_bottles"),
                lastDate == null ? null : lastDate.toLocalDate()
        );
        student.setFailedAttempts(readInt(rs, "failed_attempts"));
        student.setLocked(readBoolean(rs, "is_locked"));
        return student;
    }

    private int readInt(ResultSet rs, String column) {
        try {
            return rs.getInt(column);
        } catch (SQLException e) {
            return 0;
        }
    }

    private boolean readBoolean(ResultSet rs, String column) {
        try {
            return rs.getBoolean(column);
        } catch (SQLException e) {
            return false;
        }
    }
}
