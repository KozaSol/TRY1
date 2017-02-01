package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.CityAdapter;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.Experience;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.JS_CATEGORY;
import com.jobsearchtry.wrapper.JobType;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Salary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.jobsearchtry.utils.GlobalData.getyesnoflag;

public class JobSearch_Filter extends Activity {
    private TextView location, role, industry, salary, jobtype, experience, skill, gender,
            cl_cityheader, stateselectedloc,
            cityselectedloc;
    private Dialog alertDialog;
    private Button locationlist, rolelist, industrylist, salarylist, jobtypelist, experiencelist,
            skilllist, genderlist;
    private static final String M_Filter_Job = "filter_job";
    private ArrayList<Gender> select_gender = null;
    private ArrayList<Industry> industriesList = null;
    private ArrayList<Role> RoleList = null;
    private ArrayList<JobType> JobTypeList = null;
    private ArrayList<Salary> SalaryList = null;
    private ArrayList<Experience> experienceList = null;
    private ArrayList<String> selectedLocationList = new ArrayList<>();
    private ArrayList<String> selectedLocationLocalList = null;
    private final ArrayList<String> selectedRoleList = new ArrayList<>();
    private ArrayList<String> selectedRoleLocalList = null;
    private final ArrayList<String> selectedJobTypeList = new ArrayList<>();
    private ArrayList<String> selectedJobTypeLocalList = null;
    private final ArrayList<String> selectedIndustriesList = new ArrayList<>();
    private ArrayList<String> selectedIndustriesLocalList = null;
    private final ArrayList<String> selectedJobTypeIDList = new ArrayList<>();
    /* private ArrayList<String> SelectedskillList = new ArrayList<>();
       private ArrayList<SkillCategory> skillCategoryList = null;
       private ArrayList<Skill> skillList = null;
       private ArrayAdapter<SkillCategory> scadapter;
       private ArrayAdapter<Skill> skadapter;*/
    private boolean[] isCheckedLocation, isCheckedrole, isCheckedIndustry, isCheckedJobType;
    //private boolean[] isCheckedSkill,isCheckedSkillCategory;
    private String getFilterStatus, getState, getStateID, getFrom =
            "State", languages, getFilterStatusTamil, GenderLocal, ExperienceLocal, backtoLanding = "false",
            oldLocation, oldIndustry, oldRole, oldExperience, oldGender, oldSalary, oldJobtype;
    //private String  getSkillCategory, getSkillCategoryID;
    private ProgressDialog pg;
    private ArrayAdapter<Gender> genderadapter;
    private ArrayAdapter<String> adapter, adapter1;
    private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private ArrayAdapter<JobType> jobTypeAdapter;
    private ArrayAdapter<Industry> industryAdapter;
    private ArrayAdapter<Experience> expAdapter;
    private ArrayAdapter<Salary> salaryAdapter;
    private int indexsalary = -1, indexcity = -1, indexexp = -1, indexgender = -1,
            getcount = 0, indexrole = -1;
    private OkHttpClient client = null;
    private LinearLayout locstate_view, city_view;
    private ListView filterstate, filtercity;
    private AutoCompleteTextView locfilt_citysearch, autocity;
    private Boolean isbackTrue = true, isDetailEmpty = true;

