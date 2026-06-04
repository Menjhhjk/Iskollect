package com.iskollect.model;

import java.time.LocalDate;

public class Student {
    private int studentId;
    private String name;
    private String course;
    private int yearLevel;
    private double totalPoints;
    private String email;
    private String passwordHash;
    private int streak;
    private int weeklyBottles;
    private LocalDate lastSubmitDate;
    private int failedAttempts;
    private boolean locked;

    public Student() {
    }

    public Student(int studentId, String name, String course, int yearLevel, double totalPoints,
                   String email, String passwordHash, int streak, int weeklyBottles,
                   LocalDate lastSubmitDate) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.yearLevel = yearLevel;
        this.totalPoints = totalPoints;
        this.email = email;
        this.passwordHash = passwordHash;
        this.streak = streak;
        this.weeklyBottles = weeklyBottles;
        this.lastSubmitDate = lastSubmitDate;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public int getWeeklyBottles() { return weeklyBottles; }
    public void setWeeklyBottles(int weeklyBottles) { this.weeklyBottles = weeklyBottles; }
    public LocalDate getLastSubmitDate() { return lastSubmitDate; }
    public void setLastSubmitDate(LocalDate lastSubmitDate) { this.lastSubmitDate = lastSubmitDate; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    @Override
    public String toString() {
        return "Student{studentId=" + studentId + ", name='" + name + "', course='" + course
                + "', yearLevel=" + yearLevel + ", totalPoints=" + totalPoints + ", email='"
                + email + "', streak=" + streak + ", weeklyBottles=" + weeklyBottles
                + ", lastSubmitDate=" + lastSubmitDate + ", locked=" + locked + "}";
    }
}
