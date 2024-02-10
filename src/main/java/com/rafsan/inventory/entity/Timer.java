package com.rafsan.inventory.entity;

import com.rafsan.inventory.controller.billiard.DStationController;
import com.rafsan.inventory.controller.billiard.packageController;
import com.rafsan.inventory.core;
import static com.rafsan.inventory.interfaces.StationInterface.STATIONLIST;
import com.rafsan.inventory.model.ActiveTransactionModel;
import com.rafsan.inventory.model.Calculation;
import com.rafsan.inventory.model.StationModel;
import com.rafsan.inventory.model.TablePackageModel;
import com.rafsan.inventory.model.TableTransactionModel;
import com.rafsan.inventory.model.TransactionModel;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Timer {

    private Timeline timeline;
    private int count = 0, ratecount = 0;
    private int target = 0;
    private boolean isFinished = false;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private DStationController dstationController;
    private packageController PackageController;
    private ActiveTransactionModel activeTransactionModel;
    private ActivePackage activePackage;
    private static final StationModel stationModel = new StationModel();
    private boolean perhitunganSekarang = false, rateberbeda = false, waiting = false, terganti = false;

    private Tables parent;
    protected double totalamount = 0;
    public int rateCount = 0;
    private LocalDateTime lastSingleDate = LocalDateTime.now();

    private Stage packageControllerStage;

    private int alt = 0;
    private int kedip = 0;
    private int shours, sminutes, sseconds; // mulai
    private int ehours, eminutes, eseconds; // sisa
    private boolean alerted = false;

    private int dhours, dminutes, dseconds; // durasi
    private static final TableTransactionModel tableTransactionModel = new TableTransactionModel();
    private static final TransactionModel transactionModel = new TransactionModel();

    public Rate rate, ratesebelumnya, rateselanjutnya, rateawal;

    public void setLastSingleDate(LocalDateTime date) {
        this.lastSingleDate = date;
    }

    public LocalDateTime getLastSingleDate() {
        return lastSingleDate;
    }

    public Timer() {
    }

    public Timer(int target) {
        this.target = target;
    }

    public void setRateSebelumnya(Rate rate) {
        this.ratesebelumnya = rate;
    }

    public Rate getRateSebelumnya() {
        return ratesebelumnya;
    }

    public void setRateAwal(Rate rate) {
        this.rateawal = rate;
    }

    public Rate getRateAwal() {
        return rateawal;
    }

    public void setRateSelanjutnya(Rate rate) {
        this.rateselanjutnya = rate;
    }

    public Rate getRateSelanjutnya() {
        return rateselanjutnya;
    }

    public void setTotalAmount(double amount) {
        this.totalamount = amount;
    }

    public void IncrementTotalAmount(double amount) {
        this.totalamount += amount;
    }

    public void DecrementTotalAmount(double amount) {
        this.totalamount -= amount;
    }

    public double getTotalAmount() {
        return totalamount;
    }

    public void setActiveTransactionModel(ActiveTransactionModel model) {
        this.activeTransactionModel = model;
    }

    public ActiveTransactionModel getActiveTransactionModel() {
        return activeTransactionModel;
    }

    public void setParent(Tables parent) {
        this.parent = parent;
    }

    public Tables getParent() {
        return parent;
    }

    public void setStationController(DStationController StationController) {
        this.dstationController = StationController;
    }

    public void setPackageController(packageController PackageController) {
        this.PackageController = PackageController;
    }

    public packageController getPackageController() {
        return PackageController;
    }

    public void setPackageControllerStage(Stage packageControllerStage) {
        this.packageControllerStage = packageControllerStage;
    }

    public Stage getPackageControllerStage() {
        return packageControllerStage;
    }

    public void checkTime() {
        if (sseconds >= 60) {
            sminutes++;
            sseconds = 0;
//            System.out.println(getMulai());
            parent.tableTransaction.setDuration(getMulai());
            tableTransactionModel.updateTableTransaction(parent.tableTransaction);
//            activeTransactionModel.update(parent);

            if (sminutes >= 60 || sminutes == 60) {
                shours++;
                sminutes = 0;
                parent.tableTransaction.setDuration(getMulai());
                tableTransactionModel.updateTableTransaction(parent.tableTransaction);
            }
        }

        if (eseconds < 0) {
            eminutes--;
            eseconds = 59;

            if (eminutes < 0) {
                ehours--;
                eminutes = 59;

                if (ehours < 0) {
                    ehours = 0;
                }
            }
        }
    }

    public void setDurationSisa(String duration, String sisa) {
        dhours = Integer.valueOf(duration.split(":")[0]);
        dminutes = Integer.valueOf(duration.split(":")[1]);
        dseconds = Integer.valueOf(duration.split(":")[2]);

        ehours = Integer.valueOf(sisa.split(":")[0]);
        eminutes = Integer.valueOf(sisa.split(":")[1]);
        eseconds = Integer.valueOf(sisa.split(":")[2]);
//        System.out.println("Set sisa duration");
//        System.out.println(duration);
//        System.out.println(sisa);
    }

    public void setDuration(String duration) {
        dhours = Integer.valueOf(duration.split(":")[0]);
        dminutes = Integer.valueOf(duration.split(":")[1]);
        dseconds = Integer.valueOf(duration.split(":")[2]);

        ehours = Integer.valueOf(duration.split(":")[0]);
        eminutes = Integer.valueOf(duration.split(":")[1]);
        eseconds = Integer.valueOf(duration.split(":")[2]);
    }

    public void setMulai(String duration) {
        shours = Integer.valueOf(duration.split(":")[0]);
        sminutes = Integer.valueOf(duration.split(":")[1]);
        sseconds = Integer.valueOf(duration.split(":")[2]);
    }

    public String getMulai() {
        return String.format("%02d", shours) + ":" + String.format("%02d", sminutes) + ":" + String.format("%02d", sseconds);
    }

    public String getSisa() {
        if (target <= 0) {
            return String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0);
        } else {
            return String.format("%02d", ehours) + ":" + String.format("%02d", eminutes) + ":" + String.format("%02d", eseconds);
        }
    }

    public String getDuration() {
        return String.format("%02d", dhours) + ":" + String.format("%02d", dminutes) + ":" + String.format("%02d", dseconds);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        System.out.println("set timer count : " + count);
        this.count = count;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public int getRateCount() {
        return ratecount;
    }

    public void setRateCount(int count) {
        System.out.println("set rate count : " + count);
        this.ratecount = count;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void checkRate(Rate rate) {
        if (!isFinished) {
            int pembulatan = Integer.valueOf(parent.getTableTransaction().getTablePackage().getPembulatan());
            if (ratecount > pembulatan) {
                if (!terganti) {
//                    java.time.Duration duration = java.time.Duration.ofSeconds(rate.getEvery());
//                    java.time.Duration evr = java.time.Duration.ofSeconds(rate.getEvery());
//                    double totalmenit = duration.toMinutes();
//                    double waktuBulat = Math.ceil(totalmenit / evr.toMinutes()) * evr.toMinutes();
//                    double waktu = waktuBulat;
//                    double tarif = rate.getRate();
//                    double jumlahBlok = waktu / evr.toMinutes();
//                    double biaya = jumlahBlok * tarif;
//                    totalamount += biaya;
//                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                    System.out.println("[ " + formatter.format(new Date()) + " ] " + parent.station.getName() + " : " + biaya);
//                    System.out.println("[ " + formatter.format(new Date()) + " ] " + parent.station.getName() + " : " + totalamount);
//
//                    parent.accumulate(false, rateawal.getMinRate());
//                    parent.accumulate(false, rate.getMinRate());
//
//                    parent.tableTransaction.setAmount(String.valueOf(totalamount));
//                    tableTransactionModel.updateTableTransaction(parent.tableTransaction);
//
//                    parent.transaction.setTotal(String.valueOf(parent.total));
//
//                    new TransactionModel().updateTransaction(parent.transaction);
//                    transactionModel.updateTransaction(parent.transaction);
                    terganti = true;
                }
                if (ratecount >= rate.getEvery()) {
                    ratecount = 0;
                    terganti = false;
                }
                perhitunganSekarang = false;
            }
        } else if (isFinished) {
//            System.out.println(count);
//            System.out.println(alt);
//            System.out.println("Last rate : " + totalamount);
//            System.out.println(rateawal.getMinRate());
//            if (rateawal.getMinRate() != 0 && totalamount <= rateawal.getMinRate()) {
//                System.out.println("Minimal rate : " + rateawal.getMinRate());
//                this.totalamount = rateawal.getMinRate();
//                parent.accumulate(false, rateawal.getMinRate());
//                parent.tableTransaction.setAmount(String.valueOf(this.totalamount));
//                tableTransactionModel.updateTableTransaction(parent.tableTransaction);
//
//                parent.transaction.setTotal(String.valueOf(parent.total));
//
//                transactionModel.updateTransaction(parent.transaction);
//            } else {
//                if (rateCount <= 1) {
//                    System.out.println("Sebelum " + this.totalamount);
//                    this.totalamount = (int) (Math.round(totalamount / 1000.0) * 1000);
//                    System.out.println("Sesudah " + this.totalamount);
//                }
//                this.totalamount = Math.round(this.totalamount);
//                parent.accumulate(false, rateawal.getMinRate());
//                System.out.println(this.totalamount);
//                parent.tableTransaction.setAmount(String.valueOf(this.totalamount));
//                tableTransactionModel.updateTableTransaction(parent.tableTransaction);
//
//                parent.transaction.setTotal(String.valueOf(parent.total));
//
//                transactionModel.updateTransaction(parent.transaction);
//            }
//            activeTransactionModel.update(parent);
        }

        final Calculation result = new Calculation(parent.getTableTransaction(), Calendar.getInstance().getTime());

        // TODO: uncomment this line below to update only when last total amount != result total tarif
        // if (this.totalamount == result.getTotalTarif()) return;
        this.totalamount = result.getTotalTarif();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.println("[ " + formatter.format(new Date()) + " ] " + parent.station.getName());
        System.out.println(result.toString());

        parent.accumulate(false, rateawal.getMinRate());
        parent.tableTransaction.setAmount(String.valueOf(totalamount));

        tableTransactionModel.updateTableTransaction(parent.tableTransaction);
        parent.transaction.setTotal(String.valueOf(parent.total));

        transactionModel.updateTransaction(parent.transaction);
    }

    public void mainFunction() {
        try {
            isFinished = false;
            rate = parent.getActivePackage().getRateList().getAvailableRate();
            parent.getActivePackage().isSingle();
            if (rate != null) {
                count++;
                ratecount++;
                sseconds++;
                eseconds--;
                if (ratesebelumnya != rate) {
                    rateberbeda = true;
                    rateselanjutnya = rate;
                }
                checkTime();
                if (rateberbeda) {
                    if (rateawal.getMinRate() != 0) {
                        if (totalamount >= rateawal.getMinRate()) {
                            if (ratecount < ratesebelumnya.getEvery()) {
                                checkRate(ratesebelumnya);
                            } else if (ratecount >= ratesebelumnya.getEvery()) {
                                checkRate(ratesebelumnya);
                                parent.accumulate(false, rateawal.getMinRate());
                                //                            parent.accumulate(false, rateselanjutnya.getMinRate());

                                parent.tableTransaction.setAmount(String.valueOf(totalamount));
                                tableTransactionModel.updateTableTransaction(parent.tableTransaction);

                                parent.transaction.setTotal(String.valueOf(parent.total));

                                transactionModel.updateTransaction(parent.transaction);
                                ratesebelumnya = rateselanjutnya;
                                rateberbeda = false;
                            }
                        } else {
                            if (ratecount < ratesebelumnya.getEvery()) {
                                checkRate(ratesebelumnya);
                            } else if (ratecount >= ratesebelumnya.getEvery()) {
                                checkRate(ratesebelumnya);
                                parent.accumulate(false, rateawal.getMinRate());
                                //                            parent.accumulate(false, rateselanjutnya.getMinRate());

                                parent.tableTransaction.setAmount(String.valueOf(totalamount));
                                tableTransactionModel.updateTableTransaction(parent.tableTransaction);

                                parent.transaction.setTotal(String.valueOf(parent.total));

                                new TransactionModel().updateTransaction(parent.transaction);
                                ratesebelumnya = rateselanjutnya;
                                rateberbeda = false;
                            }
                        }
                    } else if (rateawal.getMinRate() == 0) {
                        if (ratecount < ratesebelumnya.getEvery()) {
                            checkRate(ratesebelumnya);
                        } else if (ratecount >= ratesebelumnya.getEvery()) {
                            checkRate(ratesebelumnya);
                            parent.accumulate(false, rateawal.getMinRate());
                            //                        parent.accumulate(false, rateselanjutnya.getMinRate());

                            parent.tableTransaction.setAmount(String.valueOf(totalamount));
                            tableTransactionModel.updateTableTransaction(parent.tableTransaction);

                            parent.transaction.setTotal(String.valueOf(parent.total));

                            new TransactionModel().updateTransaction(parent.transaction);
                            ratesebelumnya = rateselanjutnya;
                            rateberbeda = false;
                        }
                    }
                } else {
                    checkRate(ratesebelumnya);
                }
                dstationController.tSisaMeja.setText(getSisa());
                dstationController.tStartMeja.setText(getMulai());
            } else if (parent.getActivePackage().isSingle() && rate == null) {
                stopTimer(false, false);
                PackageController.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartTimer() {
        System.out.println("Stopping timer");
        timeline.stop();
        System.out.println("current cycle " + timeline.getCycleCount());
        if (target <= 0) {
            System.out.println("set cycle count to indefinite");
            timeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            System.out.println("set cycle count to " + target);
            timeline.setCycleCount(target);
        }
        System.out.println("current cycle " + timeline.getCycleCount());
        System.out.println("Starting timer");
        timeline.play();
    }

    public void startTimer(boolean isFirstLoad, boolean lewat) throws InterruptedException, IOException {
        this.isRunning = true;
        parent.station.setStatus("Active");
        stationModel.updateStation(parent.station);
        int index = 0;
        for (Station station : STATIONLIST) {
            if (station.getId() == parent.station.getId()) {
                STATIONLIST.set(index, parent.station);
            }
            index++;
        }
        if (lewat) {
            System.out.println("Minimal rate : " + parent.getActivePackage().getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1)).getMinRate());
            System.out.println("Current rate : " + parent.getActivePackage().getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1)).getRate());
            ratesebelumnya = parent.getActivePackage().getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1));
            rateawal = parent.getActivePackage().getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1));
        } else {
            System.out.println("Minimal rate : " + parent.getActivePackage().getRateList().getAvailableRate().getMinRate());
            System.out.println("Current rate : " + parent.getActivePackage().getRateList().getAvailableRate().getRate());
            ratesebelumnya = parent.getActivePackage().getRateList().getAvailableRate();
            rateawal = parent.getActivePackage().getRateList().getAvailableRate();
        }
        parent.accumulate(false, rateawal.getMinRate());
