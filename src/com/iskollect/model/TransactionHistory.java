package com.iskollect.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionHistory {
    private final List<Object> entries;

    public TransactionHistory(List<Object> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public List<Object> getEntries() {
        return entries;
    }

    public List<Transaction> getSubmissions() {
        List<Transaction> submissions = new ArrayList<>();
        for (Object entry : entries) {
            if (entry instanceof Transaction) {
                submissions.add((Transaction) entry);
            }
        }
        return submissions;
    }

    public List<RedeemedReward> getRedemptions() {
        List<RedeemedReward> redemptions = new ArrayList<>();
        for (Object entry : entries) {
            if (entry instanceof RedeemedReward) {
                redemptions.add((RedeemedReward) entry);
            }
        }
        return redemptions;
    }
}
