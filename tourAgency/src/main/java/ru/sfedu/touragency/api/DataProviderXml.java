package ru.sfedu.touragency.api;

import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.touragency.Constants;
import ru.sfedu.touragency.model.*;
import ru.sfedu.touragency.utils.ConfigurationUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
                save(model);
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
}
