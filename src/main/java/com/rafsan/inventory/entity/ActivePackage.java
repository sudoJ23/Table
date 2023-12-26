package com.rafsan.inventory.entity;

public class ActivePackage {
    private boolean single;
    private String packageName;
    private long packageId;
    private double price;
    private int index;
    private MRate ratelist;
    
    public ActivePackage(String name) {
        this.packageName = name;
        this.index = 0;
    }
    
    public void setPackageId(long id) {
        this.packageId = id;
    }
    
    public long getPackageId() {
        return packageId;
    }
    
    public void setRateList(MRate ratelist) {
        this.ratelist = ratelist;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public MRate getRateList() {
        return ratelist;
    }
    
    public void setPackageName(String name) {
        this.packageName = name;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public boolean isSingle() {
        return single;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setSingle(boolean single) {
        this.single = single;
    }
}
