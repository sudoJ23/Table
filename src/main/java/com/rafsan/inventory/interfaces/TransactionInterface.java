package com.rafsan.inventory.interfaces;

import com.rafsan.inventory.entity.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface TransactionInterface {
    
    public ObservableList<Transaction> TRANSACTIONLIST = FXCollections.observableArrayList();
}
