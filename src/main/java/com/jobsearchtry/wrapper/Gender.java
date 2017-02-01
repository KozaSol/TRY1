package com.jobsearchtry.wrapper;


public class Gender {
    private String genderid, gender, gender_local,isavailable;

    public String getGender_local() {
        return gender_local;
    }

    public void setGender_local(String gender_local) {
        this.gender_local = gender_local;
    }

    public String getGenderid() {
        return genderid;
    }

    public void setGenderid(String genderid) {
        this.genderid = genderid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gender)
            return ((Gender) obj).getGender().equals(this.getGender());
        return false;
    }

}
