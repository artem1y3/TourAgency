package ru.sfedu.touragency.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import ru.sfedu.touragency.model.Address;





@Entity
@Table(name = "Testbean")
public class TestBean {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    @Column(name = "birth_date", nullable = false)
    private Date birthData;

    @Embedded
    private Address homeAdress;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Address getHomeAdress() {
        return homeAdress;
    }

    public void setHomeAdress(Address homeAdress) {
        this.homeAdress = homeAdress;
    }
}
