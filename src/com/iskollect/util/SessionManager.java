package com.iskollect.util;

import com.iskollect.model.User;

public class SessionManager {
    private static User loggedInUser;

    public static void setSession(User user) {
        if (user != null) {
            //generates session token
            String token = java.util.UUID.randomUUID().toString();
            user.setSessionToken(token);
        }

        loggedInUser = user;
    }

    //getters and setters
    public static User getSession() {
        return loggedInUser;
    }

    public static User getCurrentUser() {
        return getSession();
    }

    public static void clearSession() {
        loggedInUser = null;
    }
}
