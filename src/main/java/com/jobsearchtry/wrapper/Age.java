package com.jobsearchtry.wrapper;

/**
 * Created by Administrator on 1/16/2017.
 */
public class Age {
    private String agerange,id,isavailable;

    public String getAgerange() {
        return agerange;
    }

    public void setAgerange(String agerange) {
        this.agerange = agerange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Age)
            return ((Age) obj).getAgerange().equals(this.getAgerange());
        return false;
    }
}

