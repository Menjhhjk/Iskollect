package com.iskollect.controller;

import com.iskollect.model.User;
import com.iskollect.service.ActivityHistoryService;
import com.iskollect.service.ActivityHistoryService.HistoryFilter;
import com.iskollect.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class ActivityHistoryController {
    @FXML private TableView<Object> historyTable;
    @FXML private ComboBox<HistoryFilter> filterComboBox;

    private final ActivityHistoryService activityHistoryService = new ActivityHistoryService();

    @FXML
    public void initialize() {
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(HistoryFilter.values()));
        }
        loadFullHistory();
    }

    @FXML
    public void loadFullHistory() {
        User user = SessionManager.getCurrentUser();
        if (user != null && historyTable != null) {
            historyTable.setItems(FXCollections.observableArrayList(
                    activityHistoryService.getFullHistory(user.getUserId()).getEntries()));
        }
    }

    @FXML
    public void applyFilter() {
        User user = SessionManager.getCurrentUser();
        if (user == null || historyTable == null || filterComboBox == null || filterComboBox.getValue() == null) {
            return;
        }
        historyTable.setItems(FXCollections.observableArrayList(
                activityHistoryService.getFilteredHistory(user.getUserId(), filterComboBox.getValue()).getEntries()));
    }
}
