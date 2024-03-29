package ru.sfedu.touragency.api;

import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.log4j.Logger;
import ru.sfedu.touragency.Constants;
import ru.sfedu.touragency.model.*;
import ru.sfedu.touragency.utils.ConfigurationUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DataProviderCsv implements DataProvider {
    private static final Logger LOG = Logger.getLogger(DataProviderCsv.class);
    private String dataSourcePath;
    private ModelType type;

    public void OrderTour(int idUser, int idTour, boolean isPro) {
        Order order = new Order();
        long maxId = getAllIds().stream().reduce(Long::max).orElse((long) 0) + 1;
        order.setId(maxId);
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

        save(order, maxId);
        String message = "Order (id='" + maxId + "')" + " of Tour (id='" + idTour + "') placed by user (id='" + idUser + "')";
        if (isPro) {
            message += " with PRO status";
        }
        LOG.info(message);
    }


    public void bookHotel(int idTour, int idHotel) {
        if (type == ModelType.TOUR) {
            Tour tour = (Tour) getAll().stream().filter(obj -> getId(obj) == idTour).findFirst().orElse(null);
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
            Tour tour = (Tour) getAll().stream().filter(obj -> getId(obj) == idTour).findFirst().orElse(null);

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
            long maxId = getAllIds().stream().reduce(Long::max).orElse((long) 0) + 1;

            tour.setId(maxId);
            tour.setHotel(0);
            tour.setName(name);
            tour.setDescription(desc);
            tour.setDayCount(dayCount);
            tour.setCountry(country);
            tour.setCity(city);
            tour.setPrice(price);
            save(tour, maxId);
            String message = "Added tour '" + tour + "'";
            LOG.info(message);
        } else {
            LOG.info("Failed add tour");
        }
    }


//    public void ArrangeTour(int OrderId, )

    public DataProviderCsv(ModelType type) {
        this.type = type;
        String root;
        try {
            root = ConfigurationUtil.getConfigurationEntry(Constants.CSV_DATA_ROOT);
        } catch (IOException ex) {
            LOG.error(ex);
            return;
        }

        if (Files.notExists(Paths.get(root))) {
            try {
                Files.createDirectory(Paths.get(root));
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }

        // build filepath
        String filename = type.toString();
        dataSourcePath = Paths.get(root, filename + ".csv").toString();
    }

    public static String[] modelToStringArray(Object model, ModelType type) {
        String[] row = null;
        switch (type) {
            case SIMPLE_USER:
                Client client = (Client) model;
                row = new String[5];
                row[0] = String.valueOf(client.getId());
                row[1] = client.getEmail();
                row[2] = client.getPassword();
                row[3] = client.getFirstName();
                row[4] = client.getLastName();
                break;
            case PRO_USER:
                ProClient proClient = (ProClient) model;
                row = new String[7];
                row[0] = String.valueOf(proClient.getId());
                row[1] = proClient.getEmail();
                row[2] = proClient.getPassword();
                row[3] = proClient.getFirstName();
                row[4] = proClient.getLastName();
                row[5] = String.valueOf(proClient.getDiscount());
                row[6] = String.valueOf(proClient.getPoints());
                break;
            case ORDER:
                Order order = (Order) model;
                SimpleDateFormat formater = null;
                try {
                    formater = new SimpleDateFormat(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                row = new String[6];
                row[0] = String.valueOf(order.getId());
                row[1] = String.valueOf(order.getClientId());
                row[2] = String.valueOf(order.getTourId());
                row[3] = String.valueOf(order.getStatus());
                row[4] = String.valueOf(formater.format(order.getDueDate()));
                row[5] = String.valueOf(order.isPro());
                break;
            case HOTEL:
                Hotel hotel = (Hotel) model;
                row = new String[4];
                row[0] = String.valueOf(hotel.getId());
                row[1] = hotel.getName();
                row[2] = hotel.getDescription();
                row[3] = String.valueOf(hotel.getRate());
                break;
            case TOUR:
                Tour tour = (Tour) model;
                row = new String[8];
                row[0] = String.valueOf(tour.getId());
                row[1] = tour.getName();
                row[2] = tour.getDescription();
                row[3] = String.valueOf(tour.getDayCount());
                row[4] = tour.getCountry().toString();
                row[5] = tour.getCity();
                row[6] = String.valueOf(tour.getPrice());
                row[7] = String.valueOf(tour.getHotelId());
        }
        return row;
    }

    public static Object stringArrayToModel(String[] row, ModelType type) {
        switch (type) {
            case SIMPLE_USER:
                Client client = new Client();
                client.setId(Long.parseLong(row[0]));
                client.setEmail(row[1]);
                client.setPassword(row[2]);
                client.setFirstName(row[3]);
                client.setLastName(row[4]);

                return client;
            case PRO_USER:
                ProClient proClient = new ProClient();
                proClient.setId(Long.parseLong(row[0]));
                proClient.setEmail(row[1]);
                proClient.setPassword(row[2]);
                proClient.setFirstName(row[3]);
                proClient.setLastName(row[4]);
                proClient.setDiscount(Integer.parseInt(row[5]));
                proClient.setPoints(Integer.parseInt(row[6]));
                return proClient;
            case ORDER:
                Order order = new Order();
                order.setId(Long.parseLong(row[0]));
                order.setClientId(Long.parseLong(row[1]));
                order.setTourId(Long.parseLong(row[2]));
                order.setStatus(OrderStatus.valueOf(row[3]));
                try {
                    order.setDueDate(Date.valueOf(LocalDate.parse(row[4], DateTimeFormatter.ofPattern(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT)))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                order.setPro(Boolean.valueOf(row[5]));
                return order;
            case HOTEL:
                Hotel hotel = new Hotel();
                hotel.setId(Long.parseLong(row[0]));
                hotel.setName(row[1]);
                hotel.setDescription(row[2]);
                hotel.setRate(Integer.parseInt(row[3]));
                return hotel;
            case TOUR:
                Tour tour = new Tour();
                tour.setId(Long.parseLong(row[0]));
                tour.setName(row[1]);
                tour.setDescription(row[2]);
                tour.setDayCount(Integer.parseInt(row[3]));
                tour.setCountry(Country.valueOf(row[4]));
                tour.setCity(row[5]);
                tour.setPrice(Integer.parseInt(row[6]));
                tour.setHotel(Long.parseLong(row[7]));
                return tour;
        }
        return null;
    }

    @Override
    public long save(Object model) {
        long maxId = getAllIds().stream().reduce(Long::max).orElse((long) 0) + 1;
        return save(model, maxId);
    }

    private long save(Object model, long id) {
        String str = null;
        try {
            str = ConfigurationUtil.getConfigurationEntry(Constants.DELIMITER_CSV);
        } catch (IOException e) {
            e.printStackTrace();
        }
        char ch = str.charAt(0);
        try (FileOutputStream fos = new FileOutputStream(dataSourcePath, true);
             Writer writer = new OutputStreamWriter(fos);
             CSVWriter csvWriter = new CSVWriter(writer, ch)) {
            String[] row = modelToStringArray(model, type);
            if (row != null) {
                row[0] = String.valueOf(id);
                csvWriter.writeNext(row);
            } else {
                LOG.error("Error save csv");
            }
        } catch (IOException ex) {
            LOG.error(ex);
            return -1;
        }
        return id;
    }

    @Override
    public void update(Object model) {
        long id = getId(model);
        if (getById(id) == null) {
            throw new NullPointerException("Id not found");
        }
        delete(id);
        save(model, id);
    }

    private long getId(Object model) {
        switch (type) {
            case SIMPLE_USER:
                return ((Client) model).getId();
            case PRO_USER:
                return ((ProClient) model).getId();
            case ORDER:
                return ((Order) model).getId();
            case TOUR:
                return ((Tour) model).getId();
            case HOTEL:
                return ((Hotel) model).getId();
            default:
                throw new IllegalArgumentException();
        }
    }

    private List<Long> getAllIds() {
        List<Object> all = getAll();
        List<Long> ids;
        ids = all.stream().map(item -> getId(item)).collect(Collectors.toList());
        return ids;
    }

    @Override
    public List<Object> getAll() {
        String str = null;
        try {
            str = ConfigurationUtil.getConfigurationEntry(Constants.DELIMITER_CSV);
        } catch (IOException e) {
            e.printStackTrace();
        }
        char ch = str.charAt(0);
        try (Reader fileReader = new FileReader(dataSourcePath);
             CSVReader reader = new CSVReader(fileReader, ch)) {
            List<Object> models;
            List<String[]> result = reader.readAll();
            models = result.stream().map(item -> stringArrayToModel(item, type)).collect(Collectors.toList());
            return models;
        } catch (IOException ex) {
            LOG.info(ex);
        }
        return new ArrayList<>(); //returns empty list if fail
    }

    @Override
    public Object getById(long id) {
        List<Object> all = getAll();
        return all.stream().filter(model -> id == getId(model)).findFirst().orElse(null);
    }

    @Override
    public void delete(long id) {
        if (getById(id) == null) {
            throw new NullPointerException("Id not found");
        }
        List<Object> all = getAll();
        clear();
        all.stream().filter(mod -> id != getId(mod)).forEach(mdl -> save(mdl, getId(mdl)));
    }

    private void clear() {
        try {
            Files.write(Paths.get(dataSourcePath), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOG.info(e);
        }
    }

    private static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isCounty(String s) {
        try {
            Country.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
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

    private static boolean isBoolean(String s) {
        try {
            Boolean.parseBoolean(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateColumns() throws IOException {
        String str = ConfigurationUtil.getConfigurationEntry(Constants.DELIMITER_CSV);
        char ch = str.charAt(0);
        try (Reader fileReader = new FileReader(dataSourcePath);
             CSVReader reader = new CSVReaderBuilder(fileReader)
                     .withCSVParser(
                             new CSVParserBuilder()
                                     .withSeparator(ch)
                                     .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                                     .build()
                     )
                     .build()) {

            List<String[]> models = reader.readAll();
            for (int i = 0; i < models.size(); i++) {
                if (!isLong(models.get(i)[0])) {
                    LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: id field should be long");
                    throw new IOException();
                }
                for (int j = 0; j < models.get(i).length; j++) {
                    if (models.get(i)[j] == null) {
                        LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Attribute is null at " + (j + 1) + " position");
                        throw new IOException();
                    } else if (models.get(i)[j].equals("")) {
                        LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Attribute is empty at " + (j + 1) + " position");
                        throw new IOException();
                    }
                }
                switch (type) {
                    case PRO_USER:
                        if (models.get(i).length != 7) {
                            LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Row has error");
                            throw new IOException();
                        }
                        break;
                    case SIMPLE_USER:
                        if (models.get(i).length != 5) {
                            LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Row has error");
                            throw new IOException();
                        }
                        break;
                    case ORDER:
                        if (models.get(i).length != 6) {
                            LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Row has error");
                            throw new IOException();
                        }
                        break;
                    case TOUR:
                        if (models.get(i).length != 8) {
                            LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Row has error");
                            throw new IOException();
                        }
                        break;
                    case HOTEL:
                        if (models.get(i).length != 4) {
                            LOG.error("[csv_" + type.toString() + ":" + (i + 1) + "]: Row has error");
                            throw new IOException();
                        }
                        break;
                }
            }

            return true;
        } catch (IOException ex) {
//            LOG.error(ex);
            return false;
        }
    }

    public void validate() throws IOException {
        String str = ConfigurationUtil.getConfigurationEntry(Constants.DELIMITER_CSV);
        char ch = str.charAt(0);
        try (
                Reader fileReader = new FileReader(dataSourcePath);
                CSVReader reader = new CSVReaderBuilder(fileReader)
                        .withCSVParser(
                                new CSVParserBuilder()
                                        .withSeparator(ch)
                                        .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                                        .build()
                        )
                        .build()
        ) {
            List<String[]> models = reader.readAll();
            for (int i = 0; i < models.size(); i++) {
                for (int j = i + 1; j < models.size(); j++) {
                    if (Long.parseLong(models.get(i)[0]) == Long.parseLong(models.get(j)[0])) {
                        LOG.info("[csv_" + type.toString() + "]: Find duplicate id at row '" + (i + 1) + "' and '" + (j + 1) + "'");
//                        break;
                    }
                }
            }

            for (int i = 0; i < models.size(); i++) {
                if (!isLong(models.get(i)[0])) {
                    LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: id field should be long");
                    continue;
                }
                switch (type) {
                    case PRO_USER:
                        if (!isInt(models.get(i)[5])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: discount should be number");
                        }
                        if (!isInt(models.get(i)[6])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: points should be number");
                        }
                    case SIMPLE_USER:
                        String email = models.get(i)[1];
                        if (!email.matches("\\w+@\\w+(\\.\\w+)")) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: wrong email format");
                            continue;
                        }
                        break;
                    case ORDER:
                        if (!isLong(models.get(i)[1])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: clientId should be number");
                        }
                        if (!isLong(models.get(i)[2])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: tourId should be number");
                        }
                        if (isBoolean(models.get(i)[5])) {
                            if (Boolean.parseBoolean(models.get(i)[5])) {
                                if (new DataProviderCsv(ModelType.PRO_USER).getById(Long.parseLong(models.get(i)[1])) == null) {
                                    LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: clientId broken reference");
                                }
                            } else {
                                if (new DataProviderCsv(ModelType.SIMPLE_USER).getById(Long.parseLong(models.get(i)[1])) == null) {
                                    LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: clientId broken reference");
                                }
                            }
                        } else {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: isPro should be boolean");
                        }

                        if (!isLong(models.get(i)[2])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: tourId should be number");
                        }
                        if (new DataProviderCsv(ModelType.TOUR).getById(Long.parseLong(models.get(i)[2])) == null) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: tourId broken reference");
                        }
                        if (!isOrderStatus(models.get(i)[3])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: invalid order status");
                        }
                        if (models.get(i)[4] != null) {
                            SimpleDateFormat sdfrmt = new SimpleDateFormat(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT));
                            sdfrmt.setLenient(false);

                            try {
                                java.util.Date javaDate = sdfrmt.parse(models.get(i)[4]);
                            } catch (ParseException e) {
                                LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: invalid date format");
                            }
                        }
                        break;
                    case HOTEL:
                        if (!isInt(models.get(i)[3])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: rate should be number");
                        }
                        break;
                    case TOUR:
                        if (!isInt(models.get(i)[3])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: dayCount should be number");
                        }
                        if (!isCounty(models.get(i)[4])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: wrong country value");
                        }
                        if (!isInt(models.get(i)[6])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: price should be number");
                        }
                        if (!isLong(models.get(i)[7])) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: clientId should be number");
                        }
                        if (new DataProviderCsv(ModelType.HOTEL).getById(Long.parseLong(models.get(i)[7])) == null) {
                            LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: hotel broken reference");
                        }
                        break;
                }
            }
        } catch (IOException ex) {
            LOG.error(ex);
        }
    }
}
