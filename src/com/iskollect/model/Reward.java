package com.iskollect.model;

public class Reward {
    public enum CouponType {
        SUPPLIES, SNACK_V1, SNACK_V2, LUNCH
    }

    private int rewardId;
    private String name;
    private double pointsRequired;
    private String description;
    private CouponType couponType;

    public Reward() {
    }

    public Reward(int rewardId, String name, double pointsRequired, String description, CouponType couponType) {
        this.rewardId = rewardId;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.description = description;
        this.couponType = couponType;
    }

    public int getRewardId() { return rewardId; }
    public void setRewardId(int rewardId) { this.rewardId = rewardId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(double pointsRequired) { this.pointsRequired = pointsRequired; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CouponType getCouponType() { return couponType; }
    public void setCouponType(CouponType couponType) { this.couponType = couponType; }

    @Override
    public String toString() {
        return "Reward{rewardId=" + rewardId + ", name='" + name + "', pointsRequired="
                + pointsRequired + ", description='" + description + "', couponType=" + couponType + "}";
    }
}
