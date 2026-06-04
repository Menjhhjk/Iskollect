package com.iskollect.service;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;
import com.iskollect.dao.StudentDAO;
import com.iskollect.util.SessionManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class SecurityCheck {

    private static final int MAX_INACTIVITY_MINUTES = 30;

    private final StudentDAO studentDAO = new StudentDAO();

    public boolean isSessionValid() {
        Student currentStudent = SessionManager.getSession();

        if (currentStudent == null) {
            System.out.println("[SecurityService] Blocked: No active local session found.");
            return false;
        }

        int userId = currentStudent.getUserID();

        if (isSessionExpired(currentStudent.getLastActivity())) {
            System.out.println("[SecurityService] Blocked: Inactivity idle timeout detected.");
            handleForcedLogout(userId);
            return false;
        }

        String localToken = currentStudent.getSessionToken();
        String dbToken = studentDAO.getSessionTokenDB(userId);

        if (dbToken == null || !dbToken.equals(localToken)) {
            System.out.println("[SecurityService] Blocked: Token symmetry mismatch or remote session revoked.");
            handleForcedLogout(userId);
            return false;
        }

        try {
            currentStudent.setLastActivity(LocalDateTime.now());
            studentDAO.updateLastActivity(userId);
            return true;
        } catch (DatabaseException e) {
            System.err.println("[SecurityCheck] Error updating activity: " + e.getMessage());
            return false;
        }
    }

    private boolean isSessionExpired(LocalDateTime lastActivity) {
        if (lastActivity == null) {
            return false;
        }
        long minutesIdle = Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
        return minutesIdle >= MAX_INACTIVITY_MINUTES;
    }

    private void handleForcedLogout(int userId) {
        try {
            studentDAO.updateSessionToken(userId, null);
            System.out.println("[SecurityCheck] Remote session token revoked successfully.");
        } catch (DatabaseException e) {
            System.err.println("[SecurityCheck] Warning: Failed to revoke remote token. " + e.getMessage());
        } finally {
            SessionManager.clearSession();
            System.out.println("[SecurityCheck] Local session memory cleared safely.");
        }
    }
}
