/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rafsan.inventory.model;

import com.rafsan.inventory.entity.ActivePackage;
import com.rafsan.inventory.entity.MRate;
import com.rafsan.inventory.entity.Rate;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.entity.TableTransaction;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author maulana
 */
public class Calculation {

    private static class DateRange implements Comparable<DateRange>{

        LocalDateTime start;
        LocalDateTime end;

        DateRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DateRange) {
                return start.equals(((DateRange) obj).start) && end.equals(((DateRange) obj).end);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Objects.hashCode(this.start);
            hash = 67 * hash + Objects.hashCode(this.end);
            return hash;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(start.toString());
            sb.append(" — ");
            sb.append(end.toString());
            return sb.toString();
        }

        @Override
        public int compareTo(DateRange o) {
            int compare = start.compareTo(o.start);
            if (compare == 0) {
                compare = end.compareTo(o.end);
            }
            return compare;
        }
    }

    private double totalTarif;
    private long sisaWaktu;
    private long durasi;
    private boolean unlimited;
    private final List<LocalDateTime> timeList;
    private final Map<Calculation.DateRange, Double> priceMap;

    public Calculation(TableTransaction transaction, Date upto) {
        this.totalTarif = 0;
        this.sisaWaktu = 0;
        this.durasi = 0;
        this.unlimited = false;
        this.timeList = new ArrayList<>();
        this.priceMap = new TreeMap<>();

        this.calculate(transaction, upto);
    }

    public double getTotalTarif() {
        return totalTarif;
    }

    public long getSisaWaktu() {
        return sisaWaktu;
    }

    public String getSisaWaktuFormatted() {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                sisaWaktu / 3600,
                (sisaWaktu % 3600) / 60,
                sisaWaktu % 60
        );
    }

    public boolean isLewat() {
        return sisaWaktu == 0 && !unlimited;
    }

    public boolean getUnlimited() {
        return unlimited;
    }

    public long getDurasi() {
        return durasi;
    }

    public String getDurasiFormatted() {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                durasi / 3600,
                (durasi % 3600) / 60,
                durasi % 60
        );
    }

    public List<LocalDateTime> getTimeList() {
        return timeList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<DateRange, Double> entry : priceMap.entrySet()) {
            DateRange range = entry.getKey();
            Double rate = entry.getValue();
            sb.append("# ").append(range).append(" = ").append(rate);
            sb.append("\n");
        }
        sb.append("- total Tarif : Rp").append(Math.round(totalTarif));
        sb.append("\n");
        sb.append("- sisa Waktu : ").append(getSisaWaktuFormatted());
        return sb.toString();
    }

    private void calculate(TableTransaction transaction, Date upto) {
        Date start = transaction.getStart();
        TablePackage tablePackage = transaction.getTablePackage();
        // pembulatan in minutes
        int pembulatan = Integer.parseInt(tablePackage.getPembulatan()) / 60;

        ActivePackage activePackage = tablePackage.getActivePackage();
        MRate mRate = activePackage.getRateList();
        Duration target = Duration.ofSeconds(Long.parseLong(transaction.getTarget()));
        this.unlimited = target.equals(Duration.ofSeconds(0));

        // execute when transaction status = 0
        if (transaction.getStatus() != (short) 0) {
            return;
        }

        // execute when transaction power failure = 0
        if (transaction.getPowerFailure() != (short) 0) {
            return;
        }

        // Waktu meja start
        LocalDateTime begin = toLocalDateTime(start);
        // Waktu sekarang
        LocalDateTime activeTimer = toLocalDateTime(upto);
        // Waktu meja berakhir
        LocalDateTime end = begin.plus(target.getSeconds(), ChronoUnit.SECONDS).withNano(0);
        Duration diffSisa = Duration.between(activeTimer, end);
        this.sisaWaktu = diffSisa.getSeconds();
        Duration duration = Duration.between(begin, activeTimer);
        this.durasi = duration.getSeconds();

        List<Rate> rates = mRate.getAvailableRates(begin, activeTimer);

        // stop if no range rate found
        if (rates.isEmpty()) {
            return;
        }

        // single range rate
        if (rates.size() == 1) {
            if (activeTimer.isAfter(end) && !unlimited) {
                activeTimer = end;
                if (this.sisaWaktu < 0) {
                    this.sisaWaktu = 0;

                    duration = Duration.between(begin, end);
                    this.durasi = duration.getSeconds();
                }
            }

            Rate rate = rates.get(0);
            int every = rate.getEvery();
            LocalDateTime time = begin.withNano(0);

            // calculating price map
            while (time.isBefore(activeTimer) || time.isEqual(activeTimer)) {
                DateRange range = new DateRange(time, time.plus(every, ChronoUnit.SECONDS));
                priceMap.put(range, null);
                time = range.end.withNano(0);
            }

            time = begin.plus(1, ChronoUnit.MINUTES).withNano(0);

            while (time.isBefore(activeTimer) || time.isEqual(activeTimer)) {
                Map.Entry<DateRange, Double> foundEntry = null;
                for (Map.Entry<DateRange, Double> entry : priceMap.entrySet()) {
                    DateRange key = entry.getKey();
                    if (time.isEqual(key.start) || (time.isAfter(key.start) && time.isBefore(key.end)) || time.isEqual(key.end)) {
                        foundEntry = entry;
                        break;
                    }
                }

                if (foundEntry == null) {
                    continue;
                }

                Double foundRate = foundEntry.getValue();
                LocalDateTime foundStart = foundEntry.getKey().start;
                long foundDuration = ChronoUnit.MINUTES.between(foundStart, time);
                boolean isAfterPembulatan = foundDuration > pembulatan;
                if (foundRate == null && isAfterPembulatan) {
                    priceMap.put(foundEntry.getKey(), rate.getRate());
                    // increment total tarif
                    totalTarif += rate.getRate();
                    printRateEveryMinute(rate.getRate(), time);
                } else {
                    printRateEveryMinute(0, time);
                }

                timeList.add(time);

                // increment calculation time
                time = time.plus(1, ChronoUnit.MINUTES);
            }

            if (totalTarif > 0 && totalTarif < rate.getMinRate()) {
                totalTarif = rate.getMinRate();
            }

            return;
        }

        Rate firstRate = null;
        LocalDateTime lastTime = null;

        // multi range rate
        LocalDateTime time = begin.withNano(0);

        // calculating price map
        while (time.isBefore(activeTimer) || time.isEqual(activeTimer)) {
            for (int i = 0; i < rates.size(); i++) {
                Rate rate = rates.get(i);

                LocalTime from = LocalTime.parse(rate.getMFromTime());
                LocalTime to = LocalTime.parse(rate.getMToTime());

                if (time.toLocalTime().equals(from) || (time.toLocalTime().isAfter(from) && time.toLocalTime().isBefore(to))) {
                    int every = rate.getEvery();

                    DateRange range = new DateRange(time, time.plus(every, ChronoUnit.SECONDS));
                    priceMap.put(range, null);
                    time = range.end.withNano(0);
                }
            }
        }

        for (int i = 0; i < rates.size(); i++) {
            Rate rate = rates.get(i);
            if (firstRate == null) {
                firstRate = rate;
            }

            LocalTime from = LocalTime.parse(rate.getMFromTime());
            LocalTime to = LocalTime.parse(rate.getMToTime());

            // setup from date
            Calendar calendar = Calendar.getInstance();
            setCalendarDateTime(calendar, start, from);
            LocalDateTime fromDate = lastTime != null
                    ? lastTime
                    : begin.toLocalTime().withNano(0).isAfter(from)
                    ? begin
                    : toLocalDateTime(calendar.getTime());

            // setup to date
            setCalendarDateTime(calendar, upto, to);
            LocalDateTime toDate = activeTimer.toLocalTime().withNano(0).isBefore(to)
                    ? activeTimer
                    : toLocalDateTime(calendar.getTime());

            time = fromDate.plus(1, ChronoUnit.MINUTES).withNano(0);

            double total = 0;
            while (time.isBefore(toDate) || time.isEqual(toDate)) {
                Map.Entry<DateRange, Double> foundEntry = null;
                for (Map.Entry<DateRange, Double> entry : priceMap.entrySet()) {
                    DateRange key = entry.getKey();
                    if (time.isEqual(key.start) || (time.isAfter(key.start) && time.isBefore(key.end))) {
                        foundEntry = entry;
                        break;
                    }
                }

                if (foundEntry == null) {
                    continue;
                }

                Double foundRate = foundEntry.getValue();
                LocalDateTime foundStart = foundEntry.getKey().start;
                long foundDuration = ChronoUnit.MINUTES.between(foundStart, time);
                boolean isAfterPembulatan = foundDuration > pembulatan;
                if (foundRate == null && isAfterPembulatan) {
                    double tarif = rate.getRate();

                    // check rate kelipatan sebelumnya yang menit belum penuh, menggunakan rate sebelumnya
                    if (lastTime != null) {
                        boolean isBeforeNewStartRate = lastTime.toLocalTime().isBefore(from);
                        boolean isAfterNewStartRate = time.toLocalTime().isAfter(from);
                        if (i > 0 && isBeforeNewStartRate && isAfterNewStartRate) {
                            tarif = rates.get(i - 1).getRate();
                        }
                    }
                    
                    priceMap.put(foundEntry.getKey(), tarif);
                    // increment total tarif
                    totalTarif += tarif;
                    printRateEveryMinute(tarif, time);
                } else {
                    printRateEveryMinute(0, time);
                }

                timeList.add(time);

                // store the last calculated time
                lastTime = time;

                // increment calculation time
                time = time.plus(1, ChronoUnit.MINUTES);
            }

            printRateTotal(rate.getMFromTime(), rate.getMToTime(), total);
        }

        if (firstRate != null && totalTarif > 0 && totalTarif < firstRate.getMinRate()) {
            totalTarif = firstRate.getMinRate();
        }
    }

    private void printRateEveryMinute(double rate, LocalDateTime time) {
        StringBuilder sb = new StringBuilder();
        sb.append("~ ").append(time);
        sb.append(" : Rp").append(rate);
        System.out.println(sb.toString());
    }

    private void printRateTotal(String from, String to, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("$ ").append(from).append("-").append(to);
        sb.append(" : Rp").append(Math.round(total));
        System.out.println(sb.toString());
    }

    private void setCalendarDateTime(Calendar calendar, Date date, LocalTime time) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
        calendar.set(Calendar.MINUTE, time.getMinute());
        calendar.set(Calendar.SECOND, time.getSecond());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withNano(0);
    }
}
