package com.iskollect.controller;

import com.iskollect.model.Student;
import com.iskollect.service.TransactionService;
import com.iskollect.service.TransactionService.HistoryFilter;
import com.iskollect.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class TransactionController {
    @FXML private TableView<Object> historyTable;
    @FXML private ComboBox<HistoryFilter> filterComboBox;

    private final TransactionService transactionService = new TransactionService();

    @FXML
    public void initialize() {
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(HistoryFilter.values()));
        }
        loadFullHistory();
    }

    @FXML
    public void loadFullHistory() {
        Student student = SessionManager.getCurrentStudent();
        if (student != null && historyTable != null) {
            historyTable.setItems(FXCollections.observableArrayList(
                    transactionService.getFullHistory(student.getStudentId()).getEntries()));
        }
    }

    @FXML
    public void applyFilter() {
        Student student = SessionManager.getCurrentStudent();
        if (student == null || historyTable == null || filterComboBox == null || filterComboBox.getValue() == null) {
            return;
        }
        historyTable.setItems(FXCollections.observableArrayList(
                transactionService.getFilteredHistory(student.getStudentId(), filterComboBox.getValue()).getEntries()));
    }
}
