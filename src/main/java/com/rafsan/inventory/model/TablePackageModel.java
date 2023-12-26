package com.rafsan.inventory.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.TablePackageDao;
import com.rafsan.inventory.entity.Station;
import com.rafsan.inventory.entity.TablePackage;
import com.rafsan.inventory.utility.TimeUtil;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class TablePackageModel implements TablePackageDao {
    
    private Session session;

    @Override
    public ObservableList<TablePackage> getTablePackages() {
        ObservableList<TablePackage> list = FXCollections.observableArrayList();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            List<TablePackage> tablePackages = session.createQuery("from TablePackage where deleted = 0").list();
            session.beginTransaction().commit();
            tablePackages.stream().forEach(list::add);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return list;
    }

    @Override
    public TablePackage getTablePackage(long id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TablePackage tablePackage = new TablePackage();
        
        try {
            session.beginTransaction();
            tablePackage = session.get(TablePackage.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return tablePackage;
    }

    @Override
    public TablePackage getTablePackageByName(String tablePackageName) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TablePackage tablePackage = new TablePackage();
        
        try {
            session.beginTransaction();
            Query query = session.createQuery("from TablePackage where deleted = 0 and name=:name");
            query.setParameter("name", tablePackageName);
            tablePackage = (TablePackage) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return tablePackage;
    }

    @Override
    public void saveTablePackage(TablePackage tablePackage) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            session.save(tablePackage);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Rolling back transaction");
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateTablePackage(TablePackage tablePackage) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TablePackage tp = session.get(TablePackage.class, tablePackage.getId());
            tp.setName(tablePackage.getName());
            tp.setPrice(tablePackage.getPrice());
            tp.setRate(tablePackage.getRate());
            tp.setEvery(tablePackage.getEvery());
            tp.setMinRate(tablePackage.getMinRate());
            tp.setMultiRate(tablePackage.getMultiRate());
            tp.setMultiEvery(tablePackage.getMultiEvery());
            tp.setMultiMinRate(tablePackage.getMultiMinRate());
            tp.setMFrom(tablePackage.getMFrom());
            tp.setMTo(tablePackage.getMTo());
            tp.setStations(tablePackage.getStations());
            tp.setDays(tablePackage.getDays());
            tp.setStopSetelah(tablePackage.getStopSetelah());
            tp.setPembulatan(tablePackage.getPembulatan());
            tp.setDeleted(tablePackage.getDeleted());
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Rolling back transaction");
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
//    soft delete
    @Override
    public void deleteTablePackage(TablePackage tablePackage) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TablePackage tp = session.get(TablePackage.class, tablePackage.getId());
            tp.setDeleted((short) 1);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Rolling back transaction");
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

//    Hard delete
//    @Override
//    public void deleteTablePackage(TablePackage tablePackage) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        TablePackage tp = session.get(TablePackage.class, tablePackage.getId());
//        session.delete(tp);
//        session.getTransaction().commit();
//    }

    @Override
    public ObservableList<String> getNames() {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        ObservableList<String> list = FXCollections.observableArrayList();
        
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(TablePackage.class);
            criteria.setProjection(Projections.property("name"));
            criteria.add(Restrictions.eq("deleted", (short) 0)); // Menambahkan klausa WHERE foo = 'bar'
//            ObservableList<String> list = FXCollections.observableArrayList(criteria.list());
            list.addAll(criteria.list());
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return list;
    }
    
    @Override
    public ObservableList<String> getAvailablePackage(String stationName) {
        ObservableList<String> list = FXCollections.observableArrayList();
        ObservableList<TablePackage> packageList = FXCollections.observableArrayList();
        List<TablePackage> tablePackages = new ArrayList<>();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
    //        List<TablePackage> tablePackages = session.createQuery("from TablePackage where deleted = 0").list();
            tablePackages.addAll(session.createQuery("from TablePackage where deleted = 0").list());
            session.beginTransaction().commit();
            tablePackages.stream().forEach(packageList::add);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        try {
            for (TablePackage i : tablePackages) {
//                    if (i.getDeleted() == (short) 0) {
    //                ObjectMapper mapper = new ObjectMapper();
    //                JsonNode res = mapper.readTree(i.getStations());
    //                List<String> stationList = mapper.readValue(res.get("stations").toString(), new TypeReference<List<String>>(){});

                    List<String> stationList = i.getStationList();

                    String[] MFromlist = i.getMFrom().split(",");
                    String[] MTolist = i.getMTo().split(",");
                    String status = "single";

                    // Mendapatkan hari sekarang
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    // Mendapatkan nama hari dalam bahasa Inggris
                    String[] daysOfWeek = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
                    String today = daysOfWeek[dayOfWeek];

                    // Parsing JSON menggunakan Jackson
    //                ObjectMapper mapper = new ObjectMapper();
    //                JsonNode res = mapper.readTree(getStations());
    //                List<String> stationList = mapper.readValue(res.get("stations").toString(), new TypeReference<List<String>>(){});

                    ObjectMapper objectMapper = new ObjectMapper();
    //                System.out.println(i.getDays());
                    JsonNode ros = objectMapper.readTree(i.getDays());
                    List<String> daysList = objectMapper.readValue(ros.get("days").toString(), new TypeReference<List<String>>(){});

                    // Mengecek apakah setiap nilai dalam array sesuai dengan hari ini
                    for (String day : daysList) {
                        if (day.equalsIgnoreCase(today)) {
    //                        System.out.println(day + " adalah hari ini");

                            if (MFromlist.length >= 1 && MTolist.length >= 1) {
                                status = "multi";
                            }

                            if ("multi".equals(status)) {
                                for (int j = 0; j < MFromlist.length; j++) {
                                    String[] MFrom = MFromlist[j].split(":");
                                    String[] MTo = MTolist[j].split(":");

                                    LocalTime startTime = LocalTime.of(Integer.parseInt(MFrom[0]), Integer.parseInt(MFrom[1]), Integer.parseInt(MFrom[2]));
                                    LocalTime endTime = LocalTime.of(Integer.parseInt(MTo[0]), Integer.parseInt(MTo[1]), Integer.parseInt(MTo[2]));

                                    if (TimeUtil.isPackageAvailable(startTime, endTime)) {
                                        StationModel stationModel = new StationModel();

                                        Station station;
                                        for (String st : stationList) {
                                            station = stationModel.getStation(Long.parseLong(st));

    //                                        System.out.println(station.getName());
                                            if (station.getName().toLowerCase().equals(stationName.toLowerCase())) {
    //                                            System.out.println("Match " + station.getName());
                                                list.add(i.getName());
                                            }
                                        }
                                    }
                                }
                            } else {
                                String[] MFrom = MFromlist[0].split(":");
                                String[] MTo = MTolist[0].split(":");

                                LocalTime startTime = LocalTime.of(Integer.parseInt(MFrom[0]), Integer.parseInt(MFrom[1]), Integer.parseInt(MFrom[2]));
                                LocalTime endTime = LocalTime.of(Integer.parseInt(MTo[0]), Integer.parseInt(MTo[1]), Integer.parseInt(MTo[2]));

                                if (TimeUtil.isPackageAvailable(startTime, endTime)) {
                                    StationModel stationModel = new StationModel();

                                    Station station;
                                    for (String st : stationList) {
                                        station = stationModel.getStation(Long.parseLong(st));

    //                                    System.out.println(station.getName());
                                        if (station.getName().toLowerCase().equals(stationName.toLowerCase())) {
    //                                        System.out.println("Match " + station.getName());
                                            list.add(i.getName());
                                        }
                                    }
                                }
                            }
                        }
//                    }
                }
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(TablePackageModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }
    
//    @Override
//    public ObservableList<String> getAvailablePackage(String stationName) {
//        ObservableList<String> list = FXCollections.observableArrayList();
//        ObservableList<TablePackage> packageList = FXCollections.observableArrayList();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        List<TablePackage> tablePackages = session.createQuery("from TablePackage").list();
//        session.beginTransaction().commit();
//        tablePackages.stream().forEach(packageList::add);
//        
//        try {
//            for (TablePackage i : tablePackages) {
//                StationModel stationModel = new StationModel();
//                
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode res = mapper.readTree(i.getStations());
//                List<String> stationList = mapper.readValue(res.get("stations").toString(), new TypeReference<List<String>>(){});
//                Station station;
//                for (String st : stationList) {
//                    station = stationModel.getStation(Long.parseLong(st));
//                    
//                    System.out.println(station.getName());
//                    if (station.getName().toLowerCase().equals(stationName.toLowerCase())) {
//                        System.out.println("Match " + station.getName());
//                        list.add(i.getName());
//                    }
//                }
//            }
//        } catch (JsonProcessingException ex) {
//            Logger.getLogger(TablePackageModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return list;
//    }

//    @Override
//    public ObservableList<Station> getStation(long packageId) {
//        ObservableList<Station> stations = FXCollections.observableArrayList();
//        try {
//            session = HibernateUtil.getSessionFactory().getCurrentSession();
//            session.beginTransaction();
//            TablePackage tablePackage = session.get(TablePackage.class, packageId);
//            session.getTransaction().commit();
//            
//            StationModel stationModel = new StationModel();
//            
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode res = mapper.readTree(tablePackage.getStations());
//            List<String> list = mapper.readValue(res.get("stations").toString(), new TypeReference<List<String>>(){});
//            Station station;
//            for (String i : list) {
//                station = stationModel.getStation(Long.parseLong(i));
//                stations.add(station);
//            }
//            
//        } catch (JsonProcessingException ex) {
//            Logger.getLogger(TablePackageModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return stations;
//    }
    
}
