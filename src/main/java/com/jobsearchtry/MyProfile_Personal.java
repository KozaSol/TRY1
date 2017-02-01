package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DialogFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.DatePicker.DateWatcher;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.User;
import com.jobsearchtry.utils.BackAlertDialog;

import android.app.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.widget.DatePicker;

public class MyProfile_Personal extends Activity {
    private EditText js_edit_personal_username, js_edit_personal_phonenumber,
            js_edit_personal_emailaddress, js_edit_personal_dob, js_edit_personal_age;
    private String getUsername, getPhoneNo, getEmail, getLocation, getDob, getGender,
            getAge, getLanguages, getJobRole, languages;
    private AutoCompleteTextView js_edit_personal_location;
    private LinearLayout js_edit_personal_role_lay, js_edit_personal_gender_lay;
    private Button js_edit_personal_languages, js_edit_personal_role, js_edit_personal_gender;
    private Dialog mDateTimeDialog;
    private OkHttpClient client = null;
    private ProgressDialog pg;
    private ArrayList<String> languagesList;
    private ArrayList<String> locationList;
    private final ArrayList<String> selectedLanguagesList = new ArrayList<>();
    private ArrayList<Role> select_role = null;
    private ArrayList<Gender> select_gender = null;
    private boolean[] isCheckedLanguages;
    private String[] LanguagesArray;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<Gender> genderadapter;
    private JSONObject json;
    private int indexrole = -1, indexgender = -1;

