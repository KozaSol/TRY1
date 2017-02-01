package com.jobsearchtry.wrapper;

import java.util.ArrayList;

public class Qualification {

    private String occupations_list_id, isavailable;
    private String occupations_list_name, occupations_list_name_tamil;
    private ArrayList<Specialization> specialization;

    public String getOccupations_list_name_tamil() {
        return occupations_list_name_tamil;
    }

    public void setOccupations_list_name_tamil(String occupations_list_name_tamil) {
        this.occupations_list_name_tamil = occupations_list_name_tamil;
    }

    public ArrayList<Specialization> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(ArrayList<Specialization> spcialization) {
        this.specialization = spcialization;
    }

    public String getId() {
        return occupations_list_id;
    }

    public void setId(String id) {
        this.occupations_list_id = id;
    }

    public String getQuali_name() {
        return occupations_list_name;
    }

    public void setQuali_name(String qualiname) {
        this.occupations_list_name = qualiname;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }
}
