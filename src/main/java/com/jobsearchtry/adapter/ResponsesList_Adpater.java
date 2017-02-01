package com.jobsearchtry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobsearchtry.Applicant_DetailView;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.User;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ResponsesList_Adpater extends BaseAdapter {
    private final Activity activity;
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_SESSION = 1;
    private static final int MAX_TYPES = 2;
    private ArrayList<SessionViewData> mData;
    private ArrayList<SessionViewData> arraylist;

    public ResponsesList_Adpater(Activity a, ArrayList<User> responsesList) {
        activity = a;
        updateSessionViewData(responsesList);
    }

    private void updateSessionViewData(ArrayList<User> responsesList) {
        Date previousDate = new Date();
        ArrayList<SessionViewData> data = new ArrayList<>();
        for (User session : responsesList) {
            if (!previousDate.equals(session.getDateString())) {
                data.add(new SessionViewData(TYPE_GROUP, session));
                previousDate = session.getDateString();
            }
            data.add(new SessionViewData(TYPE_SESSION, session));
        }
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(data);
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPES;
    }

    public int getCount() {
        return mData.size();
    }

    private static class SessionViewData {
        final int type;
        final User session;

        SessionViewData(int type, User session) {
            this.type = type;
            this.session = session;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        SessionViewData data = mData.get(position);
        if (convertView == null) {
            final int layoutId;
            switch (data.type) {
                case TYPE_GROUP:
                    layoutId = R.layout.responses_header;
                    break;
                case TYPE_SESSION:
                    layoutId = R.layout.responses_row;
                    break;
                default:
                    throw new IllegalArgumentException("Bad type for: " + data.session);
            }
            convertView = mInflater.inflate(layoutId, parent, false);
        }
        switch (data.type) {
            case TYPE_GROUP:
                TextView lblGroupDate = ((TextView) convertView.findViewById(R.id
                        .responselistDate));
                Date date = data.session.getDateString();
                lblGroupDate.setText(getFormattedDate(date.getTime()));
                break;
            case TYPE_SESSION:
                TextView role = (TextView) convertView.findViewById(R.id.response_role);
                TextView response_quali = (TextView) convertView.findViewById(R.id.response_quali);
                TextView experience = (TextView) convertView.findViewById(R.id.response_exp);
                TextView jobtitle = (TextView) convertView.findViewById(R.id.responses_job_title);
                ImageView responserow = (ImageView) convertView.findViewById(R.id.responserow);
                role.setText(" - " + " " + " " + " " + data.session.getJob_Role());
                String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
                if (!languages.equalsIgnoreCase("English") && data.session.getRole_name_local() != null) {
                    role.setText(" - " + " " + " " + " " + data.session.getRole_name_local());
                }
                response_quali.setText(data.session.getQualification() + " " + " " + " - ");
                if (data.session.getYears_of_experience().equalsIgnoreCase("0.0")) {
                    experience.setText("Fresher");
                } else {
                    experience.setText(data.session.getYears_of_experience());
                }
                jobtitle.setText(data.session.getJob_title());
                final String applicantid = data.session.getId();
                final String applicantjobid = data.session.getJobId();
                final String responseflag = data.session.getResponse_flag();
                if (responseflag.equalsIgnoreCase("1")) {
                    jobtitle.setTextColor(Color.parseColor("#474747"));
                    responserow.setBackgroundResource(R.drawable.notify_no);
                } else {
                    jobtitle.setTextColor(Color.parseColor("#006292"));
                    responserow.setBackgroundResource(R.drawable.notify_yes);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        GlobalData.applicantid = applicantid;
                        GlobalData.applicantjobid = applicantjobid;
                        Intent in = new Intent(activity, Applicant_DetailView.class);
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(activity);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("APPLI_ID", GlobalData.applicantid);
                        editor.putString("APPLI_JID", GlobalData.applicantjobid);
                        editor.apply();
                        activity.startActivity(in);
                    }
                });
                break;
        }
        return convertView;
    }

    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        GlobalData.ResKeyword = charText;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        Editor editor = sharedPreferences.edit();
        editor.putString("RESKEY", GlobalData.ResKeyword);
        editor.apply();
        mData.clear();
        if (charText.length() == 0) {
            if (arraylist.size() != 0) {
                mData.addAll(arraylist);
            }
        } else {
            if (arraylist.size() != 0) {
                for (SessionViewData wp : arraylist) {
                    String precompanyname = wp.session.getprevious_company();
                    if (precompanyname == null) {
                        precompanyname = "-";
                    }
                    String skillname = wp.session.getskill_name();
                    if (skillname == null) {
                        skillname = "-";
                    }
                    if (wp.session.getUserName().toLowerCase(Locale.getDefault()).contains
                            (charText)
                            || (precompanyname.toLowerCase(Locale.getDefault()).contains(charText))
                            || (skillname.toLowerCase(Locale.getDefault()).contains(charText))) {
                        mData.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }*/

    private String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) && now.get(Calendar.DATE) ==
                smsTime.get(Calendar.DATE)
                && now.get(Calendar.MONTH) == smsTime.get(Calendar.MONTH)) {
            return "Today";
        } else if ((now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1)
                && now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == smsTime.get(Calendar.MONTH)) {
            return "Yesterday";
        } else {
            SimpleDateFormat format1 = new SimpleDateFormat("MMMM dd yyyy");
            return format1.format(smsTime.getTime());
        }
    }
}