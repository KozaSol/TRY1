package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.MyProfile_EDIT_Education;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Education;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EducationList_Adpater extends BaseAdapter {

    private final Activity activity;
    private ArrayList<Education> educationList = new ArrayList<>();
    private Dialog alertDialog;
    private ProgressDialog pg;

    public EducationList_Adpater(Activity a, ArrayList<Education> educationList) {
        activity = a;
        this.educationList = educationList;
    }

    public int getCount() {
        return educationList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView edu_course, edu_specialisation, emp_institutename, emp_coursetype,
                emp_passedoutyear;
        ImageButton education_editicon, education_deleteicon;
        LinearLayout edu_specialisationlay;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.education_listrow, parent, false);
            holder = new ViewHolder();
            holder.education_editicon = (ImageButton) convertView.findViewById(R.id
                    .education_editicon);
            holder.education_deleteicon = (ImageButton) convertView.findViewById(R.id
                    .education_deleteicon);
            holder.edu_course = (TextView) convertView.findViewById(R.id.edu_course);
            holder.edu_specialisation = (TextView) convertView.findViewById(R.id
                    .edu_specialisation);
            holder.edu_specialisationlay = (LinearLayout) convertView.findViewById(R.id
                    .edu_specialisationlay);
            holder.emp_institutename = (TextView) convertView.findViewById(R.id.emp_institutename);
            holder.emp_coursetype = (TextView) convertView.findViewById(R.id.emp_coursetype);
            holder.emp_passedoutyear = (TextView) convertView.findViewById(R.id.emp_passedoutyear);
            if (educationList.size() > 1) {
                holder.education_deleteicon.setVisibility(View.VISIBLE);
            } else {
                holder.education_deleteicon.setVisibility(View.INVISIBLE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.edu_course.setText(educationList.get(position).getQualification());
        holder.edu_specialisation.setText(educationList.get(position).getSpecilisation());
        holder.emp_institutename.setText(educationList.get(position).getInstitute_name());
        holder.emp_coursetype.setText(educationList.get(position).getCourse_type());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English") &&
                educationList.get(position).getCoursename_local() != null) {
            holder.emp_coursetype.setText(educationList.get(position).getCoursename_local());
        }
        holder.emp_passedoutyear.setText(educationList.get(position).getPassout_year());
        final String educationid = educationList.get(position).getEducation_id();
        if (educationList.get(position).getSpecilisation() == null
                || educationList.get(position).getSpecilisation().isEmpty()) {
            holder.edu_specialisationlay.setVisibility(View.GONE);
        } else {
            holder.edu_specialisationlay.setVisibility(View.VISIBLE);
        }
        holder.education_deleteicon.setId(position);
        holder.education_editicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GlobalData.educationid = educationid;
                Intent in = new Intent(activity, MyProfile_EDIT_Education.class);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(activity);
                Editor editor = sharedPreferences.edit();
                editor.putString("EDU_ID", GlobalData.educationid);
                editor.apply();
                activity.startActivity(in);
            }
        });
        holder.education_deleteicon.setOnClickListener(new OnClickListener() {
            @Override
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
                f_popupsubheader.setText(activity.getResources().getString(R.string
                        .edudeletealert));
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.educationid = educationList.get(v.getId()).getEducation_id();
                final int getid = v.getId();
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        new deleteEducation(getid).execute();
                        alertDialog.dismiss();
                    }
                });
                no.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        return convertView;
    }

    private class deleteEducation extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteEducation(int id) {
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
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            paramsadd.addFormDataPart("education_id", GlobalData.educationid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "education_delete.php")
                    .post(requestBody).build();
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
                    JSONObject responseObj = new JSONObject(deleteresponse);
                    int deletestatus = responseObj.getInt("status_code");
                    String getDeleteStatus = responseObj.getString("status");
                    Toast.makeText(activity, getDeleteStatus, Toast.LENGTH_SHORT).show();
                    if (deletestatus == 1) {
                        educationList.remove(id);
                        notifyDataSetChanged();
                    }
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