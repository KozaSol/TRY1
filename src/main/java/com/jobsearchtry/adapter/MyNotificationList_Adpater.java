package com.jobsearchtry.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.JobSeeker_Detail;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyNotificationList_Adpater extends BaseAdapter {
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_SESSION = 1;
    private static final int MAX_TYPES = 2;
    private ArrayList<SessionViewData> mData;
    private final Activity activity;

    public MyNotificationList_Adpater(Activity a, ArrayList<Jobs> sessionData) {
        activity = a;
        updateSessionViewData(sessionData);
    }

    private void updateSessionViewData(ArrayList<Jobs> sessionData) {
        Date previousDate = new Date();
        ArrayList<SessionViewData> data = new ArrayList<>();
        for (Jobs session : sessionData) {
            if (!previousDate.equals(session.getDateString())) {
                data.add(new SessionViewData(TYPE_GROUP, session));
                previousDate = session.getDateString();
            }
            data.add(new SessionViewData(TYPE_SESSION, session));
        }
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        SessionViewData data = mData.get(position);
        if (convertView == null) {
            final int layoutId;
            switch (data.type) {
                case TYPE_GROUP:
                    layoutId = R.layout.notification_header;
                    break;
                case TYPE_SESSION:
                    layoutId = R.layout.notificationlist_row;
                    break;
                default:
                    throw new IllegalArgumentException("Bad type for: "
                            + data.session);
            }
            convertView = mInflater.inflate(layoutId, parent, false);
        }
        switch (data.type) {
            case TYPE_GROUP:
                TextView lblGroupDate = ((TextView) convertView
                        .findViewById(R.id.notificationlistDate));
                Date date = data.session.getDateString();
                lblGroupDate.setText(getFormattedDate(activity, date.getTime()));
                break;
            case TYPE_SESSION:
                ImageView notifyid = (ImageView) convertView
                        .findViewById(R.id.notifyid);
                TextView message = (TextView) convertView
                        .findViewById(R.id.notification_message);
                if (data.session.getView_status().equalsIgnoreCase("1")) {
                    notifyid.setBackgroundResource(R.drawable.msg_unread);
                    message.setTextColor(Color.parseColor("#006292"));
                } else {
                    notifyid.setBackgroundResource(R.drawable.msg_read);
                    message.setTextColor(Color.parseColor("#474747"));
                }
                String messaged = data.session.getMessage();
                if (messaged.length() > 0) {
                    String splitted[] = messaged.split(",", 2);
                    message.setText(splitted[1]);
                    String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
                    if (!languages.equalsIgnoreCase("English")) {
                        if (data.session.getMessage_local() != null) {
                            message.setText(data.session.getMessage_local());
                        }
                    }
                }
                final String jobid = data.session.getJob_id();
                final String jobstatus = data.session.getJobtoshowflag();
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (jobstatus.equalsIgnoreCase("3")) {
                            Toast.makeText(activity, "Job has expired", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            GlobalData.jobid = jobid;
                            GlobalData.favpagefrom = "NOTI";
                            Intent in = new Intent(activity, JobSeeker_Detail.class);
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(activity);
                            Editor editor = sharedPreferences.edit();
                            editor.putString("JOB_ID", GlobalData.jobid);
                            editor.apply();
                            activity.startActivity(in);
                            //activity.finish();
                        }
                    }
                });
                break;
        }
        return convertView;
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

    @Override
    public int getCount() {
        return mData.size();
    }

    private static class SessionViewData {
        final int type;
        final Jobs session;

        SessionViewData(int type, Jobs session) {
            this.type = type;
            this.session = session;
        }
    }

    private String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)
                && now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)
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