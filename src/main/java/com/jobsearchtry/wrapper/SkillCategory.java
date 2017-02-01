package com.jobsearchtry.wrapper;

import java.util.ArrayList;

public class SkillCategory {

	private String skillcategoryid;
	private String skillcategoryname;
	private ArrayList<Skill> skills;

	public String getSkillcategoryid() {
		return skillcategoryid;
	}

	public void setSkillcategoryid(String skillcategoryid) {
		this.skillcategoryid = skillcategoryid;
	}

	public String getSkillcategoryname() {
		return skillcategoryname;
	}

	public void setSkillcategoryname(String skillcategoryname) {
		this.skillcategoryname = skillcategoryname;
	}

	public ArrayList<Skill> getSkills() {
		return skills;
	}

	public void setSkills(ArrayList<Skill> skills) {
		this.skills = skills;
	}

}
