package com.iskollect.controller;

import com.iskollect.model.RedeemResult;
import com.iskollect.model.Student;
import com.iskollect.service.RewardService;
import com.iskollect.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RedeemController {
    @FXML private TextField rewardIdField;
    @FXML private Label statusLabel;
    @FXML private Label couponCodeLabel;

    private final RewardService rewardService = new RewardService();

    @FXML
    public void redeem() {
        Student student = SessionManager.getCurrentStudent();
        if (student == null) {
            setStatus("Please log in first.");
            return;
        }
        try {
            int rewardId = Integer.parseInt(rewardIdField.getText().trim());
            RedeemResult result = rewardService.redeem(student.getStudentId(), rewardId);
            setStatus(result.getMessage());
            if (couponCodeLabel != null) {
                couponCodeLabel.setText(result.isSuccess() ? result.getCouponCode() : "");
            }
        } catch (NumberFormatException e) {
            setStatus("Reward ID must be a whole number.");
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
