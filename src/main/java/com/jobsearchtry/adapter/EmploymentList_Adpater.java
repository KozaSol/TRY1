package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.MyProfileActivity;
import com.jobsearchtry.MyProfile_EDIT_Employment;
import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Employment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmploymentList_Adpater extends BaseAdapter {
    private ProgressDialog pg;
    private final Activity activity;
    private ArrayList<Employment> emplymentList = new ArrayList<>();
    private Dialog alertDialog;

    public EmploymentList_Adpater(Activity a, ArrayList<Employment> emplymentList) {
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
        TextView emp_designation, emp_companyname, emp_fromtodate, emp_role_val, emp_industry_val;
        ImageButton employment_editicon, employment_deleteicon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.employment_listrow, parent, false);
            holder = new ViewHolder();
            holder.employment_deleteicon = (ImageButton) convertView.findViewById(R.id
                    .employment_deleteicon);
            holder.employment_editicon = (ImageButton) convertView.findViewById(R.id
                    .employment_editicon);
            holder.emp_companyname = (TextView) convertView.findViewById(R.id.emp_companyname);
            holder.emp_fromtodate = (TextView) convertView.findViewById(R.id.emp_fromtodate);
            holder.emp_designation = (TextView) convertView.findViewById(R.id.emp_designation);
            holder.emp_role_val = (TextView) convertView.findViewById(R.id.emp_role_val);
            holder.emp_industry_val = (TextView) convertView.findViewById(R.id.emp_industry_val);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.emp_designation.setText(emplymentList.get(position).getJob_title());
        holder.emp_companyname.setText(emplymentList.get(position).getCompany_name());
        if (emplymentList.get(position).getCurrently_work_here().equalsIgnoreCase("1")) {
            holder.emp_fromtodate.setText(emplymentList.get(position)
                    .getStart_year() + " "
                    + emplymentList.get(position).getStart_month() + " - " + "Present");
        } else {
            holder.emp_fromtodate.setText(emplymentList.get(position)
                    .getStart_year() + " "
                    + emplymentList.get(position).getStart_month() + " - "
                    + emplymentList.get(position).getEnd_year() + " " + emplymentList.get
                    (position).getEnd_month());
        }
        holder.emp_role_val.setText(emplymentList.get(position).getRole());
        holder.emp_industry_val.setText(emplymentList.get(position).getManufacturing());
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        if (!languages.equalsIgnoreCase("English")) {
            if (emplymentList.get(position).getEmp_role_name_local() != null) {
                holder.emp_role_val.setText(emplymentList.get(position).getEmp_role_name_local());
            }
            if (emplymentList.get(position).getEmp_industry_local() != null) {
                holder.emp_industry_val.setText(emplymentList.get(position).getEmp_industry_local());
            }
        }
        holder.employment_editicon.setId(position);
        holder.employment_deleteicon.setId(position);
        holder.employment_editicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.employmentid = emplymentList.get(v.getId()).getEmployment_id();
                Intent in = new Intent(activity, MyProfile_EDIT_Employment.class);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(activity);
                Editor editor = sharedPreferences.edit();
                editor.putString("EMP_ID", GlobalData.employmentid);
                editor.apply();
                activity.startActivity(in);
            }
        });
        holder.employment_deleteicon.setOnClickListener(new OnClickListener() {
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
                        .employmentdeleteconfirm));
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.employmentid = emplymentList.get(v.getId()).getEmployment_id();
                final int getid = v.getId();
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        new deleteEmployment(getid).execute();
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

    private class deleteEmployment extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteEmployment(int id) {
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
            paramsadd.addFormDataPart("employment_id", GlobalData
                    .employmentid);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData
                    .login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employment_delete.php")
                    .post(requestBody)
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
                    JSONObject responseObj = new JSONObject(deleteresponse);
                    String getDeleteStatus = responseObj.getString("status");
                    int deletestatus = responseObj.getInt("status_code");
                    Toast.makeText(activity, getDeleteStatus, Toast.LENGTH_SHORT).show();
                    if (deletestatus == 1) {
                        emplymentList.remove(id);
                        new Handler(MyProfileActivity.callback).sendEmptyMessage(0);
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