package com.jobsearchtry.wrapper;

public class City {

    private String citi_id, citi_name, citi_county_id, citi_name_local, isavailable;

    public String getCiti_id() {
        return citi_id;
    }

    public String getCity_name_local() {
        return citi_name_local;
    }

    public void setCity_name_local(String citi_name_local) {
        this.citi_name_local = citi_name_local;
    }

    public void setCiti_id(String citi_id) {
        this.citi_id = citi_id;
    }

    public void setCiti_name(String citi_name) {
        this.citi_name = citi_name;
    }

    public String getCiti_name() {
        return citi_name;

    }

    public String getCiti_county_id() {
        return citi_county_id;
    }

    public void setCiti_county_id(String citi_county_id) {
        this.citi_county_id = citi_county_id;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof City)
            return ((City) obj).getCiti_name().equals(this.getCiti_name());
        return false;
    }
}
