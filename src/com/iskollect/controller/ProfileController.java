package com.iskollect.controller;

import com.iskollect.dao.UserDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML private TextField nameField;
    @FXML private TextField courseField;
    @FXML private TextField yearLevelField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            return;
        }
        setField(nameField, user.getName());
        setField(courseField, user.getCourse());
        setField(yearLevelField, String.valueOf(user.getYearLevel()));
    }

    @FXML
    public void saveProfile() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            setStatus("Please log in first.");
            return;
        }
        try {
            int yearLevel = Integer.parseInt(yearLevelField.getText().trim());
            userDAO.updateProfile(user.getUserId(), nameField.getText().trim(),
                    courseField.getText().trim(), yearLevel);
            user.setName(nameField.getText().trim());
            user.setCourse(courseField.getText().trim());
            user.setYearLevel(yearLevel);
            setStatus("Profile updated.");
        } catch (NumberFormatException e) {
            setStatus("Year level must be a whole number.");
        } catch (DatabaseException e) {
            setStatus("Could not update profile: " + e.getMessage());
        }
    }

    private void setField(TextField field, String value) {
        if (field != null) {
            field.setText(value);
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
