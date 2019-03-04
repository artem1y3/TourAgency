package ru.sfedu.touragency.api;

import org.apache.log4j.Logger;
import ru.sfedu.touragency.Constants;
import ru.sfedu.touragency.model.*;
import ru.sfedu.touragency.utils.ConfigurationUtil;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProviderJdbc implements DataProvider, Closeable {
    private static final Logger LOG = Logger.getLogger(DataProviderJdbc.class);
    private ModelType type;
    private String tableName;
    Connection connection = newConnection();

    private static Connection newConnection(){
        try {
            Class.forName(ConfigurationUtil.getConfigurationEntry(Constants.DRIVER_CLASS));
            String jdbc_string = ConfigurationUtil.getConfigurationEntry(Constants.JDBC_URL);
            String user = ConfigurationUtil.getConfigurationEntry(Constants.DB_USER);
            String password = ConfigurationUtil.getConfigurationEntry(Constants.DB_USER_PASSWORD);
            return DriverManager.getConnection(jdbc_string, user, password);
        } catch (Exception e){
            LOG.error(e);
            return null;
        }
    }

    public DataProviderJdbc(ModelType type){
        this.type = type;
        try(Statement st = connection.createStatement()){
            switch (type) {
                case SIMPLE_USER:
                    tableName = "Clients";
                    st.execute("CREATE TABLE IF NOT EXISTS Clients(" +
                            "id SERIAL," +
                            "email VARCHAR(64)," +
                            "password VARCHAR(64)," +
                            "firstName VARCHAR(64)," +
                            "lastName VARCHAR(64)" +
                            ")");
                    break;
                case PRO_USER:
                    tableName = "ProClients";
                    st.execute("CREATE TABLE IF NOT EXISTS ProClients(" +
                            "id SERIAL," +
                            "email VARCHAR(64)," +
                            "password VARCHAR(64)," +
                            "firstName VARCHAR(64)," +
                            "lastName VARCHAR(64)," +
                            "discount INT," +
                            "points INT" +
                            ")");
                    break;
                case ORDER:
                    tableName = "Orders";
                    st.execute("CREATE TABLE IF NOT EXISTS Orders(" +
                            "id SERIAL," +
                            "clientId INT," +
                            "tourId INT," +
                            "status VARCHAR(64)," +
                            "orderDate VARCHAR(64)," +
                            "isPro BOOLEAN" +
                            ")");
                    break;
                case TOUR:
                    tableName = "Tours";
                    st.execute("CREATE TABLE IF NOT EXISTS Tours(" +
                            "id SERIAL," +
                            "tourName VARCHAR(64)," +
                            "description VARCHAR(64)," +
                            "dayCount INT," +
                            "country VARCHAR(64)," +
                            "city VARCHAR(64)," +
                            "price INT," +
                            "hotelId INT" +
                            ")");
                    break;
                case HOTEL:
                    tableName = "Hotels";
                    st.execute("CREATE TABLE IF NOT EXISTS Hotels(" +
                            "id SERIAL," +
                            "hotelName VARCHAR(64)," +
                            "description VARCHAR(64)," +
                            "rate INT" +
                            ")");
                    break;
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    private static String paramsFromStringArray(String[] arr){
        for (int i = 0; i < arr.length; i++) {
            arr[i] = "'" + arr[i] + "'";
        }
        return String.join(",", Arrays.copyOfRange(arr,1, arr.length));
    }


    public void OrderTour(int idUser, int idTour, boolean isPro) {
        long maxId;
        Order order = new Order();
        order.setClientId(idUser);
        order.setTourId(idTour);
        order.setStatus(OrderStatus.SENT);
        order.setPro(isPro);

        java.util.Date date = new java.util.Date();


        SimpleDateFormat formater = null;
        try {
            formater = new SimpleDateFormat(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT));
            order.setDueDate(Date.valueOf(LocalDate.parse(formater.format(date), DateTimeFormatter.ofPattern(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT)))));

        } catch (IOException e) {
            e.printStackTrace();
        }

        maxId = save(order);
        String message = "Order (id='" + maxId + "')" + " of Tour (id='" + idTour + "') placed by user (id='" + idUser + "')";
        if (isPro) {
            message += " with PRO status";
        }
        LOG.info(message);
    }


    public void bookHotel(int idTour, int idHotel) {
        if (type == ModelType.TOUR) {
            Tour tour = (Tour) getById(idTour);
//            Tour tour = (Tour) getAll().stream().filter(obj -> getId(obj) == idTour).findFirst().orElse(null);
            if (tour != null) {
                tour.setHotel(idHotel);
                update(tour);
                String message = "Hotel (id='" + idHotel + "') booked on tour (id='" + idTour + "')";
                LOG.info(message);
            } else {
                LOG.info("Tour (id='" + idTour + "') does not exist");
            }
        } else {
            LOG.info("Failed book");
        }
    }

    public void updateTour(int idTour, int price, int dayCount, String name, String desc, Country country, String city) {
        if (type == ModelType.TOUR) {
            Tour tour = (Tour) getById(idTour);
//            Tour tour = (Tour) getAll().stream().filter(obj -> getId(obj) == idTour).findFirst().orElse(null);

            if (tour != null) {
                tour.setName(name);
                tour.setDescription(desc);
                tour.setDayCount(dayCount);
                tour.setCountry(country);
                tour.setCity(city);
                tour.setPrice(price);
                update(tour);
                String message = "Updated tour data (id='" + idTour + "') to " + tour;
                LOG.info(message);
            } else {
                LOG.info("Tour (id='" + idTour + "') does not exist");
            }
        } else {
            LOG.info("Failed update");
        }
    }

    public void deleteTour(int idTour) {
        if (type == ModelType.TOUR) {
            delete(idTour);
            String message = "Tour (id='" + idTour + "') deleted";
            LOG.info(message);
        } else {
            LOG.info("Failed deleted tour");
        }
    }

    public void addTour(int price, int dayCount, String name, String desc, Country country, String city) {
        if (type == ModelType.TOUR) {
            Tour tour = new Tour();
//            long maxId = getAllIds().stream().reduce(Long::max).orElse((long) 0) + 1;
            long maxId;
            tour.setHotel(0);
            tour.setName(name);
            tour.setDescription(desc);
            tour.setDayCount(dayCount);
            tour.setCountry(country);
            tour.setCity(city);
            tour.setPrice(price);
            maxId = save(tour);
            tour.setId(maxId);
            String message = "Added tour '" + tour + "'";
            LOG.info(message);
        } else {
            LOG.info("Failed add tour");
        }
    }

    @Override
    public long save(Object model) {
        String queryTemplate = "INSERT INTO %s VALUES( DEFAULT,%s)";
        String[] arr = DataProviderCsv.modelToStringArray(model, type);
        String query = String.format(queryTemplate, tableName, paramsFromStringArray(arr));
        try(Statement st = connection.createStatement()) {
             st.execute(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if ( rs.next() ) {
            // Retrieve the auto generated key(s).
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            LOG.error(e);
            return -1;
        }
    }

    @Override
    public void update(Object model) {
        try(Statement st = connection.createStatement()) {
            switch (type) {
                case SIMPLE_USER:
                    Client client = (Client)model;
                    st.executeUpdate(String.format("UPDATE Clients SET email='%s', password='%s', firstName='%s', lastName='%s' WHERE id=%d",
                            client.getEmail(),
                            client.getPassword(),
                            client.getFirstName(),
                            client.getLastName(),
                            client.getId()));
                    break;
                case PRO_USER:
                    ProClient proClient = (ProClient) model;
                    st.executeUpdate(String.format("UPDATE ProClients SET email='%s', password='%s', firstName='%s', lastName='%s', discount=%d, points=%d WHERE id=%d",
                            proClient.getEmail(),
                            proClient.getPassword(),
                            proClient.getFirstName(),
                            proClient.getLastName(),
                            proClient.getDiscount(),
                            proClient.getPoints(),
                            proClient.getId()));
                    break;
                case ORDER:
                    Order order = (Order) model;
                    SimpleDateFormat formater = new SimpleDateFormat(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT));
                    st.executeUpdate(String.format("UPDATE Orders SET clientId=%d, tourId=%d, status='%s', orderDate='%s', isPro='%s' WHERE id=%d",
                            order.getClientId(),
                            order.getTourId(),
                            order.getStatus(),
                            (String) formater.format(order.getDueDate()),
                            Boolean.valueOf(order.isPro()).toString(),
                            order.getId()));
                    break;
                case TOUR:
                    Tour tour = (Tour) model;
                    st.executeUpdate(String.format("UPDATE Tours SET tourName='%s', description='%s', dayCount=%d, country='%s', city='%s', price=%d, hotelId=%d WHERE id=%d",
                            tour.getName(),
                            tour.getDescription(),
                            tour.getDayCount(),
                            tour.getCountry(),
                            tour.getCity(),
                            tour.getPrice(),
                            tour.getHotelId(),
                            tour.getId()));
                    break;

                case HOTEL:
                    Hotel hotel = (Hotel)model;
                    st.executeUpdate(String.format("UPDATE Hotels SET hotelName='%s', description='%s', rate=%d WHERE id=%d",
                            hotel.getName(),
                            hotel.getDescription(),
                            hotel.getRate(),
                            hotel.getId()));
                    break;
            }
        } catch (SQLException e) {
            LOG.error(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validate() throws IOException, SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet rs;

            switch (type) {
                case ORDER:
                    rs = st.executeQuery("SELECT * FROM orders");
                    while (rs.next()) {
                        if (rs.getBoolean(6)) {
                            if (new DataProviderJdbc(ModelType.PRO_USER).getById(rs.getLong(2)) == null) {
                                LOG.info("[csv_" + type.toString() + ":" + (rs.getLong(1)) + "]: clientId broken reference");
                            }
                        } else {
                            if (new DataProviderJdbc(ModelType.SIMPLE_USER).getById(rs.getLong(2)) == null) {
                                LOG.info("[csv_" + type.toString() + ":" + (rs.getLong(1)) + "]: clientId broken reference");
                            }
                        }
                        if (new DataProviderJdbc(ModelType.TOUR).getById(rs.getLong(3)) == null) {
                            LOG.info("[csv_" + type.toString() + ":" + (rs.getLong(1)) + "]: tourId broken reference");
                        }
                        if (!isOrderStatus(rs.getString(4))) {
                            LOG.info("[csv_" + type.toString() + ":" + (rs.getLong(1)) + "]: invalid order status");
                        }
                    }
                    break;
                case TOUR:
                    rs = st.executeQuery("SELECT * FROM tours");
                    while (rs.next()) {
                        if (new DataProviderJdbc(ModelType.HOTEL).getById(rs.getLong(8)) == null) {
                            LOG.info("[csv_" + type.toString() + ":" + rs.getLong(1) + "]: hotel broken reference");
                        }
                    }
                    break;
                case SIMPLE_USER:
                    rs = st.executeQuery("SELECT * FROM clients");
                    while (rs.next()) {
                        String email = rs.getString(2);
                        if (!email.matches("\\w+@\\w+(\\.\\w+)")) {
                            LOG.info("[csv_" + type.toString() + ":" + (rs.getLong(1)) + "]: wrong email format");
                        }
                    }
                    break;
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }

    @Override
    public List<Object> getAll() {
        List<Object> models = new ArrayList<>();
        try(Statement st = connection.createStatement()) {
            ResultSet rs;
            switch (type) {
                case SIMPLE_USER:
                     rs = st.executeQuery("SELECT * FROM Clients");
                    while(rs.next()){
                        Client client = new Client();
                        client.setId(rs.getLong(1));
                        client.setEmail(rs.getString(2));
                        client.setPassword(rs.getString(3));
                        client.setFirstName(rs.getString(4));
                        client.setLastName(rs.getString(5));
                        models.add(client);
                    }
                    break;
                case PRO_USER:
                    rs = st.executeQuery("SELECT * FROM ProClients");
                    while(rs.next()){
                        ProClient proClient = new ProClient();
                        proClient.setId(rs.getLong(1));
                        proClient.setEmail(rs.getString(2));
                        proClient.setPassword(rs.getString(3));
                        proClient.setFirstName(rs.getString(4));
                        proClient.setLastName(rs.getString(5));
                        proClient.setDiscount(rs.getInt(6));
                        proClient.setPoints(rs.getInt(7));
                        models.add(proClient);
                    }
                    break;
                case ORDER:
                    rs = st.executeQuery("SELECT * FROM Orders");
                    while(rs.next()){
                        Order order = new Order();
                        order.setId(rs.getLong(1));
                        order.setClientId(rs.getLong(2));
                        order.setTourId(rs.getLong(3));
                        order.setStatus(OrderStatus.valueOf(rs.getString(4)));
                        order.setDueDate(Date.valueOf(LocalDate.parse( rs.getString(5),DateTimeFormatter.ofPattern(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT)))));
                        order.setPro(rs.getBoolean(6));
                        models.add(order);
                    }
                    break;
                case TOUR:
                    rs = st.executeQuery("SELECT * FROM Tours");
                    while(rs.next()){
                        Tour tour = new Tour();
                        tour.setId(rs.getLong(1));
                        tour.setName(rs.getString(2));
                        tour.setDescription(rs.getString(3));
                        tour.setDayCount(rs.getInt(4));
                        tour.setCountry(Country.valueOf(rs.getString(5)));
                        tour.setCity(rs.getString(6));
                        tour.setPrice(rs.getInt(7));
                        models.add(tour);
                    }
                    break;
                case HOTEL:
                     rs = st.executeQuery("SELECT * FROM Hotels");
                    while(rs.next()){
                        Hotel hotel = new Hotel();
                        hotel.setId(rs.getLong(1));
                        hotel.setName(rs.getString(2));
                        hotel.setDescription(rs.getString(3));
                        hotel.setRate(rs.getInt(4));
                        models.add(hotel);
                    }
                    break;
            }
        } catch (SQLException | IOException e) {
            LOG.error(e);
        }
        return models;
    }

    @Override
    public Object getById(long id) {
        try(Statement st = connection.createStatement()) {
            ResultSet rs;
            switch (type) {
                case SIMPLE_USER:
                    rs = st.executeQuery(String.format("SELECT * FROM Clients WHERE id='%s'", id));
                    while(rs.next()){
                        Client client = new Client();
                        client.setId(rs.getLong(1));
                        client.setEmail(rs.getString(2));
                        client.setPassword(rs.getString(3));
                        client.setFirstName(rs.getString(4));
                        client.setLastName(rs.getString(5));
                        return client;
                    }
                    break;
                case PRO_USER:
                    rs = st.executeQuery(String.format("SELECT * FROM ProClients WHERE id='%s'", id));
                    if(rs.next()){
                        ProClient proClient = new ProClient();
                        proClient.setId(rs.getLong(1));
                        proClient.setEmail(rs.getString(2));
                        proClient.setPassword(rs.getString(3));
                        proClient.setFirstName(rs.getString(4));
                        proClient.setLastName(rs.getString(5));
                        proClient.setDiscount(rs.getInt(6));
                        proClient.setPoints(rs.getInt(7));
                     return proClient;
            }
                    break;
                case ORDER:
                    rs = st.executeQuery(String.format("SELECT * FROM Orders WHERE id='%s'", id));
                    if(rs.next()){
                        Order order = new Order();
                        order.setId(rs.getLong(1));
                        order.setClientId(rs.getLong(2));
                        order.setTourId(rs.getLong(3));
                        order.setStatus(OrderStatus.valueOf(rs.getString(4)));
                        order.setDueDate(Date.valueOf(LocalDate.parse(rs.getString(5),DateTimeFormatter.ofPattern(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT)))));
                        order.setPro(rs.getBoolean(6));
                        return order;
                    }
                    break;
                case TOUR:
                    rs = st.executeQuery(String.format("SELECT * FROM Tours WHERE id='%s'", id));
                    if(rs.next()){
                        Tour tour = new Tour();
                        tour.setId(rs.getLong(1));
                        tour.setName(rs.getString(2));
                        tour.setDescription(rs.getString(3));
                        tour.setDayCount(rs.getInt(4));
                        tour.setCountry(Country.valueOf(rs.getString(5)));
                        tour.setCity(rs.getString(6));
                        tour.setPrice(rs.getInt(7));
                        return tour;
                    }
                    break;
                case HOTEL:
                    rs = st.executeQuery(String.format("SELECT * FROM Hotels WHERE id='%s'", id));
                    if(rs.next()){
                        Hotel hotel = new Hotel();
                        hotel.setId(rs.getLong(1));
                        hotel.setName(rs.getString(2));
                        hotel.setDescription(rs.getString(3));
                        hotel.setRate(rs.getInt(4));
                        return hotel;
                    }
            }
        } catch (SQLException e) {
            LOG.error(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(long id) {
        try(Statement st = connection.createStatement()) {
            switch (type) {
                case SIMPLE_USER:
                    st.executeUpdate(String.format("DELETE FROM Clients WHERE id='%s'", id));
                    break;
                case PRO_USER:
                    st.executeUpdate(String.format("DELETE FROM ProClients WHERE id='%s'", id));
                    break;
                case ORDER:
                    st.executeUpdate(String.format("DELETE FROM Orders WHERE id='%s'", id));
                    break;
                case TOUR:
                    st.executeUpdate(String.format("DELETE FROM Tours WHERE id='%s'", id));
                    break;
                case HOTEL:
                    st.executeUpdate(String.format("DELETE FROM Hotels WHERE id='%s'", id));
                    break;
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    private static boolean isOrderStatus(String s) {
        try {
            OrderStatus.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
