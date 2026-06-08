package com.iskollect.service;

import com.iskollect.dao.UserDAO;
import com.iskollect.dao.BottleRecordDAO;
import com.iskollect.dao.PointsLedgerDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.model.SubmitResult;
import com.iskollect.model.BottleRecord;

import java.time.LocalDate;
import java.util.List;

public class BottleService {
    private final UserDAO userDAO;
    private final BottleRecordDAO bottleRecordDAO;
    private final PointsLedgerDAO pointsLedgerDAO;
    private final PointsService pointsService;
    private final StreakService streakService;
    private final BadgeService badgeService;

    public BottleService() {
        UserDAO sharedUserDAO = new UserDAO();
        BottleRecordDAO sharedBottleRecordDAO = new BottleRecordDAO();
        this.userDAO = sharedUserDAO;
        this.bottleRecordDAO = sharedBottleRecordDAO;
        this.pointsLedgerDAO = new PointsLedgerDAO();
        this.pointsService = new PointsService(sharedUserDAO, sharedBottleRecordDAO);
        this.streakService = new StreakService(sharedUserDAO);
        this.badgeService = new BadgeService(sharedUserDAO);
    }

    public SubmitResult submitBottles(int userId, int bottleCount) {
        if (bottleCount <= 0) {
            return SubmitResult.failure("Bottle count must be greater than zero.");
        }
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                return SubmitResult.failure("User not found.");
            }

            double basePoints = pointsService.calculateBasePoints(bottleCount);
            double streakBonus = streakService.evaluateStreak(user, bottleCount);
            BadgeService.BadgeResult badge = badgeService.evaluateBadge(user.getWeeklyBottles());
            double badgeBonus = badge.getBonusPoints();

            BottleRecord bottleRecord = new BottleRecord(0, userId, bottleCount, basePoints,
                    streakBonus, badgeBonus, 0, LocalDate.now());
            bottleRecordDAO.insert(bottleRecord);
            pointsLedgerDAO.insert(userId, basePoints, "bottle", bottleRecord.getRecordId());
            if (streakBonus > 0) {
                pointsLedgerDAO.insert(userId, streakBonus, "streak", bottleRecord.getRecordId());
            }
            boolean newBadgeAward = badgeService.awardWeeklyBadge(userId, badge);
            if (!newBadgeAward) {
                badgeBonus = 0;
            }
            if (badgeBonus > 0) {
                pointsLedgerDAO.insert(userId, badgeBonus, "badge", bottleRecord.getRecordId());
            }
            double totalPoints = basePoints + streakBonus + badgeBonus;
            bottleRecord.setPoints(totalPoints);
            bottleRecord.setBadgeBonus(badgeBonus);
            userDAO.updatePoints(userId, user.getTotalPoints() + totalPoints);
            userDAO.updateWeeklyStats(userId, user.getWeeklyBottles(),
                    user.getStreak(), user.getLastSubmitDate());

            return new SubmitResult(true, "Bottle submission recorded.", basePoints, streakBonus,
                    badgeBonus, totalPoints, badge.getTierName(), user.getStreak());
        } catch (DatabaseException e) {
            return SubmitResult.failure("Database error: " + e.getMessage());
        }
    }

    public List<BottleRecord> getBottleHistory(int userId) {
        try {
            return bottleRecordDAO.getByUserId(userId);
        } catch (DatabaseException e) {
            return List.of();
        }
    }
}
