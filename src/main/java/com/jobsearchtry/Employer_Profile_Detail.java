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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.DisplayToastMessage;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Employer;

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

/*In this class we can view|edit the details of employer. Employer can possible to set the
 time for contacting the jobseeker(Employer - My Profile).IF the profile status is active,
 we can schedule the time,else the jobseeker call us functionality is hidden*/
public class Employer_Profile_Detail extends Activity {
    private TextView companame, email, industry, location, phoneno, contactperson, emp_per_gettime,
            emp_per_getdays, emp_days, emp_time;
    private String getStatus, getCallStatus, getFromTime, getToTime, getFromTimeAM, getToTomeAM, languages;
    private ToggleButton showmyprofile;
    private ProgressDialog pg;
    private OkHttpClient client = null;
    private final String[] select_fromm = {"1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM",
            "7:00 AM", "8:00 AM", "9:00 AM",
            "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM",
            "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM",
            "12:00" +
                    " " +
                    "AM"};
    private ArrayList<String> selected_fromampm;
    private LinearLayout calluslay, emp_dayslayout,email_lay;
    private int indexfromtime = -1, indextotime = -1;
    private ArrayAdapter<String> fromtimeAdapter, totimeAdapter;
    private ArrayList<String> selectedDays = new ArrayList<>();

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Employer_Profile_Detail.this,
                EmployerDashboard.class));
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_profile_detail);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        //get the company id from session
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        showmyprofile = (ToggleButton) findViewById(R.id.d_e_s_switch_showmyprofile);
        emp_per_gettime = (TextView) findViewById(R.id.emp_per_gettime);
        emp_per_getdays = (TextView) findViewById(R.id.emp_per_getdays);
        calluslay = (LinearLayout) findViewById(R.id.calluslay);
        emp_dayslayout = (LinearLayout) findViewById(R.id.emp_dayslayout);
        ImageButton changetime = (ImageButton) findViewById(R.id.emp_personal_settime_editicon);
        //add the timing from 1AM to 24 PM
        selected_fromampm = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            if (i < 12) {
                selected_fromampm.add(Integer.toString(i));
            } else {
                selected_fromampm.add(Integer.toString(i));
            }
        }
        changetime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changetimepopup();
            }
        });
        //header logo clicking go to dashboard page
        ImageButton emp_pro_header = (ImageButton) findViewById(R.id.js_r_h);
        emp_pro_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Employer_Profile_Detail.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Employer_Profile_Detail.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        //go to the employer detail edit page when edit icon clicking
        ImageButton emp_personal_editicon = (ImageButton) findViewById(R.id.emp_personal_editicon);
        emp_personal_editicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Employer_Profile_Detail.this,
                        Employer_Profile.class));
            }
        });
        companame = (TextView) findViewById(R.id.d_emp_u_r_companyname);
        email = (TextView) findViewById(R.id.d_emp_u_r_emailaddress);
        email_lay = (LinearLayout) findViewById(R.id.email_lay);
        phoneno = (TextView) findViewById(R.id.d_emp_u_r_phonenumber);
        contactperson = (TextView) findViewById(R.id.d_emp_u_r_contactperson);
        industry = (TextView) findViewById(R.id.d_emp_u_r_industry);
        location = (TextView) findViewById(R.id.d_emp_u_r_location);
        emp_days = (TextView) findViewById(R.id.emp_days);
        emp_time = (TextView) findViewById(R.id.emp_time);
    }

    private void changetimepopup() {
        final Dialog alertDialog = new Dialog(Employer_Profile_Detail.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Employer_Profile_Detail.this, R.layout.callsetting, null);
        RadioGroup emp_settime_call = (RadioGroup) emppromptView.findViewById(R.id
                .emp_settime_call);
        RadioButton anytime = (RadioButton) emppromptView.findViewById(R.id.emp_anytime);
        LinearLayout emp_time_from_lay = (LinearLayout) emppromptView.findViewById(R.id
                .emp_time_from_lay);
        LinearLayout emp_time_to_lay = (LinearLayout) emppromptView.findViewById(R.id
                .emp_time_to_lay);
        final LinearLayout specitimelay = (LinearLayout) emppromptView.findViewById(R.id
                .specftimelayy);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_callset);
        final Button emp_time_to = (Button) emppromptView.findViewById(R.id.emp_time_to);
        final Button emp_time_from = (Button) emppromptView.findViewById(R.id.emp_time_from);
        Button sabetime = (Button) emppromptView.findViewById(R.id.emp_profille_time_Submit);
        RadioButton specifictime = (RadioButton) emppromptView.findViewById(R.id.emp_specifictime);
        final CheckBox monday = (CheckBox) emppromptView.findViewById(R.id.call_monday);
        final CheckBox tuesday = (CheckBox) emppromptView.findViewById(R.id.call_tuesday);
        final CheckBox wednesday = (CheckBox) emppromptView.findViewById(R.id.call_wed);
        final CheckBox thursday = (CheckBox) emppromptView.findViewById(R.id.call_thurs);
        final CheckBox friday = (CheckBox) emppromptView.findViewById(R.id.call_fri);
        final CheckBox saturday = (CheckBox) emppromptView.findViewById(R.id.call_sat);
        final CheckBox sunday = (CheckBox) emppromptView.findViewById(R.id.call_sun);
        if (selectedDays.contains("Monday")) {
            monday.setChecked(true);
        }
        if (selectedDays.contains("Tuesday")) {
            tuesday.setChecked(true);
        }
        if (selectedDays.contains("Wednesday")) {
            wednesday.setChecked(true);
        }
        if (selectedDays.contains("Thursday")) {
            thursday.setChecked(true);
        }
        if (selectedDays.contains("Friday")) {
            friday.setChecked(true);
        }
        if (selectedDays.contains("Saturday")) {
            saturday.setChecked(true);
        }
        if (selectedDays.contains("Sunday")) {
            sunday.setChecked(true);
        }
        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (monday.isChecked() && !selectedDays.contains("Monday")) {
                    selectedDays.add("Monday");
                } else {
                    selectedDays.remove("Monday");
                }
            }
        });
        tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (tuesday.isChecked() && !selectedDays.contains("Tuesday")) {
                    selectedDays.add("Tuesday");
                } else {
                    selectedDays.remove("Tuesday");
                }
            }
        });
        wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (wednesday.isChecked() && !selectedDays.contains("Wednesday")) {
                    selectedDays.add("Wednesday");
                } else {
                    selectedDays.remove("Wednesday");
                }
            }
        });
        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (thursday.isChecked() && !selectedDays.contains("Thursday")) {
                    selectedDays.add("Thursday");
                } else {
                    selectedDays.remove("Thursday");
                }
            }
        });
        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (friday.isChecked() && !selectedDays.contains("Friday")) {
                    selectedDays.add("Friday");
                } else {
                    selectedDays.remove("Friday");
                }
            }
        });
        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (saturday.isChecked() && !selectedDays.contains("Saturday")) {
                    selectedDays.add("Saturday");
                } else {
                    selectedDays.remove("Saturday");
                }
            }
        });
        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (sunday.isChecked() && !selectedDays.contains("Sunday")) {
                    selectedDays.add("Sunday");
                } else {
                    selectedDays.remove("Sunday");
                }
            }
        });
        emp_time_from_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFromAlert(emp_time_from);
            }
        });
        emp_time_to_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timeToAlert(emp_time_to);
            }
        });
        if (getCallStatus.equals("Y")) {
            anytime.setChecked(false);
            specifictime.setChecked(true);
            specitimelay.setVisibility(View.VISIBLE);
            if (getToTomeAM != null && !getToTomeAM.isEmpty() && !getToTomeAM.equalsIgnoreCase(getString(R.string.time))) {
                emp_time_to.setText(getToTomeAM);
            } else {
                emp_time_to.setText(getString(R.string.time));
            }
            if (getFromTimeAM != null && !getFromTimeAM.isEmpty() && !getFromTimeAM.equalsIgnoreCase(getString(R.string.time))) {
                emp_time_from.setText(getFromTimeAM);
            } else {
                emp_time_from.setText(getString(R.string.time));
            }
        } else {
            //showmyproefile active and selecting any time means hide the time selection view.
            getFromTime = "";
            getToTime = "";
            getFromTimeAM = "";
            getToTomeAM = "";
            anytime.setChecked(true);
            specifictime.setChecked(false);
            specitimelay.setVisibility(View.GONE);
        }

        //radio group(from/to) changes
        emp_settime_call.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.emp_anytime:
                        getCallStatus = "N";
                        getFromTime = "";
                        getFromTimeAM = "";
                        getToTime = "";
                        getToTomeAM = "";
                        specitimelay.setVisibility(View.GONE);
                        break;
                    case R.id.emp_specifictime:
                        getCallStatus = "Y";
                        emp_time_to.setText(getString(R.string.to));
                        emp_time_from.setText(getString(R.string.from));
                        specitimelay.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifiedTime()) {
                    new ShowMyProfileTimeUpdate().execute();
                }
            }
        });
        sabetime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifiedTime()) {
                    new ShowMyProfileTimeUpdate().execute();
                }
            }
        });
    }

    //select from time from call setting
    private void timeFromAlert(final Button emp_time_from) {
        final Dialog alertDialog = new Dialog(Employer_Profile_Detail.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Employer_Profile_Detail.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.from);
        Button fromtimedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterfromtime = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getFromTimeAM != null && !getFromTimeAM.isEmpty() && !getFromTimeAM.equalsIgnoreCase(getString(R.string.from))) {
            indexfromtime = -1;
            for (int i = 0; i < select_fromm.length; i++) {
                if (select_fromm[i].equals(getFromTimeAM)) {
                    indexfromtime = i;
                }
            }
        } else {
            indexfromtime = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_fromm) {
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
                String yourValue = select_fromm[position];
                if (indexfromtime != -1 && (indexfromtime == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterfromtime.setAdapter(adapter);
        filterfromtime.setSelection(indexfromtime);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFromTime(emp_time_from);
                alertDialog.dismiss();
            }
        });
        filterfromtime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexfromtime != -1 && (indexfromtime == position)) {
                    getFromTimeAM = getString(R.string.time);
                    indexfromtime = -1;
                } else {
                    indexfromtime = position;
                    getFromTimeAM = select_fromm[position];
                    if (getFromTimeAM.equals(select_fromm[position])) {
                        getFromTime = selected_fromampm.get(position);
                    }
                }
                setFromTime(emp_time_from);
                adapter.notifyDataSetChanged();
            }
        });
        fromtimedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFromTime(emp_time_from);
                alertDialog.dismiss();
            }
        });
    }

    private void setFromTime(Button emp_time_from) {
        if (getFromTimeAM != null && !getFromTimeAM.isEmpty() && !getFromTimeAM.equalsIgnoreCase(getString(R.string.from))) {
            emp_time_from.setText(getFromTimeAM);
        } else {
            emp_time_from.setText(getString(R.string.from));
        }
    }

    //select to time from call setting
    private void timeToAlert(final Button emp_time_to) {
        final Dialog alertDialog = new Dialog(Employer_Profile_Detail.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Employer_Profile_Detail.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.to);
        Button totimedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtertotime = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getToTomeAM != null && !getToTomeAM.isEmpty() && !getToTomeAM.equalsIgnoreCase(getString(R.string.to))) {
            indextotime = -1;
            for (int i = 0; i < select_fromm.length; i++) {
                if (select_fromm[i].equals(getToTomeAM)) {
                    indextotime = i;
                }
            }
        } else {
            indextotime = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_fromm) {
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
                String yourValue = select_fromm[position];
                if (indextotime != -1 && (indextotime == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtertotime.setAdapter(adapter);
        filtertotime.setSelection(indextotime);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setToTime(emp_time_to);
                alertDialog.dismiss();
            }
        });
        filtertotime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indextotime != -1 && (indextotime == position)) {
                    getToTomeAM = getString(R.string.to);
                    indextotime = -1;
                } else {
                    indextotime = position;
                    getToTomeAM = select_fromm[position];
                    if (getToTomeAM.equals(select_fromm[position])) {
                        getToTime = selected_fromampm.get(position);
                    }
                }
                setToTime(emp_time_to);
                adapter.notifyDataSetChanged();
            }
        });
        totimedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setToTime(emp_time_to);
                alertDialog.dismiss();
            }
        });
    }

    private void setToTime(Button emp_time_to) {
        if (getToTomeAM != null && !getToTomeAM.isEmpty() && !getToTomeAM.equalsIgnoreCase(getString(R.string.to))) {
            emp_time_to.setText(getToTomeAM);
        } else {
            emp_time_to.setText(getString(R.string.to));
        }
    }

    private boolean verifiedTime() {
        //validation of this page
        if (getCallStatus != null && !getCallStatus.isEmpty() && getCallStatus.equalsIgnoreCase
                ("Y")) {
            if (getFromTimeAM != null && !getFromTimeAM.isEmpty() && !getFromTimeAM.equalsIgnoreCase(getString(R.string.to))) {
                if (getToTomeAM != null && !getToTomeAM.isEmpty() && !getToTomeAM.equalsIgnoreCase(getString(R.string.to))) {
                    if (Integer.parseInt(getFromTime) > Integer.parseInt(getToTime)) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.fromtotime),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.totime),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.fromtime),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //resume-get the detail of the employer from database
    @Override
    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getEmployerDetail().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //get the details of employer webservices
    private class getEmployerDetail extends AsyncTask<String, String, String> {
        String empdetailresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Employer_Profile_Detail.this,
                    R.style.MyTheme);
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
            paramsadd.addFormDataPart("action", "EmpProfileView");
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_View_update.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                empdetailresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (empdetailresponse != null
                    && !empdetailresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(empdetailresponse);
                    Gson gson = new Gson();
                    final Employer employer = gson.fromJson(
                            responseObj.getString("list"),
                            new TypeToken<Employer>() {
                            }.getType());
                    companame.setText(employer.getCompanyName());
                    String getEmail = employer.getEmailId();
                    if (!(getEmail !=null && getEmail.isEmpty())) {
                        email_lay.setVisibility(View.VISIBLE);
                        email.setText(employer.getEmailId());
                    }
                    else {
                        email_lay.setVisibility(View.GONE);
                    }
                    industry.setText(employer.getIndustry());
                    location.setText(employer.getLocation());
                    phoneno.setText(employer.getMobile());
                    contactperson.setText(employer.getContactPerson());
                    GlobalData.empusername = employer.getCompanyName();
                    if (!languages.equalsIgnoreCase("English")) {
                        if (!responseObj.getString("location_local").isEmpty() && responseObj.getString("location_local") != null) {
                            location.setText(responseObj.getString("location_local"));
                        }
                        if (!responseObj.getString("emp_industry_local").isEmpty() && responseObj.getString("emp_industry_local") != null) {
                            industry.setText(responseObj.getString("emp_industry_local"));
                        }
                    }
                    getStatus = employer.getShowMyPhone();
                    selectedDays = new ArrayList<>();
                    if (responseObj.getString("specificdays") != "null") {
                        emp_per_getdays.setText(responseObj.getString("specificdays"));
                        if (!languages.equalsIgnoreCase("English")) {
                            if (!responseObj.getString("emp_specificdays_local").isEmpty() && responseObj.getString("emp_specificdays_local") != null) {
                                emp_per_getdays.setText(responseObj.getString("emp_specificdays_local"));
                            }
                        }
                        emp_dayslayout.setVisibility(View.VISIBLE);
                        List<String> dayslist = Arrays.asList(responseObj.getString("specificdays").split(","));
                        for (int i = 0; i < dayslist.size(); i++) {
                            if (!(selectedDays.contains(dayslist.get(i)))) {
                                selectedDays.add(dayslist.get(i));
                            }
                        }
                    } else {
                        emp_dayslayout.setVisibility(View.GONE);
                    }
                    //if get status = 0 ,show my profile is off and job seeker call us view is
                    // h
                    // idden,else show my profile is on and call us view is visible, we can set
                    // the time for contact the jobseeker
                    final int valuemax = (int) getResources().getDimension(R.dimen.buttonHeightToSmall);
                    final int valuemin = (int) getResources().getDimension(R.dimen.margintop);
                    if (getStatus.equalsIgnoreCase("0")) {
                        calluslay.setVisibility(View.GONE);
                        emp_time.setVisibility(View.GONE);
                        emp_per_gettime.setText(R.string.anytime);
                        showmyprofile.setChecked(false);
                        showmyprofile.setTextOff("No");
                        showmyprofile.setTextOn("Yes");
                        showmyprofile.setPadding(valuemax, 0, valuemin, 0);
                        getCallStatus = "N";
                    } else {
                        getCallStatus = employer.getTime_status();
                        if (getCallStatus.equals("Y")) {
                            //showmyproefile active and specific time means get the time and
                            // show it
                            if (!employer.getSettime().isEmpty()) {
                                String time = employer.getSettime();
                                settime(time);
                            }
                        } else {
                            emp_per_gettime.setText(R.string.anytime);
                        }
                        calluslay.setVisibility(View.VISIBLE);
                        emp_time.setVisibility(View.VISIBLE);
                        showmyprofile.setChecked(true);
                        showmyprofile.setTextOff("No");
                        showmyprofile.setTextOn("Yes");
                        showmyprofile.setPadding(valuemin, 0, valuemax, 0);
                    }
                    //toggle the showmyprofile active/inactive and the status will be updating to
                    // database
                    showmyprofile
                            .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton arg0, boolean isChecked) {
                                    if (showmyprofile.isChecked()) {
                                        showmyprofile.setPadding(valuemin, 0, valuemax, 0);
                                        showmyprofile.setTextOff("No");
                                        showmyprofile.setTextOn("Yes");
                                        getCallStatus = employer.getTime_status();
                                        if (getCallStatus.equals("Y")) {
                                            //showmyproefile active and specific time means get
                                            // the time and show it
                                            if (!employer.getSettime().isEmpty()) {
                                                String time = employer.getSettime();
                                                settime(time);
                                            }
                                        } else {
                                            emp_per_gettime.setText(R.string.anytime);
                                        }
                                        calluslay.setVisibility(View.VISIBLE);
                                    } else {
                                        showmyprofile.setTextOff("No");
                                        showmyprofile.setTextOn("Yes");
                                        showmyprofile.setPadding(valuemax, 0, valuemin, 0);
                                        getCallStatus = "N";
                                        emp_per_gettime.setText(R.string.anytime);
                                        calluslay.setVisibility(View.GONE);
                                    }
                                    if (new UtilService()
                                            .isNetworkAvailable(getApplicationContext())) {
                                        new ShowMyProfileUpdate().execute();
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                getString(R.string.checkconnection),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } catch (Exception e) {
                    Toast.makeText(Employer_Profile_Detail.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Employer_Profile_Detail.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void settime(String time) {
        //showmyproefile active means get the time
        if (getCallStatus.equals("Y")) {
            emp_per_gettime.setText(getFromTimeAM + " - " + getToTomeAM);
            //showmyproefile active and specific time means get the time and show it
            if (!time.isEmpty()) {
                String[] out = time.split("-");
                getFromTimeAM = out[0];
                if (Arrays.asList(select_fromm).contains(getFromTimeAM)) {
                    int frompos = Arrays.asList(select_fromm).indexOf(getFromTimeAM);
                    getFromTime = selected_fromampm.get(frompos);
                }
                getToTomeAM = out[1];
                if (Arrays.asList(select_fromm).contains(getToTomeAM)) {
                    int topos = Arrays.asList(select_fromm).indexOf(getToTomeAM);
                    getToTime = selected_fromampm.get(topos);
                }
                emp_per_gettime.setText(getFromTimeAM + " - " + getToTomeAM);
            } else {
                getFromTime = "1";
                getToTime = "1";
                getFromTimeAM = "1:00 AM";
                getToTomeAM = "1:00 AM";
                emp_per_gettime.setText(getFromTimeAM + " - " + getToTomeAM);
            }
        } else {
            //showmyproefile active and selecting any time means hide the time selection view.
            getFromTime = "1";
            getToTime = "1";
            getFromTimeAM = "1:00 AM";
            getToTomeAM = "1:00 AM";
            emp_per_gettime.setText(R.string.anytime);
        }
    }

    //show my profile active/inactive update to database
    private class ShowMyProfileUpdate extends AsyncTask<String, String, String> {
        String gsonresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Employer_Profile_Detail.this,
                    R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("action", "change_status")
                    .add("company_id", GlobalData.emp_login_status).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "show_phone_employer.php")
                    .post(formBody).build();
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
            if (gsonresponse != null
                    && !gsonresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(gsonresponse);
                    String updateStatus = responseObj.getString("show_phone_no");
                    //if 0 means phoneno visible to jobseeker, 1 means phoneno is now viisble,so
                    // jobseeker doesnot contact to employer
                    if (updateStatus.equalsIgnoreCase("0")) {
                        new DisplayToastMessage().isToastMessage(Employer_Profile_Detail.this,
                                getString(R.string.showmyphonenocant));

                    } else {
                        new DisplayToastMessage().isToastMessage(Employer_Profile_Detail.this,
                                getString(R.string.showmyphoneno));
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //updating a specific(AM/PM) or anytime to database
    private class ShowMyProfileTimeUpdate extends AsyncTask<String, String, String> {
        String gsonresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Employer_Profile_Detail.this,
                    R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            String selecteddays = "";
            if (getCallStatus.equalsIgnoreCase("Y")) {
                String[] getdays = selectedDays.toArray(new
                        String[selectedDays.size()]);
                String getdaysarray = Arrays.toString(getdays);
                selecteddays = getdaysarray.substring(1, getdaysarray
                        .length() - 1);
                selecteddays = selecteddays.replace(", ", ",");
            } else {
                selecteddays = "";
            }
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("time_status", getCallStatus);
            paramsadd.addFormDataPart("settime", getFromTimeAM + "-" + getToTomeAM);
            paramsadd.addFormDataPart("specificdays", selecteddays);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_set_time.php")
                    .post(requestBody).build();
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
            if (gsonresponse != null
                    && !gsonresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(gsonresponse);
                    String message = json.getString("call_toast");
                    int statuscode = json.getInt("status_code");
                    //if the getCallStatus is "N" means anytime , else Specific time
                    if (getCallStatus.equalsIgnoreCase("N")) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.anytimecall),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (statuscode == 1) {
                            new DisplayToastMessage().isToastMessage(Employer_Profile_Detail.this, message);
                        }
                    }
                    startActivity(new Intent(Employer_Profile_Detail.this,
                            Employer_Profile_Detail.class));
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}