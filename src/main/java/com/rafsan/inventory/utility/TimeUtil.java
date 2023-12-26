package com.rafsan.inventory.utility;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static boolean isPackageAvailable(LocalTime startTime, LocalTime endTime) {
//        String dateTimeString = "09-02-2023 19:59:59";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        LocalDateTime now = LocalDateTime.parse(dateTimeString, formatter);
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // If start time is before end time, the package is available on the same day
        if (startTime.isBefore(endTime)) {
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        }

        // If start time is after end time, the package is available on two different days (overnight)
        return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
    }
    
    public static boolean isPackageAvailableTime(LocalDateTime now, LocalTime startTime, LocalTime endTime) {
        LocalTime currentTime = now.toLocalTime();

        // If start time is before end time, the package is available on the same day
        if (startTime.isBefore(endTime)) {
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        }

        // If start time is after end time, the package is available on two different days (overnight)
        return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
    }
    
    public static boolean isPackageAvailableByTime(LocalDateTime startDateTime, LocalDateTime currentDateTime, LocalTime availableStartTime, LocalTime availableEndTime) {
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime currentTime = currentDateTime.toLocalTime();

//        if (availableStartTime.isBefore(availableEndTime)) {
//            return startTime.isAfter(availableStartTime) && currentTime.isBefore(availableEndTime);
//        }

        return startTime.isBefore(availableEndTime) && (startTime.isAfter(availableStartTime) || currentTime.isAfter(availableStartTime));
    }
    
//    public static boolean isPackageAvailableByTimeAnother(LocalDateTime startDateTime, LocalDateTime currentDateTime, LocalTime availableStartTime, LocalTime availableEndTime) {
//        LocalTime startTime = startDateTime.toLocalTime();
//        LocalTime currentTime = currentDateTime.toLocalTime();
//
//        if (startTime.isAfter(availableStartTime))
//        if (availableStartTime.isBefore(availableEndTime)) {
//            return startTime.isAfter(availableStartTime) && 
//            return startTime.isAfter(availableStartTime) && currentTime.isBefore(availableEndTime);
//        }
//
//        return startTime.isAfter(availableStartTime) || currentTime.isBefore(availableEndTime);
//    }
}
