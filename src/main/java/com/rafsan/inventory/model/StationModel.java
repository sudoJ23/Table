package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.core;
import com.rafsan.inventory.dao.StationDao;
import com.rafsan.inventory.entity.Station;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

public class StationModel implements StationDao {

    private Session session;
    
    @Override
    public ObservableList<Station> getStations() {
        ObservableList<Station> list = FXCollections.observableArrayList();
        session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            List<Station> stations = session.createQuery("from Station").list();
            session.beginTransaction().commit();
            stations.stream().forEach(list::add);
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }

        return list;
    }

    @Override
    public Station getStation(long id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        Station station = new Station();
        try {
            session.beginTransaction();
            station = session.get(Station.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }

        return station;
    }

    @Override
    public Station getStationByName(String name) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        Station station = new Station();
        try {
            session.beginTransaction();
            Query query = session.createQuery("from Station where name=:name");
            query.setParameter("name", name);
            station = (Station) query.uniqueResult();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        
        return station;
    }

    @Override
    public void saveStation(Station station) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            session.save(station);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public void updateStation(Station station) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            Station st = session.get(Station.class, station.getId());
            st.setName(station.getName());
            st.setTerminal(station.getTerminal());
            st.setStatus(station.getStatus());
            st.setPackage(station.getPackage());
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteStation(Station station) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            Station st = session.get(Station.class, station.getId());
            session.delete(st);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Rolling back transaction");
            }
        } finally {
            session.close();
        }
    }

    @Override
    public ObservableList<String> getNames() {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        ObservableList<String> list = FXCollections.observableArrayList();
        
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Station.class);
            criteria.setProjection(Projections.property("name"));
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
    
}
