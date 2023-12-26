package com.rafsan.inventory.controller.billiard;

import com.rafsan.inventory.entity.Tables;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DStationController implements Initializable {
    
    private double xOffset = 0, yOffset = 0;
    
    private BilliardController billiardController;
    
    @FXML
    public Label numMeja, tPaket, tStartMeja, tStartDateMeja, tDurasiMeja, tSisaMeja, tBiayaMeja, tJualMeja, tSalesMeja, tTotMeja;
    
    @FXML
    public ImageView tStatusMeja;
    
    @FXML
    public AnchorPane mainFrame;
    
    public boolean isRunning = false;
    public boolean isShowed = false;
    private int tableIndex = 0;
    private Node tableNode;
    private FXMLLoader Loader;
    private Tables tables;
    
    public void setData() {
        numMeja.setText(tables.getStation().getName());
        tDurasiMeja.setText(tables.getTimer().getDuration());
    }
    
    public void setTables(Tables table) {
        this.tables = table;
    }
    
    public Tables getTables() {
        return tables;
    }
    
    public void setNode(Node node) {
        this.tableNode = node;
    }
    
    public Node getNode() {
        return tableNode;
    }
    
    public void setLoader(FXMLLoader loader) {
        this.Loader = loader;
    }
    
    public FXMLLoader getLoader() {
        return Loader;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setParentController(BilliardController billiardController) {
        this.billiardController = billiardController;
    }
    
    public BilliardController getParentController() {
        return billiardController;
    }
    
    public void setTableIndex(int index) {
        this.tableIndex = index;
    }
    
    public int getTableIndex() {
        return tableIndex;
    }
    
    protected void initStage(boolean isMove) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TPilPaket.fxml"));
        Parent root = loader.load();
        packageController PackageController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        root.setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Start Table");
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        if (isMove) {
//            this.isShowed = true;
            PackageController.nomorMeja.setText(tables.getStation().getName());
            PackageController.setPackages(tables.getStation().getName());
            PackageController.setParent(this);
            PackageController.setTables(tables);
            PackageController.setActiveTransactionModel(billiardController.getActiveTransactionModel());
            PackageController.setInit(tables.getTimer().getIsRunning(), tables.getTimer().getIsFinished(), false);
        } else {
            stage.show();
            tables.getTimer().setPackageControllerStage(stage);
//            this.isShowed = true;
            PackageController.nomorMeja.setText(tables.getStation().getName());
            PackageController.setPackages(tables.getStation().getName());
            PackageController.setParent(this);
            PackageController.setTables(tables);
            PackageController.setActiveTransactionModel(billiardController.getActiveTransactionModel());
            PackageController.setInit(tables.getTimer().getIsRunning(), tables.getTimer().getIsFinished(), false);
        }
    }
    
    public void showPopUp() throws IOException {
        try {
            System.out.println("Dstation status : " + tables.getTimer().getIsRunning());
            System.out.println("[ Detail ] : " + tables.getStation().getName());
            System.out.println("[ Rate count ] : " + tables.getTimer().getRateCount());
            System.out.println("[ Count ] : " + tables.getTimer().getCount());
//            System.out.println("[ Every ] : " + tables.getActivePackage().getRateList().getAvailableRate().getEvery());
            System.out.println("[ Is Finished ] : " + tables.getTimer().getIsFinished());
//            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getActivePackage().getPackageName());
            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getTimer().getPackageController());
            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getTimer().getPackageControllerStage());
            System.out.println("[ isShow ] : " + isShowed);
//            if (isShowed) {
                if (tables.getTimer().getIsRunning() && tables.getTimer().getPackageController() != null) {
                    tables.getTimer().getPackageController().setInit(tables.getTimer().getIsRunning(), tables.getTimer().getIsFinished(), false);
                    tables.getTimer().getPackageControllerStage().show();
                } else {
                    if (tables.getTimer().getIsFinished() && tables.getTimer().getPackageController() != null) {
                        tables.getTimer().getPackageController().setInit(tables.getTimer().getIsRunning(), tables.getTimer().getIsFinished(), false);
                        tables.getTimer().getPackageControllerStage().show();
                    } else {
                        initStage(false);
                    }
                }
//            } else {
//                initStage(false);
//            }
        } catch (Exception e) {
            System.out.println("[ Error ] : " + tables.getStation().getName());
//            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getActivePackage().getPackageName());
            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getTimer().getPackageController());
            System.out.println("[ " + tables.getStation().getName() + " ] : " + tables.getTimer().getPackageControllerStage());
            System.out.println("[ isShow ] : " + isShowed);
            e.printStackTrace();
        }
    }
}
