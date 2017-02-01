package com.jobsearchtry;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*details of job page(here we can change the status(active/inactive) and view/edit/update the
details of job)*/
public class PostedJob_Detail extends Activity {
    private TextView designation, companyname, salary, exp, readmore, aboutjob,
            jobstatus, qualification, gender, jobtype, skillspreferred,
            posteddate, role, clientname;
    private String getJobStatus, getJobStatusValue, languages;
    private Jobs jobdetail;
    private ProgressDialog pg;
    private Dialog alertDialog;
    private OkHttpClient client = null;
    private LinearLayout pj_showview, edit_changestatus,emp_client_lay,emp_skillslay;
    //remove html tags
    /*public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postedjob_detail);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        //get the company id and job id from session
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.ejobid = sharedPreferences.getString("EJOB_ID",
                GlobalData.ejobid);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        pj_showview = (LinearLayout) findViewById(R.id.pj_showview);
        edit_changestatus = (LinearLayout) findViewById(R.id.edit_changestatus);
        //get the details of job for this job id
        if (new UtilService().isNetworkAvailable(PostedJob_Detail.this)) {
            new GetJobDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        //logo clicking go to dashboard
        ImageButton pj_d_h = (ImageButton) findViewById(R.id.js_r_h);
        pj_d_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostedJob_Detail.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        designation = (TextView) findViewById(R.id.pj_emp_pjf_jd_designation);
        companyname = (TextView) findViewById(R.id.pj_emp_pjf_jd_companyaddress);
        salary = (TextView) findViewById(R.id.pj_emp_pjf_jd_salary);
        exp = (TextView) findViewById(R.id.pj_emp_pjf_jd_exp);
        qualification = (TextView) findViewById(R.id.pj_emp_pj_jd_qualification);
        gender = (TextView) findViewById(R.id.pj_emp_pj_jd_gender);
        jobtype = (TextView) findViewById(R.id.pj_emp_pj_jd_jobtype);
        skillspreferred = (TextView) findViewById(R.id.pj_emp_pj_jd_skillpreferred);
        aboutjob = (TextView) findViewById(R.id.pj_emp_pjf_jd_details_contants);
        readmore = (TextView) findViewById(R.id.pj_emp_pj_jd_details_readmore);
        jobstatus = (TextView) findViewById(R.id.pj_emp_pjf_jd_status);
        posteddate = (TextView) findViewById(R.id.pj_emp_pjf_jd_jp_date);
        role = (TextView) findViewById(R.id.pj_emp_pj_jd_jobrole);
        clientname = (TextView) findViewById(R.id.pj_emp_pj_jd_clientname);
        emp_client_lay = (LinearLayout) findViewById(R.id.emp_clientname_lay);
        emp_skillslay = (LinearLayout) findViewById(R.id.emp_skills_lay);
        //we can go to edit the job details
        Button editjob = (Button) findViewById(R.id.pj_tryEditJob);
        editjob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //back page identify - added shared preferences value is "FROMPOSTJOB"
                GlobalData.frompostjob = "PostedJob";
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(PostedJob_Detail.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("FROMPOSTJOB", GlobalData.frompostjob);
                editor.apply();
                startActivity(new Intent(PostedJob_Detail.this, Edit_Job.class));
                finish();
            }
        });
        //we can change the status of these job
        Button changestatusjob = (Button) findViewById(R.id.pj_tryChangeStatusJob);
        changestatusjob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //getJobStatusValue is 2 and 0 means the job is not approved by admin
                if (getJobStatusValue.equalsIgnoreCase("2")) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.changestatuswp,
                            Toast.LENGTH_LONG).show();
                } else {
                    alertDialog = new Dialog(PostedJob_Detail.this, R.style.MyThemeDialog);
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setCanceledOnTouchOutside(false);
                    View emppromptView = View.inflate(PostedJob_Detail.this,
                            R.layout.delete_popup, null);
                    alertDialog.setContentView(emppromptView);
                    alertDialog.show();
                    TextView f_popupheader = (TextView) emppromptView
                            .findViewById(R.id.d_popupheader);
                    f_popupheader.setText(R.string.changestatussubmit);
                    TextView f_popupsubheader = (TextView) emppromptView
                            .findViewById(R.id.d_popup_subheader);
                    f_popupsubheader
                            .setText(R.string.changestatusalert);
                    Button no = (Button) emppromptView.findViewById(R.id.d_no);
                    Button yes = (Button) emppromptView
                            .findViewById(R.id.d_yes);
                    //if yes changed the status of job
                    yes.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (new UtilService()
                                    .isNetworkAvailable(PostedJob_Detail.this)) {
                                new getChangeStatusJob().execute();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.checkconnection),
                                        Toast.LENGTH_SHORT).show();
                            }
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
            }
        });
    }

    //details of job from database
    private class GetJobDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostedJob_Detail.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("job_id", GlobalData.ejobid);
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
                    qualification.setText(jobdetail.getMinimum_qual());
                    gender.setText(jobdetail.getJobgender());
                    jobtype.setText(jobdetail.getJob_type_name());
                    role.setText(getString(R.string.role) + " : " + jobdetail.getRole());
                    SimpleDateFormat mdyFormat = new SimpleDateFormat(
                            "dd-MM-yyyy");
                    posteddate.setText(getString(R.string.postedon) + " : "
                            + mdyFormat.format(jobdetail.getJob_add_date()));
                    if (jobdetail.getSkills() != null && !jobdetail.getSkills().isEmpty()) {
                        emp_skillslay.setVisibility(View.VISIBLE);
                        skillspreferred.setText(jobdetail.getSkills());
                    } else {
                        emp_skillslay.setVisibility(View.GONE);
                    }
                    if (jobdetail.getClientname() != null && !jobdetail.getClientname().isEmpty()) {
                        emp_client_lay.setVisibility(View.VISIBLE);
                        clientname.setText(jobdetail.getClientname());
                    } else {
                        emp_client_lay.setVisibility(View.GONE);
                    }
                    //Jobtoshowflag = 1 means active, else job is inactive
                    if (jobdetail.getJobtoshowflag().equalsIgnoreCase("2")) {
                        getJobStatus = getString(R.string.posted);
                        getJobStatusValue = "2";
                        //edit_changestatus.setVisibility(View.VISIBLE);
                    } else if (jobdetail.getJobtoshowflag().equalsIgnoreCase("1")) {
                        getJobStatus = getString(R.string.active);
                        getJobStatusValue = "1";
                    } else {
                        getJobStatus = getString(R.string.inactive);
                        // edit_changestatus.setVisibility(View.INVISIBLE);
                        getJobStatusValue = jobdetail.getJobtoshowflag();
                    }
                    edit_changestatus.setVisibility(View.VISIBLE);
                    jobstatus.setText(getString(R.string.status) + " : " + getJobStatus);
                    //less then 250 chars of job description means read more option hidden , else
                    // readmore option visible,if clicking showing the remaining text
                    if (jobdetail.getJob_description().length() < 250) {
                        readmore.setVisibility(View.GONE);
                        aboutjob.setText(jobdetail
                                .getJob_description());
                    } else {
                        aboutjob.setText(jobdetail
                                .getJob_description().substring(0, 250)
                                + " ...");
                        readmore.setVisibility(View.VISIBLE);
                        readmore.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                readmore.setVisibility(View.GONE);
                                aboutjob.setText(jobdetail
                                        .getJob_description());
                            }
                        });
                    }
                    if (!languages.equalsIgnoreCase("English")) {
                        if (!jobdetail.getLocation_local().isEmpty() && jobdetail.getLocation_local() != null) {
                            companyname.setText(jobdetail.getCompanies_profiles_name()
                                    + " - " + jobdetail.getLocation_local());
                        }
                        if (!jobdetail.getJobtype_local().isEmpty() && jobdetail.getJobtype_local() != null) {
                            jobtype.setText(jobdetail.getJobtype_local());
                        }
                        if (!jobdetail.getGender_local().isEmpty() && jobdetail.getGender_local() != null) {
                            gender.setText(jobdetail.getGender_local());
                        }
                        if (jobdetail.getRole_name_local() != null) {
                            role.setText(jobdetail.getRole_name_local());
                        }
                        if (!jobdetail.getExperience_local().isEmpty() && jobdetail.getExperience_local() != null) {
                            exp.setText(jobdetail.getExperience_local());
                        }
                    }
                    pj_showview.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {
                }
            } else {
                pj_showview.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //update the status of job to database
    private class getChangeStatusJob extends AsyncTask<String, String, String> {
        String statusjoburesponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(PostedJob_Detail.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("action", "Changestatus")
                    .add("job_id", GlobalData.ejobid).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_View_update.php")
                    .post(formBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                statusjoburesponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (statusjoburesponse != null
                    && !statusjoburesponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(statusjoburesponse);
                    String getJobStatusUpdate = responseObj.getString("Status");
                    if (getJobStatusUpdate.equalsIgnoreCase("1")) {
                        getJobStatus = getString(R.string.active);
                    } else {
                        getJobStatus = getString(R.string.inactive);
                    }
                    jobstatus.setText(getString(R.string.status) + " : " + getJobStatus);
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
