package com.iskollect.util;

import com.iskollect.model.Student;

public class SessionManager {
    private static Student loggedInStudent;

    public static void setSession(Student student) {
        if (student != null) {
            //generates session token
            String token = java.util.UUID.randomUUID().toString();
            student.setSessionToken(token);
        }

        loggedInStudent = student;
    }

    //getters and setters
    public static Student getSession() {
        return loggedInStudent;
    }

    public static Student getCurrentStudent() {
        return getSession();
    }

    public static void clearSession() {
        loggedInStudent = null;
    }
}
