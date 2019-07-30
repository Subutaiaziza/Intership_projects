package com.example.taxiapp.model;

import java.util.List;

public class Main {
    boolean isSuccess;
    List<Company> companies;

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public Main(List<Company> companies) {
        this.companies = companies;
    }
}
