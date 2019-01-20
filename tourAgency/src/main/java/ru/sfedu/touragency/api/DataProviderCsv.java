package ru.sfedu.touragency.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
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

public class DataProviderCsv implements DataProvider {
    private static final Logger LOG = Logger.getLogger(DataProviderCsv.class);
    private String dataSourcePath;
    private ModelType type;


    public DataProviderCsv(ModelType type){
        this.type = type;
        String root;
        try {
            root = ConfigurationUtil.getConfigurationEntry(Constants.CSV_DATA_ROOT);
        } catch (IOException ex) {
            LOG.error(ex);
            return;
        }

        if(Files.notExists(Paths.get(root))){
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

    public static String[] modelToStringArray(Object model, ModelType type){
        String[] row = null;
        switch(type){
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
                SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                row = new String[5];
                row[0] = String.valueOf(order.getId());
                row[1] = String.valueOf(order.getClientId());
                row[2] = String.valueOf(order.getTourId());
                row[3] = String.valueOf(order.getStatus());
                row[4] = String.valueOf(formater.format(order.getDueDate()));
                break;
            case HOTEL:
                Hotel hotel = (Hotel)model;
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

    public static Object stringArrayToModel(String[] row, ModelType type){
        switch(type){
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
                order.setDueDate(Date.valueOf(LocalDate.parse(row[4], DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

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
        long maxId = 0;
        for(Long id : getAllIds()) {
            if(id > maxId){
                maxId = id;
            }
        }
        long id = maxId + 1;
        return save(model, id);
    }

     private long save(Object model, long id) {
        try(FileOutputStream fos = new FileOutputStream(dataSourcePath, true);
            Writer writer = new OutputStreamWriter(fos);
            CSVWriter csvWriter = new CSVWriter(writer))
        {
            String[] row = modelToStringArray(model, type);
            if (row != null){
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
        if(getById(id) == null){
            return;
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

    private List<Long> getAllIds(){
        List<Object> all = getAll();
        List<Long> ids = new ArrayList<>();
        for(Object model : all){
            ids.add(getId(model));
        }
        return ids;
    }

    @Override
    public List<Object> getAll() {
        try(Reader fileReader = new FileReader(dataSourcePath);
            CSVReader reader = new CSVReader(fileReader)) {
                List<Object> models = new ArrayList<>();
                List<String[]> result = reader.readAll();
                for(String[] row: result) {
                    models.add(stringArrayToModel(row, type));
                }
            return models;
        } catch (IOException ex) {
            LOG.error(ex);
        }
        return new ArrayList<>(); //returns empty list if fail
    }

    @Override
    public Object getById(long id) {
        List<Object> all = getAll();
        for(Object model : all) {
            if(id == getId(model)){
                return model;
            }
        }
        return null;
    }

    @Override
    public void delete(long id) {
        if(getById(id) == null){
            return;
        }
        List<Object> all = getAll();
        clear();
        for (Object model : all){
            if(id != getId(model)){
                save(model, getId(model));
            }
        }
    }

    private void clear() {
        try {
            Files.write(Paths.get(dataSourcePath), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOG.info(e);
        }
    }
}
