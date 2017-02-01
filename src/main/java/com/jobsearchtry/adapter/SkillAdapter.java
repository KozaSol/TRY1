package com.jobsearchtry.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Skill;
import java.util.ArrayList;

public class SkillAdapter extends BaseAdapter {
	private final Activity activity;
	private ArrayList<Skill> skillList = new ArrayList<>();

	public SkillAdapter(Activity a, int spinnerItemText,
						ArrayList<Skill> skillList) {
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
		TextView spinneritemspeci;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) activity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.specialization_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.spinneritemspeci = (TextView) convertView
					.findViewById(R.id.spinneritemspeci);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.spinneritemspeci.setText(skillList.get(position)
				.getSkill_name());
		return convertView;
	}

}
