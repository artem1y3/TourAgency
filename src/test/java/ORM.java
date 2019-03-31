//package ru.sfedu.touragency.model.;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import ru.sfedu.touragency.api.HibernateUtil;
import ru.sfedu.touragency.model.Hotel;
import ru.sfedu.touragency.model.TestBean;
import ru.sfedu.touragency.model.TestClientBean;

//import ru.sfedu.touragency.beans.Entity;
//import ru.sfedu.touragency.beans.UserModel;

public class ORM {
    private Hotel hotel = new Hotel();

    @Test
    public void saveHotel(){
        hotel.setId(1);
        hotel.setRate(4);
        hotel.setDescription("4");
        hotel.setName("4");

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(hotel);
        session.getTransaction().commit();
        session.close();
    }
    @Test
    public void saveOrUpdateHotel(){
        hotel.setId(1);
        hotel.setRate(4);
        hotel.setDescription("4");
        hotel.setName("4");

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(hotel);
        session.getTransaction().commit();
        session.close();
    }
    @Test
    public void getHotel(){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Hotel hotel = session.get(Hotel.class, (long)1);
        session.getTransaction().commit();
        System.out.println(hotel);
        session.close();
    }
//    фамилия, дата рождения, пол
    @Test
    public void deleteHotel(){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(session.get(Hotel.class, (long)1));
        session.getTransaction().commit();
        session.close();
    }
    @Test
    public void asd(){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("from Hotel")
                .list()
                .forEach(System.out::println);
//        session.delete(session.get(Hotel.class, (long)1));
        session.getTransaction().commit();
        session.close();
    }
    @Test
    public void qwe(){
        TestBean testBean = new TestBean(1, "name", "description", new Date());

        List<TestClientBean> clients = new ArrayList<>();
        clients.add(new TestClientBean("lastName1", new Date()));
        clients.add(new TestClientBean("lastName2", new Date()));
        clients.add(new TestClientBean("lastName3", new Date()));

        testBean.setClients(clients);

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(testBean);
        session.getTransaction().commit();
        session.close();
    }
}