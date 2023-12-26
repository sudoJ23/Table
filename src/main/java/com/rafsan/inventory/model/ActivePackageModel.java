package com.rafsan.inventory.model;

import com.rafsan.inventory.entity.ActivePackage;
import com.rafsan.inventory.entity.MRate;
import com.rafsan.inventory.entity.Rate;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.utility.TimeConverter;
import com.rafsan.inventory.utility.TimeFormat;
import java.util.List;
import javafx.collections.ObservableList;

public class ActivePackageModel {
    
    private TablePackage tablePackage;
    private TablePackageModel tablePackageModel = new TablePackageModel();
    private MRate mrate;
    private Rate rate;
    private double tableRate, minrate;
    private int every;
    private String MToTime, MFromTime;    
    
    public ActivePackage setActivePackage(TablePackage tablepackage) {
        ActivePackage activePackage = new ActivePackage(tablepackage.getName());
        activePackage.setPackageId(tablepackage.getId());
        mrate = new MRate();
        
        if (tablepackage.getRate() == null) {
            int currentIndex = 0;
            activePackage.setSingle(false);
            String[] multirate = tablepackage.getMultiRate().split("~");
            String[] multievery = tablepackage.getMultiEvery().split(",");
            String[] multiminrate = tablepackage.getMultiMinRate().split(",");
            String[] mfrom = tablepackage.getMFrom().split(",");
            String[] mto = tablepackage.getMTo().split(",");
            System.out.println(multirate.length);
            for (String rt : multirate) {
                tableRate = Double.valueOf(rt);
                every = calculate(new TimeConverter().convert(multievery[currentIndex]));
                minrate = Double.valueOf(multiminrate[currentIndex]);
                MToTime = mto[currentIndex];
                MFromTime = mfrom[currentIndex];
                rate = new Rate(tableRate, every, minrate, MFromTime, MToTime);
                mrate.addRate(rate);
                currentIndex++;
            }
        } else {
            activePackage.setSingle(true);
            tableRate = Double.valueOf(tablepackage.getRate());
            every = calculate(new TimeConverter().convert(tablepackage.getEvery()));
            minrate = Double.valueOf(tablepackage.getMinRate());
            MToTime = tablepackage.getMTo();
            MFromTime = tablepackage.getMFrom();
            rate = new Rate(tableRate, every, minrate, MFromTime, MToTime);
            mrate.addRate(rate);
        }
        
        activePackage.setRateList(mrate);
        activePackage.setPrice(Double.valueOf(tablepackage.getPrice()));
        
        return activePackage;
    }
    
    public int calculate(TimeFormat time) {
        int totalminutes, totalseconds;
        if (time.getHours() != 0) {
            totalminutes = time.getHours() * 60;
            totalseconds = time.getMinutes() + totalminutes * 60;
        } else {
            totalseconds = time.getMinutes() * 60;
        }
        totalseconds += time.getSeconds();
        
        return totalseconds;
    }
}
