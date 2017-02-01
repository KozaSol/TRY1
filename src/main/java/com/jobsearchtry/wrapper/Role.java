package com.jobsearchtry.wrapper;


import java.util.ArrayList;

public class Role {
    private String role_name;
    private String role_id,isavailable;

    public String getRole_name_local() {
        return role_name_local;
    }

    public void setRole_name_local(String role_name_local) {
        this.role_name_local = role_name_local;
    }

    private String role_name_local;
    private String role_group_id;
    private ArrayList<Skill> skills;

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getRole_id() {
        return role_id;
    }

    public String getRole_group_id() {
        return role_group_id;
    }

    public void setRole_group_id(String role_group_id) {
        this.role_group_id = role_group_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }

    public String getIsavailable() {
        return isavailable;
    }

    public void setIsavailable(String isavailable) {
        this.isavailable = isavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role)
            return ((Role) obj).getRole_name().equals(this.getRole_name());
        return false;
    }
}

