package com.jobsearchtry;

import android.annotation.SuppressLint;
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
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.CityAdapter;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.DesiredJob;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.JobType;
import com.jobsearchtry.wrapper.Role;

import org.json.JSONArray;
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

public class MyProfile_DesiredJob extends Activity {
    private Button djrole, djindustry, djsalary, djjobtype;
    private LinearLayout js_edit_dj_role_lay, js_dj_edit_industry_lay, js_dj_edit_salary_lay,
            js_dj_edit_jobtype_lay, locstate_view, city_view, js_edit_dj_location_lay;
    private Button djlocation;
    private String getLocation, getRole, languages,
            getIndustry, getSalary,
            getJobType, getStateID, getState, getFrom = "State";
    private ArrayList<FilterLocation> LocationList = new ArrayList<>();
    private ArrayList<City> CityList = new ArrayList<>(), MainlocationCityList = new ArrayList<>(),
            locationCityList = new ArrayList<>();
    private ArrayList<Role> roleList = null;
    private ArrayList<Industry> industriesList = null;
    private ArrayList<JobType> jobtypeList = null;
    private ArrayList<String> select_salary = null, selectedLocationList = new ArrayList<>(),
            selectedLocationLocalList = new ArrayList<>();
    private ProgressDialog pg;
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<Industry> industryAdapter;
    private ArrayAdapter<JobType> jobtypeadapter;
    private int indexcity = -1, indexjobtype = -1, indexindustry = -1, indexsalary = -1, indexrole = -1;
    private boolean[] isCheckedLocation;
    private OkHttpClient client = null;
    private ListView filterstate, filtercity;
    private AutoCompleteTextView locfilt_citysearch, autocity;
    private TextView cl_cityheader, stateselectedloc, cityselectedloc, loc_popupheader;

