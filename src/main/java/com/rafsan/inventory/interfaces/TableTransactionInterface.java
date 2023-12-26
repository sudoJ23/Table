package com.rafsan.inventory.interfaces;

import com.rafsan.inventory.entity.TableTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface TableTransactionInterface {
    
    public ObservableList<TableTransaction> TABLETRANSACTIONLIST = FXCollections.observableArrayList();
}
