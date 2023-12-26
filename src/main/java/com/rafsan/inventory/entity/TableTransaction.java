package com.rafsan.inventory.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="table_transaction")
public class TableTransaction implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @OneToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="transaction_id")
    private Transaction transaction;
    
    @OneToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="station_id")
    private Station station;
    
    @OneToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="package_id")
    private TablePackage tablePackage;
    
    @Column(name="trans_start", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transStart;
    
    @Column(name="trans_stop", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transStop;
    
    @Column(name="amount")
    private String amount;
    
    @Column(name="duration")
    private String duration;
    
    @Column(name="target")
    private String target;
    
    @Column(name="pf")
    private short powerfailure;
    
    @Column(name="status") // 1 = active
    private short status;
    
    public TableTransaction() {}
    
    public TableTransaction(Transaction transaction, Station station, Date transStart, String amount, String duration, short status) {
        this.transaction = transaction;
        this.station = station;
        this.transStart = transStart;
        this.amount = amount;
        this.duration = duration;
        this.status = status;
    }
    
    public void setTablePackage(TablePackage tablePackage) {
        this.tablePackage = tablePackage;
    }
    
    public TablePackage getTablePackage() {
        return tablePackage;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    public void setStation(Station station) {
        this.station = station;
    }
    
    public Station getStation() {
        return station;
    }
    
    public void setStart(Date start) {
        this.transStart = start;
    }
    
    public Date getStart() {
        return transStart;
    }
    
    public void setStop(Date stop) {
        this.transStop = stop;
    }
    
    public Date getStop() {
        return transStop;
    }
    
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public String getAmount() {
        return amount;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setStatus(short status) {
        this.status = status;
    }
    
    public short getStatus() {
        return status;
    }
    
    public void setPowerFailure(short powerstatus) {
        this.powerfailure = powerstatus;
    }
    
    public short getPowerFailure() {
        return powerfailure;
    }
}
