package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.SkillUpdateList_Adpater;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Skill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfile_EDIT_Skill extends Activity {
    private Helper skillupdatelist;
    private JSONObject skillupdatevalue;
    private String getjsonskillupdatevalues, languages;
    private ProgressDialog pg;

    @Override
    public void onBackPressed() {
        new BackAlertDialog().isBackDialog(MyProfile_EDIT_Skill.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_update);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        GlobalData.personalresponse = sharedPreferences.getString("USER_DETAILS", GlobalData
                .personalresponse);
        skillupdatelist = (Helper) findViewById(R.id.skillsupdatelist);
        skillupdatelist.setExpanded(true);
        Button submit = (Button) findViewById(R.id.js_try_Skill_update_Submit);
        try {
            JSONObject responseObj = new JSONObject(GlobalData.personalresponse);
            if (GlobalData.personalresponse != null && !GlobalData.personalresponse.isEmpty()) {
                Gson gson = new Gson();
                ArrayList<Skill> skillupdateList = gson.fromJson(responseObj.getString("skills_list"),
                        new TypeToken<ArrayList<Skill>>() {
                        }.getType());
                SkillUpdateList_Adpater skilllistAdapter = new SkillUpdateList_Adpater(MyProfile_EDIT_Skill.this,
                        skillupdateList);
                skillupdatelist.setAdapter(skilllistAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
        }
        ImageButton skill_edit_h = (ImageButton) findViewById(R.id.js_r_h);
        skill_edit_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_EDIT_Skill.this, Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackAlertDialog().isBackDialog(MyProfile_EDIT_Skill.this);
            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String getmessage = "success";
                int childCount = skillupdatelist.getChildCount();
                JSONArray jsonArray = new JSONArray();
                for (int c = 0; c < childCount; c++) {
                    View childView = skillupdatelist.getChildAt(c);
                    Button coursename = (Button) childView.findViewById(R.id.js_edit_skillname);
                    Button courseexp = (Button) childView.findViewById(R.id.js_edit_exp);
                    TextView updateskillid = (TextView) childView.findViewById(R.id.updateskillid);
                    //String percentage = courseexp.getSelectedItem().toString();
                    String skillname = coursename.getText().toString();
                    skillupdatevalue = new JSONObject();
                    try {
                        skillupdatevalue.put("skill_id", updateskillid.getText().toString());
                        skillupdatevalue.put("jobseeker_id", GlobalData.login_status);
                        skillupdatevalue.put("skill_name", skillname);
                        skillupdatevalue.put("experience", courseexp.getText().toString());
                    } catch (JSONException ignored) {
                    }
                    if (!courseexp.getText().toString().equalsIgnoreCase(getString(R.string.experience))) {
                        jsonArray.put(skillupdatevalue);
                        getmessage = "success";
                    } else {
                        getmessage = "failure";
                        Toast.makeText(getApplicationContext(), getString(R.string.expvalidation), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                if (getmessage.equalsIgnoreCase("success")) {
                    JSONObject studentsObj = new JSONObject();
                    try {
                        studentsObj.put("skills_list", jsonArray);
                    } catch (JSONException ignored) {
                    }
                    getjsonskillupdatevalues = studentsObj.toString();
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new updateSkills().execute(getjsonskillupdatevalues);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    private class updateSkills extends AsyncTask<String, String, String> {
        String updatepersonalresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_EDIT_Skill.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("skills", args[0]);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "skills_update.php").post
                    (requestBody).build();
            OkHttpClient client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                updatepersonalresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (updatepersonalresponse != null && !updatepersonalresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(updatepersonalresponse);
                    String getStatus = responseObj.getString("status");
                    int statuscode = responseObj.getInt("status_code");
                    Toast.makeText(getBaseContext(), getStatus, Toast.LENGTH_SHORT).show();
                    if (statuscode == 1) {
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}
