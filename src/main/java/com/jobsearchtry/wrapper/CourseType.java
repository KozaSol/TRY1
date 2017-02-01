package com.jobsearchtry.wrapper;


public class CourseType {
    String coursename, coursename_local;

    public String getCoursename_local() {
        return coursename_local;
    }

    public void setCoursename_local(String coursename_local) {
        this.coursename_local = coursename_local;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CourseType)
            return ((CourseType) obj).getCoursename().equals(this.getCoursename());
        return false;
    }
}
