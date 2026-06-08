package com.iskollect.service;

import com.iskollect.exception.DatabaseException;
import com.iskollect.exception.InvalidInputException;
import com.iskollect.model.User;
import com.iskollect.dao.UserDAO;
import com.iskollect.util.SessionManager;
import com.iskollect.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public boolean register(User user)
            throws DatabaseException, InvalidInputException {

        //validation for inputs
        if (user.getUsername().trim().isEmpty() || user.getWebmail().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("All fields are required. Please fill out the form entirely.");
        }

        if (!user.getWebmail().trim().endsWith("@iskolarngbayan.pup.edu.ph")) {
            throw new InvalidInputException("Registration restricted to @iskolarngbayan.pup.edu.ph accounts.");
        }

        if (user.getPassword().trim().length() < 8) {
            throw new InvalidInputException("Password must be at least 8 characters long.");
        }

        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$";

        if (!user.getPassword().trim().matches(regex)) {
            throw new InvalidInputException("Password must include one number and one special character.");
        }

        //pass to userDAO to register the user to the DB
        return userDAO.registerUser(user);
    }

    public boolean login(String webmail, String plainPassword) throws DatabaseException, InvalidInputException {

        //validation for inputs
        if (webmail.isEmpty() || plainPassword.isEmpty()) {
            throw new InvalidInputException("All fields are required. Please fill out the form entirely.");
        }

        if (!webmail.endsWith("@iskolarngbayan.pup.edu.ph")) {
            throw new InvalidInputException("You must use an @iskolarngbayan.pup.edu.ph webmail.");
        }

        //checks if the inputted webmail exists
        User user = userDAO.searchUser(webmail);

        if (user == null) {
            System.out.println("[AuthService] Authentication failed: Webmail not found.");
            return false;
        }

        //compares the inputted password to the hashed password in the DB to check if the inputted password is correct
        boolean isPasswordCorrect = PasswordUtil.checkPassword(plainPassword, user.getPassword());

        if (isPasswordCorrect) {
            //begins the session of the user
            SessionManager.setSession(user);
            userDAO.updateSessionToken(user.getUserId(), user.getSessionToken());
            return true;
        }

        System.out.println("[AuthService] Authentication failed: Incorrect password.");
        return false;
    }

    public void logout() throws DatabaseException {
        User currentUser = SessionManager.getSession();
        //clears the session and current token
        if (currentUser != null) {
            userDAO.updateSessionToken(currentUser.getUserId(), null);
        }
        SessionManager.clearSession();
    }
}