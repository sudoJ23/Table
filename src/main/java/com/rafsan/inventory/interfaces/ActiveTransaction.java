package com.rafsan.inventory.interfaces;

import com.rafsan.inventory.entity.Tables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface ActiveTransaction {
    static ObservableList<Tables> ACTIVETRANS = FXCollections.observableArrayList();
}
