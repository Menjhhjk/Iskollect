package com.iskollect.service;

import com.iskollect.exception.DatabaseException;
import com.iskollect.exception.DuplicateLogException;
import com.iskollect.model.Student;
import com.iskollect.dao.StudentDAO;
import com.iskollect.util.SessionManager;

public class SecurityCheck {

    private final StudentDAO studentDAO = new StudentDAO();
    private final InOutService sessionService = new InOutService();

    //main security check
    public boolean isSessionValid() {
        Student currentStudent = SessionManager.getSession();

        //check if there is a user in the memory
        if (currentStudent == null) {
            System.out.println("[SecurityService] Blocked: No active local session found.");
            return false;
        }

        int userId = currentStudent.getUserID();

        //check timeout expiry
        if (sessionService.isSessionExpired(userId)) {
            System.out.println("[SecurityService] Blocked: Inactivity idle timeout detected.");
            handleForcedLogout(userId);
            return false;
        }

        //check token
        String localToken = currentStudent.getSessionToken();
        String dbToken = studentDAO.getSessionTokenDB(userId);

        if (dbToken == null || !dbToken.equals(localToken)) {
            System.out.println("[SecurityService] Blocked: Token symmetry mismatch or remote session revoked.");
            handleForcedLogout(userId);
            return false;
        }
        try {
            sessionService.trackActivity(userId);
            return true;
        } catch (DuplicateLogException | DatabaseException e) {
            System.err.println("[SecurityCheck] Error tracking activity: " + e.getMessage());
            return false;
        }
    }

    //delete everything
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