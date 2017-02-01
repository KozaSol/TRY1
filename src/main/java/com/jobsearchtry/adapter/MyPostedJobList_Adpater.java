package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.JobsPosted;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPostedJobList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ArrayList<Jobs> jobPostedList = new ArrayList<>();
    private ProgressDialog pg;
    private Dialog alertDialog;

    public MyPostedJobList_Adpater(Activity a, ArrayList<Jobs> jobPostedList) {
        activity = a;
        this.jobPostedList = jobPostedList;
    }

    public int getCount() {
        return jobPostedList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView date, designation, jobstatus, location;
        ImageView delete;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jobsposted_row, parent,
                    false);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.pj_date);
            holder.location = (TextView) convertView
                    .findViewById(R.id.pj_location);
            holder.designation = (TextView) convertView
                    .findViewById(R.id.pj_designation);
            holder.jobstatus = (TextView) convertView
                    .findViewById(R.id.pj_activestatus);
            holder.delete = (ImageView) convertView
                    .findViewById(R.id.pj_job_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.date.setText(activity.getString(R.string.postedon) + " : " +
                jobPostedList.get(position).getJob_add_date_toString());
        holder.designation.setText(jobPostedList.get(position).getJob_title());
        holder.location.setText(jobPostedList.get(position).getLocation());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English") && !jobPostedList.get(position).getLocation_local().isEmpty()
                && jobPostedList.get(position).getLocation_local() != null) {
            holder.location.setText(jobPostedList.get(position).getLocation_local());
        }
        String jobgetstatus;
        if (jobPostedList.get(position).getJobtoshowflag()
                .equalsIgnoreCase("2")) {
            jobgetstatus = activity.getString(R.string.posted);
        } else if (jobPostedList.get(position).getJobtoshowflag()
                .equalsIgnoreCase("1")) {
            jobgetstatus = activity.getString(R.string.active);
        } else {
            jobgetstatus = activity.getString(R.string.inactive);
        }
        holder.jobstatus.setText(activity.getString(R.string.status) + " : " + jobgetstatus);
        holder.delete.setId(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View emppromptView = View.inflate(activity,
                        R.layout.delete_popup, null);
                alertDialog = new Dialog(activity,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.deleteconfirm);
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader
                        .setText(R.string.deletejobsposted);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.ejobid = jobPostedList.get(v.getId()).getJob_id();
                final int getid = v.getId();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (new UtilService().isNetworkAvailable(activity)) {
                            new deleteJob(getid).execute();
                        } else {
                            Toast.makeText(activity,
                                    activity.getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                    .show();
                        }
                        alertDialog.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        return convertView;
    }

    private class deleteJob extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteJob(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(activity, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("employer_id", GlobalData.emp_login_status)
                    .add("job_id", GlobalData.ejobid).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "delete_job.php").post(formBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                deleteresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (deleteresponse != null
                    && !deleteresponse.contains("connectionFailure")) {
                try {
                    jobPostedList.remove(id);
                    if (jobPostedList.size() == 0) {
                        new Handler(JobsPosted.callback).sendEmptyMessage(0);
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(activity, activity.getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.connectionfailure),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}