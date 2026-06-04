package com.iskollect.service;

import com.iskollect.exception.DatabaseException;
import com.iskollect.exception.InvalidInputException;
import com.iskollect.model.Student;
import com.iskollect.dao.StudentDAO;
import com.iskollect.util.SessionManager;
import com.iskollect.util.PasswordUtil;

public class AuthService {

    private final StudentDAO studentDAO;

    public AuthService() {
        this.studentDAO = new StudentDAO();
    }

    public boolean register(Student student)
            throws DatabaseException, InvalidInputException {

        //validation for inputs
        if (student.getUsername().trim().isEmpty() || student.getWebmail().trim().isEmpty() || student.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("All fields are required. Please fill out the form entirely.");
        }

        if (!student.getWebmail().trim().endsWith("@iskolarngbayan.pup.edu.ph")) {
            throw new InvalidInputException("Registration restricted to @iskolarngbayan.pup.edu.ph accounts.");
        }

        if (student.getPassword().trim().length() < 8) {
            throw new InvalidInputException("Password must be at least 8 characters long.");
        }

        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$";

        if (!student.getPassword().trim().matches(regex)) {
            throw new InvalidInputException("Password must include one number and one special character.");
        }

        //pass to studentDAO to register the user to the DB
        return studentDAO.registerStudent(student);
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
        Student student = studentDAO.searchStudent(webmail);

        if (student == null) {
            System.out.println("[AuthService] Authentication failed: Webmail not found.");
            return false;
        }

        //compares the inputted password to the hashed password in the DB to check if the inputted password is correct
        boolean isPasswordCorrect = PasswordUtil.checkPassword(plainPassword, student.getPassword());

        if (isPasswordCorrect) {
            //begins the session of the user
            SessionManager.setSession(student);
            studentDAO.updateSessionToken(student.getUserID(), student.getSessionToken());
            return true;
        }

        System.out.println("[AuthService] Authentication failed: Incorrect password.");
        return false;
    }

    public void logout() throws DatabaseException {
        Student currentStudent = SessionManager.getSession();
        //clears the session and current token
        if (currentStudent != null) {
            studentDAO.updateSessionToken(currentStudent.getUserID(), null);
        }
        SessionManager.clearSession();
    }
}