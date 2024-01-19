package com.rafsan.inventory.controller.billiard;

import com.rafsan.inventory.core;
import static com.rafsan.inventory.core.tableLoaded;
import com.rafsan.inventory.entity.Station;
import com.rafsan.inventory.entity.Tables;
import com.rafsan.inventory.entity.Timer;
import static com.rafsan.inventory.interfaces.StationInterface.STATIONLIST;
import static com.rafsan.inventory.interfaces.TablesInterface.TABLELIST;
import static com.rafsan.inventory.interfaces.ActiveTransaction.ACTIVETRANS;
import com.rafsan.inventory.model.ActiveTransactionModel;
import com.rafsan.inventory.model.StationModel;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BilliardController implements Initializable {
    
    @FXML
    private GridPane tGridLayar;
    
    @FXML
    protected TableView<Tables> transList;
    
    @FXML
    protected TableColumn<Tables, String> tTableId, tTableUser, tTableStation, tTableDurasi, tTableTransStart, tTableTransClose, tTableTransId, tTableBayar;
    
    @FXML
    private Label totRental, totPenjualan, totDiskon, totPajak, totService, totPrice, totDurasi, totStartStatus, totStopStatus, totTrans, totCash, totNonCash;
    
    @FXML
    public Label comPortStatus;
    
    @FXML
    public Button restockbtn;
    
    @FXML
    private ScrollPane scrollpaneKiri;
    
    private int LastGridRow = 0;
    private int LastGridColumn = 0;
    private int tableCount = 1;
    
    private Tables tables;
    private UUID customerUUID;
    private String customerName;
    private Timer timer;
    private StationModel stationModel;
    private ActiveTransactionModel activeTransactionModel = new ActiveTransactionModel();
    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    public void reload() {
        System.out.println("reloading table");
        tableLoaded = false;
        tGridLayar.getChildren().clear();
        tGridLayar.getRowConstraints().clear();
        tGridLayar.getColumnConstraints().clear();
        tGridLayar.setGridLinesVisible(false);
        tGridLayar.setPadding(new Insets(5));
        tGridLayar.setVgap(5);
        tGridLayar.setHgap(5);
        LastGridRow = 0;
        LastGridColumn = 0;
        loadTables();
        try {
            loadCustomer();
        } catch (IOException ex) {
            Logger.getLogger(BilliardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableLoaded = true;
        System.out.println("done reloading table");
    }
    
    public ActiveTransactionModel getActiveTransactionModel() {
        return activeTransactionModel;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        core.setBilliardController(this);
        stationModel = new StationModel();
        format.setMaximumFractionDigits(0);
        
        activeTransactionModel.setParent(this);
        
        tTableId.setCellValueFactory(data -> {
            return new ReadOnlyStringWrapper(data.getValue().getTransaction().getId());
        });
        tTableStation.setCellValueFactory(data -> {
            return new ReadOnlyStringWrapper(data.getValue().getStation().getName());
        });
        tTableDurasi.setCellValueFactory(data -> {
            String result = "00:00:00";
            if (data.getValue().getTableTransaction() != null) {
                result = data.getValue().getTableTransaction().getDuration();
            }
            return new ReadOnlyStringWrapper(result);
        });
        tTableTransStart.setCellValueFactory(data -> {
            if (data.getValue().getTableTransaction() != null) {
                if (data.getValue().getTableTransaction().getStart() != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return new ReadOnlyStringWrapper(formatter.format(data.getValue().getTableTransaction().getStart()));
                } else {
                    return new ReadOnlyStringWrapper("~");
                }
            } else {
                return new ReadOnlyStringWrapper("~");
            }
        });
        tTableTransClose.setCellValueFactory(data -> {
            if (data.getValue().getTableTransaction() != null) {
                if (data.getValue().getTableTransaction().getStop() != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    return new ReadOnlyStringWrapper(formatter.format(data.getValue().getTableTransaction().getStop()));
                } else {
                    return new ReadOnlyStringWrapper("~");
                }
            } else {
                return new ReadOnlyStringWrapper("~");
            }
        });
        if (!tableLoaded) {
            loadTables();
            try {
                loadCustomer();
            } catch (IOException ex) {
                Logger.getLogger(BilliardController.class.getName()).log(Level.SEVERE, null, ex);
            }
            ACTIVETRANS.clear();
            try {
                // TODO: uncomment line below to restore logic
                // activeTransactionModel.getTable();
                activeTransactionModel.getTableUsingCalculation();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            tableLoaded = true;
        } else {
            try {
                loadCustomer();
            } catch (IOException ex) {
                Logger.getLogger(BilliardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        transList.setItems(ACTIVETRANS);
        transList.getSortOrder().add(tTableId);
        tTableId.setSortType(TableColumn.SortType.DESCENDING);
        transList.sort();
        
        System.out.println("Is loaded " + tableLoaded);
    }
    
    @FXML
    public void setup(ActionEvent event) throws Exception {
        windows("/fxml/Settings.fxml", "Settings", event);
    }
    
    private void windows(String path, String title, ActionEvent event) throws Exception {

        double width = ((Node) event.getSource()).getScene().getWidth();
        double height = ((Node) event.getSource()).getScene().getHeight();

        Parent root = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(root, width, height);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.setScene(scene);
        stage.show();
    }
    
    public void loadTables() {
        if (!TABLELIST.isEmpty()) {
            TABLELIST.clear();
        }

        System.out.println("Table list : " + TABLELIST.size());
        System.out.println("Station list : " + STATIONLIST.size());
        for (Station i : STATIONLIST) {
            timer = new Timer();
            timer.setActiveTransactionModel(activeTransactionModel);
            timer.getActiveTransactionModel().setParent(this);
            tables = new Tables(i, timer);
            TABLELIST.add(tables);
        }
    }
    
//    public void loadData() {
//        long totalDurasi = 0;
//        double rental = 0;
//        double penjualan = 0;
//        double diskon = 0;
//        double pajak = 0;
//        double service = 0;
//        double total = 0;
//        int stop = 0;
//        int totaltrans = 0;
//        double cash = 0;
//        double noncash = 0;
//        String total_durasi = "00:00:00";
//        
//        for (Tables table : ACTIVETRANS) {
//            if (table.getTableTransaction() != null) {
//                rental += Double.parseDouble(table.getTableTransaction().getAmount());
////                diskon += Double.parseDouble(table.getTableTransaction().getDiscount());
////                total += Double.parseDouble(table.getTableTransaction().getAmount());
////                total += Double.parseDouble(table.getTableTransaction().getService());
////                total += Double.parseDouble(table.getTableTransaction().getTax());
//                stop += 1;
//                totaltrans += 1;
////                System.out.println(table.getTableTransaction().getDuration());
//                String[] durationStr = table.getTableTransaction().getDuration().split(":");
//                
//                long durasi = 0;
//                
//                if (Long.parseLong(durationStr[0]) > 0) {
//                        durasi += (Long.parseLong(durationStr[0]) * 3600);
//                    }
//                    
//                    if (Long.parseLong(durationStr[1]) > 0) {
//                        durasi += (Long.parseLong(durationStr[1]) * 60);
//                    }
//                    
//                    if (Long.parseLong(durationStr[2]) > 0) {
//                        durasi += Long.parseLong(durationStr[2]);
//                    }
//                    
//                    if (durasi > 0) {
//                        totalDurasi += durasi;
//                    }
//                
//                Duration duration = Duration.ofSeconds(totalDurasi);
//                total_durasi = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60);
////                System.out.println(total_durasi);
//            } else {
//                totaltrans += 1;
//            }
//            
//            cash += Double.parseDouble(table.getTransaction().getCash());
//            noncash += Double.parseDouble(table.getTransaction().getCashless());
//            
//            if (!table.items.isEmpty()) {
////                System.out.println(table.items.size());
//                for (Item item : table.items) {
//                    penjualan += item.getTotal();
//                }
////                double temp2 = 0;
////                for (int i = 0; i < table.items.size(); i++) {
////                    penjualan += Double.parseDouble(table.items.get(i).getSale().getTotal());
////                    double temp = Double.parseDouble(table.items.get(i).getSale().getTax());
////                    temp2 += temp;
////                    penjualan -= temp;
////                    temp = Double.parseDouble(table.items.get(i).getSale().getService());
////                    temp2 += temp;
////                    penjualan -= temp;
////                    System.out.println("Penjualan : " + penjualan);
////                }
////                total += penjualan;
//            }            
////            long totalDurasi = 0;
////            double rental = 0;
////            double penjualan = 0;
////            double diskon = 0;
////            double pajak = 0;
////            double service = 0;
////            double total = 0;
////            int start = 0;
////            int stop = 0;
////            int totaltrans = 0;
////            double cash = 0;
////            double noncash = 0;
//            
////            System.out.println("=====================");
////            System.out.println("rental : " + rental);
////            System.out.println("penjualan : " + penjualan);
////            System.out.println("diskon : " + diskon);
////            System.out.println("pajak : " + pajak);
////            System.out.println("service : " + service);
////            System.out.println("total : " + total);
////            System.out.println("total duration : " + total_durasi);
////            System.out.println("=====================");
//        }
//        
////        total += (rental + penjualan - diskon);// + pajak + service - diskon);
//        total += (rental + penjualan + pajak + service - diskon);
//        
//        totRental.setText(format.format(rental).replace("Rp", ""));
//        totPenjualan.setText(format.format(penjualan).replace("Rp", ""));
//        totDiskon.setText(format.format(diskon).replace("Rp", ""));
//        totPajak.setText(format.format(pajak).replace("Rp", ""));
//        totService.setText(format.format(service).replace("Rp", ""));
//        totPrice.setText(format.format(total).replace("Rp", ""));
//        totTrans.setText(String.valueOf(totaltrans));
//        totStopStatus.setText(String.valueOf(stop));
//        totStartStatus.setText(String.valueOf(core.tableStart));
//        totCash.setText(format.format(cash).replace("Rp", ""));
//        totNonCash.setText(format.format(noncash).replace("Rp", ""));
//        totDurasi.setText(total_durasi);
////        System.out.println(totalDurasi);
//        transList.sort();
//    }
   
    public void loadCustomer() throws IOException {
        if (!tableLoaded) {
            int index = 0;
            for (int i = 0; i < 5; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dstation.fxml"));
                Node cst = loader.load();
                DStationController stationController = loader.getController();
//                stationController.isShowed = true;
                stationController.setTableIndex(index);
                stationController.setNode(cst);
                stationController.setLoader(loader);
                stationController.setParentController(this);
                cst.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 1) {
//                                if (i.getTimer().getIsFinished() || i.getTimer().getIsRunning()) {
//                                    stationController.getParentController().loadData(i);
//                                } else {
//                                    System.out.println("Tidak aktif");
//                                }
                        }
                        if (mouseEvent.getClickCount() == 2) {
                            System.out.println("Double clicked from " + stationController.numMeja.getText());
                            try {
                                stationController.showPopUp();
                            } catch (IOException ex) {
                                Logger.getLogger(BilliardController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                addNode(cst, TABLELIST.get(i));
                TABLELIST.get(i).setNode(cst);
                System.out.println("Adding " + TABLELIST.get(i).getStation().getName());
                TABLELIST.get(i).setStationController(stationController);
                stationController.setTables(TABLELIST.get(i));
                stationController.setData();
                index++;
            }
        } else {
            tGridLayar.getChildren().clear();
            tGridLayar.getRowConstraints().clear();
            tGridLayar.getColumnConstraints().clear();
            tGridLayar.setGridLinesVisible(false);
            tGridLayar.setPadding(new Insets(5));
            tGridLayar.setVgap(5);
            tGridLayar.setHgap(5);
            LastGridRow = 0;
            LastGridColumn = 0;
            System.out.println("Loading node");
            for (int i = 0; i < 5; i++) {
//                System.out.println(TABLELIST.get(i).getStation().getName());
//                System.out.println(TABLELIST.get(i).getTimer().getIsRunning());
//                System.out.println(TABLELIST.get(i).getTimer().getCount());
//                System.out.println(TABLELIST.get(i).getCustomer().getName());
                Node cst = TABLELIST.get(i).getNode();
                addNode(cst, TABLELIST.get(i));
            }
        }
    }
    
    public void addNode(Node node, Tables table) {
        if (tableCount <= 5) {
            table.setColumn(LastGridColumn);
            table.setRow(LastGridRow);
            tGridLayar.add(node, LastGridColumn, LastGridRow);
//            tGridLayar.getRowConstraints().clear();
            
            LastGridColumn++;
            if (LastGridColumn >= 4) {
                LastGridColumn = 0;
                LastGridRow++;
            }
//            System.out.println(table.getStation().getName() + " Added");
            tableCount++;
        }
        
        double maxWidth = (189*4+40);
        scrollpaneKiri.setMaxWidth(maxWidth);
    }
}
