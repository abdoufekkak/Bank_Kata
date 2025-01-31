package com.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.repository.TransactionRepository;

public class AccountTest {
    private Account account;
    private TransactionRepository repository;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
        account = new Account(repository);
        System.setOut(new PrintStream(outputStreamCaptor));

    }

    @Test
    void initialBalanceShouldBeZero() {
        assertTrue(repository.getTransactions().isEmpty());
    }
   
    @Test
    void depositShouldIncreaseBalance() {
        account.deposit(1000);
        assertEquals(1000, repository.getTransactions().get(0).getBalance());
    }

    @Test
    void withdrawShouldDecreaseBalance() {
        account.deposit(2000);
        account.withdraw(500);
        assertEquals(1500, repository.getTransactions().get(1).getBalance());
    }

    @Test
    void transactionsShouldBeRecordedCorrectly() {
        account.deposit(1000);
        account.withdraw(500);

        List<Transaction> transactions = repository.getTransactions();

        assertEquals(2, transactions.size());
        assertEquals(1000, transactions.get(0).getAmount());
        assertEquals(-500, transactions.get(1).getAmount());
    }

    @Test
    void shouldHandleMultipleDepositsAndWithdrawals() {
        account.deposit(500);
        account.withdraw(200);
        account.deposit(700);
        account.withdraw(100);

        assertEquals(900, repository.getTransactions().get(3).getBalance());
    }

    @Test
    void cannotDepositNegativeAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
        assertEquals("Le montant du dépôt doit être positif.", exception.getMessage());
    }
    @Test
    void cannotDepositZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
        assertEquals("Le montant du dépôt doit être positif.", exception.getMessage());
    }
    @Test
    void cannotWithdrawZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
        assertEquals("Le montant du retrait doit être positif.", exception.getMessage());
    }
    @Test
    void cannotWithdrawNegativeAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
        assertEquals("Le montant du retrait doit être positif.", exception.getMessage());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        account.deposit(1000);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> account.withdraw(2000));
        assertEquals("Fonds insuffisants.", exception.getMessage());
    }
    @Test
    void testPrintStatement() {
        // Ajouter des transactions à l'account
        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);

        // Appeler la méthode printStatement
        account.printStatement();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        // Attendu : Format de la sortie après avoir déposé 1000, 2000 et retiré 500
        String expectedOutput = "date       || Amount  || Balance\n"
        		+formattedDate + " || 1000    || 1000   \n"
        		+formattedDate  + " || 2000    || 3000   \n"
        		+formattedDate  + " || -500    || 2500   \n";

        // Vérifier que la sortie capturée correspond à la sortie attendue
        assertEquals(expectedOutput.replace("\r\n", "\n"), outputStreamCaptor.toString().replace("\r\n", "\n"));
    }
}
