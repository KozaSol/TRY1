package com.jobsearchtry.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Role;

public class RoleAdapter extends BaseAdapter {

    private final Activity activity;
    private ArrayList<Role>  select_role = new ArrayList<>();

    public RoleAdapter(Activity a, ArrayList<Role> select_role) {
        activity = a;
        this.select_role = select_role;
    }

    public int getCount() {
        return select_role.size();
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
        holder.spinneritemspeci.setText(select_role.get(position)
                .getRole_name());
        return convertView;
    }
}