import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.sfedu.touragency.api.DataProviderCsv;
import ru.sfedu.touragency.model.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DataProviderCsvTest {
    private static DataProviderCsv clientDataProviderCsv = new DataProviderCsv(ModelType.SIMPLE_USER);
    private static DataProviderCsv proClientDataProviderCsv = new DataProviderCsv(ModelType.PRO_USER);
    private static DataProviderCsv orderDataProviderCsv = new DataProviderCsv(ModelType.ORDER);
    private static DataProviderCsv tourDataProviderCsv = new DataProviderCsv(ModelType.TOUR);
    private static DataProviderCsv hotelDataProviderCsv = new DataProviderCsv(ModelType.HOTEL);

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
            tour1.setHotel(1);
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
        clients.forEach(item -> item.setId(clientDataProviderCsv.save(item)));
        proClients.forEach(item -> item.setId(proClientDataProviderCsv.save(item)));
        orders.forEach(item -> item.setId(orderDataProviderCsv.save(item)));
        tours.forEach(item -> item.setId(tourDataProviderCsv.save(item)));
        hotels.forEach(item -> item.setId(hotelDataProviderCsv.save(item)));


//        System.out.println("saveAndGetAll");
//        clients.forEach(item -> System.out.println(item));
//        proClients.forEach(item -> System.out.println(item));
//        orders.forEach(item -> System.out.println(item));
//        tours.forEach(item -> System.out.println(item));
//        hotels.forEach(item -> System.out.println(item));

        // Считываем записанные данные и смотрим contains
        clientDataProviderCsv.getAll().forEach(item -> {
            assert clients.contains(item);
        });
        proClientDataProviderCsv.getAll().forEach(item -> {
            assert proClients.contains(item);
        });
        orderDataProviderCsv.getAll().forEach(item -> {
            assert orders.contains(item);
        });
        tourDataProviderCsv.getAll().forEach(item -> {
            assert tours.contains(item);
        });
        hotelDataProviderCsv.getAll().forEach(item -> {
            assert hotels.contains(item);
        });
    }

//    @Test
//    public void b_getByIdAndUpdate() {
//        long testId = clients.get(1).getId();
//        Client updClient = (Client) clientDataProviderCsv.getById(testId);
//        ProClient updProClient = (ProClient) proClientDataProviderCsv.getById(testId);
//        Order updOrder = (Order) orderDataProviderCsv.getById(testId);
//        Tour updTour = (Tour) tourDataProviderCsv.getById(testId);
//        Hotel updHotel = (Hotel) hotelDataProviderCsv.getById(testId);
//
//        updClient.setLastName("NewLAST");
//        updProClient.setPoints(3000);
//        updOrder.setStatus(OrderStatus.PAID);
//        updTour.setDescription("New  life");
//        updHotel.setRate(1);
//
//        clientDataProviderCsv.update(updClient);
//        proClientDataProviderCsv.update(updProClient);
//        orderDataProviderCsv.update(updOrder);
//        tourDataProviderCsv.update(updTour);
//        hotelDataProviderCsv.update(updHotel);
//
//
//        assertEquals(updClient, clientDataProviderCsv.getById(testId));
//        assertEquals(updProClient, proClientDataProviderCsv.getById(testId));
//        assertEquals(updOrder, orderDataProviderCsv.getById(testId));
//        assertEquals(updTour, tourDataProviderCsv.getById(testId));
//        assertEquals(updHotel, hotelDataProviderCsv.getById(testId));
//    }

    @Test
    public void c_validate() throws IOException {
        if (
                clientDataProviderCsv.validateColumns() &
                        proClientDataProviderCsv.validateColumns() &
                        orderDataProviderCsv.validateColumns() &
                        tourDataProviderCsv.validateColumns() &
                        hotelDataProviderCsv.validateColumns()) {
            clientDataProviderCsv.validate();
            proClientDataProviderCsv.validate();
            tourDataProviderCsv.validate();
            hotelDataProviderCsv.validate();
            orderDataProviderCsv.validate();
        }
    }

    @Test
    public void d_delete() {
        long testId = clients.get(1).getId();
        clientDataProviderCsv.delete(testId);
        proClientDataProviderCsv.delete(testId);
        orderDataProviderCsv.delete(testId);
        tourDataProviderCsv.delete(testId);
        hotelDataProviderCsv.delete(testId);

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


        clientDataProviderCsv.getAll().forEach(obj -> clientsCheck.add((Client) obj));
        proClientDataProviderCsv.getAll().forEach(obj -> proClientsCheck.add((ProClient) obj));
        orderDataProviderCsv.getAll().forEach(obj -> ordersCheck.add((Order) obj));
        tourDataProviderCsv.getAll().forEach(obj -> toursCheck.add((Tour) obj));
        hotelDataProviderCsv.getAll().forEach(obj -> hotelsCheck.add((Hotel) obj));


        assertEquals(clients, clientsCheck);
        assertEquals(proClients, proClientsCheck);
        assertEquals(orders, ordersCheck);
        assertEquals(tours, toursCheck);
        assertEquals(hotels, hotelsCheck);
    }

    @Test
    public void z_clearall() {
        clientDataProviderCsv.getAll().forEach(obj -> clientDataProviderCsv.delete(((Client) obj).getId()));
        proClientDataProviderCsv.getAll().forEach(obj -> proClientDataProviderCsv.delete(((ProClient) obj).getId()));
        orderDataProviderCsv.getAll().forEach(obj -> orderDataProviderCsv.delete(((Order) obj).getId()));
        tourDataProviderCsv.getAll().forEach(obj -> tourDataProviderCsv.delete(((Tour) obj).getId()));
        hotelDataProviderCsv.getAll().forEach(obj -> hotelDataProviderCsv.delete(((Hotel) obj).getId()));
    }


//    @Test
//    public void orderTour() {
//        DataProviderCsv data = new DataProviderCsv(ModelType.ORDER);
//        data.OrderTour(1,1, false);
////        data.OrderTour(1,1, true);
//    }
//
//    @Test
//    public void bookHotel() {
//        DataProviderCsv data = new DataProviderCsv(ModelType.TOUR);
//        data.bookHotel(1,2);
//    }
//
//    @Test
//    public void updateTour() {
//        DataProviderCsv data = new DataProviderCsv(ModelType.TOUR);
//        data.updateTour(1,10000,30, "testName", "testDesc", Country.RUSSIA, "testCity");
//    }
//
//    @Test
//    public void deleteTour() {
//        DataProviderCsv data = new DataProviderCsv(ModelType.TOUR);
//        data.deleteTour(1);
//    }
//
//    @Test
//    public void addTour() {
//        DataProviderCsv data = new DataProviderCsv(ModelType.TOUR);
//        data.addTour(10000,30, "testName", "testDesc", Country.RUSSIA, "testCity");
//    }


}

