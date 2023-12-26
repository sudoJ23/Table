package com.rafsan.inventory.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="station")
public class Station implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="status")
    private String status;
    
    @Column(name="terminal")
    private String terminal;
    
    @OneToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="package_id")
    private TablePackage packageId;
    
    public Station() {}
    
    public Station(long id, String name, String terminal) {
        this.id = id;
        this.name = name;
        this.terminal = terminal;
    }
    
    public Station(String name, String status) {
        this.name = name;
        this.status = status;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    
    public String getTerminal() {
        return terminal;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setPackage(TablePackage id) {
        this.packageId = id;
    }
    
    public TablePackage getPackage() {
        return packageId;
    }
}
