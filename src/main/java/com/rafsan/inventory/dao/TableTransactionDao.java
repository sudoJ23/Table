package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.TableTransaction;
import javafx.collections.ObservableList;

public interface TableTransactionDao {
    
    public ObservableList<TableTransaction> getTableTransactions();
    public TableTransaction getTableTransaction(long id);
    public TableTransaction getTableTransactionByTransID(String id);
    public void saveTableTransaction(TableTransaction transaction);
    public void updateTableTransaction(TableTransaction transaction);
    public void deleteTableTransaction(TableTransaction transaction);
}
