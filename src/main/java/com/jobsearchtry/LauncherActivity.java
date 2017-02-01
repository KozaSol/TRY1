package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import android.util.Log;
import com.jobsearchtry.services.AutoStartService;
import com.jobsearchtry.services.NotifiCountService;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LauncherActivity extends Activity {
    private ProgressDialog pg;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        //get the jobseeker_id and employer id from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((LauncherActivity.this));
        GlobalData.login_status = sharedPreferences.getString("LS",
                GlobalData.login_status);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        GlobalData.getLanguageFlag = sharedPreferences.getString("LANGFLAG",
                GlobalData.getLanguageFlag);
        GlobalData.getLanguageName = sharedPreferences.getString("LANGNAME", GlobalData.getLanguageName);
        Log.e("langname",GlobalData.getLanguageName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (GlobalData.getLanguageName.equalsIgnoreCase("English")) {
            conf.locale = new Locale("en");
            Log.e("langname","eng");
        } else {
            Log.e("langname","tamil");
            conf.locale = new Locale("ta");
        }
        res.updateConfiguration(conf, dm);
        GlobalData.LandRefresh = "Home";
        GlobalData.islocationAvail = "No";
        GlobalData.getjsfilterdata = null;
        //job seeker and employer does not logged in the application
        if (GlobalData.login_status.equals("No user found")
                && GlobalData.emp_login_status
                .equalsIgnoreCase("No user found")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (GlobalData.getLanguageFlag.equalsIgnoreCase("No")) {
                        GlobalData.getLanguageFlag = "Yes";
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(LauncherActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("LANGFLAG", GlobalData.getLanguageFlag);
                        editor.apply();
                        new ChooseLanguages().selectLanguagesPopup(LauncherActivity.this);
                    } else {
                        Intent i = new Intent(LauncherActivity.this, Homepage.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 1000);
        }
        //employer logged in the application
        else if (!(GlobalData.emp_login_status
                .equalsIgnoreCase("No user found"))) {
            startService(new Intent(getBaseContext(), AutoStartService.class));
            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                new getResponseCount().execute();
            } else {
                startActivity(new Intent(LauncherActivity.this,
                        EmployerDashboard.class));
                finish();
            }
        }
        //jobseeker logged in the application
        else {
            startService(new Intent(getBaseContext(), NotifiCountService.class));
            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                new getNotificationCount().execute();
            } else {
                startActivity(new Intent(LauncherActivity.this, Homepage.class));
                finish();
            }
        }
    }

    private class getNotificationCount extends AsyncTask<String, String, String> {
        String pendingresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                pg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pg = new ProgressDialog(LauncherActivity.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("action", "CountNotification")
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "updatenotification.php")
                    .post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response;
                response = client.newCall(request).execute();
                pendingresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (pendingresponse != null && !pendingresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(pendingresponse);
                    GlobalData.getCount = responseObj.getString("count");
                    startActivity(new Intent(LauncherActivity.this, Homepage.class));
                    finish();
                } catch (Exception ignored) {
                    Toast.makeText(LauncherActivity.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LauncherActivity.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class getResponseCount extends AsyncTask<String, String, String> {
        String pendingresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                pg.dismiss();
            } catch (Exception e) {
            }
            pg = new ProgressDialog(LauncherActivity.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("action", "CountNotificationNew")
                    .add("company_id", GlobalData.emp_login_status).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "updatenotification.php")
                    .post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                pendingresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (pendingresponse != null
                    && !pendingresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(pendingresponse);
                    GlobalData.empNotiCount = responseObj.getString("count");
                    startActivity(new Intent(LauncherActivity.this,
                            EmployerDashboard.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(LauncherActivity.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LauncherActivity.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
