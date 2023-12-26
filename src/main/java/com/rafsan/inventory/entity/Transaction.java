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
@Table(name="transaction")
public class Transaction implements Serializable {
    
    @Id
//    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private String id;
    
    @Column(name="transdate", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transdate;
    
    @Column(name="total")
    private String total;
    
    public Transaction() {}
    
    public Transaction(String id, Date transdate, String total) {
        this.id = id;
        this.transdate = transdate;
        this.total = total;
    }
    
//    public Transaction(String id, Customer customer, Date transdate, String tax, String total, String totalDiscount) {
//        this.id = id;
//        this.customer = customer;
//        this.transdate = transdate;
//        this.tax = tax;
//        this.total = total;
//        this.totalDiscount = totalDiscount;
//    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setTransdate(Date transdate) {
        this.transdate = transdate;
    }
    
    public Date getTransdate() {
        return transdate;
    }
    
    public void setTotal(String total) {
        this.total = total;
    }
    
    public String getTotal() {
        return total;
    }
}
