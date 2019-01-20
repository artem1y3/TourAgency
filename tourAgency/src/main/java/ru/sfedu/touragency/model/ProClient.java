package ru.sfedu.touragency.model;

import java.util.Objects;

public class ProClient extends Client {
    private int discount;
    private int points;

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
