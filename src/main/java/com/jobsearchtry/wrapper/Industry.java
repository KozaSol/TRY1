package com.jobsearchtry.wrapper;


import java.util.ArrayList;

public class Industry {
    private String industry_name;
    private String industry_id, isavailable;

    public String getIndustry_name_local() {
        return industry_name_local;
    }

    public void setIndustry_name_local(String industry_name_local) {
        this.industry_name_local = industry_name_local;
    }

    private String industry_name_local;
    private ArrayList<Role> role_name;

    public String getIndustry_name() {
        return industry_name;
    }

    public void setIndustry_name(String industry_name) {
        this.industry_name = industry_name;
    }

    public String getIndustry_id() {
        return industry_id;
    }

    public void setIndustry_id(String industry_id) {
        this.industry_id = industry_id;
    }

    public ArrayList<Role> getRole() {
        return role_name;
    }

    public void setRole(ArrayList<Role> role_name) {
        this.role_name = role_name;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Industry)
            return ((Industry) obj).getIndustry_name().equals(this.getIndustry_name());
        return false;
    }
}
