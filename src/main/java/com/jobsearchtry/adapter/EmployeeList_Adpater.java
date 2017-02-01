package com.jobsearchtry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.EmployeeListing;
import com.jobsearchtry.Homepage;
import com.jobsearchtry.JobSeeker_DetailView;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.User;

import java.util.ArrayList;
import java.util.Locale;

public class EmployeeList_Adpater extends BaseAdapter {
	private final Activity activity;
	private ArrayList<User> employeeList = new ArrayList<>();
	private final ArrayList<User> arraylist;

	public EmployeeList_Adpater(Activity a, ArrayList<User> employeeList) {
		activity = a;
		this.employeeList = employeeList;
		this.arraylist = new ArrayList<>();
		this.arraylist.addAll(employeeList);
	}

	public int getCount() {
		return employeeList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView empname, location, qualification, experience, designation, emp_exp_years;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
				.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.employeelist_row, parent, false);
			holder = new ViewHolder();
			holder.empname = (TextView) convertView.findViewById(R.id.empname);
			holder.location = (TextView) convertView.findViewById(R.id.emp_location_value);
			holder.qualification = (TextView) convertView.findViewById(R.id.sp_quali_value);
			holder.experience = (TextView) convertView.findViewById(R.id.emp_exp_years_value);
			holder.emp_exp_years = (TextView) convertView.findViewById(R.id.emp_exp_years);
			holder.designation = (TextView) convertView.findViewById(R.id.empdesi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.empname.setText(employeeList.get(position).getUserName());
		holder.location.setText(employeeList.get(position).getLocation());
		if (employeeList.get(position).getQualification() != null) {
			holder.qualification.setText(employeeList.get(position).getQualification());
		}
		if (employeeList.get(position).getYears_of_experience().equalsIgnoreCase("0.0")) {
			holder.emp_exp_years.setText("");
			holder.experience.setText(R.string.nbya);
		} else {
			holder.emp_exp_years.setText(R.string.years);
			holder.experience.setText(employeeList.get(position).getYears_of_experience());
		}
		holder.designation.setText(employeeList.get(position).getJob_Role());
		String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
		if (!languages.equalsIgnoreCase("English")) {
			if (employeeList.get(position).getLocation_local() != null) {
				holder.location.setText(employeeList.get(position).getLocation_local());
			}
			if (employeeList.get(position).getRole_name_local() != null) {
				holder.designation.setText(employeeList.get(position).getRole_name_local());
			}
		}
		final String empid = employeeList.get(position).getId();
		convertView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				GlobalData.empid = empid;
				Intent in = new Intent(activity, JobSeeker_DetailView.class);
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(activity);
				Editor editor = sharedPreferences.edit();
				editor.putString("EMP_ID", GlobalData.empid);
				editor.apply();
				activity.startActivity(in);
			}
		});
		return convertView;
	}

	/*public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		employeeList.clear();
		if (charText.length() == 0) {
			if (arraylist.size() != 0) {
				employeeList.addAll(arraylist);
			}
		} else {
			if (arraylist.size() != 0) {
				for (User wp : arraylist) {
					String companyname = wp.getCompany_name();
					if (companyname == null) {
						companyname = "-";
					}
					String precompanyname = wp.getprevious_company();
					if (precompanyname == null) {
						precompanyname = "-";
					}
					String skillname = wp.getskill_name();
					if (skillname == null) {
						skillname = "-";
					}
					String desiredloc = wp.getDesired_location();
					if (desiredloc == null || desiredloc.equalsIgnoreCase("")) {
						desiredloc = "-";
					}
					String location = wp.getLocation();
					if (location == null || location.equalsIgnoreCase("")) {
						location = "-";
					}
					String role = wp.getJob_Role();
					if (role == null || role.equalsIgnoreCase("")) {
						role = "-";
					}
					if (wp.getUserName().toLowerCase(Locale.getDefault()).contains(charText)
							|| (companyname.toLowerCase(Locale.getDefault()).contains(charText)
							|| (precompanyname.toLowerCase(Locale.getDefault()).contains(charText))
							|| (desiredloc.toLowerCase(Locale.getDefault()).contains(charText)) ||
                            (location.toLowerCase(Locale.getDefault()).contains(charText))
							|| (skillname.toLowerCase(Locale.getDefault()).contains(charText)) ||
                            (role.toLowerCase(Locale.getDefault()).contains(charText)))) {
						employeeList.add(wp);
					}
				}
			}
		}
		notifyDataSetChanged();
		final Message msg = new Message();
		final Bundle b = new Bundle();
		b.putInt("KEY", employeeList.size());
		msg.setData(b);
		new Handler(EmployeeListing.callback).sendMessage(msg);
	}*/
}