    @Override
    public void onBackPressed() {
        new BackAlertDialog().isBackDialog(MyProfile_Personal.this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_personal);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS",
                GlobalData.login_status);
        js_edit_personal_username = (EditText) findViewById(R.id.js_edit_personal_username);
        js_edit_personal_phonenumber = (EditText) findViewById(R.id.js_edit_personal_phonenumber);
        js_edit_personal_emailaddress = (EditText) findViewById(R.id
                .js_edit_personal_emailaddress);
        js_edit_personal_location = (AutoCompleteTextView) findViewById(R.id
                .js_edit_personal_location);
        js_edit_personal_gender_lay = (LinearLayout) findViewById(R.id.js_edit_personal_gender_lay);
        js_edit_personal_age = (EditText) findViewById(R.id.js_edit_personal_age);
        js_edit_personal_dob = (EditText) findViewById(R.id.js_edit_personal_dob);
        js_edit_personal_gender = (Button) findViewById(R.id.js_edit_personal_gender);
        js_edit_personal_languages = (Button) findViewById(R.id.js_edit_personal_languages);
        js_edit_personal_role = (Button) findViewById(R.id.js_edit_personal_role);
        if (!GlobalData.personalresponse.isEmpty() && GlobalData.personalresponse != null) {
            try {
                JSONObject responseObj = new JSONObject(GlobalData.personalresponse);
                Gson gson = new Gson();
                User user = gson.fromJson(responseObj.getString("profile_view"),
                        new TypeToken<User>() {
                        }.getType());
                js_edit_personal_username.setText(user.getUserName());
                js_edit_personal_phonenumber.setText(user.getMobile());
                js_edit_personal_emailaddress.setText(user.getEmailId());
                js_edit_personal_location.setText(user.getLocation());
                js_edit_personal_location.setThreshold(1);
                js_edit_personal_age.setText(user.getAge());
                js_edit_personal_dob.setText(user.getDOB());
                getGender = user.getSex();
                getJobRole = user.getJob_Role();
                js_edit_personal_gender.setText(getGender);
                if (!(user.getLanguages().equalsIgnoreCase("-"))) {
                    getLanguages = user.getLanguages();
                }
                js_edit_personal_languages.setText(user.getLanguages());
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                        Toast.LENGTH_SHORT).show();
            }
        }
        ImageButton per_h = (ImageButton) findViewById(R.id.js_r_h);
        per_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_Personal.this,
                        Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackAlertDialog().isBackDialog(MyProfile_Personal.this);
            }
        });
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetLanguages().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        LinearLayout js_edit_personal_languages_lay = (LinearLayout) findViewById(R.id
                .js_edit_personal_languages_lay);
        js_edit_personal_languages_lay
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (languagesList.size() > 0) {
                            LanguagesAlert();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.checkconnection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        js_edit_personal_role_lay = (LinearLayout) findViewById(R.id.js_edit_personal_role_lay);
        Button js_try_Personal_Submit = (Button) findViewById(R.id.js_try_Personal_Submit);
        js_try_Personal_Submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (personal_Validation()) {
                    new updatePersonal().execute();
                }
            }

            private boolean personal_Validation() {
                getPhoneNo = js_edit_personal_phonenumber.getText().toString();
                getUsername = js_edit_personal_username.getText().toString();
                getEmail = js_edit_personal_emailaddress.getText().toString();
                getDob = js_edit_personal_dob.getText().toString();
                getLocation = js_edit_personal_location.getText().toString();
                getAge = js_edit_personal_age.getText().toString();
                if (null == getUsername || getUsername.length() < 3) {
                    Toast.makeText(getApplicationContext(),


                            getString(R.string.youmusthave3mincontactperson),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getJobRole.equals(getString(R.string.selectrole))) {
                    Toast.makeText(getApplicationContext(),

                            getString(R.string.pleaseselecttherole),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (null == getPhoneNo || getPhoneNo.length() < 10) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pleaseenteravalidphno),

                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getEmail.length() > 0) {
                    if (checkEmail(getEmail)) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.pleaseentervalidemail),
                                Toast.LENGTH_LONG)
                                .show();
                        return false;
                    }
                }
                if ((null == getLocation || getLocation.length() == 0)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pleaseentertheLocation),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (!locationList.contains(getLocation)) {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.locationwrongtoast),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getGender.equals(getString(R.string.selectgender))) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pleasethegender),
                            Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (null == getDob || getDob.length() == 0) {
                    if (getDob.equalsIgnoreCase("-")) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.dobtoast),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                if (getLanguages != null && !getLanguages.isEmpty()) {
                    if (getLanguages.equalsIgnoreCase(getString(R.string.languages))) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.pleaseselectlang),
                                Toast.LENGTH_LONG)
                                .show();
                        return false;
                    }
                }
                if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }

            private boolean checkEmail(String email) {
                return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
            }
        });
        js_edit_personal_location.setThreshold(1);
        js_edit_personal_dob.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                getDob = js_edit_personal_dob.getText().toString();
                Bundle args = new Bundle();
                args.putString("key", getDob);
                DialogFragment dialogfragment = new DatePickerDialogClass();
                dialogfragment.setArguments(args);
                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
               /* if (!getDob.isEmpty() && !getDob.equalsIgnoreCase("-")) {
                     popupDOB(getDob);
                 } else {
                   String cgetDob = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    popupDOB(cgetDob);
                }*/
            }
        });
    }

    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int month, year, day;
            Bundle mArgs = getArguments();
            String getDob = mArgs.getString("key");
            if (!getDob.isEmpty() && !getDob.equalsIgnoreCase("-")) {
                String[] out = getDob.split("-");
                month = Integer.parseInt(out[1]) - 1;
                year = Integer.parseInt(out[2]);
                day = Integer.parseInt(out[0]);
            } else {
                String cgetDob = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String[] out = cgetDob.split("-");
                month = Integer.parseInt(out[1]) - 1;
                year = Integer.parseInt(out[2]);
                day = Integer.parseInt(out[0]);
            }
            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.Datepickertheme, this, year, month, day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            //TextView textview = (TextView) getActivity().findViewById(R.id.textView1);
            EditText js_edit_personal_dob = (EditText) getActivity().findViewById(R.id.js_edit_personal_dob);
            js_edit_personal_dob.setText(day + "-" + (month + 1) + "-" + year);
            //int month = c.get(Calendar.MONTH) + 1;
