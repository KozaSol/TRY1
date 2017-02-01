package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.CityAdapter;
import com.jobsearchtry.adapter.IndustryAdapter;
import com.jobsearchtry.adapter.RoleAdapter;
import com.jobsearchtry.adapter.SpeciAdapter;
import com.jobsearchtry.adapter.SpinnerAdapter;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.CustomAlertDialog;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.Experience;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.JobType;
import com.jobsearchtry.wrapper.Jobs;
import com.jobsearchtry.wrapper.Qualification;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.Specialization;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Edit_Job extends Activity {
    private EditText jobtitle, jobdesc, emp_pj_clientname;
    private TextView miniquali_req, gender_req, experience_req, stateselectedloc, cityselectedloc,
            cl_cityheader;
    private Helper skill_req;
    private Button mini_quali, e_emp_pj_qlSp, expneed, jobrole, jobtype, salary, industry, gender;
    private ToggleButton showphoneno;
    private LinearLayout e_emp_pj_jobrole_lay, e_emp_pj_minql_lay, e_emp_pj_exp_lay, e_emp_pj_jobtype_lay,
            e_emp_pj_sly_lay, ej_location_lay,
            e_emp_pj_industry_lay, emp_pj_Gender_lay, e_skill_req_lay,
            e_exp_req_lay, e_gender_req_lay, e_quali_req_lay, e_emp_pj_qlSp_layout, clientnamelay,
            e_musthavesection, locstate_view, city_view, e_pj_skill_lay, e_emp_pj_qlSp_lay;
    private String getJobTitle, getLocation, getQuali, getExp, getJobRole,
            getJobType, getSalary, getIndustry, JobTypeLocal,
            getPhoneStatus = "Yes", getJobTitleInit, getJobDescInit,
            getGender, getJobTypeID = "1", getJobdesc, getSkill = null,
            getSpecialization, RoleLocal, ExperienceLocal,
            getQuali_ID, getQualiReq, getGenderReq, getExperienceReq, getSkillReq, getState,
            getStateID, getEmpIndustry, languages,
            getClientname = null, getRequiredSkill = null,getStateName,
            getFrom = "State", IndustryLocal, getPrevJobTitle = null, getPrevDesc = null;
    private Button skills;
    private Button location;
    private CheckBox quali_cb, gender_cb, exp_cb;
    private ProgressDialog pg;
    private ArrayAdapter<String> skill_must;
    private SpeciAdapter spadapter;
    private ArrayList<Qualification> select_qualification = new ArrayList<>();
    private ArrayList<Specialization> specificationList = new ArrayList<>();
    private ArrayList<Industry> industriesList = new ArrayList<>();
    private ArrayList<Role> select_role = new ArrayList<>();
    private ArrayList<Gender> select_gender = null;
    private ArrayList<JobType> JobTypeList = new ArrayList<>();
    private ArrayList<String> SalaryList = new ArrayList<>();
    private ArrayList<Experience> ExpList = new ArrayList<>();
    private ArrayList<String> selectedLocationList = new ArrayList<>(),
            selectedLocationLocalList = new ArrayList<>(),selectedStateList = new ArrayList<>();
    private final ArrayList<String> RequiredSkillList = new ArrayList<>();
    private final ArrayList<String> SelectedRequiredSkillList = new ArrayList<>();
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private RoleAdapter roleAdapter;
    private ArrayList<City> MainlocationCityList = new ArrayList<>(), locationCityList = new ArrayList<>();
    private ArrayList<FilterLocation> locationList = new ArrayList<>();
    private ArrayList<City> CityList = null;
    private boolean[] isCheckedSkill, isCheckedLocation, isCheckedRequiredSkill;
    private int indexcity = -1, indexqual = -1, indexspec = -1, GenderalIndustryId, indexjobtype = -1;
    private OkHttpClient client = null;
    private ArrayList<Skill> skillList = new ArrayList<>();
    private ArrayList<String> SelectedskillList = new ArrayList<>();
    private ArrayAdapter<Skill> skadapter;
    private int indexskillmust = -1, indexindustry = -1, indexrole = -1, indexexp = -1, indexgender = -1, indexsalary = -1;
    private ListView filterstate, filtercity;
    private AutoCompleteTextView locfilt_citysearch, autocity;

    // remove the html tags and displaying the text
/*    public static String html2text(String html) {
       return Jsoup.parse(html).text();
   }*/
    //back page identify from edit job, if GlobalData.frompostjob = PostJob means it will go to
    // posting a job on final submit form, else jobs posted.
    @Override
    public void onBackPressed() {
        onbackclick();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_postedjob);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        getQuali = getString(R.string.selectquali);
        getExp = getString(R.string.selectexp);
        getJobRole = getString(R.string.selectrole);
        getJobType = getString(R.string.selectjobtype);
        getSalary = getString(R.string.selectsalary);
        getIndustry = getString(R.string.selectindustry);
        getGender = getString(R.string.selectgender);
        getSpecialization = getString(R.string.selectspec);
        //get the company id, back page identity,job id from session
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.ejobid = sharedPreferences.getString("EJOB_ID", GlobalData.ejobid);
        GlobalData.frompostjob = sharedPreferences.getString("FROMPOSTJOB", GlobalData
                .frompostjob);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS", GlobalData
                .emp_login_status);
        showphoneno = (ToggleButton) findViewById(R.id.e_emp_pj_showphoneno);
        e_musthavesection = (LinearLayout) findViewById(R.id.e_musthavesection);
        e_skill_req_lay = (LinearLayout) findViewById(R.id.e_skill_req_lay);
        e_exp_req_lay = (LinearLayout) findViewById(R.id.e_exp_req_lay);
        e_gender_req_lay = (LinearLayout) findViewById(R.id.e_gender_req_lay);
        e_quali_req_lay = (LinearLayout) findViewById(R.id.e_quali_req_lay);
        e_emp_pj_qlSp_layout = (LinearLayout) findViewById(R.id.e_emp_pj_qlSp_layout);
        e_emp_pj_qlSp_lay = (LinearLayout) findViewById(R.id.e_emp_pj_qlSp_lay);
        e_emp_pj_qlSp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (specificationList.size() > 0) {
                    SpecializationAlert();
                }
            }
        });
        emp_pj_clientname = (EditText) findViewById(R.id.e_emp_pj_clientname);
        clientnamelay = (LinearLayout) findViewById(R.id.e_clientnamelay);
        e_pj_skill_lay = (LinearLayout) findViewById(R.id.e_pj_skill_lay);
        //get the details of this job id
        if (new UtilService().isNetworkAvailable(Edit_Job.this)) {
            new GetJobDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        jobtitle = (EditText) findViewById(R.id.e_emp_pj_jobtitle);
        //clicking logo it will go dashboard page
        ImageButton ej_header = (ImageButton) findViewById(R.id.js_r_h);
        ej_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Edit_Job.this, EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        //location selection popup opens
        location = (Button) findViewById(R.id.e_emp_pj_location);
        ej_location_lay = (LinearLayout) findViewById(R.id.ej_location_lay);
        ej_location_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationList.size() > 0) {
                    LocationAlert();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
        mini_quali = (Button) findViewById(R.id.e_emp_pj_minql);
        e_emp_pj_qlSp = (Button) findViewById(R.id.e_emp_pj_qlSp);
        e_emp_pj_minql_lay = (LinearLayout) findViewById(R.id.e_emp_pj_minql_lay);
        e_emp_pj_minql_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_qualification.size() > 0) {
                    QualificationAlert();
                }
            }
        });
        expneed = (Button) findViewById(R.id.e_emp_pj_exp);
        e_emp_pj_exp_lay = (LinearLayout) findViewById(R.id.e_emp_pj_exp_lay);
        e_emp_pj_exp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExpList.size() > 0) {
                    ExperienceAlert();
                }
            }
        });
        jobrole = (Button) findViewById(R.id.e_emp_pj_jobrole);
        e_emp_pj_jobrole_lay = (LinearLayout) findViewById(R.id.e_emp_pj_jobrole_lay);
        e_emp_pj_jobrole_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_role.size() > 0) {
                    RoleAlert();
                }
            }
        });
        jobtype = (Button) findViewById(R.id.e_emp_pj_jobtype);
        e_emp_pj_jobtype_lay = (LinearLayout) findViewById(R.id.e_emp_pj_jobtype_lay);
        e_emp_pj_jobtype_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (JobTypeList != null && JobTypeList.size() > 0) {
                    JobTypeAlert();
                }
            }
        });
        salary = (Button) findViewById(R.id.e_emp_pj_sly);
        e_emp_pj_sly_lay = (LinearLayout) findViewById(R.id.e_emp_pj_sly_lay);
        e_emp_pj_sly_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SalaryList.size() > 0) {
                    SalaryAlert();
                }
            }
        });
        industry = (Button) findViewById(R.id.e_emp_pj_industry);
        e_emp_pj_industry_lay = (LinearLayout) findViewById(R.id.e_emp_pj_industry_lay);
        e_emp_pj_industry_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (industriesList.size() > 0) {
                    IndustryAlert();
                }
            }
        });
        emp_pj_Gender_lay = (LinearLayout) findViewById(R.id.e_emp_pj_Gender_lay);
        emp_pj_Gender_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_gender != null && select_gender.size() > 0) {
                    GenderAlert();
                }
            }
        });
        gender = (Button) findViewById(R.id.e_emp_pj_gender);
        skills = (Button) findViewById(R.id.e_emp_pj_sp_skill);
        jobdesc = (EditText) findViewById(R.id.e_emp_pj_sp_aboutjob);
        jobdesc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.e_emp_pj_sp_aboutjob) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        //popup opens for skill selection
        e_pj_skill_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    if (skillList.size() > 0) {
                        SkillAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
        miniquali_req = (TextView) findViewById(R.id.e_mini_qual_req);
        quali_cb = (CheckBox) findViewById(R.id.e_checkbox_miniqual);
        experience_req = (TextView) findViewById(R.id.e_experience_req);
        exp_cb = (CheckBox) findViewById(R.id.e_checkbox_experience);
        gender_req = (TextView) findViewById(R.id.e_gender_req);
        gender_cb = (CheckBox) findViewById(R.id.e_checkbox_gender);
        skill_req = (Helper) findViewById(R.id.e_skill_req);
        skill_req.setExpanded(true);
        quali_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getQualiReq = "Y";
                } else {
                    getQualiReq = "N";
                }
            }
        });
        exp_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getExperienceReq = "Y";
                } else {
                    getExperienceReq = "N";
                }
            }
        });
        gender_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getGenderReq = "Y";
                } else {
                    getGenderReq = "N";
                }
            }
        });
        //validate the page and check with the must have section then update the job details
        Button submit = (Button) findViewById(R.id.e_tryEmpEditJob);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedRequiredSkillList.size() > 0) {
                    String[] getskillmustaded = SelectedRequiredSkillList
                            .toArray(new String[SelectedRequiredSkillList.size()]);
                    String getskillmustarray = Arrays.toString(getskillmustaded);
                    getRequiredSkill = getskillmustarray.substring(1, getskillmustarray.length() -
                            1);
                    getRequiredSkill = getRequiredSkill.replace(", ", ",");
                    getSkillReq = "Y";
                } else {
                    getSkillReq = "N";
                    getRequiredSkill = "";
                }
                if (verifyEditJob()) {
                    new getPostJob().execute();
                }
            }

            //validatating the page
            private boolean verifyEditJob() {
                getJobTitle = jobtitle.getText().toString();
                getJobdesc = jobdesc.getText().toString();
                if (null == getJobTitle || getJobTitle.length() < 3) {
                    Toast.makeText(getApplicationContext(), getString(R.string.jobnamevalidation),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getIndustry == null || getIndustry.isEmpty() || getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.industryvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getJobRole == null || getJobRole.isEmpty() || getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.pleaseselecttherole), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (null == getLocation || getLocation.length() == 0 || getLocation.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.locationvalidation), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                String[] separated = getLocation.split(",");
                if (separated.length > 10) {
                    Toast.makeText(getApplicationContext(), getString(R.string.postjoblocationlimit),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getEmpIndustry.equalsIgnoreCase("HR / Manpower Agency")) {
                    getClientname = emp_pj_clientname.getText().toString();
                    if (getClientname.length() < 3) {
                        Toast.makeText(Edit_Job.this, getString(R.string.clientnamevalidation),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                if (getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.pleaseselectqualification), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (specificationList.size() > 0 && getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.pleaseselectthespeci), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.expvalidation), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.pleasethegender), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.jobtypevalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.salaryvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (null == getJobdesc || getJobdesc.length() < 3) {
                    Toast.makeText(getApplicationContext(), getString(R.string.aboutjobvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (!(new UtilService().isNetworkAvailable(Edit_Job.this))) {
                    Toast.makeText(Edit_Job.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
    }

    private void onbackclick() {
        if (!jobtitle.getText().toString().equalsIgnoreCase(getPrevJobTitle) ||
                !jobdesc.getText().toString().equalsIgnoreCase(getPrevDesc)) {
            if (new BackAlertDialog().isBackDialog(Edit_Job.this)) {
                if (GlobalData.frompostjob.equalsIgnoreCase("PostJob")) {
                    startActivity(new Intent(Edit_Job.this, PostJobFinalSubmit.class));
                    finish();
                } else {
                    startActivity(new Intent(Edit_Job.this, JobsPosted.class));
                    finish();
                }
            }
        } else {
            if (GlobalData.frompostjob.equalsIgnoreCase("PostJob")) {
                startActivity(new Intent(Edit_Job.this, PostJobFinalSubmit.class));
                finish();
            } else {
                startActivity(new Intent(Edit_Job.this, JobsPosted.class));
                finish();
            }
        }
    }

    //get the details of job for the job id
    class GetJobDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Edit_Job.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("action", "JobViewEdit");
            paramsadd.addFormDataPart("job_id", GlobalData.ejobid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update.php").post
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
                    GenderalIndustryId = responseObj.getInt("industry_id");
                    Gson gson = new Gson();
                    Jobs jobdetail = gson.fromJson(responseObj.getString("list"), new TypeToken<Jobs>
                            () {
                    }.getType());
                    //employer registered industry = "HR / Manpower Agency" - client name visible,
                    // else client name field hidden
                    getEmpIndustry = jobdetail.getEmployer_industry_name();
                    if (getEmpIndustry.equalsIgnoreCase("HR / Manpower Agency")) {
                        clientnamelay.setVisibility(View.VISIBLE);
                        getClientname = jobdetail.getClientname();
                        emp_pj_clientname.setText(getClientname);
                    } else {
                        getClientname = "";
                        clientnamelay.setVisibility(View.GONE);
                    }
                    jobtitle.setText(jobdetail.getJob_title());
                    if (jobdetail.getJob_title() != null) {
                        getPrevJobTitle = jobdetail.getJob_title();
                    }
                    getJobTitleInit = jobdetail.getJob_title();
                    getIndustry = jobdetail.getIndustry();
                    getJobRole = jobdetail.getRole();
                    getSkill = jobdetail.getSkills();
                    industriesList = new ArrayList<>();
                    industriesList.addAll((Collection<? extends Industry>) gson.fromJson
                            (responseObj.getString("industries"),
                                    new TypeToken<ArrayList<Industry>>() {
                                    }.getType()));
                    /*if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                        industry.setText(getIndustry);
                        if (!languages.equalsIgnoreCase("English")) {
                            setIndustryLocalLang(getIndustry);
                        }
                    }*/
                    if (getIndustry != null) {
                        if (!getIndustry.isEmpty()) {
                            if (!getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                                industry.setText(getIndustry);
                                if (!languages.equalsIgnoreCase("English")) {
                                    setIndustryLocalLang(getIndustry);
                                }
                            }
                        }
                    } else {
                        industry.setText(R.string.selectindustry);
                    }
                    select_role = new ArrayList<>();
                    for (int i = 0; i < industriesList.size(); i++) {
                        if (industriesList.get(i).getIndustry_name().equals(getIndustry)) {
                            select_role.addAll(industriesList.get(i)
                                    .getRole());
                        }
                    }
                    if (GenderalIndustryId != 0 && industriesList.size() > 0) {
                        select_role.addAll(industriesList.get(GenderalIndustryId - 1)
                                .getRole());
                        RoleRemoveFromGenderal();
                    }
                    getSkill = jobdetail.getSkills();
                    /*if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                        skillList = new ArrayList<>();
                        for (int i = 0; i < select_role.size(); i++) {
                            if (select_role.get(i).getRole_name().equals(getJobRole)) {
                                skillList.addAll(select_role.get(i)
                                        .getSkills());
                            }
                        }
                        if (skillList.size() > 0) {
                            e_pj_skill_lay.setVisibility(View.VISIBLE);
                        } else {
                            e_pj_skill_lay.setVisibility(View.GONE);
                        }
                        jobrole.setText(getJobRole);
                        if (!languages.equalsIgnoreCase("English")) {
                            setRoleLocalLang(getJobRole);
                        }
                    }*/
                    if (getJobRole != null) {
                        if (!getJobRole.isEmpty()) {
                            if (!getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                                skillList = new ArrayList<>();
                                for (int i = 0; i < select_role.size(); i++) {
                                    if (select_role.get(i).getRole_name().equals(getJobRole)) {
                                        skillList.addAll(select_role.get(i)
                                                .getSkills());
                                    }
                                }
                                if (skillList.size() > 0) {
                                    e_pj_skill_lay.setVisibility(View.VISIBLE);
                                } else {
                                    e_pj_skill_lay.setVisibility(View.GONE);
                                }
                                jobrole.setText(getJobRole);
                                if (!languages.equalsIgnoreCase("English")) {
                                    setRoleLocalLang(getJobRole);
                                }
                            }
                        }
                    } else {
                        jobrole.setText(R.string.selectrole);
                    }
                    getRequiredSkill = jobdetail.getReq_skillname();
                    if (getSkill != null && !getSkill.isEmpty()) {
                        skills.setText(getSkill);
                        e_skill_req_lay.setVisibility(View.VISIBLE);
                        List<String> skilllist = Arrays.asList(getSkill.split(","));
                        for (int i = 0; i < skilllist.size(); i++) {
                            RequiredSkillList.add(skilllist.get(i));
                        }
                        skill_req.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        skill_must = new ArrayAdapter<String>(Edit_Job.this, R.layout
                                .skill_helper,
                                RequiredSkillList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = convertView;
                                if (v == null) {
                                    Context mContext = this.getContext();
                                    LayoutInflater vi = (LayoutInflater) mContext
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    v = vi.inflate(R.layout.skill_helper, parent, false);
                                }
                                CheckedTextView textView = (CheckedTextView) v.findViewById
                                        (android.R.id.text1);
                                textView.setText(RequiredSkillList.get(position));
                                return textView;
                            }
                        };
                        skill_req.setAdapter(skill_must);
                        if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                            List<String> skillmustlist = Arrays.asList(getRequiredSkill.split("," +
                                    ""));
                            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                            indexskillmust = -1;
                            for (int i = 0; i < skillmustlist.size(); i++) {
                                indexskillmust = RequiredSkillList.indexOf(skillmustlist.get(i));
                                if (indexskillmust != -1) {
                                    isCheckedRequiredSkill[indexskillmust] = true;
                                    skill_req.setItemChecked(indexskillmust, true);
                                    if (!SelectedRequiredSkillList.contains(skillmustlist.get(i)
                                    )) {
                                        SelectedRequiredSkillList.add(skillmustlist.get(i));
                                    }
                                }
                            }
                        } else {
                            getRequiredSkill = "";
                            indexskillmust = -1;
                            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                            Arrays.fill(isCheckedRequiredSkill, false);
                        }
                        updateSkillMust();
                    } else {
                        getSkill = "";
                        skills.setText(R.string.select);
                        e_skill_req_lay.setVisibility(View.GONE);
                    }
                    getLocation = jobdetail.getLocation();
                    locationCityList = new ArrayList<>();
                    MainlocationCityList = new ArrayList<>();
                    locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    if (getLocation != null && !getLocation.isEmpty()) {
                        location.setText(getLocation);
                        getStatefromlocation(getLocation);
                        if (!languages.equalsIgnoreCase("English")) {
                            setLocationLocalLang(getLocation);
                        }
                    } else {
                        location.setText(R.string.select);
                    }
                    getQuali = jobdetail.getMinimum_qual();
                    /*if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                        mini_quali.setText(getQuali);
                    }*/
                    if (getQuali != null) {
                        if (!getQuali.isEmpty()) {
                            if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                                mini_quali.setText(getQuali);
                            }
                        }
                    } else {
                        mini_quali.setText(R.string.selectquali);
                    }
                    if (getQuali.equalsIgnoreCase("Not Needed") || getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                        e_quali_req_lay.setVisibility(View.GONE);
                    } else {
                        e_quali_req_lay.setVisibility(View.VISIBLE);
                        miniquali_req.setText(getQuali);
                    }
                    getQuali_ID = jobdetail.getQualification_id();
                    select_qualification = new ArrayList<>();
                    Qualification c2 = new Qualification();
                    c2.setId("1");
                    c2.setQuali_name("Not Needed");
                    c2.setSpecialization(null);
                    select_qualification.add(c2);
                    select_qualification.addAll((ArrayList<? extends Qualification>) gson.fromJson(
                            responseObj.getString("qualificationlist"), new
                                    TypeToken<ArrayList<Qualification>>() {
                                    }.getType()));
                    specificationList = new ArrayList<>();
                    for (int i = 0; i < select_qualification.size(); i++) {
                        if (select_qualification.get(i).getId().equals(getQuali_ID) && select_qualification.get(i)
                                .getSpecialization() != null) {
                            specificationList.addAll(select_qualification.get(i)
                                    .getSpecialization());
                        }
                    }
                    if (specificationList.size() == 0 || jobdetail.getSpecilization().isEmpty() || jobdetail.getSpecilization() == null) {
                        e_emp_pj_qlSp_layout.setVisibility(View.GONE);
                        getSpecialization = null;
                    } else {
                        e_emp_pj_qlSp_layout.setVisibility(View.VISIBLE);
                        getSpecialization = jobdetail.getSpecilization();
                        e_emp_pj_qlSp.setText(getSpecialization);
                    }
                    getSalary = jobdetail.getSalary();
                   /* if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                        salary.setText(getSalary);
                    }*/
                    if (getSalary != null) {
                        if (!getSalary.isEmpty()) {
                            if (!getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                                salary.setText(getSalary);
                            }
                        }
                    } else {
                        salary.setText(R.string.selectsalary);
                    }
                    getQualiReq = jobdetail.getMinimum_qualification_req();
                    getExperienceReq = jobdetail.getExperience_req();
                    getGenderReq = jobdetail.getGender_req();
                    getSkillReq = jobdetail.getSkills_req();
                    if (getQualiReq.equalsIgnoreCase("Y")) {
                        quali_cb.setChecked(true);
                    }
                    if (getExperienceReq.equalsIgnoreCase("Y")) {
                        exp_cb.setChecked(true);
                    }
                    if (getGenderReq.equalsIgnoreCase("Y")) {
                        gender_cb.setChecked(true);
                    }
                    SalaryList = new ArrayList<>();
                    JSONArray salarygroup = responseObj.getJSONArray("salaryrange");
                    for (int i = 0; i < salarygroup.length(); i++) {
                        JSONObject salary = salarygroup.getJSONObject(i);
                        String salary_name = salary.getString("salaryrange");
                        SalaryList.add(salary_name);
                    }
                    //gender
                    select_gender = new ArrayList<>();
                    select_gender = gson.fromJson(responseObj.getString("genderlist"), new
                            TypeToken<ArrayList<Gender>>() {
                            }.getType());
                    getGender = jobdetail.getJobgender();
                    /*if (getGender != null && !getGender.isEmpty() && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                        gender.setText(getGender);
                        if (!languages.equalsIgnoreCase("English")) {
                            setGenderLocalLang(getGender);
                        }
                    }*/
                    if (getGender != null) {
                        if (!getGender.isEmpty()) {
                            if (!getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                                gender.setText(getGender);
                                if (!languages.equalsIgnoreCase("English")) {
                                    setGenderLocalLang(getGender);
                                }
                            }
                        }
                    } else {
                        gender.setText(R.string.selectgender);
                    }
                    updateMusthave();
                    if (getGender.equalsIgnoreCase("Any") || getExp.equalsIgnoreCase(getString(R.string.selectgender))) {
                        e_gender_req_lay.setVisibility(View.GONE);
                    } else {
                        e_gender_req_lay.setVisibility(View.VISIBLE);
                        gender_req.setText(getGender);
                        if (!languages.equalsIgnoreCase("English")) {
                            setGenderLocalLang(getGender);
                        }
                    }
                    //job typelist
                    JobTypeList = new ArrayList<>();
                    JobTypeList.addAll((Collection<? extends JobType>) gson.fromJson
                            (responseObj.getString("job_types"),
                                    new TypeToken<ArrayList<JobType>>() {
                                    }.getType()));
                    getJobType = jobdetail.getJob_type_name();
                    /*if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                        jobtype.setText(getJobType);
                        if (!languages.equalsIgnoreCase("English")) {
                            setJobtypeLocalLang(getJobType);
                        }
                    }*/
                    if (getJobType != null) {
                        if (!getJobType.isEmpty()) {
                            if (!getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                                jobtype.setText(getJobType);
                                if (!languages.equalsIgnoreCase("English")) {
                                    setJobtypeLocalLang(getJobType);
                                }

                            }

                        }
                    } else {
                        jobtype.setText(R.string.selectjobtype);
                    }
                    getJobTypeID = jobdetail.getJob_offer_type();
                    getIndustry = jobdetail.getIndustry();
                    getPhoneStatus = jobdetail.getShowphoneflag();
                    final int valuemax = (int) getResources().getDimension(R.dimen.buttonHeightToSmall);
                    final int valuemin = (int) getResources().getDimension(R.dimen.margintop);
                    if (getPhoneStatus.equalsIgnoreCase("Yes")) {
                        showphoneno.setChecked(true);
                        showphoneno.setTextOff("No");
                        showphoneno.setTextOn("Yes");
                        showphoneno.setPadding(valuemin, 0, valuemax, 0);
                    } else {
                        showphoneno.setChecked(false);
                        showphoneno.setTextOff("No");
                        showphoneno.setTextOn("Yes");
                        showphoneno.setPadding(valuemax, 0, valuemin, 0);
                    }
                    showphoneno
                            .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener
                                    () {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton arg0, boolean isChecked) {
                                    if (showphoneno.isChecked()) {
                                        getPhoneStatus = "Yes";
                                        showphoneno.setTextOff("No");
                                        showphoneno.setTextOn("Yes");
                                        showphoneno.setPadding(valuemin, 0, valuemax, 0);
                                        Toast.makeText(Edit_Job.this, getString(R.string.postajobcall), Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        getPhoneStatus = "No";
                                        showphoneno.setTextOff("No");
                                        showphoneno.setTextOn("Yes");
                                        showphoneno.setPadding(valuemax, 0, valuemin, 0);
                                        Toast.makeText(Edit_Job.this, getString(R.string.postajobcannotcall), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                    jobdesc.setText(jobdetail.getJob_description());
                    if (jobdetail.getJob_description() != null) {
                        getPrevDesc = jobdetail.getJob_description();
                    }
                    getJobDescInit = jobdetail.getJob_description();
                    locationList = new ArrayList<>();
                    locationList = gson.fromJson(responseObj.getString("locations"),
                            new TypeToken<ArrayList<FilterLocation>>() {
                            }.getType());
                    //experiencelist
                    ExpList = new ArrayList<>();
                    ExpList = gson.fromJson(responseObj.getString("experiencelist"), new
                            TypeToken<ArrayList<Experience>>() {
                            }.getType());
                    getExp = jobdetail.getExperience();
                    /*if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                        expneed.setText(getExp);
                        if (!languages.equalsIgnoreCase("English")) {
                            setExperienceLocalLang(getExp);
                        }
                    }*/
                    if (getExp != null) {
                        if (!getExp.isEmpty()) {
                            if (!getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                                expneed.setText(getExp);
                                if (!languages.equalsIgnoreCase("English")) {
                                    setExperienceLocalLang(getExp);
                                }
                            }
                        }
                    } else {
                        expneed.setText(R.string.selectexp);
                    }
                    if (getExp.equalsIgnoreCase("Not Needed") || getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                        e_exp_req_lay.setVisibility(View.GONE);
                    } else {
                        experience_req.setText(getExp);
                        if (!languages.equalsIgnoreCase("English")) {
                            setExperienceLocalLang(getExp);
                        }
                        e_exp_req_lay.setVisibility(View.VISIBLE);
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

    //select industry from editing job posted page
    private void IndustryAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectindustry);
        Button industrydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterindustry = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        indexindustry = -1;
        if (industriesList.size() > 0) {
            for (int i = 0; i < industriesList.size(); i++) {
                if (industriesList.get(i).getIndustry_name().equals(getIndustry)) {
                    indexindustry = i;
                }
            }
        }
        final IndustryAdapter adapter = new IndustryAdapter(Edit_Job.this, industriesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = Edit_Job.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = industriesList.get(position).getIndustry_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = industriesList.get(position).getIndustry_name_local();
                }
                if (indexindustry != -1 && (indexindustry == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterindustry.setAdapter(adapter);
        filterindustry.setSelection(indexindustry);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertD.dismiss();
            }
        });
        filterindustry.setOnItemClickListener(new OnItemClickListener() {
                                                  @Override
                                                  public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                                      if (indexindustry != -1 && (indexindustry == pos)) {
                                                          getIndustry = getString(R.string.selectindustry);
                                                          indexindustry = -1;
                                                      } else {
                                                          indexindustry = pos;
                                                          getIndustry = industriesList.get(pos).getIndustry_name();
                                                          getJobRole = getString(R.string.selectrole);
                                                          jobrole.setText(R.string.selectrole);
                                                          select_role = new ArrayList<>();
                                                          if (getIndustry.equals(industriesList.get(pos).getIndustry_name())) {
                                                              String getIndustryId = industriesList.get(pos).getIndustry_id();
                                                              for (int i = 1; i < industriesList.size(); i++) {
                                                                  if (industriesList.get(i).getIndustry_id().equals(getIndustryId) && industriesList.get(i)
                                                                          .getRole().size() > 0) {
                                                                      select_role.addAll(industriesList.get(i)
                                                                              .getRole());
                                                                  }
                                                              }
                                                              if (GenderalIndustryId != 0) {
                                                                  select_role.addAll(industriesList.get(GenderalIndustryId - 1)
                                                                          .getRole());
                                                              }
                                                              RoleRemoveFromGenderal();
                                                          }
                                                      }
                                                      setIndustry();
                                                      adapter.notifyDataSetChanged();
                                                  }
                                              }

        );
        industrydone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertD.dismiss();
            }
        });
    }

    private void RoleRemoveFromGenderal() {
        if (select_role.size() > 0) {
            for (int i = 1; i < select_role.size(); i++) {
                for (int j = i + 1; j < select_role.size(); j++) {
                    if (select_role.get(i).getRole_name().equals(select_role.get(j).getRole_name())) {
                        select_role.remove(j);
                        j--;
                    }
                }
            }
        }
    }

    private void setIndustry() {
        if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            industry.setText(getIndustry);
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang(getIndustry);
            }
        } else {
            industry.setText(getString(R.string.selectindustry));
        }
    }

    private void setIndustryLocalLang(String getIndustry) {
        for (int j = 0; j < industriesList.size(); j++) {
            if (industriesList.get(j).getIndustry_name().equals(getIndustry)) {
                IndustryLocal = industriesList.get(j).getIndustry_name_local();
                industry.setText(IndustryLocal);
            }
        }
    }

    //select role from edit job  page
    private void RoleAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        /*if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            indexrole = -1;
            for (int i = 0; i < select_role.size(); i++) {
                if (select_role.get(i).getRole_name().equals(getJobRole)) {
                    indexrole = i;
                }
            }
        }*/
        if (getJobRole != null) {
            if (!getJobRole.isEmpty()) {
                if (!getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    indexrole = -1;
                    for (int i = 0; i < select_role.size(); i++) {
                        if (select_role.get(i).getRole_name().equals(getJobRole)) {
                            indexrole = i;
                        }
                    }
                }
            }
        } else {
            indexrole = -1;
        }
        final RoleAdapter adapter = new RoleAdapter(Edit_Job.this, select_role) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = Edit_Job.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = select_role.get(position).getRole_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_role.get(position).getRole_name_local();
                }
                if (indexrole != -1 && (indexrole == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterquali.setAdapter(adapter);
        filterquali.setSelection(indexrole);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertD.dismiss();
            }
        });
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexrole != -1 && (indexrole == position)) {
                    getJobRole = getString(R.string.selectrole);
                    indexrole = -1;
                } else {
                    indexrole = position;
                    getJobRole = select_role.get(position).getRole_name();
                    if (getJobRole != null && !getJobRole.isEmpty()) {
                        jobrole.setText(getJobRole);
                        skillList = new ArrayList<>();
                        if (getJobRole.equals(select_role.get(position).getRole_name())) {
                            String getRoleId = select_role.get(position).getRole_group_id();
                            for (int i = 1; i < select_role.size(); i++) {
                                if (select_role.get(i).getRole_group_id().equals(getRoleId) && select_role.get(i)
                                        .getSkills().size() > 0 && select_role.get(i).getSkills() != null) {
                                    skillList.addAll(select_role.get(i)
                                            .getSkills());
                                }
                            }
                        }
                        if (skillList.size() > 0) {
                            e_pj_skill_lay.setVisibility(View.VISIBLE);
                            addskilltoSelectedSkillList();
                            for (Skill u : skillList) {
                                if (!SelectedskillList.contains(u.getSkill_name())) {
                                    SelectedskillList.remove(u.getSkill_name());
                                }
                            }
                        } else {
                            resetSkill();
                            e_pj_skill_lay.setVisibility(View.GONE);
                        }
                        doneSkill();
                    } else {
                        jobrole.setText(R.string.selectrole);
                    }
                }
                setRole();
                adapter.notifyDataSetChanged();
            }
        });
        roledone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertD.dismiss();
            }
        });
    }

    private void setRole() {
        /*if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            jobrole.setText(getJobRole);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang(getJobRole);
            }
        }*/
        if (getJobRole != null) {
            if (!getJobRole.isEmpty()) {
                if (!getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    jobrole.setText(getJobRole);
                    if (!languages.equalsIgnoreCase("English")) {
                        setRoleLocalLang(getJobRole);
                    }
                } else {
                    jobrole.setText(getString(R.string.selectrole));
                }
            }
        } else {
            jobrole.setText(getString(R.string.selectrole));
        }
    }

    private void setRoleLocalLang(String getJobRole) {
        for (int j = 0; j < select_role.size(); j++) {
            if (select_role.get(j).getRole_name().equals(getJobRole)) {
                RoleLocal = select_role.get(j).getRole_name_local();
                jobrole.setText(RoleLocal);
            }
        }
    }

    //skill selection popup opens (skill category -single, skill - multiple)
    private void SkillAlert() {
        SelectedskillList = new ArrayList<>();
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.postjob_skill_popup, null);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterskill = (ListView) emppromptView.findViewById(R.id.pj_filterskilllist);
        filterskill.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
        if (getSkill != null && !getSkill.isEmpty()) {
            List<String> locationlist = Arrays.asList(getSkill.split(","));
            isCheckedSkill = new boolean[skillList.size()];
            int indexskill = -1;
            for (int i = 0; i < locationlist.size(); i++)
                for (int j = 0; j < skillList.size(); j++) {
                    if (!(SelectedskillList.contains(locationlist.get(i)))) {
                        SelectedskillList.add(locationlist.get(i));
                    }
                    if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                        indexskill = j;
                        isCheckedSkill[indexskill] = true;
                        filterskill.setItemChecked(indexskill, true);
                        if (!(RequiredSkillList.contains(locationlist.get(i)))) {
                            RequiredSkillList.add(locationlist.get(i));
                        }
                    }
                }
            skills.setText(getSkill);
            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                List<String> skillmustlist = Arrays.asList(getRequiredSkill.split(","));
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                indexskillmust = -1;
                for (int i = 0; i < skillmustlist.size(); i++) {
                    indexskillmust = RequiredSkillList.indexOf(skillmustlist.get(i));
                    if (indexskillmust != -1) {
                        isCheckedRequiredSkill[indexskillmust] = true;
                        skill_req.setItemChecked(indexskillmust, true);
                    }
                    if (!SelectedRequiredSkillList.contains(skillmustlist.get(i))) {
                        SelectedRequiredSkillList.add(skillmustlist.get(i));
                    }
                }
            } else {
                getRequiredSkill = "";
                indexskillmust = -1;
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                Arrays.fill(isCheckedRequiredSkill, false);
            }
        } else {
            skills.setText(R.string.select);
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
                    if (!(RequiredSkillList.contains(skadapter.getItem(position)
                            .getSkill_name()))) {
                        RequiredSkillList.add(skadapter.getItem(position).getSkill_name());
                    }
                } else {
                    isCheckedSkill[position] = false;
                    SelectedskillList.remove(skadapter.getItem(position).getSkill_name());
                    RequiredSkillList.remove(skadapter.getItem(position).getSkill_name());
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        alertD.setContentView(emppromptView);
        alertD.show();
        //skill selection reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetSkill();
                alertD.dismiss();
                SkillAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doneSkill();
                alertD.dismiss();
            }
        });
        //skill selection done and get the must have skill list and must have setion check|uncheck
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doneSkill();
                alertD.dismiss();
            }
        });
    }

    private void addskilltoSelectedSkillList() {
        if (getSkill != null && !getSkill.isEmpty()) {
            List<String> locationlist = Arrays.asList(getSkill.split(","));
            for (int i = 0; i < locationlist.size(); i++)
                for (int j = 0; j < skillList.size(); j++) {
                    if (!(SelectedskillList.contains(locationlist.get(i)))) {
                        SelectedskillList.add(locationlist.get(i));
                    }
                    if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                        if (!(RequiredSkillList.contains(locationlist.get(i)))) {
                            RequiredSkillList.add(locationlist.get(i));
                        }
                    }
                }
            skills.setText(getSkill);
            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                List<String> skillmustlist = Arrays.asList(getRequiredSkill.split(","));
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                indexskillmust = -1;
                for (int i = 0; i < skillmustlist.size(); i++) {
                    indexskillmust = RequiredSkillList.indexOf(skillmustlist.get(i));
                    if (indexskillmust != -1) {
                        isCheckedRequiredSkill[indexskillmust] = true;
                        skill_req.setItemChecked(indexskillmust, true);
                    }
                    if (!SelectedRequiredSkillList.contains(skillmustlist.get(i))) {
                        SelectedRequiredSkillList.add(skillmustlist.get(i));
                    }
                }
            } else {
                getRequiredSkill = "";
                indexskillmust = -1;
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                Arrays.fill(isCheckedRequiredSkill, false);
            }
        } else {
            skills.setText(R.string.select);
            isCheckedSkill = new boolean[skillList.size()];
            Arrays.fill(isCheckedSkill, false);
        }
    }

    private void doneSkill() {
        String[] getLocationaded = SelectedskillList.toArray(new String[SelectedskillList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
        getSkill = getSkill.replace(", ", ",");
        if (getSkill != null && !getSkill.isEmpty()) {
            skills.setText(getSkill);
            e_skill_req_lay.setVisibility(View.VISIBLE);
        } else {
            e_skill_req_lay.setVisibility(View.GONE);
            skills.setText(R.string.select);
        }
        skill_req.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        skill_must = new ArrayAdapter<String>(Edit_Job.this, R.layout.skill_helper,
                RequiredSkillList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.skill_helper, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id
                        .text1);
                textView.setText(RequiredSkillList.get(position));
                return textView;
            }
        };
        skill_req.setAdapter(skill_must);
        if (getSkill != null && !getSkill.isEmpty()) {
            if (SelectedRequiredSkillList.size() > 0) {
                String[] getreqskilladed = SelectedRequiredSkillList
                        .toArray(new String[SelectedRequiredSkillList.size()]);
                String getreqskillarray = Arrays.toString(getreqskilladed);
                getRequiredSkill = getreqskillarray.substring(1, getreqskillarray.length()
                        - 1);
                getRequiredSkill = getRequiredSkill.replace(", ", ",");
            }
            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                List<String> skillmusttlist = Arrays.asList(getRequiredSkill.split(","));
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                indexskillmust = -1;
                for (int i = 0; i < skillmusttlist.size(); i++) {
                    indexskillmust = RequiredSkillList.indexOf(skillmusttlist.get(i));
                    if (indexskillmust != -1) {
                        isCheckedRequiredSkill[indexskillmust] = true;
                        skill_req.setItemChecked(indexskillmust, true);
                        if (!SelectedRequiredSkillList.contains(skillmusttlist.get(i))) {
                            SelectedRequiredSkillList.add(skillmusttlist.get(i));
                        }
                    } else {
                        SelectedRequiredSkillList.remove(skillmusttlist.get(i));
                    }
                }
            } else {
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                Arrays.fill(isCheckedRequiredSkill, false);
            }
        } else {
            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
            Arrays.fill(isCheckedRequiredSkill, false);
        }
        if (SelectedRequiredSkillList.size() > 0) {
            String[] getreqskilladed = SelectedRequiredSkillList
                    .toArray(new String[SelectedRequiredSkillList.size()]);
            String getreqskillarray = Arrays.toString(getreqskilladed);
            getRequiredSkill = getreqskillarray.substring(1, getreqskillarray.length() -
                    1);
            getRequiredSkill = getRequiredSkill.replace(", ", ",");
        }
        updateSkillMust();
        updateMusthave();
    }

    private void resetSkill() {
        getRequiredSkill = null;
        getRequiredSkill = "";
        SelectedRequiredSkillList.clear();
        SelectedskillList.clear();
        RequiredSkillList.clear();
        isCheckedSkill = new boolean[skillList.size()];
        Arrays.fill(isCheckedSkill, false);
        if (RequiredSkillList.size() > 0) {
            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
            Arrays.fill(isCheckedRequiredSkill, false);
        }
        getSkill = null;
        getSkill = "";
        skills.setText(R.string.select);
        e_skill_req_lay.setVisibility(View.GONE);
        updateMusthave();
    }

    //must have section view show|hide
    private void updateMusthave() {
        if (getGender.equalsIgnoreCase("Any") && getExp.equalsIgnoreCase("Not Needed") &&
                getQuali_ID.equalsIgnoreCase("1") && (getSkill == null || getSkill.isEmpty())) {
            e_musthavesection.setVisibility(View.GONE);
        } else {
            e_musthavesection.setVisibility(View.VISIBLE);
        }
    }

    //must have skill list - check|uncheck skill
    private void updateSkillMust() {
        skill_req.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedRequiredSkill[position]) {
                    isCheckedRequiredSkill[position] = true;
                    if (!(SelectedRequiredSkillList.contains(skill_must.getItem(position)))) {
                        SelectedRequiredSkillList.add(skill_must.getItem(position));
                    }
                } else {
                    isCheckedRequiredSkill[position] = false;
                    SelectedRequiredSkillList.remove(skill_must.getItem(position));
                }
            }
        });
    }

    //select experience from edit job page
    private void ExperienceAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectexp);
        Button experiencedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.spinnerlist);
       /* if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            indexexp = -1;
            for (int i = 0; i < ExpList.size(); i++) {
                if (ExpList.get(i).getExperience_profile_name().equalsIgnoreCase(getExp)) {
                    indexexp = i;
                }
            }
        }*/
        if (getExp != null) {
            if (!getExp.isEmpty()) {
                if (!getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                    indexexp = -1;
                    for (int i = 0; i < ExpList.size(); i++) {
                        if (ExpList.get(i).getExperience_profile_name().equalsIgnoreCase(getExp)) {
                            indexexp = i;
                        }
                    }
                }
            }
        } else {
            indexexp = -1;
        }
        final ArrayAdapter<Experience> adapter = new ArrayAdapter<Experience>(this, R.layout.spinner_item_text, ExpList) {
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
                String yourValue = ExpList.get(position).getExperience_profile_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = ExpList.get(position).getExperience_profile_name_local();
                }
                if (indexexp != -1 && (indexexp == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterexp.setAdapter(adapter);
        filterexp.setSelection(indexexp);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExperience();
                alertD.dismiss();
            }
        });
        filterexp.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexexp != -1 && (indexexp == position)) {
                    getExp = getString(R.string.selectexp);
                    indexexp = -1;
                } else {
                    indexexp = position;
                    getExp = ExpList.get(position).getExperience_profile_name();
                }
                setExperience();
                adapter.notifyDataSetChanged();
            }
        });
        experiencedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExperience();
                alertD.dismiss();
            }
        });
    }

    private void setExperience() {
       /* if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            expneed.setText(getExp);
            experience_req.setText(getExp);
            if (!languages.equalsIgnoreCase("English")) {
                setExperienceLocalLang(getExp);
            }
        }*/
        if (getExp != null) {
            if (!getExp.isEmpty()) {
                if (!getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                    expneed.setText(getExp);
                    experience_req.setText(getExp);
                    if (!languages.equalsIgnoreCase("English")) {
                        setExperienceLocalLang(getExp);
                    }
                }else{
                    expneed.setText(R.string.selectexp);
                }
            }
        } else {
            expneed.setText(R.string.selectexp);
        }
        if (getExp.equalsIgnoreCase("Not Needed") || getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            e_exp_req_lay.setVisibility(View.GONE);
        } else {
            e_exp_req_lay.setVisibility(View.VISIBLE);
        }
        updateMusthave();
    }

    private void setExperienceLocalLang(String getExp) {
        for (int j = 0; j < ExpList.size(); j++) {
            if (ExpList.get(j).getExperience_profile_name().equals(getExp)) {
                ExperienceLocal = ExpList.get(j).getExperience_profile_name_local();
                expneed.setText(ExperienceLocal);
                experience_req.setText(ExperienceLocal);
            }
        }
    }

    //select gender from edit job page
    private void GenderAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectgender);
        Button genderdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtergender = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        /*if (getGender != null && !getGender.isEmpty() && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            indexgender = -1;
            if (select_gender != null && select_gender.size() > 0) {
                Gender tempgender = new Gender();
                tempgender.setGender(getGender);
                indexgender = select_gender.indexOf(tempgender);
            }
        }*/
        if (getGender != null) {
            if (!getGender.isEmpty()) {
                if (!getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                    indexgender = -1;
                    if (select_gender != null && select_gender.size() > 0) {
                        Gender tempgender = new Gender();
                        tempgender.setGender(getGender);
                        indexgender = select_gender.indexOf(tempgender);
                    }
                }
            }
        } else {
            indexgender = -1;
        }
        final ArrayAdapter<Gender> adapter = new ArrayAdapter<Gender>(this, R.layout.spinner_item_text, select_gender) {
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
                String yourValue = select_gender.get(position).getGender();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_gender.get(position).getGender_local();
                }
                if (indexgender != -1 && (indexgender == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtergender.setAdapter(adapter);
        filtergender.setSelection(indexgender);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertD.dismiss();
            }
        });
        filtergender.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexgender != -1 && (indexgender == position)) {
                    getGender = getString(R.string.selectgender);
                    indexgender = -1;
                } else {
                    indexgender = position;
                    getGender = select_gender.get(indexgender).getGender();
                }
                setGender();
                adapter.notifyDataSetChanged();
            }
        });
        genderdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertD.dismiss();
            }
        });
    }

    private void setGender() {
        /*if (getGender != null && !getGender.isEmpty() && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            gender.setText(getGender);
            gender_req.setText(getGender);
            if (!languages.equalsIgnoreCase("English")) {
                setGenderLocalLang(getGender);
            }
        }*/
        if (getGender != null) {
            if (!getGender.isEmpty()) {
                if (!getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                    gender.setText(getGender);
                    gender_req.setText(getGender);
                    if (!languages.equalsIgnoreCase("English")) {
                        setGenderLocalLang(getGender);
                    }
                } else {
                    gender.setText(getString(R.string.selectgender));
                }
            }
        } else {
            gender.setText(getString(R.string.selectgender));
        }
        if (getGender.equalsIgnoreCase("Any") || getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            e_gender_req_lay.setVisibility(View.GONE);
        } else {
            e_gender_req_lay.setVisibility(View.VISIBLE);
        }
        updateMusthave();
    }

    private void setGenderLocalLang(String getGender) {
        Gender localgender = new Gender();
        localgender.setGender(getGender);
        indexgender = select_gender.indexOf(localgender);
        String GenderLocal = select_gender.get(indexgender).getGender_local();
        gender.setText(GenderLocal);
        gender_req.setText(GenderLocal);
    }

    //select job type from edit job page
    private void JobTypeAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectjobtype);
        Button jobtypedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterjobtype = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        final ArrayAdapter<JobType> adapter = new ArrayAdapter<JobType>(this, R.layout.spinner_item_text, JobTypeList) {
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
                String yourValue = JobTypeList.get(position).getJob_type_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = JobTypeList.get(position).getJob_type_name_local();
                }
                if (indexjobtype != -1 && (indexjobtype == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
       /* if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
            indexjobtype = -1;
            for (int i = 0; i < JobTypeList.size(); i++) {
                if (JobTypeList.get(i).getJob_type_name().equalsIgnoreCase(getJobType)) {
                    indexjobtype = i;
                }
            }
        }*/
        if (getJobType != null) {
            if (!getJobType.isEmpty()) {
                if (!getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                    indexjobtype = -1;
                    for (int i = 0; i < JobTypeList.size(); i++) {
                        if (JobTypeList.get(i).getJob_type_name().equalsIgnoreCase(getJobType)) {
                            indexjobtype = i;
                        }
                    }
                }
            }
        } else {
            indexjobtype = -1;
        }
        filterjobtype.setAdapter(adapter);
        filterjobtype.setSelection(indexjobtype);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobType();
                alertD.dismiss();
            }
        });
        filterjobtype.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexjobtype != -1 && (indexjobtype == position)) {
                    getJobType = getString(R.string.selectjobtype);
                    indexjobtype = -1;
                } else {
                    indexjobtype = position;
                    getJobType = JobTypeList.get(position).getJob_type_name();
                    getJobTypeID = JobTypeList.get(position).getJob_type_id();
                }
                setJobType();
                adapter.notifyDataSetChanged();
            }
        });
        jobtypedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobType();
                alertD.dismiss();
            }
        });
    }

    private void setJobType() {
       /* if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
            jobtype.setText(getJobType);
            if (!languages.equalsIgnoreCase("English")) {
                setJobtypeLocalLang(getJobType);
            }
        }*/
        if (getJobType != null) {
            if (!getJobType.isEmpty()) {
                if (!getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                    jobtype.setText(getJobType);
                    if (!languages.equalsIgnoreCase("English")) {
                        setJobtypeLocalLang(getJobType);
                    }
                } else {
                    jobtype.setText(getString(R.string.selectjobtype));
                }
            }
        } else {
            jobtype.setText(getString(R.string.selectjobtype));
        }
    }

    private void setJobtypeLocalLang(String getJobType) {
        for (int j = 0; j < JobTypeList.size(); j++) {
            if (JobTypeList.get(j).getJob_type_name().equals(getJobType)) {
                JobTypeLocal = JobTypeList.get(j).getJob_type_name_local();
                jobtype.setText(JobTypeLocal);
            }
        }
    }

    //select salary from edit job page
    private void SalaryAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectsalary);
        Button salarydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtersalary = (ListView) emppromptView.findViewById(R.id.spinnerlist);
       /* if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            indexsalary = -1;
            for (int i = 0; i < SalaryList.size(); i++) {
                if (SalaryList.get(i).equals(getSalary)) {
                    indexsalary = i;
                }
            }
        }*/
        if (getSalary != null) {
            if (!getSalary.isEmpty()) {
                if (!getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                    indexsalary = -1;
                    for (int i = 0; i < SalaryList.size(); i++) {
                        if (SalaryList.get(i).equals(getSalary)) {
                            indexsalary = i;
                        }
                    }
                }
            }
        } else {
            indexsalary = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, SalaryList) {
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
                String yourValue = SalaryList.get(position);
                if (indexsalary != -1 && (indexsalary == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtersalary.setAdapter(adapter);
        filtersalary.setSelection(indexsalary);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSalary();
                alertD.dismiss();
            }
        });
        filtersalary.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexsalary != -1 && (indexsalary == position)) {
                    getSalary = getString(R.string.selectsalary);
                    indexsalary = -1;
                } else {
                    indexsalary = position;
                    getSalary = SalaryList.get(position);
                }
                setSalary();
                adapter.notifyDataSetChanged();
            }
        });
        salarydone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSalary();
                alertD.dismiss();
            }
        });
    }

    private void setSalary() {
        /*if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            salary.setText(getSalary);
        }*/
        if (getSalary != null) {
            if (!getSalary.isEmpty()) {
                if (!getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                    salary.setText(getSalary);
                } else {
                    salary.setText(R.string.selectsalary);
                }
            }
        } else {
            salary.setText(R.string.selectsalary);
        }
    }

    //select quali from edit job page
    private void QualificationAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectquali);
        Button qualidone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        /*if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            indexqual = -1;
            for (int i = 0; i < select_qualification.size(); i++) {
                if (select_qualification.get(i).getQuali_name().equals(getQuali)) {
                    indexqual = i;
                }
            }
        }*/
        if (getQuali != null) {
            if (!getQuali.isEmpty()) {
                if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                    indexqual = -1;
                    for (int i = 0; i < select_qualification.size(); i++) {
                        if (select_qualification.get(i).getQuali_name().equals(getQuali)) {
                            indexqual = i;
                        }
                    }
                }
            }
        } else {
            indexqual = -1;
        }
        final SpinnerAdapter adapter = new SpinnerAdapter(Edit_Job.this, R.layout.spinner_item_text, select_qualification) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = Edit_Job.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = select_qualification.get(position).getQuali_name();
                if (indexqual != -1 && (indexqual == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterquali.setAdapter(adapter);
        filterquali.setSelection(indexqual);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertD.dismiss();
            }
        });
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int pos, long arg3) {
                if (indexqual != -1 && (indexqual == pos)) {
                    getQuali = getString(R.string.selectquali);
                    indexqual = -1;
                    e_emp_pj_qlSp_layout.setVisibility(View.GONE);
                } else {
                    indexqual = pos;
                    getQuali = select_qualification.get(pos).getQuali_name();
                    if (getQuali.equals(select_qualification.get(pos).getQuali_name())) {
                        getQuali_ID = select_qualification.get(pos).getId();
                        specificationList = new ArrayList<>();
                        if (select_qualification.get(pos).getSpecialization() == null || select_qualification.get(pos).getSpecialization().isEmpty() || select_qualification.get(pos)
                                .getSpecialization().size() == 0) {
                            e_emp_pj_qlSp_layout.setVisibility(View.GONE);
                            getSpecialization = null;
                        } else {
                            e_emp_pj_qlSp_layout.setVisibility(View.VISIBLE);
                            getSpecialization = getString(R.string.selectspec);
                            e_emp_pj_qlSp.setText(getSpecialization);
                            // specificationList = new ArrayList<>();
                            for (int i = 0; i < select_qualification.size(); i++) {
                                if (select_qualification.get(i).getId().equals(getQuali_ID)) {
                                    specificationList.addAll(select_qualification.get(i)
                                            .getSpecialization());
                                }
                            }
                        }
                    }
                    /*if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                        mini_quali.setText(getQuali);
                    }*/
                    if (getQuali != null) {
                        if (!getQuali.isEmpty()) {
                            if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                                mini_quali.setText(getQuali);
                            }
                        }
                    } else {
                        mini_quali.setText(R.string.selectquali);
                    }
                    if (getQuali.equalsIgnoreCase("Not Needed") || getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                        e_quali_req_lay.setVisibility(View.GONE);
                    } else {
                        e_quali_req_lay.setVisibility(View.VISIBLE);
                        miniquali_req.setText(getQuali);
                    }
                    updateMusthave();
                }
                setQualification();
                adapter.notifyDataSetChanged();
            }
        });
        qualidone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertD.dismiss();
            }
        });
    }

    private void setQualification() {
        /*if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            mini_quali.setText(getQuali);
        }*/
        if (getQuali != null) {
            if (!getQuali.isEmpty()) {
                if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                    mini_quali.setText(getQuali);
                } else {
                    mini_quali.setText(R.string.selectquali);
                }
            }
        } else {
            mini_quali.setText(R.string.selectquali);
        }
    }

    //select specialization from edit job page
    private void SpecializationAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectspec);
        Button specialidone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSpecialization != null && !getSpecialization.isEmpty() && !getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
            indexspec = -1;
            for (int i = 0; i < specificationList.size(); i++) {
                if (specificationList.get(i).getSpeciali_name().equals(getSpecialization)) {
                    indexspec = i;
                }
            }
        } else {
            indexspec = -1;
        }
        final SpeciAdapter adapter = new SpeciAdapter(Edit_Job.this, R.layout.spinner_item_text, specificationList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = Edit_Job.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = specificationList.get(position).getSpeciali_name();
                if (indexspec != -1 && (indexspec == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterquali.setAdapter(adapter);
        filterquali.setSelection(indexspec);
        alertD.setContentView(emppromptView);
        alertD.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpecialization();
                alertD.dismiss();
            }
        });
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexspec != -1 && (indexspec == position)) {
                    getSpecialization = getString(R.string.selectspec);
                    indexspec = -1;
                } else {
                    indexspec = position;
                    getSpecialization = specificationList.get(position).getSpeciali_name();
                }
                setSpecialization();
                adapter.notifyDataSetChanged();
            }
        });
        specialidone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpecialization();
                alertD.dismiss();
            }
        });
    }

    private void setSpecialization() {
        /*if (getSpecialization != null && !getSpecialization.isEmpty() && !getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
            e_emp_pj_qlSp.setText(getSpecialization);
        }*/
        if (getSpecialization != null) {
            if (!getSpecialization.isEmpty()) {
                if (!getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
                    e_emp_pj_qlSp.setText(getSpecialization);
                } else {
                    e_emp_pj_qlSp.setText(R.string.selectspec);
                }
            }
        } else {
            e_emp_pj_qlSp.setText(R.string.selectspec);
        }
    }

    //location selection(state -single,cities - multiple
    private void LocationAlert() {
        final Dialog alertD = new Dialog(Edit_Job.this,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Edit_Job.this, R.layout.location_filter_popup, null);
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
        autocity.setThreshold(1);
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
        if (getLocation != null && !getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            location.setText(getLocation);
            List<String> locationlist = Arrays.asList(getLocation.split(","));
            for (int i = 0; i < locationlist.size(); i++) {
                if (!(selectedLocationList.contains(locationlist.get(i)))) {
                    selectedLocationList.add(locationlist.get(i));
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            location.setText(R.string.select);
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
                getState = locationList.get(position).getState_name();
                getStateID = locationList.get(position).getId();
                autocity.setFocusableInTouchMode(false);
                locfilt_citysearch.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                getCityListAdapter();
            }
        });
        //city - autocomplete textview
        if (locationCityList.size() > 0) {
            final CityAdapter loccityAdapter = new CityAdapter(Edit_Job.this, R.layout.spinner_item_text, locationCityList) {
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
                    String yourValue = locationCityList.get(position).getCiti_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        yourValue = locationCityList.get(position).getCity_name_local();
                    }
                    textView.setText(yourValue);
                    return textView;
                }
            };
            autocity.setAdapter(loccityAdapter);
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
        alertD.setContentView(emppromptView);
        alertD.show();
        //location - reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getState = null;
                getStateID = null;
                if (selectedLocationList.size() > 0) {
                    selectedLocationList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedLocationLocalList.clear();
                    }
                }
                isCheckedLocation = new boolean[CityList.size()];
                Arrays.fill(isCheckedLocation, false);
                getLocation = null;
                getLocation = "";
                location.setText(R.string.select);
                alertD.dismiss();
                LocationAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDone();
                alertD.dismiss();
            }
        });
        //location - done
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                locationDone();
                alertD.dismiss();
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
                    alertD.dismiss();
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
                    alertD.dismiss();
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
        List<String> locationlist = Arrays.asList(getLocation.split(","));
        for (int i = 0; i < locationlist.size(); i++) {
            City cityname = new City();
            cityname.setCiti_name(locationlist.get(i));
            int j = MainlocationCityList.indexOf(cityname);
            if (j != -1) {
                selectedLocationLocalList.add(MainlocationCityList.get(j).getCity_name_local());
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
        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocation = getLocation.replace(", ", ",");
        if (getLocation != null && !getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            location.setText(getLocation);
            getStatefromlocation(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
                setLocationLocalLang(getLocation);
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            location.setText(R.string.select);
        }
    }

    private void setLocationLocalLang(String getLocation) {
        selectedLocationLocalList = new ArrayList<>();
        List<String> locationlistt = Arrays.asList(getLocation.split(","));
        for (int i = 0; i < locationlistt.size(); i++) {
            for (int j = 0; j < locationCityList.size(); j++) {
                if (locationCityList.get(j).getCiti_name().equals(locationlistt.get(i))) {
                    selectedLocationLocalList.add(locationCityList.get(j).getCity_name_local());
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

    private void getStatefromlocation(String getLocation) {
        selectedStateList = new ArrayList<>();
        List<String> locationlistt = Arrays.asList(getLocation.split(","));
        for (int i = 0; i < locationlistt.size(); i++) {
            for (int j = 0; j < locationCityList.size(); j++) {
                if (locationCityList.get(j).getCiti_name().equals(locationlistt.get(i))) {
                    if (!selectedStateList.contains(locationCityList.get(j).getCiti_county_id())) {
                        selectedStateList.add(locationCityList.get(j).getCiti_county_id());
                    }
                }
            }
        }
        String[] getStateaded = selectedStateList.toArray(new String[selectedStateList.size
                ()]);
        String getstatearray = Arrays.toString(getStateaded);
        String getStatename = getstatearray.substring(1, getstatearray.length() - 1);
        getStateName = getStatename.replace(", ", ",");
    }

    private void getCityListAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocation = getLocation.replace(", ", ",");
        CityList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getId().equals(getStateID)) {
                CityList = locationList.get(i).getCities();
            }
        }
        locstate_view.setVisibility(View.GONE);
        city_view.setVisibility(View.VISIBLE);
        cl_cityheader.setText(getState);
        locfilt_citysearch.setText(autocity.getText().toString());
        //get the city list depends upon selected state
        cadapter = new ArrayAdapter<City>(Edit_Job.this, R.layout.filter_listrow, CityList) {
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
                textView.setText(CityList.get(position).getCiti_name());
                if (!languages.equalsIgnoreCase("English")) {
                    textView.setText(CityList.get(position).getCity_name_local());
                }
                return textView;
            }
        };
        filtercity.setAdapter(cadapter);
        getCityAdapter();
        if (getLocation != null && !getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            location.setText(getLocation);
            List<String> locationlist = Arrays.asList(getLocation.split(","));
            isCheckedLocation = new boolean[CityList.size()];
            indexcity = -1;
            for (int i = 0; i < locationlist.size(); i++) {
                for (int j = 0; j < CityList.size(); j++) {
                    if (!(selectedLocationList.contains(locationlist.get(i)))) {
                        selectedLocationList.add(locationlist.get(i));
                    }
                    if (CityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                        indexcity = j;
                        isCheckedLocation[indexcity] = true;
                        filtercity.setItemChecked(indexcity, true);
                    }
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[CityList.size()];
            Arrays.fill(isCheckedLocation, false);
        }
        //city list item check|uncheck
        filtercity.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
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
                getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
                getLocation = getLocation.replace(", ", ",");
                location.setText(getLocation);
                if (getLocation != null && !getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(getLocation);
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
        });
    }

    private void getSelectedCities() {
        if (selectedLocationList.size() > 0) {
            selectedLocationList.clear();
            isCheckedLocation = new boolean[CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            getLocation = null;
            if (getFrom.equalsIgnoreCase("State")) {
                setStateAdapter();
            } else {
                getCityListAdapter();
            }
            location.setText(R.string.select);
        }
    }

    private void getCityAdapter() {
        if (CityList.size() > 0) {
            final ArrayList<City> getcityforstate = new ArrayList<>();
            getcityforstate.addAll(CityList);
            loccityAdapter = new CityAdapter(Edit_Job.this, R.layout.spinner_item_text, getcityforstate) {
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
                        for (int j = 0; j < CityList.size(); j++) {
                            if (CityList.get(j).getCiti_name().equals(loccityAdapter.getItem(i)
                                    .getCiti_name())) {
                                indexcity = j;
                                isCheckedLocation[indexcity] = true;
                                filtercity.setItemChecked(indexcity, true);
                            }
                        }
                        String[] getLocationaded = selectedLocationList.toArray(new
                                String[selectedLocationList.size()]);
                        String getlocationarray = Arrays.toString(getLocationaded);
                        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
                        getLocation = getLocation.replace(", ", ",");
                        stateselectedloc.setVisibility(View.VISIBLE);
                        stateselectedloc.setText(getLocation);
                        cityselectedloc.setVisibility(View.VISIBLE);
                        cityselectedloc.setText(getLocation);
                        location.setText(getLocation);
                        if (!languages.equalsIgnoreCase("English")) {
                            setSelectedLocation(stateselectedloc, cityselectedloc);
                        }
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

    private void setStateAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocation = getLocation.replace(", ", ",");
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(Edit_Job.this, R.layout.skillcategory_row,
                locationList) {
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
                textView.setText(locationList.get(position).getState_name());
                if (getLocation != null && !getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
                    location.setText(getLocation);
                    List<String> locationlist = Arrays.asList(getLocation.split(","));
                    getStateID = locationList.get(position).getId();
                    CityList = locationList.get(position).getCities();
                    ArrayList<String> selectedcitylist = new ArrayList<>();
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < CityList.size(); j++) {
                            if (CityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                                String getCityID = CityList.get(j).getCiti_county_id();
                                if (languages.equalsIgnoreCase("English")) {
                                    selectedcitylist.add(CityList.get(j).getCiti_name());
                                } else {
                                    selectedcitylist.add(CityList.get(j).getCity_name_local());
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
                    location.setText(R.string.select);
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

    //update the job details for this job id
    private class getPostJob extends AsyncTask<String, String, String> {
        String postjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Edit_Job.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("action", "AllJobUpdate");
            paramsadd.addFormDataPart("job_id", GlobalData.ejobid);
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("job_title", getJobTitle);
            paramsadd.addFormDataPart("location", getLocation);
            paramsadd.addFormDataPart("location_state",getStateName);
            paramsadd.addFormDataPart("minimum_qual", getQuali);
            paramsadd.addFormDataPart("qualification_id", getQuali_ID);
            if (getSpecialization != null) {
                paramsadd.addFormDataPart("specilization", getSpecialization);
            }
            paramsadd.addFormDataPart("experience", getExp);
            paramsadd.addFormDataPart("industry", getIndustry);
            paramsadd.addFormDataPart("role", getJobRole);
            paramsadd.addFormDataPart("jobgender", getGender);
            paramsadd.addFormDataPart("job_offer_type", getJobTypeID);
            paramsadd.addFormDataPart("salary", getSalary);
            paramsadd.addFormDataPart("showphoneflag", getPhoneStatus);
            paramsadd.addFormDataPart("job_description", getJobdesc);
            paramsadd.addFormDataPart("Skills", getSkill);
            paramsadd.addFormDataPart("minimum_qualification_req", getQualiReq);
            paramsadd.addFormDataPart("gender_req", getGenderReq);
            paramsadd.addFormDataPart("skills_req", getSkillReq);
            paramsadd.addFormDataPart("experience_req", getExperienceReq);
            paramsadd.addFormDataPart("clientname", getClientname);
            paramsadd.addFormDataPart("req_skillname", getRequiredSkill);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update" +
                    ".php").post(requestBody)
                    .build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                postjobresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (postjobresponse != null && !postjobresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(postjobresponse);
                    int status_code = responseObj.getInt("status_code");
                    String getJobsAddStatus = responseObj.getString("message");
                    if (status_code == 1) {
                        //if updation success redirect to the corresponding page
                        if (!getJobDescInit.isEmpty() || !getJobTitleInit.isEmpty()) {
                            if (!getJobTitle.equalsIgnoreCase(getJobTitleInit) || !getJobdesc.equalsIgnoreCase(getJobDescInit)) {
                                new CustomAlertDialog().isDisplayMessage(Edit_Job.this, getString(R.string.postjobsuccess));
                            } else {
                                Toast.makeText(getBaseContext(), getJobsAddStatus, Toast.LENGTH_SHORT)
                                        .show();
                                if (GlobalData.frompostjob.equalsIgnoreCase("PostJob")) {
                                    startActivity(new Intent(Edit_Job.this, PostJobFinalSubmit.class));
                                } else {
                                    startActivity(new Intent(Edit_Job.this, JobsPosted.class));
                                }
                            }
                        } else {
                            Toast.makeText(getBaseContext(), getJobsAddStatus, Toast.LENGTH_SHORT)
                                    .show();
                            if (GlobalData.frompostjob.equalsIgnoreCase("PostJob")) {
                                startActivity(new Intent(Edit_Job.this, PostJobFinalSubmit.class));
                            } else {
                                startActivity(new Intent(Edit_Job.this, JobsPosted.class));
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getJobsAddStatus, Toast.LENGTH_SHORT)
                                .show();
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

