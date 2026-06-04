package com.iskollect.service;

import com.iskollect.dao.RedeemedRewardDAO;
import com.iskollect.dao.RewardDAO;
import com.iskollect.dao.StudentDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.exception.InsufficientPointsException;
import com.iskollect.model.RedeemResult;
import com.iskollect.model.RedeemedReward;
import com.iskollect.model.Reward;
import com.iskollect.model.Student;
import com.iskollect.util.CouponGenerator;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RewardService {
    private final RewardDAO rewardDAO = new RewardDAO();
    private final RedeemedRewardDAO redeemedRewardDAO = new RedeemedRewardDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Reward> getAllRewards() {
        try {
            return rewardDAO.getAll();
        } catch (DatabaseException e) {
            return List.of();
        }
    }

    public RedeemResult redeem(int studentId, int rewardId) {
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            Student student = studentDAO.findById(studentId);
            Reward reward = rewardDAO.findById(rewardId);
            if (student == null || reward == null) {
                throw new DatabaseException("Student or reward not found.");
            }
            if (student.getTotalPoints() < reward.getPointsRequired()) {
                throw new InsufficientPointsException("Insufficient points for selected reward.");
            }

            String couponCode = CouponGenerator.generate();
            RedeemedReward redemption = new RedeemedReward(0, studentId, rewardId, LocalDate.now(),
                    couponCode, false, reward.getPointsRequired());
            redeemedRewardDAO.insert(redemption);
            double remainingPoints = student.getTotalPoints() - reward.getPointsRequired();
            studentDAO.updatePoints(studentId, remainingPoints);
            conn.commit();

            return new RedeemResult(true, "Reward redeemed.", couponCode, reward.getName(),
                    reward.getPointsRequired(), remainingPoints);
        } catch (InsufficientPointsException | DatabaseException | SQLException e) {
            rollback(conn);
            return RedeemResult.failure(e.getMessage());
        } finally {
            restoreAutoCommit(conn);
        }
    }

    public List<RedeemedReward> getRedemptionHistory(int studentId) {
        try {
            return redeemedRewardDAO.getByStudentId(studentId);
        } catch (DatabaseException e) {
            return List.of();
        }
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommit(Connection conn) {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }
}
