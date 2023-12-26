package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.TableDiscountDao;
import com.rafsan.inventory.entity.TableDiscount;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

public class TableDiscountModel implements TableDiscountDao {
    
//    private static Session session;
    private Session session;

    @Override
    public ObservableList<TableDiscount> getTableDiscounts() {
        ObservableList<TableDiscount> list = FXCollections.observableArrayList();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            List<TableDiscount> tablediscount = session.createQuery("from TableDiscount").list();
            session.beginTransaction().commit();
            tablediscount.stream().forEach(list::add);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return list;
    }

    @Override
    public TableDiscount getTableDiscount(long id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TableDiscount tableDiscount = new TableDiscount();
        
        try {
            session.beginTransaction();
            tableDiscount = session.get(TableDiscount.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return tableDiscount;
    }
    
    @Override
    public TableDiscount getTableDiscountByName(String name) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TableDiscount product = new TableDiscount();
        
        try {
            session.beginTransaction();
            Query query = session.createQuery("from TableDiscount where name=:name");
            query.setParameter("name", name);
            product = (TableDiscount) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return product;
    }

    @Override
    public void saveTableDiscount(TableDiscount tableDiscount) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            session.save(tableDiscount);
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
    public void updateTableDiscount(TableDiscount tableDiscount) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TableDiscount pd = session.get(TableDiscount.class, tableDiscount.getId());
            pd.setName(tableDiscount.getName());
            pd.setMemberOnly(tableDiscount.getMemberOnly());
            pd.setPercentage(tableDiscount.getPercentage());
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
    public void deleteTableDiscount(TableDiscount tableDiscount) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TableDiscount pd = session.get(TableDiscount.class, tableDiscount.getId());
            session.delete(pd);
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
    public ObservableList<String> getNames() {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        ObservableList<String> list = FXCollections.observableArrayList();
        
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(TableDiscount.class);
            criteria.setProjection(Projections.property("name"));
    //        ObservableList<String> list = FXCollections.observableArrayList(criteria.list());
            list.addAll(criteria.list());
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return list;
    }
}
