package com.example.taxiapp.model;

import java.util.List;

public class Company {
    private String name;
    private String icon;

    public Company(String name, String icon, List<Contact> contacts, List<Driver> drivers) {
        this.name = name;
        this.icon = icon;
        this.contacts = contacts;
        this.drivers = drivers;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    List<Contact> contacts;

    public List<Driver> getDrivers() {
        return drivers;
    }

    List<Driver> drivers;

    public String getName() {
        return name;
    }

    public String getIcon()
    {
        return icon;
    }
}
