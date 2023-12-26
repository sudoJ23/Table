package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.TableTransactionDao;
import com.rafsan.inventory.entity.TableTransaction;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Query;
import org.hibernate.Session;

public class TableTransactionModel implements TableTransactionDao {
    
//    private static Session session;
    private Session session;

    @Override
    public ObservableList<TableTransaction> getTableTransactions() {
        ObservableList<TableTransaction> list = FXCollections.observableArrayList();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            List<TableTransaction> tableTransactions = session.createQuery("from TableTransaction").list();
            session.beginTransaction().commit();
            tableTransactions.stream().forEach(list::add);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return list;
    }

    @Override
    public TableTransaction getTableTransaction(long id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TableTransaction tableTransaction = new TableTransaction();
        
        try {
            session.beginTransaction();
            tableTransaction = session.get(TableTransaction.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return tableTransaction;
    }
    
    @Override
    public TableTransaction getTableTransactionByTransID(String id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        TableTransaction tableTransaction = new TableTransaction();
        
        try {
            session.beginTransaction();

            Query query = session.createQuery("from TableTransaction where transaction.id=:transid");
            query.setParameter("transid", id);

            tableTransaction = (TableTransaction) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return tableTransaction;
    }

    @Override
    public void saveTableTransaction(TableTransaction transaction) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            session.save(transaction);
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
    public void updateTableTransaction(TableTransaction transaction) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TableTransaction tt = session.get(TableTransaction.class, transaction.getId());
            tt.setTransaction(transaction.getTransaction());
            tt.setStation(transaction.getStation());
            tt.setStart(transaction.getStart());
            tt.setStop(transaction.getStop());
            tt.setAmount(transaction.getAmount());
            tt.setDuration(transaction.getDuration());
            tt.setTarget(transaction.getTarget());
            tt.setStatus(transaction.getStatus());
            tt.setTablePackage(transaction.getTablePackage());
            tt.setPowerFailure(transaction.getPowerFailure());
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
    public void deleteTableTransaction(TableTransaction transaction) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            TableTransaction tt = session.get(TableTransaction.class, transaction.getId());
            session.delete(tt);
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
    
}
