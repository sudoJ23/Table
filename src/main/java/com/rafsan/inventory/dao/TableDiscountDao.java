package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.TableDiscount;
import javafx.collections.ObservableList;

public interface TableDiscountDao {
    
    public ObservableList<TableDiscount> getTableDiscounts();
    public TableDiscount getTableDiscount(long id);
    public TableDiscount getTableDiscountByName(String name);
    public void saveTableDiscount(TableDiscount tableDiscount);
    public void updateTableDiscount(TableDiscount tableDiscount);
    public void deleteTableDiscount(TableDiscount tableDiscount);
    public ObservableList<String> getNames();
}
