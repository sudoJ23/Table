package com.rafsan.inventory.utility;

public class TimeConverter {
    
    public TimeFormat convert(String time) {
        int hour, minute, second;
        
        hour = Integer.valueOf(time.split(":")[0]);
        minute = Integer.valueOf(time.split(":")[1]);
        second = Integer.valueOf(time.split(":")[2]);
        
        TimeFormat timeFormat = new TimeFormat(hour, minute, second);
        return timeFormat;
    }
}
