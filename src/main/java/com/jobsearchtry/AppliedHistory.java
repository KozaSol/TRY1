package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.AppliedJobList_Adpater;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppliedHistory extends Activity {
    private ListView jobappliedhistorylist;
    private TextView emptyappliedmsg;
    private ProgressDialog pg;
    private String languages;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onbackclick();
    }

    //This class implements an application that getting the list of applied job history
    // (jobseeker_appliedjob-history).
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myappliedhistory);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS",
                GlobalData.login_status);
        //get the list of jobs applied by jobseeker
        if (new UtilService().isNetworkAvailable(AppliedHistory.this)) {
            new My_AppliedJobs().execute();
        } else {
            Toast.makeText(AppliedHistory.this,
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        jobappliedhistorylist = (ListView) findViewById(R.id.jobappliedhistorylist);
        emptyappliedmsg = (TextView) findViewById(R.id.appliedemptymsg);
        ImageButton apphis_header = (ImageButton) findViewById(R.id.js_r_h);
        apphis_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
    }

    private void onbackclick() {
        GlobalData.joblistfrom = "RL";
        startActivity(new Intent(AppliedHistory.this, Homepage.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case 31:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    //calltoEmployer(getPhoneStatus,getTimeStatus,getPhoneno,getTime,holder
                    // .applyhis_call);
                } else {
                    Toast.makeText(AppliedHistory.this, getString(R.string.callpermissiondenied), Toast.LENGTH_SHORT).show();
                }
                break;
            case 33:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    StringBuffer sb = new StringBuffer();
                    String strOrder = CallLog.Calls.DATE + " DESC";
                    if (ActivityCompat.checkSelfPermission(AppliedHistory.this, android.Manifest
                            .permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Cursor managedCursor = getContentResolver().query(CallLog.Calls
                                .CONTENT_URI, null, null, null, strOrder);
                        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                        sb.append("Call Log :");
                        if (managedCursor.moveToFirst()) {
                            String callDuration = managedCursor.getString(duration);
                            if (callDuration.equalsIgnoreCase("0")) {
                                Toast.makeText(AppliedHistory.this, getString(R.string.empoyernotacceptcall), Toast.LENGTH_SHORT).show();
                            }
                        }
                        managedCursor.close();
                    }
                } else {
                    Toast.makeText(AppliedHistory.this, getString(R.string.empoyernotacceptcall),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class My_AppliedJobs extends AsyncTask<String, String, String> {
        String appliedresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(AppliedHistory.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "applied_history.php").post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                appliedresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (appliedresponse != null
                    && !appliedresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(appliedresponse);
                    Gson gson = new Gson();
                    ArrayList<Jobs> jobApplyHistoryList;
                    jobApplyHistoryList = gson.fromJson(
                            responseObj.getString("applied_list"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    if (jobApplyHistoryList.size() == 0) {
                        emptyappliedmsg.setVisibility(View.VISIBLE);
                        jobApplyHistoryList.clear();
                    } else {
                        emptyappliedmsg.setVisibility(View.GONE);
                        AppliedJobList_Adpater myjobsAdapter = new AppliedJobList_Adpater(
                                AppliedHistory.this, jobApplyHistoryList);
                        jobappliedhistorylist.setAdapter(myjobsAdapter);
                    }
                } catch (Exception e) {
                    Toast.makeText(AppliedHistory.this, getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AppliedHistory.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
