package ru.sfedu.touragency.api;

import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.sfedu.touragency.Constants;
import ru.sfedu.touragency.model.*;
import ru.sfedu.touragency.utils.ConfigurationUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataProviderXml implements DataProvider {
    private static final Logger LOG = Logger.getLogger(DataProviderXml.class);
    private String dataSourcePath;
    ModelType type;

    Persister persister = new Persister();
    public DataProviderXml(ModelType type){
        this.type = type;
        String root;
        try {
            root = ConfigurationUtil.getConfigurationEntry(Constants.XML_DATA_ROOT);
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
        dataSourcePath = Paths.get(root, filename + ".xml").toString();
    }

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
            order.setDueDate(java.sql.Date.valueOf(LocalDate.parse(formater.format(date), DateTimeFormatter.ofPattern(ConfigurationUtil.getConfigurationEntry(Constants.DATE_FORMAT)))));

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

    public boolean validateColumns() throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Load the input XML document, parse it and return an instance of the
            // Document class.
            Document document = builder.parse(dataSourcePath);
            List<String[]> models = new ArrayList<>();
            NodeList entityNodes = document.getDocumentElement().getElementsByTagName("list").item(0).getChildNodes();
            for (int i = 0; i < entityNodes.getLength(); i++) {
                Node entityNode = entityNodes.item(i);
                if (entityNode.getNodeName().equals("object")){
                    NodeList fieldsNodes = entityNode.getChildNodes();
                    List<String> fields = new ArrayList<>();
                    for (int j = 0; j < fieldsNodes.getLength(); j++) {
                        Node fieldNode = fieldsNodes.item(j);
                        if(fieldNode.getNodeType() == Node.ELEMENT_NODE){
                            fields.add(fieldNode.getTextContent());
                        }
                    }
                    models.add(fields.toArray(new String[fields.size()]));
                }
            }

            for (int i = 0; i < models.size(); i++) {
                if (!isLong(models.get(i)[0])) {
                    LOG.info("[csv_" + type.toString() + ":" + (i + 1) + "]: id field should be long");
                    throw new IOException();
                }
                for (int j = 0; j < models.get(i).length; j++) {
                    if (models.get(i)[j] == null) {
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
        } catch (ParserConfigurationException | IOException | SAXException e) {
//            LOG.error(ex);
            return false;
        }
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

    private void setId(Object model, long id){
        switch (type) {
            case SIMPLE_USER:
                ((Client)model).setId(id);
                break;
            case PRO_USER:
                ((ProClient)model).setId(id);
                break;
            case ORDER:
                ((Order)model).setId(id);
                break;
            case TOUR:
                ((Tour)model).setId(id);
                break;
            case HOTEL:
                ((Hotel)model).setId(id);
                break;
        }
    }

    public long save(Object model, long id) {
        List<Object> all = getAll();
        setId(model, id);
        all.add(model);
        XmlList xmlList = new XmlList();
        xmlList.setList(all);
        try {
            persister.write(xmlList, new FileWriter(dataSourcePath, false));
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public List<Object> getAll() {
        try {
            return persister.read(XmlList.class, new File(dataSourcePath)).getList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Object getById(long id) {
        List<Object> all = getAll();
        return all.stream().filter(model -> id == getId(model)).findFirst().orElse(null);
    }

    @Override
    public void delete(long id) {
        if(getById(id) == null){
            return;
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

    private static boolean isLong(String s){
        try{
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    private static boolean isInt(String s){
        try{
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    private static boolean isCounty(String s){
        try{
            Country.valueOf(s);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private static boolean isOrderStatus(String s){
        try{
            OrderStatus.valueOf(s);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private static boolean isBoolean(String s){
        try{
            Boolean.parseBoolean(s);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void validate(){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Load the input XML document, parse it and return an instance of the
            // Document class.
            Document document = builder.parse(dataSourcePath);
            List<String[]> models = new ArrayList<>();
            NodeList entityNodes = document.getDocumentElement().getElementsByTagName("list").item(0).getChildNodes();
            for (int i = 0; i < entityNodes.getLength(); i++) {
                Node entityNode = entityNodes.item(i);
                if (entityNode.getNodeName().equals("object")){
                    NodeList fieldsNodes = entityNode.getChildNodes();
                    List<String> fields = new ArrayList<>();
                    for (int j = 0; j < fieldsNodes.getLength(); j++) {
                        Node fieldNode = fieldsNodes.item(j);
                        if(fieldNode.getNodeType() == Node.ELEMENT_NODE){
                            fields.add(fieldNode.getTextContent());
                        }
                    }
                    models.add(fields.toArray(new String[fields.size()]));
                }
            }

            for (int i = 0; i < models.size(); i++) {
                if(!isLong(models.get(i)[0])){
                    LOG.info("[xml_" + type.toString() + ":" + i + "]: id field should be long");
                }
                switch (type) {
                    case PRO_USER:
                        if (!isInt(models.get(i)[5])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: discount should be number");
                        }
                        if (!isInt(models.get(i)[6])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: points should be number");
                        }
                    case SIMPLE_USER:
                        String email = models.get(i)[1];
                        if(!email.matches("\\w+@\\w+(\\.\\w+)")){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: wrong email format");
                        }
                        break;
                    case ORDER:
                        if (!isLong(models.get(i)[1])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: clientId should be number");
                        }
                        if(isBoolean(models.get(i)[5])){
                            if(Boolean.parseBoolean(models.get(i)[5])){
                                if(new DataProviderXml(ModelType.PRO_USER).getById(Long.parseLong(models.get(i)[1]))==null){
                                    LOG.info("[xml_" + type.toString() + ":" + i + "]: clientId broken reference");
                                }
                            } else {
                                if(new DataProviderXml(ModelType.SIMPLE_USER).getById(Long.parseLong(models.get(i)[1]))==null){
                                    LOG.info("[xml_" + type.toString() + ":" + i + "]: clientId broken reference");
                                }
                            }
                        } else {
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: isPro should be boolean");
                        }

                        if (!isLong(models.get(i)[2])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: tourId should be number");
                        }
                        if(new DataProviderXml(ModelType.TOUR).getById(Long.parseLong(models.get(i)[2]))==null){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: tourId broken reference");
                        }
                        if (!isOrderStatus(models.get(i)[3])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: invalid order status");
                        }
                        break;
                    case HOTEL:
                        if (!isInt(models.get(i)[3])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: rate should be number");
                        }
                        break;
                    case TOUR:
                        if (!isInt(models.get(i)[3])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: dayCount should be number");
                        }
                        if (!isCounty(models.get(i)[4])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: invalid country");
                        }
                        if (!isInt(models.get(i)[6])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: price should be number");
                        }
                        if (!isLong(models.get(i)[7])){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: clientId should be number");
                        }
                        if(new DataProviderXml(ModelType.HOTEL).getById(Long.parseLong(models.get(i)[7]))==null){
                            LOG.info("[xml_" + type.toString() + ":" + i + "]: hotel broken reference");
                        }
                        break;
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(e);
        }
    }

}
