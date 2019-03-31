package ru.sfedu.touragency.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "TestBean")
public class TestBean implements Serializable {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    @Column(name = "date_created")
    private Date dateCreated;
//    @Column(name = "check")
//    private boolean check;

//    @Embedded
    @ElementCollection
    private List<TestClientBean> clients;

    public TestBean(int id, String name, String description, Date dateCreated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateCreated = dateCreated;
//        this.check = check;
    }

    public TestBean() {
    }

    public List<TestClientBean> getClients() {
        return clients;
    }

    public void setClients(List<TestClientBean> clients) {
        this.clients = clients;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

//    public boolean isCheck() {
//        return check;
//    }
//
//    public void setCheck(boolean check) {
//        this.check = check;
//    }
}
