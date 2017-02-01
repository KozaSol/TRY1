package com.jobsearchtry.wrapper;

public class Employer {

    private String company_id;
    private String company_email;
    private String location;
    private String location_local;
    private String company_short_name;
    private String Password;
    private String Industry;

    public String getEmp_industry_local() {
        return emp_industry_local;
    }

    public void setEmp_industry_local(String emp_industry_local) {
        this.emp_industry_local = emp_industry_local;
    }

    private String emp_industry_local;
    private String phone_no;
    private String show_my_profile;
    private String show_phone_no;
    private String time_status;
    private String settime;
    private String ContactPerson, specificdays;

    public String getLocation_local() {
        return location_local;
    }

    public void setLocation_local(String location_local) {
        this.location_local = location_local;
    }
    public String getSpecificdays() {
        return specificdays;
    }

    public void setSpecificdays(String specificdays) {
        this.specificdays = specificdays;
    }

    public String getTime_status() {
        return time_status;
    }

    public void setTime_status(String time_status) {
        this.time_status = time_status;
    }

    public String getSettime() {
        return settime;
    }

    public void setSettime(String settime) {
        this.settime = settime;
    }

    public String getId() {
        return company_id;
    }

    public void setId(String id) {
        company_id = id;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmailId() {
        return company_email;
    }

    public void setEmailId(String emailId) {
        company_email = emailId;
    }

    public String getIndustry() {
        return Industry;
    }

    public void setIndustry(String industry) {
        Industry = industry;
    }

    public String getMobile() {
        return phone_no;
    }

    public void setMobile(String mobile) {
        phone_no = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String Location) {
        this.location = Location;
    }


    public String getShowMyProfile() {
        return show_my_profile;
    }

    public void setShowMyProfile(String Show_my_profile) {
        show_my_profile = Show_my_profile;
    }

    public String getShowMyPhone() {
        return show_phone_no;
    }

    public void setShowMyPhone(String Show_phone_no) {
        show_phone_no = Show_phone_no;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public String getCompanyName() {
        return company_short_name;
    }

    public void setCompanyName(String name) {
        company_short_name = name;
    }

}
