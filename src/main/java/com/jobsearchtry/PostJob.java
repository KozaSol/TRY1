package com.jobsearchtry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
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
import com.jobsearchtry.utils.DisplayToastMessage;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.Experience;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.JobType;
import com.jobsearchtry.wrapper.Qualification;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Specialization;

import org.json.JSONArray;
import org.json.JSONException;
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

public class PostJob extends Activity {
    private EditText jobtitle, emp_pj_clientname;
    private Button mini_quali, specialization, expneed, jobrole, jobtype, salary, industry,
            gender;
    private ToggleButton showphoneno;
    private LinearLayout emp_pj_jobrole_lay, emp_pj_minql_lay, emp_pj_exp_lay, emp_pj_jobtype_lay,
            emp_pj_sly_lay, pj_location_lay,
            emp_pj_industry_lay, locstate_view, city_view, emp_pj_qlSp_lay;
    private String getJobTitle, getLocation, getQuali, getExp, getJobRole,
            getJobType, getSalary, getIndustry, getPhoneStatus = "Yes", languages, RoleLocal,
            getGender, getJobTypeID = "0", selectedQuali_ID = "0", getSpecialization,
            IndustryLocal, ExperienceLocal, GenderLocal, JobTypeLocal,getStateName,
            getState, getStateID, getEmpIndustry, getClientname = null, getFrom = "State";
    private Button locationlist;
    private ProgressDialog pg;
    private ArrayList<String> selectedLocationLocalList = new ArrayList<>(),
            SalaryList = new ArrayList<>(), selectedLocationList = new ArrayList<>(),
            selectedStateList = new ArrayList<>();
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private SpeciAdapter spadapter;
    private RoleAdapter roleAdapter;
    private ArrayList<JobType> JobTypeList = null;
    private ArrayList<City> MainlocationCityList = new ArrayList<>(), locationCityList = new ArrayList<>();
    private ArrayList<Experience> ExpList = new ArrayList<>();
    private ArrayList<Qualification> select_qualification = new ArrayList<>();
    private ArrayList<Industry> industriesList = new ArrayList<>();
    private ArrayList<Role> select_role = new ArrayList<>();
    private ArrayList<City> CityList = new ArrayList<>();
    private ArrayList<FilterLocation> locationList = new ArrayList<>();
    private ArrayList<Specialization> specificationList = new ArrayList<>();
    private ArrayList<Gender> select_gender = null;
    private ArrayAdapter<Gender> genderadapter;
    private ArrayAdapter<JobType> jobTypeAdapter;
    private OkHttpClient client = null;
    private boolean[] isCheckedLocation;
    private int indexcity = -1, GenderalIndustryId, indexindustry = -1, indexqual = -1, indexspec = -1, indexrole = -1, indexexp = -1, indexgender = -1, indexjobtype = -1,
            indexsalary = -1;
    private ListView filterstate, filtercity;
    private AutoCompleteTextView locfilt_citysearch, autocity;
    private TextView stateselectedloc, cityselectedloc, cl_cityheader;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postjob);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        getQuali = getString(R.string.selectquali);
        getExp = getString(R.string.selectexp);
        getJobRole = getString(R.string.selectrole);
        getJobType = getString(R.string.selectjobtype);
        getSalary = getString(R.string.selectsalary);
        getIndustry = getString(R.string.selectindustry);
        getGender = getString(R.string.selectgender);
        getSpecialization = getString(R.string.selectspec);
        if (new UtilService().isNetworkAvailable(PostJob.this)) {
            new getQualification().execute();
        } else {
            Toast.makeText(PostJob.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton pj_h = (ImageButton) findViewById(R.id.js_r_h);
        pj_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostJob.this, EmployerDashboard.class));
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        pj_location_lay = (LinearLayout) findViewById(R.id.pj_location_lay);
        locationlist = (Button) findViewById(R.id.emp_pj_location);
        if (getLocation != null && !getLocation.isEmpty()) {
            locationlist.setText(getLocation);
        } else {
            locationlist.setText(R.string.select);
        }
        pj_location_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationList.size() > 0) {
                    LocationAlert();
                } else {
                    Toast.makeText(PostJob.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
        jobtitle = (EditText) findViewById(R.id.emp_pj_jobtitle);
        emp_pj_clientname = (EditText) findViewById(R.id.emp_pj_clientname);
        mini_quali = (Button) findViewById(R.id.emp_pj_minql);
        emp_pj_minql_lay = (LinearLayout) findViewById(R.id.emp_pj_minql_lay);
        emp_pj_minql_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_qualification.size() > 0) {
                    QualificationAlert();
                }
            }
        });
        expneed = (Button) findViewById(R.id.emp_pj_exp);
        emp_pj_exp_lay = (LinearLayout) findViewById(R.id.emp_pj_exp_lay);
        emp_pj_exp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExpList.size() > 0) {
                    ExperienceAlert();
                }
            }
        });
        jobrole = (Button) findViewById(R.id.emp_pj_jobrole);
        emp_pj_jobrole_lay = (LinearLayout) findViewById(R.id.emp_pj_jobrole_lay);
        emp_pj_jobrole_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_role.size() > 0) {
                    RoleAlert();
                }
            }
        });
        LinearLayout emp_pj_Gender_lay = (LinearLayout) findViewById(R.id
                .emp_pj_Gender_lay);
        emp_pj_Gender_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_gender != null && select_gender.size() > 0) {
                    GenderAlert();
                }
            }
        });
        emp_pj_qlSp_lay = (LinearLayout) findViewById(R.id
                .emp_pj_qlSp_lay);
        specialization = (Button) findViewById(R.id.emp_pj_qlSp);
        emp_pj_qlSp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (specificationList.size() > 0) {
                    SpecializationAlert();
                }
            }
        });
        gender = (Button) findViewById(R.id.emp_pj_gender);
        emp_pj_Gender_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GenderAlert();
            }
        });
        jobtype = (Button) findViewById(R.id.emp_pj_jobtype);
        emp_pj_jobtype_lay = (LinearLayout) findViewById(R.id.emp_pj_jobtype_lay);
        emp_pj_jobtype_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JobTypeList != null && JobTypeList.size() > 0) {
                    JobTypeAlert();
                }
            }
        });
        salary = (Button) findViewById(R.id.emp_pj_sly);
        emp_pj_sly_lay = (LinearLayout) findViewById(R.id.emp_pj_sly_lay);
        emp_pj_sly_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SalaryList.size() > 0) {
                    SalaryAlert();
                }
            }
        });
        showphoneno = (ToggleButton) findViewById(R.id.emp_pj_showphoneno);
        showphoneno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    CompoundButton arg0, boolean isChecked) {
                final int valuemax = (int) getResources().getDimension(R.dimen.buttonHeightToSmall);
                final int valuemin = (int) getResources().getDimension(R.dimen.margintop);
                if (showphoneno.isChecked()) {
                    getPhoneStatus = "Yes";
                    showphoneno.setPadding(valuemin, 0, valuemax, 0);
                    showphoneno.setTextOff("No");
                    showphoneno.setTextOn("Yes");
                    new DisplayToastMessage().isToastMessage(PostJob.this, getString(R.string.postajobcall));
                } else {
                    getPhoneStatus = "No";
                    showphoneno.setTextOff("No");
                    showphoneno.setTextOn("Yes");
                    showphoneno.setPadding(valuemax, 0, valuemin, 0);
                    new DisplayToastMessage().isToastMessage(PostJob.this, getString(R.string.postajobcannotcall));
                }
            }
        });
        industry = (Button) findViewById(R.id.emp_pj_industry);
        emp_pj_industry_lay = (LinearLayout) findViewById(R.id.emp_pj_industry_lay);
        emp_pj_industry_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (industriesList.size() > 0) {
                    IndustryAlert();
                }
            }
        });
        Button submit = (Button) findViewById(R.id.tryEmpPostJob);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPostJob();
                if (verifyPostJob()) {
                    new getPostJob().execute();
                }
            }

            private boolean verifyPostJob() {
                getJobTitle = jobtitle.getText().toString();
                if (getJobTitle.length() < 3) {
                    Toast.makeText(PostJob.this, getString(R.string.jobnamevalidation),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                    Toast.makeText(PostJob.this, getString(R.string.industryvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    Toast.makeText(PostJob.this, getString(R.string.pleaseselecttherole), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (null == getLocation || getLocation.length() == 0) {
                    Toast.makeText(PostJob.this, getString(R.string.locationvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                String[] separated = getLocation.split(",");
                if (separated.length > 10) {
                    Toast.makeText(PostJob.this, getString(R.string.postjoblocationlimit),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getEmpIndustry.equalsIgnoreCase("HR / Manpower Agency")) {
                    getClientname = emp_pj_clientname.getText().toString();
                    if (getClientname.length() < 3) {
                        Toast.makeText(PostJob.this, getString(R.string.clientnamevalidation),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                if (selectedQuali_ID.equalsIgnoreCase("0") || getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                    Toast.makeText(PostJob.this, getString(R.string.pleaseselectqualification), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (specificationList.size() > 0 && getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
                    Toast.makeText(PostJob.this, getString(R.string.pleaseselectthespeci), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
                    Toast.makeText(PostJob.this, getString(R.string.expvalidation), Toast
                            .LENGTH_LONG).show();
                    return false;
                }
                if (getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                    Toast.makeText(PostJob.this, getString(R.string.pleasethegender), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getJobTypeID.equalsIgnoreCase("0") || getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                    Toast.makeText(PostJob.this, getString(R.string.jobtypevalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                    Toast.makeText(PostJob.this, getString(R.string.salaryvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (!(new UtilService().isNetworkAvailable(PostJob.this))) {
                    Toast.makeText(PostJob.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
    }

    private void onbackclick() {
        if (jobtitle.getText().toString().length() != 0 ||
                !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry)) ||
                !getJobRole.equalsIgnoreCase(getString(R.string.selectrole)) ||
                getLocation.length() != 0 || !getQuali.equalsIgnoreCase(getString(R.string.selectquali)) ||
                !getExp.equalsIgnoreCase(getString(R.string.selectexp)) ||
                !getGender.equalsIgnoreCase(getString(R.string.selectgender)) ||
                !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype)) ||
                !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            new BackAlertDialog().isBackDialog(PostJob.this);
        } else {
            finish();
            GlobalData.loginfrom = null;
            startActivity(new Intent(PostJob.this, EmployerDashboard.class));
        }
    }

    //select quali from posting job page
    private void QualificationAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectquali);
        Button qualidone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            indexqual = -1;
            for (int i = 0; i < select_qualification.size(); i++) {
                if (select_qualification.get(i).getQuali_name().equals(getQuali)) {
                    indexqual = i;
                }
            }
        } else {
            indexqual = -1;
        }
        final SpinnerAdapter adapter = new SpinnerAdapter(PostJob.this, R.layout.spinner_item_text, select_qualification) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = PostJob.this;
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int pos, long arg3) {
                if (indexqual != -1 && (indexqual == pos)) {
                    getQuali = getString(R.string.selectquali);
                    indexqual = -1;
                    emp_pj_qlSp_lay.setVisibility(View.GONE);
                } else {
                    indexqual = pos;
                    getQuali = select_qualification.get(pos).getQuali_name();
                    if (getQuali.equals(select_qualification.get(pos).getQuali_name())) {
                        selectedQuali_ID = select_qualification.get(pos).getId();
                        specificationList = new ArrayList<>();
                        if (select_qualification.get(pos).getSpecialization() == null || select_qualification.get(pos).getSpecialization().isEmpty() || select_qualification.get(pos)
                                .getSpecialization().size() == 0) {
                            emp_pj_qlSp_lay.setVisibility(View.GONE);
                        } else {
                            emp_pj_qlSp_lay.setVisibility(View.VISIBLE);
                            getSpecialization = getString(R.string.selectspec);
                            specialization.setText(getSpecialization);
                            // specificationList = new ArrayList<>();
                            for (int i = 0; i < select_qualification.size(); i++) {
                                if (select_qualification.get(i).getId().equals(selectedQuali_ID)) {
                                    specificationList.addAll(select_qualification.get(i)
                                            .getSpecialization());
                                }
                            }
                            emp_pj_qlSp_lay.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (specificationList.size() > 0) {
                                        SpecializationAlert();
                                    }
                                }
                            });
                        }
                    }
                }
                setQualification();
                adapter.notifyDataSetChanged();
            }
        });
        qualidone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
    }

    private void setQualification() {
        if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            mini_quali.setText(getQuali);
        } else {
            mini_quali.setText(R.string.selectquali);
        }
    }
    //select specialization from job posting page

    private void SpecializationAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
        final SpeciAdapter adapter = new SpeciAdapter(PostJob.this, R.layout.spinner_item_text, specificationList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = PostJob.this;
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpecialization();
                alertDialog.dismiss();
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
                alertDialog.dismiss();
            }
        });
    }

    private void setSpecialization() {
        if (getSpecialization != null && !getSpecialization.isEmpty() && !getSpecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
            specialization.setText(getSpecialization);
        } else {
            specialization.setText(R.string.selectspec);
        }
    }

    //select industry from posting job page
    private void IndustryAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
        final IndustryAdapter adapter = new IndustryAdapter(PostJob.this, industriesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = PostJob.this;
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
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
                        emp_pj_jobrole_lay.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (select_role.size() > 0) {
                                    RoleAlert();
                                }
                            }
                        });
                    }
                }
                setIndustry();
                adapter.notifyDataSetChanged();
            }
        });
        industrydone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
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

    //select role from posting job page
    private void RoleAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            indexrole = -1;
            for (int i = 0; i < select_role.size(); i++) {
                if (select_role.get(i).getRole_name().equals(getJobRole)) {
                    indexrole = i;
                }
            }
        } else {
            indexrole = -1;
        }
        final RoleAdapter adapter = new RoleAdapter(PostJob.this, select_role) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = PostJob.this;
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
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
                }
                setRole();
                adapter.notifyDataSetChanged();
            }
        });
        roledone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
            }
        });
    }

    private void setRole() {
        if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            jobrole.setText(getJobRole);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang(getJobRole);
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

    //select experience from posting job page
    private void ExperienceAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectexp);
        Button experiencedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            indexexp = -1;
            for (int i = 0; i < ExpList.size(); i++) {
                if (ExpList.get(i).getExperience_profile_name().equals(getExp)) {
                    indexexp = i;
                    filterexp.setItemChecked(indexexp, true);
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExperience();
                alertDialog.dismiss();
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
                alertDialog.dismiss();
            }
        });
    }

    private void setExperience() {
        if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            expneed.setText(getExp);
            if (!languages.equalsIgnoreCase("English")) {
                setExperienceLocalLang();
            }
        } else {
            expneed.setText(R.string.selectexp);
        }
    }

    private void setExperienceLocalLang() {
        for (int j = 0; j < ExpList.size(); j++) {
            if (ExpList.get(j).getExperience_profile_name().equals(getExp)) {
                ExperienceLocal = ExpList.get(j).getExperience_profile_name_local();
                expneed.setText(ExperienceLocal);
            }
        }
    }

    //select gender from posting job page
    private void GenderAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectgender);
        Button genderdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtergender = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getGender != null && !getGender.isEmpty() && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            indexgender = -1;
            if (select_gender != null && select_gender.size() > 0) {
                Gender tempgender = new Gender();
                tempgender.setGender(getGender);
                indexgender = select_gender.indexOf(tempgender);
            }
        } else {
            indexgender = -1;
        }
        genderadapter = new ArrayAdapter<Gender>(this, R.layout.spinner_item_text, select_gender) {
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
        filtergender.setAdapter(genderadapter);
        filtergender.setSelection(indexgender);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertDialog.dismiss();
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
                genderadapter.notifyDataSetChanged();
            }
        });
        genderdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertDialog.dismiss();
            }
        });
    }

    private void setGender() {
        if (getGender != null && !getGender.isEmpty() && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            gender.setText(getGender);
            if (!languages.equalsIgnoreCase("English")) {
                setGenderLocalLang();
            }
        } else {
            gender.setText(getString(R.string.selectgender));
        }
    }

    private void setGenderLocalLang() {
        Gender localgender = new Gender();
        localgender.setGender(getGender);
        indexgender = select_gender.indexOf(localgender);
        String GenderLocal = select_gender.get(indexgender).getGender_local();
        gender.setText(GenderLocal);
    }

    //select job type from posting job page
    private void JobTypeAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectjobtype);
        Button jobtypedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterjobtype = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        jobTypeAdapter = new ArrayAdapter<JobType>(this, R.layout.spinner_item_text, JobTypeList) {
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
        if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
            indexjobtype = -1;
            for (int i = 0; i < JobTypeList.size(); i++) {
                if (JobTypeList.get(i).getJob_type_name().equals(getJobType)) {
                    indexjobtype = i;
                    filterjobtype.setItemChecked(indexjobtype, true);
                }
            }
        } else {
            indexjobtype = -1;
        }
        filterjobtype.setAdapter(jobTypeAdapter);
        filterjobtype.setSelection(indexjobtype);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobType();
                alertDialog.dismiss();
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
                    //getJobTypeID = JobTypeIdList.get(position);
                }
                setJobType();
                jobTypeAdapter.notifyDataSetChanged();
            }
        });
        jobtypedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobType();
                alertDialog.dismiss();
            }
        });
    }

    private void setJobType() {
        if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
            jobtype.setText(getJobType);
            if (!languages.equalsIgnoreCase("English")) {
                setJobtypeLocalLang();
            }
        } else {
            jobtype.setText(getString(R.string.selectjobtype));
        }
    }

    private void setJobtypeLocalLang() {
        for (int j = 0; j < JobTypeList.size(); j++) {
            if (JobTypeList.get(j).getJob_type_name().equals(getJobType)) {
                JobTypeLocal = JobTypeList.get(j).getJob_type_name_local();
                jobtype.setText(JobTypeLocal);
            }
        }
    }

    //select salary from posting job page
    private void SalaryAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectsalary);
        Button salarydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtersalary = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            indexsalary = -1;
            for (int i = 0; i < SalaryList.size(); i++) {
                if (SalaryList.get(i).equals(getSalary)) {
                    indexsalary = i;
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
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSalary();
                alertDialog.dismiss();
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
                alertDialog.dismiss();
            }
        });
    }

    private void setSalary() {
        if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            salary.setText(getSalary);
        } else {
            salary.setText(R.string.selectsalary);
        }
    }

    private void LocationAlert() {
        View emppromptView = View.inflate(PostJob.this, R.layout
                .location_filter_popup, null);
        final Dialog alertDialog = new Dialog(PostJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
            locationlist.setText(getLocation);
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
            final CityAdapter loccityAdapter = new CityAdapter(PostJob.this, R.layout.spinner_item_text, locationCityList) {
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
                locationlist.setText(R.string.select);
                alertDialog.dismiss();
                LocationAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDone();
                alertDialog.dismiss();
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
            locationlist.setText(getLocation);
            getStatefromlocation(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
                setLocationLocalLang(getLocation);
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            locationlist.setText(R.string.select);
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
        locationlist.setText(getLocationTamil);
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
        cadapter = new ArrayAdapter<City>(PostJob.this, R.layout.filter_listrow, CityList) {
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
                    textView.setText(GlobalData.CityList.get(position).getCity_name_local());
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
            locationlist.setText(getLocation);
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
                locationlist.setText(getLocation);
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
            locationlist.setText(R.string.select);
        }
    }

    private void getCityAdapter() {
        if (CityList.size() > 0) {
            final ArrayList<City> getcityforstate = new ArrayList<>();
            getcityforstate.addAll(CityList);
            loccityAdapter = new CityAdapter(PostJob.this, R.layout.spinner_item_text, getcityforstate) {
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
                        locationlist.setText(getLocation);
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
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(PostJob.this, R
                .layout.skillcategory_row, locationList) {
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
                    locationlist.setText(getLocation);
                    List<String> locationlist = Arrays.asList(getLocation.split(","));
                    getStateID = locationList.get(position).getId();
                    CityList = locationList.get(position).getCities();
                    ArrayList<String> selectedcitylist = new ArrayList<>();
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < CityList.size(); j++) {
                            if (CityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                                String getCityID = CityList.get(j).getCiti_county_id();
                                if (getCityID.equalsIgnoreCase(getStateID)) {
                                    if (languages.equalsIgnoreCase("English")) {
                                        selectedcitylist.add(CityList.get(j).getCiti_name());
                                    } else {
                                        selectedcitylist.add(CityList.get(j).getCity_name_local());
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

    class getQualification extends AsyncTask<String, String, String> {
        String postjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostJob.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "postjob_list.php").post
                    (requestBody).build();
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
            try {
                JSONObject json = new JSONObject(postjobresponse);
                getIndustry = json.getString("employer_industry_name");
                getLocation = json.getString("location");
                GenderalIndustryId = json.getInt("industry_id");
                getEmpIndustry = json.getString("employer_industry_name");
                if (getEmpIndustry.equalsIgnoreCase("HR / Manpower Agency")) {
                    emp_pj_clientname.setVisibility(View.VISIBLE);
                    getClientname = emp_pj_clientname.getText().toString();
                } else {
                    getClientname = "";
                    emp_pj_clientname.setVisibility(View.GONE);
                }
                industriesList = new ArrayList<>();
                Gson gson = new Gson();
                industriesList.addAll((Collection<? extends Industry>) gson.fromJson
                        (json.getString("industries"),
                                new TypeToken<ArrayList<Industry>>() {
                                }.getType()));
                if (getIndustry != null && !getIndustry.isEmpty() && getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    industry.setText(getIndustry);
                    if (!languages.equalsIgnoreCase("English")) {
                        setIndustryLocalLang(getIndustry);
                    }
                    select_role = new ArrayList<>();
                    for (int i = 1; i < industriesList.size(); i++) {
                        if (industriesList.get(i).getIndustry_name().equals(getIndustry) && industriesList.get(i)
                                .getRole().size() > 0) {
                            select_role.addAll(industriesList.get(i)
                                    .getRole());
                        }
                    }
                    if (GenderalIndustryId != 0) {
                        select_role.addAll(industriesList.get(GenderalIndustryId - 1)
                                .getRole());
                    }
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
                } else {
                    industry.setText(R.string.selectindustry);
                }
                select_qualification = new ArrayList<>();
                Qualification c2 = new Qualification();
                c2.setId("1");
                c2.setQuali_name("Not Needed");
                c2.setSpecialization(null);
                select_qualification.add(c2);
                select_qualification.addAll((Collection<? extends Qualification>) gson.fromJson
                        (json.getString("list"),
                                new TypeToken<ArrayList<Qualification>>() {
                                }.getType()));
                JobTypeList = new ArrayList<>();
                // JobTypeIdList = new ArrayList<>();
                JobTypeList.addAll((Collection<? extends JobType>) gson.fromJson
                        (json.getString("job_types"),
                                new TypeToken<ArrayList<JobType>>() {
                                }.getType()));
               /* JSONArray jobtypegroups = json.getJSONArray("job_types");
                for (int i = 0; i < jobtypegroups.length(); i++) {
                    JSONObject c = jobtypegroups.getJSONObject(i);
                    String job_type_id = c.getString("job_type_id");
                    String job_type_name = c.getString("job_type_name");
                    JobTypeIdList.add(job_type_id);
                    JobTypeList.add(job_type_name);
                }*/
                // experience
                ExpList = new ArrayList<>();
                ExpList = gson.fromJson(json.getString("experiencelist"), new
                        TypeToken<ArrayList<Experience>>() {
                        }.getType());
                locationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                MainlocationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                locationList = new ArrayList<>();
                locationList = gson.fromJson(json.getString("locations"), new
                        TypeToken<ArrayList<FilterLocation>>() {
                        }.getType());
                if (getLocation != null && !getLocation.isEmpty()) {
                    locationlist.setText(getLocation);
                    getStatefromlocation(getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setLocationLocalLang(getLocation);
                    }
                } else {
                    locationlist.setText(R.string.select);
                }
                //gender
                select_gender = new ArrayList<>();
                select_gender = gson.fromJson(json.getString("genderlist"), new
                        TypeToken<ArrayList<Gender>>() {
                        }.getType());
                SalaryList = new ArrayList<>();
                JSONArray salarygroup = json.getJSONArray("salaryrange");
                for (int i = 0; i < salarygroup.length(); i++) {
                    JSONObject salary = salarygroup.getJSONObject(i);
                    String salary_name = salary.getString("salaryrange");
                    SalaryList.add(salary_name);
                }
            } catch (JSONException ignored) {
            }

            emp_pj_minql_lay.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (select_qualification.size() > 0) {
                                                            QualificationAlert();
                                                        }
                                                    }
                                                }

            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (PostJob.this));
        GlobalData.jobregid = sharedPreferences.getString("JRID", GlobalData.jobregid);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS", GlobalData
                .emp_login_status);
    }

    class getPostJob extends AsyncTask<String, String, String> {
        String postjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostJob.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody;
            if (GlobalData.jobregid != null && !GlobalData.jobregid.isEmpty()) {
                formBody = new FormBody.Builder().add("action", "JobUpdate").add("job_id",
                        GlobalData.jobregid)
                        .add("company_id", GlobalData.emp_login_status).add("job_title",
                                getJobTitle)
                        .add("location", getLocation)
                        .add("location_state",getStateName)
                        .add("minimum_qual", getQuali)
                        .add("qualification_id", selectedQuali_ID).add("specilization",
                                getSpecialization)
                        .add("experience", getExp).add("industry", getIndustry).add("role",
                                getJobRole)
                        .add("jobgender", getGender).add("job_offer_type", getJobTypeID).add
                                ("salary", getSalary)
                        .add("showphoneflag", getPhoneStatus).add("clientname", getClientname)
                        .build();
            } else {
                formBody = new FormBody.Builder().add("action", "Jobpost")
                        .add("company_id", GlobalData.emp_login_status).add("job_title",
                                getJobTitle)
                        .add("location", getLocation)
                        .add("location_state",getStateName)
                        .add("minimum_qual", getQuali)
                        .add("qualification_id", selectedQuali_ID).add("specilization",
                                getSpecialization)
                        .add("experience", getExp).add("industry", getIndustry).add("role",
                                getJobRole)
                        .add("jobgender", getGender).add("job_offer_type", getJobTypeID).add
                                ("salary", getSalary)
                        .add("showphoneflag", getPhoneStatus).add("clientname", getClientname)
                        .build();
            }
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update" +
                    ".php").post(formBody)
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
                    String getJobsAddStatus = responseObj.getString("status");
                    if (GlobalData.jobregid != null && !GlobalData.jobregid.isEmpty()) {
                        startActivity(new Intent(PostJob.this, PostJobNext.class));
                    } else {
                        if (getJobsAddStatus.equalsIgnoreCase("Jobs Added Sucessfully")) {
                            GlobalData.jobregid = responseObj.getString("insert_id");
                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(PostJob.this);
                            Editor editor = sharedPreferences.edit();
                            editor.putString("JRID", GlobalData.jobregid);
                            editor.apply();
                            startActivity(new Intent(PostJob.this, PostJobNext.class));
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PostJob.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(PostJob.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}