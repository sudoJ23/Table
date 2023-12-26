package com.rafsan.inventory.entity;

public class Rate {
    private double rate, minrate;
    private int every;
    private String mfromTime, mtoTime;    
    
    public Rate() {}
    
    public Rate(double rate, int every, double minrate, String mfromTime, String mtoTime) {
        this.rate = rate;
        this.every = every;
        this.minrate = minrate;
        this.mfromTime = mfromTime;
        this.mtoTime = mtoTime;
    }
    
    public double getRate() {
        return rate;
    }
    
    public void setRate(double rate) {
        this.rate = rate;
    }
    
    public int getEvery() {
        return every;
    }
    
    public void setEvery(int every) {
        this.every = every;
    }
    
    public double getMinRate() {
        return minrate;
    }
    
    public void setMinRate(double minrate) {
        this.minrate = minrate;
    }
    
    public String getMFromTime() {
        return mfromTime;
    }
    
    public void setMFromTime(String mFromTime) {
        this.mfromTime = mFromTime;
    }
    
    public String getMToTime() {
        return mtoTime;
    }
    
    public void setMToTime(String mtotime) {
        this.mtoTime = mtotime;
    }
}
