package ru.sfedu.touragency.api;

import ru.sfedu.touragency.model.ModelType;

import java.util.List;

public interface DataProvider {
    long save(Object obj);
    void update(Object obj);
    List<Object> getAll();
    Object getById(long id);
    void delete(long id);
}
