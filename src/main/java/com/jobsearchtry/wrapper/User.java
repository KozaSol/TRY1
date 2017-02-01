package com.jobsearchtry.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {

    private String jobseeker_id;
    private String job_id, apply_id;
    private String job_title;
    private String Job_Role, role_name_local;
    private String role_group_id;
    private String Name;
    private String show_my_profile;
    private String Password;
    private String emailid;

    public String getRole_name_local() {
        return role_name_local;
    }

    public void setRole_name_local(String role_name_local) {
        this.role_name_local = role_name_local;
    }

    public String getGender_local() {
        return gender_local;
    }

    public void setGender_local(String gender_local) {
        this.gender_local = gender_local;
    }

    public String getLocation_local() {
        return location_local;
    }

    public void setLocation_local(String location_local) {
        this.location_local = location_local;
    }

    private String Age;
    private String phone;
    private String Gender, gender_local;
    private String GENDER;
    private String Qualification;
    private String APPLIED_DATE;
    private String DateString;
    private String DOB;
    private String Location, location_local;
    private String profile_photo;
    private String desired_location;
    private String profile_title;
    private String years_of_experience;
    private String months_of_exp;
    private String team_handled;
    private String notice_period;
    private String total_exp_years;
    private String modified_date;
    private String Resume;
    private String company_name;
    private String languages;

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    private String previous_company;
    private String response_flag, salary;
    private int status;
    private String last_login;
    private String skill_name;

    public String getDesired_location() {
        return desired_location;
    }

    public void setDesired_location(String desired_location) {
        this.desired_location = desired_location;
    }

    public String getRole_group_id() {
        return role_group_id;
    }

    public void setRole_group_id(String role_group_id) {
        this.role_group_id = role_group_id;
    }

    public String getId() {
        return jobseeker_id;
    }

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public void setId(String id) {
        jobseeker_id = id;
    }

    public String getskill_name() {
        return skill_name;
    }

    public void setskill_name(String skill_name) {
        this.skill_name = skill_name;
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
        this.DateString = dateString;
    }

    public String gettotal_exp_years() {
        return total_exp_years;
    }

    public String getResponse_flag() {
        return response_flag;
    }

    public void setResponse_flag(String response_flag) {
        this.response_flag = response_flag;
    }

    public void settotal_exp_years(String Total_exp_years) {
        total_exp_years = Total_exp_years;
    }

    public String getprevious_company() {
        return previous_company;
    }

    public void setprevious_company(String Previous_company) {
        previous_company = Previous_company;
    }

    public String getJobId() {
        return job_id;
    }

    public void setJobId(String jid) {
        job_id = jid;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String Languages) {
        languages = Languages;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String Job_title) {
        job_title = Job_title;
    }

    public String getUserName() {
        return Name;
    }

    public void setUserName(String userName) {
        Name = userName;
    }

    public String getJob_Role() {
        return Job_Role;
    }

    public void setJob_Role(String job_Role) {
        Job_Role = job_Role;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String Company_name) {
        company_name = Company_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String Profile_photo) {
        profile_photo = Profile_photo;
    }

    public String getResume() {
        return Resume;
    }

    public void setResume(String resume) {
        Resume = resume;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String dob) {
        DOB = dob;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmailId() {
        return emailid;
    }

    public void setEmailId(String emailId) {
        emailid = emailId;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getMobile() {
        return phone;
    }

    public void setMobile(String mobile) {
        phone = mobile;
    }

    public String getSex() {
        return Gender;
    }

    public void setSex(String sex) {
        this.Gender = sex;
    }

    public String getGender() {
        return GENDER;
    }

    public void setGender(String gENDER) {
        this.GENDER = gENDER;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getShowMyProfile() {
        return show_my_profile;
    }

    public void setShowMyProfile(String Show_my_profile) {
        show_my_profile = Show_my_profile;
    }

    public String getProfile_title() {
        return profile_title;
    }

    public void setProfile_title(String Profile_title) {
        profile_title = Profile_title;
    }

    public String getYears_of_experience() {
        return years_of_experience;
    }

    public void setYears_of_experience(String Years_of_experience) {
        years_of_experience = Years_of_experience;
    }

    public String getMonths_of_exp() {
        return months_of_exp;
    }

    public void setMonths_of_exp(String Months_of_exp) {
        months_of_exp = Months_of_exp;
    }

    public String getTeam_handled() {
        return team_handled;
    }

    public void setTeam_handled(String Team_handled) {
        team_handled = Team_handled;
    }

    public String getNotice_period() {
        return notice_period;
    }

    public void setNotice_period(String Notice_period) {
        notice_period = Notice_period;
    }
}
