package com.jobsearchtry.wrapper;

public class Specialization {

	private String od_id,od_occupations_list_id,occupations_list_name;
	private String od_name;

	public String getOccupations_list_name() {
		return occupations_list_name;
	}

	public void setOccupations_list_name(String occupations_list_name) {
		this.occupations_list_name = occupations_list_name;
	}

	public String getSpeciali_id() {
		return od_id;
	}

	public String getOd_occupations_list_id() {
		return od_occupations_list_id;
	}

	public void setOd_occupations_list_id(String od_occupations_list_id) {
		this.od_occupations_list_id = od_occupations_list_id;
	}

	public void setSpeciali_id(String od_id) {
		this.od_id = od_id;
	}

	public String getSpeciali_name() {
		return od_name;
	}

	public void setSpeciali_name(String od_name) {
		this.od_name = od_name;
	}

}
