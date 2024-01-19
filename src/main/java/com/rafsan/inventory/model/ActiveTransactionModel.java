package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.controller.billiard.BilliardController;
import com.rafsan.inventory.controller.billiard.packageController;
import com.rafsan.inventory.entity.ActivePackage;
import com.rafsan.inventory.entity.Rate;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.entity.TableTransaction;
import com.rafsan.inventory.entity.Tables;
import com.rafsan.inventory.entity.Timer;
import static com.rafsan.inventory.interfaces.ActiveTransaction.ACTIVETRANS;
import static com.rafsan.inventory.interfaces.TablesInterface.TABLELIST;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.Session;

public class ActiveTransactionModel {

    double xOffset = 0, yOffset = 0;
    public int lastrestock = 1;

    @FXML
    protected TableView<Tables> transList;

    private BilliardController parent;
    private Session session;

    public boolean isLoadTableFinish = false;

    public void setTransList(TableView<Tables> transList) {
        this.transList = transList;
    }

    public TableView<Tables> getTransList() {
        return transList;
    }

    public void update(Tables table) {
        try {
            int index = 0;
            for (Tables tbl : ACTIVETRANS) {
                if (tbl.getTransaction().getId().equals(table.getTransaction().getId())) {
                    ACTIVETRANS.set(index, table);
                }
                index++;
            }
        } catch (Exception p) {
            p.printStackTrace();
        }
    }

    public void add(Tables table) {
        System.out.println("Add new transaction to table");
        ACTIVETRANS.add(table);
    }

    public void remove(Tables table) {
        int index = 0;
        try {
            for (Tables tbl : ACTIVETRANS) {
                if (tbl.getTransaction().getId().equals(table.getTransaction().getId())) {
                    ACTIVETRANS.remove(table);
                }
                index++;
            }
        } catch (Exception e) {
            System.out.println("Abaikan");
        }
    }

    public void setParent(BilliardController parent) {
        this.parent = parent;
    }

    public BilliardController getParent() {
        return parent;
    }

    private static double[] hitungTarif(int waktu, double tarif, int every) {
        every = every / 60; // convert detik ke menit
        int jumlahBlok = waktu / every;
        double totalTarif = jumlahBlok * tarif;
        int sisaWaktu = (waktu - jumlahBlok * every);// * 60;
        // if (sisaWaktu > 0) {
        // totalTarif += (tarif * 1);
        // }
        // System.out.println("Every : " + every);
        // System.out.println("Tarif : " + tarif);
        // System.out.println("Waktu : " + waktu);
        double[] hasil = {totalTarif, sisaWaktu};
        return hasil;
    }

    public double[] hitungLastRate(double rate, double minute, int every) {
        double[] hasil = hitungTarif(Integer.parseInt(String.valueOf(minute).replace(".0", "")), rate, every);
        System.out.println(hasil[0] + " " + hasil[1]);
        return hasil;
    }

    public void getTable() throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        isLoadTableFinish = false;
        session = HibernateUtil.getSessionFactory().openSession();
        List<TableTransaction> tableTransactions = new ArrayList<>();

        try {
            session.beginTransaction();
            tableTransactions = session.createQuery("from TableTransaction").list();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }

