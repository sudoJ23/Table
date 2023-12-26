package com.rafsan.inventory;

import com.rafsan.inventory.controller.billiard.BilliardController;
import com.rafsan.inventory.entity.Station;
import static com.rafsan.inventory.interfaces.StationInterface.STATIONLIST;
import com.rafsan.inventory.model.StationModel;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class core {
    
    public static boolean tableLoaded = false;
    public static int tableStart = 0;
    private static BilliardController billiardController;
    
    public static boolean isQueue = false;
    public static void setBilliardController(BilliardController controller) {
        billiardController = controller;
    }
    
    public static BilliardController getBilliardController() {
        return billiardController;
    }
    
    public static void init() throws InterruptedException, IOException {
        STATIONLIST.addAll(new StationModel().getStations());
        for (Station station : STATIONLIST) {
            station.setStatus("Idle");
            new StationModel().updateStation(station);
        }
    }
}
