package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.TransactionDao;
import com.rafsan.inventory.entity.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;

public class TransactionModel implements TransactionDao {

//    private static Session session;
    private Session session;
    
    @Override
    public ObservableList<Transaction> getTransactions() {
        ObservableList<Transaction> list = FXCollections.observableArrayList();
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            List<Transaction> transactions = session.createQuery("from Transaction").list();
            session.beginTransaction().commit();
            transactions.stream().forEach(list::add);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return list;
    }

    @Override
    public Transaction getTransaction(String id) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = new Transaction();
        
        try {
            session.beginTransaction();
            transaction = session.get(Transaction.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return transaction;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
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
    public void updateTransaction(Transaction transaction) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            Transaction tr = session.get(Transaction.class, transaction.getId());
            tr.setTransdate(transaction.getTransdate());
            tr.setTotal(transaction.getTotal());
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
    public void deleteTransaction(Transaction transaction) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            Transaction tr = session.get(Transaction.class, transaction.getId());
            session.delete(tr);
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
    public Transaction getLast() {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = new Transaction();
        
        try {
            session.beginTransaction();
            Query query = session.createQuery("from Transaction order by id desc").setMaxResults(1);
            transaction = (Transaction) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return transaction;
    }
    
    @Override
    public Transaction getLastAuditDate() {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = new Transaction();
        
        try {
            session.beginTransaction();
            Query query = session.createQuery("from Transaction where auditdate IS NOT NULL order by id desc").setMaxResults(1);
            transaction = (Transaction) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return transaction;
    }
    
    @Override
    public List<Transaction> getTransactionByAuditDate(Date auditdate) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            session.beginTransaction();

            Query query = session.createQuery("from Transaction where auditdate=:auditdate");
            query.setParameter("auditdate", auditdate);

//            List<Transaction> transactions = query.list();
            transactions.addAll(query.list());
            session.beginTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return transactions;
    }
    
    @Override
    public List<String> getTransactionsByRange(String start, String stop) {
//        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session = HibernateUtil.getSessionFactory().openSession();
        List<String> list = new ArrayList<>();
        
        try {
            session.beginTransaction();
            SQLQuery query = session.createSQLQuery("SELECT DISTINCT(transaction.id) FROM sales LEFT JOIN transaction ON transaction.id = sales.transaction_id where sales.transdate BETWEEN :start AND :stop");
            query.setParameter("start", start);
            query.setParameter("stop", stop);
    //        List<String> list = query.list();
            list.addAll(query.list());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return list;
    }
    
    @Override
    public Transaction addTransaction() {
        Transaction transaction = null;
        LocalDateTime datetime = LocalDateTime.now();
        String tglf = datetime.format(DateTimeFormatter.ofPattern("uuMMdd"));
        String formatted = "";
        boolean isDuplicated = false;
        
        session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            session.beginTransaction();
            SQLQuery inst = session.createSQLQuery("SELECT id from transaction where id LIKE :tglf ORDER BY id desc");
            inst.setParameter("tglf", MatchMode.START.toMatchString(tglf));
            inst.setMaxResults(1);

            if (inst.uniqueResult() != null) {
                String transid = inst.uniqueResult().toString();
                int nomor = Integer.valueOf(transid.substring(6)) + 1;
                formatted = tglf + String.format("%05d", nomor);
            } else {
                formatted = tglf + String.format("%05d", 1);
            }
            inst = session.createSQLQuery("INSERT INTO `transaction` (`id`, `transdate`, `total`) VALUES (?, ?, ?)");
            inst.setParameter(0, formatted);
            inst.setParameter(1, datetime);
            inst.setParameter(2, "0");
            inst.executeUpdate();
            session.getTransaction().commit();
        } catch (NumberFormatException e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Rolling back transaction");
            }
            
            if (e.getCause().getMessage().startsWith("Duplicate entry")) {
                System.out.println("Transaction duplicated");
                System.out.println(e.getCause().getMessage());
                isDuplicated = true;
            }
        } finally {
            session.close();
        }
        
        if (!isDuplicated) {
            transaction = new Transaction(formatted, new Date(), "0");
        }
        
        return transaction;
    }
}
