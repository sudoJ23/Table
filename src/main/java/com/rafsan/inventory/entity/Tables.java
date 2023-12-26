package com.rafsan.inventory.entity;

import com.rafsan.inventory.controller.billiard.DStationController;
import com.rafsan.inventory.model.TableTransactionModel;
import com.rafsan.inventory.model.TransactionModel;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class Tables {
    
    protected TableTransaction tableTransaction;
    protected Transaction transaction;
//    protected Sale sales;
    
    private TableTransactionModel tableTransactionModel = new TableTransactionModel();
    private TransactionModel transactionModel = new TransactionModel();
//    private SalesModel salesModel = new SalesModel();
    
    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    
    private Date transStart;
    private Date transStop;
    
    protected Station station;
    private Timer timer;
    private DStationController dstationController;
    
    private int column = 0;
    private int row = 0;
    
    private Node tableNode;
    private FXMLLoader loader;
    protected double total = 0, totalsale = 0, totaldummy = 0; // total = sale + table
    public double another;
    
    private ActivePackage activePackage;
    
    private double discount = 0; //diskon manual
    
    private double persentaseDiskon = 0;//persentase diskon manual
    
    public void setDiscountPercentage(double value) {
        this.persentaseDiskon = value;
    }
    
    public double getDiscountPercentage() {
        return persentaseDiskon;
    }
    
    public void setDiscount(double value) {
        this.discount = value;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    public void setTableTransaction(TableTransaction tableTransaction) {
        this.tableTransaction = tableTransaction;
    }
    
    public TableTransaction getTableTransaction() {
        return tableTransaction;
    }
    
//    public void setSale(Sale sales) {
//        this.sales = sales;
//    }
//    
//    public Sale getSale() {
//        return sales;
//    }
    
    public double getTotalSale() {
        return totalsale;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void resetTotal() {
        this.total = 0;
    }
    
    public void resetTotalSale() {
        this.totalsale = 0;
    }
    
    public void incrementTotalSale(double value) {
        this.totalsale += value;
    }
    
    public void incrementTotal(double value) {
        this.total += value;
    }
    
    public void decrementTotalSale(double value) {
        this.totalsale -= value;
    }
    
    public void decrementTotal(double value) {
        this.total -= value;
    }
    
    public void setTransStart(Date date) {
        this.transStart = date;
    }
    
    public Date getTransStart() {
        return transStart;
    }
    
    public void setTransStop(Date date) {
        this.transStop = date;
    }
    
    public Date getTransStop() {
        return transStop;
    }
    
//    Package
    public void setActivePackage(ActivePackage activePackage) {
        this.activePackage = activePackage;
    }
    
    public ActivePackage getActivePackage() {
        return activePackage;
    }
    
    public void accumulateOnly(boolean loader, boolean noTable) {
        if (loader) {
            if (noTable) {
                total = totalsale;
            } else {
                total = (Double.parseDouble(tableTransaction.getAmount()) + totalsale);
            }
        } else {
            total = (timer.totalamount + totalsale);
            if (timer.rateawal.getMinRate() != 0 && timer.totalamount <= timer.rateawal.getMinRate()) {
                totaldummy = (timer.rateawal.getMinRate() + totalsale);
                dstationController.tTotMeja.setText(format.format(totaldummy).replace("Rp", ""));
                dstationController.tJualMeja.setText(format.format(getTotalSale()).replace("Rp", ""));
            } else {
                dstationController.tTotMeja.setText(format.format(total).replace("Rp", ""));
                dstationController.tJualMeja.setText(format.format(getTotalSale()).replace("Rp", ""));
            }
        }
    }
    
//    public void accumulateOnly(boolean loader, boolean noTable) {
//        if (loader) {
//            if (noTable) {
//                total = totalsale;
//            } else {
//                total = Double.parseDouble(tableTransaction.getAmount()) + totalsale;
//                if (timer.rateawal.getMinRate() != 0 && timer.totalamount <= timer.rateawal.getMinRate()) {
//                    totaldummy = (timer.getRateAwal().getMinRate() + totalsale);
//                    System.out.println("Dibawah minrate : " + totaldummy);
//                    dstationController.tTotMeja.setText(format.format(totaldummy).replace("Rp", ""));
//                    dstationController.tJualMeja.setText(format.format(getTotalSale()).replace("Rp", ""));
//                } else {
//                    System.out.println("Diatas minrate");
//                    dstationController.tTotMeja.setText(format.format(total).replace("Rp", ""));
//                    dstationController.tJualMeja.setText(format.format(getTotalSale()).replace("Rp", ""));
//                }
//            }
//        } else {
//            total = timer.totalamount + totalsale;
//        }
//    }
    
    public void accumulate(boolean noTable, double minrate) {
        total = timer.totalamount + totalsale;
        if (!noTable) {
            if (minrate != 0 && timer.totalamount <= minrate) {
                totaldummy = minrate + totalsale;
                dstationController.tBiayaMeja.setText(format.format(minrate).replace("Rp", ""));
            } else {
                dstationController.tBiayaMeja.setText(format.format(timer.totalamount).replace("Rp", ""));
                totaldummy = total;
            }
            dstationController.tTotMeja.setText(format.format(totaldummy).replace("Rp", ""));
            dstationController.tJualMeja.setText(format.format(totalsale).replace("Rp", ""));
//            tableTransaction.setAmount(String.valueOf(timer.totalamount));
//            tableTransactionModel.updateTableTransaction(tableTransaction);
            
//            Double temp = (Double.parseDouble(transaction.getTotal()) - timer.totalamount );
//            temp += (timer.totalamount);
//            transaction.setTotal(String.valueOf(temp));
//            temp = Double.parseDouble(transaction.getPaid());
//            temp += (timer.totalamount);
//            transaction.setPaid(String.valueOf(temp));
//            transactionModel.updateTransaction(transaction);
        }
    }
    
    public void accumulateAll() {
        total = timer.totalamount + totalsale;
        transactionModel.updateTransaction(transaction);
    }
    
    public void setNode(Node node) {
        this.tableNode = node;
    }
    
    public Node getNode() {
        return tableNode;
    }
    
    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }
    
    public FXMLLoader getLoader() {
        return loader;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public Tables() {
        format.setMaximumFractionDigits(0);        
//        sales = new Sale();
    }
    
    public Tables(Station station, Timer timer) {
        this.station = station;
        this.timer = timer;
//        sales = new Sale();
        format.setMaximumFractionDigits(0);
    }
    
    public Tables(Station station) {
        this.station = station;
        format.setMaximumFractionDigits(0);
    }
    
    public void setStationController(DStationController StationController) {
        this.dstationController = StationController;
    }
    
    public DStationController getStationController() {
        return dstationController;
    }
    
    public void setStation(Station station) {
        this.station = station;
    }
    
    public void setTimerParent(DStationController stationController) {
        timer.setStationController(stationController);
    }
    
    public void startTimer(boolean isFirstLoad) throws InterruptedException, IOException {
        timer.startTimer(isFirstLoad, false);
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public Station getStation() {
        return station;
    }
    
    public Timer getTimer() {
        return timer;
    }
}
