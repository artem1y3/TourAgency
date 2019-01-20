import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ru.sfedu.touragency.api.DataProviderXml;
import ru.sfedu.touragency.model.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DataProviderXmlTest {
    private static DataProviderXml clientDataProviderXml = new DataProviderXml(ModelType.SIMPLE_USER);
    private static DataProviderXml proClientDataProviderXml = new DataProviderXml(ModelType.PRO_USER);
    private static DataProviderXml orderDataProviderXml = new DataProviderXml(ModelType.ORDER);
    private static DataProviderXml tourDataProviderXml = new DataProviderXml(ModelType.TOUR);
    private static DataProviderXml hotelDataProviderXml = new DataProviderXml(ModelType.HOTEL);

    private List<Client> clients = makeClients();
    private List<ProClient> proClients = makeProClients();
    private List<Order> orders = makeOrders();
    private List<Tour> tours = makeTours();
    private List<Hotel> hotels = makeHotels();

    private static List<Client> makeClients() {
        List<Client> clients = new ArrayList<>();
        Client client1 = new Client();
        client1.setId(1);
        client1.setEmail("so@ba.ka");
        client1.setPassword("123");
        client1.setLastName("Galagidze");
        client1.setFirstName("Maramadzai");

        Client client2 = new Client();
        client2.setId(2);
        client2.setEmail("na@ru.to");
        client2.setPassword("1234");
        client2.setLastName("Uchiha");
        client2.setFirstName("Sasuke");

        Client client3 = new Client();
        client3.setId(3);
        client3.setEmail("sa@su.ke");
        client3.setPassword("5213");
        client3.setLastName("Uzumaki");
        client3.setFirstName("Naruto");
        clients.add(client1);
        clients.add(client2);
        clients.add(client3);
        return clients;
    }

    private static List<ProClient> makeProClients() {
        List<ProClient> proClients = new ArrayList<>();
        ProClient proClient1 = new ProClient();
        proClient1.setId(1);
        proClient1.setEmail("ka@bu.ka");
        proClient1.setPassword("123");
        proClient1.setLastName("Galagidze");
        proClient1.setFirstName("Maramadzai");
        proClient1.setDiscount(20);
        proClient1.setPoints(120);

        ProClient proClient2 = new ProClient();
        proClient2.setId(2);
        proClient2.setEmail("kdsds@bua.ka");
        proClient2.setPassword("123");
        proClient2.setLastName("Anasim");
        proClient2.setFirstName("Kukusim");
        proClient2.setDiscount(30);
        proClient2.setPoints(590);

        ProClient proClient3 = new ProClient();
        proClient3.setId(3);
        proClient3.setEmail("1x@be.t");
        proClient3.setPassword("123");
        proClient3.setLastName("Pokeb");
        proClient3.setFirstName("Ollest");
        proClient3.setDiscount(40);
        proClient3.setPoints(10000);

        proClients.add(proClient1);
        proClients.add(proClient2);
        proClients.add(proClient3);
        return proClients;
    }

    private static List<Order> makeOrders() {
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setId(1);
        order1.setTourId(1);
        order1.setClientId(1);
        order1.setDueDate(new Date(116, 8, 2));
        order1.setStatus(OrderStatus.SENT);

        Order order2 = new Order();
        order2.setId(2);
        order2.setTourId(2);
        order2.setClientId(2);
        order2.setDueDate(new Date(117, 12, 12));
        order2.setStatus(OrderStatus.PAID);

        Order order3 = new Order();
        order3.setId(3);
        order3.setTourId(3);
        order3.setClientId(3);
        order3.setDueDate(new Date(118, 1, 22));
        order3.setStatus(OrderStatus.SENT);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        return orders;
    }

    private static List<Tour> makeTours() {
        List<Tour> tours = new ArrayList<>();
        Tour tour1 = new Tour();
        tour1.setId(1);
        tour1.setPrice(2200);
        tour1.setCity("Taganrog");
        tour1.setDayCount(12);
        tour1.setDescription("Fantastic");
        tour1.setName("Tagan IS BEST");
        tour1.setCountry(Country.RUSSIA);


        Tour tour2 = new Tour();
        tour2.setId(2);
        tour2.setPrice(22200);
        tour2.setCity("Paris");
        tour2.setDayCount(3);
        tour2.setDescription("Francetastic");
        tour2.setName("FRACLOVE");
        tour2.setCountry(Country.FRANCE);

        Tour tour3 = new Tour();
        tour3.setId(3);
        tour3.setPrice(12200);
        tour3.setCity("Oslo");
        tour3.setDayCount(2);
        tour3.setDescription("Norwegiya");
        tour3.setName("COOL NORTH");
        tour3.setCountry(Country.NORWAY);

        tours.add(tour1);
        tours.add(tour2);
        tours.add(tour3);
        return tours;
    }

    private static List<Hotel> makeHotels() {
        List<Hotel> hotels = new ArrayList<>();
        Hotel hotel1 = new Hotel();
        hotel1.setId(1);
        hotel1.setName("Verona");
        hotel1.setDescription("The best hotel of my life");
        hotel1.setRate(5);

        Hotel hotel2 = new Hotel();
        hotel2.setId(2);
        hotel2.setName("Corona");
        hotel2.setDescription("Poison for body");
        hotel2.setRate(3);

        Hotel hotel3 = new Hotel();
        hotel3.setId(3);
        hotel3.setName("Germiona");
        hotel3.setDescription("Turkish STYLE");
        hotel3.setRate(4);

        hotels.add(hotel1);
        hotels.add(hotel2);
        hotels.add(hotel3);
        return hotels;

    }

    @Test
    public void a_saveAndGetAll() {
        //Записываем все данные
        clients.forEach(clientDataProviderXml::save);
        proClients.forEach(proClientDataProviderXml::save);
        orders.forEach(orderDataProviderXml::save);
        tours.forEach(tourDataProviderXml::save);
        hotels.forEach(hotelDataProviderXml::save);

        //считываем все данные в другие списки
        List<Client> clientsCheck = new ArrayList<>();
        List<ProClient> proClientsCheck = new ArrayList<>();
        List<Order> ordersCheck = new ArrayList<>();
        List<Tour> toursCheck = new ArrayList<>();
        List<Hotel> hotelsCheck = new ArrayList<>();

        for (Object obj : clientDataProviderXml.getAll()) {
            clientsCheck.add((Client) obj);
        }
        for (Object obj : proClientDataProviderXml.getAll()) {
            proClientsCheck.add((ProClient) obj);
        }
        for (Object obj : orderDataProviderXml.getAll()) {
            ordersCheck.add((Order) obj);
        }
        for (Object obj : tourDataProviderXml.getAll()) {
            toursCheck.add((Tour) obj);
        }
        for (Object obj : hotelDataProviderXml.getAll()) {
            hotelsCheck.add((Hotel) obj);
        }

        assertEquals(clients, clientsCheck);
        assertEquals(proClients, proClientsCheck);
        assertEquals(orders, ordersCheck);
        assertEquals(tours, toursCheck);
        assertEquals(hotels, hotelsCheck);


    }

    @Test
    public void b_getByIdAndUpdate() {
        Client updClient = (Client) clientDataProviderXml.getById(2);
        ProClient updProClient = (ProClient) proClientDataProviderXml.getById(2);
        Order updOrder = (Order) orderDataProviderXml.getById(2);
        Tour updTour = (Tour) tourDataProviderXml.getById(2);
        Hotel updHotel = (Hotel) hotelDataProviderXml.getById(2);

        updClient.setLastName("NewLAST");
        updProClient.setPoints(3000);
        updOrder.setStatus(OrderStatus.PAID);
        updTour.setDescription("New  life");
        updHotel.setRate(1);

        clientDataProviderXml.update(updClient);
        proClientDataProviderXml.update(updProClient);
        orderDataProviderXml.update(updOrder);
        tourDataProviderXml.update(updTour);
        hotelDataProviderXml.update(updHotel);


        assertEquals(updClient, clientDataProviderXml.getById(2));
        assertEquals(updProClient, proClientDataProviderXml.getById(2));
        assertEquals(updOrder, orderDataProviderXml.getById(2));
        assertEquals(updTour, tourDataProviderXml.getById(2));
        assertEquals(updHotel, hotelDataProviderXml.getById(2));
    }

    @Test
    public void c_delete() {

        clientDataProviderXml.delete(2);
        proClientDataProviderXml.delete(2);
        orderDataProviderXml.delete(2);
        tourDataProviderXml.delete(2);
        hotelDataProviderXml.delete(2);

        //убираем удаленные строки и из изначальных листов
        clients.remove(1);
        proClients.remove(1);
        orders.remove(1);
        tours.remove(1);
        hotels.remove(1);


        //считываем оставшиеся данные в списки
        List<Client> clientsCheck = new ArrayList<>();
        List<ProClient> proClientsCheck = new ArrayList<>();
        List<Order> ordersCheck = new ArrayList<>();
        List<Tour> toursCheck = new ArrayList<>();
        List<Hotel> hotelsCheck = new ArrayList<>();

        for (Object obj : clientDataProviderXml.getAll()) {
            clientsCheck.add((Client) obj);
        }
        for (Object obj : proClientDataProviderXml.getAll()) {
            proClientsCheck.add((ProClient) obj);
        }
        for (Object obj : orderDataProviderXml.getAll()) {
            ordersCheck.add((Order) obj);
        }
        for (Object obj : tourDataProviderXml.getAll()) {
            toursCheck.add((Tour) obj);
        }
        for (Object obj : hotelDataProviderXml.getAll()) {
            hotelsCheck.add((Hotel) obj);
        }


        assertEquals(clients, clientsCheck);
        assertEquals(proClients, proClientsCheck);
        assertEquals(orders, ordersCheck);
        assertEquals(tours, toursCheck);
        assertEquals(hotels, hotelsCheck);
    }
    @Test
    public void z_clearall(){
        for (Object obj : clientDataProviderXml.getAll()) {
            clientDataProviderXml.delete(((Client) obj).getId());
        }
        for (Object obj : proClientDataProviderXml.getAll()) {
            proClientDataProviderXml.delete(((ProClient) obj).getId());
        }
        for (Object obj : orderDataProviderXml.getAll()) {
            orderDataProviderXml.delete(((Order) obj).getId());
        }
        for (Object obj : tourDataProviderXml.getAll()) {
            tourDataProviderXml.delete(((Tour) obj).getId());
        }
        for (Object obj : hotelDataProviderXml.getAll()) {
            hotelDataProviderXml.delete(((Hotel) obj).getId());
        }
    }


}