//            String result_string = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-"
//                    + c.get(Calendar.YEAR);
            // js_edit_personal_dob.setText(result_string);
            //int year = c.get(Calendar.YEAR) - 1;
            EditText js_edit_personal_age = (EditText) getActivity().findViewById(R.id.js_edit_personal_age);
            js_edit_personal_age.setText(String.valueOf(calculateMyAge(year, month,
                    day)));
            //  mDateTimeDialog.setTitle(DatePicker.dayName);
        }
    }

    /*private void popupDOB(String dob) {
        mDateTimeDialog = new Dialog(MyProfile_Personal.this);
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.date_time_dialog, null);
        final DatePicker mDateTimePicker = (DatePicker) mDateTimeDialogView
                .findViewById(R.id.DatePicker);
        mDateTimePicker.setDateChangedListener(MyProfile_Personal.this);
        mDateTimePicker.initData(dob);
        Button setDateBtn = (Button) mDateTimeDialogView
                .findViewById(R.id.FM_SetDateTime);
        setDateBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mDateTimePicker.clearFocus();
                mDateTimeDialog.dismiss();
            }
        });
        Button cancelBtn = (Button) mDateTimeDialogView
                .findViewById(R.id.FM_CancelDialog);
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mDateTimePicker.reset();
                mDateTimeDialog.cancel();
            }
        });
        mDateTimeDialog.setTitle(DatePicker.dayName);
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        mDateTimeDialog.show();
    }*/

    private void LanguagesAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_Personal.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_Personal.this, R.layout.filterpopup,
                null);
        TextView f_popupheader = (TextView) emppromptView
                .findViewById(R.id.f_popupheader);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        f_popupheader.setText(R.string.languages);
        final ListView filterrole = (ListView) emppromptView
                .findViewById(R.id.filterlist);
        filterrole.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.filter_listrow,
                languagesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v
                        .findViewById(android.R.id.text1);
                String yourValue = languagesList.get(position);
                textView.setText(yourValue);
                return textView;
            }
        };
        filterrole.setAdapter(adapter);
        if (getLanguages != null && !getLanguages.isEmpty() && !getLanguages.equalsIgnoreCase(getString(R.string.languages))) {
            List<String> rolelist = Arrays.asList(getLanguages.split(","));
            isCheckedLanguages = new boolean[LanguagesArray.length];
            for (int i = 0; i < rolelist.size(); i++) {
                if (!(selectedLanguagesList.contains(rolelist.get(i)))) {
                    selectedLanguagesList.add(rolelist.get(i));
                }
                int indexlang = languagesList.indexOf(rolelist.get(i));
                if (indexlang != -1) {
                    isCheckedLanguages[indexlang] = true;
                    filterrole.setItemChecked(indexlang, true);
                }
            }
        } else {
            isCheckedLanguages = new boolean[LanguagesArray.length];
            Arrays.fill(isCheckedLanguages, false);
        }
        filterrole.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (!isCheckedLanguages[position]) {
                    if (!(selectedLanguagesList.contains(adapter
                            .getItem(position)))) {
                        selectedLanguagesList.add(adapter.getItem(position));
                    }
                } else {
                    selectedLanguagesList.remove(adapter.getItem(position));
                }
            }
        });
        Button done_filter = (Button) emppromptView
                .findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView
                .findViewById(R.id.resetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguages();
                alertDialog.dismiss();
            }
        });
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (selectedLanguagesList.size() > 0) {
                    selectedLanguagesList.clear();
                }
                if (languagesList.size() > 0) {
                    isCheckedLanguages = new boolean[LanguagesArray.length];
                    Arrays.fill(isCheckedLanguages, false);
                }
                getLanguages = null;
                js_edit_personal_languages.setText(R.string.languages);
                alertDialog.dismiss();
                LanguagesAlert();
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
        String[] getRoleaded = selectedLanguagesList
                .toArray(new String[selectedLanguagesList.size()]);
        String getrolearray = Arrays.toString(getRoleaded);
        getLanguages = getrolearray.substring(1,
                getrolearray.length() - 1);
        getLanguages = getLanguages.replace(", ", ",");
        if (getLanguages != null && !getLanguages.isEmpty()) {
            js_edit_personal_languages.setText(getLanguages);
        } else {
            js_edit_personal_languages.setText(R.string.languages);
        }
    }

    /*@Override
    public void onDateChanged(Calendar c) {
        int month = c.get(Calendar.MONTH) + 1;
        String result_string = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-"
                + c.get(Calendar.YEAR);
        js_edit_personal_dob.setText(result_string);
        int year = c.get(Calendar.YEAR) - 1;
        js_edit_personal_age.setText(String.valueOf(calculateMyAge(year, month,
                c.get(Calendar.DAY_OF_MONTH))));
        mDateTimeDialog.setTitle(DatePicker.dayName);
    }*/

    private class updatePersonal extends AsyncTask<String, String, String> {
        String updatepersonalresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_Personal.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            paramsadd.addFormDataPart("Name", getUsername);
            paramsadd.addFormDataPart("NewMobileNo", getPhoneNo);
            paramsadd.addFormDataPart("emailid", getEmail);
            paramsadd.addFormDataPart("Currentlocation", getLocation);
            paramsadd.addFormDataPart("Job_Role", getJobRole);
            paramsadd.addFormDataPart("Gender", getGender);
            paramsadd.addFormDataPart("dob", getDob);
            paramsadd.addFormDataPart("age", getAge);
            paramsadd.addFormDataPart("languages", getLanguages);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "personal_save.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                updatepersonalresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (updatepersonalresponse != null
                    && !updatepersonalresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(updatepersonalresponse);
                    int getStatusCode = json.getInt("status_code");
                    String getStatus = json.getString("status");
                    if (getStatusCode == 1) {
                        GlobalData.mobilenumber = getPhoneNo;
                        GlobalData.username = getUsername;
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(MyProfile_Personal.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("MN", GlobalData.mobilenumber);
                        editor.putString("NAME", GlobalData.username);
                        editor.apply();
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), getStatus,
                            Toast.LENGTH_SHORT).show();
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

    private static int calculateMyAge(int year, int month, int day) {
        Calendar birthCal = new GregorianCalendar(year, month, day);
        Calendar nowCal = new GregorianCalendar();
        int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        boolean isMonthGreater = birthCal.get(Calendar.MONTH) >= nowCal
                .get(Calendar.MONTH);
        boolean isMonthSameButDayGreater = birthCal.get(Calendar.MONTH) == nowCal
                .get(Calendar.MONTH)
                && birthCal.get(Calendar.DAY_OF_MONTH) > nowCal
                .get(Calendar.DAY_OF_MONTH);
        if (isMonthGreater || isMonthSameButDayGreater) {
            age = age - 1;
        }
        return age;
    }

    private class GetLanguages extends AsyncTask<String, String, String> {
        String personalresponse;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(MyProfile_Personal.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            try {
                if (!languages.equalsIgnoreCase("English")) {
                    MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                            .FORM);
                    paramsadd.addFormDataPart("languages", languages);
                    MultipartBody requestBody = paramsadd.build();
                    request = new Request.Builder().url(GlobalData.url + "jobseeker_personallist.php").post
                            (requestBody).build();
                } else {
                    request = new Request.Builder().url(
                            GlobalData.url + "jobseeker_personallist.php").build();
                }
                client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    personalresponse = response.body().string();
                    json = new JSONObject(personalresponse);
                } catch (IOException ignored) {
                }
            } catch (JSONException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (personalresponse != null && !personalresponse.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    languagesList = new ArrayList<>();
                    JSONArray groups = json.getJSONArray("language_name");
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject c = groups.getJSONObject(i);
                        String occupations_list_name = c
                                .getString("language_name");
                        languagesList.add(occupations_list_name);
                    }
                    LanguagesArray = languagesList
                            .toArray(new String[languagesList.size()]);
                    if (getLanguages != null && !getLanguages.isEmpty() && !getLanguages.equalsIgnoreCase(getString(R.string.languages))) {
                        List<String> langlist = Arrays.asList(getLanguages
                                .split(","));
                        isCheckedLanguages = new boolean[LanguagesArray.length];
                        for (int i = 0; i < langlist.size(); i++) {
                            selectedLanguagesList.add(langlist.get(i));
                            int indexlang = languagesList.indexOf(langlist
                                    .get(i));
                            if (indexlang != -1) {
                                isCheckedLanguages[indexlang] = true;
                            }
                        }
                    }
                    locationList = new ArrayList<>();
                    JSONArray langgroups = json.getJSONArray("locations");
                    for (int i = 0; i < langgroups.length(); i++) {
                        JSONObject c = langgroups.getJSONObject(i);
                        String occupations_list_name = c.getString("citi_name");
                        locationList.add(occupations_list_name);
                    }
                    //role
                    select_role = new ArrayList<>();
                    select_role = gson.fromJson(json.getString("role_name"), new
                            TypeToken<ArrayList<Role>>() {
                            }.getType());
                    //gender
                    select_gender = new ArrayList<>();
                    select_gender = gson.fromJson(json.getString("genderlist"), new
                            TypeToken<ArrayList<Gender>>() {
                            }.getType());
                    if (select_gender != null && select_gender.size() > 0) {
                        Gender anygender = new Gender();
                        anygender.setGender("Any");
                        int removegender = select_gender.indexOf(anygender);
                        select_gender.remove(removegender);
                    }
                } catch (JSONException ignored) {
                }
                if (!getJobRole.isEmpty() && getJobRole != null && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                    js_edit_personal_role.setText(getJobRole);
                    if (!languages.equalsIgnoreCase("English")) {
                        setRoleLocalLang();
                    }
                } else {
                    js_edit_personal_role.setText(getString(R.string.selectrole));
                }
                if (!getGender.isEmpty() && getGender != null && !getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
                    js_edit_personal_gender.setText(getGender);
                    if (!languages.equalsIgnoreCase("English")) {
                        setGenderLocalLang();
                    }
                } else {
                    js_edit_personal_gender.setText(getString(R.string.selectgender));
                }
                if (getLanguages != null && !getLanguages.isEmpty() && !getLanguages.equalsIgnoreCase(getString(R.string.languages))) {
                    js_edit_personal_languages.setText(getLanguages);
                } else {
                    js_edit_personal_languages.setText(R.string.languages);
                }
                ArrayAdapter<String> locAdapter = new ArrayAdapter<>(
                        MyProfile_Personal.this, R.layout.spinner_item_text,
                        locationList);
                js_edit_personal_location.setAdapter(locAdapter);
                js_edit_personal_role_lay
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (select_role != null && select_role.size() > 0) {
                                    RoleAlert();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.checkconnection),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                js_edit_personal_gender_lay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (select_gender != null && select_gender.size() > 0) {
                            GenderAlert();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.checkconnection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }

    //select gender from job seeker personal editing page
    private void GenderAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_Personal.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_Personal.this, R.layout.spinner, null);
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
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setGender();
                alertDialog.dismiss();
            }
        });
        filtergender.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
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
        if (!getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            js_edit_personal_gender.setText(getGender);
            if (!languages.equalsIgnoreCase("English")) {
                setGenderLocalLang();
            }
        } else {
            js_edit_personal_gender.setText(getString(R.string.selectgender));
        }
    }

    private void setGenderLocalLang() {
        Gender localgender = new Gender();
        localgender.setGender(getGender);
        indexgender = select_gender.indexOf(localgender);
        String GenderLocal = select_gender.get(indexgender).getGender_local();
        js_edit_personal_gender.setText(GenderLocal);
    }

    //select job role from job seeker person editing page
    private void RoleAlert() {
        View emppromptView = View.inflate(MyProfile_Personal.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(MyProfile_Personal.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getJobRole != null && !getJobRole.isEmpty() && !getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            indexrole = -1;
            if (select_role != null && select_role.size() > 0) {
                Role localrole = new Role();
                localrole.setRole_name(getJobRole);
                indexrole = select_role.indexOf(localrole);
            }
        } else {
            indexrole = -1;
        }

        roleadapter = new ArrayAdapter<Role>(this, R.layout.spinner_item_text, select_role) {
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
        }

        ;
        filterrole.setAdapter(roleadapter);
        filterrole.setSelection(indexrole);
        exit.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setRole();
                                        alertDialog.dismiss();
                                    }
                                }

        );
        filterrole.setOnItemClickListener(new OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                                  if (indexrole != -1 && (indexrole == position)) {
                                                      getJobRole = getString(R.string.selectrole);
                                                      indexrole = -1;
                                                  } else {
                                                      indexrole = position;
                                                      getJobRole = select_role.get(position).getRole_name();
                                                  }
                                                  setRole();
                                                  roleadapter.notifyDataSetChanged();
                                              }
                                          }

        );
        roledone.setOnClickListener(new

                                            OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    setRole();
                                                    alertDialog.dismiss();
                                                }
                                            }

        );
    }

    private void setRole() {
        if (!getJobRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            js_edit_personal_role.setText(getJobRole);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang();
            }
        } else {
            js_edit_personal_role.setText(getString(R.string.selectrole));
        }
    }

    private void setRoleLocalLang() {
        Role localrole = new Role();
        localrole.setRole_name(getJobRole);
        int indexrole = select_role.indexOf(localrole);
        String RoleLocal = select_role.get(indexrole).getRole_name_local();
        js_edit_personal_role.setText(RoleLocal);
    }
}
