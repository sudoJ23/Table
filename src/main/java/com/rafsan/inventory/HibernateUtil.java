package com.rafsan.inventory;

import java.io.IOException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static Configuration configuration = new Configuration();
    
    private static void getData() throws IOException {
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/tableonly?useSSL=false&autoReconnect=true");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.connection.autoReconnect", "true");
        configuration.setProperty("hibernate.connection.autoReconnectForPools", "true");
        configuration.setProperty("hibernate.connection.is-connection-validation-required", "true");
    }

    public static boolean setSessionFactory() throws IOException {
        getData();
        try {
            sessionFactory = configuration
                    .configure()
                    .buildSessionFactory();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            return false;
            
        }
        
        return true;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
