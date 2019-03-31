package ru.sfedu.touragency.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Objects;

@Entity(name = "ProClient")
@PrimaryKeyJoinColumn(name = "client_id")
public class ProClient extends Client {
    private int discount;
    private int points;

    public ProClient () {

    }

    public ProClient (long id, String email, String password, String firstName, String lastName, int discount, int points) {
        super (id, email, password, firstName, lastName);
        this.discount = discount;
        this.points = points;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProClient proClient = (ProClient) o;
        return points == proClient.points &&
                Objects.equals(discount, proClient.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), discount, points);
    }

    @Override
    public String toString() {
        return "ProClient{" +
                "discount=" + discount +
                ", points=" + points +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
