package com.iskollect.service;

import com.iskollect.dao.RedeemedRewardDAO;
import com.iskollect.dao.TransactionDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.RedeemedReward;
import com.iskollect.model.Transaction;
import com.iskollect.model.TransactionHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransactionService {
    public enum HistoryFilter {
        TODAY, THIS_WEEK, THIS_MONTH, THIS_YEAR
    }

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final RedeemedRewardDAO redeemedRewardDAO = new RedeemedRewardDAO();

    public TransactionHistory getFullHistory(int studentId) {
        try {
            List<Object> entries = new ArrayList<>();
            entries.addAll(transactionDAO.getByStudentId(studentId));
            entries.addAll(redeemedRewardDAO.getByStudentId(studentId));
            entries.sort(Comparator.comparing(this::entryDate).reversed());
            return new TransactionHistory(entries);
        } catch (DatabaseException e) {
            return new TransactionHistory(List.of());
        }
    }

    public TransactionHistory getFilteredHistory(int studentId, HistoryFilter filter) {
        LocalDate today = LocalDate.now();
        LocalDate from;
        if (filter == HistoryFilter.TODAY) {
            from = today;
        } else if (filter == HistoryFilter.THIS_WEEK) {
            from = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        } else if (filter == HistoryFilter.THIS_MONTH) {
            from = today.withDayOfMonth(1);
        } else {
            from = today.withDayOfYear(1);
        }

        List<Object> filtered = new ArrayList<>();
        for (Object entry : getFullHistory(studentId).getEntries()) {
            LocalDate date = entryDate(entry);
            if (!date.isBefore(from) && !date.isAfter(today)) {
                filtered.add(entry);
            }
        }
        return new TransactionHistory(filtered);
    }

    private LocalDate entryDate(Object entry) {
        if (entry instanceof Transaction) {
            return ((Transaction) entry).getDate();
        }
        return ((RedeemedReward) entry).getRedeemDate();
    }
}
