package com.app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.app.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Comparator;

public class Account implements AccountService {
    private final TransactionRepository transactionRepository;
    private int balance = 0;

    public Account(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif.");
        }
        balance += amount;
        transactionRepository.addTransaction(new Transaction(LocalDate.now(), amount, balance));
    }

    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Fonds insuffisants.");
        }
        balance -= amount;
        transactionRepository.addTransaction(new Transaction(LocalDate.now(), -amount, balance));
    }
    @Override
    public void printStatement() {
        List<Transaction> transactions = new ArrayList<>(transactionRepository.getTransactions());

        // Trier les transactions par date, du plus récent au plus ancien
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        // Affichage de l'entête
        System.out.println("date       || Amount  || Balance");

        // Formatage et affichage des transactions
        for (Transaction transaction : transactions) {
            // Formatage de la date au format dd/MM/yyyy
            String formattedDate = transaction.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Affichage avec un espacement pour aligner les colonnes
            System.out.printf("%-10s || %-7d || %-7d%n", formattedDate, transaction.getAmount(), transaction.getBalance());
        }
    }
}
