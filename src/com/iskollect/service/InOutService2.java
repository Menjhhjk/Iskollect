package com.iskollect.service;

import com.iskollect.dao.InOutLogDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.exception.DuplicateLogException;
import com.iskollect.model.InOutLog;
import java.time.Duration;
import java.time.LocalDateTime;

public class InOutService2 {

    private final InOutLogDAO logDAO;
    private static final int MAX_INACTIVITY_MINUTES = 1;

    public InOutService2() {
        this.logDAO = new InOutLogDAO();
    }

    public void trackActivity(int studentId) throws DuplicateLogException, DatabaseException {

        InOutLog lastLog = logDAO.getLastActivity(studentId);

        if (lastLog != null) {
            long secondsSinceLast = Duration.between(lastLog.getTimestamp(), LocalDateTime.now()).toSeconds();

            if (secondsSinceLast < 30) {
                throw new DuplicateLogException(lastLog.getStudentId(),
                        "Activity tracking rejected: Duplicate log attempt within the 30s threshold.");
            }
        }

        InOutLog newLog = new InOutLog(studentId, LocalDateTime.now());
        logDAO.insertLastActivity(newLog);
    }

    public boolean isSessionExpired(int studentId) {
        try {
            InOutLog lastLog = logDAO.getLastActivity(studentId);
            if (lastLog == null) return false;

            long minutesIdle = Duration.between(lastLog.getTimestamp(), LocalDateTime.now()).toMinutes();
            return minutesIdle >= MAX_INACTIVITY_MINUTES;

        } catch (DatabaseException e) {
            System.err.println("Error checking session timeout: " + e.getMessage());
            return false;
        }
    }

    public boolean isTimeExpired(LocalDateTime lastInteractionTime) {
        if (lastInteractionTime == null) return false;

        long minutesIdle = Duration.between(lastInteractionTime, LocalDateTime.now()).toMinutes();

        return minutesIdle >= MAX_INACTIVITY_MINUTES;
    }
}