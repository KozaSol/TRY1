package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.utils.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JobSeeker_Detail extends Activity {
    private String setApplyStatus, getCallStatus = "Yes", getPhoneNo, getTime, getSpecificDays, languages,
            getcall_toast;
    private TextView jobtitle, compname, salrange, exp, jobdesc, applytext, calltab_text,
            readmore, qualification, jobtype, skillspreferred, jd_jp_date,
            jd_gender_text,
            jd_qual_text, jd_skill_text, messagetab_text, sharetab_text, jd_exp_req,
            jd_qualification_req, jd_note,
            jd_clientname, jd_role;
    private ImageView jd_setfav, applyimg, calltab_img, emailtab_img;
    private LinearLayout jd_footer, call, apply, jd_details, jd_message, share, pageview, jd_clientname_lay;
    private ProgressDialog pg;
    private Jobs jobdetail;
    private int prev_state;
    private OkHttpClient client = null;
    private ArrayList<String> selected_fromampm;

    @Override
    public void onBackPressed() {
        onclickback();
    }

    //remove html tags
    /*public static String html2text(String html) {
        return Jsoup.parse(html).text();
	}*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobseeker_detailpage);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.jobid = sharedPreferences.getString("JOB_ID", GlobalData.jobid);
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        GlobalData.joblistfrom = sharedPreferences.getString("JobListFrom", GlobalData
                .joblistfrom);
        pageview = (LinearLayout) findViewById(R.id.jsd_show_lay);
        jd_exp_req = (TextView) findViewById(R.id.jd_exp_req);
        jd_qualification_req = (TextView) findViewById(R.id.jd_qualification_req);
        jd_clientname = (TextView) findViewById(R.id.jd_clientname);
        jd_role = (TextView) findViewById(R.id.jd_role);
        jd_note = (TextView) findViewById(R.id.jd_note);
        //get the details of job from the webservices
        if (new UtilService().isNetworkAvailable(JobSeeker_Detail.this)) {
            new GetJobDetails().execute();
        } else {
            Toast.makeText(JobSeeker_Detail.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton js_d_header = (ImageButton) findViewById(R.id.js_r_h);
        js_d_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickback();
            }
        });
        jd_footer = (LinearLayout) findViewById(R.id.jdfooter);
        call = (LinearLayout) findViewById(R.id.calltab);
        apply = (LinearLayout) findViewById(R.id.applytab);
        share = (LinearLayout) findViewById(R.id.mysocialsharetab);
        jd_message = (LinearLayout) findViewById(R.id.js_mymessagetab);
        jd_details = (LinearLayout) findViewById(R.id.detailsdata);
        jd_clientname_lay = (LinearLayout) findViewById(R.id.jd_clientname_lay);
        applytext = (TextView) findViewById(R.id.applytab_text);
        if (GlobalData.login_status.equalsIgnoreCase("No user found")) {
            setApplyStatus = getString(R.string.loginapply);
            applytext.setText(setApplyStatus);
        }
        TextView jd_header = (TextView) findViewById(R.id.jd_headertext);
        calltab_text = (TextView) findViewById(R.id.calltab_text);
        sharetab_text = (TextView) findViewById(R.id.socialshare_text);
        messagetab_text = (TextView) findViewById(R.id.js_message_text);
        readmore = (TextView) findViewById(R.id.jd_details_readmore);
        qualification = (TextView) findViewById(R.id.jd_qualification);
        jd_gender_text = (TextView) findViewById(R.id.jd_gender);
        jobtype = (TextView) findViewById(R.id.jd_jobtype);
        jd_skill_text = (TextView) findViewById(R.id.jd_skill_text);
        skillspreferred = (TextView) findViewById(R.id.jd_skillpreferred);
        jd_qual_text = (TextView) findViewById(R.id.jd_qual_text);
        jd_jp_date = (TextView) findViewById(R.id.jd_jp_date);
        applyimg = (ImageView) findViewById(R.id.applytab_img);
        calltab_img = (ImageView) findViewById(R.id.calltab_img);
        emailtab_img = (ImageView) findViewById(R.id.emailtab_img);
        jd_details.setVisibility(View.VISIBLE);
        jd_header.setText(R.string.jobdetails);
        //if phone no is available ,can possible to call the employer
        call.setBackgroundResource(R.drawable.list_row_bg_hover);
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                calltoEmployer();
            }
        });
        //can possible to send a email to employer
        jd_message.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jd_message.setBackgroundResource(R.drawable.list_row_bg_hover);
                call.setBackgroundResource(R.drawable.menu_bg);
                apply.setBackgroundResource(R.drawable.menu_bg);
                share.setBackgroundResource(R.drawable.menu_bg);
                if (jobdetail.getCompany_email() != null && !jobdetail.getCompany_email().isEmpty
                        ()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + jobdetail.getCompany_email()));
                    if (jobdetail.getJob_title() != null && !jobdetail.getJob_title().isEmpty()) {
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reg:" + " " + jobdetail
                                .getJob_title());
                    }
                    startActivity(Intent.createChooser(emailIntent, "Send an email"));
                } else {
                    new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getString(R.string.noemailid));
                }
            }
        });
        //job seeker apply a job
        apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jd_details.setVisibility(View.VISIBLE);
                call.setBackgroundResource(R.drawable.menu_bg);
                apply.setBackgroundResource(R.drawable.list_row_bg_hover);
                share.setBackgroundResource(R.drawable.menu_bg);
                jd_message.setBackgroundResource(R.drawable.menu_bg);
                if (GlobalData.login_status.equalsIgnoreCase("No user found")) {
                    GlobalData.loginfrom = "Jobseekar_D";
                    GlobalData.favpagefrom = "Home";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(JobSeeker_Detail.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("LOGINFROM", GlobalData.loginfrom);
                    editor.apply();
                    finish();
                    startActivity(new Intent(JobSeeker_Detail.this, Login.class));
                } else {
                    if (setApplyStatus.equalsIgnoreCase(getString(R.string.applied))) {
                        new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getString(R.string.youhavealreadyappliedforthisjob));
                        //  new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getString(R.string.youhavealreadyappliedforthisjob));
                        //Toast.makeText(JobSeeker_Detail.this, getString(R.string.youhavealreadyappliedforthisjob),
                        //       Toast.LENGTH_SHORT).show();
                        applyimg.setEnabled(false);
                    } else {
                        applyimg.setEnabled(true);
                        if (new UtilService().isNetworkAvailable(JobSeeker_Detail.this)) {
                            new GetApplyJob().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        //can share a job through facebook,whatsapp,gmail and email
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jd_details.setVisibility(View.VISIBLE);
                call.setBackgroundResource(R.drawable.menu_bg);
                apply.setBackgroundResource(R.drawable.menu_bg);
                share.setBackgroundResource(R.drawable.list_row_bg_hover);
                jd_message.setBackgroundResource(R.drawable.menu_bg);
                Share(getShareApplication());
            }

            private List<String> getShareApplication() {
                List<String> mList = new ArrayList<>();
                mList.add("com.facebook.katana");
                mList.add("com.whatsapp");
                mList.add("com.google.android.gm");
                mList.add("com.android.mms");
                return mList;
            }

            private void Share(List<String> PackageName) {
                try {
                    List<Intent> targetedShareIntents = new ArrayList<>();
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String job_title = jobdetail.getJob_title();
                    String message = "<b>" + "Hello" + "</b><br><br>";
                    message += "<b>" + "Hope you would be interested at this job." + "</b><br>";
                    message += "<b>" + "Title :" + " " + jobdetail.getJob_title() + "</b><br>";
                    message += "<b>" + "Location :" + " " + jobdetail.getLocation() + "</b><br>";
                    message += "<b>" + "Qualification :" + " " + jobdetail.getMinimum_qual() +
                            "</b><br>";
                    message += "<b>" + "Salary :" + " " + jobdetail.getSalary() + "</b><br>";
                    message += "<b>" + "Experience :" + " " + jobdetail.getExperience() +
                            "</b><br>";
                    if (jobdetail.getShowphoneflag().equalsIgnoreCase("Yes")) {
                        message += "<b>" + "Contact No :" + " " + jobdetail.getPhone_no() +
                                "</b><br>";
                    }
                    message += "<b>" + "Contact Email :" + " " + jobdetail.getCompany_email() +
                            "</b><br><br>";
                    message += "<b>" + "You can search more jobs by downloading TRY mobile app." +
                            "</b><br><br>";
                    message += "<b>" + "Keep TRYing!" + "</b>";
                    List<ResolveInfo> resInfo = JobSeeker_Detail.this.getPackageManager()
                            .queryIntentActivities(share,
                                    0);
                    if (!resInfo.isEmpty()) {
                        for (ResolveInfo info : resInfo) {
                            Intent targetedShare = new Intent(Intent.ACTION_SEND);
                            targetedShare.setType("text/plain");
                            if (PackageName.contains(info.activityInfo.packageName.toLowerCase()
                            )) {
                                if (info.activityInfo.packageName.contains("com.facebook" +
                                        ".katana")) {
                                    if (GlobalData.jobid != null) {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT,
                                                GlobalData.url + "viewjob.php?id=" + GlobalData
                                                        .jobid);
                                    }
                                    targetedShare.setPackage(info.activityInfo.packageName);
                                    targetedShareIntents.add(targetedShare);
                                } else if (info.activityInfo.packageName.contains("com" +
                                        ".whatsapp")) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build
                                            .VERSION_CODES.N) {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, Html.fromHtml
                                                (message, Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, "" + Html
                                                .fromHtml(message));
                                    }
                                    targetedShare.setPackage(info.activityInfo.packageName);
                                    targetedShareIntents.add(targetedShare);
                                } else if (info.activityInfo.packageName.contains("com.android" +
                                        ".mms")) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build
                                            .VERSION_CODES.N) {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, Html.fromHtml
                                                (message, Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, "" + Html
                                                .fromHtml(message));
                                    }
                                    targetedShare.setPackage(info.activityInfo.packageName);
                                    targetedShareIntents.add(targetedShare);
                                } else {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build
                                            .VERSION_CODES.N) {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, Html.fromHtml
                                                (message, Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        targetedShare.putExtra(Intent.EXTRA_TEXT, "" + Html
                                                .fromHtml(message));
                                    }
                                    targetedShare.putExtra(Intent.EXTRA_SUBJECT, "Reg:" + " " +
                                            job_title);
                                    targetedShare.setPackage(info.activityInfo.packageName);
                                    targetedShareIntents.add(targetedShare);
                                }
                            }
                        }
                        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0)
                                , "Share");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                targetedShareIntents.toArray(new Parcelable[]{}));
                        startActivity(chooserIntent);
                    } else {
                        Builder alert = new Builder(JobSeeker_Detail.this);
                        alert.setTitle("Warning");
                        alert.setMessage("Facebook,Gmail,Whatsapp and Messaging application are " +
                                "not found");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        alert.show();
                    }
                } catch (Exception ignored) {
                }
            }
        });
        jd_setfav = (ImageView) findViewById(R.id.jd_setfav);
        if (!(GlobalData.login_status.equalsIgnoreCase("No user found"))) {
            GlobalData.username = sharedPreferences.getString("NAME", GlobalData.username);
            GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        }
        jobtitle = (TextView) findViewById(R.id.jd_designation);
        compname = (TextView) findViewById(R.id.jd_companyaddress);
        salrange = (TextView) findViewById(R.id.jd_salary);
        exp = (TextView) findViewById(R.id.jd_exp);
        jobdesc = (TextView) findViewById(R.id.jd_details_contants);
        jd_setfav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (GlobalData.login_status.equalsIgnoreCase("No user found")) {
                    GlobalData.loginfrom = "Jobseekar_F";
                    GlobalData.favpagefrom = "Home";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(JobSeeker_Detail.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("LOGINFROM", GlobalData.loginfrom);
                    editor.apply();
                    startActivity(new Intent(JobSeeker_Detail.this, Login.class));
                    finish();
                } else {
                    if (new UtilService().isNetworkAvailable(JobSeeker_Detail.this)) {
                        new AddJobToFavourite().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onclickback() {
        if (GlobalData.favpagefrom.equalsIgnoreCase("Fav") && GlobalData.pageback.equalsIgnoreCase
                ("Home")) {
            finish();
        } else if (GlobalData.favpagefrom.equalsIgnoreCase("Fav") || GlobalData.pageback
                .equalsIgnoreCase("Filter")) {
            GlobalData.joblistfrom = "JL";
            GlobalData.pagefrom = "Home";
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(JobSeeker_Detail.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("JobListFrom", GlobalData.joblistfrom);
            editor.apply();
            finish();
           /* if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_D")) {
                Intent homepageback = new Intent(this, Homepage.class);
                startActivity(homepageback);
            }*/
        } else if (GlobalData.favpagefrom.equalsIgnoreCase("NOTI")) {
            GlobalData.pagefrom = "Notification";
            GlobalData.pageback = "Home";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (JobSeeker_Detail.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("PAGEBACK", GlobalData.pageback);
            editor.apply();
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case 31:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    calltoEmployer();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.callpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
            case 33:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    StringBuffer sb = new StringBuffer();
                    String strOrder = CallLog.Calls.DATE + " DESC";
                    if (ActivityCompat.checkSelfPermission(JobSeeker_Detail.this, android.Manifest
                            .permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Cursor managedCursor = getContentResolver().query(CallLog.Calls
                                .CONTENT_URI, null, null, null, strOrder);
                        if (managedCursor != null && managedCursor.moveToFirst()) {
                            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                            sb.append("Call Log :");
                            if (managedCursor.moveToFirst()) {
                                String callDuration = managedCursor.getString(duration);
                                if (callDuration.equalsIgnoreCase("0")) {
                                    Toast.makeText(JobSeeker_Detail.this, getString(R.string.empoyernotacceptcall), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        managedCursor.close();
                    }
                } else {
                    Toast.makeText(JobSeeker_Detail.this, getString(R.string.empoyernotacceptcall),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class AddJobToFavourite extends AsyncTask<String, String, String> {
        String setfavresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSeeker_Detail.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("job_id", GlobalData.jobid)
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder().url(GlobalData.url + "favourite_job.php").post
                    (formBody).build();
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
                    JSONObject responseObj = new JSONObject(setfavresponse);
                    String getFavStatus = responseObj.getString("Status");
                    if (!(getFavStatus.equalsIgnoreCase("Favourite Job Added"))) {
                        jd_setfav.setImageResource(R.drawable.star);
                        Toast.makeText(getApplicationContext(), getString(R.string.favjobremoved), Toast
                                .LENGTH_SHORT).show();
                    } else {
                        jd_setfav.setImageResource(R.drawable.yellow_star_icon);
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

    private class GetApplyJob extends AsyncTask<String, String, String> {
        String applyjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSeeker_Detail.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("job_id", GlobalData.jobid);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "apply_job.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                applyjobresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (applyjobresponse != null && !applyjobresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(applyjobresponse);
                    String getApplyStatus = json.getString("Status");
                    new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getApplyStatus);
                    //new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getApplyStatus);
                    int status_code = json.getInt("status_code");
                    if (status_code == 3) {
                        setApplyStatus = getString(R.string.apply);
                    } else {
                        setApplyStatus = getString(R.string.applied);
                    }
                    applytext.setText(setApplyStatus);
                } catch (Exception e) {
                    Toast.makeText(JobSeeker_Detail.this, getString(R.string.errortoparse), Toast
                            .LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JobSeeker_Detail.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private class GetJobDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSeeker_Detail.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.favpagefrom.equalsIgnoreCase("NOTI")) {
                paramsadd.addFormDataPart("pagefrom", "NOTI");
            }
            paramsadd.addFormDataPart("job_id", GlobalData.jobid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "job_details.php").post
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
                    selected_fromampm = new ArrayList<>();
                    for (int i = 1; i < 25; i++) {
                        selected_fromampm.add(Integer.toString(i));
                    }
                    JSONObject responseObj = new JSONObject(jobdetailresponse);
                    Gson gson = new Gson();
                    jobdetail = gson.fromJson(responseObj.getString("job_details"), new
                            TypeToken<Jobs>() {
                            }.getType());
                    GlobalData.jobdetail = jobdetail;
                    GlobalData.jobdetailresponse = jobdetailresponse;
                    GlobalData.company_id = jobdetail.getCompany_id();
                    String getQualiReq = jobdetail.getMinimum_qualification_req();
                    String getClientname = jobdetail.getClientname();
                    jd_role.setText(jobdetail.getRole());
                    if (!getClientname.isEmpty() && getClientname != null) {
                        jd_clientname_lay.setVisibility(View.VISIBLE);
                        jd_clientname.setText(getClientname);
                    } else {
                        jd_clientname_lay.setVisibility(View.GONE);
                    }
                    if (getQualiReq.equalsIgnoreCase("Y")) {
                        jd_qualification_req.setVisibility(View.VISIBLE);
                    } else {
                        jd_qualification_req.setVisibility(View.GONE);
                    }
                    String getGenderReq = jobdetail.getGender_req();
                    if (getGenderReq.equalsIgnoreCase("Y")) {
                        jd_gender_text
                                .setText(Html.fromHtml("<font color=#161100>" +
                                        jobdetail.getJobgender() + "</font> <font " +
                                        "color=#ff0000>*</font>"));
                    } else {
                        jd_gender_text.setText(Html.fromHtml("<font color=#161100>"
                                + jobdetail.getJobgender() + "</font>"));
                    }
                    String getSkillreq = jobdetail.getSkills_req();
                    if (jobdetail.getSkills() != null && !jobdetail.getSkills().isEmpty()) {
                        skillspreferred.setVisibility(View.VISIBLE);
                        jd_skill_text.setVisibility(View.VISIBLE);
                        if (getSkillreq.equalsIgnoreCase("Y")) {
                            String text = "<font color=#161100>" + jobdetail.getSkills()
                                    + "</font> <font color=#ff0000>*</font>";
                            skillspreferred.setText(Html.fromHtml(text));
                        } else {
                            skillspreferred.setText(jobdetail.getSkills());
                        }
                    } else {
                        jd_skill_text.setVisibility(View.GONE);
                        skillspreferred.setVisibility(View.GONE);
                    }
                    String getExpReq = jobdetail.getExperience_req();
                    if (getExpReq.equalsIgnoreCase("Y")) {
                        jd_exp_req.setVisibility(View.VISIBLE);
                    } else {
                        jd_exp_req.setVisibility(View.GONE);
                    }
                    if (getQualiReq.equalsIgnoreCase("Y") || getGenderReq.equalsIgnoreCase("Y")
                            || getSkillreq.equalsIgnoreCase("Y") || getExpReq.equalsIgnoreCase
                            ("Y")) {
                        jd_note.setVisibility(View.VISIBLE);
                        jd_note.setText(Html.fromHtml(
                                "<font color=##3b2e00>" + getString(R.string.note1) + " </font> <font " +
                                        "color=#ff0000>*</font><font color=##3b2e00>" + getString(R.string.note2) +
                                        "</font>"));
                    } else {
                        jd_note.setVisibility(View.GONE);
                    }
                    jobtitle.setText(jobdetail.getJob_title());
                    String[] separated = jobdetail.getLocation().split(",");
                    if (separated.length > 1) {
                        compname.setText(jobdetail.getCompanies_profiles_name() + " - " +
                                separated[0] + " " + "+"
                                + (separated.length - 1));
                    } else {
                        compname.setText(jobdetail.getCompanies_profiles_name() + " - " +
                                separated[0]);
                    }
                    salrange.setText(getString(R.string.slypm) + " : " + jobdetail.getSalary());
                    exp.setText(jobdetail.getExperience());
                    qualification.setText(jobdetail.getMinimum_qual());
                    jobtype.setText(jobdetail.getJob_type_name());
                    SimpleDateFormat mdyFormat = new SimpleDateFormat("dd-MM-yyyy");
                    jd_jp_date.setText(getString(R.string.postedon) + " : " + mdyFormat.format(jobdetail.getJob_add_date
                            ()));
                    if (!languages.equalsIgnoreCase("English")) {
                        if (jobdetail.getLocation_local() != null) {
                            String[] separatedloc = jobdetail.getLocation_local().split(",");
                            if (separatedloc.length > 1) {
                                compname.setText(jobdetail.getCompanies_profiles_name() + " - " +
                                        separatedloc[0] + " " + "+"
                                        + (separatedloc.length - 1));
                            } else {
                                compname.setText(jobdetail.getCompanies_profiles_name() + " - " +
                                        separatedloc[0]);
                            }
                        }
                        if (jobdetail.getJobtype_local() != null) {
                            jobtype.setText(jobdetail.getJobtype_local());
                        }
                        if (jobdetail.getGender_local() != null) {
                            if (getGenderReq.equalsIgnoreCase("Y")) {
                                jd_gender_text
                                        .setText(Html.fromHtml("<font color=#161100>" +
                                                jobdetail.getGender_local() + "</font> <font " +
                                                "color=#ff0000>*</font>"));
                            } else {
                                jd_gender_text.setText(Html.fromHtml("<font color=#161100>" + jobdetail.getGender_local() + "</font>"));
                            }
                        }
                        if (jobdetail.getRole_name_local() != null) {
                            jd_role.setText(jobdetail.getRole_name_local());
                        }
                        if (jobdetail.getExperience_local() != null) {
                            exp.setText(jobdetail.getExperience_local());
                        }
                        if (jobdetail.getQualification_local() != null) {
                            qualification.setText(jobdetail.getQualification_local());
                        }
                    }
                    getCallStatus = jobdetail.getShowphoneflag();
                    if (jobdetail.getJob_description().length() < 250) {
                        jobdesc.setText(jobdetail.getJob_description());
                        readmore.setVisibility(View.GONE);
                    } else {
                        readmore.setVisibility(View.VISIBLE);
                        jobdesc.setText(jobdetail.getJob_description().substring(0, 250) + " ...");
                        readmore.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                readmore.setVisibility(View.GONE);
                                jobdesc.setText(jobdetail.getJob_description());
                            }
                        });
                    }
                    if (jobdetail.getFav_status().equalsIgnoreCase("1")) {
                        jd_setfav.setImageResource(R.drawable.yellow_star_icon);
                    } else {
                        jd_setfav.setImageResource(R.drawable.star);
                    }
                    if (GlobalData.login_status.equalsIgnoreCase("No user found")) {
                        setApplyStatus = getString(R.string.loginapply);
                        applytext.setText(setApplyStatus);
                    } else {
                        if (jobdetail.getApplystatus().equalsIgnoreCase("N")) {
                            setApplyStatus = getString(R.string.apply);
                            applytext.setText(setApplyStatus);
                            applyimg.setImageResource(R.drawable.applyjob_icon);
                        } else {
                            setApplyStatus = getString(R.string.applied);
                            applytext.setText(setApplyStatus);
                            applyimg.setImageResource(R.drawable.applyiedjob_icon);
                            applytext.setTextColor(Color.parseColor("#d1d1d1"));
                        }
                    }
                    pageview.setVisibility(View.VISIBLE);
                    jd_footer.setVisibility(View.VISIBLE);
                    getcall_toast = jobdetail.getCall_toast();
                    if (getCallStatus.equalsIgnoreCase("Yes")) {
                        if (jobdetail.getTime_status().equalsIgnoreCase("N")) {
                            calltab_img.setImageResource(R.drawable.call_icon);
                            getPhoneNo = jobdetail.getPhone_no();
                        } else {
                            getTime = jobdetail.getSettime();
                            //getPhoneNo = jobdetail.getPhone_no();
                            String[] out = getTime.split("-");
                            SimpleDateFormat sdf = new SimpleDateFormat("HH");
                            String currenthour = sdf.format(new Date());
                            SimpleDateFormat sdfmin = new SimpleDateFormat("mm");
                            String currentminuts = sdfmin.format(new Date());
                            String fromhr = "1", tohr = "1";
                            getSpecificDays = jobdetail.getSpecificdays();
                            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
                            Date d = new Date();
                            String dayOfTheWeek = sdf1.format(d);
                            if (Arrays.asList(GlobalData.select_fromm).contains(out[0])) {
                                int getpos = Arrays.asList(GlobalData.select_fromm).indexOf
                                        (out[0]);
                                fromhr = selected_fromampm.get(getpos);
                            }
                            if (Arrays.asList(GlobalData.select_fromm).contains(out[1])) {
                                int gettopos = Arrays.asList(GlobalData.select_fromm).indexOf
                                        (out[1]);
                                tohr = selected_fromampm.get(gettopos);
                                if ((Integer.parseInt(currenthour) == Integer.parseInt(tohr)) && (Integer.parseInt(currenthour) != 1)) {
                                    tohr = Integer.toString(Integer.parseInt(tohr) - 1);
                                }
                            }
                            if (getSpecificDays != null && !getSpecificDays.isEmpty()) {
                                List<String> specdayslist = Arrays.asList(getSpecificDays.split(","));
                                if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                                        (Integer.parseInt(currenthour) <= Integer.parseInt(tohr)) &&
                                        specdayslist.contains(dayOfTheWeek)) {
                                    calltab_img.setImageResource(R.drawable.call_icon);
                                    getPhoneNo = jobdetail.getPhone_no();
                                } else {
                                    calltab_img.setImageResource(R.drawable.dis_call_icon);
                                    calltab_text.setTextColor(Color.parseColor("#d1d1d1"));
                                }
                            } else {
                                if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                                        (Integer.parseInt(currenthour) <= Integer.parseInt(tohr))) {
                                    calltab_img.setImageResource(R.drawable.call_icon);
                                    getPhoneNo = jobdetail.getPhone_no();
                                } else {
                                    calltab_img.setImageResource(R.drawable.dis_call_icon);
                                    calltab_text.setTextColor(Color.parseColor("#d1d1d1"));
                                }
                            }
                        }
                    } else {
                        calltab_img.setImageResource(R.drawable.dis_call_icon);
                        calltab_text.setTextColor(Color.parseColor("#d1d1d1"));
                    }
                    if (jobdetail.getCompany_email().isEmpty()) {
                        emailtab_img.setImageResource(R.drawable.no_email);
                        messagetab_text.setTextColor(Color.parseColor("#d1d1d1"));
                    } else {
                        emailtab_img.setImageResource(R.drawable.email_iconwhite);
                    }
                    if (GlobalData.favpagefrom.equalsIgnoreCase("NOTI")) {
                        GlobalData.getCount = responseObj.getString("count");
                    }
                } catch (Exception ignored) {
                }
            } else {
                pageview.setVisibility(View.INVISIBLE);
                jd_footer.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
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
            if (ActivityCompat.checkSelfPermission(JobSeeker_Detail.this, android.Manifest
                    .permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(JobSeeker_Detail.this, new String[]{android
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
                        Toast.makeText(JobSeeker_Detail.this, getString(R.string.empoyernotacceptcall), Toast.LENGTH_SHORT).show();
                    }
                }
                managedCursor.close();
            }
        }
    }

    private void calltoEmployer() {
        if (getCallStatus.equalsIgnoreCase("Yes")) {
            jd_details.setVisibility(View.VISIBLE);
            call.setBackgroundResource(R.drawable.list_row_bg_hover);
            apply.setBackgroundResource(R.drawable.menu_bg);
            share.setBackgroundResource(R.drawable.menu_bg);
            jd_message.setBackgroundResource(R.drawable.menu_bg);
            if (jobdetail.getTime_status().equalsIgnoreCase("N")) {
                calltab_img.setImageResource(R.drawable.call_icon);
                getPhoneNo = jobdetail.getPhone_no();
                PhoneCallListener phoneListener = new PhoneCallListener();
                TelephonyManager telephonyManager = (TelephonyManager) JobSeeker_Detail.this
                        .getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getPhoneNo));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(JobSeeker_Detail.this, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(JobSeeker_Detail.this, new
                                String[]{android.Manifest.permission.CALL_PHONE}, 31);
                    } else {
                        startActivity(callIntent);
                    }
                } else {
                    startActivity(callIntent);
                }
            } else {
                getTime = jobdetail.getSettime();
                String[] out = getTime.split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String currenthour = sdf.format(new Date());
                String fromhr = "1", tohr = "1";
                if (Arrays.asList(GlobalData.select_fromm).contains(out[0])) {
                    int getpos = Arrays.asList(GlobalData.select_fromm).indexOf(out[0]);
                    fromhr = selected_fromampm.get(getpos);
                }
                if (Arrays.asList(GlobalData.select_fromm).contains(out[1])) {
                    int gettopos = Arrays.asList(GlobalData.select_fromm).indexOf(out[1]);
                    tohr = selected_fromampm.get(gettopos);
                    if ((Integer.parseInt(currenthour) == Integer.parseInt(tohr)) && (Integer.parseInt(currenthour) != 1)) {
                        tohr = Integer.toString(Integer.parseInt(tohr) - 1);
                    }
                }
                getSpecificDays = jobdetail.getSpecificdays();
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = sdf1.format(d);
                if (getSpecificDays != null && !getSpecificDays.isEmpty()) {
                    List<String> specdayslist = Arrays.asList(getSpecificDays.split(","));
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                            (Integer.parseInt(currenthour) <= Integer.parseInt(tohr)) && specdayslist.contains(dayOfTheWeek)) {
                        getCall();
                    } else {
                        if (!languages.equalsIgnoreCase("English")) {
                            new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getcall_toast);
                            //MyToast.MyGetToast(JobSeeker_Detail.this, getcall_toast, Toast.LENGTH_LONG).show();
                            //new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getcall_toast);
                        } else {
                            new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getcall_toast);
                            // MyToast.MyGetToast(JobSeeker_Detail.this, getcall_toast, Toast.LENGTH_LONG).show();
                            //new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getcall_toast);
                        }
                        calltab_img.setImageResource(R.drawable.dis_call_icon);
                    }
                } else {
                    if ((Integer.parseInt(currenthour) >= Integer.parseInt(fromhr)) &&
                            (Integer.parseInt(currenthour) <= Integer.parseInt(tohr))) {
                        getCall();
                    } else {
                        if (!languages.equalsIgnoreCase("English")) {
                            //  MyToast.MyGetToast(JobSeeker_Detail.this, getcall_toast, Toast.LENGTH_LONG).show();
                            //new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getcall_toast);
                            new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getcall_toast);
                        } else {
                            //MyToast.MyGetToast(JobSeeker_Detail.this, getcall_toast, Toast.LENGTH_LONG).show();
                            // new DisplayToastMessage().isToastMessage(JobSeeker_Detail.this, getcall_toast);
                            new CustomAlertDialog().isDisplayMessage(JobSeeker_Detail.this, getcall_toast);
//                            Toast.makeText(JobSeeker_Detail.this,
//                                    getcall_toast,
//                                    Toast.LENGTH_LONG);
                            //MyToast toast = new MyToast(JobSeeker_Detail.this);
                            //  toast.show();
                        }
                        calltab_img.setImageResource(R.drawable.dis_call_icon);
                    }
                }
            }
        } else {
            Toast.makeText(JobSeeker_Detail.this, getString(R.string.empoyernotacceptcall), Toast
                    .LENGTH_SHORT)
                    .show();
        }
    }

    private void getCall() {
        calltab_img.setImageResource(R.drawable.call_icon);
        getPhoneNo = jobdetail.getPhone_no();
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) JobSeeker_Detail.this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + getPhoneNo));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(JobSeeker_Detail.this, android
                    .Manifest.permission.CALL_PHONE) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(JobSeeker_Detail.this, new
                        String[]{android.Manifest.permission.CALL_PHONE}, 31);
            } else {
                startActivity(callIntent);
            }
        } else {
            startActivity(callIntent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.jobid = sharedPreferences.getString("JOB_ID", GlobalData.jobid);
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        if (GlobalData.login_status.equalsIgnoreCase("No user found")) {
            setApplyStatus = getString(R.string.loginapply);
            applytext.setText(setApplyStatus);
        }
    }
}
