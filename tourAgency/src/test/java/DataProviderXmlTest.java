import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.sfedu.touragency.api.DataProviderXml;
import ru.sfedu.touragency.model.*;


import java.io.IOException;
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
        clients.stream().forEach(item -> item.setId(clientDataProviderXml.save(item)));
        proClients.stream().forEach(item -> item.setId(proClientDataProviderXml.save(item)));
        orders.stream().forEach(item -> item.setId(orderDataProviderXml.save(item)));
        tours.stream().forEach(item -> item.setId(tourDataProviderXml.save(item)));
        hotels.stream().forEach(item -> item.setId(hotelDataProviderXml.save(item)));


//        System.out.println("saveAndGetAll");
//        clients.forEach(item -> System.out.println(item));
//        proClients.forEach(item -> System.out.println(item));
//        orders.forEach(item -> System.out.println(item));
//        tours.forEach(item -> System.out.println(item));
//        hotels.forEach(item -> System.out.println(item));

        // Считываем записанные данные и смотрим contains
        clientDataProviderXml.getAll().stream().forEach(item -> {
            assert clients.contains(item);
        });
        proClientDataProviderXml.getAll().stream().forEach(item -> {
            assert proClients.contains(item);
        });
        orderDataProviderXml.getAll().stream().forEach(item -> {
            assert orders.contains(item);
        });
        tourDataProviderXml.getAll().stream().forEach(item -> {
            assert tours.contains(item);
        });
        hotelDataProviderXml.getAll().stream().forEach(item -> {
            assert hotels.contains(item);
        });
    }


    @Test
    public void b_getByIdAndUpdate() {
        long testId = clients.get(1).getId();
        Client updClient = (Client) clientDataProviderXml.getById(testId);
        ProClient updProClient = (ProClient) proClientDataProviderXml.getById(testId);
        Order updOrder = (Order) orderDataProviderXml.getById(testId);
        Tour updTour = (Tour) tourDataProviderXml.getById(testId);
        Hotel updHotel = (Hotel) hotelDataProviderXml.getById(testId);

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


        assertEquals(updClient, clientDataProviderXml.getById(testId));
        assertEquals(updProClient, proClientDataProviderXml.getById(testId));
        assertEquals(updOrder, orderDataProviderXml.getById(testId));
        assertEquals(updTour, tourDataProviderXml.getById(testId));
        assertEquals(updHotel, hotelDataProviderXml.getById(testId));
    }

    @Test
    public void c_validate() throws IOException {
        if (
                clientDataProviderXml.validateColumns() &
                proClientDataProviderXml.validateColumns() &
                orderDataProviderXml.validateColumns() &
                tourDataProviderXml.validateColumns() &
                hotelDataProviderXml.validateColumns()) {
            clientDataProviderXml.validate();
            proClientDataProviderXml.validate();
            tourDataProviderXml.validate();
            hotelDataProviderXml.validate();
            orderDataProviderXml.validate();
        }
    }

    @Test
    public void d_delete() {
        long testId = clients.get(1).getId();
        clientDataProviderXml.delete(testId);
        proClientDataProviderXml.delete(testId);
        orderDataProviderXml.delete(testId);
        tourDataProviderXml.delete(testId);
        hotelDataProviderXml.delete(testId);

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

        clientDataProviderXml.getAll().forEach(obj -> clientsCheck.add((Client) obj));
        proClientDataProviderXml.getAll().forEach(obj -> proClientsCheck.add((ProClient) obj));
        orderDataProviderXml.getAll().forEach(obj -> ordersCheck.add((Order) obj));
        tourDataProviderXml.getAll().forEach(obj -> toursCheck.add((Tour) obj));
        hotelDataProviderXml.getAll().forEach(obj -> hotelsCheck.add((Hotel) obj));


        assertEquals(clients, clientsCheck);
        assertEquals(proClients, proClientsCheck);
        assertEquals(orders, ordersCheck);
        assertEquals(tours, toursCheck);
        assertEquals(hotels, hotelsCheck);
    }
    @Test
    public void z_clearall(){
        clientDataProviderXml.getAll().forEach(obj -> clientDataProviderXml.delete(((Client) obj).getId()));
        proClientDataProviderXml.getAll().forEach(obj -> proClientDataProviderXml.delete(((ProClient) obj).getId()));
        orderDataProviderXml.getAll().forEach(obj -> orderDataProviderXml.delete(((Order) obj).getId()));
        tourDataProviderXml.getAll().forEach(obj -> tourDataProviderXml.delete(((Tour) obj).getId()));
        hotelDataProviderXml.getAll().forEach(obj -> hotelDataProviderXml.delete(((Hotel) obj).getId()));
    }


//    @Test
//    public void orderTour() {
//        DataProviderXml data = new DataProviderXml(ModelType.ORDER);
//        data.OrderTour(1, 1, false);
////        data.OrderTour(1,1, true);
//    }

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

