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

import com.jobsearchtry.Employer_Favourties;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.User;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmpFavJobList_Adpater extends BaseAdapter {
    private final Activity activity;
    private ProgressDialog pg;
    private ArrayList<User> EmpFavHistoryList = new ArrayList<>();
    private Dialog alertDialog;

    public EmpFavJobList_Adpater(Activity a, ArrayList<User> EmpFavHistoryList) {
        activity = a;
        this.EmpFavHistoryList = EmpFavHistoryList;
    }

    public int getCount() {
        return EmpFavHistoryList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView name, role, qualification, experience;
        ImageView favjob_delete;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.empfavjobs_row, parent, false);
            holder = new ViewHolder();
            holder.favjob_delete = (ImageView) convertView.findViewById(R.id.emp_favjob_delete);
            holder.name = (TextView) convertView.findViewById(R.id.myfav_name);
            holder.role = (TextView) convertView.findViewById(R.id.myfav_role);
            holder.qualification = (TextView) convertView.findViewById(R.id.emp_my_quali);
            holder.experience = (TextView) convertView.findViewById(R.id.emp_myfav_exp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(EmpFavHistoryList.get(position).getUserName());
        holder.role.setText(EmpFavHistoryList.get(position).getJob_Role());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English")) {
            if (EmpFavHistoryList.get(position).getRole_name_local() != null) {
                holder.role.setText(EmpFavHistoryList.get(position).getRole_name_local());
            }
        }
        if (EmpFavHistoryList.get(position).getQualification() != null && !EmpFavHistoryList.get
                (position).getQualification().isEmpty()) {
            holder.qualification.setText(activity.getString(R.string.quali) + EmpFavHistoryList.get(position)
                    .getQualification());
        } else {
            holder.qualification.setText(R.string.qualina);
        }
        if (EmpFavHistoryList.get(position).getYears_of_experience().equalsIgnoreCase("0 Yrs") &&
                EmpFavHistoryList.get(position).getMonths_of_exp().equalsIgnoreCase("0 Months")) {
            holder.experience.setText(R.string.expna);
        } else {
            holder.experience.setText(activity.getString(R.string.exp)+" : " + EmpFavHistoryList.get(position)
                    .getYears_of_experience() + " " + " - "
                    + " " + EmpFavHistoryList.get(position).getMonths_of_exp());
        }
        holder.favjob_delete.setId(position);
        holder.favjob_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View emppromptView = View.inflate(activity, R.layout.delete_popup,
                        null);
                alertDialog = new Dialog(activity,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.d_popupheader);
                f_popupheader.setText(activity.getResources().getString(R.string.deleteconfirm));
                TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                        .d_popup_subheader);
                f_popupsubheader.setText(activity.getResources().getString(R.string.empfavremove));
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.empid = EmpFavHistoryList.get(v.getId()).getId();
                final int getid = v.getId();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        new deleteJob(getid).execute();
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
            RequestBody formBody = new FormBody.Builder().add("action", "remove_fav")
                    .add("employer_id", GlobalData.emp_login_status).add("jobseeker_id",
                            GlobalData.empid).build();
            Request request = new Request.Builder().url(GlobalData.url + "favourite_jobseeker" +
                    ".php").post(formBody)
                    .build();
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
            if (deleteresponse != null && !deleteresponse.contains("connectionFailure")) {
                try {
                    EmpFavHistoryList.remove(id);
                    if (EmpFavHistoryList.size() == 0) {
                        new Handler(Employer_Favourties.callback).sendEmptyMessage(0);
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(activity, activity.getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}