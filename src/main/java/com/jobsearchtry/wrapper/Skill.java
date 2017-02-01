package com.jobsearchtry.wrapper;

public class Skill {
	private String skill_id;
	private String skill_name;
	private String skillcategory;
	private String skillcategoryid;
	private String experience;
	private String jobseeker_id;

	public String getSkill_id() {
		return skill_id;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getskillcategory() {
		return skillcategory;
	}

	public void setskillcategory(String skillcategory) {
		this.skillcategory = skillcategory;
	}

	public String getJobseeker_id() {
		return jobseeker_id;
	}

	public void setJobseeker_id(String jobseeker_id) {
		this.jobseeker_id = jobseeker_id;
	}

	public void setSkill_id(String skill_id) {
		this.skill_id = skill_id;
	}

	public String getSkill_name() {
		return skill_name;
	}

	public void setSkill_name(String skill_name) {
		this.skill_name = skill_name;
	}

	public String getSkillcategoryid() {
		return skillcategoryid;
	}

	public void setSkillcategoryid(String skillcategoryid) {
		this.skillcategoryid = skillcategoryid;
	}

}
