package com.iskollect.controller;

import com.iskollect.model.Student;
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
        Student student = SessionManager.getCurrentStudent();
        if (student == null) {
            setText(nameLabel, "No active session");
            return;
        }
        int studentId = student.getStudentId();
        setText(nameLabel, student.getName());
        setText(pointsLabel, String.valueOf(pointsService.getTotalPoints(studentId)));
        setText(badgeLabel, badgeService.getCurrentBadge(studentId).getTierName());
        setText(streakLabel, String.valueOf(streakService.getStreakCount(studentId)));
    }

    private void setText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }
}
