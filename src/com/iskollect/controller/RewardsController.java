package com.iskollect.controller;

import com.iskollect.model.Reward;
import com.iskollect.service.RewardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class RewardsController {
    @FXML private TableView<Reward> rewardsTable;

    private final RewardService rewardService = new RewardService();

    @FXML
    public void initialize() {
        refreshRewards();
    }

    @FXML
    public void refreshRewards() {
        if (rewardsTable != null) {
            rewardsTable.setItems(FXCollections.observableArrayList(rewardService.getAllRewards()));
        }
    }
}
