package com.jobsearchtry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.webkit.MimeTypeMap;

import com.jobsearchtry.utils.DisplayToastMessage;

import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.EducationList_Adpater;
import com.jobsearchtry.adapter.EmploymentList_Adpater;
import com.jobsearchtry.adapter.SkillList_Adpater;
import com.jobsearchtry.utils.FileUtils;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.DesiredJob;
import com.jobsearchtry.wrapper.Education;
import com.jobsearchtry.wrapper.Employment;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.SkillCategory;
import com.jobsearchtry.wrapper.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Dialog;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jobsearchtry.R.id.desired_editicon1;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyProfileActivity extends Activity {
    private ImageButton resume_deleteicon, desired_editicon1, myprofile_skill_add,
            myprofile_desire_add, resume_add;
    private TextView myprofile_name, myprofile_mobilenumber, myprofile_emailid,
            myprofile_location, myprofile_gender,
            myprofile_dob, myprofile_languagesknown, myprofile_desired_location,
            myprofile_desired_role, myprofile_desired_industry,
            myprofile_desired_jobtype, myprofile_desired_salary,
            myprofile_resume_name, myprofile_exp, myprofile_salary, myprofile_desi, myprofile_age,
            myprofile_role;
    private Helper emplymentlist, educationlist, skilllist;
    private ProgressDialog pg;
    private File file1;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath, getStatus, languages;
    private FileBody bin1;
    //private MySwitch showmyprofile;
    public static Callback callback;
    private OkHttpClient client = null;
    private Dialog alertDialog;
    ToggleButton showmyprofile;
    public LinearLayout desire_lay, myprofile_email_lay;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GlobalData.joblistfrom = "RL";
        startActivity(new Intent(MyProfileActivity.this, Homepage.class));
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        showmyprofile = (ToggleButton) findViewById(R.id.toggle_douneedjob);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.mobilenumber = sharedPreferences.getString("MN", GlobalData.mobilenumber);
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        ImageButton profile_h = (ImageButton) findViewById(R.id.js_r_h);
        profile_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfileActivity.this, Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfileActivity.this, Homepage.class));
                finish();
            }
        });
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            StrictMode.ThreadPolicy threadpolicy = new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build();
            StrictMode.setThreadPolicy(threadpolicy);
        }
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetPersonalDetail().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton personalIcon = (ImageButton) findViewById(R.id.personal_editicon);
        emplymentlist = (Helper) findViewById(R.id.employmentlist);
        emplymentlist.setExpanded(true);
        emplymentlist.setFocusable(false);
        educationlist = (Helper) findViewById(R.id.educationlist);
        educationlist.setExpanded(true);
        educationlist.setFocusable(false);
        skilllist = (Helper) findViewById(R.id.skillslist);
        skilllist.setExpanded(true);
        skilllist.setFocusable(false);
        // default
        myprofile_exp = (TextView) findViewById(R.id.myprofile_exp);
        myprofile_salary = (TextView) findViewById(R.id.myprofile_salary);
        myprofile_desi = (TextView) findViewById(R.id.myprofile_desi);
        // personal
        myprofile_mobilenumber = (TextView) findViewById(R.id.myprofile_mobilenumber);
        myprofile_emailid = (TextView) findViewById(R.id.myprofile_emailid);
        myprofile_location = (TextView) findViewById(R.id.myprofile_location);
        myprofile_role = (TextView) findViewById(R.id.myprofile_role);
        myprofile_gender = (TextView) findViewById(R.id.myprofile_gender);
        myprofile_name = (TextView) findViewById(R.id.myprofile_name);
        myprofile_dob = (TextView) findViewById(R.id.myprofile_dob);
        myprofile_age = (TextView) findViewById(R.id.myprofile_age);
        myprofile_languagesknown = (TextView) findViewById(R.id.myprofile_languagesknown);
        myprofile_email_lay = (LinearLayout) findViewById(R.id.email_lay);
        personalIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MyProfileActivity.this, MyProfile_Personal.class));
            }
        });
        ImageButton myprofile_employment_add = (ImageButton) findViewById(R.id.myprofile_employment_add);
        myprofile_employment_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MyProfileActivity.this, MyProfile_ADD_Employment.class));
            }
        });
        ImageButton myprofile_education_add = (ImageButton) findViewById(R.id.myprofile_education_add);
        myprofile_education_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MyProfileActivity.this, MyProfile_ADD_Education.class));
            }
        });
        myprofile_skill_add = (ImageButton) findViewById(R.id.myprofile_skills_add);
        myprofile_skill_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (GlobalData.skillEditList != null && GlobalData.skillEditList.size() != 0) {
                    startActivity(new Intent(MyProfileActivity.this, MyProfile_ADD_Skill.class));
                }
            }
        });
        // desiredjob
        myprofile_desired_location = (TextView) findViewById(R.id.myprofile_desired_location);
        myprofile_desired_role = (TextView) findViewById(R.id.myprofile_desired_role);
        myprofile_desired_industry = (TextView) findViewById(R.id.myprofile_desired_industry);
        myprofile_desired_jobtype = (TextView) findViewById(R.id.myprofile_desired_jobtype);
        myprofile_desired_salary = (TextView) findViewById(R.id.myprofile_desired_salary);
        desired_editicon1 = (ImageButton) findViewById(R.id.desired_editicon1);
        desired_editicon1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MyProfileActivity.this, MyProfile_DesiredJob.class));
            }
        });
        desire_lay = (LinearLayout) findViewById(R.id.desire_lay);
        myprofile_desire_add = (ImageButton) findViewById(R.id.myprofile_desire_add);
        myprofile_desire_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileActivity.this, MyProfile_DesiredJob.class));
            }
        });
        // resume
        resume_add = (ImageButton) findViewById(R.id.resume_add);
        resume_deleteicon = (ImageButton) findViewById(R.id.resume_deleteicon);
        myprofile_resume_name = (TextView) findViewById(R.id.myprofile_resume_name);
        myprofile_resume_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String viewresume = GlobalData.login_status + "_" + myprofile_resume_name.getText().toString();
                if (myprofile_resume_name.getText().toString().equalsIgnoreCase("-") || myprofile_resume_name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.resumenotavailable), Toast
                            .LENGTH_SHORT).show();
                } else {
                    GlobalData.resume = "resume/" + viewresume;
                    startActivity(new Intent(MyProfileActivity.this, View_Resume.class));
                }
            }
        });
        resume_deleteicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkresume = GlobalData.login_status + "_" + myprofile_resume_name.getText().toString();
                if (checkresume.equalsIgnoreCase("-") || checkresume.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.resumenotavailable), Toast
                            .LENGTH_SHORT).show();
                } else {
                    alertDialog = new Dialog(MyProfileActivity.this,R.style.MyThemeDialog);
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setCanceledOnTouchOutside(false);
                    View emppromptView = View.inflate(MyProfileActivity.this, R.layout.delete_popup, null);
                    alertDialog.setContentView(emppromptView);
                    alertDialog.show();
                    TextView f_popupheader = (TextView) emppromptView.findViewById(R.id
                            .d_popupheader);
                    f_popupheader.setText(R.string.deleteconfirm);
                    TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                            .d_popup_subheader);
                    f_popupsubheader.setText(R.string.deleresume);
                    Button no = (Button) emppromptView.findViewById(R.id.d_no);
                    Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                    yes.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                                new DeleteResume().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
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
        resume_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(MyProfileActivity.this, android.Manifest
                            .permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                            .PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission
                                .WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        Intent target = FileUtils.createGetContentIntent();
                        Intent intent = Intent.createChooser(target, "Select File");
                        try {
                            startActivityForResult(intent, SELECT_PICTURE);
                        } catch (ActivityNotFoundException ignored) {

                        }
                    }
                } else {
                    Intent target = FileUtils.createGetContentIntent();
                    Intent intent = Intent.createChooser(target, "Select File");
                    try {
                        startActivityForResult(intent, SELECT_PICTURE);
                    } catch (ActivityNotFoundException ignored) {

                    }
                }

            }
        });
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    new GetPersonalDetail().execute();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
                return false;
            }
        };
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lang_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ta:
                setLocale("ta");
                return true;
            case R.id.eng:
                setLocale("en");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /*public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MyProfileActivity.class);
        startActivity(refresh);
        finish();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetPersonalDetail().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }

    }

    private class GetPersonalDetail extends AsyncTask<String, String, String> {
        String personalresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pg = new ProgressDialog(MyProfileActivity.this, R.style.MyTheme);
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
            Request request = new Request.Builder().url(GlobalData.url + "jobseeker_myprofile" +
                    ".php").post(requestBody)
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                personalresponse = response.body().string();
            } catch (IOException e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (personalresponse != null && !personalresponse.contains("connectionFailure")) {
                GlobalData.personalresponse = personalresponse;
                try {
                    JSONObject responseObj = new JSONObject(personalresponse);
                    Gson gson = new Gson();
                    User user = gson.fromJson(responseObj.getString("profile_view"), new
                            TypeToken<User>() {
                            }.getType());
                    GlobalData.getRoleID = user.getRole_group_id();
                    // default
                    char verifyfirstletter = user.gettotal_exp_years().charAt(0);
                    if (verifyfirstletter == '0') {
                        String getexp = user.gettotal_exp_years().substring(2);
                        if (getexp.equalsIgnoreCase("0")) {
                            myprofile_exp.setText(getString(R.string.exp) + ":" + " " + "Fresher");
                        } else if (getexp.equalsIgnoreCase("1")) {
                            myprofile_exp.setText(getString(R.string.exp) + ":" + " " + getexp + " " + "Month");
                        } else
                            myprofile_exp.setText(getString(R.string.exp) + ":" + " " + getexp + " " + "Months");
                    } else {
                        myprofile_exp.setText(getString(R.string.exp) + ":" + " " + user.gettotal_exp_years() + " "
                                + "Yrs");
                    }
                    if (!user.getSalary().equalsIgnoreCase("0")) {
                        myprofile_salary.setVisibility(View.VISIBLE);
                        myprofile_salary.setText("Rs." + user.getSalary() + " per month");
                    } else {
                        myprofile_salary.setVisibility(View.GONE);
                    }
                    myprofile_desi.setText(user.getJob_Role());
                    getStatus = user.getShowMyProfile();
                    final int valuemax = (int) getResources().getDimension(R.dimen.buttonHeightToSmall);
                    final int valuemin = (int) getResources().getDimension(R.dimen.margintop);
                    if (getStatus.equalsIgnoreCase("0")) {
                        showmyprofile.setChecked(false);
                        showmyprofile.setTextOff("No");
                        showmyprofile.setTextOn("Yes");
                        showmyprofile.setPadding(valuemax, 0, valuemin, 0);
                    } else {
                        showmyprofile.setChecked(true);
                        showmyprofile.setTextOff("No");
                        showmyprofile.setTextOn("Yes");
                        showmyprofile.setPadding(valuemin, 0, valuemax, 0);
                    }
                    showmyprofile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                            if (showmyprofile.isChecked()) {
                                showmyprofile.setPadding(valuemin, 0, valuemax, 0);
                                showmyprofile.setTextOff("No");
                                showmyprofile.setTextOn("Yes");
                            } else {
                                showmyprofile.setTextOff("No");
                                showmyprofile.setTextOn("Yes");
                                showmyprofile.setPadding(valuemax, 0, valuemin, 0);
                            }
                            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                                new ShowMyProfileUpdate().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // personal
                    myprofile_mobilenumber.setText(user.getMobile());
                    String getEmailId = user.getEmailId();
                    if (!(getEmailId != null && getEmailId.isEmpty())) {
                        myprofile_email_lay.setVisibility(View.VISIBLE);
                        myprofile_emailid.setText(user.getEmailId());
                    } else {
                        myprofile_email_lay.setVisibility(View.GONE);
                    }
                    myprofile_role.setText(user.getJob_Role());
                    myprofile_gender.setText(user.getSex());
                    myprofile_location.setText(user.getLocation());
                    if (user.getSex().equalsIgnoreCase("Female")) {
                        myprofile_name.setText("Ms." + " " + user.getUserName());
                    } else if (user.getSex().equalsIgnoreCase("Male")) {
                        myprofile_name.setText("Mr." + " " + user.getUserName());
                    } else {
                        myprofile_name.setText(user.getUserName());
                    }
                    myprofile_dob.setText(user.getDOB());
                    myprofile_age.setText(user.getAge());
                    myprofile_languagesknown.setText(user.getLanguages());
                    GlobalData.mobilenumber = user.getMobile();
                    GlobalData.username = user.getUserName();
                    if (!languages.equalsIgnoreCase("English")) {
                        if (!user.getLocation_local().isEmpty() && user.getLocation_local() != null) {
                            myprofile_location.setText(user.getLocation_local());
                        }
                        if (user.getRole_name_local() != null) {
                            myprofile_role.setText(user.getRole_name_local());
                            myprofile_desi.setText(user.getRole_name_local());
                        }
                        if (!user.getGender_local().isEmpty() && user.getGender_local() != null) {
                            myprofile_gender.setText(user.getGender_local());
                        }
                    }
                    GlobalData.personalresponse = personalresponse;
                    selectedImagePath = user.getResume();
                    if (selectedImagePath.equalsIgnoreCase("-")) {
                        resume_add.setVisibility(View.VISIBLE);
                        resume_deleteicon.setVisibility(View.INVISIBLE);
                    } else {
                        resume_add.setVisibility(View.GONE);
                        resume_deleteicon.setVisibility(View.VISIBLE);
                        selectedImagePath = selectedImagePath.substring(7);
                        if (selectedImagePath.length() > 0) {
                            String splitted[] = selectedImagePath.split("_", 2);
                            myprofile_resume_name.setText(splitted[1]);
                        }
                    }
                    ArrayList<Employment> emplymentList = gson.fromJson(responseObj.getString("employment_list"),
                            new TypeToken<ArrayList<Employment>>() {
                            }.getType());
                    if (emplymentList.size() != 0) {
                        emplymentlist.setVisibility(View.VISIBLE);
                        myprofile_exp.setVisibility(View.VISIBLE);
                        myprofile_salary.setVisibility(View.VISIBLE);
                        EmploymentList_Adpater emplymentlistAdapter = new EmploymentList_Adpater(MyProfileActivity.this,
                                emplymentList);
                        emplymentlist.setAdapter(emplymentlistAdapter);
                    } else {
                        emplymentlist.setVisibility(View.GONE);
                        myprofile_exp.setVisibility(View.GONE);
                        myprofile_salary.setVisibility(View.GONE);
                    }
                    ArrayList<Education> educationList = gson.fromJson(responseObj.getString("education_list"),
                            new TypeToken<ArrayList<Education>>() {
                            }.getType());
                    if (educationList.size() != 0) {
                        EducationList_Adpater educationlistAdapter = new EducationList_Adpater(MyProfileActivity.this,
                                educationList);
                        educationlist.setAdapter(educationlistAdapter);
                    }
                    ArrayList<Skill> skillList = gson.fromJson(responseObj.getString("skills_list"), new
                            TypeToken<ArrayList<Skill>>() {
                            }.getType());
                    if (skillList.size() != 0) {
                        skilllist.setVisibility(View.VISIBLE);
                        SkillList_Adpater skilllistAdapter = new SkillList_Adpater(MyProfileActivity.this,
                                skillList);
                        skilllist.setAdapter(skilllistAdapter);
                    } else {
                        skilllist.setVisibility(View.GONE);
                    }
                    GlobalData.skillEditList = new ArrayList<>();
                    GlobalData.skillEditList = gson.fromJson(responseObj.getString
                                    ("skills"),
                            new TypeToken<ArrayList<Skill>>() {
                            }.getType());
                    if (GlobalData.skillEditList.size() != 0) {
                        myprofile_skill_add.setBackgroundColor(Color.parseColor("#ffffff"));
                    } else {
                        myprofile_skill_add.setBackgroundColor(Color.parseColor("#f2efe9"));
                    }
                    DesiredJob desiredjob = gson.fromJson(responseObj.getString("desired_view"),
                            new TypeToken<DesiredJob>() {
                            }.getType());
                    int desire_status = responseObj.getInt("desired_view_status");
                    if (desire_status == 1) {
                        desire_lay.setVisibility(View.VISIBLE);
                        myprofile_desire_add.setVisibility(View.GONE);
                        desired_editicon1.setVisibility(View.VISIBLE);
                        myprofile_desired_location.setText(desiredjob.getLocation());
                        myprofile_desired_role.setText(desiredjob.getRole());
                        myprofile_desired_industry.setText(desiredjob.getIndustry());
                        myprofile_desired_jobtype.setText(desiredjob.getJob_type());
                        myprofile_desired_salary.setText(desiredjob.getSalary());
                        if (!languages.equalsIgnoreCase("English")) {
                            if (!desiredjob.getDesiredlocation_local().isEmpty() && desiredjob.getDesiredlocation_local() != null) {
                                myprofile_desired_location.setText(desiredjob.getDesiredlocation_local());
                            }
                            if (!desiredjob.getDesired_role_name_local().isEmpty() && desiredjob.getDesired_role_name_local() != null) {
                                myprofile_desired_role.setText(desiredjob.getDesired_role_name_local());
                            }
                            if (!desiredjob.getDesired_industry_local().isEmpty() && desiredjob.getDesired_industry_local() != null) {
                                myprofile_desired_industry.setText(desiredjob.getDesired_industry_local());
                            }
                            if (!desiredjob.getDesired_jobtype_local().isEmpty() && desiredjob.getDesired_jobtype_local() != null) {
                                myprofile_desired_jobtype.setText(desiredjob.getDesired_jobtype_local());
                            }
                        }
                    } else {
                        desire_lay.setVisibility(View.GONE);
                        myprofile_desire_add.setVisibility(View.VISIBLE);
                        desired_editicon1.setVisibility(View.GONE);
                    }
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(MyProfileActivity.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("MN", GlobalData.mobilenumber);
                    editor.putString("LS", GlobalData.login_status);
                    editor.putString("NAME", GlobalData.username);
                    editor.putString("USER_DETAILS", GlobalData.personalresponse);
                    editor.apply();
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if (data != null) {
                    // Get the URI of the selected file
                    final Uri uri = data.getData();
                    try {
                        // Get the file path from the URI
                        selectedImagePath = FileUtils.getPath(this, uri);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                        if (fileExtension.equalsIgnoreCase("pdf") || fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx")) {
                            file1 = new File(selectedImagePath);
                            double bytes = file1.length();
                            double kilobytes = (bytes / 1024);
                            if (kilobytes < 300) {
                                if (new UtilService().isNetworkAvailable(MyProfileActivity.this)) {
                                    new createFile().execute();
                                } else {
                                    Toast.makeText(MyProfileActivity.this,
                                            getString(R.string.checkconnection), Toast
                                                    .LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                new DisplayToastMessage().isToastMessage(MyProfileActivity.this, getString(R.string.fileexceeded));
                            }
                        } else {
                            new DisplayToastMessage().isToastMessage(MyProfileActivity.this, getString(R.string.unsupportedfile));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    Intent target = FileUtils.createGetContentIntent();
                    Intent intent = Intent.createChooser(target, "Select File");
                    try {
                        startActivityForResult(intent, SELECT_PICTURE);
                    } catch (ActivityNotFoundException ignored) {
                    }
                } else {
                    Toast.makeText(MyProfileActivity.this, "Permission Denied,You cannot access the storage", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    ;
    /*private int uploadFile(String sourceFileUri, File file1) {
        int serverResponseCode = 0;
        final String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        Log.e("file", file1.getName());
        Log.e("fileuri", sourceFileUri);
        if (!file1.exists()) {
            if (pg != null && pg.isShowing())
                pg.dismiss();
           runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MyProfileActivity.this, "Source File not exist " + fileName, Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                file1 = new File(Environment.getExternalStorageDirectory(), file1.getName());
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(file1);
                URL url = new URL(GlobalData.url + "resume.php");
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Resume", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"Resume\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"jobseeker_id\"" + GlobalData.login_status);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                           Toast.makeText(getApplicationContext(), "Successfully resume was added", Toast
                                    .LENGTH_SHORT).show();
                            resume_add.setVisibility(View.GONE);
                            resume_editicon.setVisibility(View.VISIBLE);
                            resume_deleteicon.setVisibility(View.VISIBLE);
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                if (pg != null && pg.isShowing())
                    pg.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyProfileActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                if (pg != null && pg.isShowing())
                    pg.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyProfileActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
            return serverResponseCode;
        }
    } // End else block*/


    class createFile extends AsyncTask<String, String, String> {
        String resumeresult;
        RequestBody formBody;
        Response response;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfileActivity.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            String urlString = GlobalData.url + "resume.php";
            if (!languages.equalsIgnoreCase("English")) {
                formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Resume", file1.getName(),
                                RequestBody.create(MediaType.parse("form-data"), file1))
                        .addFormDataPart("jobseeker_id", GlobalData.login_status)
                        .addFormDataPart("languages", languages)
                        .build();
            } else {
                formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Resume", file1.getName(),
                                RequestBody.create(MediaType.parse("form-data"), file1))
                        .addFormDataPart("jobseeker_id", GlobalData.login_status)
                        .build();
            }
            request = new Request.Builder().url(GlobalData.url + "resume.php")
                    .post(formBody).build();

            client = new OkHttpClient();
            try {
                response = client.newCall(request).execute();
                resumeresult = response.body().string();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (resumeresult != null && !resumeresult.contains
                    ("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(resumeresult);
                    String message = json.getString("status");
                    Toast.makeText(getApplicationContext(),
                            message, Toast.LENGTH_SHORT).show();
                    myprofile_resume_name.setText(file1.getName());
                    resume_add.setVisibility(View.GONE);
                    resume_deleteicon.setVisibility(View.VISIBLE);
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

    private class DeleteResume extends AsyncTask<String, String, String> {
        String deleteresumeresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
            }
            pg = new ProgressDialog(MyProfileActivity.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData
                    .login_status);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "resume_delete.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                deleteresumeresponse = response.body().string();
            } catch (IOException e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (deleteresumeresponse != null && !deleteresumeresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject delresume = new JSONObject(deleteresumeresponse);
                    int deletestatus = delresume.getInt("status");
                    String succmessage = delresume.getString("message");
                    Toast.makeText(getApplicationContext(), succmessage, Toast.LENGTH_SHORT)
                            .show();
                    if (deletestatus == 0) {
                        Toast.makeText(getApplicationContext(), succmessage, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        myprofile_resume_name.setText("-");
                        resume_add.setVisibility(View.VISIBLE);
                        resume_deleteicon.setVisibility(View.INVISIBLE);
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

    private class ShowMyProfileUpdate extends AsyncTask<String, String, String> {
        String gsonresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
            }
            pg = new ProgressDialog(MyProfileActivity.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", GlobalData.mobilenumber)
                    .build();
            Request request = new Request.Builder().url(GlobalData.url + "change_profile_status" +
                    ".php").post(formBody)
                    .build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                gsonresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (gsonresponse != null && !gsonresponse.contains("connectionFailure")) {
                try {
                    JSONObject getprofilestatus = new JSONObject(gsonresponse);
                    String updateStatus = getprofilestatus.getString("show_my_profile");
                    if (updateStatus.equalsIgnoreCase("1")) {
                        Toast.makeText(getBaseContext(), getString(R.string.profileactive), Toast
                                .LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.profileinactive), Toast
                                .LENGTH_SHORT).show();
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