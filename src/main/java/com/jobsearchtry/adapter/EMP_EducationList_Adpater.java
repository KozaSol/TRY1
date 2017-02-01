package com.jobsearchtry.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Education;

import java.util.ArrayList;

public class EMP_EducationList_Adpater extends BaseAdapter {
	private final Activity activity;
	private ArrayList<Education> educationList = new ArrayList<>();

	public EMP_EducationList_Adpater(Activity a, ArrayList<Education> educationList) {
		activity = a;
		this.educationList = educationList;
	}

	public int getCount() {
		return educationList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView edu_course, edu_specialisation, emp_institutename, emp_coursetype,
				emp_passedoutyear;
		ImageButton education_editicon, education_deleteicon;
		LinearLayout edu_specialisationlay;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
				.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.education_listrow, parent, false);
			holder = new ViewHolder();
			holder.education_editicon = (ImageButton) convertView.findViewById(R.id
					.education_editicon);
			holder.education_deleteicon = (ImageButton) convertView.findViewById(R.id
					.education_deleteicon);
			holder.edu_course = (TextView) convertView.findViewById(R.id.edu_course);
			holder.edu_specialisation = (TextView) convertView.findViewById(R.id
					.edu_specialisation);
			holder.edu_specialisationlay = (LinearLayout) convertView.findViewById(R.id
					.edu_specialisationlay);
			holder.emp_institutename = (TextView) convertView.findViewById(R.id.emp_institutename);
			holder.emp_coursetype = (TextView) convertView.findViewById(R.id.emp_coursetype);
			holder.emp_passedoutyear = (TextView) convertView.findViewById(R.id.emp_passedoutyear);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.edu_course.setText(educationList.get(position).getQualification());
		holder.edu_specialisation.setText(educationList.get(position).getSpecilisation());
		holder.emp_institutename.setText(educationList.get(position).getInstitute_name());
		holder.emp_coursetype.setText(educationList.get(position).getCourse_type());
		String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
		if (!languages.equalsIgnoreCase("English") &&
				educationList.get(position).getCoursename_local() != null) {
			holder.emp_coursetype.setText(educationList.get(position).getCoursename_local());
		}
		holder.emp_passedoutyear.setText(educationList.get(position).getPassout_year());
		if (educationList.get(position).getSpecilisation() == null
				|| educationList.get(position).getSpecilisation().isEmpty()) {
			holder.edu_specialisationlay.setVisibility(View.GONE);
		} else {
			holder.edu_specialisationlay.setVisibility(View.VISIBLE);
		}
		holder.education_deleteicon.setVisibility(View.GONE);
		holder.education_editicon.setVisibility(View.GONE);
		return convertView;
	}
}