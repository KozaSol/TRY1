package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.app.Dialog;

import com.jobsearchtry.Homepage;
import com.jobsearchtry.MyFavorites;
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

public class MyFavJobList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ProgressDialog pg;
    private ArrayList<Jobs> jobHistoryList = new ArrayList<>();
    private Dialog alertDialog;

    public MyFavJobList_Adpater(Activity a, ArrayList<Jobs> jobHistoryList) {
        activity = a;
        this.jobHistoryList = jobHistoryList;
    }

    public int getCount() {
        return jobHistoryList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView date, location, companyname, experience, designation;
        ImageView favjob_delete;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.myfavjobs_row, parent,
                    false);
            holder = new ViewHolder();
            holder.favjob_delete = (ImageView) convertView
                    .findViewById(R.id.favjob_delete);
            holder.date = (TextView) convertView.findViewById(R.id.myfav_date);
            holder.location = (TextView) convertView
                    .findViewById(R.id.myfav_location);
            holder.companyname = (TextView) convertView
                    .findViewById(R.id.myfav_companyname);
            holder.experience = (TextView) convertView
                    .findViewById(R.id.myfav_experience);
            holder.designation = (TextView) convertView
                    .findViewById(R.id.myfav_designation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.date.setText(jobHistoryList.get(position)
                .getJob_add_date_toString());
        holder.location.setText(jobHistoryList.get(position).getLocation());
        holder.companyname.setText(jobHistoryList.get(position)
                .getCompanies_profiles_name() + ".,");
        holder.experience.setText("Exp : "
                + jobHistoryList.get(position).getExperience());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English")) {
            if (jobHistoryList.get(position).getLocation_local() != null) {
                holder.location.setText(jobHistoryList.get(position).getLocation_local());
            }
            if (jobHistoryList.get(position).getExperience_local() != null) {
                holder.experience.setText(activity.getString(R.string.experience) + " : " +
                        jobHistoryList.get(position).getExperience_local());
            }
        }
        holder.designation.setText(jobHistoryList.get(position).getJob_title());
        holder.favjob_delete.setId(position);
        holder.favjob_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View emppromptView = View.inflate(activity,
                        R.layout.delete_popup, null);
                alertDialog = new Dialog(activity,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(activity.getResources().getString(R.string.deleteconfirm));
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader.setText(activity.getResources().getString(R.string.removejobfav));
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.jobid = jobHistoryList.get(v.getId()).getJob_id();
                final int getid = v.getId();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (new UtilService().isNetworkAvailable(activity)) {
                            new deleteJob(getid).execute();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.checkconnection), Toast
                                    .LENGTH_SHORT).show();
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
            RequestBody formBody = new FormBody.Builder().add("job_id", GlobalData.jobid)
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder().url(GlobalData.url + "forgot_fav.php").post
                    (formBody).build();
            OkHttpClient client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
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
                    jobHistoryList.remove(id);
                    if (jobHistoryList.size() == 0) {
                        new Handler(MyFavorites.callback).sendEmptyMessage(0);
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