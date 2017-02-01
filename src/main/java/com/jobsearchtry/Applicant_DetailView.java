package com.jobsearchtry;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Education;
import com.jobsearchtry.wrapper.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jobsearchtry.R.id.bold;
import static com.jobsearchtry.R.id.educationlist;

//This class implements an application that detail view of job seeker who are applied a job
// (employer-responses-detail).
public class Applicant_DetailView extends Activity {
    private String getPhoneno, getEmail, getStatus,
            setStatus, languages, gettotal_exp_years = "0", getMonths_of_exp = "0";
    private TextView name,role, sal, exp, location,qualification,cv,
            calltab_text, shortlist, rejectuser, block, hold;
    private ProgressDialog pg;
    private OkHttpClient client = null;
    private LinearLayout call,res_profileview, jsdfooter;
    private Dialog alertDialog;
    private static final int REQUEST_CALL_PERMISSION = 105;
    private static final int REQUEST_READ_CALLLOG = 35;
    private ImageView shortlist_tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applicant_profile);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        //get the jobseekerid,jobid from responses page and employer email from emploer login page
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.applicantid = sharedPreferences.getString("APPLI_ID",
                GlobalData.applicantid);
        GlobalData.applicantjobid = sharedPreferences.getString("APPLI_JID",
                GlobalData.applicantjobid);
        GlobalData.company_email = sharedPreferences.getString("EEMAIL",
                GlobalData.company_email);
        res_profileview = (LinearLayout) findViewById(R.id.res_profileview);
        jsdfooter = (LinearLayout) findViewById(R.id.jsdfooter);
        call = (LinearLayout) findViewById(R.id.jsd_calltab);
        LinearLayout email = (LinearLayout) findViewById(R.id.jsd_emailtab);
        calltab_text = (TextView) findViewById(R.id.jsd_calltab_text);
        cv = (TextView) findViewById(R.id.a_jsd_details_cv);
        //detail view of jobseeker
        TextView viewprofile = (TextView) findViewById(R.id.a_jsd_details_vp);
        viewprofile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.applicantid != null) {
                    GlobalData.empid = GlobalData.applicantid;
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Applicant_DetailView.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("EMP_ID", GlobalData.empid);
                    editor.apply();
                    startActivity(new Intent(Applicant_DetailView.this,
                            JobSeeker_DetailView.class));
                }
            }
        });
        //clicking logo - go to employer dashboard
        ImageButton adv_header = (ImageButton) findViewById(R.id.js_r_h);
        adv_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Applicant_DetailView.this,
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
        calltab_text.setTextColor(Color.parseColor("#ffc600"));
        call.setBackgroundResource(R.drawable.list_row_bg_hover);
        //get the details of jobseeker from webservices
        if (new UtilService().isNetworkAvailable(Applicant_DetailView.this)) {
            new GetJobSeekerDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        //call to jobseeker
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callJobseeker();
            }
        });
        //email to jobseeker
        email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getContacts();
            }
        });
        name = (TextView) findViewById(R.id.a_jsd_name);
        role = (TextView)findViewById(R.id.a_jsd_role);
        qualification = (TextView) findViewById(R.id.a_jsd_qual);
        exp = (TextView) findViewById(R.id.a_jsd_exp);
        location = (TextView) findViewById(R.id.jsd_location);
        sal = (TextView) findViewById(R.id.a_jsd_salary);
        shortlist = (TextView) findViewById(R.id.a_shortlist);
        rejectuser = (TextView) findViewById(R.id.a_reject);
        block = (TextView) findViewById(R.id.a_block);
        hold = (TextView) findViewById(R.id.a_hold);
        shortlist_tick = (ImageView) findViewById(R.id.jsd_selected);

        shortlist.setBackgroundColor(Color.parseColor("#75971a"));
        hold.setBackgroundColor(Color.parseColor("#005d8a"));
        rejectuser.setBackgroundColor(Color.parseColor("#d14545"));
        block.setBackgroundColor(Color.parseColor("#696969"));
        //shortlist a jobseeker for this job
        shortlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(Applicant_DetailView.this,R.style.MyThemeDialog);
                View emppromptView = View.inflate(
                        Applicant_DetailView.this, R.layout.delete_popup, null);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.slconfirmation);
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader.setText(R.string.slalert);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView
                        .findViewById(R.id.d_yes);
                alertDialog.show();
                //if yes shorted the user for this job
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        setStatus = "shorlisted";
                        if (new UtilService()
                                .isNetworkAvailable(Applicant_DetailView.this)) {
                            new getResponseStatus().execute();
                            shortlist.setTextColor(Color.parseColor("#ffc600"));
                            rejectuser.setTextColor(Color.WHITE);
                            block.setTextColor(Color.WHITE);
                            hold.setTextColor(Color.WHITE);
                            shortlist_tick.setVisibility(View.VISIBLE);
                            shortlist.setVisibility(View.INVISIBLE);
                            hold.setVisibility(View.VISIBLE);
                            block.setVisibility(View.VISIBLE);
                            rejectuser.setVisibility(View.VISIBLE);
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
        });
        //reject a jobseeker for this job
        rejectuser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(Applicant_DetailView.this,R.style.MyThemeDialog);
                View emppromptView = View.inflate(Applicant_DetailView.this,
                        R.layout.delete_popup, null);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.rejectconfirm);
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader.setText(R.string.rejectalert);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView
                        .findViewById(R.id.d_yes);
                alertDialog.show();
                //if yes rejected a user for this job
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        setStatus = "rejected";
                        if (new UtilService()
                                .isNetworkAvailable(Applicant_DetailView.this)) {
                            new getResponseStatus().execute();
                            rejectuser.setTextColor(Color.parseColor("#ffc600"));
                            shortlist.setTextColor(Color.WHITE);
                            block.setTextColor(Color.WHITE);
                            shortlist_tick.setVisibility(View.GONE);
                            hold.setTextColor(Color.WHITE);
                            rejectuser.setVisibility(View.INVISIBLE);
                            shortlist.setVisibility(View.VISIBLE);
                            hold.setVisibility(View.VISIBLE);
                            block.setVisibility(View.VISIBLE);
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
        });
        //block a jobseeker for this job
        block.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(Applicant_DetailView.this,R.style.MyThemeDialog);
                View emppromptView = View.inflate(Applicant_DetailView.this,
                        R.layout.delete_popup, null);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.blockconfim);
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader
                        .setText(R.string.blockalert);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView
                        .findViewById(R.id.d_yes);
                alertDialog.show();
                //if yes blocked a user for this job
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        setStatus = "blocked";
                        if (new UtilService()
                                .isNetworkAvailable(Applicant_DetailView.this)) {
                            new getResponseStatus().execute();
                            rejectuser.setTextColor(Color.WHITE);
                            shortlist.setTextColor(Color.WHITE);
                            block.setTextColor(Color.parseColor("#ffc600"));
                            hold.setTextColor(Color.WHITE);
                            shortlist_tick.setVisibility(View.GONE);
                            block.setVisibility(View.INVISIBLE);
                            shortlist.setVisibility(View.VISIBLE);
                            hold.setVisibility(View.VISIBLE);
                            rejectuser.setVisibility(View.VISIBLE);
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
        });
        //hold a jobseeker for this job
        hold.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(Applicant_DetailView.this,R.style.MyThemeDialog);
                View emppromptView = View.inflate(Applicant_DetailView.this,
                        R.layout.delete_popup, null);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView
                        .findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.holdconfirm);
                TextView f_popupsubheader = (TextView) emppromptView
                        .findViewById(R.id.d_popup_subheader);
                f_popupsubheader
                        .setText(R.string.holdalert);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView
                        .findViewById(R.id.d_yes);
                alertDialog.show();
                //if yes hold a user for this job
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        setStatus = "hold";
                        if (new UtilService()
                                .isNetworkAvailable(Applicant_DetailView.this)) {
                            new getResponseStatus().execute();
                            rejectuser.setTextColor(Color.WHITE);
                            shortlist.setTextColor(Color.WHITE);
                            block.setTextColor(Color.WHITE);
                            hold.setTextColor(Color.parseColor("#ffc600"));
                            hold.setVisibility(View.INVISIBLE);
                            shortlist_tick.setVisibility(View.GONE);
                            shortlist.setVisibility(View.VISIBLE);
                            block.setVisibility(View.VISIBLE);
                            rejectuser.setVisibility(View.VISIBLE);
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(Applicant_DetailView.this)) {
            new GetJobSeekerDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //update a status(shortlist,reject,block or hold) to the database
    private class getResponseStatus extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Applicant_DetailView.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("actionn", setStatus)
                    .add("job_id", GlobalData.applicantjobid)
                    .add("jobseeker_id", GlobalData.applicantid)
                    .build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "apply_job_status.php")
                    .post(formBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                randomkeysendresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (randomkeysendresponse != null
                    && !randomkeysendresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(randomkeysendresponse);
                    getStatus = responseObj.getString("status");
                    if (getStatus.equalsIgnoreCase("S")) {
                        Toast.makeText(Applicant_DetailView.this,
                                getString(R.string.profileshortlisted),
                                Toast.LENGTH_SHORT).show();
                    } else if (getStatus.equalsIgnoreCase("R")) {
                        Toast.makeText(Applicant_DetailView.this,
                                getString(R.string.profilerejected), Toast.LENGTH_SHORT)
                                .show();
                    } else if (getStatus.equalsIgnoreCase("B")) {
                        Toast.makeText(Applicant_DetailView.this,
                                getString(R.string.profileblocked), Toast.LENGTH_SHORT)
                                .show();
                    } else if (getStatus.equalsIgnoreCase("H")) {
                        Toast.makeText(Applicant_DetailView.this,
                                getString(R.string.profilehold),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(Applicant_DetailView.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void callJobseeker() {
        if (getPhoneno != null && !getPhoneno.isEmpty()) {
            calltab_text.setTextColor(Color.parseColor("#ffc600"));
            call.setBackgroundResource(R.drawable.list_row_bg_hover);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getPhoneno));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(Applicant_DetailView.this, android.Manifest
                        .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Applicant_DetailView.this, new
                            String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
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

    private void getContacts() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Applicant_DetailView.this, android.Manifest
                    .permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Applicant_DetailView.this, new String[]{android
                        .Manifest.permission.GET_ACCOUNTS}, REQUEST_READ_CALLLOG);
            } else {
                Account[] accounts = AccountManager.get(
                        Applicant_DetailView.this).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        String possibleEmail = account.name;
                        if (GlobalData.company_email != null) {
                            if (possibleEmail != null) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:" + getEmail));
                                startActivity(Intent.createChooser(intent,
                                        "Email"));
                            }
                        } else {
                            Toast.makeText(
                                    Applicant_DetailView.this,
                                    getString(R.string.notconfiguresemail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else {
            Account[] accounts = AccountManager.get(Applicant_DetailView.this).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;
                    if (GlobalData.company_email != null) {
                        if (possibleEmail != null) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + getEmail));
                            startActivity(Intent.createChooser(intent,
                                    "Email"));
                        }
                    } else {
                        Toast.makeText(
                                Applicant_DetailView.this,
                                getString(R.string.notconfiguresemail),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    callJobseeker();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.callpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_CALLLOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.emailpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class GetJobSeekerDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Applicant_DetailView.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("job_id", GlobalData.applicantjobid);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.applicantid);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "candidate_view_status.php")
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
                    final User userdetail = gson.fromJson(
                            responseObj.getString("applicantprofile"),
                            new TypeToken<User>() {
                            }.getType());
                    name.setText(userdetail.getUserName());
                    role.setText(userdetail.getJob_Role());
                    String getSalary = responseObj.getString("salary");
                    if (!getSalary.equalsIgnoreCase("0")) {
                        sal.setText(getString(R.string.slyy) + " : " + getSalary);
                    } else {
                        sal.setText(getString(R.string.slyy) + " : " + getString(R.string.na));
                    }
                    gettotal_exp_years = responseObj.getString("years_of_experience");
                    getMonths_of_exp = responseObj.getString("months_of_exp");
                    if (!gettotal_exp_years.equalsIgnoreCase("0 Yrs") && !getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + responseObj.getString("years_of_experience") + "" +
                                " " + "-" + " "
                                + responseObj.getString("months_of_exp"));
                    } else if (!gettotal_exp_years.equalsIgnoreCase("0 Yrs") && getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + responseObj.getString("years_of_experience"));
                    } else if (gettotal_exp_years.equalsIgnoreCase("0 Yrs") && !getMonths_of_exp.equalsIgnoreCase("0 Months")) {
                        exp.setText(getString(R.string.experience) + " : " + responseObj.getString("months_of_exp"));
                    } else {
                        exp.setText(getString(R.string.experience) + " :  n/a");
                    }
                    location.setText(getString(R.string.currentlocation) + " : " + userdetail.getLocation());
                    if (!languages.equalsIgnoreCase("English")) {
                        if (responseObj.getString("location_local") != "null") {
                            location.setText(getString(R.string.currentlocation) + " : " + responseObj.getString("location_local"));
                        }
                    }
                    getPhoneno = userdetail.getMobile();
                    getEmail = userdetail.getEmailId();
                    qualification.setText(responseObj.getString("education"));
                    getStatus = responseObj.getString("status");
                    if (getStatus.equalsIgnoreCase("A")) {
                        rejectuser.setVisibility(View.VISIBLE);
                        shortlist.setVisibility(View.VISIBLE);
                        hold.setVisibility(View.VISIBLE);
                        block.setVisibility(View.VISIBLE);
                        shortlist.setTextColor(Color.WHITE);
                        rejectuser.setTextColor(Color.WHITE);
                        block.setTextColor(Color.WHITE);
                        hold.setTextColor(Color.WHITE);
                    } else if (getStatus.equalsIgnoreCase("S")) {
                        shortlist_tick.setVisibility(View.VISIBLE);
                        shortlist.setVisibility(View.INVISIBLE);
                        shortlist.setTextColor(Color.parseColor("#ffc600"));
                    } else if (getStatus.equalsIgnoreCase("R")) {
                        rejectuser.setVisibility(View.INVISIBLE);
                        rejectuser.setTextColor(Color.parseColor("#ffc600"));
                    } else if (getStatus.equalsIgnoreCase("B")) {
                        block.setVisibility(View.INVISIBLE);
                        block.setTextColor(Color.parseColor("#ffc600"));
                    } else {
                        hold.setVisibility(View.INVISIBLE);
                        hold.setTextColor(Color.parseColor("#ffc600"));
                    }
                    if (!(userdetail.getResume().isEmpty())) {
                        cv.setText(R.string.clickhere);
                        cv.setClickable(true);
                        cv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GlobalData.resume = userdetail.getResume();
                                Intent in = new Intent(
                                        Applicant_DetailView.this,
                                        View_Resume.class);
                                startActivity(in);
                            }
                        });
                    } else {
                        cv.setText("N/A");
                        cv.setClickable(false);
                    }
                    if (!languages.equalsIgnoreCase("English")) {
                        if (responseObj.getString("location_local") != null) {
                            location.setText(getString(R.string.currentlocation) + " : " + responseObj.getString("location_local"));
                        }
                    }
                    res_profileview.setVisibility(View.VISIBLE);
                    jsdfooter.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {
                }
            } else {
                res_profileview.setVisibility(View.GONE);
                jsdfooter.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
