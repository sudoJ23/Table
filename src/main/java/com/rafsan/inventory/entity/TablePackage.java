package com.rafsan.inventory.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name="table_package")
@TypeDef(name="json", typeClass=JsonType.class)
public class TablePackage implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="price")
    private String price;
    
    @Column(name="rate")
    private String rate;
    
    @Column(name="every")
    private String every;
    
    @Column(name="minrate")
    private String minrate;
    
    @Column(name="multi_rate")
    private String multi_rate;
    
    @Column(name="multi_every")
    private String multi_every;
    
    @Column(name="multi_minrate")
    private String multi_minrate;
    
    @Column(name="mfrom")
    private String mfrom;
    
    @Column(name="mto")
    private String mto;
    
    @Type(type="json")
    @Column(name="stations", columnDefinition = "json")
    private String stations;
    
    @Type(type="json")
    @Column(name="available_day", columnDefinition = "json")
    private String days;
    
    @Column(name="stopsetelah")
    private String stopsetelah;
    
    @Column(name="pembulatan")
    private String pembulatan;
    
    @Column(name="deleted")
    private short deleted;
    
    public TablePackage() {}
    
    public void setMinRate(String minrate) {
        this.minrate = minrate;
    }
    
    public String getMinRate() {
        return minrate;
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
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getRate() {
        return rate;
    }
    
    public void setRate(String rate) {
        this.rate = rate;
    }
    
    public String getEvery() {
        return every;
    }
    
    public void setEvery(String every) {
        this.every = every;
    }
    
    public String getMultiRate() {
        return multi_rate;
    }
    
    public void setMultiRate(String multiRate) {
        this.multi_rate = multiRate;
    }
    
    public String getMultiEvery() {
        return multi_every;
    }
    
    public void setMultiEvery(String multiEvery) {
        this.multi_every = multiEvery;
    }
    
    public String getMultiMinRate() {
        return multi_minrate;
    }
    
    public void setMultiMinRate(String multiMinRate) {
        this.multi_minrate = multiMinRate;
    }
    
    public String getMFrom() {
        return mfrom;
    }
    
    public void setMFrom(String MFrom) {
        this.mfrom = MFrom;
    }
    
    public String getMTo() {
        return mto;
    }
    
    public void setMTo(String mto) {
        this.mto = mto;
    }
    
    public String getStations() {
        return stations;
    }
    
    public List<String> getStationList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode res = mapper.readTree(getStations());
        List<String> stationList = mapper.readValue(res.get("stations").toString(), new TypeReference<List<String>>(){});
        
        return stationList;
    }
    
    public void setStations(String stations) {
        this.stations = stations;
    }
    
    public String getDays() {
        return days;
    }
    
    public void setDays(String days) {
        this.days = days;
    }
    
    public void setStopSetelah(String stopsetelah) {
        this.stopsetelah = stopsetelah;
    }
    
    public String getStopSetelah() {
        return stopsetelah;
    }
    
    public void setPembulatan(String pembulatan) {
        this.pembulatan = pembulatan;
    }
    
    public String getPembulatan() {
        return pembulatan;
    }
    
    public void setDeleted(short deleted) {
        this.deleted = deleted;
    }
    
    public short getDeleted() {
        return deleted;
    }
    
}
