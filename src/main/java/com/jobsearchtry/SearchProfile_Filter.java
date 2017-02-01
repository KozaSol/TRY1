package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.CityAdapter;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Age;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.Experience;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.JobType;
import com.jobsearchtry.wrapper.Language;
import com.jobsearchtry.wrapper.Qualification;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.SkillCategory;
import com.jobsearchtry.wrapper.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchProfile_Filter extends Activity {
    private TextView gender, age, qualification, experience, role, industry, location, skills,
            languagess, cl_cityheader, stateselectedloc,
            cityselectedloc;
    private Button genderlist, agelist, qualificationlist, experiencelist, skilllist, rolelist,
            industrylist, locationlist,
            languageslist;
    private ArrayList<FilterLocation> LocationList = new ArrayList<>();
    private ArrayList<City> CityList = new ArrayList<>();
    private ArrayList<Qualification> qualificationList = null;
    private ArrayList<Qualification> qualificationIDList = null;
    private ArrayList<City> locationCityList = new ArrayList<>();
    public static ArrayList<City> MainlocationCityList = new ArrayList<>();
    private ArrayList<Experience> experienceList = null;
    private ArrayList<Role> roleList = null;
    private ArrayList<Industry> industriesList = null;
    private ArrayList<Gender> select_gender = null;
    private ArrayList<Age> ageList = null;
    private ArrayList<Language> languagesList = null;
    private final ArrayList<String> SelectedlanguagesList = new ArrayList<>();
    private final ArrayList<String> SelectedageList = new ArrayList<>();
    private ArrayList<String> selectedLocationList = new ArrayList<>(), selectedLocationLocalList = null;
    private final ArrayList<String> SelectedRoleList = new ArrayList<>();
    private final ArrayList<String> selectedIndustriesList = new ArrayList<>();
    private ArrayList<String> SelectedskillList = new ArrayList<>();
    private final ArrayList<String> SelectedqualificationList = new ArrayList<>();
    private ArrayList<String> SelectedqualificationIDList = new
            ArrayList<>();
    private boolean[] isCheckedQuali, isCheckedQualiID, isCheckedRole, isCheckedIndustry,
            isCheckedLocation, isCheckedSkill,
            isCheckedAge, isCheckedLanguages, isCheckedSkillCategory;
    private String getQualification, getQualificationID, getGender, getRole, getIndustry,
            getLocation, getAge, getExp, getFrom = "State",backtoSPLanding = "false",
            getSkill, getStateID, getState, getLanguages, getSkillCategory,
            getSkillCategoryID, languages, GenderLocal, ExperienceLocal, oldLocation, oldRole, oldGender,
            oldAge, oldQualification, oldExperience, oldIndustry, oldLanguages;
    private ProgressDialog pg;
    private ArrayList<SkillCategory> skillCategoryList = null;
    private ArrayList<Skill> skillList = null;
    private ArrayAdapter<SkillCategory> scadapter;
    private ArrayAdapter<Gender> genderadapter;
    private ArrayAdapter<Skill> skadapter;
    private ArrayAdapter<String> adapter, adapter1;
    private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<Age> ageadapter;
    private ArrayAdapter<Qualification> qualiadapter;
    private ArrayAdapter<Industry> industryAdapter;
    private ArrayAdapter<Experience> expAdapter;
    private ArrayAdapter<Language> langageAdapter;
    private static final String M_SP = "searchprofile";
    private int indexexp = -1, indexcity = -1, indexgender = -1,
            indexskill = -1;
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private OkHttpClient client = null;
    private LinearLayout locstate_view, city_view;
    private ListView filterstate, filtercity;
    private AutoCompleteTextView locfilt_citysearch, autocity;
    private ArrayList<String> selectedRoleLocalList = null;
    private ArrayList<String> selectedIndustriesLocalList = null;

    public void onBackPressed() {
        if (backtoSPLanding.equalsIgnoreCase("true")) {
            onbackClick();
        }else{
            finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.searchprofile_filter);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        ImageButton sp_f_h = (ImageButton) findViewById(R.id.js_r_h);
        sp_f_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchProfile_Filter.this, EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backtoSPLanding.equalsIgnoreCase("true")) {
                    onbackClick();
                }else{
                    finish();
                }
            }
        });
        languagess = (TextView) findViewById(R.id.esp_languages_filter_text);
        languageslist = (Button) findViewById(R.id.esp_languages_filter);
        gender = (TextView) findViewById(R.id.esp_gender_filter_text);
        genderlist = (Button) findViewById(R.id.esp_gender_filter);
        age = (TextView) findViewById(R.id.esp_age_filter_text);
        agelist = (Button) findViewById(R.id.esp_age_filter);
        role = (TextView) findViewById(R.id.esp_role_filter_text);
        rolelist = (Button) findViewById(R.id.esp_role_filter);
        industry = (TextView) findViewById(R.id.esp_industry_filter_text);
        qualification = (TextView) findViewById(R.id.esp_quali_filter_text);
        qualificationlist = (Button) findViewById(R.id.esp_quali_filter);
        skills = (TextView) findViewById(R.id.esp_skill_filter_text);
        skilllist = (Button) findViewById(R.id.esp_skill_filter);
        experience = (TextView) findViewById(R.id.esp_exp_filter_text);
        experiencelist = (Button) findViewById(R.id.esp_exp_filter);
        industrylist = (Button) findViewById(R.id.esp_industry_filter);
        location = (TextView) findViewById(R.id.esp_location_filter_text);
        locationlist = (Button) findViewById(R.id.esp_location_filter);
        Button reset = (Button) findViewById(R.id.esp_resetall_filter);
        Button done = (Button) findViewById(R.id.esp_done_filter);
        getSharedData();
        if (getLocation != null && !getLocation.isEmpty()) {
            locationlist.setText(R.string.edit);
        } else {
            locationlist.setText(R.string.select);
        }
        if (getGender != null && !getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
        } else {
            genderlist.setText(R.string.select);
        }
        if (getAge != null && !getAge.isEmpty()) {
            agelist.setText(R.string.edit);
        } else {
            agelist.setText(R.string.select);
        }
        if (getQualification != null && !getQualification.isEmpty()) {
            qualificationlist.setText(R.string.edit);
        } else {
            qualificationlist.setText(R.string.select);
        }
        if (getLanguages != null && !getLanguages.isEmpty()) {
            languageslist.setText(R.string.edit);
        } else {
            languageslist.setText(R.string.select);
        }
        if (getExp != null && !getExp.isEmpty()) {
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
        if (getIndustry != null && !getIndustry.isEmpty()) {
            industrylist.setText(R.string.edit);
        } else {
            industrylist.setText(R.string.select);
        }
        if (getRole != null && !getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
        } else {
            rolelist.setText(R.string.select);
        }
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getQualification().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        languageslist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languagesList != null && languagesList.size() > 0) {
                    LanguagesAlert();
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
        agelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ageList != null && ageList.size() > 0) {
                    AgeAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        skilllist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skillCategoryList != null && skillCategoryList.size() > 0) {
                    SkillAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        qualificationlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qualificationList != null && qualificationList.size() > 0) {
                    QualificationAlert();
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
        rolelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleList != null && roleList.size() > 0) {
                    RoleAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
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
        locationlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationList != null && LocationList.size() > 0) {
                    LocationAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.getdefaultLocation = false;
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    SharedPreferences searchprofilefilterPreferences = getSharedPreferences(M_SP,
                            Context.MODE_PRIVATE);
                    searchprofilefilterPreferences.edit().putString("F_P_ST", getStateID)
                            .putString("F_P_Q", getQualification).putString("F_P_QID",
                            getQualificationID)
                            .putString("F_P_G", getGender).putString("F_P_A", getAge).putString
                            ("F_P_S", getSkill)
                            .putString("F_P_LA", getLanguages).putString("F_P_E", getExp)
                            .putString("F_P_R", getRole)
                            .putString("F_P_I", getIndustry).putString("F_P_L", getLocation)
                            .apply();
                    new getFilterData().execute();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getLocation != null && !getLocation.isEmpty()) || getRole != null
                        || getGender != null || getAge != null
                        || (getQualificationID != null && !getQualificationID.isEmpty())
                        || getExp != null
                        || getIndustry != null || getLanguages != null) {
                    backtoSPLanding = "false";
                }else{
                    backtoSPLanding = "true";
                }
                SelectedqualificationList.clear();
                SelectedqualificationIDList.clear();
                if (SelectedRoleList.size() > 0) {
                    SelectedRoleList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedRoleLocalList.clear();
                    }
                }
                if (selectedIndustriesList.size() > 0) {
                    selectedIndustriesList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedIndustriesLocalList.clear();
                    }
                }
                if (selectedLocationList.size() > 0) {
                    selectedLocationList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedLocationLocalList.clear();
                    }
                }
                SelectedlanguagesList.clear();
                SelectedageList.clear();
                GlobalData.getdefaultLocation = false;
                languagess.setText("");
                if (languagesList != null && languagesList.size() > 0) {
                    isCheckedLanguages = new boolean[languagesList.size()];
                    Arrays.fill(isCheckedLanguages, false);
                }
                if (CityList != null && CityList.size() > 0) {
                    isCheckedLocation = new boolean[CityList.size()];
                    Arrays.fill(isCheckedLocation, false);
                }
                if (skillCategoryList != null && skillCategoryList.size() > 0) {
                    isCheckedSkillCategory = new boolean[skillCategoryList.size()];
                    Arrays.fill(isCheckedSkillCategory, false);
                }
                if (skillList != null && skillList.size() > 0) {
                    isCheckedSkill = new boolean[skillList.size()];
                    Arrays.fill(isCheckedSkill, false);
                }
                if (ageList != null && ageList.size() > 0) {
                    isCheckedAge = new boolean[ageList.size()];
                    Arrays.fill(isCheckedAge, false);
                }
                if (qualificationList != null && qualificationList.size() > 0) {
                    isCheckedQuali = new boolean[qualificationList.size()];
                    Arrays.fill(isCheckedQuali, false);
                    isCheckedQualiID = new boolean[qualificationIDList.size()];
                    Arrays.fill(isCheckedQualiID, false);
                }
                if (roleList != null && roleList.size() > 0) {
                    isCheckedRole = new boolean[roleList.size()];
                    Arrays.fill(isCheckedRole, false);
                }
                if (industriesList != null && industriesList.size() > 0) {
                    isCheckedIndustry = new boolean[industriesList.size()];
                    Arrays.fill(isCheckedIndustry, false);
                }
                getGender = null;
                //getGender = "";
                getLocation = null;
                //getLocation = "";
                getAge = null;
                //getAge = "";
                getQualification = null;
                //getQualification = "";
                getQualificationID = null;
                //getQualificationID = "";
                getSkill = null;
                getExp = null;
                getIndustry = null;
                getRole = null;
                getLanguages = null;
                gender.setText("");
                genderlist.setText(R.string.select);
                age.setText("");
                agelist.setText(R.string.select);
                qualification.setText("");
                qualificationlist.setText(R.string.select);
                experience.setText("");
                experiencelist.setText(R.string.select);
                location.setText("");
                locationlist.setText(R.string.select);
                role.setText("");
                rolelist.setText(R.string.select);
                industry.setText("");
                industrylist.setText(R.string.select);
                skills.setText("");
                skilllist.setText(R.string.select);
                languageslist.setText(R.string.select);
                if (!backtoSPLanding.equalsIgnoreCase("true")) {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new getQualification().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }
                backtoSPLanding = "true";
            }
        });
    }

    private void onbackClick() {
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            SharedPreferences searchprofilefilterPreferences = getSharedPreferences(M_SP, Context
                    .MODE_PRIVATE);
            searchprofilefilterPreferences.edit().putString("F_P_ST", getStateID)
                    .putString("F_P_Q", getQualification).putString("F_P_QID", getQualificationID)
                    .putString("F_P_G", getGender).putString("F_P_A", getAge).putString("F_P_S",
                    getSkill)
                    .putString("F_P_LA", getLanguages).putString("F_P_E", getExp).putString
                    ("F_P_R", getRole)
                    .putString("F_P_I", getIndustry).putString("F_P_L", getLocation).apply();
            new getFilterData().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT)
                    .show();
        }
    }

    private void getSharedData() {
        SharedPreferences searchprofilefilterPreferences = getSharedPreferences(M_SP, Context
                .MODE_PRIVATE);
        getQualification = searchprofilefilterPreferences.getString("F_P_Q", getQualification);
        oldQualification = getQualification;
        getQualificationID = searchprofilefilterPreferences.getString("F_P_QID",
                getQualificationID);
        getGender = searchprofilefilterPreferences.getString("F_P_G", getGender);
        oldGender = getGender;
        getAge = searchprofilefilterPreferences.getString("F_P_A", getAge);
        oldAge = getAge;
        //getSkill = searchprofilefilterPreferences.getString("F_P_S", getSkill);
        getExp = searchprofilefilterPreferences.getString("F_P_E", getExp);
        oldExperience = getExp;
        getRole = searchprofilefilterPreferences.getString("F_P_R", getRole);
        oldRole = getRole;
        getIndustry = searchprofilefilterPreferences.getString("F_P_I", getIndustry);
        oldIndustry = getIndustry;
        getLocation = searchprofilefilterPreferences.getString("F_P_L", getLocation);
        oldLocation = getLocation;
        getStateID = searchprofilefilterPreferences.getString("F_P_ST", getStateID);
        getLanguages = searchprofilefilterPreferences.getString("F_P_LA", getLanguages);
        oldLanguages = getLanguages;
    }

    private void refreshList(String fromfield) {
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            backtoSPLanding = "true";
            if (fromfield.equalsIgnoreCase("Location")) {
                oldLocation = getLocation;
            }
            if (fromfield.equalsIgnoreCase("Role")) {
                oldRole = getRole;
            }
            if (fromfield.equalsIgnoreCase("Gender")) {
                oldGender = getGender;
            }
            if (fromfield.equalsIgnoreCase("Age")) {
                oldAge = getAge;
            }
            if (fromfield.equalsIgnoreCase("Qualification")) {
                oldQualification = getQualification;
            }
            if (fromfield.equalsIgnoreCase("Experience")) {
                oldExperience = getExp;
            }
            if (fromfield.equalsIgnoreCase("Industry")) {
                oldIndustry = getIndustry;
            }
            if (fromfield.equalsIgnoreCase("Languages")) {
                oldLanguages = getLanguages;
            }
            new getQualification().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private class getQualification extends AsyncTask<String, String, String> {
        String searchprofileresponse;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(SearchProfile_Filter.this, R.style.MyTheme);
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
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("language", languages);
            }
            if (getGender != null && !getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", getGender);
            }
            if (getAge != null && !getAge.isEmpty()) {
                paramsadd.addFormDataPart("Age", getAge);
            }
            if (getExp != null && !getExp.isEmpty()) {
                paramsadd.addFormDataPart("experience", getExp);
            }
            if (getQualificationID != null && !getQualificationID.isEmpty()) {
                paramsadd.addFormDataPart("Qualification", getQualificationID);
            }
            if (getRole != null && !getRole.isEmpty()) {
                paramsadd.addFormDataPart("role", getRole);
            }
            if (getIndustry != null && !getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", getIndustry);
            }
            if (getLocation != null && !getLocation.isEmpty()) {
                paramsadd.addFormDataPart("location", getLocation);
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            if (GlobalData.isspkeywordsavail.equalsIgnoreCase("No")) {
                paramsadd.addFormDataPart("keywordflag", "No");
            }
            MultipartBody requestBody = paramsadd.build();
            request = new Request.Builder().url(GlobalData.url + "searchprofile_filterlist.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                searchprofileresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            getResultsData(searchprofileresponse);
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }

    private void getResultsData(String searchprofileresponse) {
        if (searchprofileresponse != null && !searchprofileresponse.isEmpty()) {
            try {
                JSONObject json = new JSONObject(searchprofileresponse);
                //locationlist
                LocationList = new ArrayList<>();
                Gson gson = new Gson();
                LocationList = gson.fromJson(json.getString("locations"), new
                        TypeToken<ArrayList<FilterLocation>>() {
                        }.getType());
                locationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                MainlocationCityList = gson.fromJson(json.getString("filterlocations"), new
                        TypeToken<ArrayList<City>>() {
                        }.getType());
                if (getLocation != null && !getLocation.isEmpty()) {
                    String removealltext = getLocation.replaceAll("All,", "");
                    location.setText(removealltext);
                    if (!languages.equalsIgnoreCase("English")) {
                        setLocationLocalLang();
                    }
                    locationlist.setText(R.string.edit);
                } else {
                    locationlist.setText(R.string.select);
                }
                // role
                roleList = new ArrayList<>();
                roleList = gson.fromJson(json.getString("role_name"), new
                        TypeToken<ArrayList<Role>>() {
                        }.getType());
                if (getRole != null && !getRole.isEmpty()) {
                    role.setText(getRole);
                    if (!languages.equalsIgnoreCase("English")) {
                        setRoleLocalLang(getRole);
                    }
                }
                //genderlist
                select_gender = gson.fromJson(json.getString("genderlist"), new
                        TypeToken<ArrayList<Gender>>() {
                        }.getType());
                if (getGender != null && !getGender.isEmpty()) {
                    gender.setText(getGender);
                    if (!languages.equalsIgnoreCase("English")) {
                        setGenderLocalLang();
                    }
                }
                //agelist
                ageList = new ArrayList<>();
                ageList = gson.fromJson(json.getString("agerange"), new
                        TypeToken<ArrayList<Age>>() {
                        }.getType());
                if (getAge != null && !getAge.isEmpty()) {
                    age.setText(getAge);
                }
                //qualificationlist
                qualificationList = new ArrayList<>();
                qualificationIDList = new ArrayList<>();
                qualificationList = gson.fromJson(json.getString("list"), new
                        TypeToken<ArrayList<Qualification>>() {
                        }.getType());
                /*JSONArray qualigroups = json.getJSONArray("list");
                for (int i = 0; i < qualigroups.length(); i++) {
                    JSONObject c = qualigroups.getJSONObject(i);
                    String occupations_list_id = c.getString("occupations_list_id");
                    String occupations_list_name = c.getString("occupations_list_name");
                    qualificationIDList.add(occupations_list_id);
                    qualificationList.add(occupations_list_name);
                }*/
                if (getQualification != null && !getQualification.isEmpty() && getQualificationID
                        != null
                        && !getQualificationID.isEmpty()) {
                    /*List<String> qualificationlist = Arrays.asList(getQualification.split(","));
                    List<String> qualificationidlist = Arrays.asList(getQualificationID.split("," +
                            ""));
                    isCheckedQuali = new boolean[qualificationList.size()];
                    for (int i = 0; i < qualificationlist.size(); i++) {
                        SelectedqualificationList.add(qualificationlist.get(i));
                        SelectedqualificationIDList.add(qualificationidlist.get(i));
                        int indexquali = qualificationList.indexOf(qualificationlist.get(i));
                        if (indexquali != -1) {
                            isCheckedQuali[indexquali] = true;
                        }
                    }*/
                    qualification.setText(getQualification);
                }
                // experience
                experienceList = new ArrayList<>();
                experienceList = gson.fromJson(json.getString("experiencelist"), new
                        TypeToken<ArrayList<Experience>>() {
                        }.getType());
                if (getExp != null && !getExp.isEmpty()) {
                    experience.setText(getExp);
                    if (!languages.equalsIgnoreCase("English")) {
                        setExperienceLocalLang();
                    }
                }
                // industry
                industriesList = new ArrayList<>();
                industriesList = gson.fromJson(json.getString("industries"), new
                        TypeToken<ArrayList<Industry>>() {
                        }.getType());
                if (getIndustry != null && !getIndustry.isEmpty()) {
                    industry.setText(getIndustry);
                    if (!languages.equalsIgnoreCase("English")) {
                        setIndustryLocalLang();
                    }
                }
                //languageslist
                languagesList = new ArrayList<>();
                languagesList = gson.fromJson(json.getString("languageslist"), new
                        TypeToken<ArrayList<Language>>() {
                        }.getType());
            } catch (JSONException ignored) {
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.connectionfailure), Toast
                    .LENGTH_SHORT).show();
        }
        if (getQualification != null && !getQualification.isEmpty() && getQualificationID !=
                null
                && !getQualificationID.isEmpty()) {
            qualification.setText(getQualification);
            qualificationlist.setText(R.string.edit);
        } else {
            qualification.setText("");
            qualificationlist.setText(R.string.select);
        }
        if (getExp != null && !getExp.isEmpty()) {
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
        if (getRole != null && !getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
        } else {
            rolelist.setText(R.string.select);
        }
        if (getIndustry != null && !getIndustry.isEmpty()) {
            industrylist.setText(R.string.edit);
        } else {
            industrylist.setText(R.string.select);
        }
        if (getSkill != null && !getSkill.isEmpty()) {
            skilllist.setText(R.string.edit);
        } else {
            skilllist.setText(R.string.select);
        }
        if (getAge != null && !getAge.isEmpty()) {
            age.setText(getAge);
            agelist.setText(R.string.edit);
        } else {
            age.setText("");
            agelist.setText(R.string.select);
        }
        if (getLanguages != null && !getLanguages.isEmpty()) {
            languagess.setText(getLanguages);
            languageslist.setText(R.string.edit);
        } else {
            languagess.setText("");
            languageslist.setText(R.string.select);
        }
    }

    private void GenderAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
                textView.setText(yourValue);
                String genderclick = select_gender.get(position).getIsavailable();
                if (select_gender.get(position).getIsavailable() == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filtergender.setAdapter(genderadapter);
        if (getGender != null && !getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
            indexgender = -1;
            for (int i = 0; i < select_gender.size(); i++) {
                if (select_gender.get(i).getGender().equals(getGender)) {
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
                String genderclick = select_gender.get(position).getIsavailable();
                if (genderclick == null) {
                    filtergender.setItemChecked(position, false);
                    GlobalData.getGender = null;
                } else {
                    if (filtergender.isItemChecked(indexgender)) {
                        filtergender.setItemChecked(position, false);
                        getGender = null;
                    } else {
                        filtergender.setItemChecked(position, true);
                        getGender = select_gender.get(position).getGender();
                    }
                    indexgender = filtergender.getCheckedItemPosition();
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gender.setText("");
                getGender = null;
                genderlist.setText(R.string.select);
                alertDialog.dismiss();
                GenderAlert();
            }
        });
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
        });
    }

    private void setGender() {
        gender.setText(getGender);
        if (!languages.equalsIgnoreCase("English")) {
            setGenderLocalLang();
        }
        reloadGenderData();
        if (getGender != null && !getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
        } else {
            genderlist.setText(R.string.select);
        }
    }

    private void reloadGenderData() {
        if (!(getGender == null || getGender.isEmpty()) && (oldGender == null || oldGender.isEmpty())) {
            if (getGender != null && !getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    if (!getGender.equalsIgnoreCase(oldGender)) {
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
            if (getGender == null || getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (oldGender == null || oldGender.isEmpty()) {
                if (getGender != null && !getGender.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (getGender != null && !getGender.isEmpty()) {
                if (oldGender != null && !oldGender.isEmpty()) {
                    if (!getGender.equalsIgnoreCase(oldGender)) {
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

    private void setGenderLocalLang() {
        for (int j = 0; j < select_gender.size(); j++) {
            if (select_gender.get(j).getGender().equals(getGender)) {
                GenderLocal = select_gender.get(j).getGender_local();
                gender.setText(GenderLocal);
            }
        }
    }

    private void AgeAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.age);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterage = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterage.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ageadapter = new ArrayAdapter<Age>(this, R.layout.filter_listrow, ageList) {
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
                String yourValue = ageList.get(position).getAgerange();
                textView.setText(yourValue);
                String ageclick = ageList.get(position).getIsavailable();
                if (ageclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };

        filterage.setAdapter(ageadapter);
        if (getAge != null && !getAge.isEmpty()) {
            agelist.setText(R.string.edit);
            List<String> agelist = Arrays.asList(getAge.split(","));
            isCheckedAge = new boolean[ageList.size()];
            for (int i = 0; i < agelist.size(); i++) {
                if (!(SelectedageList.contains(agelist.get(i)))) {
                    SelectedageList.add(agelist.get(i));
                }
                for (int j = 0; j < ageList.size(); j++) {
                    if (ageList.get(j).getAgerange().equals(agelist.get(i))) {
                        int indexage = j;
                        if (indexage != -1) {
                            isCheckedAge[indexage] = true;
                            filterage.setItemChecked(indexage, true);
                        }
                    }
                }
            }
        } else {
            agelist.setText(R.string.select);
            isCheckedAge = new boolean[ageList.size()];
            Arrays.fill(isCheckedAge, false);
        }
        filterage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String ageclick = ageadapter.getItem(position).getIsavailable();
                if (ageclick == null) {
                    // filterindustry.getChildAt(position).setEnabled(false);
                    filterage.setItemChecked(position, false);
                } else {
                    if (!isCheckedAge[position]) {
                        if (!(SelectedageList.contains(ageadapter.getItem(position).getAgerange()))) {
                            SelectedageList.add(ageadapter.getItem(position).getAgerange());
                        }
                    } else {
                        SelectedageList.remove(ageadapter.getItem(position).getAgerange());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectedageList.clear();
                age.setText("");
                isCheckedAge = new boolean[ageList.size()];
                Arrays.fill(isCheckedAge, false);
                getAge = null;
                agelist.setText(R.string.select);
                alertDialog.dismiss();
                AgeAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setAge();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setAge();
                alertDialog.dismiss();
            }
        });
    }

    private void setAge() {
        String[] getageaded = SelectedageList.toArray(new String[SelectedageList.size()]);
        String getagearray = Arrays.toString(getageaded);
        getAge = getagearray.substring(1, getagearray.length() - 1);
        getAge = getAge.replace(", ", ",");
        age.setText(getAge);
        reloadAgeData();
        if (getAge != null && !getAge.isEmpty()) {
            agelist.setText(R.string.edit);
        } else {
            agelist.setText(R.string.select);
        }
    }

    private void reloadAgeData() {
        if (!(getAge == null || getAge.isEmpty()) && (oldAge == null || oldAge.isEmpty())) {
            if (getAge != null && !getAge.isEmpty()) {
                if (oldAge != null && !oldAge.isEmpty()) {
                    if (!getAge.equalsIgnoreCase(oldAge)) {
                        refreshList("Age");
                    }
                } else {
                    refreshList("Age");
                }
            } else {
                if (oldAge != null && !oldAge.isEmpty()) {
                    refreshList("Age");
                }
            }
        } else {
            if (getAge == null || getAge.isEmpty()) {
                if (oldAge != null && !oldAge.isEmpty()) {
                    refreshList("Age");
                }
            }
            if (oldAge == null || oldAge.isEmpty()) {
                if (getAge != null && !getAge.isEmpty()) {
                    refreshList("Age");
                }
            }
            if (getAge != null && !getAge.isEmpty()) {
                if (oldAge != null && !oldAge.isEmpty()) {
                    if (!getAge.equalsIgnoreCase(oldAge)) {
                        refreshList("Age");
                    }
                } else {
                    refreshList("Age");
                }
            } else {
                if (oldAge != null && !oldAge.isEmpty()) {
                    refreshList("Age");
                }
            }
        }
    }

    private void QualificationAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.qualification);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterqualification = (ListView) emppromptView.findViewById(R.id
                .filterlist);
        filterqualification.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        qualiadapter = new ArrayAdapter<Qualification>(this, R.layout.filter_listrow, qualificationList) {
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
                String yourValue = qualificationList.get(position).getQuali_name();
                textView.setText(yourValue);
                String qualiclick = qualificationList.get(position).getIsavailable();
                if (qualiclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        /*adapter1 = new ArrayAdapter<>(SearchProfile_Filter.this, R.layout.filter_listrow,
                qualificationIDList);*/
        filterqualification.setAdapter(qualiadapter);
        if (getQualification != null && !getQualification.isEmpty() && getQualificationID != null
                && !getQualificationID.isEmpty()) {
            List<String> qualificationlist = Arrays.asList(getQualification.split(","));
            //List<String> qualificationlistidlist = Arrays.asList(getQualificationID.split(","));
            isCheckedQuali = new boolean[qualificationList.size()];
            //isCheckedQualiID = new boolean[qualificationIDList.size()];
            for (int i = 0; i < qualificationlist.size(); i++) {
                if (!(SelectedqualificationList.contains(qualificationlist.get(i)))) {
                    SelectedqualificationList.add(qualificationlist.get(i));
                    //SelectedqualificationIDList.add(qualificationlistidlist.get(i));
                }
                for (int j = 0; j < qualificationList.size(); j++) {
                    if (qualificationList.get(j).getQuali_name().equals(qualificationlist.get(i))) {
                        int indexquali = j;
                        if (indexquali != -1) {
                            isCheckedQuali[indexquali] = true;
                            filterqualification.setItemChecked(indexquali, true);
                        }
                    }
                }
            }
        } else {
            isCheckedQuali = new boolean[qualificationList.size()];
            Arrays.fill(isCheckedQuali, false);
            //isCheckedQualiID = new boolean[qualificationIDList.size()];
            // Arrays.fill(isCheckedQualiID, false);
        }
        filterqualification.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String qualiclick = qualiadapter.getItem(position).getIsavailable();
                if (qualiclick == null) {
                    // filterindustry.getChildAt(position).setEnabled(false);
                    filterqualification.setItemChecked(position, false);
                } else {
                    if (!isCheckedQuali[position]) {
                        if (!(SelectedqualificationList.contains(qualiadapter.getItem(position).getQuali_name()))) {
                            SelectedqualificationList.add(qualiadapter.getItem(position).getQuali_name());
                            // SelectedqualificationIDList.add(adapter1.getItem(position));
                        }
                    } else {
                        SelectedqualificationList.remove(qualiadapter.getItem(position).getQuali_name());
                        //SelectedqualificationIDList.remove(adapter1.getItem(position));
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectedqualificationList.clear();
                // SelectedqualificationIDList.clear();
                qualification.setText("");
                isCheckedQuali = new boolean[qualificationList.size()];
                Arrays.fill(isCheckedQuali, false);
                getQualification = null;
                getQualificationID = null;
                qualificationlist.setText(R.string.select);
                alertDialog.dismiss();
                QualificationAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setQualification();
                alertDialog.dismiss();
            }
        });
    }

    private void setQualification() {
        String[] getQualiaded = SelectedqualificationList.toArray(new
                String[SelectedqualificationList.size()]);
        String getqualiarray = Arrays.toString(getQualiaded);
        getQualification = getqualiarray.substring(1, getqualiarray.length() - 1);
        getQualification = getQualification.replace(", ", ",");
        qualification.setText(getQualification);
        reloadQualificationData();
        if (getQualification != null && !getQualification.isEmpty()) {
            List<String> qualificationlistt = Arrays.asList(getQualification.split(","));
            SelectedqualificationIDList = new ArrayList<>();
            for (int j = 0; j < qualificationlistt.size(); j++) {
                if (qualificationList.get(j).getQuali_name().equals(qualificationlistt.get(j))) {
                    SelectedqualificationIDList.add(qualificationList.get(j).getId());
                }
            }
            String[] getqualidtypeaded = SelectedqualificationIDList
                    .toArray(new String[SelectedqualificationIDList.size()]);
            String getqualiidarray = Arrays.toString(getqualidtypeaded);
            getQualificationID = getqualiidarray.substring(1, getqualiidarray.length() - 1);
            getQualificationID = getQualificationID.replace(", ", ",");
            qualificationlist.setText(R.string.edit);
        } else {
            qualificationlist.setText(R.string.select);
        }
    }
    private void reloadQualificationData() {
        if (!(getQualification == null || getQualification.isEmpty()) && (oldQualification == null || oldQualification.isEmpty())) {
            if (getQualification != null && !getQualification.isEmpty()) {
                if (oldQualification != null && !oldQualification.isEmpty()) {
                    if (!getQualification.equalsIgnoreCase(oldQualification)) {
                        refreshList("Qualification");
                    }
                } else {
                    refreshList("Qualification");
                }
            } else {
                if (oldQualification != null && !oldQualification.isEmpty()) {
                    refreshList("Qualification");
                }
            }
        } else {
            if (getQualification == null || getQualification.isEmpty()) {
                if (oldQualification != null && !oldQualification.isEmpty()) {
                    refreshList("Qualification");
                }
            }
            if (oldQualification == null || oldQualification.isEmpty()) {
                if (getQualification != null && !getQualification.isEmpty()) {
                    refreshList("Qualification");
                }
            }
            if (getQualification != null && !getQualification.isEmpty()) {
                if (oldQualification != null && !oldQualification.isEmpty()) {
                    if (!getQualification.equalsIgnoreCase(oldQualification)) {
                        refreshList("Qualification");
                    }
                } else {
                    refreshList("Qualification");
                }
            } else {
                if (oldQualification != null && !oldQualification.isEmpty()) {
                    refreshList("Qualification");
                }
            }
        }
    }
    private void ExpAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
        if (getExp != null && !getExp.isEmpty()) {
            experiencelist.setText(R.string.edit);
            indexexp = -1;
            for (int i = 0; i < experienceList.size(); i++) {
                if (experienceList.get(i).getExperience_profile_name().equals(getExp)) {
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
                    filterexp.setItemChecked(position, false);
                    GlobalData.getExperience = null;
                } else {
                    if (filterexp.isItemChecked(indexexp)) {
                        filterexp.setItemChecked(position, false);
                        getExp = null;
                    } else {
                        filterexp.setItemChecked(position, true);
                        getExp = experienceList.get(position).getExperience_profile_name();
                    }
                    indexexp = filterexp.getCheckedItemPosition();
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getExp = null;
                experience.setText("");
                experiencelist.setText(R.string.select);
                alertDialog.dismiss();
                ExpAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExperience();
                alertDialog.dismiss();
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
        experience.setText(getExp);
        if (!languages.equalsIgnoreCase("English")) {
            setExperienceLocalLang();
        }
        reloadExperienceData();
        if (getExp != null && !getExp.isEmpty()) {
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
    }

    private void setExperienceLocalLang() {
        for (int j = 0; j < experienceList.size(); j++) {
            if (experienceList.get(j).getExperience_profile_name().equals(getExp)) {
                ExperienceLocal = experienceList.get(j).getExperience_profile_name_local();
                experience.setText(ExperienceLocal);
            }
        }
    }

    private void reloadExperienceData() {
        if (!(getExp == null || getExp.isEmpty()) && (oldExperience == null || oldExperience.isEmpty())) {
            if (getExp != null && !getExp.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    if (!getExp.equalsIgnoreCase(oldExperience)) {
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
            if (getExp == null || getExp.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    refreshList("Experience");
                }
            }
            if (oldExperience == null || oldExperience.isEmpty()) {
                if (getExp != null && !getExp.isEmpty()) {
                    refreshList("Experience");
                }
            }
            if (getExp != null && !getExp.isEmpty()) {
                if (oldExperience != null && !oldExperience.isEmpty()) {
                    if (!getExp.equalsIgnoreCase(oldExperience)) {
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

    private void RoleAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.role);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterrole.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        roleadapter = new ArrayAdapter<Role>(this, R.layout.filter_listrow, roleList) {
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
                String yourValue = roleList.get(position).getRole_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = roleList.get(position).getRole_name_local();
                }
                textView.setText(yourValue);
                String roleclick = roleList.get(position).getIsavailable();
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
        if (getRole != null && !getRole.isEmpty()) {
            List<String> rolelist = Arrays.asList(getRole.split(","));
            isCheckedRole = new boolean[roleList.size()];
            for (int i = 0; i < rolelist.size(); i++) {
                if (!(SelectedRoleList.contains(rolelist.get(i)))) {
                    SelectedRoleList.add(rolelist.get(i));
                }
                int indexrole = roleList.indexOf(rolelist.get(i));
                for (int j = 0; j < roleList.size(); j++) {
                    if (roleList.get(j).getRole_name().equals(rolelist.get(i))) {
                        indexrole = j;
                        if (indexrole != -1) {
                            isCheckedRole[indexrole] = true;
                            filterrole.setItemChecked(indexrole, true);
                        }
                    }
                }
            }
        } else {
            isCheckedRole = new boolean[roleList.size()];
            Arrays.fill(isCheckedRole, false);
        }
        filterrole.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String roleclick = roleadapter.getItem(position).getIsavailable();
                if (roleclick == null) {
                    // filterrole.getChildAt(position).setEnabled(false);
                    filterrole.setItemChecked(position, false);
                } else {
                    if (!isCheckedRole[position]) {
                        isCheckedRole[position] = true;
                        if (!(SelectedRoleList.contains(roleadapter.getItem(position).getRole_name()))) {
                            SelectedRoleList.add(roleadapter.getItem(position).getRole_name());
                        }
                    } else {
                        isCheckedRole[position] = false;
                        SelectedRoleList.remove(roleadapter.getItem(position).getRole_name());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SelectedRoleList.size() > 0) {
                    SelectedRoleList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedRoleLocalList.clear();
                    }
                }
                role.setText("");
                isCheckedRole = new boolean[roleList.size()];
                Arrays.fill(isCheckedRole, false);
                getRole = null;
                rolelist.setText(R.string.select);
                alertDialog.dismiss();
                RoleAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setRole();
                alertDialog.dismiss();
            }
        });
    }

    private void setRole() {
        String[] getroleaded = SelectedRoleList.toArray(new String[SelectedRoleList.size
                ()]);
        String getrolearray = Arrays.toString(getroleaded);
        getRole = getrolearray.substring(1, getrolearray.length() - 1);
        getRole = getRole.replace(", ", ",");
        role.setText(getRole);
        reloadRoleData();
        if (getRole != null && !getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang(getRole);
            }
        } else {
            rolelist.setText(R.string.select);
        }
    }

    private void reloadRoleData() {
        if (!(getRole == null || getRole.isEmpty()) && (oldRole == null || oldRole.isEmpty())) {
            if (getRole != null && !getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    if (!getRole.equalsIgnoreCase(oldRole)) {
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
            if (getRole == null || getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    refreshList("Role");
                }
            }
            if (oldRole == null || oldRole.isEmpty()) {
                if (getRole != null && !getRole.isEmpty()) {
                    refreshList("Role");
                }
            }
            if (getRole != null && !getRole.isEmpty()) {
                if (oldRole != null && !oldRole.isEmpty()) {
                    if (!getRole.equalsIgnoreCase(oldRole)) {
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
            for (int j = 0; j < roleList.size(); j++) {
                if (roleList.get(j).getRole_name().equals(rolenamelist.get(i))) {
                    selectedRoleLocalList.add(roleList.get(j).getRole_name_local());
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
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
                textView.setText(yourValue);
                return textView;
            }
        };
        filterindustry.setAdapter(industryAdapter);
        if (getIndustry != null && !getIndustry.isEmpty()) {
           /* if (getIndustry.equalsIgnoreCase("All")) {
                isCheckedIndustry = new boolean[industriesList.size()];
                for (int j = 0; j < industriesList.size(); j++) {
                    isCheckedIndustry[j] = true;
                    filterindustry.setItemChecked(j, true);
                    if (!(selectedIndustriesList.contains(adapter.getItem(j)))) {
                        selectedIndustriesList.add(adapter.getItem(j));
                    }
                }
            } else {*/
            List<String> industrylist = Arrays.asList(getIndustry.split(","));
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
            // }
        } else {
            isCheckedIndustry = new boolean[industriesList.size()];
            Arrays.fill(isCheckedIndustry, false);
        }
        filterindustry.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String industryclick = industryAdapter.getItem(position).getIsavailable();
               /* if (adapter.getItem(position).equalsIgnoreCase("All")) {
                    if (!isCheckedIndustry[position]) {
                        isCheckedIndustry[position] = true;
                        filterindustry.setItemChecked(position, true);
                        isCheckedIndustry = new boolean[industriesList.size()];
                        for (int j = 0; j < industriesList.size(); j++) {
                            isCheckedIndustry[j] = true;
                            filterindustry.setItemChecked(j, true);
                            if (!(selectedIndustriesList.contains(adapter.getItem(j)))) {
                                selectedIndustriesList.add(adapter.getItem(j));
                            }
                        }
                    } else {
                        isCheckedIndustry[position] = false;
                        filterindustry.setItemChecked(position, false);
                        isCheckedIndustry = new boolean[industriesList.size()];
                        for (int j = 0; j < industriesList.size(); j++) {
                            isCheckedIndustry[j] = false;
                            filterindustry.setItemChecked(j, false);
                            selectedIndustriesList.remove(adapter.getItem(j));
                        }
                    }
                } else*/
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
                    /*if (selectedIndustriesList.contains("All")) {
                        selectedIndustriesList.remove("All");
                        isCheckedIndustry[0] = false;
                        filterindustry.setItemChecked(0, false);
                    }*/
                        selectedIndustriesList.remove(industryAdapter.getItem(position).getIndustry_name());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
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
                getIndustry = null;
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
        getIndustry = getindustryarray.substring(1, getindustryarray.length() - 1);
        getIndustry = getIndustry.replace(", ", ",");
        industry.setText(getIndustry);
        reloadIndustryData();
        if (getIndustry != null && !getIndustry.isEmpty()) {
            industrylist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang();
            }
        } else {
            industrylist.setText(R.string.select);
        }
    }

    private void reloadIndustryData() {
        if (!(getIndustry == null || getIndustry.isEmpty()) && (oldIndustry == null || oldIndustry.isEmpty())) {
            if (getIndustry != null && !getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    if (!getIndustry.equalsIgnoreCase(oldIndustry)) {
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
            if (getIndustry == null || getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
            if (oldIndustry == null || oldIndustry.isEmpty()) {
                if (getIndustry != null && !getIndustry.isEmpty()) {
                    refreshList("Industry");
                }
            }
            if (getIndustry != null && !getIndustry.isEmpty()) {
                if (oldIndustry != null && !oldIndustry.isEmpty()) {
                    if (!getIndustry.equalsIgnoreCase(oldIndustry)) {
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
        List<String> industrylist = Arrays.asList(getIndustry.split(","));
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

    private void LocationAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout
                .location_filter_popup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
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
            //location.setText(getLocation);
            locationlist.setText(R.string.edit);
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
                getState = LocationList.get(position).getState_name();
                getStateID = LocationList.get(position).getId();
                autocity.setFocusableInTouchMode(false);
                locfilt_citysearch.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                getCityListAdapter();
            }
        });
        //city - autocomplete textview
        if (locationCityList.size() > 0) {
            final CityAdapter loccityAdapter = new CityAdapter(SearchProfile_Filter.this, R.layout.spinner_item_text, locationCityList) {
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
                GlobalData.getdefaultLocation = false;
                isCheckedLocation = new boolean[CityList.size()];
                Arrays.fill(isCheckedLocation, false);
                getLocation = null;
                location.setText("");
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
        GlobalData.getdefaultLocation = false;
        String[] getLocationaded = selectedLocationList.toArray(new
                String[selectedLocationList.size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocation = getLocation.replace(", ", ",");
        reloadLocationData();
        if (getLocation != null && !getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            location.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
                setLocationLocalLang();
            }
            locationlist.setText(R.string.edit);
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            location.setText("");
            locationlist.setText(R.string.select);
        }

    }
    private void reloadLocationData() {
        if (!(getLocation == null || getLocation.isEmpty()) && (oldLocation == null || oldLocation.isEmpty())) {
            if (getLocation != null && !getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    if (!getLocation.equalsIgnoreCase(oldLocation)) {
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
            if (getLocation == null || getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    refreshList("Location");
                }
            }
            if (oldLocation == null || oldLocation.isEmpty()) {
                if (getLocation != null && !getLocation.isEmpty()) {
                    refreshList("Gender");
                }
            }
            if (getLocation != null && !getLocation.isEmpty()) {
                if (oldLocation != null && !oldLocation.isEmpty()) {
                    if (!getLocation.equalsIgnoreCase(oldLocation)) {
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
        List<String> locationlist = Arrays.asList(getLocation.split(","));
        for (int i = 0; i < locationlist.size(); i++) {
            for (int j = 0; j < locationCityList.size(); j++) {
                if (locationCityList.get(j).getCiti_name().equals(locationlist.get(i))) {
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

    private void getCityListAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocation = getLocation.replace(", ", ",");
        CityList = new ArrayList<>();
        for (int i = 0; i < LocationList.size(); i++) {
            if (LocationList.get(i).getId().equals(getStateID)) {
                CityList = LocationList.get(i).getCities();
            }
        }
        locstate_view.setVisibility(View.GONE);
        city_view.setVisibility(View.VISIBLE);
        cl_cityheader.setText(getState);
        locfilt_citysearch.setText(autocity.getText().toString());
        //get the city list depends upon selected state
        cadapter = new ArrayAdapter<City>(SearchProfile_Filter.this, R.layout.filter_listrow,
                CityList) {
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
                String locclick = CityList.get(position).getIsavailable();
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
        if (getLocation != null && !getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            location.setText(getLocation);
            locationlist.setText(R.string.edit);
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
            location.setText("");
            locationlist.setText(R.string.select);
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[CityList.size()];
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
            loccityAdapter = new CityAdapter(SearchProfile_Filter.this, R.layout.spinner_item_text, getcityforstate) {
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
                        if (!languages.equalsIgnoreCase("English")) {
                            setSelectedLocation(stateselectedloc, cityselectedloc);
                        }
                        location.setText(getLocation);
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
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(SearchProfile_Filter.this, R.layout
                .skillcategory_row, LocationList) {
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
                textView.setText(LocationList.get(position).getState_name());
                if (getLocation != null && !getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
                    //location.setText(getLocation);
                    locationlist.setText(R.string.edit);
                    List<String> locationlist = Arrays.asList(getLocation.split(","));
                    getStateID = LocationList.get(position).getId();
                    CityList = LocationList.get(position).getCities();
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

    private void SkillAlert() {
        SelectedskillList = new ArrayList<>();
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.skill_filterpopup,
                null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
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
                getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                getSkill = getSkill.replace(", ", ",");
                //get|set the skill category items
                scadapter = new ArrayAdapter<SkillCategory>(SearchProfile_Filter.this, R.layout
                        .skillcategory_row, skillCategoryList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Context mContext = this.getContext();
                        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        View root;
                        if (convertView == null) {
                            root = layoutInflater.inflate(R.layout.skillcategory_row, parent,
                                    false);
                        } else {
                            root = convertView;
                        }
                        TextView textView = (TextView) root.findViewById(R.id
                                .editjob_skill_catename);
                        TextView selectedskill = (TextView) root.findViewById(R.id
                                .editjob_skillcate_skillname);
                        textView.setText(skillCategoryList.get(position).getSkillcategoryname());
                        if (getSkill != null && !getSkill.isEmpty()) {
                            List<String> locationlist = Arrays.asList(getSkill.split(","));
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
        scadapter = new ArrayAdapter<SkillCategory>(SearchProfile_Filter.this, R.layout
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
                if (getSkill != null && !getSkill.isEmpty()) {
                    List<String> locationlist = Arrays.asList(getSkill.split(","));
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
        if (getSkill != null && !getSkill.isEmpty()) {
            List<String> locationlist = Arrays.asList(getSkill.split(","));
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
            skilllist.setText(getSkill);
            skills.setText(getSkill);
        } else {
            skills.setText("");
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
                getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                getSkill = getSkill.replace(", ", ",");
                skillList = new ArrayList<>();
                for (int i = 0; i < skillCategoryList.size(); i++) {
                    if (skillCategoryList.get(i).getSkillcategoryid().equals(getSkillCategoryID)) {
                        skillList = skillCategoryList.get(i).getSkills();
                    }
                }
                //get|set the skills
                skadapter = new ArrayAdapter<Skill>(SearchProfile_Filter.this, R.layout
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
                if (getSkill != null && !getSkill.isEmpty()) {
                    List<String> locationlist = Arrays.asList(getSkill.split(","));
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
        //skill selection reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getSkillCategory = null;
                getSkillCategoryID = null;
                SelectedskillList.clear();
                isCheckedSkill = new boolean[skillList.size()];
                Arrays.fill(isCheckedSkill, false);
                getSkill = null;
                skilllist.setText(R.string.select);
                skills.setText(getSkill);
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
                getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
                getSkill = getSkill.replace(", ", ",");
                if (getSkill != null && !getSkill.isEmpty()) {
                    skills.setText(getSkill);
                    skilllist.setText(R.string.edit);
                } else {
                    skills.setText("");
                    skilllist.setText(R.string.select);
                }
                alertDialog.dismiss();
            }
        });
    }

    private void LanguagesAlert() {
        View emppromptView = View.inflate(SearchProfile_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(SearchProfile_Filter.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.languages);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterlanguages = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterlanguages.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        langageAdapter = new ArrayAdapter<Language>(this, R.layout.filter_listrow, languagesList) {
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
                String yourValue = languagesList.get(position).getLanguage_name();
                textView.setText(yourValue);
                String langclick = languagesList.get(position).getIsavailable();
                if (langclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filterlanguages.setAdapter(langageAdapter);
        if (getLanguages != null && !getLanguages.isEmpty()) {
            List<String> langlist = Arrays.asList(getLanguages.split(","));
            isCheckedLanguages = new boolean[languagesList.size()];
            for (int i = 0; i < langlist.size(); i++) {
                if (!(SelectedlanguagesList.contains(langlist.get(i)))) {
                    SelectedlanguagesList.add(langlist.get(i));
                }
                for (int j = 0; j < languagesList.size(); j++) {
                    if (languagesList.get(j).getLanguage_name().equals(langlist.get(i))) {
                        int indexlang = j;
                        if (indexlang != -1) {
                            isCheckedLanguages[indexlang] = true;
                            filterlanguages.setItemChecked(indexlang, true);
                        }
                    }
                }
            }
        } else {
            isCheckedLanguages = new boolean[languagesList.size()];
            Arrays.fill(isCheckedLanguages, false);
        }
        filterlanguages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String langclick = langageAdapter.getItem(position).getIsavailable();
                if (langclick == null) {
                    // filterindustry.getChildAt(position).setEnabled(false);
                    filterlanguages.setItemChecked(position, false);
                } else {
                    if (!isCheckedLanguages[position]) {
                        if (!(SelectedlanguagesList.contains(langageAdapter.getItem(position).getLanguage_name()))) {
                            SelectedlanguagesList.add(langageAdapter.getItem(position).getLanguage_name());
                        }
                    } else {
                        SelectedlanguagesList.remove(langageAdapter.getItem(position).getLanguage_name());
                    }
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectedlanguagesList.clear();
                languagess.setText("");
                isCheckedLanguages = new boolean[languagesList.size()];
                Arrays.fill(isCheckedLanguages, false);
                getLanguages = null;
                languageslist.setText(R.string.select);
                alertDialog.dismiss();
                LanguagesAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguages();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setLanguages();
                alertDialog.dismiss();
            }
        });
    }

    private void setLanguages() {
        String[] getlangaded = SelectedlanguagesList.toArray(new
                String[SelectedlanguagesList.size()]);
        String getlangarray = Arrays.toString(getlangaded);
        getLanguages = getlangarray.substring(1, getlangarray.length() - 1);
        getLanguages = getLanguages.replace(", ", ",");
        languagess.setText(getLanguages);
        reloadLanguagesData();
        if (getLanguages != null && !getLanguages.isEmpty()) {
            languageslist.setText(R.string.edit);
        } else {
            languageslist.setText(R.string.select);
        }
    }

    private void reloadLanguagesData() {
        if (!(getLanguages == null || getLanguages.isEmpty()) && (oldLanguages == null || oldLanguages.isEmpty())) {
            if (getLanguages != null && !getLanguages.isEmpty()) {
                if (oldLanguages != null && !oldLanguages.isEmpty()) {
                    if (!getLanguages.equalsIgnoreCase(oldLanguages)) {
                        refreshList("Languages");
                    }
                } else {
                    refreshList("Languages");
                }
            } else {
                if (oldLanguages != null && !oldLanguages.isEmpty()) {
                    refreshList("Languages");
                }
            }
        } else {
            if (getLanguages == null || getLanguages.isEmpty()) {
                if (oldLanguages != null && !oldLanguages.isEmpty()) {
                    refreshList("Languages");
                }
            }
            if (oldLanguages == null || oldLanguages.isEmpty()) {
                if (getLanguages != null && !getLanguages.isEmpty()) {
                    refreshList("Languages");
                }
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                if (oldLanguages != null && !oldLanguages.isEmpty()) {
                    if (!getLanguages.equalsIgnoreCase(oldLanguages)) {
                        refreshList("Languages");
                    }
                } else {
                    refreshList("Languages");
                }
            } else {
                if (oldLanguages != null && !oldLanguages.isEmpty()) {
                    refreshList("Languages");
                }
            }
        }
    }

    private class getFilterData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(SearchProfile_Filter.this, R.style.MyTheme);
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
                paramsadd.addFormDataPart("language", languages);
            }
            if (getGender != null && !getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", getGender);
            }
            if (getAge != null && !getAge.isEmpty()) {
                paramsadd.addFormDataPart("Age", getAge);
            }
            if (getExp != null && !getExp.isEmpty()) {
                paramsadd.addFormDataPart("experience", getExp);
            }
            if (GlobalData.SEKeyword != null && !GlobalData.SEKeyword.isEmpty()) {
                paramsadd.addFormDataPart("keyword", GlobalData.SEKeyword);
            }
            if (getQualificationID != null && !getQualificationID.isEmpty()) {
                paramsadd.addFormDataPart("Qualification", getQualificationID);
            }
            if (getRole != null && !getRole.isEmpty()) {
                paramsadd.addFormDataPart("role", getRole);
            }
            if (getIndustry != null && !getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", getIndustry);
            }
            if (getLocation != null && !getLocation.isEmpty()) {
                paramsadd.addFormDataPart("location", getLocation);
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "profile_search.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.employerfilterresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (SearchProfile_Filter.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SF", GlobalData.employerfilterresponse);
            editor.apply();
            if (GlobalData.employerfilterresponse != null
                    && !GlobalData.employerfilterresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.employerfilterresponse);
                    String getFilterStatus = responseObj.getString("message");
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        GlobalData.employeeList = new ArrayList<>();
                        Gson gson = new Gson();
                        GlobalData.employeeList = gson.fromJson(responseObj.getString
                                        ("profile_view"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        startActivity(new Intent(SearchProfile_Filter.this, EmployeeListing
                                .class));
                        finish();
                    } else {
                        GlobalData.employeeList.clear();
                        Toast.makeText(SearchProfile_Filter.this, getString(R.string.emplistemptymsg), Toast
                                .LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SearchProfile_Filter.this, getString(R.string.errortoparse), Toast
                            .LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SearchProfile_Filter.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}
