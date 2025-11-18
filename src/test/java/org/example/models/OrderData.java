package org.example.models;

import java.util.Arrays;
import java.util.List;

public class OrderData {
    public String firstName;
    public String lastName;
    public String address;
    public String metroStation;
    public String phone;
    public int rentTime;
    public String deliveryDate;
    public String comment;
    public List<String> color;

    public OrderData(String firstName, String lastName, String address, String metroStation,
                     String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    // Конструктор без цветов
    public OrderData(String firstName, String lastName, String address, String metroStation,
                     String phone, int rentTime, String deliveryDate, String comment) {
        this(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, null);
    }
}

