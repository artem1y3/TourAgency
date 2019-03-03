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

    private static List<Client> clients = makeClients(3);
    private static List<ProClient> proClients = makeProClients(3);
    private static List<Order> orders = makeOrders(3);
    private static List<Tour> tours = makeTours(3);
    private static List<Hotel> hotels = makeHotels(3);

    private static List<Client> makeClients(int count) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Client client1 = new Client();
            client1.setId(i + 1);
            client1.setEmail("so" + i + "@ba.ka");
            client1.setPassword("TestPassword" + i);
            client1.setLastName("TestLastName" + i);
            client1.setFirstName("TestFirstName");
            clients.add(client1);
        }
        return clients;
    }

    private static List<ProClient> makeProClients(int count) {
        List<ProClient> proClients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProClient proClient1 = new ProClient();
            proClient1.setId(i + 1);
            proClient1.setEmail("ka" + i + "@bu.ka");
            proClient1.setPassword("TestPassword" + i);
            proClient1.setLastName("TestLastName" + i);
            proClient1.setFirstName("TestFirstName" + i);
            proClient1.setDiscount(20 + i);
            proClient1.setPoints(120 + i);
            proClients.add(proClient1);
        }
        return proClients;
    }

    private static List<Order> makeOrders(int count) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Order order1 = new Order();
            order1.setId(i + 1);
            order1.setTourId(1);
            order1.setClientId(1);
            order1.setDueDate(new Date(116, 8, 2));
            order1.setStatus(OrderStatus.SENT);
            order1.setPro(false);
            orders.add(order1);
        }
        return orders;
    }

    private static List<Tour> makeTours(int count) {
        List<Tour> tours = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Tour tour1 = new Tour();
            tour1.setId(i + 1);
            tour1.setPrice(2200 + i);
            tour1.setCity("Taganrog");
            tour1.setDayCount(12 + i);
            tour1.setDescription("Fantastic");
            tour1.setName("Tagan IS BEST");
            tour1.setCountry(Country.RUSSIA);
            tours.add(tour1);
        }
        return tours;
    }

    private static List<Hotel> makeHotels(int count) {
        List<Hotel> hotels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Hotel hotel1 = new Hotel();
            hotel1.setId(1);
            hotel1.setName("Verona");
            hotel1.setDescription("The best hotel of my life");
            hotel1.setRate(5);
            hotels.add(hotel1);
        }
        return hotels;
    }

    @Test
    public void a_saveAndGetAll() {
        // Записываем все данные
        clients.stream().forEach(item -> item.setId(clientDataProviderJdbc.save(item)));
        proClients.stream().forEach(item -> item.setId(proClientDataProviderJdbc.save(item)));
        orders.stream().forEach(item -> item.setId(orderDataProviderJdbc.save(item)));
        tours.stream().forEach(item -> item.setId(tourDataProviderJdbc.save(item)));
        hotels.stream().forEach(item -> item.setId(hotelDataProviderJdbc.save(item)));

        System.out.println("saveAndGetAll");
        clients.forEach(item -> System.out.println(item));
        proClients.forEach(item -> System.out.println(item));
        orders.forEach(item -> System.out.println(item));
        tours.forEach(item -> System.out.println(item));
        hotels.forEach(item -> System.out.println(item));


        // Считываем записанные данные и смотрим contains
        clientDataProviderJdbc.getAll().stream().forEach(item -> {
            assert clients.contains(item);
        });
        proClientDataProviderJdbc.getAll().stream().forEach(item -> {
            assert proClients.contains(item);
        });
        orderDataProviderJdbc.getAll().stream().forEach(item -> {
            assert orders.contains(item);
        });
        tourDataProviderJdbc.getAll().stream().forEach(item -> {
            assert tours.contains(item);
        });
        hotelDataProviderJdbc.getAll().stream().forEach(item -> {
            assert hotels.contains(item);
        });
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


    @Test
    public void y_a_addTour() {
        DataProviderJdbc data = new DataProviderJdbc(ModelType.TOUR);
        data.addTour(10000,30, "testName", "testDesc", Country.RUSSIA, "testCity");
    }


    @Test
    public void y_b_orderTour() {
        DataProviderJdbc data = new DataProviderJdbc(ModelType.ORDER);
        data.OrderTour(1,1, false);
//        data.OrderTour(1,1, true);
    }

    @Test
    public void y_c_bookHotel() {
        DataProviderJdbc data = new DataProviderJdbc(ModelType.TOUR);
        data.bookHotel(1,2);
    }

    @Test
    public void y_d_updateTour() {
        DataProviderJdbc data = new DataProviderJdbc(ModelType.TOUR);
        data.updateTour(1,10000,30, "testName", "testDesc", Country.RUSSIA, "testCity");
    }

    @Test
    public void y_e_deleteTour() {
        DataProviderJdbc data = new DataProviderJdbc(ModelType.TOUR);
        data.deleteTour(1);
    }


}

