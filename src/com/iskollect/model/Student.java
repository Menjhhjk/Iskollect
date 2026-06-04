package com.iskollect.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Student {
    private int user_id;
    private String name;
    private String username;
    private String webmail;
    private String password;
    private String course;
    private int year_level;
    private int age;
    private String profile_photo;
    private double total_points;
    private int raw_bottle_count;
    private int weekly_bottles;
    private int streak;
    private LocalDate last_submit_date;
    private String account_status;
    private int failed_login_attempts = 0;
    private String session_token;
    private LocalDateTime last_activity;

    //for login
    public Student(int id, String webmail, String password) {
        this.user_id = id;
        this.webmail = webmail;
        this.password = password;
    }

    //for signup
    public Student(String username, String webmail, String password) {
        this.username = username;
        this.webmail = webmail;
        this.password = password;
    }

    //complete Student info
    public Student(int user_id, String username, String webmail, String password,
                   int age, String profile_photo, double total_points,
                   int raw_bottle_count, String account_status, int failed_login_attempts,
                   String session_token, LocalDateTime last_activity) {
        this.user_id = user_id;
        this.username = username;
        this.webmail = webmail;
        this.password = password;
        this.age = age;
        this.profile_photo = profile_photo;
        this.total_points = total_points;
        this.raw_bottle_count = raw_bottle_count;
        this.account_status = account_status;
        this.failed_login_attempts = failed_login_attempts;
        this.session_token = session_token;
        this.last_activity = last_activity;
    }

    public Student() {}

    //getters and setters
    //user_id
    public int getUserID() {
        return user_id;
    }
    public void setUserID(int user_id) {
        this.user_id = user_id;
    }

    public int getStudentId() {
        return user_id;
    }

    public void setStudentId(int studentId) {
        this.user_id = studentId;
    }

    public String getName() {
        return name != null && !name.isBlank() ? name : username;
    }

    public void setName(String name) {
        this.name = name;
    }

    //username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    //webmail
    public String getWebmail() {
        return webmail;
    }
    public void setWebmail(String webmail) {
        this.webmail = webmail;
    }

    //password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return year_level;
    }

    public void setYearLevel(int yearLevel) {
        this.year_level = yearLevel;
    }

    //age
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    //photo
    public String getProfilePhoto() {
        return profile_photo;
    }
    public void setProfilePhoto(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    //total pts
    public double getTotalPoints() {
        return total_points;
    }
    public void setTotalPoints(double total_points) {
        this.total_points = total_points;
    }

    //raw bottle count
    public int getRawBottleCount() {
        return raw_bottle_count;
    }
    public void setRawBottleCount(int raw_bottle_count) {
        this.raw_bottle_count = raw_bottle_count;
    }

    public int getWeeklyBottles() {
        return weekly_bottles;
    }

    public void setWeeklyBottles(int weeklyBottles) {
        this.weekly_bottles = weeklyBottles;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public LocalDate getLastSubmitDate() {
        return last_submit_date;
    }

    public void setLastSubmitDate(LocalDate lastSubmitDate) {
        this.last_submit_date = lastSubmitDate;
    }

    //account status
    public String getAccountStatus() {
        return account_status;
    }
    public void setAccountStatus(String account_status) {
        this.account_status = account_status;
    }

    //failed login attempts
    public int getFailedLoginAttempts() {
        return failed_login_attempts;
    }
    public void setFailedLoginAttempts(int failed_login_attempts) {
        this.failed_login_attempts = failed_login_attempts;
    }

    //session token
    public String getSessionToken() { return session_token; }
    public void setSessionToken(String session_token) {
        this.session_token = session_token;
    }

    //last activity
    public LocalDateTime getLastActivity() {
        return last_activity;
    }
    public void setLastActivity(LocalDateTime last_activity) {
        this.last_activity = last_activity;
    }
}
