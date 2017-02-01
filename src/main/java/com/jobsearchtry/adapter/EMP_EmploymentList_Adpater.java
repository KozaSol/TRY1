package com.jobsearchtry.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.Employment;

import java.util.ArrayList;

public class EMP_EmploymentList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ArrayList<Employment> emplymentList = new ArrayList<>();

    public EMP_EmploymentList_Adpater(Activity a,
                                      ArrayList<Employment> emplymentList) {
        activity = a;
        this.emplymentList = emplymentList;
    }

    public int getCount() {
        return emplymentList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView emp_designation, emp_companyname, emp_fromtodate,
                emp_role_val, emp_industry_val;
        ImageButton employment_editicon, employment_deleteicon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.employment_listrow,
                    parent, false);
            holder = new ViewHolder();
            holder.employment_deleteicon = (ImageButton) convertView
                    .findViewById(R.id.employment_deleteicon);

            holder.employment_editicon = (ImageButton) convertView
                    .findViewById(R.id.employment_editicon);
            holder.emp_companyname = (TextView) convertView
                    .findViewById(R.id.emp_companyname);
            holder.emp_fromtodate = (TextView) convertView
                    .findViewById(R.id.emp_fromtodate);
            holder.emp_designation = (TextView) convertView
                    .findViewById(R.id.emp_designation);
            holder.emp_role_val = (TextView) convertView
                    .findViewById(R.id.emp_role_val);
            holder.emp_industry_val = (TextView) convertView
                    .findViewById(R.id.emp_industry_val);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.emp_designation.setText(emplymentList.get(position)
                .getJob_title());
        holder.emp_companyname.setText(emplymentList.get(position)
                .getCompany_name());
        if (emplymentList.get(position).getCurrently_work_here()
                .equalsIgnoreCase("1")) {
            holder.emp_fromtodate.setText(emplymentList.get(position).getStart_year() + " "
                            + emplymentList.get(position).getStart_month() + " - "
                            + " " + "Present");
        } else {
            holder.emp_fromtodate.setText(emplymentList.get(position).getStart_year() + " "
                    + emplymentList.get(position).getStart_month() + " - "
                    + " " + emplymentList.get(position).getEnd_year() + " "
                    + emplymentList.get(position).getEnd_month());
        }
        holder.emp_role_val.setText(emplymentList.get(position)
                .getRole());
        holder.emp_industry_val.setText(emplymentList.get(position)
                .getManufacturing());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English")) {
            if (emplymentList.get(position).getEmp_role_name_local() != null) {
                holder.emp_role_val.setText(emplymentList.get(position).getEmp_role_name_local());
            }
            if (emplymentList.get(position).getEmp_industry_local() != null) {
                holder.emp_industry_val.setText(emplymentList.get(position).getEmp_industry_local());
            }
        }
        holder.employment_editicon.setVisibility(View.GONE);
        holder.employment_deleteicon.setVisibility(View.GONE);
        return convertView;
    }
}