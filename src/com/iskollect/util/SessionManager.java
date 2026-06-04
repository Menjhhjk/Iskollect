package com.iskollect.util;

import com.iskollect.model.Student;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private static Student currentStudent;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static void setCurrentStudent(Student s) {
        currentStudent = s;
    }

    public static Student getCurrentStudent() {
        // DEPENDS ON: registration module
        return currentStudent;
    }

    public static void clearSession() {
        currentStudent = null;
    }

    public static boolean isLoggedIn() {
        return currentStudent != null;
    }
}
