package com.rafsan.inventory.utility;

public class TimeFormat {
    private int hours, minutes, seconds;
    
    public TimeFormat() {}
    
    public TimeFormat(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
    
    public void setHours(int hours) {
        this.hours = hours;
    }
    
    public int getHours() {
        return hours;
    }
    
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    
    public int getMinutes() {
        return minutes;
    }
    
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    
    public int getSeconds() {
        return seconds;
    }
}
