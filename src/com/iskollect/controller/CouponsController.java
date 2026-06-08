package com.iskollect.controller;

import com.iskollect.model.Coupon;
import com.iskollect.service.CouponService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class CouponsController {
    @FXML private TableView<Coupon> couponsTable;

    private final CouponService couponService = new CouponService();

    @FXML
    public void initialize() {
        refreshCoupons();
    }

    @FXML
    public void refreshCoupons() {
        if (couponsTable != null) {
            couponsTable.setItems(FXCollections.observableArrayList(couponService.getAllCoupons()));
        }
    }
}
