package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.Station;
import com.rafsan.inventory.entity.TablePackage;
import javafx.collections.ObservableList;

public interface TablePackageDao {
    public ObservableList<TablePackage> getTablePackages();
    public TablePackage getTablePackage(long id);
    public TablePackage getTablePackageByName(String tablePackageName);
//    public ObservableList<Station> getStation(long packageId);
    public void saveTablePackage(TablePackage tablePackage);
    public void updateTablePackage(TablePackage tablePackage);
    public void deleteTablePackage(TablePackage tablePackage);
    public ObservableList<String> getNames();
    public ObservableList<String> getAvailablePackage(String stationName);
}
