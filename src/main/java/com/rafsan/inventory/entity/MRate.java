package com.rafsan.inventory.entity;

import com.rafsan.inventory.utility.TimeUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MRate {
    private ObservableList<Rate> ratelist  = FXCollections.observableArrayList();
    
    public MRate() {}
    public MRate(ObservableList<Rate> rates) {
        this.ratelist = rates;
    }
    
    public void setRates(ObservableList<Rate> rates) {
        for (Rate rate : rates) {
            ratelist.add(rate);
        }
    }
    
    public void addRate(Rate rate) {
        ratelist.add(rate);
    }
    
    public Rate getRate(int index) {
        return ratelist.get(index);
    }
    
    public ObservableList<Rate> getRates() {
        return ratelist;
    }
    
    public List<Rate> getAvailableRates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Rate> availableRates = new ArrayList<>();
        
        System.out.println("Ratelist : " + ratelist.size());
        for (Rate rate : ratelist) {
            LocalTime availableStartTime = LocalTime.parse(rate.getMFromTime());
            LocalTime availableEndTime = LocalTime.parse(rate.getMToTime());
            
            // Cek apakah jadwal paket tersedia selama jangka waktu yang diberikan
            if (TimeUtil.isPackageAvailableByTime(startDateTime, endDateTime, availableStartTime, availableEndTime)) {
                System.out.println("Cocok menambahkan");
                System.out.println("MFrom : " + rate.getMFromTime());
                availableRates.add(rate);
            }
//            if (TimeUtil.isPackageAvailableByTime(startDateTime, endDateTime, availableStartTime, availableEndTime)) {
//                availableRates.add(rate);
//            }
        }
        
        return availableRates;
    }
    
    public Rate getAvailableRateByTime(LocalDateTime currentTime) {
        LocalTime MFrom, MTo;
        Rate rate = null;
        
        for (Rate rt : ratelist) {
            MFrom = LocalTime.parse(rt.getMFromTime());
            MTo = LocalTime.parse(rt.getMToTime());
            
            if (TimeUtil.isPackageAvailableTime(currentTime, MFrom, MTo)) {
                rate = rt;
                break;
            }
        }
        return rate;
    }
    
    public Rate getAvailableRate() {
//        boolean status = false;
        LocalTime MFrom, MTo;
        LocalTime currentTime = LocalTime.now();
        Rate rate = null;
        
        for (Rate rt : ratelist) {
//            System.out.println(rt.getMFromTime());
//            System.out.println(rt.getMToTime());
            MFrom = LocalTime.parse(rt.getMFromTime());
            MTo = LocalTime.parse(rt.getMToTime());
            
            if (TimeUtil.isPackageAvailable(MFrom, MTo)) {
                rate = rt;
            }
            
//            if (currentTime.isAfter(MFrom) && currentTime.isBefore(MTo)) {
//                rate = rt;
//            }
        }
        
//        System.out.println("Ada");
//        System.out.println(rate.getRate());
        return rate;
    }
    
    public Rate getAvailableRateByTime(LocalDateTime startDateTime, LocalDateTime currentDateTime) {
        LocalTime MFrom, MTo;
        Rate rate = null;
        
        for (Rate rt : ratelist) {
            MFrom = LocalTime.parse(rt.getMFromTime());
            MTo = LocalTime.parse(rt.getMToTime());
            
            if (TimeUtil.isPackageAvailableByTime(startDateTime, currentDateTime, MFrom, MTo)) {
                rate = rt;
                break; // jika sudah ditemukan, keluar dari loop
            }
        }
        
        return rate;
    }
}