    @Override
    public void onBackPressed() {
        if (backtoLanding.equalsIgnoreCase("true")) {
            if (isbackTrue) {
                onbackClick();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        GlobalData.getrolepage = "DR";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (JobSearch_Filter.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("ROLEPAGE", GlobalData.getrolepage);
        editor.apply();
        ImageButton filter_h = (ImageButton) findViewById(R.id.js_r_h);
        filter_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backtoLanding.equalsIgnoreCase("true")) {
                    if (isbackTrue) {
                        onbackClick();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
        location = (TextView) findViewById(R.id.location_filter_text);
        locationlist = (Button) findViewById(R.id.location_filter);
        role = (TextView) findViewById(R.id.role_filter_text);
        rolelist = (Button) findViewById(R.id.role_filter);
        industry = (TextView) findViewById(R.id.industry_filter_text);
        industrylist = (Button) findViewById(R.id.industry_filter);
        salary = (TextView) findViewById(R.id.minsal_filter_text);
        salarylist = (Button) findViewById(R.id.minsal_filter);
        jobtype = (TextView) findViewById(R.id.jobtype_filter_text);
        jobtypelist = (Button) findViewById(R.id.jobtype_filter);
        experience = (TextView) findViewById(R.id.experience_filter_text);
        experiencelist = (Button) findViewById(R.id.experience_filter);
        // skill = (TextView) findViewById(R.id.skill_filter_text);
        // skilllist = (Button) findViewById(R.id.skill_filter);
        gender = (TextView) findViewById(R.id.gender_filter_text);
        genderlist = (Button) findViewById(R.id.gender_filter);
        Button reset = (Button) findViewById(R.id.resetall_filter);
        Button done = (Button) findViewById(R.id.done_filter);
        SharedPreferences jobfilterPreferences = getSharedPreferences(M_Filter_Job, Context
                .MODE_PRIVATE);
        GlobalData.getPrevLocation = jobfilterPreferences.getString("F_PL", GlobalData
                .getPrevLocation);
        GlobalData.getRole = jobfilterPreferences.getString("F_R", GlobalData.getRole);
        oldRole = GlobalData.getRole;
        GlobalData.getSSalary = jobfilterPreferences.getString("F_S", GlobalData.getSSalary);
        oldSalary = GlobalData.getSSalary;
        GlobalData.getJobType = jobfilterPreferences.getString("F_JT", GlobalData.getJobType);
        oldJobtype = GlobalData.getJobType;
        GlobalData.getJobIDType = jobfilterPreferences.getString("F_JTID", GlobalData
                .getJobIDType);
        getStateID = jobfilterPreferences.getString("F_ST", getStateID);
        GlobalData.getExperience = jobfilterPreferences.getString("F_E", GlobalData.getExperience);
        oldExperience = GlobalData.getExperience;
        GlobalData.getSkill = jobfilterPreferences.getString("F_SK", GlobalData.getSkill);
        GlobalData.getIndustry = sharedPreferences.getString("F_I", GlobalData.getIndustry);
        oldIndustry = GlobalData.getIndustry;
        GlobalData.getGender = jobfilterPreferences.getString("F_G", GlobalData.getGender);
        oldGender = GlobalData.getGender;
        //GlobalData.getSkill = jobfilterPreferences.getString("F_SK", GlobalData.getSkill);
        GlobalData.getPrevLocation = GlobalData.getLocation;
        SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                (JobSearch_Filter.this);
        GlobalData.getLocation = sharedPreferencess.getString("F_L", GlobalData.getLocation);
        oldLocation = GlobalData.getLocation;
        GlobalData.getNearLocation = sharedPreferencess.getString("F_NL", GlobalData.getNearLocation);
        GlobalData.getFinalLocation = sharedPreferencess.getString("F_FL", GlobalData.getFinalLocation);
        GlobalData.getdefaultLocationHome = sharedPreferencess.getBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        GlobalData.getyesnoflag = sharedPreferences.getString("YNFLAG", GlobalData.getyesnoflag);
        if (((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty())
                && GlobalData.getRole == null && GlobalData.getCRole == null
                && GlobalData.getSSalary == null
                && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                && GlobalData.getExperience == null
                && (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                && GlobalData.getGender == null) && (GlobalData.getLocation == null || GlobalData.getLocation.isEmpty())) {
            isDetailEmpty = false;
        } else if ((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) || GlobalData.getRole != null
                || GlobalData.getCRole != null || GlobalData.getSSalary != null
                || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                || GlobalData.getExperience != null
                || (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                || GlobalData.getGender != null) {
            isDetailEmpty = true;
        }
        //skill.setText(GlobalData.getSkill);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            if (GlobalData.getjsfilterdata != null && !GlobalData.getjsfilterdata.isEmpty()) {
                Filterdata();
            } else {
                new getRole().execute();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        locationlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.getdefaultLocationHome) {
                    if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                        new getLocation().execute();
                        // new getRole().execute();
                    } else {
                        if (GlobalData.LocationList.size() > 0) {
                            LocationAlert();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (GlobalData.LocationList.size() > 0) {
                        LocationAlert();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        industrylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (industriesList != null && industriesList.size() > 0) {
                    IndustryAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        rolelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoleList != null && RoleList.size() > 0) {
                    RoleAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        salarylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SalaryList != null && SalaryList.size() > 0) {
                    SalaryAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        jobtypelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JobTypeList != null && JobTypeList.size() > 0) {
                    JobTypeAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        experiencelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (experienceList != null && experienceList.size() > 0) {
                    ExpAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        genderlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_gender != null && select_gender.size() > 0) {
                    GenderAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        /*skilllist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skillCategoryList != null && skillCategoryList.size() > 0) {
                    SkillAlert();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check the internet " +
                            "connection", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });*/
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackClick();
            }
        });
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentlocationautoupdate();
                /*if (skillList != null && skillList.size() > 0) {
                    isCheckedSkill = new boolean[skillList.size()];
                    Arrays.fill(isCheckedSkill, false);
                }
                if (skillCategoryList != null && skillCategoryList.size() > 0) {
                    isCheckedSkillCategory = new boolean[skillCategoryList.size()];
                    Arrays.fill(isCheckedSkillCategory, false);
                }*/
                selectedLocationList.clear();
                if (selectedIndustriesList.size() > 0) {
                    selectedIndustriesList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedIndustriesLocalList.clear();
                    }
                }
                if (industriesList != null && industriesList.size() > 0) {
                    isCheckedIndustry = new boolean[industriesList.size()];
                    Arrays.fill(isCheckedIndustry, false);
                }
                if (selectedJobTypeList.size() > 0) {
                    selectedJobTypeIDList.clear();
                    selectedJobTypeList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedJobTypeLocalList.clear();
                    }
                }
                if (JobTypeList != null && JobTypeList.size() > 0) {
                    isCheckedJobType = new boolean[JobTypeList.size()];
                    Arrays.fill(isCheckedJobType, false);
                }
                if (selectedRoleList.size() > 0) {
                    selectedRoleList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedRoleLocalList.clear();
                    }
                }
                if (RoleList != null && RoleList.size() > 0) {
                    isCheckedrole = new boolean[RoleList.size()];
                    Arrays.fill(isCheckedrole, false);
                }
                if (((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) || GlobalData.getRole != null
                        || GlobalData.getSSalary != null
                        || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                        || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                        || GlobalData.getExperience != null
                        //|| (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                        || GlobalData.getGender != null) || (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty())) {
                    backtoLanding = "false";
                }else{
                    backtoLanding = "true";
                }
                //SelectedskillList.clear();
                //GlobalData.getSkill = null;
                getStateID = null;
                GlobalData.getLocation = null;
                GlobalData.getNearLocation = null;
                GlobalData.getFinalLocation = null;
                GlobalData.getRole = null;
                GlobalData.getCRole = null;
                GlobalData.getJobIDType = null;
                GlobalData.getJobType = null;
                GlobalData.getGender = null;
                GlobalData.getExperience = null;
                GlobalData.getIndustry = null;
                GlobalData.getSSalary = null;
                location.setText("");
                locationlist.setText(R.string.select);
                role.setText("");
                rolelist.setText(R.string.select);
                salary.setText("");
                salarylist.setText(R.string.select);
                industry.setText("");
                industrylist.setText(R.string.select);
                jobtype.setText("");
                jobtypelist.setText(R.string.select);
                gender.setText("");
                genderlist.setText(R.string.select);
                experience.setText("");
                experiencelist.setText(R.string.select);
                //skill.setText("");
                // skilllist.setText(R.string.select);
                if (GlobalData.LocationList.size() > 0) {
                    isCheckedLocation = new boolean[GlobalData.LocationList.size()];
                    Arrays.fill(isCheckedLocation, false);
                }

                if (!backtoLanding.equalsIgnoreCase("true")) {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new getRole().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }
                backtoLanding = "true";
            }
        });
    }

    private void currentlocationautoupdate() {
        if (GlobalData.getdefaultLocationHome) {
            GlobalData.getdefaultLocationHome = false;
            SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                    (JobSearch_Filter.this);
            Editor editorr = sharedPreferencess.edit();
            editorr.putBoolean("JSLOC", GlobalData
                    .getdefaultLocationHome).apply();
        }
    }

    private void onbackClick() {
        currentlocationautoupdate();
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                    (JobSearch_Filter.this);
            sharedPreferencess.edit().putString("F_L", GlobalData.getLocation)
                    .putString("F_NL", GlobalData.getNearLocation)
                    .putString("F_FL", GlobalData.getFinalLocation).apply();
            SharedPreferences jobfilterPreferences = getSharedPreferences(M_Filter_Job,
                    Context.MODE_PRIVATE);
            jobfilterPreferences.edit().putString("F_ST", getStateID)
                    .putString("F_PL", GlobalData.getPrevLocation)
                    .putString("F_I", GlobalData.getIndustry).putString("F_R", GlobalData
                    .getRole)
                    .putString("F_S", GlobalData.getSSalary).putString("F_JT", GlobalData
                    .getJobType)
                    .putString("F_JTID", GlobalData.getJobIDType).putString("F_E",
                    GlobalData.getExperience)
                    .putString("F_SK", GlobalData.getSkill).putString("F_G", GlobalData
                    .getGender).apply();
            if (((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) || GlobalData.getRole != null
                    || GlobalData.getSSalary != null
                    || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                    || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                    || GlobalData.getExperience != null
                    //|| (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                    || GlobalData.getGender != null) || (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty())) {
                isDetailEmpty = true;
            }
            if (((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) && GlobalData.getRole == null
                    && GlobalData.getSSalary == null
                    && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                    && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                    && GlobalData.getExperience == null
                    // && (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                    && GlobalData.getGender == null) && (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getFinalLocation))
                    ) {
                Log.e("log1", "if");
                new getFilterData().execute();
            } else if (((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) && GlobalData.getRole == null
                    && GlobalData.getSSalary == null
                    && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                    && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                    && GlobalData.getExperience == null
                    // && (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                    && GlobalData.getGender == null) && (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getPrevLocation) &&
                            (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getFinalLocation)))) {
                new getFilterData().execute();
            } else {
                if (isDetailEmpty) {
                    GlobalData.getFinalLocation = GlobalData.getLocation;
                    final SharedPreferences sharedPreferences1 = PreferenceManager
                            .getDefaultSharedPreferences(JobSearch_Filter.this);
                    Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("F_FL", GlobalData.getFinalLocation);
                    editor1.apply();
                    Log.e("log3", "if");
                    new getFilterData().execute();
                } else {
                    finish();
                    //new getFilterData().execute();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    class getLocation extends AsyncTask<String, String, String> {
        String rresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(JobSearch_Filter.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody.FORM);
            String languages = getResources().getConfiguration().locale.getDisplayLanguage();
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", GlobalData.getLocation);
            }
            paramsadd.addFormDataPart("locationflag", "Yes");
            //if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
            //    paramsadd.addFormDataPart("locationflag", "No");
            //}
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            } else {
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                }
            }
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "filterlocation.php").post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                rresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (rresponse != null && !rresponse.contains("connectionFailure")) {
                try {
                    GlobalData.LocationList = new ArrayList<>();
                    GlobalData.CityList = new ArrayList<>();
                    JSONObject responseObj = new JSONObject(rresponse);
                    Gson gson = new Gson();
                    GlobalData.LocationList = gson.fromJson(responseObj.getString("locations"),
                            new TypeToken<ArrayList<FilterLocation>>() {
                            }.getType());
                    if (getStateID != null && !getStateID.isEmpty()) {
                        for (int i = 0; i < GlobalData.LocationList.size(); i++) {
                            if (GlobalData.LocationList.get(i).getId().equals(getStateID)) {
                                GlobalData.CityList = GlobalData.LocationList.get(i).getCities();
                            }
                        }
                    } else {
                        GlobalData.CityList = GlobalData.LocationList.get(0).getCities();
                    }
                    GlobalData.locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    GlobalData.MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    LocationAlert();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void savelocationpopup() {
        GlobalData.getdefaultLocationHome = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (JobSearch_Filter.this);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        editor.apply();
    }

    private class getRole extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSearch_Filter.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            Request request;
            try {
                MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                        .FORM);
                if (!languages.equalsIgnoreCase("English")) {
                    paramsadd.addFormDataPart("languages", languages);
                }
                if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                    paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
                }
                if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                    paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
                }
                if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                    paramsadd.addFormDataPart("state", GlobalData.getHomeState);
                }
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                    paramsadd.addFormDataPart("location", GlobalData.getLocation);
                }
                paramsadd.addFormDataPart("locationflag", "Yes");
                //if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
                //    paramsadd.addFormDataPart("locationflag", "No");
                //}
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role", GlobalData.getCRole);
                }
                if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getRole);
                } else {
                    if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                        paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                    }
                }
                if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                    paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
                }
                if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                    paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
                }
                if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                    paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
                }
                if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                    paramsadd.addFormDataPart("experience", GlobalData.getExperience);
                }
                if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                    paramsadd.addFormDataPart("Gender", GlobalData.getGender);
                }
                MultipartBody requestBody = paramsadd.build();
                request = new Request.Builder().url(GlobalData.url + "jobsearch_filterlist.php").post
                        (requestBody).build();
                client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                GlobalData.getjsfilterdata = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            Filterdata();
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }

    private void Filterdata() {
        if (GlobalData.getjsfilterdata != null && !GlobalData.getjsfilterdata.isEmpty()) {
            // location
            GlobalData.LocationList = new ArrayList<>();
            try {
                JSONObject json = new JSONObject(GlobalData.getjsfilterdata);
                Gson gson = new Gson();
                GlobalData.LocationList = gson.fromJson(json.getString("locations"), new
                        TypeToken<ArrayList<FilterLocation>>() {
                        }.getType());
                //citylist
                GlobalData.locationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                GlobalData.MainlocationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    String removealltext = GlobalData.getLocation.replaceAll("All,", "");
                    location.setText(removealltext);
                    if (!languages.equalsIgnoreCase("English")) {
                        setLocationLocalLang();
                    }
                    locationlist.setText(R.string.edit);
                } else {
                    locationlist.setText(R.string.select);
                }
                //genderlist
                select_gender = gson.fromJson(json.getString("genderlist"), new
                        TypeToken<ArrayList<Gender>>() {
                        }.getType());
                if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                    gender.setText(GlobalData.getGender);
                    if (!languages.equalsIgnoreCase("English")) {
                        setGenderLocalLang();
                    }
                }
                // role
                RoleList = new ArrayList<>();
                RoleList = gson.fromJson(json.getString("role_name"), new
                        TypeToken<ArrayList<Role>>() {
                        }.getType());
                if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
                    if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                        role.setText(GlobalData.getCRole);
                        if (!languages.equalsIgnoreCase("English")) {
                            setRoleLocalLang(GlobalData.getCRole);
                        }
                    } else {
                        role.setText("");
                    }
                } else {
                    if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                        role.setText(GlobalData.getRole);
                        if (!languages.equalsIgnoreCase("English")) {
                            setRoleLocalLang(GlobalData.getRole);
                        }
                    } else {
                        role.setText("");
                    }
                }
                // industry
                industriesList = new ArrayList<>();
                industriesList = gson.fromJson(json.getString("industries"), new
                        TypeToken<ArrayList<Industry>>() {
                        }.getType());
                if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                    industry.setText(GlobalData.getIndustry);
                    if (!languages.equalsIgnoreCase("English")) {
                        setIndustryLocalLang();
                    }
                }
                // salary
                SalaryList = new ArrayList<>();
                SalaryList = gson.fromJson(json.getString("salaryrange"), new
                        TypeToken<ArrayList<Salary>>() {
                        }.getType());
                if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                    salary.setText(GlobalData.getSSalary);
                }
                // jobtype
                JobTypeList = new ArrayList<>();
                JobTypeList = gson.fromJson(json.getString("job_types"), new
                        TypeToken<ArrayList<JobType>>() {
                        }.getType());
                if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty() &&
                        GlobalData.getJobIDType != null
                        && !GlobalData.getJobIDType.isEmpty()) {
                    List<String> jobtypelist = Arrays.asList(GlobalData.getJobType.split(","));
                    List<String> jobtypeidlist = Arrays.asList(GlobalData.getJobIDType.split(","));
                    isCheckedJobType = new boolean[JobTypeList.size()];
                    for (int i = 0; i < jobtypelist.size(); i++) {
                        if (!selectedJobTypeList.contains(jobtypelist.get(i))) {
                            selectedJobTypeList.add(jobtypelist.get(i));
                            selectedJobTypeIDList.add(jobtypeidlist.get(i));
                        }
                        jobtype.setText(GlobalData.getJobType);
                        if (!languages.equalsIgnoreCase("English")) {
                            setJobtypeLocalLang();
                        }
                    }
                }
                // experience
                experienceList = new ArrayList<>();
                experienceList = gson.fromJson(json.getString("experiencelist"), new
                        TypeToken<ArrayList<Experience>>() {
                        }.getType());
                if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                    experience.setText(GlobalData.getExperience);
                    if (!languages.equalsIgnoreCase("English")) {
                        setExperienceLocalLang();
                    }
                }
                // skilllist
                /*skillCategoryList = new ArrayList<>();
                skillCategoryList = gson.fromJson(json.getString("skillcategory"),
                        new TypeToken<ArrayList<SkillCategory>>() {
                        }.getType());
                skillList = new ArrayList<>();
                skillList = skillCategoryList.get(0).getSkills();
                isCheckedSkillCategory = new boolean[skillCategoryList.size()];
                Arrays.fill(isCheckedSkillCategory, false);
                isCheckedSkill = new boolean[skillList.size()];
                Arrays.fill(isCheckedSkill, false);
                if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                    List<String> locationlist = Arrays.asList(GlobalData.getSkill.split(","));
                    isCheckedSkill = new boolean[skillList.size()];
                    indexskill = -1;
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < skillList.size(); j++) {
                            if (!(SelectedskillList.contains(locationlist.get(i)))) {
                                SelectedskillList.add(locationlist.get(i));
                            }
                            if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                                indexskill = j;
                                isCheckedSkill[indexskill] = true;
                            }
                        }
                    }
                } else {
                    isCheckedSkill = new boolean[skillList.size()];
                    Arrays.fill(isCheckedSkill, false);
                }*/
            } catch (JSONException ignored) {
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.connectionfailure), Toast
                    .LENGTH_SHORT).show();
        }
        if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
        } else {
            rolelist.setText(R.string.select);
        }
        if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
            industrylist.setText(R.string.edit);
        } else {
            industrylist.setText(R.string.select);
        }
        if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty()) {
            jobtypelist.setText(R.string.edit);
        } else {
            jobtypelist.setText(R.string.select);
        }
        if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
        /*if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
            skilllist.setText(R.string.edit);
        } else {
            skilllist.setText(R.string.select);
        }*/
    }

    private void LocationAlert() {
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            GlobalData.getPrevLocation = GlobalData.getLocation;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (JobSearch_Filter.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("F_PL", GlobalData.getPrevLocation);
            editor.apply();
        }
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.location_filter_popup, null);
        locstate_view = (LinearLayout) emppromptView.findViewById(R.id.locstate_view);
        city_view = (LinearLayout) emppromptView.findViewById(R.id.city_view);
        filterstate = (ListView) emppromptView.findViewById(R.id.filterlocstatelist);
        filtercity = (ListView) emppromptView.findViewById(R.id.filterloccitylist);
        cl_cityheader = (TextView) emppromptView.findViewById(R.id.cl_cityheader);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ImageButton cl_cityheader_back = (ImageButton) emppromptView.findViewById(R.id
                .cl_cityheader_back);
        autocity = (AutoCompleteTextView) emppromptView.findViewById(R.id.locfil_citysearch);
        locfilt_citysearch = (AutoCompleteTextView) emppromptView.findViewById(R.id
                .locfilt_citysearch);
        stateselectedloc = (TextView) emppromptView.findViewById(R.id.loc_locationadded);
        cityselectedloc = (TextView) emppromptView.findViewById(R.id.loccity_locationadded);
        autocity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autocity.setFocusableInTouchMode(true);
                locfilt_citysearch.setFocusableInTouchMode(false);
                return false;
            }
        });
        locfilt_citysearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                locfilt_citysearch.setFocusableInTouchMode(true);
                autocity.setFocusableInTouchMode(false);
                return false;
            }
        });
        selectedLocationList = new ArrayList<>();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            //location.setText(GlobalData.getLocation);
            locationlist.setText(R.string.edit);
            List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
            for (int i = 0; i < locationlist.size(); i++) {
                if (!(selectedLocationList.contains(locationlist.get(i)))) {
                    selectedLocationList.add(locationlist.get(i));
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            location.setText("");
            locationlist.setText(R.string.select);
        }
        //city back button
        cl_cityheader_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                locstate_view.setVisibility(View.VISIBLE);
                city_view.setVisibility(View.GONE);
                setStateAdapter();
            }
        });
        locstate_view.setVisibility(View.VISIBLE);
        city_view.setVisibility(View.GONE);
        filterstate.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        filtercity.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //state list
        setStateAdapter();
        //statelist onclick
        filterstate.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                getState = GlobalData.LocationList.get(position).getState_name();
                getStateID = GlobalData.LocationList.get(position).getId();
                autocity.setFocusableInTouchMode(false);
                locfilt_citysearch.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                getCityListAdapter();
            }
        });
        //city - autocomplete textview
        if (GlobalData.locationCityList.size() > 0) {
            final CityAdapter loccityAdapter = new CityAdapter(JobSearch_Filter.this, R.layout.spinner_item_text, GlobalData.locationCityList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        Context mContext = this.getContext();
                        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                                .LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.spinner_item_text, parent, false);
                    }
                    TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                    String yourValue = GlobalData.locationCityList.get(position).getCiti_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        yourValue = GlobalData.locationCityList.get(position).getCity_name_local();
                    }
                    textView.setText(yourValue);
                    return textView;
                }
            };
            autocity.setAdapter(loccityAdapter);
            //autocity.setThreshold(1);
            autocity.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    autocity.setText("");
                    if (!selectedLocationList.contains(loccityAdapter.getItem(i).getCiti_name())) {
                        selectedLocationList.add(loccityAdapter.getItem(i).getCiti_name());
                        setStateAdapter();
                    }
                }
            });
        }

        Button done_filter = (Button) emppromptView.findViewById(R.id.locdone_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.locresetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDone();
                alertDialog.dismiss();
            }
        });
        //location - reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                currentlocationautoupdate();
                getState = null;
                getStateID = null;
                if (selectedLocationList.size() > 0) {
                    selectedLocationList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedLocationLocalList.clear();
                    }
                }
                isCheckedLocation = new boolean[GlobalData.CityList.size()];
                Arrays.fill(isCheckedLocation, false);
                GlobalData.getLocation = null;
                GlobalData.getNearLocation = null;
                GlobalData.getFinalLocation = null;
                location.setText("");
                locationlist.setText(R.string.select);
                alertDialog.dismiss();
                savelocationpopup();
                LocationAlert();
            }
        });
        //location - done
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                locationDone();
                alertDialog.dismiss();
            }
        });
        //statelistedittext keyboard done
        autocity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    locationDone();
                    autocity.setFocusableInTouchMode(false);
                    locfilt_citysearch.setFocusableInTouchMode(false);
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        //citylistedittext keyboard done
        locfilt_citysearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    locationDone();
                    autocity.setFocusableInTouchMode(false);
                    locfilt_citysearch.setFocusableInTouchMode(false);
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        if (selectedLocationList.size() > 0) {
            stateselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                @Override
                public boolean onDrawableClick() {
                    getFrom = "State";
                    getSelectedCities();
                    return true;
                }
            });
        }
    }

    private void setSelectedLocation(TextView stateselectedloc, TextView cityselectedloc) {
        selectedLocationLocalList = new ArrayList<>();
        List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
        for (int i = 0; i < locationlist.size(); i++) {
            City cityname = new City();
            cityname.setCiti_name(locationlist.get(i));
            int j = GlobalData.MainlocationCityList.indexOf(cityname);
            if (j != -1) {
                selectedLocationLocalList.add(GlobalData.MainlocationCityList.get(j).getCity_name_local());
            }
        }
        String[] getlocationaded = selectedLocationLocalList.toArray(new String[selectedLocationLocalList.size
                ()]);
        String getlocationarray = Arrays.toString(getlocationaded);
        String getLocationTamil = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocationTamil = getLocationTamil.replace(", ", ",");
        stateselectedloc.setText(getLocationTamil);
        cityselectedloc.setText(getLocationTamil);
    }

    private void locationDone() {
        String[] getLocationaded = selectedLocationList.toArray(new
                String[selectedLocationList.size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() -
                1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        GlobalData.getNearLocation = GlobalData.getLocation;
        GlobalData.getFinalLocation = GlobalData.getLocation;
        final SharedPreferences sharedPreferences1 = PreferenceManager
                .getDefaultSharedPreferences(JobSearch_Filter.this);
        Editor editor1 = sharedPreferences1.edit();
        editor1.putString("F_FL", GlobalData.getFinalLocation);
        editor1.putString("F_NL", GlobalData.getNearLocation);
        editor1.apply();
        reloadLocationData();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            location.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
                setLocationLocalLang();
            }
            locationlist.setText(R.string.edit);
            savelocationpopup();
            currentlocationautoupdate();
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            location.setText("");
            locationlist.setText(R.string.select);
        }
    }

    private void reloadLocationData() {
        if (!(GlobalData.getLocation == null || GlobalData.getLocation.isEmpty()) && (oldLocation == null || oldLocation.isEmpty())) {
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    if (!GlobalData.getLocation.equalsIgnoreCase(oldLocation)) {
                        refreshList("Location");
                    }
                } else {
                    refreshList("Location");
                }
            } else {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    refreshList("Location");
                }
            }
        } else {
            if (GlobalData.getLocation == null || GlobalData.getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    refreshList("Location");
                }
            }
            if (oldLocation == null || oldLocation.isEmpty()) {
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    if (!GlobalData.getLocation.equalsIgnoreCase(oldLocation)) {
                        refreshList("Location");
                    }
                } else {
                    refreshList("Location");
                }
            } else {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    refreshList("Location");
                }
            }
        }
    }

    private void setLocationLocalLang() {
        selectedLocationLocalList = new ArrayList<>();
        List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
        for (int i = 0; i < locationlist.size(); i++) {
            for (int j = 0; j < GlobalData.locationCityList.size(); j++) {
                if (GlobalData.locationCityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                    selectedLocationLocalList.add(GlobalData.locationCityList.get(j).getCity_name_local());
                }
            }
        }
        String[] getlocationaded = selectedLocationLocalList.toArray(new String[selectedLocationLocalList.size
                ()]);
        String getlocationarray = Arrays.toString(getlocationaded);
        String getLocationTamil = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocationTamil = getLocationTamil.replace(", ", ",");
        location.setText(getLocationTamil);
    }

    private void getCityListAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        GlobalData.CityList = new ArrayList<>();
        for (int i = 0; i < GlobalData.LocationList.size(); i++) {
            if (GlobalData.LocationList.get(i).getId().equals(getStateID)) {
                GlobalData.CityList = GlobalData.LocationList.get(i).getCities();
            }
        }
        locstate_view.setVisibility(View.GONE);
        city_view.setVisibility(View.VISIBLE);
        cl_cityheader.setText(getState);
        locfilt_citysearch.setText(autocity.getText().toString());
        //get the city list depends upon selected state
        cadapter = new ArrayAdapter<City>(JobSearch_Filter.this, R.layout.filter_listrow,
                GlobalData.CityList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                textView.setText(GlobalData.CityList.get(position).getCiti_name());
                if (!languages.equalsIgnoreCase("English")) {
                    textView.setText(GlobalData.CityList.get(position).getCity_name_local());
                }
                String locclick = GlobalData.CityList.get(position).getIsavailable();
                if (locclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filtercity.setAdapter(cadapter);
        getCityAdapter();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            location.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            indexcity = -1;
            for (int i = 0; i < locationlist.size(); i++) {
                for (int j = 0; j < GlobalData.CityList.size(); j++) {
                    if (!(selectedLocationList.contains(locationlist.get(i)))) {
                        selectedLocationList.add(locationlist.get(i));
                    }
                    if (GlobalData.CityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                        indexcity = j;
                        isCheckedLocation[indexcity] = true;
                        filtercity.setItemChecked(indexcity, true);
                    }
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
        }
        //city list item check|uncheck
        filtercity.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String locclick = cadapter.getItem(position).getIsavailable();
                if (locclick == null) {
                    //   filtercity.getChildAt(position).setEnabled(false);
                    filtercity.setItemChecked(position, false);
                } else {
                    //   filtercity.getChildAt(position).setEnabled(true);
                    if (!isCheckedLocation[position]) {
                        isCheckedLocation[position] = true;
                        if (!(selectedLocationList.contains(cadapter.getItem(position).getCiti_name())
                        )) {
                            selectedLocationList.add(cadapter.getItem(position).getCiti_name());
                        }
                    } else {
                        isCheckedLocation[position] = false;
                        selectedLocationList.remove(cadapter.getItem(position).getCiti_name());
                    }
                    String[] getLocationaded = selectedLocationList.toArray(new
                            String[selectedLocationList.size()]);
                    String getlocationarray = Arrays.toString(getLocationaded);
                    GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() -
                            1);
                    GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
                    location.setText(GlobalData.getLocation);
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        stateselectedloc.setVisibility(View.VISIBLE);
                        stateselectedloc.setText(GlobalData.getLocation);
                        cityselectedloc.setVisibility(View.VISIBLE);
                        cityselectedloc.setText(GlobalData.getLocation);
                        if (!languages.equalsIgnoreCase("English")) {
                            setSelectedLocation(stateselectedloc, cityselectedloc);
                        }
                    } else {
                        stateselectedloc.setVisibility(View.GONE);
                        cityselectedloc.setVisibility(View.GONE);
                    }
                    if (selectedLocationList.size() > 0) {
                        cityselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                            @Override
                            public boolean onDrawableClick() {
                                getFrom = "City";
                                getSelectedCities();
                                return true;
                            }
                        });
                    }
                }
            }
        });
    }

    private void setStateAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(JobSearch_Filter.this, R.layout
                .skillcategory_row, GlobalData.LocationList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context mContext = this.getContext();
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                View root;
                if (convertView == null) {
                    root = layoutInflater.inflate(R.layout.skillcategory_row, parent, false);
                } else {
                    root = convertView;
                }
                TextView textView = (TextView) root.findViewById(R.id.editjob_skill_catename);
                TextView selectedskill = (TextView) root.findViewById(R.id
                        .editjob_skillcate_skillname);
                textView.setText(GlobalData.LocationList.get(position).getState_name());
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(GlobalData.getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(GlobalData.getLocation);
                    //location.setText(GlobalData.getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
                    locationlist.setText(R.string.edit);
                    List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
                    getStateID = GlobalData.LocationList.get(position).getId();
                    GlobalData.CityList = GlobalData.LocationList.get(position).getCities();
                    ArrayList<String> selectedcitylist = new ArrayList<>();
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < GlobalData.CityList.size(); j++) {
                            if (GlobalData.CityList.get(j).getCiti_name().equals(locationlist.get
                                    (i))) {
                                String getCityID = GlobalData.CityList.get(j).getCiti_county_id();
                                if (getCityID.equalsIgnoreCase(getStateID)) {
                                    if (languages.equalsIgnoreCase("English")) {
                                        selectedcitylist.add(GlobalData.CityList.get(j).getCiti_name());
                                    } else {
                                        selectedcitylist.add(GlobalData.CityList.get(j).getCity_name_local());
                                    }
                                }
                            }
                        }
                        String[] getskilladed = selectedcitylist.toArray(new
                                String[selectedcitylist.size()]);
                        if (getskilladed.length > 0) {
                            selectedskill.setText(Arrays.toString(getskilladed));
                        } else {
                            selectedskill.setText("");
                        }
                    }
                } else {
                    location.setText("");
                    locationlist.setText(R.string.select);
                }
                return root;
            }
        };
        filterstate.setAdapter(sadapter);
        if (selectedLocationList.size() > 0) {
            stateselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                @Override
                public boolean onDrawableClick() {
                    getFrom = "State";
                    getSelectedCities();
                    return true;
                }
            });
        }
    }

    private void getSelectedCities() {
        if (selectedLocationList.size() > 0) {
            currentlocationautoupdate();
            selectedLocationList.clear();
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            GlobalData.getLocation = null;
            GlobalData.getNearLocation = null;
            GlobalData.getFinalLocation = null;
            if (getFrom.equalsIgnoreCase("State")) {
                setStateAdapter();
            } else {
                getCityListAdapter();
            }
            location.setText("");
            locationlist.setText(R.string.select);
            savelocationpopup();
        }
    }

    private void getCityAdapter() {
        if (GlobalData.CityList.size() > 0) {
            final ArrayList<City> getcityforstate = new ArrayList<>();
            getcityforstate.addAll(GlobalData.CityList);
            loccityAdapter = new CityAdapter(JobSearch_Filter.this, R.layout.spinner_item_text, getcityforstate) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        Context mContext = this.getContext();
                        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                                .LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.spinner_item_text, parent, false);
                    }
                    TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                    String yourValue = getcityforstate.get(position).getCiti_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        yourValue = getcityforstate.get(position).getCity_name_local();
                    }
                    String locclick = getcityforstate.get(position).getIsavailable();
                    if (locclick == null) {
                        v.setEnabled(false);
                    } else {
                        v.setEnabled(true);
                    }
                    textView.setText(yourValue);
                    return textView;
                }
            };
            locfilt_citysearch.setAdapter(loccityAdapter);
            locfilt_citysearch.setThreshold(1);
            locfilt_citysearch.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    locfilt_citysearch.setText("");
                    if (!selectedLocationList.contains(loccityAdapter.getItem(i).getCiti_name())) {
                        selectedLocationList.add(loccityAdapter.getItem(i).getCiti_name());
                        for (int j = 0; j < GlobalData.CityList.size(); j++) {
                            if (GlobalData.CityList.get(j).getCiti_name().equals(loccityAdapter
                                    .getItem(i).getCiti_name())) {
                                indexcity = j;
                                isCheckedLocation[indexcity] = true;
                                filtercity.setItemChecked(indexcity, true);
                            }
                        }
                        String[] getLocationaded = selectedLocationList.toArray(new
                                String[selectedLocationList.size()]);
                        String getlocationarray = Arrays.toString(getLocationaded);
                        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray
                                .length() - 1);
                        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
                        stateselectedloc.setVisibility(View.VISIBLE);
                        stateselectedloc.setText(GlobalData.getLocation);
                        cityselectedloc.setVisibility(View.VISIBLE);
                        cityselectedloc.setText(GlobalData.getLocation);
                        if (!languages.equalsIgnoreCase("English")) {
                            setSelectedLocation(stateselectedloc, cityselectedloc);
                        }
                        location.setText(GlobalData.getLocation);
                    }
                }
            });
            if (selectedLocationList.size() > 0) {
                cityselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                    @Override
                    public boolean onDrawableClick() {
                        getFrom = "City";
                        getSelectedCities();
                        return true;
                    }
                });
            }
        }
    }

    private void RoleAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.role);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterrole.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        roleadapter = new ArrayAdapter<Role>(this, R.layout.filter_listrow, RoleList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = RoleList.get(position).getRole_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = RoleList.get(position).getRole_name_local();
                }
                textView.setText(yourValue);
                // textView.setTextColor(Color.BLACK);
                String roleclick = RoleList.get(position).getIsavailable();
                if (roleclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filterrole.setAdapter(roleadapter);

        if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
           /* if (GlobalData.getRole.equalsIgnoreCase("All")) {
                isCheckedrole = new boolean[RoleList.size()];
                for (int j = 0; j < RoleList.size(); j++) {
                    isCheckedrole[j] = true;
                    filterrole.setItemChecked(j, true);
                    if (!(selectedRoleList.contains(roleadapter.getItem(j).getRole_name()))) {
                        selectedRoleList.add(roleadapter.getItem(j).getRole_name());
                    }
                }
            } else {*/
            List<String> rolelist = Arrays.asList(GlobalData.getRole.split(","));
            isCheckedrole = new boolean[RoleList.size()];
            for (int i = 0; i < rolelist.size(); i++) {
                if (!(selectedRoleList.contains(rolelist.get(i)))) {
                    selectedRoleList.add(rolelist.get(i));
                }
                for (int j = 0; j < RoleList.size(); j++) {
                    if (RoleList.get(j).getRole_name().equals(rolelist.get(i))) {
                        indexrole = j;
                        if (indexrole != -1) {
                            isCheckedrole[indexrole] = true;
                            filterrole.setItemChecked(indexrole, true);
                        }
                    }
                }
            }
            // }
        } else {
            isCheckedrole = new boolean[RoleList.size()];
            Arrays.fill(isCheckedrole, false);
        }
        filterrole.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               /* if (roleadapter.getItem(position).getRole_name().equalsIgnoreCase("All")) {
                    if (!isCheckedrole[position]) {
                        isCheckedrole[position] = true;
                        filterrole.setItemChecked(position, true);
                        isCheckedrole = new boolean[RoleList.size()];
                        for (int j = 0; j < RoleList.size(); j++) {
                            isCheckedrole[j] = true;
                            filterrole.setItemChecked(j, true);
                            if (!(selectedRoleList.contains(roleadapter.getItem(j).getRole_name()))) {
                                selectedRoleList.add(roleadapter.getItem(j).getRole_name());
                            }
                        }
                    } else {
                        isCheckedrole[position] = false;
                        filterrole.setItemChecked(position, false);
                        isCheckedrole = new boolean[RoleList.size()];
                        for (int j = 0; j < RoleList.size(); j++) {
                            isCheckedrole[j] = false;
                            filterrole.setItemChecked(j, false);
                            selectedRoleList.remove(roleadapter.getItem(j).getRole_name());
                        }
                    }
                }else*/
                String roleclick = roleadapter.getItem(position).getIsavailable();
                if (roleclick == null) {
                    // filterrole.getChildAt(position).setEnabled(false);
                    filterrole.setItemChecked(position, false);
                } else {
                    //  filterrole.getChildAt(position).setEnabled(true);
                    if (!isCheckedrole[position]) {
                        isCheckedrole[position] = true;
                        if (!(selectedRoleList.contains(roleadapter.getItem(position).getRole_name()))) {
                            selectedRoleList.add(roleadapter.getItem(position).getRole_name());
                        }
                    } else {
                        isCheckedrole[position] = false;
                   /* if (selectedRoleList.contains("All")) {
                        selectedRoleList.remove("All");
                        isCheckedrole[0] = false;
                        filterrole.setItemChecked(0, false);
                    }*/
                        selectedRoleList.remove(roleadapter.getItem(position).getRole_name());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRolelist();
                alertDialog.dismiss();
            }
        });
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (selectedRoleList.size() > 0) {
                    selectedRoleList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedRoleLocalList.clear();
                    }
                }
                role.setText("");
                isCheckedrole = new boolean[RoleList.size()];
                Arrays.fill(isCheckedrole, false);
                GlobalData.getRole = null;
                GlobalData.getCRole = null;
                rolelist.setText(R.string.select);
                alertDialog.dismiss();
                RoleAlert();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setRolelist();
                alertDialog.dismiss();
            }
        });
    }

    private void setRolelist() {
        if (selectedRoleList.size() > 0) {
            String[] getRoleaded = selectedRoleList.toArray(new String[selectedRoleList.size
                    ()]);
            String getrolearray = Arrays.toString(getRoleaded);
            GlobalData.getRole = getrolearray.substring(1, getrolearray.length() - 1);
            GlobalData.getRole = GlobalData.getRole.replace(", ", ",");
            role.setText(GlobalData.getRole);
        } else {
            role.setText("");
            isCheckedrole = new boolean[RoleList.size()];
            Arrays.fill(isCheckedrole, false);
            GlobalData.getRole = null;
            GlobalData.getCRole = null;
        }
        reloadRoleData();
        if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang(GlobalData.getRole);
            }
        } else {
            rolelist.setText(R.string.select);
            role.setText("");
        }
    }

    private void reloadRoleData() {
        if (!(GlobalData.getRole == null || GlobalData.getRole.isEmpty()) && (oldRole == null || oldRole.isEmpty())) {
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    if (!GlobalData.getRole.equalsIgnoreCase(oldRole)) {
                        refreshList("Role");
                    }
                } else {
                    refreshList("Role");
                }
            } else {
                if (oldRole != null && !oldRole.isEmpty()) {
                    refreshList("Role");
                }
            }
        } else {
            if (GlobalData.getRole == null || GlobalData.getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    refreshList("Role");
                }
            }
            if (oldRole == null || oldRole.isEmpty()) {
                if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                    refreshList("Role");
                }
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    if (!GlobalData.getRole.equalsIgnoreCase(oldRole)) {
                        refreshList("Role");
                    }
                } else {
                    refreshList("Role");
                }
            } else {
                if (oldRole != null && !oldRole.isEmpty()) {
                    refreshList("Role");
                }
            }
        }
    }

    private void setRoleLocalLang(String getRole) {
        selectedRoleLocalList = new ArrayList<>();
        List<String> rolenamelist = Arrays.asList(getRole.split(","));
        for (int i = 0; i < rolenamelist.size(); i++) {
            for (int j = 0; j < RoleList.size(); j++) {
                if (RoleList.get(j).getRole_name().equals(rolenamelist.get(i))) {
                    selectedRoleLocalList.add(RoleList.get(j).getRole_name_local());
                }
            }
        }
        String[] getRoleaded = selectedRoleLocalList.toArray(new String[selectedRoleLocalList.size
                ()]);
        String getrolearray = Arrays.toString(getRoleaded);
        String getRoleTamil = getrolearray.substring(1, getrolearray.length() - 1);
        getRoleTamil = getRoleTamil.replace(", ", ",");
        role.setText(getRoleTamil);
    }

    private void IndustryAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.industry);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterindustry = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterindustry.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        industryAdapter = new ArrayAdapter<Industry>(this, R.layout.filter_listrow, industriesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = industriesList.get(position).getIndustry_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = industriesList.get(position).getIndustry_name_local();
                }
                String industryclick = industriesList.get(position).getIsavailable();
                if (industryclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                filterindustry.setId(position);
                textView.setText(yourValue);
                return textView;
            }
        };

        filterindustry.setAdapter(industryAdapter);
        if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
            List<String> industrylist = Arrays.asList(GlobalData.getIndustry.split(","));
            isCheckedIndustry = new boolean[industriesList.size()];
            for (int i = 0; i < industrylist.size(); i++) {
                if (!(selectedIndustriesList.contains(industrylist.get(i)))) {
                    selectedIndustriesList.add(industrylist.get(i));
                }
                for (int j = 0; j < industriesList.size(); j++) {
                    if (industriesList.get(j).getIndustry_name().equals(industrylist.get(i))) {
                        int indexindustry = j;
                        if (indexindustry != -1) {
                            isCheckedIndustry[indexindustry] = true;
                            filterindustry.setItemChecked(indexindustry, true);
                        }
                    }
                }
            }
        } else {
            isCheckedIndustry = new boolean[industriesList.size()];
            Arrays.fill(isCheckedIndustry, false);
        }
        filterindustry.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String industryclick = industryAdapter.getItem(position).getIsavailable();
                if (industryclick == null) {
                    // filterindustry.getChildAt(position).setEnabled(false);
                    filterindustry.setItemChecked(position, false);
                } else {
                    if (!isCheckedIndustry[position]) {
                        isCheckedIndustry[position] = true;
                        if (!(selectedIndustriesList.contains(industryAdapter.getItem(position).getIndustry_name()))) {
                            selectedIndustriesList.add(industryAdapter.getItem(position).getIndustry_name());
                        }
                    } else {
                        isCheckedIndustry[position] = false;
                        selectedIndustriesList.remove(industryAdapter.getItem(position).getIndustry_name());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (selectedIndustriesList.size() > 0) {
                    selectedIndustriesList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedIndustriesLocalList.clear();
                    }
                }
                industry.setText("");
                isCheckedIndustry = new boolean[industriesList.size()];
                Arrays.fill(isCheckedIndustry, false);
                GlobalData.getIndustry = null;
                industrylist.setText(R.string.select);
                alertDialog.dismiss();
                IndustryAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
    }

    private void setIndustry() {
        String[] getIndustryaded = selectedIndustriesList.toArray(new
                String[selectedIndustriesList.size()]);
        String getindustryarray = Arrays.toString(getIndustryaded);
        GlobalData.getIndustry = getindustryarray.substring(1, getindustryarray.length() -
                1);
        GlobalData.getIndustry = GlobalData.getIndustry.replace(", ", ",");
        industry.setText(GlobalData.getIndustry);
        reloadIndustryData();
        if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
            GlobalData.getRole = null;
            role.setText("");
            if (selectedRoleList != null && selectedRoleList.size() > 0) {
                selectedRoleList.clear();
                if (selectedRoleLocalList != null && selectedRoleLocalList.size() > 0) {
                    selectedRoleLocalList.clear();
                }
            }
            industrylist.setText(R.string.edit);
           /* if(oldIndustry != null && !oldIndustry.isEmpty())
            {
                if(GlobalData.getIndustry.equalsIgnoreCase(oldIndustry)){

                }
            }*/
           /* List<String> industrylist = Arrays.asList(GlobalData.getIndustry.split(","));
            RoleList = new ArrayList<>();
            for (int i = 0; i < industrylist.size(); i++) {
                for (int j = 0; j < industriesList.size(); j++) {
                    if (industriesList.get(j).getIndustry_name().equals(industrylist.get(i))) {
                        RoleList.addAll(industriesList.get(j).getRole());
                    }
                }
            }
            if (GenderalIndustryId != 0) {
                RoleList.addAll(industriesList.get(GenderalIndustryId - 1)
                        .getRole());
            }
            if (RoleList != null && RoleList.size() > 0) {
                for (int i = 1; i < RoleList.size(); i++) {
                    for (int j = i + 1; j < RoleList.size(); j++) {
                        if (RoleList.get(i).getRole_name().equals(RoleList.get(j).getRole_name())) {
                            RoleList.remove(j);
                            j--;
                        }
                    }
                }
            }*/
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang();
            }
        } else {
            industrylist.setText(R.string.select);
            industry.setText("");
            /*RoleList = new ArrayList<>();
            for (int j = 0; j < industriesList.size(); j++) {
                RoleList.addAll(industriesList.get(j).getRole());
            }*/
        }
    }

    private void reloadIndustryData() {
        if (!(GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) && (oldIndustry == null || oldIndustry.isEmpty())) {
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    if (!GlobalData.getIndustry.equalsIgnoreCase(oldIndustry)) {
                        refreshList("Industry");
                    }
                } else {
                    refreshList("Industry");
                }
            } else {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
        } else {
            if (GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
            if (oldIndustry == null || oldIndustry.isEmpty()) {
                if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    if (!GlobalData.getIndustry.equalsIgnoreCase(oldIndustry)) {
                        refreshList("Industry");
                    }
                } else {
                    refreshList("Industry");
                }
            } else {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
        }
    }

    private void setIndustryLocalLang() {
        selectedIndustriesLocalList = new ArrayList<>();
        List<String> industrylist = Arrays.asList(GlobalData.getIndustry.split(","));
        for (int i = 0; i < industrylist.size(); i++) {
            for (int j = 0; j < industriesList.size(); j++) {
                if (industriesList.get(j).getIndustry_name().equals(industrylist.get(i))) {
                    selectedIndustriesLocalList.add(industriesList.get(j).getIndustry_name_local());
                }
            }
        }
        String[] getindustryaded = selectedIndustriesLocalList.toArray(new String[selectedIndustriesLocalList.size
                ()]);
        String getindustryarray = Arrays.toString(getindustryaded);
        String getIndustryTamil = getindustryarray.substring(1, getindustryarray.length() - 1);
        getIndustryTamil = getIndustryTamil.replace(", ", ",");
        industry.setText(getIndustryTamil);
    }

    private void SalaryAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.sly);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filtersalary = (ListView) emppromptView.findViewById(R.id.filterlist);
        filtersalary.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        salaryAdapter = new ArrayAdapter<Salary>(this, R.layout.filter_listrow, SalaryList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = SalaryList.get(position).getSalaryrange();
                String salaryclick = SalaryList.get(position).getIsavailable();
                if (salaryclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtersalary.setAdapter(salaryAdapter);
        if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
            salarylist.setText(R.string.edit);
            indexsalary = -1;
            for (int i = 0; i < SalaryList.size(); i++) {
                if (SalaryList.get(i).getSalaryrange().equals(GlobalData.getSSalary)) {
                    indexsalary = i;
                    filtersalary.setItemChecked(indexsalary, true);
                }
            }
        } else {
            indexsalary = -1;
            salarylist.setText(R.string.select);
        }
        filtersalary.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String salaryclick = SalaryList.get(position).getIsavailable();
                if (salaryclick == null) {
                    // filtersalary.getChildAt(position).setEnabled(false);
                    filtersalary.setItemChecked(position, false);
                    GlobalData.getSSalary = null;
                } else {
                    //  filtersalary.getChildAt(position).setEnabled(true);
                    if (filtersalary.isItemChecked(indexsalary)) {
                        filtersalary.setItemChecked(position, false);
                        GlobalData.getSSalary = null;
                    } else {
                        filtersalary.setItemChecked(position, true);
                        GlobalData.getSSalary = SalaryList.get(position).getSalaryrange();
                    }
                    indexsalary = filtersalary.getCheckedItemPosition();
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                salary.setText("");
                GlobalData.getSSalary = null;
                salarylist.setText(R.string.select);
                alertDialog.dismiss();
                SalaryAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSalary();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setSalary();
                alertDialog.dismiss();
            }
        });
    }

    private void setSalary() {
        salary.setText(GlobalData.getSSalary);
        reloadSalaryData();
        if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
            salarylist.setText(R.string.edit);
        } else {
            salarylist.setText(R.string.select);
        }
    }

    private void reloadSalaryData() {
        if (!(GlobalData.getSSalary == null || GlobalData.getSSalary.isEmpty()) && (oldSalary == null || oldSalary.isEmpty())) {
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                if (oldSalary != null && !oldSalary.isEmpty()) {
                    if (!GlobalData.getSSalary.equalsIgnoreCase(oldSalary)) {
                        refreshList("Salary");
                    }
                } else {
                    refreshList("Salary");
                }
            } else {
                if (oldSalary != null && !oldSalary.isEmpty()) {
                    refreshList("Salary");
                }
            }
        } else {
            if (GlobalData.getSSalary == null || GlobalData.getSSalary.isEmpty()) {
                if (oldSalary != null && !oldSalary.isEmpty()) {
                    refreshList("Salary");
                }
            }
            if (oldSalary == null || oldSalary.isEmpty()) {
                if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                    refreshList("Salary");
                }
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                if (oldSalary != null && !oldSalary.isEmpty()) {
                    if (!GlobalData.getSSalary.equalsIgnoreCase(oldSalary)) {
                        refreshList("Salary");
                    }
                } else {
                    refreshList("Salary");
                }
            } else {
                if (oldSalary != null && !oldSalary.isEmpty()) {
                    refreshList("Salary");
                }
            }
        }

    }

    private void JobTypeAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.jobtype);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterjobtype = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterjobtype.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        jobTypeAdapter = new ArrayAdapter<JobType>(this, R.layout.filter_listrow, JobTypeList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = JobTypeList.get(position).getJob_type_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = JobTypeList.get(position).getJob_type_name_local();
                }
                String jobtypeclick = JobTypeList.get(position).getIsavailable();
                if (jobtypeclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterjobtype.setAdapter(jobTypeAdapter);
        if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty()) {
            List<String> jobtypelist = Arrays.asList(GlobalData.getJobType.split(","));
            List<String> jobtypeidlist = Arrays.asList(GlobalData.getJobIDType.split(","));
            isCheckedJobType = new boolean[JobTypeList.size()];
            for (int i = 0; i < jobtypelist.size(); i++) {
                if (!(selectedJobTypeList.contains(jobtypelist.get(i)))) {
                    selectedJobTypeList.add(jobtypelist.get(i));
                    selectedJobTypeIDList.add(jobtypeidlist.get(i));
                }
                for (int j = 0; j < JobTypeList.size(); j++) {
                    if (JobTypeList.get(j).getJob_type_name().equals(jobtypelist.get(i))) {
                        int indexjobtype = j;
                        if (indexjobtype != -1) {
                            isCheckedJobType[indexjobtype] = true;
                            filterjobtype.setItemChecked(indexjobtype, true);
                        }
                    }
                }
            }
        } else {
            isCheckedJobType = new boolean[JobTypeList.size()];
            Arrays.fill(isCheckedJobType, false);
        }
        filterjobtype.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                /*if (jobTypeAdapter.getItem(position).getJob_type_name().equalsIgnoreCase("All")) {
                    if (!isCheckedJobType[position]) {
                        isCheckedJobType[position] = true;
                        filterjobtype.setItemChecked(position, true);
                        isCheckedJobType = new boolean[JobTypeList.size()];
                        for (int j = 0; j < JobTypeList.size(); j++) {
                            isCheckedJobType[j] = true;
                            filterjobtype.setItemChecked(j, true);
                            if (!(selectedJobTypeList.contains(jobTypeAdapter.getItem(j).getJob_type_name()))) {
                                selectedJobTypeList.add(jobTypeAdapter.getItem(j).getJob_type_name());
                                selectedJobTypeIDList.add(jobTypeAdapter.getItem(j).getJob_type_id());
                            }
                        }
                    } else {
                        isCheckedJobType[position] = false;
                        filterjobtype.setItemChecked(position, false);
                        isCheckedJobType = new boolean[JobTypeList.size()];
                        for (int j = 0; j < JobTypeList.size(); j++) {
                            isCheckedJobType[j] = false;
                            filterjobtype.setItemChecked(j, false);
                            selectedJobTypeList.remove(jobTypeAdapter.getItem(j).getJob_type_name());
                            selectedJobTypeIDList.remove(jobTypeAdapter.getItem(j).getJob_type_id());
                        }
                    }
                } else */
                String jobtypeclick = jobTypeAdapter.getItem(position).getIsavailable();
                if (jobtypeclick == null) {
                    //  filterjobtype.getChildAt(position).setEnabled(false);
                    filterjobtype.setItemChecked(position, false);
                } else {
                    //   filterjobtype.getChildAt(position).setEnabled(true);
                    if (!isCheckedJobType[position]) {
                        isCheckedJobType[position] = true;
                        if (!(selectedJobTypeList.contains(jobTypeAdapter.getItem(position).getJob_type_name()))) {
                            selectedJobTypeList.add(jobTypeAdapter.getItem(position).getJob_type_name());
                            selectedJobTypeIDList.add(jobTypeAdapter.getItem(position).getJob_type_id());
                        }
                    } else {
                        isCheckedJobType[position] = false;
                        selectedJobTypeList.remove(jobTypeAdapter.getItem(position).getJob_type_name());
                        selectedJobTypeIDList.remove(jobTypeAdapter.getItem(position).getJob_type_id());
                  /*  if (selectedJobTypeList.contains("All")) {
                        selectedJobTypeList.remove("All");
                        selectedJobTypeIDList.remove("1");
                        isCheckedJobType[0] = false;
                        filterjobtype.setItemChecked(0, false);
                    }*/
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (selectedJobTypeList.size() > 0) {
                    selectedJobTypeIDList.clear();
                    selectedJobTypeList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedJobTypeLocalList.clear();
                    }
                }
                jobtype.setText("");
                isCheckedJobType = new boolean[JobTypeList.size()];
                Arrays.fill(isCheckedJobType, false);
                GlobalData.getJobIDType = null;
                GlobalData.getJobType = null;
                jobtypelist.setText(R.string.select);
                alertDialog.dismiss();
                JobTypeAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobType();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setJobType();
                alertDialog.dismiss();
            }
        });
    }

    private void setJobType() {
        String[] getjobtypeaded = selectedJobTypeList.toArray(new
                String[selectedJobTypeList.size()]);
        String[] getjobidtypeaded = selectedJobTypeIDList.toArray(new
                String[selectedJobTypeIDList.size()]);
        String getjobtypearray = Arrays.toString(getjobtypeaded);
        String getjobtypeidarray = Arrays.toString(getjobidtypeaded);
        GlobalData.getJobType = getjobtypearray.substring(1, getjobtypearray.length() - 1);
        GlobalData.getJobIDType = getjobtypeidarray.substring(1, getjobtypeidarray.length
                () - 1);
        GlobalData.getJobType = GlobalData.getJobType.replace(", ", ",");
        GlobalData.getJobIDType = GlobalData.getJobIDType.replace(", ", ",");
        jobtype.setText(GlobalData.getJobType);
        reloadJobTypeData();
        if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty() &&
                GlobalData.getJobIDType != null
                && !GlobalData.getJobIDType.isEmpty()) {
            if (!languages.equalsIgnoreCase("English")) {
                setJobtypeLocalLang();
            }
            jobtypelist.setText(R.string.edit);
        } else {
            jobtype.setText("");
            jobtypelist.setText(R.string.select);
        }
    }

    private void setJobtypeLocalLang() {
        List<String> jobtypeidlist = Arrays.asList(GlobalData.getJobIDType.split(","));
        selectedJobTypeLocalList = new ArrayList<>();
        for (int i = 0; i < jobtypeidlist.size(); i++) {
            for (int j = 0; j < JobTypeList.size(); j++) {
                if (JobTypeList.get(j).getJob_type_id().equals(jobtypeidlist.get(i))) {
                    selectedJobTypeLocalList.add(JobTypeList.get(j).getJob_type_name_local());
                }
            }
        }
        String[] getjobtypeaded = selectedJobTypeLocalList.toArray(new String[selectedJobTypeLocalList.size
                ()]);
        String getjypearray = Arrays.toString(getjobtypeaded);
        String getJobTypeTamil = getjypearray.substring(1, getjypearray.length() - 1);
        getJobTypeTamil = getJobTypeTamil.replace(", ", ",");
        jobtype.setText(getJobTypeTamil);
    }

    private void reloadJobTypeData() {
        if (!(GlobalData.getJobType == null || GlobalData.getJobType.isEmpty()) && (oldJobtype == null || oldJobtype.isEmpty())) {
            if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty()) {
                if (oldJobtype != null && !oldJobtype.isEmpty()) {
                    if (!GlobalData.getJobType.equalsIgnoreCase(oldJobtype)) {
                        refreshList("Jobtype");
                    }
                } else {
                    refreshList("Jobtype");
                }
            } else {
                if (oldJobtype != null && !oldJobtype.isEmpty()) {
                    refreshList("Jobtype");
                }
            }
        } else {
            if (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty()) {
                if (oldJobtype != null && !oldJobtype.isEmpty()) {
                    refreshList("Jobtype");
                }
            }
            if (oldJobtype == null || oldJobtype.isEmpty()) {
                if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty()) {
                    refreshList("Jobtype");
                }
            }
            if (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty()) {
                if (oldJobtype != null && !oldJobtype.isEmpty()) {
                    if (!GlobalData.getJobType.equalsIgnoreCase(oldJobtype)) {
                        refreshList("Jobtype");
                    }
                } else {
                    refreshList("Jobtype");
                }
            } else {
                if (oldJobtype != null && !oldJobtype.isEmpty()) {
                    refreshList("Jobtype");
                }
            }
        }
    }

    private void GenderAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.gender);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filtergender = (ListView) emppromptView.findViewById(R.id.filterlist);
        filtergender.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        genderadapter = new ArrayAdapter<Gender>(this, R.layout.filter_listrow, select_gender) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = select_gender.get(position).getGender();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_gender.get(position).getGender_local();
                }
                String genderclick = select_gender.get(position).getIsavailable();
                if (select_gender.get(position).getIsavailable() == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtergender.setAdapter(genderadapter);
        if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
            indexgender = -1;
            for (int i = 0; i < select_gender.size(); i++) {
                if (select_gender.get(i).getGender().equals(GlobalData.getGender)) {
                    indexgender = i;
                    filtergender.setItemChecked(indexgender, true);
                }
            }
        } else {
            indexgender = -1;
            genderlist.setText(R.string.select);
        }
        filtergender.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               // int pos = filtergender.getId();
                String genderclick = select_gender.get(position).getIsavailable();
                if (genderclick == null) {
                    filtergender.setItemChecked(position, false);
                    GlobalData.getGender = null;
                } else {
                    if (filtergender.isItemChecked(indexgender)) {
                        filtergender.setItemChecked(position, false);
                        GlobalData.getGender = null;
                    } else {
                        filtergender.setItemChecked(position, true);
                        GlobalData.getGender = select_gender.get(position).getGender();
                    }
                    indexgender = filtergender.getCheckedItemPosition();
                }
            }
        });

        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        resetall_filter.setOnClickListener(new OnClickListener() {
                                               @Override
                                               public void onClick(View arg0) {
                                                   gender.setText("");
                                                   GlobalData.getGender = null;
                                                   GenderLocal = null;
                                                   genderlist.setText(R.string.select);
                                                   alertDialog.dismiss();
                                                   GenderAlert();
                                               }
                                           }

        );
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
                                           @Override
                                           public void onClick(View arg0) {
                                               setGender();
                                               alertDialog.dismiss();
                                           }
                                       }

        );
    }

    private void setGender() {
        gender.setText(GlobalData.getGender);
        if (!languages.equalsIgnoreCase("English")) {
            setGenderLocalLang();
        }
        reloadGenderData();
        if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
        } else {
            genderlist.setText(R.string.select);
        }
    }

    private void reloadGenderData() {
        if (!(GlobalData.getGender == null || GlobalData.getGender.isEmpty()) && (oldGender == null || oldGender.isEmpty())) {
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    if (!GlobalData.getGender.equalsIgnoreCase(oldGender)) {
                        refreshList("Gender");
                    }
                } else {
                    refreshList("Gender");
                }
            } else {
                if (oldGender != null && !oldGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
        } else {
            if (GlobalData.getGender == null || GlobalData.getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (oldGender == null || oldGender.isEmpty()) {
                if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    if (!GlobalData.getGender.equalsIgnoreCase(oldGender)) {
                        refreshList("Gender");
                    }
                } else {
                    refreshList("Gender");
                }
            } else {
                if (oldGender != null && !oldGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
        }
    }

    private void refreshList(String fromfield) {
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            backtoLanding = "true";
            if (fromfield.equalsIgnoreCase("Gender")) {
                oldGender = GlobalData.getGender;
            }
            if (fromfield.equalsIgnoreCase("Salary")) {
                oldSalary = GlobalData.getSSalary;
            }
            if (fromfield.equalsIgnoreCase("Experience")) {
                oldExperience = GlobalData.getExperience;
            }
            if (fromfield.equalsIgnoreCase("Jobtype")) {
                oldJobtype = GlobalData.getJobType;
            }
            if (fromfield.equalsIgnoreCase("Role")) {
                oldRole = GlobalData.getRole;
            }
            if (fromfield.equalsIgnoreCase("Industry")) {
                oldIndustry = GlobalData.getIndustry;
            }
            if (fromfield.equalsIgnoreCase("Location")) {
                oldLocation = GlobalData.getLocation;
            }
            new getRole().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void setGenderLocalLang() {
        for (int j = 0; j < select_gender.size(); j++) {
            if (select_gender.get(j).getGender().equals(GlobalData.getGender)) {
                GenderLocal = select_gender.get(j).getGender_local();
                gender.setText(GenderLocal);
            }
        }
    }

    private void ExpAlert() {
        final Dialog alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.filterpopup, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.experience);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterexp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        expAdapter = new ArrayAdapter<Experience>(this, R.layout.filter_listrow, experienceList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                String yourValue = experienceList.get(position).getExperience_profile_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = experienceList.get(position).getExperience_profile_name_local();
                }
                textView.setText(yourValue);
                String expclick = experienceList.get(position).getIsavailable();
                if (experienceList.get(position).getIsavailable() == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filterexp.setAdapter(expAdapter);
        if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
            experiencelist.setText(R.string.edit);
            indexexp = -1;
            for (int i = 0; i < experienceList.size(); i++) {
                if (experienceList.get(i).getExperience_profile_name().equals(GlobalData.getExperience)) {
                    indexexp = i;
                    filterexp.setItemChecked(indexexp, true);
                }
            }
        } else {
            indexexp = -1;
            experiencelist.setText(R.string.select);
        }
        filterexp.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String expclick = experienceList.get(position).getIsavailable();
                if (expclick == null) {
                    //  filterexp.getChildAt(position).setEnabled(false);
                    filterexp.setItemChecked(position, false);
                    GlobalData.getExperience = null;
                } else {
                    //  filterexp.getChildAt(position).setEnabled(true);
                    if (filterexp.isItemChecked(indexexp)) {
                        filterexp.setItemChecked(position, false);
                        GlobalData.getExperience = null;
                    } else {
                        filterexp.setItemChecked(position, true);
                        GlobalData.getExperience = experienceList.get(position).getExperience_profile_name();
                    }
                    indexexp = filterexp.getCheckedItemPosition();
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExperience();
                alertDialog.dismiss();
            }
        });
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GlobalData.getExperience = null;
                experiencelist.setText(R.string.select);
                experience.setText(R.string.select);
                alertDialog.dismiss();
                ExpAlert();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setExperience();
                alertDialog.dismiss();
            }
        });
    }

    private void setExperience() {
        experience.setText(GlobalData.getExperience);
        if (!languages.equalsIgnoreCase("English")) {
            setExperienceLocalLang();
        }
        reloadExperienceData();
        if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
    }

    private void reloadExperienceData() {
        if (!(GlobalData.getExperience == null || GlobalData.getExperience.isEmpty()) && (oldExperience == null || oldExperience.isEmpty())) {
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    if (!GlobalData.getExperience.equalsIgnoreCase(oldExperience)) {
                        refreshList("Experience");
                    }
                } else {
                    refreshList("Experience");
                }
            } else {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    refreshList("Experience");
                }
            }
        } else {
            if (GlobalData.getExperience == null || GlobalData.getExperience.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    refreshList("Experience");
                }
            }
            if (oldExperience == null || oldExperience.isEmpty()) {
                if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                    refreshList("Experience");
                }
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    if (!GlobalData.getExperience.equalsIgnoreCase(oldExperience)) {
                        refreshList("Experience");
                    }
                } else {
                    refreshList("Experience");
                }
            } else {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    refreshList("Experience");
                }
            }
        }
    }

    private void setExperienceLocalLang() {
        for (int j = 0; j < experienceList.size(); j++) {
            if (experienceList.get(j).getExperience_profile_name().equals(GlobalData.getExperience)) {
                ExperienceLocal = experienceList.get(j).getExperience_profile_name_local();
                experience.setText(ExperienceLocal);
            }
        }
    }
    /*private void SkillAlert() {
        SelectedskillList = new ArrayList<>();
        Dialog alertDialog = new Dialog(JobSearch_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.skill_filterpopup, null);
        TextView sk_popupheader = (TextView) emppromptView.findViewById(R.id.sl_popupheader);
        sk_popupheader.setText(R.string.skill);
        final LinearLayout categoryview = (LinearLayout) emppromptView.findViewById(R.id
                .scate_view);
        final LinearLayout skillview = (LinearLayout) emppromptView.findViewById(R.id.skill_view);
        final ListView filterskcate = (ListView) emppromptView.findViewById(R.id
                .filterskillcategorylist);
        final ListView filterskill = (ListView) emppromptView.findViewById(R.id.filterskilllist);
        final TextView sl_skillheader = (TextView) emppromptView.findViewById(R.id.sl_skillheader);
        //skill list - back button (skillcategory view show & skill view hidden)
        final ImageButton sl_skillheader_back = (ImageButton) emppromptView.findViewById(R.id
                .sl_skillheader_back);
        sl_skillheader_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryview.setVisibility(View.VISIBLE);
                skillview.setVisibility(View.GONE);
                //get the selected skill names , split as "," separated
                String[] getLocationaded = SelectedskillList.toArray(new String[SelectedskillList
                        .size()]);
                String getlocationarray = Arrays.toString(getLocationaded);
                GlobalData.getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                GlobalData.getSkill = GlobalData.getSkill.replace(", ", ",");
                //get|set the skill category items
                scadapter = new ArrayAdapter<SkillCategory>(JobSearch_Filter.this, R.layout
                        .skillcategory_row, skillCategoryList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Context mContext = this.getContext();
                        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        View root;
                        if (convertView == null) {
                            root = layoutInflater.inflate(R.layout.skillcategory_row, parent, false);
                        } else {
                            root = convertView;
                        }
                        TextView textView = (TextView) root.findViewById(R.id
                                .editjob_skill_catename);
                        TextView selectedskill = (TextView) root.findViewById(R.id
                                .editjob_skillcate_skillname);
                        textView.setText(skillCategoryList.get(position).getSkillcategoryname());
                        if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                            List<String> locationlist = Arrays.asList(GlobalData.getSkill.split("," +
                                    ""));
                            getSkillCategoryID = skillCategoryList.get(position)
                                    .getSkillcategoryid();
                            skillList = skillCategoryList.get(position).getSkills();
                            ArrayList<String> selectedskilllist = new ArrayList<>();
                            for (int i = 0; i < locationlist.size(); i++) {
                                for (int j = 0; j < skillList.size(); j++) {
                                    if (skillList.get(j).getSkill_name().equals(locationlist.get
                                            (i))) {
                                        String getSkillID = skillList.get(j).getSkillcategoryid();
                                        if (getSkillID.equalsIgnoreCase(getSkillCategoryID)) {
                                            selectedskilllist.add(skillList.get(j).getSkill_name
                                                    ());
                                        }
                                    }
                                }
                                String[] getskilladed = selectedskilllist.toArray(new
                                        String[selectedskilllist.size()]);
                                if (getskilladed.length > 0) {
                                    selectedskill.setText(Arrays.toString(getskilladed));
                                } else {
                                    selectedskill.setText("");
                                }
                            }
                        }
                        return root;
                    }
                };
                filterskcate.setAdapter(scadapter);
            }
        });
        categoryview.setVisibility(View.VISIBLE);
        skillview.setVisibility(View.GONE);
        filterskcate.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        filterskill.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //get|set the skill category items
        scadapter = new ArrayAdapter<SkillCategory>(JobSearch_Filter.this, R.layout
                .skillcategory_row, skillCategoryList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context mContext = this.getContext();
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                View root;
                if (convertView == null) {
                    root = layoutInflater.inflate(R.layout.skillcategory_row, parent, false);
                } else {
                    root = convertView;
                }
                TextView textView = (TextView) root.findViewById(R.id.editjob_skill_catename);
                TextView selectedskill = (TextView) root.findViewById(R.id
                        .editjob_skillcate_skillname);
                textView.setText(skillCategoryList.get(position).getSkillcategoryname());
                if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                    List<String> locationlist = Arrays.asList(GlobalData.getSkill.split(","));
                    getSkillCategoryID = skillCategoryList.get(position).getSkillcategoryid();
                    skillList = skillCategoryList.get(position).getSkills();
                    ArrayList<String> selectedskilllist = new ArrayList<>();
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < skillList.size(); j++) {
                            if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                                String getSkillID = skillList.get(j).getSkillcategoryid();
                                if (getSkillID.equalsIgnoreCase(getSkillCategoryID)) {
                                    selectedskilllist.add(skillList.get(j).getSkill_name());
                                }
                            }
                        }
                        String[] getskilladed = selectedskilllist.toArray(new
                                String[selectedskilllist.size()]);
                        if (getskilladed.length > 0) {
                            selectedskill.setText(Arrays.toString(getskilladed));
                        } else {
                            selectedskill.setText("");
                        }
                    }
                }
                return root;
            }
        };
        filterskcate.setAdapter(scadapter);
        if (getSkillCategoryID == null) {
            skillList = skillCategoryList.get(0).getSkills();
        } else {
            for (int i = 0; i < skillCategoryList.size(); i++) {
                if (skillCategoryList.get(i).getSkillcategoryid().equals(getSkillCategoryID)) {
                    skillList = skillCategoryList.get(i).getSkills();
                }
            }
        }
        //get|set the skills
        skadapter = new ArrayAdapter<Skill>(this, R.layout.filter_listrow, skillList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                textView.setText(skillList.get(position).getSkill_name());
                return textView;
            }
        };
        filterskill.setAdapter(skadapter);
        if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
            List<String> locationlist = Arrays.asList(GlobalData.getSkill.split(","));
            isCheckedSkill = new boolean[skillList.size()];
            indexskill = -1;
            for (int i = 0; i < locationlist.size(); i++)
                for (int j = 0; j < skillList.size(); j++) {
                    if (!(SelectedskillList.contains(locationlist.get(i)))) {
                        SelectedskillList.add(locationlist.get(i));
                    }
                    if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                        indexskill = j;
                        isCheckedSkill[indexskill] = true;
                        filterskill.setItemChecked(indexskill, true);
                    }
                }
            skilllist.setText(GlobalData.getSkill);
        } else {
            skilllist.setText(R.string.select);
            isCheckedSkill = new boolean[skillList.size()];
            Arrays.fill(isCheckedSkill, false);
        }
        //get|set the skill category check|uncheck
        filterskcate.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                getSkillCategory = skillCategoryList.get(position).getSkillcategoryname();
                getSkillCategoryID = skillCategoryList.get(position).getSkillcategoryid();
                sl_skillheader.setText(getSkillCategory);
                String[] getLocationaded = SelectedskillList.toArray(new String[SelectedskillList
                        .size()]);
                String getlocationarray = Arrays.toString(getLocationaded);
                GlobalData.getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                GlobalData.getSkill = GlobalData.getSkill.replace(", ", ",");
                skillList = new ArrayList<>();
                for (int i = 0; i < skillCategoryList.size(); i++) {
                    if (skillCategoryList.get(i).getSkillcategoryid().equals(getSkillCategoryID)) {
                        skillList = skillCategoryList.get(i).getSkills();
                    }
                }
                //get|set the skills
                skadapter = new ArrayAdapter<Skill>(JobSearch_Filter.this, R.layout
                        .filter_listrow, skillList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            Context mContext = this.getContext();
                            LayoutInflater vi = (LayoutInflater) mContext
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.filter_listrow, parent, false);
                        }
                        CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id
                                .text1);
                        textView.setText(skillList.get(position).getSkill_name());
                        return textView;
                    }
                };
                filterskill.setAdapter(skadapter);
                categoryview.setVisibility(View.GONE);
                skillview.setVisibility(View.VISIBLE);
                if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                    List<String> locationlist = Arrays.asList(GlobalData.getSkill.split(","));
                    isCheckedSkill = new boolean[skillList.size()];
                    indexskill = -1;
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < skillList.size(); j++) {
                            if (!(SelectedskillList.contains(locationlist.get(i)))) {
                                SelectedskillList.add(locationlist.get(i));
                            }
                            if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                                indexskill = j;
                                isCheckedSkill[indexskill] = true;
                                filterskill.setItemChecked(indexskill, true);
                            }
                        }
                    }
                } else {
                    isCheckedSkill = new boolean[skillList.size()];
                    Arrays.fill(isCheckedSkill, false);
                }
                //skills check|uncheck
                filterskill.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long
                            arg3) {
                        if (!isCheckedSkill[position]) {
                            isCheckedSkill[position] = true;
                            if (!(SelectedskillList.contains(skadapter.getItem(position)
                                    .getSkill_name()))) {
                                SelectedskillList.add(skadapter.getItem(position).getSkill_name());
                            }
                        } else {
                            isCheckedSkill[position] = false;
                            SelectedskillList.remove(skadapter.getItem(position).getSkill_name());
                        }
                    }
                });
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        //skill selection reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getSkillCategory = null;
                getSkillCategoryID = null;
                SelectedskillList.clear();
                isCheckedSkill = new boolean[skillList.size()];
                Arrays.fill(isCheckedSkill, false);
                GlobalData.getSkill = null;
                skilllist.setText(R.string.select);
                skill.setText(GlobalData.getSkill);
                alertDialog.dismiss();
                SkillAlert();
            }
        });
        //skill selection done and get the must have skill list and must have setion check|uncheck
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String[] getLocationaded = SelectedskillList.toArray(new String[SelectedskillList
                        .size()]);
                String getlocationarray = Arrays.toString(getLocationaded);
                GlobalData.getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                GlobalData.getSkill = GlobalData.getSkill.replace(", ", ",");
                if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                    skill.setText(GlobalData.getSkill);
                    skilllist.setText(R.string.edit);
                } else {
                    skill.setText("");
                    skilllist.setText(R.string.select);
                }
                alertDialog.dismiss();
            }
        });
    }*/

    private class getFilterData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobSearch_Filter.this, R.style.MyTheme);
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
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                if (selectedRoleList.size() > 0) {
                    if (selectedRoleList.contains(GlobalData.getCRole)) {
                        paramsadd.addFormDataPart("role", GlobalData.getCRole);
                    } else {
                        if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                            paramsadd.addFormDataPart("role", GlobalData.getRole);
                        }
                    }
                } else {
                    paramsadd.addFormDataPart("role", GlobalData.getCRole);
                }
            } else {
                if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                    paramsadd.addFormDataPart("role", GlobalData.getRole);
                }
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            }
            /*else {
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                }
            }*/
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", removealltext);
                Log.e("loc2", GlobalData.getFinalLocation);
            }
            //if (GlobalData.islocationAvail.equalsIgnoreCase("No")) {
            paramsadd.addFormDataPart("locationflag", "Yes");
            //}
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
          /*  if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", GlobalData.getSkill);
            }*/
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "jobsearch_new.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.getroleresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (JobSearch_Filter.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("CATEGORY", GlobalData.getroleresponse);
            editor.apply();
            if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.getroleresponse);
                    getFilterStatus = responseObj.getString("message");
                    if (!languages.equalsIgnoreCase("English")) {
                        getFilterStatusTamil = responseObj.getString("message_tamil");
                    }
                    Gson gson = new Gson();
                    //if (GlobalData.islocationAvail.equalsIgnoreCase("No")) {
                    GlobalData.LocationList = new ArrayList<>();
                    GlobalData.LocationList = gson.fromJson(responseObj.getString
                                    ("locations"),
                            new TypeToken<ArrayList<FilterLocation>>() {
                            }.getType());
                    //citylist
                    GlobalData.locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    GlobalData.MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    // if (GlobalData.LocationList.size() > 0) {
                    //     GlobalData.islocationAvail = "Yes";
                    //  }
                    //}
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getNearLocation = responseObj.getString("cities_name");
                        GlobalData.getFinalLocation = responseObj.getString("cities_name");
                        SharedPreferences sharedPreferences1 = PreferenceManager
                                .getDefaultSharedPreferences(JobSearch_Filter.this);
                        Editor editor1 = sharedPreferences1.edit();
                        editor1.putString("F_NL", GlobalData.getNearLocation);
                        editor1.putString("F_FL", GlobalData.getFinalLocation);
                        editor1.apply();
                    }
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        getcount = responseObj.getInt("count");
                        isbackTrue = true;
                        GlobalData.select_role = new ArrayList<>();
                        JSONArray groups = responseObj.getJSONArray("role_name");
                        for (int i = 0; i < groups.length(); i++) {
                            JSONObject c = groups.getJSONObject(i);
                            JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                            jscategoryitem.setRole_id(c.getString("role_group_id"));
                            jscategoryitem.setRole_name(c.getString("role_name"));
                            jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                            jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                            GlobalData.select_role.add(jscategoryitem);
                        }
                        finish();
                        GlobalData.getyesnoflag = "Yes";
                        editor.putString("YNFLAG", GlobalData.getyesnoflag);
                        editor.apply();
                    } else {
                        isbackTrue = false;
                        String new_word = getFilterStatus.substring(getFilterStatus.length() - 6);
                        if (new_word.equalsIgnoreCase("again.")) {
                            alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
                            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialog.setCanceledOnTouchOutside(false);
                            getFilterConditionAlert();
                            isbackTrue = false;
                        } else if ((GlobalData.getLocation.equalsIgnoreCase(GlobalData
                                .getPrevLocation)) && (GlobalData.getLocation.equalsIgnoreCase(GlobalData
                                .getFinalLocation)) && (GlobalData.getIndustry == null
                                && GlobalData.getRole == null
                                && GlobalData.getSSalary == null
                                && GlobalData.getJobType == null
                                && GlobalData.getJobIDType == null
                                && GlobalData.getExperience == null
                                // && GlobalData.getSkill == null
                                && GlobalData.getGender == null)
                                && getyesnoflag.equalsIgnoreCase("No")) {
                            GlobalData.select_role = new ArrayList<>();
                            try {
                                JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                                JSONArray groups = getrolelocresponse.getJSONArray("role_name");
                                for (int i = 0; i < groups.length(); i++) {
                                    JSONObject c = groups.getJSONObject(i);
                                    JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                                    jscategoryitem.setRole_id(c.getString("role_group_id"));
                                    jscategoryitem.setRole_name(c.getString("role_name"));
                                    jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                                    jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                                    GlobalData.select_role.add(jscategoryitem);
                                }
                            } catch (JSONException ignored) {
                            }
                            finish();
                        } else {
                            alertDialog = new Dialog(JobSearch_Filter.this, R.style.MyThemeDialog);
                            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialog.setCanceledOnTouchOutside(false);
                            View emppromptView = View.inflate(JobSearch_Filter.this, R.layout
                                    .delete_popup, null);
                            TextView f_popupheader = (TextView) emppromptView.findViewById(R.id
                                    .d_popupheader);
                            f_popupheader.setText(R.string.confirmation);
                            TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                                    .d_popup_subheader);
                            f_popupsubheader.setText(getFilterStatus);
                            if (!languages.equalsIgnoreCase("English")) {
                                f_popupsubheader.setText(getFilterStatusTamil);
                            }
                            Button no = (Button) emppromptView.findViewById(R.id.d_no);
                            Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                            alertDialog.setContentView(emppromptView);
                            alertDialog.show();
                            alertDialog.setCancelable(false);
                            yes.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    Gson gson = new Gson();
                                    GlobalData.select_role = new ArrayList<>();
                                    try {
                                        JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                                        JSONArray groups = getrolelocresponse.getJSONArray("role_name");
                                        for (int i = 0; i < groups.length(); i++) {
                                            JSONObject c = groups.getJSONObject(i);
                                            JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                                            jscategoryitem.setRole_id(c.getString("role_group_id"));
                                            jscategoryitem.setRole_name(c.getString("role_name"));
                                            jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                                            jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                                            GlobalData.select_role.add(jscategoryitem);
                                        }
                                    } catch (JSONException ignored) {
                                    }
                                    finish();
                                    GlobalData.getyesnoflag = "No";
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                            (JobSearch_Filter.this);
                                    Editor editor = sharedPreferences.edit();
                                    editor.putString("YNFLAG", GlobalData.getyesnoflag);
                                    editor.apply();
                                    alertDialog.dismiss();
                                }
                            });
                            no.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    GlobalData.getroleresponse = null;
                                    GlobalData.getyesnoflag = "Yes";
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                            (JobSearch_Filter.this);
                                    Editor editor = sharedPreferences.edit();
                                    editor.putString("CATEGORY", GlobalData.getroleresponse);
                                    editor.putString("YNFLAG", GlobalData.getyesnoflag);
                                    editor.apply();
                                    resetNearFinalLocation();
                                    alertDialog.dismiss();
                                }
                            });
                        }
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

    private void getFilterConditionAlert() {
        View emppromptView = View.inflate(JobSearch_Filter.this, R.layout.popup_single, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.ds_popupheader);
        f_popupheader.setText(R.string.confirmation);
        TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id.ds_popup_subheader);
        if ((GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                ((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty())
                        && (GlobalData.getRole == null || GlobalData.getRole.isEmpty())
                        && (GlobalData.getSSalary == null || GlobalData.getSSalary.isEmpty())
                        && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                        && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                        && (GlobalData.getExperience == null || GlobalData.getExperience.isEmpty())
                        //|| !(GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                        && (GlobalData.getGender == null || GlobalData.getGender.isEmpty()))) {
            if (languages.equalsIgnoreCase("English")) {
                f_popupsubheader.setText(getFilterStatus);
            } else {
                f_popupsubheader.setText(R.string.nojobfoundstate);
            }
        } else if ((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty())
                || (GlobalData.getRole != null && !GlobalData.getRole.isEmpty())
                || (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty())
                || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                || (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty())
                //|| !(GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                || (GlobalData.getGender != null && !GlobalData.getGender.isEmpty())) {
            f_popupsubheader.setText(R.string.nojobfoundfilcondwoloc);
        } else {
            f_popupsubheader.setText(R.string.nojobfoundfilcondwoloc);
        }
        Button no = (Button) emppromptView.findViewById(R.id.d_ok);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GlobalData.getyesnoflag = "Yes";
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                        (JobSearch_Filter.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("YNFLAG", GlobalData.getyesnoflag);
                editor.apply();
                resetNearFinalLocation();
                alertDialog.dismiss();
            }
        });
    }

    private void resetNearFinalLocation() {
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            GlobalData.getNearLocation = GlobalData.getLocation;
            GlobalData.getFinalLocation = GlobalData.getLocation;
            SharedPreferences sharedPreferences1 = PreferenceManager
                    .getDefaultSharedPreferences(JobSearch_Filter.this);
            Editor editor1 = sharedPreferences1.edit();
            editor1.putString("F_NL", GlobalData.getNearLocation);
            editor1.putString("F_FL", GlobalData.getFinalLocation);
            editor1.apply();
        }
        if (GlobalData.select_role.size() > 0) {
            GlobalData.select_role.clear();
        }
        if (GlobalData.jobList.size() > 0) {
            GlobalData.jobList.clear();
        }
    }
}