//        parent.accumulate(false, ratesebelumnya.getMinRate());
        perhitunganSekarang = true;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            mainFunction();
        }));
        if (target <= 0) {
            timeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            timeline.setCycleCount(target);
        }
        timeline.setOnFinished(e -> {
            try {
                stopTimer(false, false);
            } catch (InterruptedException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            }
            PackageController.stop();
            System.out.println("Selesai");
        });
        timeline.play();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        if (parent.getTableTransaction().getStart() != null) {
            String res = formatter.format(parent.getTableTransaction().getStart());
            dstationController.tStartDateMeja.setText(res);
            System.out.println("set start meja : " + res);
        } else {
            String res = formatter.format(parent.getTransaction().getTransdate());
            dstationController.tStartDateMeja.setText(res);
            System.out.println("set start meja : " + res);
        }
        core.tableStart++;
    }

    public void cancel() throws InterruptedException, IOException {
        core.tableStart--;
        isRunning = false;
        alerted = false;
        parent.station.setStatus("Idle");
        stationModel.updateStation(parent.station);
        int index = 0;
        for (Station station : STATIONLIST) {
            if (station.getId() == parent.station.getId()) {
                STATIONLIST.set(index, parent.station);
            }
            index++;
        }

        PackageController.setStatus(isRunning);
        dstationController.tStatusMeja.setImage(new Image("/images/stop.png"));
        dstationController.mainFrame.setStyle("-fx-background-color: #7F7F7F; -fx-background-radius: 10px;");
        parent.getTableTransaction().setStop(new Date());
        parent.getTableTransaction().setStatus((short) 1);
        tableTransactionModel.updateTableTransaction(parent.getTableTransaction());
        timeline.stop();
        timeline = null;

        count = 0;
        ratecount = 0;
        target = 0;
        isFinished = false;
        isRunning = false;
        activePackage = null;
        totalamount = 0;
        shours = 0;
        sminutes = 0;
        sseconds = 0;
        ehours = 0;
        eminutes = 0;
        eseconds = 0;
        dhours = 0;
        dminutes = 0;
        dseconds = 0;
        rate = null;

        parent.total = 0;
        parent.totalsale = 0;
        System.out.println("Timer canceled");
    }

    public void pauseTimer() throws InterruptedException, IOException {
        System.out.println("Is Finished " + isFinished);
        isFinished = true;
        isRunning = false;
        isPaused = true;
        alerted = false;
        core.tableStart--;
        dstationController.tStatusMeja.setImage(new Image("/images/stop.png"));
        dstationController.mainFrame.setStyle("-fx-background-color: #DA281C; -fx-background-radius: 10px;"); // red
        PackageController.setInit(isRunning, isFinished, isPaused);
        parent.station.setStatus("Idle");
        stationModel.updateStation(parent.station);
        int index = 0;
        for (Station station : STATIONLIST) {
            if (station.getId() == parent.station.getId()) {
                STATIONLIST.set(index, parent.station);
            }
            index++;
        }
        timeline.pause();

        System.out.println("Timer paused");
    }

    public void stopTimer(boolean lebih, boolean lewat) throws InterruptedException, IOException {
        System.out.println("Is Finished " + isFinished);
        isFinished = true;
        isRunning = false;
        alerted = false;
        core.tableStart--;
        if (lewat) {
            rate = parent.getActivePackage().getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1));
            checkRate(rate);
        } else {
            rate = parent.getActivePackage().getRateList().getAvailableRate();
            checkRate(rate);
        }
        dstationController.tStatusMeja.setImage(new Image("/images/stop.png"));
        dstationController.mainFrame.setStyle("-fx-background-color: #7F7F7F; -fx-background-radius: 10px;"); // default
        PackageController.setInit(isRunning, isFinished, false);
        if (lewat) {
            Instant instant = lastSingleDate.atZone(java.time.ZoneId.systemDefault()).toInstant();
            parent.getTableTransaction().setStop(Date.from(instant));
        } else {
            parent.getTableTransaction().setStop(new Date());
        }
        parent.getTableTransaction().setStatus((short) 1);
        if (!lebih) {
            parent.getTableTransaction().setDuration(getMulai());
        }
        parent.station.setStatus("Idle");
        stationModel.updateStation(parent.station);
        int index = 0;
        for (Station station : STATIONLIST) {
            if (station.getId() == parent.station.getId()) {
                STATIONLIST.set(index, parent.station);
            }
            index++;
        }
        timeline.stop();
        timeline = null;
        tableTransactionModel.updateTableTransaction(parent.getTableTransaction());
        transactionModel.updateTransaction(parent.getTransaction());
        activeTransactionModel.add(parent);
    }
}
