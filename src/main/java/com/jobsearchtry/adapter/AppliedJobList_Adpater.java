package com.jobsearchtry.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.JobSeeker_Detail;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.CustomAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//applied job history - list - adapter file
public class AppliedJobList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ArrayList<Jobs> jobApplyHistoryList = new ArrayList<>();
    private int prev_state;
    private final ArrayList<String> selected_fromampm;
    private final String[] select_fromm = {"1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM",
            "7:00 AM", "8:00 AM", "9:00 AM",
            "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM",
            "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM", "12:00 " +
            "AM"};

    public AppliedJobList_Adpater(Activity a,
                                  ArrayList<Jobs> jobApplyHistoryList) {
        activity = a;
        this.jobApplyHistoryList = jobApplyHistoryList;
        selected_fromampm = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            selected_fromampm.add(Integer.toString(i));
        }
    }

    public int getCount() {
        return jobApplyHistoryList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView appliedon, currentstatus, companyname, designation;
        LinearLayout appliedjob_sms, appliedjob_call;
        ImageView applyhis_call,applyhis_email;
        Button email_button,call_button;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jobhistory_row, parent,
                    false);
            holder = new ViewHolder();
            holder.appliedjob_sms = (LinearLayout) convertView
                    .findViewById(R.id.applied_sms);
            holder.applyhis_call = (ImageView) convertView.findViewById(R.id.applyhis_call);
            holder.appliedjob_call = (LinearLayout) convertView
                    .findViewById(R.id.applied_call);
            holder.applyhis_email = (ImageView) convertView.findViewById(R.id.applyhis_email);
            holder.appliedon = (TextView) convertView
                    .findViewById(R.id.appliedon_date);
            holder.email_button = (Button) convertView.findViewById(R.id.email_button);
            holder.call_button = (Button) convertView.findViewById(R.id.call_button);
            holder.currentstatus = (TextView) convertView
                    .findViewById(R.id.currentstatus_value);
            holder.companyname = (TextView) convertView
                    .findViewById(R.id.apply_history_companyname);
            holder.designation = (TextView) convertView
                    .findViewById(R.id.apply_history_designation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.appliedon.setText(" " + jobApplyHistoryList.get(position)
                .getJob_apply_date());
        holder.companyname.setText(jobApplyHistoryList.get(position)
                .getCompanies_profiles_name() + ".,");
        holder.designation.setText(jobApplyHistoryList.get(position)
                .getJob_title());
        if (!jobApplyHistoryList.get(position).getCompany_email().isEmpty()) {
            holder.applyhis_email.setImageResource(R.drawable.email_icon);
            holder.appliedjob_sms.setBackgroundColor(Color.parseColor("#ffc600"));
            holder.appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
            holder.email_button.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.applyhis_email.setImageResource(R.drawable.dis_messagetab_icon);
            holder.email_button.setTextColor(Color.parseColor("#ae984d"));
            holder.appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
            holder.appliedjob_sms.setBackgroundColor(Color.parseColor("#ffe58c"));
        }
        if (jobApplyHistoryList.get(position).getShow_phone_no().equalsIgnoreCase("1")) {
            if (jobApplyHistoryList.get(position).getTime_status().equalsIgnoreCase("N")) {
                holder.appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
            } else {
                String getTime = jobApplyHistoryList.get(position).getSettime();
                String[] out = getTime.split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String currenthour = sdf.format(new Date());
                String fromhr = "1", tohr = "1";
                String getSpecificDays = jobApplyHistoryList.get(position).getSpecificdays();
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = sdf1.format(d);
                if (Arrays.asList(select_fromm).contains(out[0])) {
                    int getpos = Arrays.asList(select_fromm).indexOf(out[0]);
                    fromhr = selected_fromampm.get(getpos);
                }
                if (Arrays.asList(select_fromm).contains(out[1])) {
                    int gettopos = Arrays.asList(select_fromm).indexOf(out[1]);
                    tohr = selected_fromampm.get(gettopos);
                    if ((Integer.parseInt(currenthour) == Integer.parseInt(tohr)) && (Integer.parseInt(currenthour) != 1)) {
                        tohr = Integer.toString(Integer.parseInt(tohr) - 1);
                    }
                }
                if (getSpecificDays != null && !getSpecificDays.isEmpty()) {
                    List<String> specdayslist = Arrays.asList(getSpecificDays.split(","));
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) && (Integer
                            .parseInt(currenthour) <= Integer.parseInt(tohr)) && specdayslist.contains(dayOfTheWeek)) {
                        holder.appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
                        String getPhoneNo = jobApplyHistoryList.get(position).getPhone_no();
                    } else {
                        holder.appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
                    }
                } else {
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                            (Integer.parseInt(currenthour) <= Integer.parseInt(tohr))) {
                        holder.appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
                        String getPhoneNo = jobApplyHistoryList.get(position).getPhone_no();
                    } else {
                        holder.appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
                    }
                }
            }
        } else {
            holder.appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
        }
        String jobStatus;
        if (jobApplyHistoryList.get(position).getStatus().equalsIgnoreCase("A")) {
            jobStatus = activity.getString(R.string.applied);
        } else if (jobApplyHistoryList.get(position).getStatus()
                .equalsIgnoreCase("S")) {
            jobStatus = activity.getString(R.string.shortlisted);
        } else if (jobApplyHistoryList.get(position).getStatus()
                .equalsIgnoreCase("H")) {
            jobStatus = activity.getString(R.string.holded);
        } else if (jobApplyHistoryList.get(position).getStatus()
                .equalsIgnoreCase("B")) {
            jobStatus = activity.getString(R.string.blocked);
        } else {
            jobStatus = activity.getString(R.string.rejected);
        }
        holder.currentstatus.setText(" " + jobStatus);
        holder.appliedjob_sms.setId(position);
        holder.appliedjob_call.setId(position);
        final String jobid = jobApplyHistoryList.get(position).getJob_id();
        //detail view of jobs
        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalData.jobid = jobid;
                GlobalData.favpagefrom = "Fav";
                GlobalData.pageback = "Home";
                Intent in = new Intent(activity, JobSeeker_Detail.class);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(activity);
                Editor editor = sharedPreferences.edit();
                editor.putString("JOB_ID", GlobalData.jobid);
                editor.putString("PAGEBACK", GlobalData.pageback);
                editor.apply();
                activity.startActivity(in);
            }
        });
        //send an sms to employer
        holder.appliedjob_sms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String getCompanyemail = jobApplyHistoryList.get(v.getId()).getCompany_email();
                if (jobApplyHistoryList.get(v.getId()).getCompany_email() != null
                        && !jobApplyHistoryList.get(v.getId())
                        .getCompany_email().isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"
                            + jobApplyHistoryList.get(v.getId())
                            .getCompany_email()));
                    if (jobApplyHistoryList.get(v.getId()).getJob_title() != null
                            && !jobApplyHistoryList.get(v.getId()).getJob_title()
                            .isEmpty()) {
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reg:" + " "
                                + jobApplyHistoryList.get(v.getId()).getJob_title());
                    }
                    activity.startActivity(Intent.createChooser(emailIntent,
                            "Send an email"));
                } else {
                    new CustomAlertDialog().isDisplayMessage(activity, activity.getString(R.string.noemailid));
                }
            }
        });
        //call to employer
        holder.appliedjob_call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalData.jobid = jobApplyHistoryList.get(v.getId())
                        .getJob_id();
                String getPhoneStatus = jobApplyHistoryList.get(v.getId())
                        .getShow_phone_no();
                String getTimeStatus = jobApplyHistoryList.get(v.getId())
                        .getTime_status();
                String getPhoneno = jobApplyHistoryList.get(v.getId())
                        .getPhone_no();
                String getSpecificDays = jobApplyHistoryList.get(v.getId()).getSpecificdays();
                String getcall_toast = jobApplyHistoryList.get(v.getId())
                        .getCall_toast();
                String getTime = jobApplyHistoryList.get(v.getId()).getSettime();
                calltoEmployer(getPhoneStatus, getTimeStatus, getPhoneno, getTime, getSpecificDays,
                        getcall_toast, holder.applyhis_call,holder.appliedjob_call);
            }
        });
        return convertView;
    }

    private void calltoEmployer(String getPhoneStatus, String getTimeStatus, String getPhoneno,
                                String getTime, String getSpecificDays, String getcall_toast, ImageView applyhis_call,
                                LinearLayout appliedjob_call) {
        if (getPhoneStatus.equalsIgnoreCase("1")) {
            if (getTimeStatus.equalsIgnoreCase("N")) {
                appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
                PhoneCallListener phoneListener = new PhoneCallListener();
                TelephonyManager telephonyManager = (TelephonyManager) activity
                        .getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getPhoneno));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission
                            .CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest
                                .permission.CALL_PHONE}, 31);
                    } else {
                        activity.startActivity(callIntent);
                    }
                } else {
                    activity.startActivity(callIntent);
                }
            } else {
                String[] out = getTime.split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String currenthour = sdf.format(new Date());
                String fromhr = "1", tohr = "1";
                if (Arrays.asList(select_fromm).contains(out[0])) {
                    int getpos = Arrays.asList(select_fromm).indexOf(out[0]);
                    fromhr = selected_fromampm.get(getpos);
                }
                if (Arrays.asList(select_fromm).contains(out[1])) {
                    int gettopos = Arrays.asList(select_fromm).indexOf(out[1]);
                    tohr = selected_fromampm.get(gettopos);
                    if ((Integer.parseInt(currenthour) == Integer.parseInt(tohr)) && (Integer.parseInt(currenthour) != 1)) {
                        tohr = Integer.toString(Integer.parseInt(tohr) - 1);
                    }
                }
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = sdf1.format(d);
                if (getSpecificDays != null && !getSpecificDays.isEmpty()) {
                    List<String> specdayslist = Arrays.asList(getSpecificDays.split(","));
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) && (Integer
                            .parseInt(currenthour) <= Integer.parseInt(tohr)) && specdayslist.contains(dayOfTheWeek)) {
                        appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
                        PhoneCallListener phoneListener = new PhoneCallListener();
                        TelephonyManager telephonyManager = (TelephonyManager) activity
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + getPhoneno));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(activity, android.Manifest
                                    .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{android
                                        .Manifest.permission.CALL_PHONE}, 31);
                            } else {
                                activity.startActivity(callIntent);
                            }
                        } else {
                            activity.startActivity(callIntent);
                        }
                    } else {
                        appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
                        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
                        if (!languages.equalsIgnoreCase("English")) {
                            new CustomAlertDialog().isDisplayMessage(activity, getcall_toast);
                        } else {
                            new CustomAlertDialog().isDisplayMessage(activity, getcall_toast);
                        }
                    }
                } else {
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                            (Integer.parseInt(currenthour) <= Integer.parseInt(tohr))) {
                        appliedjob_call.setBackgroundColor(Color.parseColor("#068ec0"));
                        PhoneCallListener phoneListener = new PhoneCallListener();
                        TelephonyManager telephonyManager = (TelephonyManager) activity
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + getPhoneno));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(activity, android.Manifest
                                    .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{android
                                        .Manifest.permission.CALL_PHONE}, 31);
                            } else {
                                activity.startActivity(callIntent);
                            }
                        } else {
                            activity.startActivity(callIntent);
                        }
                    } else {
                        appliedjob_call.setBackgroundColor(Color.parseColor("#8fcce3"));
                        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
                        if (!languages.equalsIgnoreCase("English")) {
                            Toast.makeText(activity,
                                    getcall_toast,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity,
                                    getcall_toast,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(activity,
                    activity.getString(R.string.empoyernotacceptcall),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //outgoing call status
    private class PhoneCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    prev_state = state;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    prev_state = state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if ((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)) {
                        prev_state = state;
                        getCallDetails();
                    }
                    break;
            }
        }
    }

    //get the history of calls for finding the last call duration
    private void getCallDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission
                    .READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest
                        .permission.READ_CALL_LOG}, 33);
            } else {
                StringBuffer sb = new StringBuffer();
                String strOrder = CallLog.Calls.DATE + " DESC";
                Cursor managedCursor = activity.getContentResolver().query(CallLog.Calls
                        .CONTENT_URI, null, null, null, strOrder);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Log :");
                if (managedCursor.moveToFirst()) {
                    String callDuration = managedCursor.getString(duration);
                    if (callDuration.equalsIgnoreCase("0")) {
                        Toast.makeText(activity, activity.getString(R.string.empoyernotacceptcall), Toast
                                .LENGTH_SHORT).show();
                    }
                }
                managedCursor.close();
            }
        }
    }
}