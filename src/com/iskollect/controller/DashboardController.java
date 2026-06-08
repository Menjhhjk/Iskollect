package com.iskollect.controller;

import com.iskollect.model.User;
import com.iskollect.service.BadgeService;
import com.iskollect.service.PointsService;
import com.iskollect.service.StreakService;
import com.iskollect.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label nameLabel;
    @FXML private Label pointsLabel;
    @FXML private Label badgeLabel;
    @FXML private Label streakLabel;

    private final PointsService pointsService = new PointsService();
    private final BadgeService badgeService = new BadgeService();
    private final StreakService streakService = new StreakService();

    @FXML
    public void initialize() {
        refresh();
    }

    @FXML
    public void refresh() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            setText(nameLabel, "No active session");
            return;
        }
        int userId = user.getUserId();
        setText(nameLabel, user.getName());
        setText(pointsLabel, String.valueOf(pointsService.getTotalPoints(userId)));
        setText(badgeLabel, badgeService.getCurrentBadge(userId).getTierName());
        setText(streakLabel, String.valueOf(streakService.getStreakCount(userId)));
    }

    private void setText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }
}
