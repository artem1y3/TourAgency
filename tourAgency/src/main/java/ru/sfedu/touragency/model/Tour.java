package ru.sfedu.touragency.model;

import java.util.Objects;

public class Tour {
    private long id;
    private String name;
    private String description;
    private int dayCount;
    private Country country;
    private String city;
    private int price;
    private long hotelId;

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

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotel(long hotelId) {
        this.hotelId = hotelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tour tour = (Tour) o;
        return id == tour.id &&
                dayCount == tour.dayCount &&
                name.equals(tour.name) &&
                Objects.equals(description, tour.description) &&
                country == tour.country &&
                Objects.equals(city, tour.city) &&
                Objects.equals(price, tour.price) &&
                hotelId == tour.hotelId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, dayCount, country, city, price, hotelId);
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dayCount=" + dayCount +
                ", country=" + country +
                ", city='" + city + '\'' +
                ", price=" + price +
                ", hotelId=" + hotelId +
                '}';
    }
}