        for (TableTransaction tableTransaction : tableTransactions) {
            if (tableTransaction.getStatus() == (short) 0) {
                if (tableTransaction.getPowerFailure() == (short) 0) {
                    Rate ratesebelumnya = null;
                    Rate ratesekarang = null;
                    Rate rateselanjutnya = new Rate();
                    Rate rateperpindahan = null;
                    Rate rateawal = null;
                    int rateCount = 1;
                    int jumlahRate = 0;
                    int totalDurasiSekarang = 0;

                    Instant instant = tableTransaction.getStart().toInstant();
                    LocalDateTime dateTime1 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // Waktu
                    // meja
                    // start

                    LocalDateTime dateTime2 = LocalDateTime.now(); // Waktu sekarang
                    LocalDateTime lastSingleDate = LocalDateTime.now();
                    Duration duration = Duration.between(dateTime1, dateTime2);
                    Duration durationInBetween = Duration.between(dateTime1, dateTime2);
                    long seconds = duration.getSeconds();
                    long target = Long.parseLong(tableTransaction.getTarget());
                    boolean masih = false;
                    long sisa = 0;
                    double lastRate = 0;
                    int ratecount = 0;
                    int sisawaktu = 0;
                    int totalratecount = 0;
                    double totalkeseluruhan = 0;
                    boolean lewat = false;

                    TablePackage tablePackage = tableTransaction.getTablePackage();
                    ActivePackage activeP = new ActivePackageModel().setActivePackage(tablePackage);

                    if (!activeP.isSingle()) {
                        LocalDateTime currentDateTime = dateTime1;
                        LocalDateTime lastDate = dateTime1;

                        while (currentDateTime.isBefore(dateTime2) || currentDateTime.equals(dateTime2)) {
                            LocalTime currentTime = currentDateTime.toLocalTime();
                            for (int i = 0; i < activeP.getRateList().getRates().size(); i++) {
                                if (currentTime
                                        .isAfter(LocalTime.parse(activeP.getRateList().getRate(i).getMFromTime()))
                                        && currentTime.isBefore(
                                                LocalTime.parse(activeP.getRateList().getRate(i).getMToTime()))) {
                                    if (rateawal == null) {
                                        rateawal = activeP.getRateList().getRates().get(i);
                                        System.out.println(
                                                "Rate awal adalah " + activeP.getRateList().getRate(i).getRate());
                                    }

                                    if (ratesebelumnya == null) {
                                        ratesebelumnya = rateawal;
                                        jumlahRate += 1;
                                    }

                                    ratesekarang = activeP.getRateList().getRate(i);
                                    if (ratesekarang != ratesebelumnya) {
                                        double[] hasil = hitungTarif(totalDurasiSekarang, ratesebelumnya.getRate(), ratesebelumnya.getEvery());
                                        int rounding = (int) (hasil[1] * 60); // mengubah satuan menit ke detik
                                        int remainingTime = 0;
                                        int tmp = totalDurasiSekarang;
                                        System.out.println("Sisa Rate count : " + rounding);
                                        System.out.println("Total durasi sekarang : " + totalDurasiSekarang);
                                        totalDurasiSekarang = 0;

                                        TablePackage tblPkg = new TablePackageModel()
                                                .getTablePackage(activeP.getPackageId());
                                        int pembulatan = Integer.valueOf(tblPkg.getPembulatan());
                                        totalratecount += ((tmp * 60) + pembulatan);
                                        if (rounding > 0) {
                                            if (pembulatan > 0) {
                                                if (rounding > pembulatan) {
                                                    hasil[0] += ratesebelumnya.getRate();
                                                    rateperpindahan = ratesebelumnya;
                                                    if (rounding <= ratesebelumnya.getEvery()) {
                                                        System.out.println("Sisa waktu sebelum perpindahan kurang dari every");
                                                        sisawaktu = rounding;
                                                        int temp = ((ratesebelumnya.getEvery() / 60) + (pembulatan / 60));
                                                        lastDate = currentDateTime;
                                                        currentDateTime = currentDateTime.plusMinutes(temp);
                                                        System.out.println("total pembulatan :" + pembulatan);
                                                        System.out.println(ratesebelumnya.getEvery());
                                                    }
                                                } else {
                                                    remainingTime = (rounding / 60);
                                                }
                                            }
                                        }
                                        System.out.println("Sisa waktu : " + hasil[1] + " menit");
                                        totalkeseluruhan += hasil[0];

                                        if (remainingTime > 0) {
                                            totalDurasiSekarang = remainingTime;
                                        }
                                        jumlahRate += 1;
                                        ratesebelumnya = ratesekarang;
                                        System.out.println("Terjadi perpindahan rate, total dari rate sebelumnya : " + format.format(totalkeseluruhan).replace("Rp", ""));
                                        Duration durasiSebelumnya = Duration.between(lastDate, currentDateTime);
                                        System.out.println("Total durasi rate sebelumnya : " + String.format("%02d:%02d:%02d", durasiSebelumnya.toHours(), durasiSebelumnya.toMinutes() % 60, durasiSebelumnya.getSeconds() % 60));
                                        lastDate = currentDateTime;
                                        DateTimeFormatter jamFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                                        int FromMenit = Integer.parseInt(ratesekarang.getMFromTime().split(":")[1]);
                                        int LastMenit = Integer.parseInt(jamFormat.format(lastDate).split(":")[1]);
                                        if (LastMenit > FromMenit) {
                                            lastDate = lastDate.minusMinutes(1);
                                            currentDateTime = currentDateTime.minusMinutes(1);
                                        }
                                    }

                                    String formattedDateTime = currentDateTime.format(formatter);
                                    totalDurasiSekarang += 1;
                                    System.out.println("[ " + tableTransaction.getStation().getName() + " ]"
                                            + " | [ " + formattedDateTime + " ] " + "paket ke " + i
                                            + " dengan rate " + activeP.getRateList().getRate(i).getRate());
                                }
                            }

                            currentDateTime = currentDateTime.plusMinutes(1);

                            if (currentTime.equals(LocalTime.MAX)
                                    && currentDateTime.toLocalDate().isBefore(dateTime2.toLocalDate())) {
                                currentDateTime = currentDateTime.plusDays(1).with(LocalTime.MIN);
                            }
                        }

                        if (ratesekarang == null) {
                            Rate availableRate = activeP.getRateList().getAvailableRateByTime(LocalDateTime.now());
                            ratesebelumnya = availableRate;
                            ratesekarang = availableRate;
                            rateselanjutnya = availableRate;
                            rateawal = availableRate;
                        }

                        double[] hasil = hitungTarif(totalDurasiSekarang - 1, ratesekarang.getRate(), ratesekarang.getEvery());
                        totalkeseluruhan += hasil[0];
                        int temp = (int) (hasil[1] * 60);
                        System.out.println("temp :" + temp);
                        System.out.println("Total durasi sekarang : " + totalDurasiSekarang);
                        ratecount = (int) (hasil[1] * 60);

                        Duration durasiSebelumnya = Duration.between(lastDate, currentDateTime);
                        System.out.println("Total durasi rate sekarang : "
                                + String.format("%02d:%02d:%02d", durasiSebelumnya.toHours(),
                                        durasiSebelumnya.toMinutes() % 60, durasiSebelumnya.getSeconds() % 60));
                        System.out.println("Total durasi rate dalam menit : " + durasiSebelumnya.toMinutes());
                        System.out.println("Rate awal adalah " + rateawal.getRate());
                        System.out
                                .println("Total biaya adalah " + format.format(totalkeseluruhan).replace("Rp", ""));
                        System.out.println(formatter.format(currentDateTime));

                        tableTransaction.setDuration(String.format("%02d:%02d:%02d", durationInBetween.toHours(),
                                durationInBetween.toMinutes() % 60, durationInBetween.getSeconds() % 60));
                        tableTransaction.setAmount(String.valueOf(totalkeseluruhan));
                        new TableTransactionModel().updateTableTransaction(tableTransaction);
                    } else {
                        LocalDateTime currentDateTime = dateTime1;
                        totalDurasiSekarang = 0;
                        lewat = false;

                        while (currentDateTime.isBefore(dateTime2) || currentDateTime.equals(dateTime2)) {
                            LocalTime currentTime = currentDateTime.toLocalTime();

                            Rate rate = activeP.getRateList().getAvailableRateByTime(currentDateTime);
                            if (rate == null) {
                                lewat = true;
                                break;
                            } else {
                                totalDurasiSekarang += 1;
                            }

                            currentDateTime = currentDateTime.plusMinutes(1);

                            if (currentTime.equals(LocalTime.MAX)
                                    && currentDateTime.toLocalDate().isBefore(dateTime2.toLocalDate())) {
                                currentDateTime = currentDateTime.plusDays(1).with(LocalTime.MIN);
                            }
                        }

                        if (lewat) {
                            lastSingleDate = currentDateTime;
                            Duration temp = Duration.between(dateTime1, currentDateTime);
                            Rate rr = activeP.getRateList().getAvailableRateByTime(currentDateTime.minusMinutes(1));
                            if (rr != null) {
                                System.out.println("Dapat");
                            } else {
                                System.out.println("Ga Dapat");
                            }
                            System.out.println(currentDateTime.getMinute());
                            System.out.println(String.format("%02d:%02d:%02d", temp.toHours(), temp.toMinutes() % 60, temp.getSeconds() % 60));
                            System.out.println(activeP.getPackageName());
                            double rate = activeP.getRateList().getAvailableRateByTime(currentDateTime.minusMinutes(1)).getRate();
                            int every = activeP.getRateList().getAvailableRateByTime(currentDateTime.minusMinutes(1)).getEvery();
                            double[] rates = hitungTarif(totalDurasiSekarang, rate, every);
                            lastRate = rates[0];
                            ratecount = (int) (rates[1] * 60);
                            tableTransaction.setAmount(String.valueOf(lastRate));
                            tableTransaction.setDuration(String.format("%02d:%02d:%02d", temp.toHours(),
                                    temp.toMinutes() % 60, temp.getSeconds() % 60));
                            new TableTransactionModel().updateTableTransaction(tableTransaction);
                        } else {
                            double rate = activeP.getRateList().getAvailableRate().getRate();
                            int every = activeP.getRateList().getAvailableRate().getEvery();
                            double[] rates = hitungLastRate(rate, (double) duration.toMinutes(), every);
                            lastRate = rates[0];
                            ratecount = (int) (rates[1] * 60);
                            tableTransaction.setAmount(String.valueOf(lastRate));
                            tableTransaction.setDuration(String.format("%02d:%02d:%02d",
                                    durationInBetween.toHours(), durationInBetween.toMinutes() % 60,
                                    durationInBetween.getSeconds() % 60));
                            new TableTransactionModel().updateTableTransaction(tableTransaction);
                        }
                    }

                    if (target != 0) {
                        if (target >= seconds) {
                            sisa = ((int) target - (int) seconds);
                            masih = true;
                        }
                    } else {
                        masih = true;
                    }

                    Tables table = new Tables(tableTransaction.getStation());
                    table.setTableTransaction(tableTransaction);
                    table.setTransaction(tableTransaction.getTransaction());

                    int index = 0;
                    for (Tables tb : TABLELIST) {
                        if (tb.getStation().getName().equals(table.getStation().getName())) {
                            ActivePackage activePackage = new ActivePackageModel().setActivePackage(table.getTableTransaction().getTablePackage());
                            Timer timer = new Timer();
                            timer.setParent(table);
                            timer.setActiveTransactionModel(this);
                            timer.getActiveTransactionModel().setParent(parent);
                            timer.IncrementTotalAmount(Double.parseDouble(tableTransaction.getAmount()));

                            table.setStationController(tb.getStationController());
                            table.setNode(tb.getNode());
                            table.setActivePackage(activePackage);
                            table.setTimer(timer);
                            table.getStationController().setTables(table);
                            table.getStationController().setData();

                            Duration durationSisa = Duration.ofSeconds(sisa);
                            String[] idk = String.format("%02d:%02d:%02d", durationSisa.toHours(), durationSisa.toMinutes() % 60, durationSisa.getSeconds() % 60).split(":");
                            int hour = Integer.parseInt(idk[0]);
                            int minute = Integer.parseInt(idk[1]);
                            int second = 0;
                            int totalSeconds = hour * 3600 + minute * 60 + second;
                            durationSisa = Duration.ofSeconds(totalSeconds);
                            String sisaDurasi = String.format("%02d:%02d:%02d", durationSisa.toHours(), durationSisa.toMinutes() % 60, durationSisa.getSeconds() % 60);

                            if (lewat) {
                                Duration temp = Duration.between(dateTime1, lastSingleDate);
                                idk = String.format("%02d:%02d:%02d", temp.toHours(), temp.toMinutes() % 60, temp.getSeconds() % 60).split(":");
                            } else {
                                idk = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60).split(":");
                            }
                            hour = Integer.parseInt(idk[0]);
                            minute = Integer.parseInt(idk[1]);
                            second = 0;
                            totalSeconds = hour * 3600 + minute * 60 + second;
                            duration = Duration.ofSeconds(totalSeconds);
                            String[] temp = tableTransaction.getDuration().split(":");
                            int tempCount = ((Integer.parseInt(temp[0]) * 3600) + (Integer.parseInt(temp[1]) * 60) + Integer.parseInt(temp[2]));
                            if (lewat) {
                                Duration temp2 = Duration.between(dateTime1, lastSingleDate);
                                table.getTimer().setMulai(String.format("%02d:%02d:%02d", temp2.toHours(), temp2.toMinutes() % 60, temp2.getSeconds() % 60));
                            } else {
                                table.getTimer().setMulai(String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60));
                            }
                            Duration targetDuration = Duration.ofSeconds(Long.parseLong(tableTransaction.getTarget()));

                            if (lewat) {
                                Duration temp2 = Duration.between(dateTime1, lastSingleDate);
                                int h = (int) temp2.toHours();
                                int m = (int) temp2.toMinutes() % 60;
                                int s = (int) temp2.getSeconds() % 60;
                                int currentCount = ((h * 3600) + (m * 60) + s);
                                table.getTimer().setCount(currentCount);
                            } else {
                                int h = (int) durationInBetween.toHours();
                                int m = (int) durationInBetween.toMinutes() % 60;
                                int s = (int) durationInBetween.getSeconds() % 60;
                                int currentCount = ((h * 3600) + (m * 60) + s);
                                table.getTimer().setCount(currentCount);
                            }
                            table.getTimer().setRateCount(ratecount);
                            table.getTimer().setDurationSisa(tableTransaction.getDuration(), sisaDurasi);
                            table.getTimer().setStationController(table.getStationController());
                            table.getTimer().setTarget((int) durationSisa.getSeconds());
                            if (lewat) {
                                table.getTimer().setLastSingleDate(lastSingleDate);
                                table.getTimer().startTimer(true, true);
                            } else {
                                table.getTimer().startTimer(true, false);
                            }
                            if (!activeP.isSingle()) {
                                if (rateCount > 1) {
                                    table.getTimer().rateCount = rateCount;
                                    table.getTimer().setRateAwal(rateawal);
                                    table.getTimer().setRateSelanjutnya(rateselanjutnya);
                                    table.getTimer().setRateSebelumnya(ratesebelumnya);
                                }
                            }
                            table.getStationController().tPaket.setText(tableTransaction.getTablePackage().getName());
                            table.getStationController().tStatusMeja.setImage(new Image("/images/start.png"));
                            table.getStationController().mainFrame.setStyle("-fx-background-color: #96EA93; -fx-background-radius: 10px;");
                            table.getStationController().tDurasiMeja.setText(String.format("%02d:%02d:%02d", targetDuration.toHours(), targetDuration.toMinutes() % 60, targetDuration.getSeconds() % 60));
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
                            table.getTimer().setPackageControllerStage(stage);
                            PackageController.nomorMeja.setText(table.getStation().getName());
                            PackageController.setPackages(table.getStation().getName());
                            PackageController.setParent(table.getStationController());
                            PackageController.pilPaketList.setValue(table.getTableTransaction().getTablePackage().getName());
                            PackageController.setTables(table);
                            if (target >= 0 && target < 1) {
                                PackageController.pilPaketListJam.setValue("00:00:00");
                            } else {
                                Duration tempDuration = Duration.ofSeconds(target);
                                PackageController.pilPaketListJam.setValue(String.format("%02d", tempDuration.toHours()) + ":" + String.format("%02d", tempDuration.toMinutes()) + ":" + String.format("%02d", tempDuration.getSeconds()));
                            }
                            PackageController.setInit(table.getTimer().getIsRunning(), table.getTimer().getIsFinished(), false);
                            table.getTimer().setPackageController(PackageController);
                            table.getTableTransaction().setStatus((short) 0);
                            table.getTableTransaction().setPowerFailure((short) 0);
                            if (lewat) {
                                Rate rate = activePackage.getRateList().getAvailableRateByTime(lastSingleDate.minusMinutes(1));
                                table.accumulate(false, rate.getMinRate());
                            } else {
                                Rate rate = activePackage.getRateList().getAvailableRate();
                                table.accumulate(false, rate.getMinRate());
                            }
                            new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                            TABLELIST.set(index, table);
                            if (!masih) {
                                Duration ss = Duration.ofSeconds(Long.parseLong(table.getTableTransaction().getTarget()));
                                table.getTableTransaction().setDuration(String.format("%02d:%02d:%02d", ss.toHours(), ss.toMinutes() % 60, ss.getSeconds() % 60));
                                table.getTimer().stopTimer(true, lewat);
                                new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                                PackageController.stop();
                            } else if (lewat) {
                                Duration ss = Duration.between(dateTime1, lastSingleDate);
                                table.getTableTransaction().setDuration(String.format("%02d:%02d:%02d", ss.toHours(), ss.toMinutes() % 60, ss.getSeconds() % 60));
                                table.getTimer().stopTimer(true, lewat);
                                new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                                PackageController.stop();
                            }
                        }
                        index++;
                    }
                    // ACTIVETRANS.add(table);
                }
            } else {
                System.out.println(tableTransaction.getId());
                Tables table = new Tables(tableTransaction.getStation());
                table.setTableTransaction(tableTransaction);
                table.setTransaction(tableTransaction.getTransaction());
                ACTIVETRANS.add(table);
            }
        }
        isLoadTableFinish = true;
    }

    public void getTableUsingCalculation() throws IOException, InterruptedException {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        isLoadTableFinish = false;
        session = HibernateUtil.getSessionFactory().openSession();
        List<TableTransaction> tableTransactions = new ArrayList<>();

        try {
            session.beginTransaction();
            tableTransactions = session.createQuery("from TableTransaction").list();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }

        for (TableTransaction tableTransaction : tableTransactions) {
            if (tableTransaction.getStatus() != (short) 0) {
                System.out.println(tableTransaction.getId());
                Tables table = new Tables(tableTransaction.getStation());
                table.setTableTransaction(tableTransaction);
                table.setTransaction(tableTransaction.getTransaction());
                ACTIVETRANS.add(table);
                continue;
            }

            if (tableTransaction.getPowerFailure() != (short) 0) {
                continue;
            }

            Calendar cal = Calendar.getInstance();
            Calculation result = new Calculation(tableTransaction, cal.getTime());

            tableTransaction.setAmount(String.valueOf(result.getTotalTarif()));
            tableTransaction.setDuration(result.getDurasiFormatted());
            new TableTransactionModel().updateTableTransaction(tableTransaction);

            Tables table = new Tables(tableTransaction.getStation());
            table.setTableTransaction(tableTransaction);
            table.setTransaction(tableTransaction.getTransaction());

            for (int index = 0; index < TABLELIST.size(); index++) {
                Tables tb = TABLELIST.get(index);
                if (!tb.getStation().getName().equals(table.getStation().getName())) {
                    continue;
                }

                ActivePackage activePackage = table.getTableTransaction().getTablePackage().getActivePackage();
                Timer timer = new Timer();
                timer.setParent(table);
                timer.setActiveTransactionModel(this);
                timer.getActiveTransactionModel().setParent(parent);
                timer.setTotalAmount(Double.parseDouble(tableTransaction.getAmount()));

                table.setStationController(tb.getStationController());
                table.setNode(tb.getNode());
                table.setActivePackage(activePackage);
                table.setTimer(timer);
                table.getStationController().setTables(table);
                table.getStationController().setData();

                Duration durationSisa = Duration.ofSeconds(result.getSisaWaktu());
                String[] idk = result.getSisaWaktuFormatted().split(":");
                int hour = Integer.parseInt(idk[0]);
                int minute = Integer.parseInt(idk[1]);
                int second = 0;
                int totalSeconds = hour * 3600 + minute * 60 + second;
                durationSisa = Duration.ofSeconds(totalSeconds);
                String sisaDurasi = String.format("%02d:%02d:%02d", durationSisa.toHours(), durationSisa.toMinutes() % 60, durationSisa.getSeconds() % 60);

                table.getTimer().setMulai(result.getDurasiFormatted());
                Duration targetDuration = Duration.ofSeconds(Long.parseLong(tableTransaction.getTarget()));

                table.getTimer().setCount((int) result.getDurasi());
//                table.getTimer().setRateCount(ratecount);
                table.getTimer().setDurationSisa(tableTransaction.getDuration(), sisaDurasi);
                table.getTimer().setStationController(table.getStationController());
                table.getTimer().setTarget((int) durationSisa.getSeconds());
                if (result.isLewat()) {
//                    table.getTimer().setLastSingleDate(lastSingleDate);
                    table.getTimer().startTimer(true, true);
                } else {
                    table.getTimer().startTimer(true, false);
                }
//                if (!activePackage.isSingle()) {
//                    if (rateCount > 1) {
//                        table.getTimer().rateCount = rateCount;
//                        table.getTimer().setRateAwal(rateawal);
//                        table.getTimer().setRateSelanjutnya(rateselanjutnya);
//                        table.getTimer().setRateSebelumnya(ratesebelumnya);
//                    }
//                }
                table.getStationController().tPaket.setText(tableTransaction.getTablePackage().getName());
                table.getStationController().tStatusMeja.setImage(new Image("/images/start.png"));
                table.getStationController().mainFrame.setStyle("-fx-background-color: #96EA93; -fx-background-radius: 10px;");
                table.getStationController().tDurasiMeja.setText(String.format("%02d:%02d:%02d", targetDuration.toHours(), targetDuration.toMinutes() % 60, targetDuration.getSeconds() % 60));
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
                table.getTimer().setPackageControllerStage(stage);
                PackageController.nomorMeja.setText(table.getStation().getName());
                PackageController.setPackages(table.getStation().getName());
                PackageController.setParent(table.getStationController());
                PackageController.pilPaketList.setValue(table.getTableTransaction().getTablePackage().getName());
                PackageController.setTables(table);
                int target = (int) targetDuration.getSeconds();
                if (target >= 0 && target < 1) {
                    PackageController.pilPaketListJam.setValue("00:00:00");
                } else {
                    Duration tempDuration = Duration.ofSeconds(target);
                    PackageController.pilPaketListJam.setValue(String.format("%02d", tempDuration.toHours()) + ":" + String.format("%02d", tempDuration.toMinutes()) + ":" + String.format("%02d", tempDuration.getSeconds()));
                }
                PackageController.setInit(table.getTimer().getIsRunning(), table.getTimer().getIsFinished(), false);
                table.getTimer().setPackageController(PackageController);
                table.getTableTransaction().setStatus((short) 0);
                table.getTableTransaction().setPowerFailure((short) 0);
                if (result.isLewat()) {
                    LocalDateTime lastSingleDate = result.getTimeList().get(result.getTimeList().size() - 1);
                    Rate rate = activePackage.getRateList().getAvailableRateByTime(lastSingleDate);
                    table.accumulate(false, rate.getMinRate());
                } else {
                    Rate rate = activePackage.getRateList().getAvailableRate();
                    table.accumulate(false, rate.getMinRate());
                }
                new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                TABLELIST.set(index, table);
                boolean masih = result.getSisaWaktu() > 0;
                if (!masih) {
                    Duration ss = Duration.ofSeconds(Long.parseLong(table.getTableTransaction().getTarget()));
                    table.getTableTransaction().setDuration(String.format("%02d:%02d:%02d", ss.toHours(), ss.toMinutes() % 60, ss.getSeconds() % 60));
                    table.getTimer().stopTimer(true, result.isLewat());
                    new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                    PackageController.stop();
                } else if (result.isLewat()) {
                    Duration ss = Duration.ofSeconds(result.getDurasi());
                    table.getTableTransaction().setDuration(String.format("%02d:%02d:%02d", ss.toHours(), ss.toMinutes() % 60, ss.getSeconds() % 60));
                    table.getTimer().stopTimer(true, result.isLewat());
                    new TableTransactionModel().updateTableTransaction(table.getTableTransaction());
                    PackageController.stop();
                }
            }
        }

        isLoadTableFinish = true;
    }
}
