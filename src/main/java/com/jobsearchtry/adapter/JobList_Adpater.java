package com.jobsearchtry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobsearchtry.Homepage;
import com.jobsearchtry.JobSeeker_Detail;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class JobList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ArrayList<Jobs> jobList = new ArrayList<>();
    private final ArrayList<Jobs> arraylist;

    public JobList_Adpater(Activity a, ArrayList<Jobs> jobList) {
        activity = a;
        this.jobList = jobList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(jobList);
    }

    public int getCount() {
        return jobList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView location, companyname, experience, designation, date;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.homepage_listrow, parent,
                    false);
            holder = new ViewHolder();
            holder.location = (TextView) convertView
                    .findViewById(R.id.location);
            holder.companyname = (TextView) convertView
                    .findViewById(R.id.companyname);
            holder.experience = (TextView) convertView
                    .findViewById(R.id.experience);
            holder.designation = (TextView) convertView
                    .findViewById(R.id.designation);
            holder.date = (TextView) convertView
                    .findViewById(R.id.js_posteddate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] separated = jobList.get(position).getLocation().split(",");
        if (separated.length > 1) {
            holder.location.setText(separated[0] + " " + "+" + (separated.length - 1));
        } else {
            holder.location.setText(separated[0]);
        }
        holder.experience.setText(activity.getString(R.string.experience) + " : "
                + jobList.get(position).getExperience());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English")) {
            if (jobList.get(position).getLocation_local() != null) {
                String[] separatedd = jobList.get(position).getLocation_local().split(",");
                if (separatedd.length > 1) {
                    holder.location.setText(separatedd[0] + " " + "+" + (separatedd.length - 1));
                } else {
                    holder.location.setText(separatedd[0]);
                }
            }
            if (jobList.get(position).getExperience_local() != null) {
                holder.experience.setText(activity.getString(R.string.experience) + " : "
                        + jobList.get(position).getExperience_local());
            }
        }

        holder.companyname.setText(jobList.get(position)
                .getCompanies_profiles_name() + ".,");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (activity.getApplicationContext()));
        GlobalData.getViewedJob = sharedPreferences.getString("VJID", GlobalData.getViewedJob);
        if (GlobalData.getViewedJob != null && !GlobalData.getViewedJob.isEmpty()) {
            String[] viewcateadd = GlobalData.getViewedJob.split(",");
            for (String aViewcateadd : viewcateadd) {
                if (!GlobalData.getjobid.contains(aViewcateadd)) {
                    GlobalData.getjobid.add(aViewcateadd);
                }
            }
        }
        if (GlobalData.getjobid.size() > 0) {
            if (GlobalData.getjobid.contains(jobList.get(position).getJob_id())) {
                holder.designation.setTextColor(Color.parseColor("#474747"));
            } else {
                holder.designation.setTextColor(Color.parseColor("#006292"));
            }
        } else {
            holder.designation.setTextColor(Color.parseColor("#006292"));
        }
        holder.designation.setText(jobList.get(position).getJob_title());
        holder.date.setText(jobList.get(position).getJob_add_date_toString());
        final String jobid = jobList.get(position).getJob_id();
        convertView.setId(position);
        convertView.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               GlobalData.jobid = jobid;
                                               if (GlobalData.getSearchKeyword != null && !GlobalData.getSearchKeyword.isEmpty()) {
                                                   if (!GlobalData.jobSuggestions.contains(GlobalData
                                                           .getSearchKeyword)) {
                                                       GlobalData.jobSuggestions.add(GlobalData.getSearchKeyword);
                                                   }
                                               }
                                               if (!GlobalData.getjobid.contains(GlobalData
                                                       .jobid)) {
                                                   GlobalData.getjobid.add(GlobalData.jobid);
                                               }
                                               if (GlobalData.getjobid.size() > 0) {
                                                   String[] getmostviewedrole = new String[GlobalData.getjobid.size()];
                                                   for (int k = 0; k < GlobalData.getjobid.size();
                                                        k++) {
                                                       getmostviewedrole[k] = GlobalData.getjobid.get(k);
                                                   }
                                                   String mostviewedrole = Arrays.toString(getmostviewedrole);
                                                   mostviewedrole = mostviewedrole.substring(1,
                                                           mostviewedrole.length() - 1);
                                                   mostviewedrole = mostviewedrole.replace(", ",
                                                           ",");
                                                   GlobalData.getViewedJob = mostviewedrole;
                                               }
                                               notifyDataSetChanged();
                                               GlobalData.favpagefrom = "Fav";
                                               GlobalData.getrolepage = "DR";
                                               GlobalData.pageback = "Filter";
                                               GlobalData.joblistfrom = "JL";
                                               Intent in = new Intent(activity, JobSeeker_Detail
                                                       .class);
                                               SharedPreferences sharedPreferences = PreferenceManager
                                                       .getDefaultSharedPreferences(activity);
                                               Editor editor = sharedPreferences.edit();
                                               editor.putString("JOB_ID", GlobalData.jobid);
                                               editor.putString("PAGEBACK", GlobalData.pageback);
                                               editor.putString("ROLEPAGE", GlobalData.getrolepage);
                                               editor.putString("JobListFrom", GlobalData
                                                       .joblistfrom);
                                               editor.putString("VJID", GlobalData.getViewedJob);
                                               editor.apply();
                                               activity.startActivity(in);
                                           }
                                       }
        );
        return convertView;
    }

    /*public void filterrole(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        jobList.clear();
        if (charText.length() == 0) {
            if (arraylist.size() != 0) {
                jobList.addAll(arraylist);
            }
        } else {
            if (arraylist.size() != 0) {
                for (Jobs wp : arraylist) {
                    String jobrole = wp.getRole();
                    if (jobrole == null) {
                        jobrole = "-";
                    }
                    if (jobrole.toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        jobList.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }*/
   /* public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        GlobalData.Landingkeyword = charText;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        Editor editor = sharedPreferences.edit();
        editor.putString("LSKEY", GlobalData.Landingkeyword);
        editor.apply();
        jobList.clear();
        if (charText.length() == 0) {
            if (arraylist.size() != 0) {
                jobList.addAll(arraylist);
            }
        } else {
            if (arraylist.size() != 0) {
                for (Jobs wp : arraylist) {
                    String companyname = wp.getCompanies_profiles_name();
                    if (companyname == null) {
                        companyname = "-";
                    }
                    String location = wp.getLocation();
                    if (location == null) {
                        location = "-";
                    }
                    String skillname = wp.getskill_name();
                    if (skillname == null) {
                        skillname = "-";
                    }
                    String jobdesc = wp.getJob_description();
                    if (jobdesc == null) {
                        jobdesc = "-";
                    }
                    String jobrole = wp.getRole();
                    if (jobrole == null) {
                        jobrole = "-";
                    }
                    String clientname = wp.getClientname();
                    if (clientname == null || clientname.isEmpty()) {
                        clientname = "-";
                    }
                    if (wp.getJob_title().toLowerCase(Locale.getDefault())
                            .contains(charText)
                            || (companyname.toLowerCase(Locale.getDefault())
                            .contains(charText)
                            || (location.toLowerCase(Locale.getDefault())
                            .contains(charText)
                            || (jobrole.toLowerCase(Locale.getDefault())
                            .contains(charText) || (jobdesc
                            .toLowerCase(Locale.getDefault())
                            .contains(charText)) || (skillname
                            .toLowerCase(Locale.getDefault())
                            .contains(charText) || (clientname
                            .toLowerCase(Locale.getDefault())
                            .contains(charText))))))) {
                        jobList.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
        final Message msg = new Message();
        final Bundle b = new Bundle();
        b.putInt("KEY", jobList.size());
        msg.setData(b);
        new Handler(Homepage.callback1).sendMessage(msg);
    }*/
}