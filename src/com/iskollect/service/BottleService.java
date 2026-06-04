package com.iskollect.service;

import com.iskollect.dao.StudentDAO;
import com.iskollect.dao.TransactionDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;
import com.iskollect.model.SubmitResult;
import com.iskollect.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public class BottleService {
    private final StudentDAO studentDAO;
    private final TransactionDAO transactionDAO;
    private final PointsService pointsService;
    private final StreakService streakService;
    private final BadgeService badgeService;

    public BottleService() {
        StudentDAO sharedStudentDAO = new StudentDAO();
        TransactionDAO sharedTransactionDAO = new TransactionDAO();
        this.studentDAO = sharedStudentDAO;
        this.transactionDAO = sharedTransactionDAO;
        this.pointsService = new PointsService(sharedStudentDAO, sharedTransactionDAO);
        this.streakService = new StreakService(sharedStudentDAO);
        this.badgeService = new BadgeService(sharedStudentDAO);
    }

    public SubmitResult submitBottles(int studentId, int bottleCount) {
        if (bottleCount <= 0) {
            return SubmitResult.failure("Bottle count must be greater than zero.");
        }
        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                return SubmitResult.failure("Student not found.");
            }

            double basePoints = pointsService.calculateBasePoints(bottleCount);
            double streakBonus = streakService.evaluateStreak(student, bottleCount);
            BadgeService.BadgeResult badge = badgeService.evaluateBadge(student.getWeeklyBottles());
            double badgeBonus = badge.getBonusPoints();
            double totalPoints = basePoints + streakBonus + badgeBonus;

            Transaction transaction = new Transaction(0, studentId, bottleCount, basePoints,
                    streakBonus, badgeBonus, totalPoints, LocalDate.now());
            transactionDAO.insert(transaction);
            studentDAO.updatePoints(studentId, student.getTotalPoints() + totalPoints);
            studentDAO.updateWeeklyStats(studentId, student.getWeeklyBottles(),
                    student.getStreak(), student.getLastSubmitDate());

            return new SubmitResult(true, "Bottle submission recorded.", basePoints, streakBonus,
                    badgeBonus, totalPoints, badge.getTierName(), student.getStreak());
        } catch (DatabaseException e) {
            return SubmitResult.failure("Database error: " + e.getMessage());
        }
    }

    public List<Transaction> getBottleHistory(int studentId) {
        try {
            return transactionDAO.getByStudentId(studentId);
        } catch (DatabaseException e) {
            return List.of();
        }
    }
}
