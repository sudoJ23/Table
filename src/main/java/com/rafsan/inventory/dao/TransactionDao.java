package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.Transaction;
import java.util.Date;
import java.util.List;
import javafx.collections.ObservableList;

public interface TransactionDao {
    
    public ObservableList<Transaction> getTransactions();
    public List<String> getTransactionsByRange(String start, String stop);
    public Transaction getTransaction(String id);
    public void saveTransaction(Transaction transaction);
    public void updateTransaction(Transaction transaction);
    public void deleteTransaction(Transaction transaction);
    public Transaction getLast();
    public Transaction getLastAuditDate();
    public List<Transaction> getTransactionByAuditDate(Date auditdate);
    public Transaction addTransaction();
}
