package com.jobsearchtry.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Jobs {

    private String job_id;
    private String job_title;
    private String role, role_name_local;
    private String minimum_qual, qualification_local;
    private String jobgender, gender_local;
    private String companies_profiles_name;
    private String job_add_date;
    private String location, location_local;
    private String experience, experience_local;
    private String date_diff;
    private String salary;
    private String job_description;
    private String industry;
    private String fav_status;
    private String applystatus;
    private String job_apply_date;
    private String Skills;
    private String jobtoshowflag;
    private String job_offer_type;
    private String job_type_name, jobtype_local;
    private String showphoneflag;
    private String show_phone_no;
    private String phone_no;
    private String status;
    private String view_status;
    private String domain;
    private String notify_me;
    private String message, message_local;
    private String company_id;
    private String country_id;
    private String specilization;
    private String qualification_id;
    private String company_email;
    private String APPLIED_DATE;
    private String DateString;
    private String skill_name;
    private String minimum_qualification_req;
    private String gender_req;
    private String skills_req;
    private String experience_req;
    private String employer_industry_name;
    private String clientname, call_toast;

    public String getSpecificdays() {
        return specificdays;
    }

    public void setSpecificdays(String specificdays) {
        this.specificdays = specificdays;
    }

    private String req_skillname;
    private String languages;
    private String time_status;
    private String settime, specificdays;

    public String getCall_toast() {
        return call_toast;
    }

    public void setCall_toast(String call_toast) {
        this.call_toast = call_toast;
    }

    public String getLocation_local() {
        return location_local;
    }

    public void setLocation_local(String location_local) {
        this.location_local = location_local;
    }

    public String getExperience_local() {
        return experience_local;
    }

    public void setExperience_local(String experience_local) {
        this.experience_local = experience_local;
    }

    public String getRole_name_local() {
        return role_name_local;
    }

    public void setRole_name_local(String role_name_local) {
        this.role_name_local = role_name_local;
    }

    public String getQualification_local() {
        return qualification_local;
    }

    public void setQualification_local(String qualification_local) {
        this.qualification_local = qualification_local;
    }

    public String getGender_local() {
        return gender_local;
    }

    public void setGender_local(String gender_local) {
        this.gender_local = gender_local;
    }

    public String getJobtype_local() {
        return jobtype_local;
    }

    public void setJobtype_local(String jobtype_local) {
        this.jobtype_local = jobtype_local;
    }

    public String getEmployer_industry_name() {
        return employer_industry_name;
    }

    public String getLanguages() {
        return languages;
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

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getClientname() {
        return clientname;
    }

    public String getReq_skillname() {
        return req_skillname;
    }

    public void setReq_skillname(String req_skillname) {
        this.req_skillname = req_skillname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public void setEmployer_industry_name(String employer_industry_name) {
        this.employer_industry_name = employer_industry_name;
    }

    public String getMinimum_qualification_req() {
        return minimum_qualification_req;
    }

    public void setMinimum_qualification_req(String minimum_qualification_req) {
        this.minimum_qualification_req = minimum_qualification_req;
    }

    public String getGender_req() {
        return gender_req;
    }

    public void setGender_req(String gender_req) {
        this.gender_req = gender_req;
    }

    public String getSkills_req() {
        return skills_req;
    }

    public void setSkills_req(String skills_req) {
        this.skills_req = skills_req;
    }

    public String getExperience_req() {
        return experience_req;
    }

    public void setExperience_req(String experience_req) {
        this.experience_req = experience_req;
    }

    public String getQualification_id() {
        return qualification_id;
    }

    public String getCompany_email() {
        return company_email;
    }

    public Date getAPPLIED_DATE() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(APPLIED_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public void setAPPLIED_DATE(String aPPLIED_DATE) {
        APPLIED_DATE = aPPLIED_DATE;
    }

    public Date getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        try {
            return format.parse(DateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public void setDateString(String dateString) {
        DateString = dateString;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email;
    }

    public void setQualification_id(String qualification_id) {
        this.qualification_id = qualification_id;
    }

    private boolean isViewed = false;

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public String getSpecilization() {
        return specilization;
    }

    public void setSpecilization(String specilization) {
        this.specilization = specilization;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getskill_name() {
        return skill_name;
    }

    public void setskill_name(String Skill_name) {
        this.skill_name = Skill_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMinimum_qual() {
        return minimum_qual;
    }

    public void setMinimum_qual(String minimum_qual) {
        this.minimum_qual = minimum_qual;
    }

    public String getJobgender() {
        return jobgender;
    }

    public void setJobgender(String jobgender) {
        this.jobgender = jobgender;
    }

    public String getCompanies_profiles_name() {
        return companies_profiles_name;
    }

    public void setCompanies_profiles_name(String companies_profiles_name) {
        this.companies_profiles_name = companies_profiles_name;
    }

    public Date getJob_add_date() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(job_add_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public String getJob_add_date_toString() {

        return job_add_date;
    }

    public void setJob_add_date(String job_add_date) {
        this.job_add_date = job_add_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDate_diff() {
        return date_diff;
    }

    public void setDate_diff(String date_diff) {
        this.date_diff = date_diff;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getFav_status() {
        return fav_status;
    }

    public void setFav_status(String fav_status) {
        this.fav_status = fav_status;
    }

    public String getApplystatus() {
        return applystatus;
    }

    public void setApplystatus(String applystatus) {
        this.applystatus = applystatus;
    }

    public String getJob_apply_date() {
        return job_apply_date;
    }

    public void setJob_apply_date(String job_apply_date) {
        this.job_apply_date = job_apply_date;
    }

    public String getSkills() {
        return Skills;
    }

    public void setSkills(String skills) {
        Skills = skills;
    }

    public String getJobtoshowflag() {
        return jobtoshowflag;
    }

    public void setJobtoshowflag(String jobtoshowflag) {
        this.jobtoshowflag = jobtoshowflag;
    }

    public String getJob_offer_type() {
        return job_offer_type;
    }

    public void setJob_offer_type(String job_offer_type) {
        this.job_offer_type = job_offer_type;
    }

    public String getJob_type_name() {
        return job_type_name;
    }

    public void setJob_type_name(String job_type_name) {
        this.job_type_name = job_type_name;
    }

    public String getShowphoneflag() {
        return showphoneflag;
    }

    public void setShowphoneflag(String showphoneflag) {
        this.showphoneflag = showphoneflag;
    }

    public String getShow_phone_no() {
        return show_phone_no;
    }

    public void setShow_phone_no(String show_phone_no) {
        this.show_phone_no = show_phone_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getView_status() {
        return view_status;
    }

    public void setView_status(String view_status) {
        this.view_status = view_status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getNotify_me() {
        return notify_me;
    }

    public void setNotify_me(String notify_me) {
        this.notify_me = notify_me;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_local() {
        return message_local;
    }

    public void setMessage_local(String message_local) {
        this.message_local = message_local;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean isViewed) {
        this.isViewed = isViewed;
    }

}
