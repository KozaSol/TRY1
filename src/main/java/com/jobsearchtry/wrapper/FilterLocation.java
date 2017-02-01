package com.jobsearchtry.wrapper;

import java.util.ArrayList;

public class FilterLocation {

    private String id, state_name;
    private ArrayList<City> cities;

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

}
