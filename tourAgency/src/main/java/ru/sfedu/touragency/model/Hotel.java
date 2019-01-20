package ru.sfedu.touragency.model;

import java.util.Objects;

public class Hotel {
    private long id;
    private String name;
    private String description;
    private int rate;

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return id == hotel.id &&
                name.equals(hotel.name) &&
                Objects.equals(description, hotel.description) &&
                Objects.equals(rate, hotel.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, rate);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rate=" + rate +
                '}';
    }
}
