package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.MyNotificationList_Adpater;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JS_Notification extends Activity {
    private ListView mynotificationlist;
    private TextView notificationemptymsg;
    private ProgressDialog pg;
    private String languages;
    private OkHttpClient client = null;
    private ArrayList<Jobs> jobNotificationList = null;
    MyNotificationList_Adpater mynotificationjobsAdapter = null;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GlobalData.joblistfrom = "RL";
        startActivity(new Intent(JS_Notification.this, Homepage.class));
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences1 = PreferenceManager
                .getDefaultSharedPreferences(JS_Notification.this);
        GlobalData.login_status = sharedPreferences1.getString("LS",
                GlobalData.login_status);
        GlobalData.getrolepage = "Home";
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(JS_Notification.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("ROLEPAGE", GlobalData.getrolepage);
        editor.apply();
        if (new UtilService().isNetworkAvailable(JS_Notification.this)) {
            new My_NotficationJobs().execute();
        } else {
            Toast.makeText(JS_Notification.this,
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        mynotificationlist = (ListView) findViewById(R.id.mynotificationlist);
        notificationemptymsg = (TextView) findViewById(R.id.notificationemptymsg);
        ImageButton js_notify_h = (ImageButton) findViewById(R.id.js_r_h);
        js_notify_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(JS_Notification.this, Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JS_Notification.this, Homepage.class));
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(JS_Notification.this)) {
            new My_NotficationJobs().execute();
        } else {
            Toast.makeText(JS_Notification.this,
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class My_NotficationJobs extends AsyncTask<String, String, String> {
        String notificationresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
            }
            pg = new ProgressDialog(JS_Notification.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "notification_list.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                notificationresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (notificationresponse != null
                    && !notificationresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(notificationresponse);
                    Gson gson = new Gson();
                    jobNotificationList = new ArrayList<>();
                    jobNotificationList = gson.fromJson(
                            responseObj.getString("tasks"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    if (jobNotificationList.size() == 0) {
                        notificationemptymsg.setVisibility(View.VISIBLE);
                    } else {
                        notificationemptymsg.setVisibility(View.GONE);
                        mynotificationjobsAdapter = new MyNotificationList_Adpater(
                                JS_Notification.this, jobNotificationList);
                        mynotificationlist
                                .setAdapter(mynotificationjobsAdapter);
                    }
                } catch (Exception e) {
                    Toast.makeText(JS_Notification.this, getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JS_Notification.this, getString(R.string.connectionfailure),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
