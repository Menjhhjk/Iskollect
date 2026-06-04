package com.iskollect.model;

import java.time.LocalDate;

public class Transaction {
    private int transId;
    private int studentId;
    private int bottles;
    private double basePoints;
    private double streakBonus;
    private double badgeBonus;
    private double points;
    private LocalDate date;

    public Transaction() {
    }

    public Transaction(int transId, int studentId, int bottles, double basePoints,
                       double streakBonus, double badgeBonus, double points, LocalDate date) {
        this.transId = transId;
        this.studentId = studentId;
        this.bottles = bottles;
        this.basePoints = basePoints;
        this.streakBonus = streakBonus;
        this.badgeBonus = badgeBonus;
        this.points = points;
        this.date = date;
    }

    public int getTransId() { return transId; }
    public void setTransId(int transId) { this.transId = transId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getBottles() { return bottles; }
    public void setBottles(int bottles) { this.bottles = bottles; }
    public double getBasePoints() { return basePoints; }
    public void setBasePoints(double basePoints) { this.basePoints = basePoints; }
    public double getStreakBonus() { return streakBonus; }
    public void setStreakBonus(double streakBonus) { this.streakBonus = streakBonus; }
    public double getBadgeBonus() { return badgeBonus; }
    public void setBadgeBonus(double badgeBonus) { this.badgeBonus = badgeBonus; }
    public double getPoints() { return points; }
    public void setPoints(double points) { this.points = points; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return "Transaction{transId=" + transId + ", studentId=" + studentId + ", bottles="
                + bottles + ", basePoints=" + basePoints + ", streakBonus=" + streakBonus
                + ", badgeBonus=" + badgeBonus + ", points=" + points + ", date=" + date + "}";
    }
}
