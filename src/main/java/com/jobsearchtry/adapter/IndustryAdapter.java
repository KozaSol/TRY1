package com.jobsearchtry.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Industry;

public class IndustryAdapter extends BaseAdapter {

    private final Activity activity;
    private ArrayList<Industry> industriesList = new ArrayList<>();

    public IndustryAdapter(Activity a, ArrayList<Industry>
            industriesList) {
        activity = a;
        this.industriesList = industriesList;
    }

    public int getCount() {
        return industriesList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView spinneritemindustry;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_item_text, parent,
                    false);
            holder = new ViewHolder();
            holder.spinneritemindustry = (TextView) convertView
                    .findViewById(R.id.spinneritemqualification);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.spinneritemindustry.setText(industriesList.get(position)
                .getIndustry_name());
        return convertView;
    }
}