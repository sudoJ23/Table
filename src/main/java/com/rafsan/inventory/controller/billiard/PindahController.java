package com.rafsan.inventory.controller.billiard;

import com.rafsan.inventory.entity.Station;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.entity.Tables;
import com.rafsan.inventory.model.StationModel;
import com.rafsan.inventory.model.TablePackageModel;
import com.rafsan.inventory.model.TableTransactionModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class PindahController implements Initializable {
    
    @FXML
    private ChoiceBox pilihmeja;
    
    @FXML
    private Label statusmeja;
    
    @FXML
    private Button btnPindahMeja, btnCancelMeja;
    
    private StationModel stationModel = new StationModel();
    private TablePackageModel tablePackageModel = new TablePackageModel();
    private TablePackage tablePackage;
    private ObservableList<Station> stationlist = FXCollections.observableArrayList();
    private packageController parent;
    private ObservableList<String> TableList = FXCollections.observableArrayList();
    private Tables tables;
    private TableTransactionModel tableTransactionModel = new TableTransactionModel();
    
    private Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stationlist = stationModel.getStations();
        pilihmeja.setItems(TableList);
        statusmeja.setText("Memuat meja");
        pilihmeja.setDisable(true);
        btnPindahMeja.setDisable(true);
    }
    
    public void loadData() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                System.out.println("Loading data");
                ObservableList<String> packageList = FXCollections.observableArrayList();
                ObservableList<String> tablelist = FXCollections.observableArrayList();

                for (Station station : stationlist) {
                    // Check if thread is interrupted
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread interrupted. Exiting...");
                        return null;
                    }

                    if (station.getStatus().equalsIgnoreCase("idle")) {
                        packageList = tablePackageModel.getAvailablePackage(station.getName());
                        for (String tablepackage : packageList) {
                            if (tablePackage.getName().equalsIgnoreCase(tablepackage)) {
                                tablelist.add(station.getName());
                            }
                        }
                    }
                }
                return tablelist;
            }
        };

        task.setOnSucceeded(event -> {
            ObservableList<String> tablelist = task.getValue();
            if (tablelist != null) {
                TableList = tablelist;
                pilihmeja.setItems(TableList);
                btnPindahMeja.setDisable(false);
                pilihmeja.setDisable(false);
                statusmeja.setText("Meja tersedia");
                System.out.println("Done loading");
            }
        });

        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private boolean validate() {
        boolean status = true;
        
        if (pilihmeja.getSelectionModel().getSelectedItem() == null || pilihmeja.getSelectionModel().getSelectedItem().toString() == null || pilihmeja.getSelectionModel().getSelectedItem().toString().length() == 0) {
            status = false;
        }
        
        return status;
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) {
        if (this.thread.isAlive()) {
            this.thread.interrupt();
            System.out.println("Interrupted : " + this.thread.isInterrupted());
            System.out.println("Alive : " + this.thread.isAlive());
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
    
    public void setParent(packageController parent) {
        this.parent = parent;
    }
    
    public void setPackage(TablePackage tablePackage) {
        this.tablePackage = tablePackage;
    }
    
    public void setTables(Tables tables) {
        this.tables = tables;
    }
}
