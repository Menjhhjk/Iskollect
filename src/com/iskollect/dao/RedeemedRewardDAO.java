package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.RedeemedReward;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RedeemedRewardDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public boolean insert(RedeemedReward r) throws DatabaseException {
        String sql = "INSERT INTO redeemed_rewards "
                + "(student_id, reward_id, redeem_date, coupon_code, is_fulfilled, points_deducted) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getStudentId());
            ps.setInt(2, r.getRewardId());
            ps.setDate(3, Date.valueOf(r.getRedeemDate()));
            ps.setString(4, r.getCouponCode());
            ps.setBoolean(5, r.isFulfilled());
            ps.setDouble(6, r.getPointsDeducted());
            boolean inserted = ps.executeUpdate() > 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setRedeemId(keys.getInt(1));
                }
            }
            return inserted;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert redeemed reward.", e);
        }
    }

    public List<RedeemedReward> getByStudentId(int studentId) throws DatabaseException {
        String sql = "SELECT rr.*, r.name AS reward_name FROM redeemed_rewards rr "
                + "JOIN rewards r ON rr.reward_id = r.reward_id "
                + "WHERE rr.student_id = ? ORDER BY rr.redeem_date DESC, rr.redeem_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return collect(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch redemptions for student " + studentId, e);
        }
    }

    public void markFulfilled(int redeemId) throws DatabaseException {
        String sql = "UPDATE redeemed_rewards SET is_fulfilled = TRUE WHERE redeem_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, redeemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark redemption fulfilled.", e);
        }
    }

    public RedeemedReward findByCouponCode(String code) throws DatabaseException {
        String sql = "SELECT rr.*, r.name AS reward_name FROM redeemed_rewards rr "
                + "JOIN rewards r ON rr.reward_id = r.reward_id WHERE rr.coupon_code = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find redemption by coupon code.", e);
        }
    }

    private List<RedeemedReward> collect(PreparedStatement ps) throws SQLException {
        List<RedeemedReward> redemptions = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                redemptions.add(map(rs));
            }
        }
        return redemptions;
    }

    private RedeemedReward map(ResultSet rs) throws SQLException {
        RedeemedReward reward = new RedeemedReward(
                rs.getInt("redeem_id"),
                rs.getInt("student_id"),
                rs.getInt("reward_id"),
                rs.getDate("redeem_date").toLocalDate(),
                rs.getString("coupon_code"),
                rs.getBoolean("is_fulfilled"),
                rs.getDouble("points_deducted")
        );
        try {
            reward.setRewardName(rs.getString("reward_name"));
        } catch (SQLException ignored) {
            reward.setRewardName(null);
        }
        return reward;
    }
}
