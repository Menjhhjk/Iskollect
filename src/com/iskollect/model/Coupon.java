package com.iskollect.model;

public class Coupon {
    public enum CouponType {
        SUPPLIES, SNACK_V1, SNACK_V2, LUNCH
    }

    private int couponId;
    private String name;
    private double pointsRequired;
    private String description;
    private CouponType couponType;

    public Coupon() {
    }

    public Coupon(int couponId, String name, double pointsRequired, String description, CouponType couponType) {
        this.couponId = couponId;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.description = description;
        this.couponType = couponType;
    }

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }
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
        return "Coupon{couponId=" + couponId + ", name='" + name + "', pointsRequired="
                + pointsRequired + ", description='" + description + "', couponType=" + couponType + "}";
    }
}
