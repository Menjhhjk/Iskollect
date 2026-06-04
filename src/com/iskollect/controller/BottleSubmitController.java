package com.iskollect.controller;

import com.iskollect.model.Student;
import com.iskollect.model.SubmitResult;
import com.iskollect.service.BottleService;
import com.iskollect.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BottleSubmitController {
    @FXML private TextField bottleCountField;
    @FXML private Label statusLabel;

    private final BottleService bottleService = new BottleService();

    @FXML
    public void submitBottles() {
        Student student = SessionManager.getCurrentStudent();
        if (student == null) {
            setStatus("Please log in first.");
            return;
        }
        try {
            int bottles = Integer.parseInt(bottleCountField.getText().trim());
            SubmitResult result = bottleService.submitBottles(student.getStudentId(), bottles);
            if (result.isSuccess()) {
                setStatus("Earned " + result.getTotalPoints() + " pts. Badge: " + result.getNewBadgeTier());
            } else {
                setStatus(result.getMessage());
            }
        } catch (NumberFormatException e) {
            setStatus("Bottle count must be a whole number.");
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
