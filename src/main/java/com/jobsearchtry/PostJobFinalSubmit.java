package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.CustomAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.Dialog;
public class PostJobFinalSubmit extends Activity {
    private ProgressDialog pg;
    private TextView designation, companyname, salary, exp, readmore, aboutjob,
            qualification, gender, jobtype, skillspreferred, postedon, role, clientname;
    private Jobs jobdetail;
    private OkHttpClient client = null;
    private Dialog alertDialog;
    private String languages;

    @Override
    public void onBackPressed() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postjob_detailjob);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.jobregid = sharedPreferences.getString("JRID",
                GlobalData.jobregid);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetJobDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        ImageButton pj_f_h = (ImageButton) findViewById(R.id.js_r_h);
        pj_f_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostJobFinalSubmit.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        designation = (TextView) findViewById(R.id.emp_pjf_jd_designation);
        companyname = (TextView) findViewById(R.id.emp_pjf_jd_companyaddress);
        salary = (TextView) findViewById(R.id.emp_pjf_jd_salary);
        exp = (TextView) findViewById(R.id.emp_pjf_jd_exp);
        aboutjob = (TextView) findViewById(R.id.emp_pjf_jd_details_contants);
        readmore = (TextView) findViewById(R.id.emp_pj_jd_details_readmore);
        qualification = (TextView) findViewById(R.id.emp_pj_jd_qualification);
        gender = (TextView) findViewById(R.id.emp_pj_jd_gender);
        jobtype = (TextView) findViewById(R.id.emp_pj_jd_jobtype);
        skillspreferred = (TextView) findViewById(R.id.emp_pj_jd_skillpreferred);
        postedon = (TextView) findViewById(R.id.emp_pjf_jd_jp_date);
        role = (TextView) findViewById(R.id.emp_pj_jd_role);
        clientname = (TextView) findViewById(R.id.emp_pj_jd_clientname);
        ImageButton editjob = (ImageButton) findViewById(R.id.tryEditJob);
        Button submitjob = (Button) findViewById(R.id.tryPublishJob);
        Button delete = (Button) findViewById(R.id.tryDeleteJob);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(PostJobFinalSubmit.this,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                View emppromptView = View.inflate(PostJobFinalSubmit.this,
                        R.layout.delete_popup, null);
                alertDialog.setContentView(emppromptView);
                alertDialog.show();
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.canceljobsubmit);

                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader
                        .setText(R.string.canceljobalert);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        finish();
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
        editjob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.frompostjob = "PostJob";
                GlobalData.ejobid = GlobalData.jobregid;
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(PostJobFinalSubmit.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("EJOB_ID", GlobalData.ejobid);
                editor.putString("FROMPOSTJOB", GlobalData.frompostjob);
                editor.apply();
                startActivity(new Intent(PostJobFinalSubmit.this,
                        Edit_Job.class));
            }
        });
        submitjob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new UtilService()
                        .isNetworkAvailable(getApplicationContext())) {
                    new publishJob().execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GetJobDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostJobFinalSubmit.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("action", "PostedJobDetails");
            paramsadd.addFormDataPart("job_id", GlobalData.jobregid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_View_update.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                jobdetailresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (jobdetailresponse != null
                    && !jobdetailresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(jobdetailresponse);
                    Gson gson = new Gson();
                    jobdetail = gson.fromJson(responseObj.getString("list"),
                            new TypeToken<Jobs>() {
                            }.getType());
                    designation.setText(jobdetail.getJob_title());
                    companyname.setText(jobdetail.getCompanies_profiles_name()
                            + " - " + jobdetail.getLocation());
                    salary.setText(getString(R.string.slypm) + " : " + jobdetail.getSalary());
                    exp.setText(getString(R.string.experience) + " : " + jobdetail.getExperience());
                    qualification.setText(getString(R.string.qualification) + " : "
                            + jobdetail.getMinimum_qual());
                    gender.setText(getString(R.string.gender) + " : " + jobdetail.getJobgender());
                    jobtype.setText(getString(R.string.jobtype) + " : "
                            + jobdetail.getJob_type_name());
                    role.setText(getString(R.string.role) + " : " + jobdetail.getRole());
                    SimpleDateFormat mdyFormat = new SimpleDateFormat(
                            "dd-MM-yyyy");
                    postedon.setText(getString(R.string.postedon) + " : "
                            + mdyFormat.format(jobdetail.getJob_add_date()));
                    if (jobdetail.getSkills().isEmpty()) {
                        skillspreferred.setVisibility(View.GONE);
                    } else {
                        skillspreferred.setVisibility(View.VISIBLE);
                        skillspreferred
                                .setText(getString(R.string.skills) + " : " + jobdetail.getSkills());
                    }
                    if (jobdetail.getClientname().isEmpty()) {
                        clientname.setVisibility(View.GONE);
                    } else {
                        clientname.setVisibility(View.VISIBLE);
                        clientname
                                .setText(getString(R.string.clientname) + " : " + jobdetail.getClientname());
                    }
                    if (jobdetail.getJob_description().length() < 250) {
                        readmore.setVisibility(View.GONE);
                        aboutjob.setText(jobdetail.getJob_description());
                    } else {
                        aboutjob.setText(jobdetail.getJob_description()
                                .substring(0, 250) + " ...");
                        readmore.setVisibility(View.VISIBLE);
                        readmore.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                readmore.setVisibility(View.GONE);
                                aboutjob.setText(jobdetail.getJob_description());
                            }
                        });
                    }
                    if (!languages.equalsIgnoreCase("English")) {
                        if (!jobdetail.getLocation_local().isEmpty() && jobdetail.getLocation_local() != null) {
                            companyname.setText(jobdetail.getCompanies_profiles_name()
                                    + " - " + jobdetail.getLocation_local());
                        }
                        if (!jobdetail.getJobtype_local().isEmpty() && jobdetail.getJobtype_local() != null) {
                            jobtype.setText(getString(R.string.jobtype) + " : " + jobdetail.getJobtype_local());
                        }
                        if (!jobdetail.getGender_local().isEmpty() && jobdetail.getGender_local() != null) {
                            gender.setText(getString(R.string.gender) + " : " + jobdetail.getGender_local());
                        }
                        if (!jobdetail.getRole_name_local().isEmpty() && jobdetail.getRole_name_local() != null) {
                            role.setText(getString(R.string.role) + " : " + jobdetail.getRole_name_local());
                        }
                        if (!jobdetail.getExperience_local().isEmpty() && jobdetail.getExperience_local() != null) {
                            exp.setText(getString(R.string.experience) + " : " + jobdetail.getExperience_local());
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class publishJob extends AsyncTask<String, String, String> {
        String postjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostJobFinalSubmit.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("action", "JobpostSubmit");
            paramsadd.addFormDataPart("job_id", GlobalData.jobregid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_View_update.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                postjobresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (postjobresponse != null
                    && !postjobresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(postjobresponse);
                    int stauscode = responseObj.getInt("status_code");
                    String getJobsAddStatus = responseObj.getString("status");
                    if (stauscode == 1) {
                        GlobalData.jobregid = null;
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(PostJobFinalSubmit.this);
                        Editor editor = sharedPreferences.edit();
                        editor.remove("JRID");
                        editor.apply();
                        new CustomAlertDialog().isDisplayMessage(PostJobFinalSubmit.this, getString(R.string.postjobsuccess));
                    } else {
                        Toast.makeText(PostJobFinalSubmit.this, getJobsAddStatus,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
