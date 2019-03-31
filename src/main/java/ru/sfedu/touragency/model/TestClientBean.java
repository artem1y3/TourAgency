package ru.sfedu.touragency.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.util.Date;

@Embeddable
public class TestClientBean {
//    @Id
//    @Column(name = "id")
//    private long id;
    @Column(name = "lastName", nullable = false, length = 100)
    private String lastName;
    @Column(name = "birthData", nullable = false)
    private Date birthData;

    public TestClientBean (String lastName, Date birthData) {
//        this.id = id;
        this.lastName = lastName;
        this.birthData = birthData;
    }

    public TestClientBean() {
    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthData() {
        return birthData;
    }

    public void setBirthData(Date birthData) {
        this.birthData = birthData;
    }
}
