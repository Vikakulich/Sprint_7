package org.example.models;

public class CourierData {
    public String login;
    public String password;
    public String firstName;

    public CourierData(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public CourierData(String login, String password) {
        this.login = login;
        this.password = password;
    }
}

