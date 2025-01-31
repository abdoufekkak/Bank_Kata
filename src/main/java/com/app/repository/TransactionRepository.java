package com.app.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.app.Transaction;

public class TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
