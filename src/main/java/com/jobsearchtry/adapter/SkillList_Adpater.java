package com.jobsearchtry.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jobsearchtry.MyProfile_EDIT_Skill;
import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Skill;

public class SkillList_Adpater extends BaseAdapter {

	private final Activity activity;
	private ArrayList<Skill> skillList = new ArrayList<>();

	public SkillList_Adpater(Activity a, ArrayList<Skill> skillList) {
		activity = a;
		this.skillList = skillList;
	}

	public int getCount() {
		return skillList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView skill_course;
		ImageButton skill_editicon;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.skills_listrow, parent,
					false);
			holder = new ViewHolder();
			holder.skill_editicon = (ImageButton) convertView
					.findViewById(R.id.skill_editicon);
			holder.skill_course = (TextView) convertView
					.findViewById(R.id.skill_course);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.skill_editicon.setVisibility(View.VISIBLE);
		} else {
			holder.skill_editicon.setVisibility(View.GONE);
		}
		holder.skill_course.setText(skillList.get(position).getSkill_name()
				+ " " + "(" + skillList.get(position).getExperience() + " "
				+ ")");
		holder.skill_editicon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(activity, MyProfile_EDIT_Skill.class);
				activity.startActivity(in);
			}
		});
		return convertView;
	}
}