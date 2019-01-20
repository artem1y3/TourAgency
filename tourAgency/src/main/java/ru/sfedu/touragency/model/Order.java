package ru.sfedu.touragency.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Order {
    private long id;
    private long clientId;
    private long tourId;
    private OrderStatus status;
    private Date dueDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getTourId() {
        return tourId;
    }

    public void setTourId(long tourId) {
        this.tourId = tourId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id &&
                clientId == order.clientId &&
                tourId == order.tourId &&
                status == order.status &&
                dueDate.equals(order.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, tourId, status, dueDate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", tourId=" + tourId +
                ", status=" + status +
                ", dueDate=" + dueDate +
                '}';
    }



}
