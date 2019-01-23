import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.sfedu.touragency.api.DataProviderJdbc;
import ru.sfedu.touragency.model.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DataProviderJdbcTest {
    private static DataProviderJdbc clientDataProviderJdbc = new DataProviderJdbc(ModelType.SIMPLE_USER);
    private static DataProviderJdbc proClientDataProviderJdbc = new DataProviderJdbc(ModelType.PRO_USER);
    private static DataProviderJdbc orderDataProviderJdbc = new DataProviderJdbc(ModelType.ORDER);
    private static DataProviderJdbc tourDataProviderJdbc = new DataProviderJdbc(ModelType.TOUR);
    private static DataProviderJdbc hotelDataProviderJdbc = new DataProviderJdbc(ModelType.HOTEL);

    private static List<Client> clients = makeClients();
    private static List<ProClient> proClients = makeProClients();
    private static List<Order> orders = makeOrders();
    private static List<Tour> tours = makeTours();
    private static List<Hotel> hotels = makeHotels();

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
        for (Client client : clients) {
            client.setId(clientDataProviderJdbc.save(client));
        }
        for (ProClient proClient : proClients) {
            proClient.setId(proClientDataProviderJdbc.save(proClient));
        }
        for (Order order : orders) {
            order.setId(orderDataProviderJdbc.save(order));
        }
        for (Tour tour : tours) {
            tour.setId(tourDataProviderJdbc.save(tour));
        }
        for (Hotel hotel : hotels) {
            hotel.setId(hotelDataProviderJdbc.save(hotel));
        }

        //считываем все данные в другие списки
        List<Client> clientsCheck = new ArrayList<>();
        List<ProClient> proClientsCheck = new ArrayList<>();
        List<Order> ordersCheck = new ArrayList<>();
        List<Tour> toursCheck = new ArrayList<>();
        List<Hotel> hotelsCheck = new ArrayList<>();

        for (Object obj : clientDataProviderJdbc.getAll()) {
            clientsCheck.add((Client) obj);
        }
        for (Object obj : proClientDataProviderJdbc.getAll()) {
            proClientsCheck.add((ProClient) obj);
        }
        for (Object obj : orderDataProviderJdbc.getAll()) {
            ordersCheck.add((Order) obj);
        }
        for (Object obj : tourDataProviderJdbc.getAll()) {
            toursCheck.add((Tour) obj);
        }
        for (Object obj : hotelDataProviderJdbc.getAll()) {
            hotelsCheck.add((Hotel) obj);
        }

        for (Client client : clients) {
            assert clientsCheck.contains(client);
        }
        for (ProClient proClient : proClients) {
            assert proClientsCheck.contains(proClient);
        }
        for (Order order : orders) {
            assert ordersCheck.contains(order);
        }
        for (Tour tour : tours) {
            assert toursCheck.contains(tour);
        }
        for (Hotel hotel : hotels) {
            assert hotelsCheck.contains(hotel);
        }
    }


    @Test
    public void b_getByIdAndUpdate() {
        long testId = clients.get(1).getId();
        Client updClient = (Client) clientDataProviderJdbc.getById(testId);
        ProClient updProClient = (ProClient) proClientDataProviderJdbc.getById(testId);
        Order updOrder = (Order) orderDataProviderJdbc.getById(testId);
        Tour updTour = (Tour) tourDataProviderJdbc.getById(testId);
        Hotel updHotel = (Hotel) hotelDataProviderJdbc.getById(testId);

        updClient.setLastName("NewLAST");
        updProClient.setPoints(3000);
        updOrder.setStatus(OrderStatus.PAID);
        updTour.setDescription("New  life");
        updHotel.setRate(1);

        clientDataProviderJdbc.update(updClient);
        proClientDataProviderJdbc.update(updProClient);
        orderDataProviderJdbc.update(updOrder);
        tourDataProviderJdbc.update(updTour);
        hotelDataProviderJdbc.update(updHotel);


        assertEquals(updClient, clientDataProviderJdbc.getById(testId));
        assertEquals(updProClient, proClientDataProviderJdbc.getById(testId));
        assertEquals(updOrder, orderDataProviderJdbc.getById(testId));
        assertEquals(updTour, tourDataProviderJdbc.getById(testId));
        assertEquals(updHotel, hotelDataProviderJdbc.getById(testId));
    }

    @Test
    public void c_delete() {
        long testId = clients.get(1).getId();
        clientDataProviderJdbc.delete(testId);
        proClientDataProviderJdbc.delete(testId);
        orderDataProviderJdbc.delete(testId);
        tourDataProviderJdbc.delete(testId);
        hotelDataProviderJdbc.delete(testId);

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

        for (Object obj : clientDataProviderJdbc.getAll()) {
            clientsCheck.add((Client) obj);
        }
        for (Object obj : proClientDataProviderJdbc.getAll()) {
            proClientsCheck.add((ProClient) obj);
        }
        for (Object obj : orderDataProviderJdbc.getAll()) {
            ordersCheck.add((Order) obj);
        }
        for (Object obj : tourDataProviderJdbc.getAll()) {
            toursCheck.add((Tour) obj);
        }
        for (Object obj : hotelDataProviderJdbc.getAll()) {
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
        for (Object obj : clientDataProviderJdbc.getAll()) {
            clientDataProviderJdbc.delete(((Client) obj).getId());
        }
        for (Object obj : proClientDataProviderJdbc.getAll()) {
            proClientDataProviderJdbc.delete(((ProClient) obj).getId());
        }
        for (Object obj : orderDataProviderJdbc.getAll()) {
            orderDataProviderJdbc.delete(((Order) obj).getId());
        }
        for (Object obj : tourDataProviderJdbc.getAll()) {
            tourDataProviderJdbc.delete(((Tour) obj).getId());
        }
        for (Object obj : hotelDataProviderJdbc.getAll()) {
            hotelDataProviderJdbc.delete(((Hotel) obj).getId());
        }
    }


}

