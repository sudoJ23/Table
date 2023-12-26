package com.rafsan.inventory.controller.billiard;

import com.rafsan.inventory.core;
import com.rafsan.inventory.entity.ActivePackage;
import com.rafsan.inventory.entity.Station;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.entity.TableTransaction;
import com.rafsan.inventory.entity.Tables;
import com.rafsan.inventory.entity.Timer;
import com.rafsan.inventory.entity.Transaction;
import static com.rafsan.inventory.interfaces.TablesInterface.TABLELIST;
import com.rafsan.inventory.model.ActivePackageModel;
import com.rafsan.inventory.model.ActiveTransactionModel;
import com.rafsan.inventory.model.StationModel;
import com.rafsan.inventory.model.TablePackageModel;
import com.rafsan.inventory.model.TableTransactionModel;
import com.rafsan.inventory.model.TransactionModel;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

public class packageController implements Initializable {
    
    @FXML
    public Label nomorMeja;
    
    @FXML
    public ChoiceBox pilPaketList, pilPaketSG, pilPaketListJam;
    
    @FXML
    public Spinner<Integer> pilPaketJam, pilPaketMenit;
    
    @FXML
    public TextField pilPaketUser;
    
    @FXML
    public Button pilButtonJual, pilPaketPindah, pilPaketRefresh, PilPaketBatal, pilButtonStart;
    
    private TablePackage tablePackage;
    private TablePackageModel tablePackageModel;
    
    private TableTransaction tableTransaction;
    private TableTransactionModel tableTransactionModel = new TableTransactionModel();
    
    private Transaction transaction;
    private TransactionModel transactionModel = new TransactionModel();
    
    protected Tables tables;
    private double xOffset = 0, yOffset = 0;
    
    private StationModel stationModel = new StationModel();
    private ActiveTransactionModel activeTransactionModel;
    private ActivePackageModel activePackageModel;
    
    private Timer timer;
    
    private int currentJam = 1, currentMenit = 1, currentDetik = 0;
    
    private DStationController parentController;
    private boolean isRunning;
    
    public void setTables(Tables table) {
        this.tables = table;
    }
    
    public Tables getTables() {
        return tables;
    }
    
