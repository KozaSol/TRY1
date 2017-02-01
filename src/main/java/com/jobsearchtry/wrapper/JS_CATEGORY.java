package com.jobsearchtry.wrapper;


public class JS_CATEGORY {

    private String role_group_id;
    private String role_name, role_name_tamil;
    private String img;

    public String getRole_id() {
        return role_group_id;
    }

    public void setRole_id(String Role_id) {
        role_group_id = Role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String Role_name) {
        role_name = Role_name;
    }

    public String getRole_name_local() {
        return role_name_tamil;
    }

    public void setRole_name_local(String role_name_tamil) {
        this.role_name_tamil = role_name_tamil;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String Img) {
        img = Img;
    }

}
