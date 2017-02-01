package com.jobsearchtry.wrapper;


public class JobType {
    private String job_type_id, job_type_name, job_type_name_local, isavailable;

    public String getJob_type_id() {
        return job_type_id;
    }

    public void setJob_type_id(String job_type_id) {
        this.job_type_id = job_type_id;
    }

    public String getJob_type_name() {
        return job_type_name;
    }

    public void setJob_type_name(String job_type_name) {
        this.job_type_name = job_type_name;
    }

    public String getJob_type_name_local() {
        return job_type_name_local;
    }

    public void setJob_type_name_local(String job_type_name_local) {
        this.job_type_name_local = job_type_name_local;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JobType)
            return ((JobType) obj).getJob_type_name().equals(this.getJob_type_name());
        return false;
    }
}