    @Override
    public void onBackPressed() {
        new BackAlertDialog().isBackDialog(MyProfile_DesiredJob.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desiredjobdetail_add);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS",
                GlobalData.login_status);
        GlobalData.personalresponse = sharedPreferences.getString(
                "USER_DETAILS", GlobalData.personalresponse);
        ImageButton dj_d_h = (ImageButton) findViewById(R.id.js_r_h);
        dj_d_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_DesiredJob.this,
                        Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackAlertDialog().isBackDialog(MyProfile_DesiredJob.this);
            }
        });
        js_edit_dj_location_lay = (LinearLayout) findViewById(R.id.js_edit_dj_location_lay);
        djlocation = (Button) findViewById(R.id.js_edit_dj_location);
        js_edit_dj_location_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationList.size() > 0) {
                    LocationAlert();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        djrole = (Button) findViewById(R.id.js_edit_dj_role);
        js_edit_dj_role_lay = (LinearLayout) findViewById(R.id.js_edit_dj_role_lay);
        djindustry = (Button) findViewById(R.id.js_dj_edit_industry);
        js_dj_edit_industry_lay = (LinearLayout) findViewById(R.id.js_dj_edit_industry_lay);
        djsalary = (Button) findViewById(R.id.js_dj_edit_salary);
        js_dj_edit_salary_lay = (LinearLayout) findViewById(R.id
                .js_dj_edit_salary_lay);
        djjobtype = (Button) findViewById(R.id.js_dj_edit_jobtype);
        js_dj_edit_jobtype_lay = (LinearLayout) findViewById(R.id.js_dj_edit_jobtype_lay);
        try {
            JSONObject responseObj = new JSONObject(GlobalData.personalresponse);
            Gson gson = new Gson();
            if (GlobalData.personalresponse != null
                    && !GlobalData.personalresponse.isEmpty()) {
                DesiredJob dj = gson.fromJson(
                        responseObj.getString("desired_view"),
                        new TypeToken<DesiredJob>() {
                        }.getType());
                if (dj != null) {
                    getLocation = dj.getLocation();
                    if (getLocation != null && !getLocation.isEmpty() && !getLocation.equalsIgnoreCase(getString(R.string.select))) {
                        djlocation.setText(getLocation);
                    } else {
                        djlocation.setText(R.string.select);
                    }
                    getRole = dj.getRole();
                    if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                        djrole.setText(getRole);
                    } else {
                        djrole.setText(R.string.selectrole);
                    }
                    getIndustry = dj.getIndustry();
                    if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                        djindustry.setText(getIndustry);
                    } else {
                        djindustry.setText(R.string.selectindustry);
                    }
                    getSalary = dj.getSalary();
                    if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                        djsalary.setText(getSalary);
                    } else {
                        djsalary.setText(R.string.selectsalary);
                    }
                    getJobType = dj.getJob_type();
                    if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                        djjobtype.setText(getJobType);
                    } else {
                        djjobtype.setText(R.string.selectjobtype);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getLocation().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        js_dj_edit_salary_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_salary != null && select_salary.size() > 0) {
                    SalaryAlert();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        Button submit = (Button) findViewById(R.id.js_try_DJ_EDIT_Submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    if (desireValidation()) {
                        new updateDesireJob().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            private boolean desireValidation() {
                if (getLocation == null || getLocation.isEmpty() || getLocation.equalsIgnoreCase(getString(R.string.select))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.locationvalidation),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (getRole == null || getRole.isEmpty() || getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.jobrolevalidation),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (getIndustry == null || getIndustry.isEmpty() || getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.industryvalidation),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (getSalary == null || getSalary.isEmpty() || getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.desiredsalaryvalidation),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (getJobType == null || getJobType.isEmpty() || getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.jobtypevalidation),
                            Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
                if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
    }

    private class updateDesireJob extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_DesiredJob.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("location", getLocation);
            paramsadd.addFormDataPart("role", getRole);
            paramsadd.addFormDataPart("salary", getSalary);
            paramsadd.addFormDataPart("job_type", getJobType);
            paramsadd.addFormDataPart("industry", getIndustry);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "desired_job_save.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                randomkeysendresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (randomkeysendresponse != null
                    && !randomkeysendresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(randomkeysendresponse);
                    int getStatusCode = responseObj.getInt("status_code");
                    String getStatus = responseObj.getString("status");
                    Toast.makeText(getBaseContext(), getStatus,
                            Toast.LENGTH_SHORT).show();
                    if (getStatusCode == 1) {
                        finish();
                    }
                    if (getStatusCode == 2) {
                        finish();
                    }
                    /*if (getStatus.equalsIgnoreCase("status")) {
                            Toast.makeText(getBaseContext(), getStatus,
                                    Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (getStatus.equalsIgnoreCase("status")) {
                            Toast.makeText(getBaseContext(), getStatus,
                                    Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                            Toast.makeText(getBaseContext(), getStatus,
                                    Toast.LENGTH_SHORT).show();
                    }*/
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class getLocation extends AsyncTask<String, String, String> {
        String desiredresponse;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_DesiredJob.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            if (!languages.equalsIgnoreCase("English")) {
                MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                        .FORM);
                paramsadd.addFormDataPart("languages", languages);
                MultipartBody requestBody = paramsadd.build();
                request = new Request.Builder().url(GlobalData.url + "jobseeker_desiredjob_list.php").post
                        (requestBody).build();
            } else {
                request = new Request.Builder().url(
                        GlobalData.url + "jobseeker_desiredjob_list.php").build();
            }
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                desiredresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (desiredresponse != null
                    && !desiredresponse.contains("connectionFailure")) {
                try {
                    LocationList = new ArrayList<>();
                    Gson gson = new Gson();
                    JSONObject responseObj = new JSONObject(desiredresponse);
                    locationCityList = new ArrayList<>();
                    locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    LocationList = gson.fromJson(
                            responseObj.getString("locations"),
                            new TypeToken<ArrayList<FilterLocation>>() {
                            }.getType());
                    //role
                    roleList = new ArrayList<>();
                    roleList = gson.fromJson(responseObj.getString("role_name"), new
                            TypeToken<ArrayList<Role>>() {
                            }.getType());
                    js_edit_dj_role_lay
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RoleAlert();
                                }
                            });
                    //industry
                    industriesList = new ArrayList<>();
                    industriesList = gson.fromJson(responseObj.getString("industries"), new
                            TypeToken<ArrayList<Industry>>() {
                            }.getType());
                    js_dj_edit_industry_lay
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    IndustryAlert();
                                }
                            });
                    //job types
                    jobtypeList = new ArrayList<>();
                    jobtypeList = gson.fromJson(responseObj.getString("jobtypes"), new
                            TypeToken<ArrayList<JobType>>() {
                            }.getType());
                    js_dj_edit_jobtype_lay
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    JobTypeAlert();
                                }
                            });
                    //salary
                    select_salary = new ArrayList<>();
                    JSONArray salarygroup = responseObj.getJSONArray("salaryrange");
                    for (int i = 0; i < salarygroup.length(); i++) {
                        JSONObject salary = salarygroup.getJSONObject(i);
                        String salary_name = salary.getString("salaryrange");
                        select_salary.add(salary_name);
                    }
                } catch (Exception ignored) {
                }
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (getLocation != null && !getLocation.isEmpty() && !getLocation.equalsIgnoreCase(getString(R.string.select)) &&
                    MainlocationCityList != null && MainlocationCityList.size() > 0) {
                djlocation.setText(getLocation);
                if (!languages.equalsIgnoreCase("English")) {
                    setLocationLocalLang();
                }
            } else {
                djlocation.setText(R.string.select);
            }
            if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))
                    && roleList != null && roleList.size() > 0) {
                djrole.setText(getRole);
                if (!languages.equalsIgnoreCase("English")) {
                    setRoleLocalLang();
                }
            } else {
                djrole.setText(R.string.selectrole);
            }
            if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))
                    && industriesList != null && industriesList.size() > 0) {
                djindustry.setText(getIndustry);
                if (!languages.equalsIgnoreCase("English")) {
                    setIndustryLocalLang();
                }
            } else {
                djindustry.setText(R.string.selectindustry);
            }
            if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))
                    && jobtypeList != null && jobtypeList.size() > 0) {
                djjobtype.setText(getJobType);
                if (!languages.equalsIgnoreCase("English")) {
                    setJobTypeLocalLang();
                }
            } else {
                djjobtype.setText(R.string.selectjobtype);
            }
        }
    }

    //select job role from desired job location page
    private void RoleAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_DesiredJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_DesiredJob.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getRole != null && !getRole.isEmpty()) {
            indexrole = -1;
            if (roleList != null && roleList.size() > 0) {
                Role localrole = new Role();
                localrole.setRole_name(getRole);
                indexrole = roleList.indexOf(localrole);
            }
        } else {
            indexrole = -1;
        }
        roleadapter = new ArrayAdapter<Role>(this, R.layout.spinner_item_text, roleList) {
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
                String yourValue = roleList.get(position).getRole_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = roleList.get(position).getRole_name_local();
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
        filterrole.setAdapter(roleadapter);
        filterrole.setSelection(indexrole);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
            }
        });
        filterrole.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexrole != -1 && (indexrole == position)) {
                    getRole = getString(R.string.selectrole);
                    indexrole = -1;
                } else {
                    indexrole = position;
                    getRole = roleList.get(position).getRole_name();
                }
                setRole();
                roleadapter.notifyDataSetChanged();
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
        if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            djrole.setText(getRole);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang();
            }
        } else {
            djrole.setText(getString(R.string.selectrole));
        }
    }

    private void setRoleLocalLang() {
        Role localrole = new Role();
        localrole.setRole_name(getRole);
        int indexrole = roleList.indexOf(localrole);
        String RoleLocal = roleList.get(indexrole).getRole_name_local();
        djrole.setText(RoleLocal);
    }

    //select industry from desired job page
    private void IndustryAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_DesiredJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_DesiredJob.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectindustry);
        Button industrydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterindsutry = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            indexindustry = -1;
            if (industriesList != null && industriesList.size() > 0) {
                Industry localindustry = new Industry();
                localindustry.setIndustry_name(getIndustry);
                indexindustry = industriesList.indexOf(localindustry);
            }
        } else {
            indexindustry = -1;
        }
        industryAdapter = new ArrayAdapter<Industry>(this, R.layout.spinner_item_text, industriesList) {
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
        filterindsutry.setAdapter(industryAdapter);
        filterindsutry.setSelection(indexindustry);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
        filterindsutry.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexindustry != -1 && (indexindustry == position)) {
                    getIndustry = getString(R.string.selectindustry);
                    indexindustry = -1;
                } else {
                    indexindustry = position;
                    getIndustry = industriesList.get(position).getIndustry_name();
                }
                setIndustry();
                industryAdapter.notifyDataSetChanged();
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
            djindustry.setText(getIndustry);
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang();
            }
        } else {
            djindustry.setText(getString(R.string.selectindustry));
        }
    }

    private void setIndustryLocalLang() {
        Industry localindustry = new Industry();
        localindustry.setIndustry_name(getIndustry);
        indexindustry = industriesList.indexOf(localindustry);
        String IndustryLocal = industriesList.get(indexindustry).getIndustry_name_local();
        djindustry.setText(IndustryLocal);
    }

    //select salary from desired job page
    private void SalaryAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_DesiredJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_DesiredJob.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectsalary);
        Button salarydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtersalary = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSalary != null && !getSalary.isEmpty() && !getSalary.equalsIgnoreCase(getString(R.string.selectsalary))) {
            indexsalary = -1;
            for (int i = 0; i < select_salary.size(); i++) {
                if (select_salary.get(i).equals(getSalary)) {
                    indexsalary = i;
                }
            }
        } else {
            indexsalary = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_salary) {
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
                String yourValue = select_salary.get(position);
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
                    getSalary = select_salary.get(position);
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
            djsalary.setText(getSalary);
        } else {
            djsalary.setText(getString(R.string.selectsalary));
        }
    }

    //select jobtype from desired job page
    private void JobTypeAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_DesiredJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_DesiredJob.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectjobtype);
        Button jobtypedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterjobtype = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getJobType != null && !getJobType.isEmpty() && !getJobType.equalsIgnoreCase(getString(R.string.selectjobtype))) {
            indexjobtype = -1;
            if (jobtypeList != null && jobtypeList.size() > 0) {
                JobType localjobtype = new JobType();
                localjobtype.setJob_type_name(getJobType);
                indexjobtype = jobtypeList.indexOf(localjobtype);
            }
        } else {
            indexjobtype = -1;
        }
        jobtypeadapter = new ArrayAdapter<JobType>(this, R.layout.spinner_item_text, jobtypeList) {
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
                String yourValue = jobtypeList.get(position).getJob_type_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = jobtypeList.get(position).getJob_type_name_local();
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
        filterjobtype.setAdapter(jobtypeadapter);
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
                    getJobType = jobtypeList.get(position).getJob_type_name();
                }
                setJobType();
                jobtypeadapter.notifyDataSetChanged();
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
            djjobtype.setText(getJobType);
            if (!languages.equalsIgnoreCase("English")) {
                setJobTypeLocalLang();
            }
        } else {
            djjobtype.setText(getString(R.string.selectjobtype));
        }
    }

    private void setJobTypeLocalLang() {
        JobType localjobtype = new JobType();
        localjobtype.setJob_type_name(getJobType);
        indexjobtype = jobtypeList.indexOf(localjobtype);
        String JobTypeLocal = jobtypeList.get(indexjobtype).getJob_type_name_local();
        djjobtype.setText(JobTypeLocal);
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
        cadapter = new ArrayAdapter<City>(MyProfile_DesiredJob.this, R.layout.filter_listrow,
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
            djlocation.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
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
                djlocation.setText(getLocation);
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
            djlocation.setText(R.string.select);
        }
    }

    private void getCityAdapter() {
        if (CityList.size() > 0) {
            final ArrayList<City> getcityforstate = new ArrayList<>();
            getcityforstate.addAll(CityList);
            loccityAdapter = new CityAdapter(MyProfile_DesiredJob.this, R.layout.spinner_item_text, getcityforstate) {
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
                        djlocation.setText(getLocation);
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
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(MyProfile_DesiredJob.this, R.layout
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
                if (getLocation != null && !getLocation.isEmpty() && !getLocation.equalsIgnoreCase(getString(R.string.select))) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(getLocation);
                    djlocation.setText(getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
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
                    djlocation.setText(R.string.select);
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

    private void LocationAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_DesiredJob.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_DesiredJob.this, R.layout.location_filter_popup, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        locstate_view = (LinearLayout) emppromptView.findViewById(R.id.locstate_view);
        city_view = (LinearLayout) emppromptView.findViewById(R.id.city_view);
        loc_popupheader = (TextView) emppromptView.findViewById(R.id.loc_popupheader);
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
        if (getLocation != null && !getLocation.isEmpty() && !getLocation.equalsIgnoreCase(getString(R.string.select))) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            djlocation.setText(getLocation);
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
            djlocation.setText(R.string.select);
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
            final CityAdapter loccityAdapter = new CityAdapter(MyProfile_DesiredJob.this, R.layout.spinner_item_text, locationCityList) {
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
                djlocation.setText(R.string.select);
                alertDialog.dismiss();
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
        if (getLocation != null && !getLocation.isEmpty() && !getLocation.equalsIgnoreCase(getString(R.string.select))) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(getLocation);
            djlocation.setText(getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
                setLocationLocalLang();
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            djlocation.setText(R.string.select);
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
        djlocation.setText(getLocationTamil);
    }
}