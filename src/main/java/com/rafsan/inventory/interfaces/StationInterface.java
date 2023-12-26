package com.rafsan.inventory.interfaces;

import com.rafsan.inventory.entity.Station;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface StationInterface {
    public ObservableList<Station> STATIONLIST = FXCollections.observableArrayList();
}
