package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;
import com.iskollect.util.DBConnection;
import com.iskollect.util.PasswordUtil;
import java.sql.*;

public class StudentDAO {

    //register user
    public boolean registerStudent(Student student) throws DatabaseException {
        String register_query = "INSERT INTO users (username, webmail, password) VALUES (?, ?, ?);";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(register_query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getUsername());
            ps.setString(2, student.getWebmail());
            String hashedPassword = PasswordUtil.hashPassword(student.getPassword());
            ps.setString(3,hashedPassword);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setUserID(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Student registered successfully with ID: " + student.getUserID());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute registration insertion on Supabase server.", e);
        }
    }

    //search for user's webmail to match inputted webmail and password
    public Student searchStudent(String webmail) throws DatabaseException {
        String query = "SELECT * FROM users WHERE LOWER(webmail) = LOWER(?)";

        Student fullStudent = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, webmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    fullStudent = new Student();

                    fullStudent.setUserID(rs.getInt("user_id"));
                    fullStudent.setUsername(rs.getString("username"));
                    fullStudent.setWebmail(rs.getString("webmail"));
                    fullStudent.setPassword(rs.getString("password"));
                    fullStudent.setAge(rs.getInt("age"));
                    fullStudent.setProfilePhoto(rs.getString("profile_photo"));
                    fullStudent.setTotalPoints(rs.getInt("total_points"));
                    fullStudent.setRawBottleCount(rs.getInt("raw_bottle_count"));
                    fullStudent.setAccountStatus(rs.getString("account_status"));
                    fullStudent.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
                    fullStudent.setSessionToken(rs.getString("session_token"));

                    java.sql.Timestamp activityTimestamp = rs.getTimestamp("last_activity");
                    if (activityTimestamp != null) {
                        fullStudent.setLastActivity(activityTimestamp.toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search student credential.", e);
        }

        return fullStudent;
    }

    //get the user's token
    public String getSessionTokenDB(int studentId) {
        String query = "SELECT session_token FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("session_token");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying token against database.", e);
        }
        return null;
    }

    //update the user's token
    public void updateSessionToken(int studentId, String token) throws DatabaseException {
        String query = "UPDATE users SET session_token = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, token);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update token in database.", e);
        }
    }
}