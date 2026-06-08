package com.iskollect.controller;

import com.iskollect.model.RedeemResult;
import com.iskollect.model.User;
import com.iskollect.service.CouponService;
import com.iskollect.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RedeemController {
    @FXML private TextField couponIdField;
    @FXML private Label statusLabel;
    @FXML private Label couponCodeLabel;

    private final CouponService couponService = new CouponService();

    @FXML
    public void redeem() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            setStatus("Please log in first.");
            return;
        }
        try {
            int couponId = Integer.parseInt(couponIdField.getText().trim());
            RedeemResult result = couponService.redeem(user.getUserId(), couponId);
            setStatus(result.getMessage());
            if (couponCodeLabel != null) {
                couponCodeLabel.setText(result.isSuccess() ? result.getCouponCode() : "");
            }
        } catch (NumberFormatException e) {
            setStatus("Coupon ID must be a whole number.");
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
