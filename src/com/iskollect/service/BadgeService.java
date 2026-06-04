package com.iskollect.service;

import com.iskollect.dao.StudentDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;

public class BadgeService {
    private final StudentDAO studentDAO;

    public BadgeService() {
        this(new StudentDAO());
    }

    public BadgeService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
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

    public BadgeResult getCurrentBadge(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            return student == null ? new BadgeResult("Bronze", 0) : evaluateBadge(student.getWeeklyBottles());
        } catch (DatabaseException e) {
            return new BadgeResult("Bronze", 0);
        }
    }

    public void resetWeeklyData(int studentId) {
        try {
            studentDAO.resetWeeklyStats(studentId);
        } catch (DatabaseException e) {
            System.err.println("resetWeeklyData failed: " + e.getMessage());
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
