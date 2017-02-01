package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Experience;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.JobStatus;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.SkillCategory;
import com.jobsearchtry.wrapper.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Responses_Filter extends Activity {
    private TextView gender, qualification, experience, age, languagess, status, jobtitle;
    private TextView role, skills;
    private Button genderlist, qualificationlist, experiencelist, agelist, statuslist,
            languageslist, jobtitlelist;
    //private Button rolelist,skillslist;
    private static final String[] select_status_char = {"A", "S", "R", "B", "H"};
    private ArrayList<String> qualificationList = new ArrayList<>();
    private ArrayList<String> qualificationIDList = new
            ArrayList<>();
    private ArrayList<JobStatus> select_status = null;
    private ArrayList<Gender> select_gender = null;
    private ArrayList<Experience> experienceList = new ArrayList<>();
    private ArrayList<String> ageList = new ArrayList<>();
    //private ArrayList<String> SelectedskillList = new ArrayList<>();
    private ArrayList<String> languagesList = new ArrayList<>();
    //private ArrayList<Role> roleList = null;
    //private final ArrayList<String> SelectedRoleList = new ArrayList<>();
    private final ArrayList<String> SelectedlanguagesList = new ArrayList<>();
    private ArrayList<String> jobtitleList = new ArrayList<>();
    private final ArrayList<String> SelectedjobtitleList = new ArrayList<>();
    private final ArrayList<String> SelectedexperienceList = new ArrayList<>();
    private final ArrayList<String> SelectedageList = new ArrayList<>();
    private final ArrayList<String> SelectedstatusList = new ArrayList<>();
    private final ArrayList<String> SelectedstatusCharList = new ArrayList<>();
    private final ArrayList<String> SelectedqualificationList = new
            ArrayList<>();
    //private ArrayList<String> selectedRoleLocalList = new ArrayList<>();
    private ArrayList<String> selectedStatusLocalList = new ArrayList<>(),
            selectedExperienceLocalList = new ArrayList<>();
    private final ArrayList<String> SelectedqualificationIDList = new ArrayList<>();
    private boolean[] isCheckedQuali, isCheckedQualiID, isCheckedStatus, isCheckedAge,
            isCheckedJobTitle, isCheckedStatusChar,
            isCheckedExp, isCheckedLanguages;
    //private boolean[]  isCheckedRole,isCheckedSkill;
    private String getQualification, getQualificationID, getGender, getExperience, getAge,
            getStatus, getJobTitle, languages, GenderLocal,
            getStatusChar, getLanguages, getSkillCategoryID;
    //private String getRole,getSkillCategory,getSkill;
    private OkHttpClient client = null;
    private ProgressDialog pg;
    // private ArrayAdapter<Skill> skadapter;
    private static final String M_RF = "responsesfilter";
    private int indexgender = -1;
    // private int indexskill = -1;
    private ArrayAdapter<String> adapter, adapter1;
    private ArrayAdapter<Gender> genderadapter;
    //private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<Experience> expAdapter;
    //private ArrayAdapter<SkillCategory> scadapter;
    private ArrayAdapter<JobStatus> jobstatusadapter;

    //This class implements an application that filter the joblist who are applied a job
    // (employer-responses-filter).
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.responses_filter);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        ImageButton res_f_h = (ImageButton) findViewById(R.id.js_r_h);
        res_f_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Responses_Filter.this, EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gender = (TextView) findViewById(R.id.res_gender_filter_text);
        genderlist = (Button) findViewById(R.id.res_gender_filter);
        // skills = (TextView) findViewById(R.id.res_skill_filter_text);
        // skillslist = (Button) findViewById(R.id.res_skill_filter);
        languagess = (TextView) findViewById(R.id.res_languages_filter_text);
        languageslist = (Button) findViewById(R.id.res_languages_filter);
        jobtitle = (TextView) findViewById(R.id.er_jobtitle_filter_text);
        jobtitlelist = (Button) findViewById(R.id.er_jobtitle_filter);
        qualification = (TextView) findViewById(R.id.er_quali_filter_text);
        qualificationlist = (Button) findViewById(R.id.er_quali_filter);
        //role = (TextView) findViewById(R.id.er_role_filter_text);
        //rolelist = (Button) findViewById(R.id.er_role_filter);
        experience = (TextView) findViewById(R.id.er_exp_filter_text);
        experiencelist = (Button) findViewById(R.id.er_exp_filter);
        age = (TextView) findViewById(R.id.er_age_filter_text);
        agelist = (Button) findViewById(R.id.er_age_filter);
        status = (TextView) findViewById(R.id.er_status_filter_text);
        statuslist = (Button) findViewById(R.id.er_status_filter);
        Button reset = (Button) findViewById(R.id.er_resetall_filter);
        Button done = (Button) findViewById(R.id.er_done_filter);
        getSharedData();
        if (getJobTitle != null && !getJobTitle.isEmpty()) {
            jobtitle.setText(getJobTitle);
            jobtitlelist.setText(R.string.edit);
        } else {
            jobtitlelist.setText(R.string.select);
        }
        if (getGender != null && !getGender.isEmpty()) {
            gender.setText(getGender);
            genderlist.setText(R.string.edit);
        } else {
            genderlist.setText(R.string.select);
        }
       /* if (getSkill != null && !getSkill.isEmpty()) {
            skills.setText(getSkill);
            skillslist.setText(R.string.edit);
        } else {
            skillslist.setText(R.string.select);
        }*/
        if (getLanguages != null && !getLanguages.isEmpty()) {
            languagess.setText(getLanguages);
            languageslist.setText(R.string.edit);
        } else {
            languageslist.setText(R.string.select);
        }
        if (getQualification != null && !getQualification.isEmpty()) {
            qualification.setText(getQualification);
            qualificationlist.setText(R.string.edit);
        } else {
            qualificationlist.setText(R.string.select);
        }
        /*if (getRole != null && !getRole.isEmpty()) {
            role.setText(getRole);
            rolelist.setText(R.string.edit);
        } else {
            rolelist.setText(R.string.select);
        }*/
        if (getExperience != null && !getExperience.isEmpty()) {
            experience.setText(getExperience);
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
        if (getAge != null && !getAge.isEmpty()) {
            age.setText(getAge);
            agelist.setText(R.string.edit);
        } else {
            agelist.setText(R.string.select);
        }
        if (getStatus != null && !getStatus.isEmpty()) {
            status.setText(getStatus);
            statuslist.setText(R.string.edit);
        } else {
            statuslist.setText(R.string.select);
        }
        if (getExperience != null && !getExperience.isEmpty()) {
            experience.setText(getExperience);
            experiencelist.setText(R.string.edit);
        } else {
            experiencelist.setText(R.string.select);
        }
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getJobTitle().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
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
        languageslist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languagesList.size() > 0) {
                    LanguagesAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
      /*  rolelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleList != null && roleList.size() > 0) {
                    RoleAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });*/
        jobtitlelist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (jobtitleList.size() > 0) {
                    JobTitleAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        qualificationlist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (qualificationList.size() > 0) {
                    QualificationAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        experiencelist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (experienceList != null && experienceList.size() > 0) {
                    ExpAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        agelist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ageList != null && ageList.size() > 0) {
                    AgeAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        statuslist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (select_status != null && select_status.size() > 0) {
                    StatusAlert();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        done.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SharedPreferences responsefilterPreferences = getSharedPreferences(M_RF, Context
                        .MODE_PRIVATE);
                responsefilterPreferences.edit().putString("F_R_J", getJobTitle).putString
                        ("F_R_Q", getQualification)
                        .putString("F_R_QID", getQualificationID)
                        //.putString("F_R_R", getRole)
                        .putString("F_R_G", getGender)
                        .putString("F_R_E", getExperience).putString
                        ("F_R_A", getAge)
                        //.putString("F_R_SK", getSkill)
                        .putString("F_R_L", getLanguages).putString
                        ("F_R_S", getStatus)
                        .putString("F_R_SS", getStatusChar).apply();
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    new getResponseFilterData().execute();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SelectedjobtitleList.clear();
                SelectedqualificationList.clear();
                SelectedqualificationIDList.clear();
                SelectedexperienceList.clear();
                SelectedageList.clear();
                // SelectedskillList.clear();
                SelectedlanguagesList.clear();
                SelectedstatusList.clear();
                SelectedstatusCharList.clear();
                selectedStatusLocalList.clear();
                //SelectedRoleList.clear();
                qualification.setText("");
                jobtitle.setText("");
                experience.setText("");
                status.setText("");
                age.setText("");
                // skills.setText("");
                languagess.setText("");
                if (jobtitleList != null && jobtitleList.size() > 0) {
                    isCheckedJobTitle = new boolean[jobtitleList.size()];
                    Arrays.fill(isCheckedJobTitle, false);
                }
                if (experienceList != null && experienceList.size() > 0) {
                    isCheckedExp = new boolean[experienceList.size()];
                    Arrays.fill(isCheckedExp, false);
                }
               /* if (roleList != null && roleList.size() > 0) {
                    isCheckedRole = new boolean[roleList.size()];
                    Arrays.fill(isCheckedRole, false);
                }*/
                if (ageList != null && ageList.size() > 0) {
                    isCheckedAge = new boolean[ageList.size()];
                    Arrays.fill(isCheckedAge, false);
                }
                if (languagesList != null && languagesList.size() > 0) {
                    isCheckedLanguages = new boolean[languagesList.size()];
                    Arrays.fill(isCheckedLanguages, false);
                }
                if (qualificationList.size() > 0) {
                    isCheckedQuali = new boolean[qualificationList.size()];
                    Arrays.fill(isCheckedQuali, false);
                    isCheckedQualiID = new boolean[qualificationIDList.size()];
                    Arrays.fill(isCheckedQualiID, false);
                }
                if (select_status != null && select_status.size() > 0) {
                    isCheckedStatus = new boolean[select_status.size()];
                    Arrays.fill(isCheckedStatus, false);
                    isCheckedStatusChar = new boolean[select_status_char.length];
                    Arrays.fill(isCheckedStatusChar, false);
                }
                getJobTitle = null;
                //getRole = null;
                getGender = null;
                getQualification = null;
                getQualificationID = null;
                getExperience = null;
                getAge = null;
                // getSkill = null;
                getLanguages = null;
                getStatus = null;
                getStatusChar = null;
                gender.setText("");
                genderlist.setText(R.string.select);
                //role.setText("");
                //rolelist.setText(R.string.select);
                qualificationlist.setText(R.string.select);
                jobtitlelist.setText(R.string.select);
                experiencelist.setText(R.string.select);
                statuslist.setText(R.string.select);
                agelist.setText(R.string.select);
                // skillslist.setText(R.string.select);
                languageslist.setText(R.string.select);
                GlobalData.responsesList.clear();
            }
        });
    }

    private void getSharedData() {
        SharedPreferences responsefilterPreferences = getSharedPreferences(M_RF, Context
                .MODE_PRIVATE);
        getJobTitle = responsefilterPreferences.getString("F_R_J", getJobTitle);
        getQualification = responsefilterPreferences.getString("F_R_Q", getQualification);
        getQualificationID = responsefilterPreferences.getString("F_R_QID", getQualificationID);
        getGender = responsefilterPreferences.getString("F_R_G", getGender);
        //getRole = responsefilterPreferences.getString("F_R_R", getRole);
        getExperience = responsefilterPreferences.getString("F_R_E", getExperience);
        getAge = responsefilterPreferences.getString("F_R_A", getAge);
        //getSkill = responsefilterPreferences.getString("F_R_SK", getSkill);
        getLanguages = responsefilterPreferences.getString("F_R_L", getLanguages);
        getStatus = responsefilterPreferences.getString("F_R_S", getStatus);
        getStatusChar = responsefilterPreferences.getString("F_R_SS", getStatusChar);
    }

    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getJobTitle().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private class getJobTitle extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Responses_Filter.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "responses_filterlist" +
                    ".php").post(requestBody)
                    .build();
            try {
                client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                Gson gson = new Gson();
                jobtitleList = new ArrayList<>();
                JSONArray groups = json.getJSONArray("title");
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject c = groups.getJSONObject(i);
                    String occupations_list_name = c.getString("job_title");
                    jobtitleList.add(occupations_list_name);
                }
                /*roleList = new ArrayList<>();
                roleList = gson.fromJson(json.getString("role_name"), new
                        TypeToken<ArrayList<Role>>() {
                        }.getType());*/
                qualificationList = new ArrayList<>();
                qualificationIDList = new ArrayList<>();
                JSONArray qualigroups = json.getJSONArray("list");
                for (int i = 0; i < qualigroups.length(); i++) {
                    JSONObject c = qualigroups.getJSONObject(i);
                    String occupations_list_id = c.getString("occupations_list_id");
                    String occupations_list_name = c.getString("occupations_list_name");
                    qualificationIDList.add(occupations_list_id);
                    qualificationList.add(occupations_list_name);
                }
                //genderlist
                select_gender = gson.fromJson(json.getString("genderlist"), new
                        TypeToken<ArrayList<Gender>>() {
                        }.getType());
                experienceList = new ArrayList<>();
                experienceList = gson.fromJson(json.getString("experiencelist"), new
                        TypeToken<ArrayList<Experience>>() {
                        }.getType());
                languagesList = new ArrayList<>();
                JSONArray langgroups = json.getJSONArray("languageslist");
                for (int i = 0; i < langgroups.length(); i++) {
                    JSONObject c = langgroups.getJSONObject(i);
                    String languname = c.getString("language_name");
                    languagesList.add(languname);
                }
                ageList = new ArrayList<>();
                JSONArray agegroups = json.getJSONArray("agerange");
                isCheckedAge = new boolean[agegroups.length()];
                Arrays.fill(isCheckedAge, false);
                for (int i = 0; i < agegroups.length(); i++) {
                    JSONObject c = agegroups.getJSONObject(i);
                    String occupations_list_name = c.getString("agerange");
                    ageList.add(occupations_list_name);
                }
                //statuslist
                select_status = gson.fromJson(json.getString("statuslist"), new
                        TypeToken<ArrayList<JobStatus>>() {
                        }.getType());
            } catch (JSONException | IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (getJobTitle != null && !getJobTitle.isEmpty()) {
                jobtitle.setText(getJobTitle);
                jobtitlelist.setText(R.string.edit);
            } else {
                jobtitlelist.setText(R.string.select);
            }
            if (getGender != null && !getGender.isEmpty()) {
                gender.setText(getGender);
                if (!languages.equalsIgnoreCase("English") && select_gender != null && select_gender.size() > 0) {
                    setGenderLocalLang();
                }
            }
            /*if (getRole != null && !getRole.isEmpty()) {
                role.setText(getRole);
                if (!languages.equalsIgnoreCase("English") && roleList != null && roleList.size() > 0) {
                    setRoleLocalLang(getRole);
                }
                rolelist.setText(R.string.edit);
            } else {
                rolelist.setText(R.string.select);
            }*/
            if (getQualification != null && !getQualification.isEmpty() && getQualificationID !=
                    null
                    && !getQualificationID.isEmpty()) {
                qualification.setText(getQualification);
                qualificationlist.setText(R.string.edit);
            } else {
                qualificationlist.setText(R.string.select);
            }
            if (getExperience != null && !getExperience.isEmpty()) {
                experience.setText(getExperience);
                if (!languages.equalsIgnoreCase("English") && experienceList != null && experienceList.size() > 0) {
                    setExperienceLocalLang(getExperience);
                }
                experiencelist.setText(R.string.edit);
            } else {
                experiencelist.setText(R.string.select);
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                languagess.setText(getLanguages);
                languageslist.setText(R.string.edit);
            } else {
                languageslist.setText(R.string.select);
            }
            if (getAge != null && !getAge.isEmpty()) {
                age.setText(getAge);
                agelist.setText(R.string.edit);
            } else {
                agelist.setText(R.string.select);
            }
            if (getStatus != null && !getStatus.isEmpty()) {
                status.setText(getStatus);
                if (!languages.equalsIgnoreCase("English") && select_status != null && select_status.size() > 0) {
                    setStatusLocalLang(getStatus);
                }
                experiencelist.setText(R.string.edit);
            } else {
                experiencelist.setText(R.string.select);
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }

    private void GenderAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
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
                if (filtergender.isItemChecked(indexgender)) {
                    filtergender.setItemChecked(position, false);
                    getGender = null;
                } else {
                    filtergender.setItemChecked(position, true);
                    getGender = select_gender.get(position).getGender();
                }
                indexgender = filtergender.getCheckedItemPosition();
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
        if (getGender != null && !getGender.isEmpty()) {
            genderlist.setText(R.string.edit);
        } else {
            genderlist.setText(R.string.select);
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

    /*private void RoleAlert() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Responses_Filter.this);
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
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
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        final AlertDialog alertD = alertdialog.create();
        alertD.setView(emppromptView, 0, 0, 0, 0);
        alertD.show();
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
                alertD.dismiss();
                RoleAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertD.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setRole();
                alertD.dismiss();
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
        if (getRole != null && !getRole.isEmpty()) {
            rolelist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang(getRole);
            }
        } else {
            rolelist.setText(R.string.select);
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
    }*/

    private void JobTitleAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.jobtitle);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterjobtitle = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterjobtitle.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.filter_listrow, jobtitleList) {
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
                String yourValue = jobtitleList.get(position);
                textView.setText(yourValue);
                return textView;
            }
        };
        filterjobtitle.setAdapter(adapter);
        if (getJobTitle != null && !getJobTitle.isEmpty()) {
            List<String> jontitlelist = Arrays.asList(getJobTitle.split(","));
            isCheckedJobTitle = new boolean[jobtitleList.size()];
            for (int i = 0; i < jontitlelist.size(); i++) {
                if (!(SelectedjobtitleList.contains(jontitlelist.get(i)))) {
                    SelectedjobtitleList.add(jontitlelist.get(i));
                }
                int indexrole = jobtitleList.indexOf(jontitlelist.get(i));
                if (indexrole != -1) {
                    isCheckedJobTitle[indexrole] = true;
                    filterjobtitle.setItemChecked(indexrole, true);
                }
            }
        } else {
            isCheckedJobTitle = new boolean[jobtitleList.size()];
            Arrays.fill(isCheckedJobTitle, false);
        }
        filterjobtitle.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedJobTitle[position]) {
                    if (!(SelectedjobtitleList.contains(adapter.getItem(position)))) {
                        SelectedjobtitleList.add(adapter.getItem(position));
                    }
                } else {
                    SelectedjobtitleList.remove(adapter.getItem(position));
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectedjobtitleList.clear();
                jobtitle.setText("");
                isCheckedJobTitle = new boolean[jobtitleList.size()];
                Arrays.fill(isCheckedJobTitle, false);
                getJobTitle = null;
                jobtitlelist.setText(R.string.select);
                alertDialog.dismiss();
                JobTitleAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobTitle();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setJobTitle();
                alertDialog.dismiss();
            }
        });
    }

    private void setJobTitle() {
        String[] getjobtitleaded = SelectedjobtitleList.toArray(new
                String[SelectedjobtitleList.size()]);
        String getjobtitlearray = Arrays.toString(getjobtitleaded);
        getJobTitle = getjobtitlearray.substring(1, getjobtitlearray.length() - 1);
        getJobTitle = getJobTitle.replace(", ", ",");
        jobtitle.setText(getJobTitle);
        if (getJobTitle != null && !getJobTitle.isEmpty()) {
            jobtitlelist.setText(R.string.edit);
        } else {
            jobtitlelist.setText(R.string.select);
        }
    }

    private void QualificationAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
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
        adapter = new ArrayAdapter<String>(this, R.layout.filter_listrow, qualificationList) {
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
                String yourValue = qualificationList.get(position);
                textView.setText(yourValue);
                return textView;
            }
        };
        adapter1 = new ArrayAdapter<>(Responses_Filter.this, android.R.layout
                .simple_list_item_multiple_choice,
                qualificationIDList);
        filterqualification.setAdapter(adapter);
        if (getQualification != null && !getQualification.isEmpty() && getQualificationID != null
                && !getQualificationID.isEmpty()) {
            List<String> qualificationlist = Arrays.asList(getQualification.split(","));
            List<String> qualificationlistidlist = Arrays.asList(getQualificationID.split(","));
            isCheckedQuali = new boolean[qualificationList.size()];
            isCheckedQualiID = new boolean[qualificationIDList.size()];
            for (int i = 0; i < qualificationlist.size(); i++) {
                if (!(SelectedqualificationList.contains(qualificationlist.get(i)))) {
                    SelectedqualificationList.add(qualificationlist.get(i));
                    SelectedqualificationIDList.add(qualificationlistidlist.get(i));
                }
                int indexquali = qualificationList.indexOf(qualificationlist.get(i));
                if (indexquali != -1) {
                    isCheckedQuali[indexquali] = true;
                    isCheckedQualiID[indexquali] = true;
                    filterqualification.setItemChecked(indexquali, true);
                }
            }
        } else {
            isCheckedQuali = new boolean[qualificationList.size()];
            Arrays.fill(isCheckedQuali, false);
            isCheckedQualiID = new boolean[qualificationIDList.size()];
            Arrays.fill(isCheckedQualiID, false);
        }
        filterqualification.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedQuali[position]) {
                    if (!(SelectedqualificationList.contains(adapter.getItem(position)))) {
                        SelectedqualificationList.add(adapter.getItem(position));
                        SelectedqualificationIDList.add(adapter1.getItem(position));
                    }
                } else {
                    SelectedqualificationList.remove(adapter.getItem(position));
                    SelectedqualificationIDList.remove(adapter1.getItem(position));
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectedqualificationList.clear();
                SelectedqualificationIDList.clear();
                qualification.setText("");
                isCheckedQuali = new boolean[qualificationList.size()];
                Arrays.fill(isCheckedQuali, false);
                getQualification = null;
                getQualificationID = null;
                qualificationlist.setText(R.string.select);
                QualificationAlert();
                alertDialog.dismiss();
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
        String[] getqualidtypeaded = SelectedqualificationIDList
                .toArray(new String[SelectedqualificationIDList.size()]);
        String getqualiarray = Arrays.toString(getQualiaded);
        String getqualiidarray = Arrays.toString(getqualidtypeaded);
        getQualification = getqualiarray.substring(1, getqualiarray.length() - 1);
        getQualificationID = getqualiidarray.substring(1, getqualiidarray.length() - 1);
        getQualification = getQualification.replace(", ", ",");
        getQualificationID = getQualificationID.replace(", ", ",");
        qualification.setText(getQualification);
        if (getQualification != null && !getQualification.isEmpty() && getQualificationID
                != null
                && !getQualificationID.isEmpty()) {
            qualificationlist.setText(R.string.edit);
        } else {
            qualificationlist.setText(R.string.select);
        }
    }

    private void ExpAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        final TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.experience);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterexp.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                return textView;
            }
        };
        filterexp.setAdapter(expAdapter);
        if (getExperience != null && !getExperience.isEmpty()) {
            List<String> explist = Arrays.asList(getExperience.split(","));
            isCheckedExp = new boolean[experienceList.size()];
            for (int i = 0; i < explist.size(); i++) {
                if (!(SelectedexperienceList.contains(explist.get(i)))) {
                    SelectedexperienceList.add(explist.get(i));
                }
                int indexexp = experienceList.indexOf(explist.get(i));
                for (int j = 0; j < experienceList.size(); j++) {
                    if (experienceList.get(j).getExperience_profile_name().equals(explist.get(i))) {
                        indexexp = j;
                        if (indexexp != -1) {
                            isCheckedExp[indexexp] = true;
                            filterexp.setItemChecked(indexexp, true);
                        }
                    }
                }
            }
        } else {
            isCheckedExp = new boolean[experienceList.size()];
            Arrays.fill(isCheckedExp, false);
        }
        filterexp.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedExp[position]) {
                    if (!(SelectedexperienceList.contains(expAdapter.getItem(position).getExperience_profile_name()))) {
                        SelectedexperienceList.add(expAdapter.getItem(position).getExperience_profile_name());
                    }
                } else {
                    SelectedexperienceList.remove(expAdapter.getItem(position).getExperience_profile_name());
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SelectedexperienceList.size() > 0) {
                    SelectedexperienceList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedExperienceLocalList.clear();
                    }
                }
                experience.setText("");
                isCheckedExp = new boolean[experienceList.size()];
                Arrays.fill(isCheckedExp, false);
                getExperience = null;
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
        String[] getexpaded = SelectedexperienceList.toArray(new
                String[SelectedexperienceList.size()]);
        String getexparray = Arrays.toString(getexpaded);
        getExperience = getexparray.substring(1, getexparray.length() - 1);
        getExperience = getExperience.replace(", ", ",");
        experience.setText(getExperience);
        if (getExperience != null && !getExperience.isEmpty()) {
            experiencelist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setExperienceLocalLang(getExperience);
            }
        } else {
            experiencelist.setText(R.string.select);
        }
    }

    private void setExperienceLocalLang(String getExperience) {
        selectedExperienceLocalList = new ArrayList<>();
        List<String> experiencelist = Arrays.asList(getExperience.split(","));
        for (int i = 0; i < experiencelist.size(); i++) {
            for (int j = 0; j < experienceList.size(); j++) {
                if (experienceList.get(j).getExperience_profile_name().equalsIgnoreCase(experiencelist.get(i))) {
                    selectedExperienceLocalList.add(experienceList.get(j).getExperience_profile_name_local());
                }
            }
        }
        String[] getExpaded = selectedExperienceLocalList.toArray(new String[selectedExperienceLocalList.size
                ()]);
        String getexparray = Arrays.toString(getExpaded);
        String getExpTamil = getexparray.substring(1, getexparray.length() - 1);
        getExpTamil = getExpTamil.replace(", ", ",");
        experience.setText(getExpTamil);
    }

    private void AgeAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.age);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterage = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterage.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.filter_listrow, ageList) {
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
                String yourValue = ageList.get(position);
                textView.setText(yourValue);
                return textView;
            }
        };
        filterage.setAdapter(adapter);
        if (getAge != null && !getAge.isEmpty()) {
            agelist.setText(R.string.edit);
            List<String> agelist = Arrays.asList(getAge.split(","));
            isCheckedAge = new boolean[ageList.size()];
            for (int i = 0; i < agelist.size(); i++) {
                if (!(SelectedageList.contains(agelist.get(i)))) {
                    SelectedageList.add(agelist.get(i));
                }
                int indexage = ageList.indexOf(agelist.get(i));
                if (indexage != -1) {
                    isCheckedAge[indexage] = true;
                    filterage.setItemChecked(indexage, true);
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
                if (!isCheckedAge[position]) {
                    if (!(SelectedageList.contains(adapter.getItem(position)))) {
                        SelectedageList.add(adapter.getItem(position));
                    }
                } else {
                    SelectedageList.remove(adapter.getItem(position));
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
        if (getAge != null && !getAge.isEmpty()) {
            agelist.setText(R.string.edit);
        } else {
            agelist.setText(R.string.select);
        }
    }

    private void StatusAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.spplicastatus);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterstatus = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterstatus.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        jobstatusadapter = new ArrayAdapter<JobStatus>(this, R.layout.filter_listrow, select_status) {
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
                String yourValue = select_status.get(position).getStatusname();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_status.get(position).getStatusname_local();
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        adapter1 = new ArrayAdapter<>(Responses_Filter.this, R.layout.filter_listrow,
                select_status_char);
        filterstatus.setAdapter(jobstatusadapter);
        if (getStatus != null && !getStatus.isEmpty() && getStatusChar != null && !getStatusChar
                .isEmpty()) {
            statuslist.setText(R.string.edit);
            List<String> statuslist = Arrays.asList(getStatus.split(","));
            List<String> statuscharlist = Arrays.asList(getStatusChar.split(","));
            isCheckedStatus = new boolean[select_status.size()];
            isCheckedStatusChar = new boolean[select_status_char.length];
            for (int i = 0; i < select_status.size(); i++) {
                for (int j = 0; j < statuslist.size(); j++) {
                    if (select_status.get(i).getStatusname().equalsIgnoreCase(statuslist.get(j))) {
                        if (!(SelectedstatusList.contains(statuslist.get(j)))) {
                            SelectedstatusList.add(statuslist.get(j));
                            SelectedstatusCharList.add(statuscharlist.get(j));
                        }
                        if (i != -1) {
                            isCheckedStatus[i] = true;
                            isCheckedStatusChar[i] = true;
                            filterstatus.setItemChecked(i, true);
                        }
                    }
                }
            }
        } else {
            statuslist.setText(R.string.select);
            isCheckedStatus = new boolean[select_status.size()];
            Arrays.fill(isCheckedStatus, false);
            isCheckedStatusChar = new boolean[select_status_char.length];
            Arrays.fill(isCheckedStatusChar, false);
        }
        filterstatus.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!(SelectedstatusList.contains(jobstatusadapter.getItem(position).getStatusname()))) {
                    SelectedstatusList.add(jobstatusadapter.getItem(position).getStatusname());
                    SelectedstatusCharList.add(adapter1.getItem(position));
                } else {
                    SelectedstatusList.remove(jobstatusadapter.getItem(position).getStatusname());
                    SelectedstatusCharList.remove(adapter1.getItem(position));
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (SelectedstatusList.size() > 0) {
                    SelectedstatusList.clear();
                    SelectedstatusCharList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedStatusLocalList.clear();
                    }
                }
                status.setText("");
                isCheckedStatus = new boolean[select_status.size()];
                Arrays.fill(isCheckedStatus, false);
                isCheckedStatusChar = new boolean[select_status_char.length];
                Arrays.fill(isCheckedStatusChar, false);
                getStatus = null;
                getStatusChar = null;
                statuslist.setText(R.string.select);
                alertDialog.dismiss();
                StatusAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setApplicationStatus();
                alertDialog.dismiss();
            }
        });
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setApplicationStatus();
                alertDialog.dismiss();
            }
        });
    }

    private void setApplicationStatus() {
        String[] getstatusaded = SelectedstatusList.toArray(new String[SelectedstatusList
                .size()]);
        String[] getstatuscharaded = SelectedstatusCharList.toArray(new
                String[SelectedstatusCharList.size()]);
        String getstatusarray = Arrays.toString(getstatusaded);
        String getstatuschararray = Arrays.toString(getstatuscharaded);
        getStatus = getstatusarray.substring(1, getstatusarray.length() - 1);
        getStatusChar = getstatuschararray.substring(1, getstatuschararray.length() - 1);
        getStatus = getStatus.replace(", ", ",");
        getStatusChar = getStatusChar.replace(", ", ",");
        status.setText(getStatus);
        if (getStatus != null && !getStatus.isEmpty()) {
            statuslist.setText(R.string.edit);
            if (!languages.equalsIgnoreCase("English")) {
                setStatusLocalLang(getStatus);
            }
        } else {
            statuslist.setText(R.string.select);
        }
    }

    private void setStatusLocalLang(String getStatus) {
        selectedStatusLocalList = new ArrayList<>();
        List<String> jobstatuslist = Arrays.asList(getStatus.split(","));
        for (int i = 0; i < jobstatuslist.size(); i++) {
            for (int j = 0; j < select_status.size(); j++) {
                if (select_status.get(j).getStatusname().equals(jobstatuslist.get(i))) {
                    selectedStatusLocalList.add(select_status.get(j).getStatusname_local());
                }
            }
        }
        String[] getStatusaded = selectedStatusLocalList.toArray(new String[selectedStatusLocalList.size
                ()]);
        String getstatusarray = Arrays.toString(getStatusaded);
        String getStatusTamil = getstatusarray.substring(1, getstatusarray.length() - 1);
        getStatusTamil = getStatusTamil.replace(", ", ",");
        status.setText(getStatusTamil);
    }

    private void LanguagesAlert() {
        View emppromptView = View.inflate(Responses_Filter.this, R.layout.filterpopup, null);
        final Dialog alertDialog = new Dialog(Responses_Filter.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.f_popupheader);
        f_popupheader.setText(R.string.languages);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterlanguages = (ListView) emppromptView.findViewById(R.id.filterlist);
        filterlanguages.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.filter_listrow, languagesList) {
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
                String yourValue = languagesList.get(position);
                textView.setText(yourValue);
                return textView;
            }
        };
        filterlanguages.setAdapter(adapter);
        if (getLanguages != null && !getLanguages.isEmpty()) {
            List<String> langlist = Arrays.asList(getLanguages.split(","));
            isCheckedLanguages = new boolean[languagesList.size()];
            for (int i = 0; i < langlist.size(); i++) {
                if (!(SelectedlanguagesList.contains(langlist.get(i)))) {
                    SelectedlanguagesList.add(langlist.get(i));
                }
                int indexlang = languagesList.indexOf(langlist.get(i));
                if (indexlang != -1) {
                    isCheckedLanguages[indexlang] = true;
                    filterlanguages.setItemChecked(indexlang, true);
                }
            }
        } else {
            isCheckedLanguages = new boolean[languagesList.size()];
            Arrays.fill(isCheckedLanguages, false);
        }
        filterlanguages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedLanguages[position]) {
                    if (!(SelectedlanguagesList.contains(adapter.getItem(position)))) {
                        SelectedlanguagesList.add(adapter.getItem(position));
                    }
                } else {
                    SelectedlanguagesList.remove(adapter.getItem(position));
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
        if (getLanguages != null && !getLanguages.isEmpty()) {
            languageslist.setText(R.string.edit);
        } else {
            languageslist.setText(R.string.select);
        }
    }

    private class getResponseFilterData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Responses_Filter.this, R.style.MyTheme);
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
                paramsadd.addFormDataPart("language", languages);
            }
            paramsadd.addFormDataPart("action", "filter");
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            if (getJobTitle != null && !getJobTitle.isEmpty()) {
                paramsadd.addFormDataPart("job_title", getJobTitle);
            }
            if (getGender != null && !getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", getGender);
            }
            /*if (getRole != null && !getRole.isEmpty()) {
                paramsadd.addFormDataPart("role", getRole);
            }*/
            if (getQualificationID != null && !getQualificationID.isEmpty()) {
                paramsadd.addFormDataPart("Qualification", getQualificationID);
            }
            if (getExperience != null && !getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", getExperience);
            }
            if (getAge != null && !getAge.isEmpty()) {
                paramsadd.addFormDataPart("Age", getAge);
            }
            /* if (getSkill != null && !getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", getSkill);
            }*/
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            if (getStatusChar != null && !getStatusChar.isEmpty()) {
                paramsadd.addFormDataPart("Status", getStatusChar);
            }
            if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
                paramsadd.addFormDataPart("keyword", GlobalData.ResKeyword);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "applied_job_search.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.responsesfilterresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            SharedPreferences responsefilterPreferences = getSharedPreferences(M_RF, Context
                    .MODE_PRIVATE);
            responsefilterPreferences.edit().putString("RF", GlobalData.responsesfilterresponse)
                    .apply();
            if (GlobalData.responsesfilterresponse != null
                    && !GlobalData.responsesfilterresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.responsesfilterresponse);
                    String getFilterStatus = responseObj.getString("message");
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        GlobalData.responsesList = new ArrayList<>();
                        GlobalData.responsesList = gson.fromJson(responseObj.getString
                                        ("jobseeker_profile"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        startActivity(new Intent(Responses_Filter.this, Responses
                                .class));
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.responselistemptymsg), Toast.LENGTH_SHORT)
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
