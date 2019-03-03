package ru.sfedu.touragency.api;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class XmlList {

    @ElementList
    public List<Object> list;

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }
}
