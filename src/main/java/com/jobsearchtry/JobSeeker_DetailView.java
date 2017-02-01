package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.EMP_EducationList_Adpater;
import com.jobsearchtry.adapter.EMP_EmploymentList_Adpater;
import com.jobsearchtry.adapter.EMP_SkillList_Adpater;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.DesiredJob;
import com.jobsearchtry.wrapper.Education;
import com.jobsearchtry.wrapper.Employment;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.User;

public class JobSeeker_DetailView extends Activity {
    private String getFavStatus, getPhoneno, getEmail, possibleEmail, getSalary = "0",
            languages, gettotal_exp_years = "0", getMonths_of_exp = "0";
    private TextView name, age, sal, exp, location, gender, cv, calltab_text, mobileno,
            emailid, dob, langknown, dj_location, dj_role, dj_industry, dj_jobtype,
            dj_salary, emp_myprofile_role, jsprofile_desi;
    private ImageView jsd_setfav;
    private LinearLayout jsd_footer, call, cv_lay, emp_js_empshlay, emp_js_skilllay,
            desired_jd_emp, js_showview,emp_js_emaillay ;
    private ProgressDialog pg;
    private User userdetail;
    private Helper emplymentlist, educationlist, skilllist;
    private OkHttpClient client = new OkHttpClient();
    private int prev_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_jobseeker_detail);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        js_showview = (LinearLayout) findViewById(R.id.js_showview);
        jsd_footer = (LinearLayout) findViewById(R.id.jsdfooter);
        call = (LinearLayout) findViewById(R.id.jsd_calltab);
        LinearLayout email = (LinearLayout) findViewById(R.id.jsd_emailtab);
        cv_lay = (LinearLayout) findViewById(R.id.jsd_details_lay);
        calltab_text = (TextView) findViewById(R.id.jsd_calltab_text);
        emp_js_emaillay = (LinearLayout) findViewById(R.id.emp_emaillay);
        ImageButton emp_js_d_h = (ImageButton) findViewById(R.id.js_r_h);
        emp_js_empshlay = (LinearLayout) findViewById(R.id.emp_js_empshlay);
        emp_js_skilllay = (LinearLayout) findViewById(R.id.emp_js_skilllay);
        desired_jd_emp = (LinearLayout) findViewById(R.id.desired_jd_emp);
        emp_js_d_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobSeeker_DetailView.this, EmployerDashboard.class));
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
        calltab_text.setTextColor(Color.parseColor("#ffc600"));
        call.setBackgroundResource(R.drawable.list_row_bg_hover);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.empid = sharedPreferences.getString("EMP_ID", GlobalData.empid);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS", GlobalData
                .emp_login_status);
        GlobalData.company_email = sharedPreferences.getString("EEMAIL", GlobalData.company_email);
        if (new UtilService().isNetworkAvailable(JobSeeker_DetailView.this)) {
            new GetJobSeekerDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callJobseeker();
            }
        });
        email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getContacts();
            }
        });
        jsd_setfav = (ImageView) findViewById(R.id.jsd_setfav);
        name = (TextView) findViewById(R.id.jsd_name);
        jsprofile_desi = (TextView) findViewById(R.id.jsprofile_desi);
        age = (TextView) findViewById(R.id.jsd_age);
        gender = (TextView) findViewById(R.id.jsd_gender);
        exp = (TextView) findViewById(R.id.jsd_exp);
        location = (TextView) findViewById(R.id.jsd_location);
        cv = (TextView) findViewById(R.id.jsd_details_cv);
        sal = (TextView) findViewById(R.id.jsd_salary);
        mobileno = (TextView) findViewById(R.id.emp_js_d_mobilenumber);
        emailid = (TextView) findViewById(R.id.emp_js_d_emailid);
        dob = (TextView) findViewById(R.id.emp_js_d_dob);
        langknown = (TextView) findViewById(R.id.emp_js_d_langknown);
        emplymentlist = (Helper) findViewById(R.id.emp_js_employmentlist);
        emplymentlist.setExpanded(true);
        emplymentlist.setFocusable(false);
        educationlist = (Helper) findViewById(R.id.emp_js_educationlist);
        educationlist.setExpanded(true);
        educationlist.setFocusable(false);
        skilllist = (Helper) findViewById(R.id.emp_js_skillslist);
        skilllist.setExpanded(true);
        skilllist.setFocusable(false);
        dj_location = (TextView) findViewById(R.id.emp_js_desired_location);
        dj_role = (TextView) findViewById(R.id.emp_js_desired_role);
        dj_industry = (TextView) findViewById(R.id.emp_js_desired_industry);
        dj_jobtype = (TextView) findViewById(R.id.emp_js_desired_jobtype);
        dj_salary = (TextView) findViewById(R.id.emp_js_desired_salary);
        emp_myprofile_role = (TextView) findViewById(R.id.emp_myprofile_role);
        jsd_setfav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (new UtilService().isNetworkAvailable(JobSeeker_DetailView.this)) {
                    new AddJobToFavourite().execute();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void getContacts() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(JobSeeker_DetailView.this, android.Manifest
                    .permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(JobSeeker_DetailView.this, new String[]{android
                        .Manifest.permission.GET_ACCOUNTS}, 35);
            } else {
                Account[] accounts = AccountManager.get(JobSeeker_DetailView.this).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        possibleEmail = account.name;
                        if (sendEmailValidation()) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + getEmail));
                            startActivity(Intent.createChooser(intent, "Email"));
                        }
                    }
                }
            }
        } else {
            Account[] accounts = AccountManager.get(JobSeeker_DetailView.this).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    if (sendEmailValidation()) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + getEmail));
                        startActivity(Intent.createChooser(intent, "Email"));
                    }
                }
            }
        }

    }

    private boolean sendEmailValidation() {
        if (GlobalData.company_email == null || GlobalData.company_email.isEmpty()) {
            Toast.makeText(JobSeeker_DetailView.this,
                    getString(R.string.notconfiguresemail),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (possibleEmail == null || possibleEmail.isEmpty()) {
            Toast.makeText(JobSeeker_DetailView.this,
                    getString(R.string.notconfiguresemail),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void callJobseeker() {
        if (getPhoneno != null && !getPhoneno.isEmpty()) {
            calltab_text.setTextColor(Color.parseColor("#ffc600"));
            call.setBackgroundResource(R.drawable.list_row_bg_hover);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getPhoneno));
            PhoneCallListener phoneListener = new PhoneCallListener();
            TelephonyManager telephonyManager = (TelephonyManager) JobSeeker_DetailView.this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(JobSeeker_DetailView.this, android.Manifest
                        .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(JobSeeker_DetailView.this, new
                            String[]{android.Manifest.permission.CALL_PHONE}, 105);
                } else {
                    startActivity(callIntent);
                }
            } else {
                startActivity(callIntent);
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.phnonull), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case 105:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    callJobseeker();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.callpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
            case 33:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    StringBuffer sb = new StringBuffer();
                    String strOrder = CallLog.Calls.DATE + " DESC";
                    if (ActivityCompat.checkSelfPermission(JobSeeker_DetailView.this, android
                            .Manifest.permission.READ_CALL_LOG) == PackageManager
                            .PERMISSION_GRANTED) {
                        Cursor managedCursor = getContentResolver().query(CallLog.Calls
                                .CONTENT_URI, null, null, null, strOrder);
                        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                        sb.append("Call Log :");
                        if (managedCursor.moveToFirst()) {
                            String callDuration = managedCursor.getString(duration);
                            if (callDuration.equalsIgnoreCase("0")) {
                                Toast.makeText(JobSeeker_DetailView.this, getString(R.string.jobseekernotacceptcall), Toast.LENGTH_SHORT).show();
                            }
                        }
                        managedCursor.close();
                    }
                } else {
                    Toast.makeText(JobSeeker_DetailView.this, getString(R.string.jobseekernotacceptcall), Toast.LENGTH_SHORT).show();
                }
                break;
            case 35:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.emailpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

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

    private void getCallDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(JobSeeker_DetailView.this, android.Manifest
                    .permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(JobSeeker_DetailView.this, new String[]{android
                        .Manifest.permission.READ_CALL_LOG}, 33);
            } else {
                StringBuffer sb = new StringBuffer();
                String strOrder = CallLog.Calls.DATE + " DESC";
                Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                        null, null, strOrder);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Log :");
                if (managedCursor.moveToFirst()) {
                    String callDuration = managedCursor.getString(duration);
                    if (callDuration.equalsIgnoreCase("0")) {
                        Toast.makeText(JobSeeker_DetailView.this, getString(R.string.jobseekernotacceptcall), Toast.LENGTH_SHORT).show();
                    }
                }
                managedCursor.close();
            }
        } else {
            StringBuffer sb = new StringBuffer();
            String strOrder = CallLog.Calls.DATE + " DESC";
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    null, null, strOrder);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Log :");
            if (managedCursor.moveToFirst()) {
                String callDuration = managedCursor.getString(duration);
                if (callDuration.equalsIgnoreCase("0")) {
                    Toast.makeText(JobSeeker_DetailView.this, getString(R.string.jobseekernotacceptcall), Toast.LENGTH_SHORT).show();
                }
            }
            managedCursor.close();
        }
    }

    class AddJobToFavourite extends AsyncTask<String, String, String> {
        String setfavresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSeeker_DetailView.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("action", "Change_fav_status")
                    .add("employer_id", GlobalData.emp_login_status).add("jobseeker_id",
                            GlobalData.empid).build();
            Request request = new Request.Builder().url(GlobalData.url + "favourite_jobseeker" +
                    ".php").post(formBody)
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                setfavresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (setfavresponse != null && !setfavresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(setfavresponse);
                    getFavStatus = json.getString("Status");
                    if (!(getFavStatus.equalsIgnoreCase("Favourite Job Added"))) {
                        jsd_setfav.setImageResource(R.drawable.star);
                        Toast.makeText(getApplicationContext(), getString(R.string.favjobremoved), Toast
                                .LENGTH_SHORT).show();
                    } else {
                        jsd_setfav.setImageResource(R.drawable.yellow_star_icon);
                        Toast.makeText(getApplicationContext(), getString(R.string.favjobadded), Toast
                                .LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.errortoparse), Toast
                            .LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    class GetJobSeekerDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSeeker_DetailView.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "View");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.empid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "candidate_view_new.php").post
                    (requestBody).build();
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
            if (jobdetailresponse != null && !jobdetailresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(jobdetailresponse);
                    Gson gson = new Gson();
                    userdetail = gson.fromJson(responseObj.getString("profile"), new
                            TypeToken<User>() {
                            }.getType());
                    getFavStatus = responseObj.getString("fav_status");
                    if (getFavStatus.equalsIgnoreCase("1")) {
                        jsd_setfav.setImageResource(R.drawable.yellow_star_icon);
                    } else {
                        jsd_setfav.setImageResource(R.drawable.star);
                    }
                    GlobalData.jobseeker_id = userdetail.getId();
                    name.setText(userdetail.getUserName());
                    jsprofile_desi.setText(userdetail.getJob_Role());
                    age.setText(userdetail.getAge());
                    getSalary = responseObj.getString("salary");
                    if (!getSalary.equalsIgnoreCase("0")) {
                        sal.setText(getString(R.string.slypm) + " : " + getSalary);
                    } else {
                        sal.setText(getString(R.string.slypm) + " :  n/a");
                    }
                    gettotal_exp_years = responseObj.getString("years_of_experience");
                    getMonths_of_exp = responseObj.getString("months_of_exp");
                    if (!gettotal_exp_years.equalsIgnoreCase("0 Yrs") && !getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + gettotal_exp_years + "" +
                                " " + "-" + " "
                                + getMonths_of_exp);
                    } else if (!gettotal_exp_years.equalsIgnoreCase("0 Yrs") && getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + gettotal_exp_years);
                    } else if (gettotal_exp_years.equalsIgnoreCase("0 Yrs") && !getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + getMonths_of_exp);
                    } else {
                        exp.setText(getString(R.string.experience) + " :  n/a");
                    }
                    /*exp.setText(getString(R.string.experience) + " : " + responseObj.getString("years_of_experience") + "" +
                            " " + "-" + " "
                            + responseObj.getString("months_of_exp"));*/
                    location.setText(userdetail.getLocation());
                    gender.setText(userdetail.getGender());
                    mobileno.setText(userdetail.getMobile());
                    String getEmailId = userdetail.getEmailId();
                    if (!(getEmailId !=null && getEmailId.isEmpty())) {
                        emp_js_emaillay.setVisibility(View.VISIBLE);
                        emailid.setText(userdetail.getEmailId());
                    }
                    else {
                        emp_js_emaillay.setVisibility(View.GONE);
                    }
                    emp_myprofile_role.setText(userdetail.getJob_Role());
                    dob.setText(userdetail.getDOB());
                    langknown.setText(userdetail.getLanguages());
                    if (!languages.equalsIgnoreCase("English")) {
                        if (responseObj.getString("location_local") != "null") {
                            location.setText(responseObj.getString("location_local"));
                        }
                        if (responseObj.getString("gender_local") != "null") {
                            gender.setText(responseObj.getString("gender_local"));
                        }
                        if (responseObj.getString("role_name_local") != "null") {
                            emp_myprofile_role.setText(responseObj.getString("role_name_local"));
                        }
                    }
                    getPhoneno = userdetail.getMobile();
                    getEmail = userdetail.getEmailId();
                    if (!(userdetail.getResume().isEmpty())) {
                        cv_lay.setVisibility(View.VISIBLE);
                        cv.setClickable(true);
                        cv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GlobalData.resume = userdetail.getResume();
                                Intent in = new Intent(JobSeeker_DetailView.this, View_Resume
                                        .class);
                                startActivity(in);
                            }
                        });
                    } else {
                        cv_lay.setVisibility(View.GONE);
                        cv.setClickable(false);
                    }
                    ArrayList<Employment> emplymentList = gson.fromJson(responseObj.getString("employment_list"),
                            new TypeToken<ArrayList<Employment>>() {
                            }.getType());
                    if (emplymentList.size() != 0) {
                        emp_js_empshlay.setVisibility(View.VISIBLE);
                        EMP_EmploymentList_Adpater emplymentlistAdapter = new EMP_EmploymentList_Adpater(JobSeeker_DetailView
                                .this, emplymentList);
                        emplymentlist.setAdapter(emplymentlistAdapter);
                    } else {
                        emp_js_empshlay.setVisibility(View.GONE);
                    }
                    ArrayList<Education> educationList = gson.fromJson(responseObj.getString("education_list"),
                            new TypeToken<ArrayList<Education>>() {
                            }.getType());
                    if (educationList.size() != 0) {
                        EMP_EducationList_Adpater educationlistAdapter = new EMP_EducationList_Adpater(JobSeeker_DetailView
                                .this, educationList);
                        educationlist.setAdapter(educationlistAdapter);
                    }
                    ArrayList<Skill> skillList = gson.fromJson(responseObj.getString("skills_list"), new
                            TypeToken<ArrayList<Skill>>() {
                            }.getType());
                    if (skillList.size() != 0) {
                        emp_js_skilllay.setVisibility(View.VISIBLE);
                        EMP_SkillList_Adpater skilllistAdapter = new EMP_SkillList_Adpater(JobSeeker_DetailView.this,
                                skillList);
                        skilllist.setAdapter(skilllistAdapter);
                    } else {
                        emp_js_skilllay.setVisibility(View.GONE);
                    }
                    if (responseObj.getString("desired_view_status").equalsIgnoreCase("1")) {
                        desired_jd_emp.setVisibility(View.VISIBLE);
                        DesiredJob desiredjob = gson.fromJson(responseObj.getString
                                        ("desired_view"),
                                new TypeToken<DesiredJob>() {
                                }.getType());
                        dj_location.setText(desiredjob.getLocation());
                        dj_role.setText(desiredjob.getRole());
                        dj_industry.setText(desiredjob.getIndustry());
                        dj_jobtype.setText(desiredjob.getJob_type());
                        dj_salary.setText(desiredjob.getSalary());
                        if (!languages.equalsIgnoreCase("English")) {
                            if (desiredjob.getDesiredlocation_local() != null) {
                                dj_location.setText(desiredjob.getDesiredlocation_local());
                            }
                            if (desiredjob.getDesired_role_name_local() != null) {
                                dj_role.setText(desiredjob.getDesired_role_name_local());
                            }
                            if (desiredjob.getDesired_industry_local() != null) {
                                dj_industry.setText(desiredjob.getDesired_industry_local());
                            }
                            if (desiredjob.getDesired_jobtype_local() != null) {
                                dj_jobtype.setText(desiredjob.getDesired_jobtype_local());
                            }
                        }
                    } else {
                        desired_jd_emp.setVisibility(View.GONE);
                    }
                    js_showview.setVisibility(View.VISIBLE);
                    jsd_footer.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {
                }
            } else {
                js_showview.setVisibility(View.INVISIBLE);
                jsd_footer.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}
