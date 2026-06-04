package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Reward;
import com.iskollect.model.Reward.CouponType;
import com.iskollect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RewardDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public List<Reward> getAll() throws DatabaseException {
        String sql = "SELECT * FROM rewards ORDER BY points_required ASC";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Reward> rewards = new ArrayList<>();
            while (rs.next()) {
                rewards.add(map(rs));
            }
            return rewards;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch rewards.", e);
        }
    }

    public Reward findById(int rewardId) throws DatabaseException {
        String sql = "SELECT * FROM rewards WHERE reward_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, rewardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find reward " + rewardId, e);
        }
    }

    public boolean insert(Reward r) throws DatabaseException {
        String sql = "INSERT INTO rewards (name, points_required, description, coupon_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getName());
            ps.setDouble(2, r.getPointsRequired());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getCouponType().name());
            boolean inserted = ps.executeUpdate() > 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setRewardId(keys.getInt(1));
                }
            }
            return inserted;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert reward.", e);
        }
    }

    private Reward map(ResultSet rs) throws SQLException {
        return new Reward(
                rs.getInt("reward_id"),
                rs.getString("name"),
                rs.getDouble("points_required"),
                rs.getString("description"),
                CouponType.valueOf(rs.getString("coupon_type"))
        );
    }
}
