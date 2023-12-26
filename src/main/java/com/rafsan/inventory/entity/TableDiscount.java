package com.rafsan.inventory.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="table_discount")
public class TableDiscount implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="member_only")
    private short memberonly;
    
    @Column(name="percentage")
    private String percentage;
    
    public TableDiscount() {}
    
    public TableDiscount(long id, String name, short memberonly, String percentage) {
        this.id = id;
        this.name = name;
        this.memberonly = memberonly;
        this.percentage = percentage;
    }
    
    public TableDiscount(String name, short memberonly, String percentage) {
        this.name = name;
        this.memberonly = memberonly;
        this.percentage = percentage;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public short getMemberOnly() {
        return memberonly;
    }
    
    public void setMemberOnly(short memberonly) {
        this.memberonly = memberonly;
    }
    
    public String getPercentage() {
        return percentage;
    }
    
    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
    
}
