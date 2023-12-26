package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.Station;
import javafx.collections.ObservableList;

public interface StationDao {
    
    public ObservableList<Station> getStations();
    public Station getStation(long id);
    public Station getStationByName(String name);
    public void saveStation(Station station);
    public void updateStation(Station station);
    public void deleteStation(Station station);
    public ObservableList<String> getNames();
}
