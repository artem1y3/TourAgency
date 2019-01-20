package ru.sfedu.touragency;

import org.apache.log4j.Logger;
import ru.sfedu.touragency.api.DataProvider;
import ru.sfedu.touragency.api.DataProviderCsv;
import ru.sfedu.touragency.api.DataProviderJdbc;
import ru.sfedu.touragency.api.DataProviderXml;
import ru.sfedu.touragency.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private static final Logger LOG = Logger.getLogger(DataProviderCsv.class);
    private static Scanner sc = new Scanner(System.in);

    private static String getDataSourceType(){
        while (true){
            LOG.info("Choose data source type (csv, xml, jdbc):");
            String input = sc.nextLine().toLowerCase();
            if (input.equals("csv") || input.equals("xml") || input.equals("jdbc")){
                return input;
            }
            LOG.info("Wrong data source type: '" + input + "'");
        }
    }

    private static String[] actions = {"save", "update","print_all", "select", "delete", "exit"};
    private static String getAction(){
        while (true){
            LOG.info("Choose action (" + String.join(",", actions) + "):");
            String input = sc.nextLine().toLowerCase();
            if(Arrays.asList(actions).contains(input)){
                return input;
            }
            LOG.info("Wrong action: '" + input + "'");
        }
    }

    private static String[] modelTypes; // set in initCli

    private static ModelType getModelType(){
        while (true){
            LOG.info("Choose model (" + String.join(",", modelTypes) + "):");
            String input = sc.nextLine();
            if(Arrays.asList(modelTypes).contains(input)){
                return ModelType.valueOf(input);
            }
            LOG.info("Wrong model: '" + input + "'");
        }
    }

    private static void initCli(){
        // init modelTypes variable
        ModelType[] allModelTypes = ModelType.values();
        modelTypes = new String[allModelTypes.length];
        for(int i = 0; i < modelTypes.length; i++) {
            modelTypes[i] = allModelTypes[i].toString();
        }
    }

    public static void main(String[] args){
        initCli();
        String dsType = getDataSourceType();

        while (true){
            String action = getAction();
            if (action.equals("exit")){
                LOG.info("Bye!");
                return;
            }

            DataProvider provider = null;
            ModelType modelType = getModelType();
            switch (dsType){
                case "csv":
                    provider = new DataProviderCsv(modelType);
                    break;
                case "xml":
                    provider = new DataProviderXml(modelType);
                    break;
                case "jdbc":
                    provider = new DataProviderJdbc(modelType);
            }
            switch (action) {
                case "save":
                    try {
                        switch (modelType) {
                            case SIMPLE_USER:
                                long id = provider.save(getClient(false));
                                LOG.info("Client was saved, id: " + id);
                                break;
                            case PRO_USER:
                                id = provider.save(getProClient(false));
                                LOG.info("ProClient was saved, id: " + id);
                                break;
                            case ORDER:
                                Order order = getOrder(false);
                                id = provider.save(order);
                                LOG.info("Order was saved, id: " + id);
                                break;
                            case TOUR:
                                Tour tour = getTour(false);
                                id = provider.save(tour);
                                LOG.info("Tour was saved, id: " + id);
                                break;
                            case HOTEL:
                                Hotel hotel = getHotel(false);
                                id = provider.save(hotel);
                                LOG.info("Hotel was saved, id: " + id);
                                break;
                        }
                        break;
                    }catch (Exception ex){
                        LOG.error(ex);
                        continue;
                    }
                case "update":
                    try {
                        switch (modelType) {
                            case SIMPLE_USER:
                                Client client = getClient(true);
                                provider.update(client);
                                LOG.info("Client was updated, id: " + client.getId());
                                break;
                            case PRO_USER:
                                ProClient proClient = getProClient(true);
                                provider.update(proClient);
                                LOG.info("ProClient was updated, id: " + proClient.getId());
                                break;
                            case ORDER:
                                Order order = getOrder(true);
                                provider.update(order);
                                LOG.info("Order was updated, id: " + order.getId());
                                break;
                            case TOUR:
                                Tour tour = getTour(true);
                                provider.update(tour);
                                LOG.info("Tour was updated, id: " + tour.getId());
                                break;
                            case HOTEL:
                                Hotel hotel = getHotel(true);
                                provider.update(hotel);
                                LOG.info("Hotel was updated, id: " + hotel.getId());
                                break;
                        }
                        break;
                    } catch (Exception ex){
                        LOG.error(ex);
                        continue;
                    }
                case "print_all":
                    List<Object> all = provider.getAll();
                    if (all == null){
                        LOG.info("nothing was found");
                    } else {
                        all.forEach(LOG::info);
                    }
                    break;
                case "select":
                    LOG.info("Input id:");
                    long id = sc.nextInt();
                    sc.nextLine();
                    Object model = provider.getById(id);
                    if (model == null){
                        LOG.info("nothing was found");
                    } else {
                        LOG.info(model.toString());
                    }
                    break;
                case "delete":
                    LOG.info("Input id:");
                    long deleteId = sc.nextInt();
                    sc.nextLine();
                    provider.delete(deleteId);
                    LOG.info("Model deleted: " + deleteId);
                    break;
            }
        }
    }

    private static Client getClient(boolean update){
        while (true) {
            int paramsCount;
            if (update) {
                LOG.info("Input comma-separated id, email, password, firstName, lastName");
                paramsCount = 5;
            } else {
                LOG.info("Input comma-separated email, password, firstName, lastName");
                paramsCount = 4;
            }
            String[] arr = sc.nextLine().split(",");
            if(arr.length != paramsCount){
                LOG.info("Wrong count of params");
                continue;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            if(!update){
                String[] clientStringArray = new String[arr.length + 1];
                clientStringArray[0] = "0";
                System.arraycopy(arr, 0, clientStringArray,1, arr.length);
                arr = clientStringArray;
            }
            return (Client) DataProviderCsv.stringArrayToModel(arr, ModelType.SIMPLE_USER);
        }
    }


    private static ProClient getProClient(boolean update){
        while (true) {
            int paramsCount;
            if (update) {
                LOG.info("Input comma-separated id, email, password, firstName, lastName, discount, points");
                paramsCount = 7;
            } else {
                LOG.info("Input comma-separated email, password, firstName, lastName, discount, points");
                paramsCount = 6;
            }
            String[] arr = sc.nextLine().split(",");
            if(arr.length != paramsCount){
                LOG.info("Wrong count of params");
                continue;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            if(!update){
                String[] proClientStringArray = new String[arr.length + 1];
                proClientStringArray[0] = "0";
                System.arraycopy(arr, 0, proClientStringArray,1, arr.length);
                arr = proClientStringArray;
            }
            return (ProClient) DataProviderCsv.stringArrayToModel(arr, ModelType.PRO_USER);
        }
    }

    private static Order getOrder(boolean update){
        while (true) {
            int paramsCount;
            if (update) {
                LOG.info("Input comma-separated id, clientId, tourId, status (SENT or PAID), date (DD.MM.YYYY)");
                paramsCount = 5;
            } else {
                LOG.info("Input comma-separated clientId, tourId, status (SENT or PAID), date (DD.MM.YYYY)");
                paramsCount = 4;
            }
            String[] arr = sc.nextLine().split(",");
            if(arr.length != paramsCount){
                LOG.info("Wrong count of params");
                continue;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }

            if(!update){
                String[] orderStringArray = new String[arr.length + 1];
                orderStringArray[0] = "0";
                System.arraycopy(arr, 0, orderStringArray,1, arr.length);
                arr = orderStringArray;
            }

            try{
                OrderStatus.valueOf(arr[3].toUpperCase());
            }
            catch (Exception ex){
                LOG.info("Wrong status param");
                continue;
            }
            return (Order) DataProviderCsv.stringArrayToModel(arr, ModelType.ORDER);
        }
    }
    
    private static Tour getTour(boolean update){
        while (true) {
            int paramsCount;
            if (update) {
                LOG.info("Input comma-separated id, name, description, dayCount, country(Russia,France and Norway allowed), city, price, hotelId");
                paramsCount = 8;
            } else {
                LOG.info("Input comma-separated name, description, dayCount, country(Russia,France and Norway allowed), city, price, hotelId");
                paramsCount = 7;
            }
            String[] arr = sc.nextLine().split(",");
            if(arr.length != paramsCount){
                LOG.info("Wrong count of params");
                continue;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            if(!update){
                String[] tourStringArray = new String[arr.length + 1];
                tourStringArray[0] = "0";
                System.arraycopy(arr, 0, tourStringArray,1, arr.length);
                arr = tourStringArray;
            }
            arr[4]= arr[4].toUpperCase();
            try{
                Country.valueOf(arr[4]);
            }
            catch (Exception ex){
                LOG.info("Wrong country param");
                continue;
            }
            return (Tour) DataProviderCsv.stringArrayToModel(arr, ModelType.TOUR);
        }
    }

    private static Hotel getHotel(boolean update){
        while (true) {
            int paramsCount;
            if (update) {
                LOG.info("Input comma-separated id, name, description, rate");
                paramsCount = 4;
            } else {
                LOG.info("Input comma-separated name, description, rate");
                paramsCount = 3;
            }
            String[] arr = sc.nextLine().split(",");
            if(arr.length != paramsCount){
                LOG.info("Wrong count of params");
                continue;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            if(!update){
                String[] clientStringArray = new String[arr.length + 1];
                clientStringArray[0] = "0";
                System.arraycopy(arr, 0, clientStringArray,1, arr.length);
                arr = clientStringArray;
            }
            return (Hotel) DataProviderCsv.stringArrayToModel(arr, ModelType.HOTEL);
        }
    }
}
