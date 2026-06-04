package com.iskollect.model;

import java.time.LocalDate;

public class RedeemedReward {
    private int redeemId;
    private int studentId;
    private int rewardId;
    private LocalDate redeemDate;
    private String couponCode;
    private boolean fulfilled;
    private double pointsDeducted;
    private String rewardName;

    public RedeemedReward() {
    }

    public RedeemedReward(int redeemId, int studentId, int rewardId, LocalDate redeemDate,
                          String couponCode, boolean fulfilled, double pointsDeducted) {
        this.redeemId = redeemId;
        this.studentId = studentId;
        this.rewardId = rewardId;
        this.redeemDate = redeemDate;
        this.couponCode = couponCode;
        this.fulfilled = fulfilled;
        this.pointsDeducted = pointsDeducted;
    }

    public int getRedeemId() { return redeemId; }
    public void setRedeemId(int redeemId) { this.redeemId = redeemId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getRewardId() { return rewardId; }
    public void setRewardId(int rewardId) { this.rewardId = rewardId; }
    public LocalDate getRedeemDate() { return redeemDate; }
    public void setRedeemDate(LocalDate redeemDate) { this.redeemDate = redeemDate; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }
    public double getPointsDeducted() { return pointsDeducted; }
    public void setPointsDeducted(double pointsDeducted) { this.pointsDeducted = pointsDeducted; }
    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }

    @Override
    public String toString() {
        return "RedeemedReward{redeemId=" + redeemId + ", studentId=" + studentId
                + ", rewardId=" + rewardId + ", rewardName='" + rewardName + "', redeemDate="
                + redeemDate + ", couponCode='" + couponCode + "', fulfilled=" + fulfilled
                + ", pointsDeducted=" + pointsDeducted + "}";
    }
}