    public void setStatus(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    public void setActiveTransactionModel(ActiveTransactionModel model) {
        this.activeTransactionModel = model;
    }
    
    public void setInit(boolean isRunning, boolean isFinished, boolean isPaused) {
        this.isRunning = isRunning;
        System.out.println("packageController : " + this.isRunning);
        if (!isPaused) {
            if (isRunning) {
                pilPaketJam.setEditable(false);
                pilPaketMenit.setEditable(false);
                pilPaketListJam.setDisable(true);
                pilPaketJam.setDisable(true);
                pilPaketMenit.setDisable(true);
                pilButtonStart.setText("Ganti");
                if (tables.getTimer().getCount() > 300) {
                    PilPaketBatal.setDisable(false);
                } else {
                    PilPaketBatal.setDisable(true);
                }
                pilPaketList.setDisable(true);
            } else {
                if (isFinished) {
                    PilPaketBatal.setDisable(true);
                    pilButtonStart.setDisable(true);
                    pilButtonStart.setText("Start");
                    pilPaketList.setDisable(true);
                } else {
                    PilPaketBatal.setDisable(true);
                    pilButtonStart.setText("Start");
                    pilPaketList.setDisable(false);
                }
            }
        } else {
            pilPaketMenit.setDisable(true);
            pilPaketJam.setDisable(true);
            pilPaketListJam.setDisable(true);
            pilPaketList.setDisable(true);
            pilButtonStart.setDisable(true);
            PilPaketBatal.setDisable(true);
        }
    }
    
    public void setParent(DStationController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablePackageModel = new TablePackageModel();
        stationModel = new StationModel();
        activePackageModel = new ActivePackageModel();
        
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60);
        valueFactory.setValue(0);
        valueFactory2.setValue(0);
        pilPaketJam.setValueFactory(valueFactory);
        pilPaketMenit.setValueFactory(valueFactory2);
        
        pilPaketJam.setEditable(false);
        pilPaketMenit.setEditable(false);
        
        pilPaketJam.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> ov, Integer t, Integer t1) {
                currentJam = pilPaketJam.getValue();
                currentMenit = pilPaketMenit.getValue();
                
                String res = String.format("%02d", currentJam)+":"+String.format("%02d", currentMenit)+":00";
                pilPaketListJam.setValue(res);
            }
        });
        
        pilPaketMenit.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> ov, Integer t, Integer t1) {
                currentJam = pilPaketJam.getValue();
                currentMenit = pilPaketMenit.getValue();
                
                String res = String.format("%02d", currentJam)+":"+String.format("%02d", currentMenit)+":00";
                pilPaketListJam.setValue(res);
            }
        });
        
        pilPaketListJam.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                String res = pilPaketListJam.getValue().toString();
                int menit = 0, jam = 0, detik = 0;
                
                jam = Integer.valueOf(res.split(":")[0]);
                menit = Integer.valueOf(res.split(":")[1]);
                detik = Integer.valueOf(res.split(":")[2]);
                
                currentJam = jam;
                currentMenit = menit;
                currentDetik = detik;
                
                pilPaketJam.getValueFactory().setValue(jam);
                pilPaketMenit.getValueFactory().setValue(menit);
            }
        });
        
        pilPaketList.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                tablePackage = tablePackageModel.getTablePackageByName(t1);
                if (tablePackage != null && !"00:00:00".equals(tablePackage.getStopSetelah())) {
                    String[] format = tablePackage.getStopSetelah().split(":");
                    pilPaketJam.getValueFactory().setValue(Integer.valueOf(format[0]));
                    pilPaketMenit.getValueFactory().setValue(Integer.valueOf(format[1]));
                    currentDetik = Integer.valueOf(format[2]);
                    
                    pilPaketJam.setDisable(true);
                    pilPaketMenit.setDisable(true);
                    pilPaketListJam.setDisable(true);
                } else {
                    pilPaketJam.getValueFactory().setValue(0);
                    pilPaketMenit.getValueFactory().setValue(0);
                    currentDetik = 0;
                    
                    pilPaketJam.setDisable(false);
                    pilPaketMenit.setDisable(false);
                    pilPaketListJam.setDisable(false);
                }
            }
        });
        
        setPilPaketListJam();
    }
    
    public void setPilPaketListJam() {
        ObservableList<String> list = FXCollections.observableArrayList();
        
        list.add("00:00:00");
        list.add("00:30:00");
        list.add("01:00:00");
        list.add("01:30:00");
        list.add("02:00:00");
        list.add("02:30:00");
        list.add("03:00:00");
        list.add("03:30:00");
        list.add("04:00:00");
        list.add("04:30:00");
        list.add("05:00:00");
        list.add("05:30:00");
        list.add("10:00:00");
        list.add("23:59:59");
        pilPaketListJam.setItems(list);
        pilPaketListJam.setValue("00:00:00");
    }
    
    public void setPackages(String tableName) {
        pilPaketList.setItems(tablePackageModel.getAvailablePackage(tableName));
    }
    
    @FXML
    public void closePop(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
    
    @FXML
    public void start(ActionEvent event) throws InterruptedException, IOException {
        int totalseconds = 0, totalminutes = 0, totalhours = 0;
        if (isRunning) {
            if (pilPaketMenit.isDisable()) {
                pilPaketMenit.setDisable(false);
                pilPaketJam.setDisable(false);
                pilPaketListJam.setDisable(false);
            } else {
                totalhours = pilPaketJam.getValue() * 3600;
                totalminutes = pilPaketMenit.getValue() * 60;
                totalseconds = totalhours + totalminutes;
                totalseconds += currentDetik;
                System.out.println(totalseconds);
                
//                String[] durasi = tables.getTableTransaction().getDuration().split(":");
                String[] durasi = tables.getTimer().getMulai().split(":");
                int jam = Integer.valueOf(durasi[0]);
                int menit = Integer.valueOf(durasi[1]);
                int detik = Integer.valueOf(durasi[2]);
                int totalDetik = jam * 3600 + menit * 60 + detik;
                Duration duration = Duration.ofSeconds(totalseconds);

                System.out.println(totalDetik);
                if (totalseconds == 0) {
                    tables.getTableTransaction().setTarget(String.valueOf(totalseconds));
                    tables.getTimer().setDurationSisa(String.format("%02d", pilPaketJam.getValue())+":"+String.format("%02d", pilPaketMenit.getValue())+":"+String.format("%02d", currentDetik), "00:00:00");
                    tables.getTimer().setTarget(totalseconds);
                    parentController.tDurasiMeja.setText(String.format("%02d:%02d:%02d",duration.toHours(),duration.toMinutes() % 60,duration.getSeconds() % 60));
                    parentController.tSisaMeja.setText("00:00:00");
                    tables.getTimer().restartTimer();
                    tableTransactionModel.updateTableTransaction(tables.getTableTransaction());
                    pilPaketMenit.setDisable(true);
                    pilPaketJam.setDisable(true);
                    pilPaketListJam.setDisable(true);
                } else {
                    if (totalseconds > totalDetik) {
                        System.out.println("Sesuai");
                        int sisaDetik = (totalseconds - totalDetik);
                        Duration SisaDetik = Duration.ofSeconds(sisaDetik);
                        tables.getTableTransaction().setTarget(String.valueOf(totalseconds));
                        tables.getTimer().setDurationSisa(String.format("%02d", pilPaketJam.getValue())+":"+String.format("%02d", pilPaketMenit.getValue())+":"+String.format("%02d", currentDetik), String.format("%02d:%02d:%02d",SisaDetik.toHours(),SisaDetik.toMinutes() % 60,SisaDetik.getSeconds() % 60));
                        tables.getTimer().setTarget(sisaDetik);
                        parentController.tDurasiMeja.setText(String.format("%02d:%02d:%02d",duration.toHours(),duration.toMinutes() % 60,duration.getSeconds() % 60));
                        parentController.tSisaMeja.setText(String.format("%02d:%02d:%02d",SisaDetik.toHours(),SisaDetik.toMinutes() % 60,SisaDetik.getSeconds() % 60));
                        tables.getTimer().restartTimer();
                        tableTransactionModel.updateTableTransaction(tables.getTableTransaction());
                        pilPaketMenit.setDisable(true);
                        pilPaketJam.setDisable(true);
                        pilPaketListJam.setDisable(true);
                    } else {
                        notif(Alert.AlertType.WARNING, "Kesalahan", "Kesalahan", "Harus lebih besar dari waktu sebelumnya");
                    }
                }
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        } else {
                if (validation()) {
                    totalhours = pilPaketJam.getValue() * 3600;
                    totalminutes = pilPaketMenit.getValue() * 60;
                    totalseconds = totalhours + totalminutes;
                    totalseconds += currentDetik;
                    System.out.println(totalseconds);
                    System.out.println("Play for " + String.format("%02d", pilPaketJam.getValue())+":"+String.format("%02d", pilPaketMenit.getValue())+":"+String.format("%02d", currentDetik));

                    System.out.println("Selected package : " + pilPaketList.getSelectionModel().getSelectedItem().toString());
                    tablePackage = tablePackageModel.getTablePackageByName(pilPaketList.getSelectionModel().getSelectedItem().toString());
                    ActivePackage activePackage = activePackageModel.setActivePackage(tablePackage);

                    tables.setActivePackage(activePackage);
                    System.out.println("Rate every " + activePackage.getRateList().getAvailableRate().getEvery());
                    tables.getTimer().setParent(tables);
                    tables.getTimer().setPackageController(this);
                    tables.getTimer().setTarget(totalseconds);
                    tables.getTimer().setDuration(String.format("%02d", pilPaketJam.getValue())+":"+String.format("%02d", pilPaketMenit.getValue())+":"+String.format("%02d", currentDetik));
                    parentController.tDurasiMeja.setText(String.format("%02d", pilPaketJam.getValue())+":"+String.format("%02d", pilPaketMenit.getValue())+":"+String.format("%02d", currentDetik));
                    parentController.tPaket.setText(tablePackage.getName());
//                    tables.setStationController(parentController);
                    tables.setTimerParent(parentController);
                    parentController.tStatusMeja.setImage(new Image("/images/start.png"));
                    parentController.mainFrame.setStyle("-fx-background-color: #96EA93; -fx-background-radius: 10px;");
                    pilPaketJam.setDisable(true);
                    pilPaketMenit.setDisable(true);
                    PilPaketBatal.setDisable(false);
                    pilPaketList.setDisable(true);
                    pilPaketListJam.setDisable(true);
                    
                    tables.setTransStart(new Date());
                    
                    transaction = transactionModel.addTransaction();
                    while (transaction == null) {
                        transaction = transactionModel.addTransaction();
                    }
                    System.out.println(tables.getStation().getName());
                    tableTransaction = new TableTransaction(transaction, tables.getStation(), tables.getTransStart(), "0", "00:00:00", (short) 0);
                    tableTransaction.setPowerFailure((short) 0);
                    tableTransaction.setTarget(String.valueOf(totalseconds));
                    tableTransaction.setTablePackage(tablePackage);
                    tableTransactionModel.saveTableTransaction(tableTransaction);
                    transactionModel.updateTransaction(transaction);
                    
                    tables.setTableTransaction(tableTransaction);
                    tables.setTransaction(transaction);
                    tables.startTimer(false);
                    
//                    ACTIVETRANS.add(tables);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
//            }
        }
    }
    
    public boolean validation() {
        boolean status = false;
        if (pilPaketList.getSelectionModel().isEmpty()) {
            notif(Alert.AlertType.WARNING, "Kesalahan", "Kesalahan input", "Harap mengisi input dengan benar");
        } else {
            status = true;
        }
        
        return status;
    }
    
    public void notif(Alert.AlertType type,String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    public void cancel(ActionEvent actionEvent) throws InterruptedException, IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Stop");
        alert.setHeaderText("Hentikan meja");
        alert.setContentText("Apakah anda yakin ingin stop?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            tables.getTimer().stopTimer(false, false);
            stop();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
    
    public void stop() {
        timer = new Timer();
        timer.setActiveTransactionModel(tables.getTimer().getActiveTransactionModel());
        
        tables = new Tables(tables.getStation(), timer);
        tables.setStationController(parentController);
        int index = 0;
        for (Tables table : TABLELIST) {
            if (tables.getStation().getName().equals(table.getStation().getName())) {
                TABLELIST.set(index, tables);
            }
            index++;
        }
        parentController.setTables(tables);
        parentController.setData();
        
        parentController.tPaket.setText("~");
        parentController.tBiayaMeja.setText("0");
        parentController.tDurasiMeja.setText("00:00:00");
        parentController.tJualMeja.setText("0");
        parentController.tSisaMeja.setText("00:00:00");
        parentController.tTotMeja.setText("0");
        parentController.tStartMeja.setText("00:00:00");
        parentController.tStartDateMeja.setText("00:00:00");

        pilPaketJam.getEditor().setText("0");
        pilPaketJam.setDisable(false);
        pilPaketMenit.getEditor().setText("0");
        pilPaketMenit.setDisable(false);
        pilPaketList.getSelectionModel().clearSelection(0);
        pilPaketListJam.getSelectionModel().select(0);
        pilPaketList.setDisable(false);
        pilPaketListJam.setDisable(false);
        pilButtonStart.setText("Start");
        PilPaketBatal.setText("Batalkan");
        PilPaketBatal.setDisable(true);
    }
    
}
