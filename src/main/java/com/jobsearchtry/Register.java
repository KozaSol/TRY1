package com.jobsearchtry;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import android.app.Dialog;
import com.jobsearchtry.utils.BackAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.services.AutoStartService;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Employer;
import com.jobsearchtry.wrapper.Gender;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Qualification;
import com.jobsearchtry.wrapper.Specialization;
import com.jobsearchtry.wrapper.User;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jobsearchtry.utils.GlobalData.SENDER_ID;

public class Register extends Activity {
    private static String randomString;
    private ArrayList<String> SelectedqualificationList = new ArrayList<>(),
            SelectedqualificationIDList = new ArrayList<>(),
            SelectedspecializationIDList = new ArrayList<>(), industriesList = null;
    private Button btnVerify, js_r_quali_lay, gender, jobrole, industry;
    private AutoCompleteTextView location, jobseeker_location;
    private EditText username, age, email, contactperson, phoneno, companyname,
            code1;
    private CheckBox agree;
    private AlertDialog alertD;
    private ArrayAdapter<Qualification> qadapter, qpart1adapter;
    private ArrayAdapter<Specialization> sadapter;
    private LinearLayout js_r_role_lay, r_industry_lay, qualivalue;
    private String getEmail, getAge, getLocation, getJSLocation, getPhoneNo, getQualification,
            getQualificationID, getGender, getGenderTamil, getStatus, getRegisterStatus,
            getUsername, getRole, getRoleLocal, getForgotPasswordStatus,
            getCompName, getContactPerson,
            getIndustry, regId, getSpecialisationID, languages;
    private boolean[] isCheckedQuali, isCheckedQualiMain;
    private ArrayList<Qualification> qualificationList = new ArrayList<>(),
            qualiwithoutspecify = new ArrayList<>();
    private ArrayList<Gender> genderList = new ArrayList<>();
    private ArrayList<Role> select_role;
    private ArrayList<String> locationList = new ArrayList<>();
    private ArrayList<Specialization> specializationList = new ArrayList<>();
    private ProgressDialog pg;
    private OkHttpClient client = null;
    private int indexspeci = -1, indexindustry = -1, indexrole = -1, indexgender = -1;
    private Dialog alertDialog;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        getGender = getString(R.string.selectgender);
        getRole = getString(R.string.selectrole);
        getIndustry = getString(R.string.selectindustry);
        if (languages.equalsIgnoreCase("Tamil")) {
            getGenderTamil = getString(R.string.selectgender);
        }
        //find a registration from where(whether jobseeker/employer)
        if (GlobalData.loginfrom != null && !GlobalData.loginfrom.isEmpty()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    (Register.this));
            GlobalData.loginfrom = sharedPreferences.getString("LOGINFROM", GlobalData.loginfrom);
        } else {
            GlobalData.loginfrom = "Jobseekar";
        }
        if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar") || (GlobalData.loginfrom
                .equalsIgnoreCase("Jobseekar_D")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_M"))))) {
            //if jobseeker register
            setContentView(R.layout.jobseeker_registration);
            //get the gcm registration id for getting the notifications
            GCMRegistrar.checkDevice(Register.this);
            GCMRegistrar.checkManifest(Register.this);
            regId = GCMRegistrar.getRegistrationId(Register.this);
            if (regId.equals("")) {
                GCMRegistrar.register(Register.this, SENDER_ID);
            }
            //get the qualification,location & job role list from webservices
            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                new getQualification().execute();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            //back to homepage when clicking the logo
            ImageButton js_r_h = (ImageButton) findViewById(R.id.js_r_h);
            js_r_h.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.joblistfrom = "RL";
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
            Button tryregister = (Button) findViewById(R.id.js_tryRegister);
            username = (EditText) findViewById(R.id.js_r_username);
            phoneno = (EditText) findViewById(R.id.js_r_phonenumber);
            email = (EditText) findViewById(R.id.js_r_emailaddress);
            js_r_quali_lay = (Button) findViewById(R.id.js_r_qualification_lay);
            qualivalue = (LinearLayout) findViewById(R.id.js_r_qualification_layout);
            js_r_quali_lay.setText(R.string.select);
            jobrole = (Button) findViewById(R.id.js_r_jobrolespinner);
            js_r_role_lay = (LinearLayout) findViewById(R.id.js_r_jobrole_lay);
            gender = (Button) findViewById(R.id.js_personal_gender);
            LinearLayout js_personal_gender_lay = (LinearLayout) findViewById(R.id.js_personal_gender_lay);
            age = (EditText) findViewById(R.id.js_r_age);
            agree = (CheckBox) findViewById(R.id.checkbox_jd_register);
            TextView termsOfAgreement = (TextView) findViewById(R.id.termsOfAgreement);
            //go to read the terms and conditions page
            termsOfAgreement.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Register.this, TermsCondition.class);
                    i.putExtra("Terms", "REG");
                    startActivity(i);
                    // startActivityForResult(i, 2);
                }
            });
            jobseeker_location = (AutoCompleteTextView) findViewById(R.id.js_r_location);
            jobseeker_location.setThreshold(1);
            //gender click and get the selected gender
            js_personal_gender_lay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (genderList.size() > 0) {
                        GenderAlert();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
            //jobseeker registraion submit button click
            tryregister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jobseekerRegisterValidation()) {
                        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                            new getJobSeekerRegister().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            });
        } else {
            //employer register page
            setContentView(R.layout.register);
            //get the list of industry from the webservices
            if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                new getIndustry().execute();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            ImageButton js_r_h = (ImageButton) findViewById(R.id.js_r_h);
            js_r_h.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.joblistfrom = "RL";
                    finish();
                }
            });
            ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BackAlertDialog().isBackDialog(Register.this);
                }
            });
            Button tryregister = (Button) findViewById(R.id.tryEmpRegister);
            email = (EditText) findViewById(R.id.r_emailaddress);
            companyname = (EditText) findViewById(R.id.r_companyname);
            contactperson = (EditText) findViewById(R.id.r_contactperson);
            industry = (Button) findViewById(R.id.r_industry);
            r_industry_lay = (LinearLayout) findViewById(R.id.r_industry_lay);
            r_industry_lay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (industriesList.size() > 0) {
                        IndustryAlert();
                    }
                }
            });
            phoneno = (EditText) findViewById(R.id.r_phonenumber);
            agree = (CheckBox) findViewById(R.id.emp_checkbox_jd_register);
            TextView termsOfAgreement = (TextView) findViewById(R.id.emp_termsOfAgreement);
            termsOfAgreement.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Register.this, TermsCondition.class);
                    i.putExtra("Terms", "REG");
                    startActivity(i);
                }
            });
            location = (AutoCompleteTextView) findViewById(R.id.r_location);
            location.setThreshold(1);
            tryregister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (employerRegister()) {
                        new getEmployerRegister().execute();
                    }
                }

                private boolean employerRegister() {
                    getCompName = companyname.getText().toString();
                    getEmail = email.getText().toString();
                    getLocation = location.getText().toString();
                    getPhoneNo = phoneno.getText().toString();
                    getContactPerson = contactperson.getText().toString();
                    randomString = getRandomNumber();
                    if (null == getCompName || getCompName.length() < 3) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.companynamevalidation), Toast
                                        .LENGTH_LONG).show();
                        return false;
                    }
                    if (null == getPhoneNo || getPhoneNo.length() != 10) {
                        Toast.makeText(getApplicationContext(), getString(R.string.pleaseenteravalidphno),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                    if (getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                        Toast.makeText(getApplicationContext(), getString(R.string.industryvalidation), Toast
                                .LENGTH_LONG).show();
                        return false;
                    }
                    if (null == getLocation || getLocation.length() == 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.locationvalidation), Toast
                                .LENGTH_LONG).show();
                        return false;
                    }
                    if (locationList.size() > 0) {
                        if (!locationList.contains(getLocation)) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.locationwrongtoast),
                                    Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                    if (getEmail.length() > 0) {
                        if (checkEmail(getEmail)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.pleaseentervalidemail),
                                    Toast.LENGTH_LONG)
                                    .show();
                            return false;
                        }
                    }
                    if (null == getContactPerson || getContactPerson.length() < 3) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.contactpersonvalidation), Toast
                                        .LENGTH_LONG).show();
                        return false;
                    }
                    if (!agree.isChecked()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.acceptterms),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                    if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }

                private boolean checkEmail(String email) {
                    return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
                }
            });
        }
    }

    private void onbackclick() {
        if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar") || (GlobalData.loginfrom
                .equalsIgnoreCase("Jobseekar_D")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_M"))))) {
            if (username.getText().toString().length() != 0 ||
                    !getGender.equalsIgnoreCase(getString(R.string.selectgender)) ||
                    age.getText().toString().length() != 0 ||
                    phoneno.getText().toString().length() != 0 ||
                    jobseeker_location.getText().toString().length() != 0 ||
                    !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                new BackAlertDialog().isBackDialog(Register.this);
            } else {
                finish();
            }
        } else {
            if (companyname.getText().toString().length() != 0 ||
                    phoneno.getText().toString().length() != 0 ||
                    !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry)) ||
                    location.getText().toString().length() != 0 || contactperson.getText().toString().length() != 0) {
                new BackAlertDialog().isBackDialog(Register.this);
            } else {
                finish();
            }
        }
    }

    //select gender from job seeker registration page
    private void GenderAlert() {
        View emppromptView = View.inflate(Register.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectgender);
        Button genderdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtergender = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (!getGender.equalsIgnoreCase(getString(R.string.selectgender))) {
            indexgender = -1;
            for (int i = 0; i < genderList.size(); i++) {
                if (genderList.get(i).getGender().equals(getGender)) {
                    indexgender = i;
                }
            }
        } else {
            indexgender = -1;
        }
        final ArrayAdapter<Gender> adapter = new ArrayAdapter<Gender>(this, R.layout.spinner_item_text, genderList) {
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
                String yourValue = genderList.get(position).getGender();
                if (languages.equalsIgnoreCase("Tamil")) {
                    yourValue = genderList.get(position).getGender_local();
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
                    getGender = genderList.get(position).getGender();
                    if (!languages.equalsIgnoreCase("English")) {
                        getGenderTamil = genderList.get(position).getGender_local();
                    }
                }
                setGender();
                adapter.notifyDataSetChanged();
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
            gender.setText(getGender);
            if (!languages.equalsIgnoreCase("English")) {
                gender.setText(getGenderTamil);
            }
        } else {
            gender.setText(getString(R.string.selectgender));
        }
    }

    //select job role from job seeker registration page
    private void RoleAlert() {
        View emppromptView = View.inflate(Register.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (!getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            indexrole = -1;
            for (int i = 0; i < select_role.size(); i++) {
                if (select_role.get(i).getRole_name().equals(getRole)) {
                    indexrole = i;
                }
            }
        } else {
            indexrole = -1;
        }
        final ArrayAdapter<Role> adapter = new ArrayAdapter<Role>(this, R.layout.spinner_item_text, select_role) {
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
        };
        filterrole.setAdapter(adapter);
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
                    getRoleLocal = getString(R.string.selectrole);
                    indexrole = -1;
                } else {
                    indexrole = position;
                    getRole = select_role.get(position).getRole_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        getRoleLocal = select_role.get(position).getRole_name_local();
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
                alertDialog.dismiss();
            }
        });
    }

    private void setRole() {
        if (!getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            jobrole.setText(getRole);
            if (!languages.equalsIgnoreCase("English")) {
                jobrole.setText(getRoleLocal);
            }
        } else {
            jobrole.setText(getString(R.string.selectrole));
        }
    }


    //select job industry from employer registration page
    private void IndustryAlert() {
        View emppromptView = View.inflate(Register.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectindustry);
        Button industrydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterindustry = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (!getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            indexindustry = -1;
            for (int i = 0; i < industriesList.size(); i++) {
                if (industriesList.get(i).equals(getIndustry)) {
                    indexindustry = i;
                }
            }
        } else {
            indexindustry = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, industriesList) {
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
                String yourValue = industriesList.get(position);
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexindustry != -1 && (indexindustry == position)) {
                    getIndustry = getString(R.string.selectindustry);
                    indexindustry = -1;
                } else {
                    indexindustry = position;
                    getIndustry = industriesList.get(position);
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
        if (!getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            industry.setText(getIndustry);
        } else {
            industry.setText(getString(R.string.selectindustry));
        }
    }

    //jobseeker form validation
    private boolean jobseekerRegisterValidation() {
        getPhoneNo = phoneno.getText().toString();
        getUsername = username.getText().toString();
        getEmail = email.getText().toString();
        getAge = age.getText().toString();
        randomString = getRandomNumber();
        getQualification = js_r_quali_lay.getText().toString();
        getJSLocation = jobseeker_location.getText().toString();
        if (null == getUsername || getUsername.length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.youmusthave3mincontactperson),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (getGender.equals(getString(R.string.selectgender))) {
            Toast.makeText(getApplicationContext(), getString(R.string.pleasethegender), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == getAge || getAge.length() == 0 || Integer.parseInt(getAge) < 18) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.agevalidation), Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == getPhoneNo || getPhoneNo.length() != 10) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pleaseenteravalidphno), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (getEmail.length() > 0) {
            if (checkEmail(getEmail)) {
                Toast.makeText(getApplicationContext(), getString(R.string.pleaseentervalidemail),
                        Toast.LENGTH_LONG)
                        .show();
                return false;
            }
        }
        if (getQualification == null || getQualification.isEmpty()
                || getQualificationID == null || getQualificationID.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.pleaseselectqualification),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (locationList.size() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (null == getJSLocation || getJSLocation.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.locationvalidation),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!locationList.contains(getJSLocation)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.locationwrongtoast),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (getRole.equals(getString(R.string.selectrole))) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pleaseselecttherole), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!agree.isChecked()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.acceptterms),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //jobseeker - verification dialog
    private void showVerifyDialog() {
        View verifyView = View.inflate(Register.this, R.layout.verify_dialog, null);
        TextView subheader_mobilenumber = (TextView) verifyView.findViewById(R.id
                .subheader_mobilenumber);
        subheader_mobilenumber.setText(getString(R.string.userid) + " " + getPhoneNo);
        btnVerify = (Button) verifyView.findViewById(R.id.btnVerify);
        ImageButton btnCancel = (ImageButton) verifyView.findViewById(R.id.btnCancel);
        code1 = (EditText) verifyView.findViewById(R.id.code1);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(verifyView);
        alertDialog.show();
        btnVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = code1.getText().toString();
                if (enteredCode.isEmpty() || enteredCode.length() == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.verifycodevalidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!enteredCode.equals(randomString.trim())) {
                    Toast.makeText(getBaseContext(), getString(R.string.verifycodematch), Toast
                            .LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new SignUpJobSeekerTask().execute();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                }
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //employer - verification dialog
    private void showVerifyEmpDialog() {
        View verifyView = View.inflate(Register.this, R.layout.verify_dialog, null);
        TextView subheader_mobilenumber = (TextView) verifyView.findViewById(R.id
                .subheader_mobilenumber);
        subheader_mobilenumber.setText(getString(R.string.userid) + " " + getPhoneNo);
        btnVerify = (Button) verifyView.findViewById(R.id.btnVerify);
        ImageButton btnCancel = (ImageButton) verifyView.findViewById(R.id.btnCancel);
        code1 = (EditText) verifyView.findViewById(R.id.code1);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(verifyView);
        alertDialog.show();
        btnVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = code1.getText().toString();
                if (enteredCode.isEmpty() || enteredCode.length() == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.verifycodevalidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!enteredCode.equals(randomString.trim())) {
                    Toast.makeText(getBaseContext(), getString(R.string.verifycodematch), Toast
                            .LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new EmployerRegister().execute();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                }
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //random number generation for jobseeker verification code
    private String getRandomNumber() {
        Random generator = new Random();
        int random = 10000 + generator.nextInt(90000);
        return String.valueOf(random);
    }

    private void QualificationAlert() {
        View emppromptView = View.inflate(Register.this, R.layout.jobregister_quali, null);
        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView sk_popupheader = (TextView) emppromptView.findViewById(R.id.ql_popupheader);
        sk_popupheader.setText(R.string.qualification);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final LinearLayout categoryview = (LinearLayout) emppromptView.findViewById(R.id
                .qcate_view);
        final LinearLayout skillview = (LinearLayout) emppromptView.findViewById(R.id.speciali_view);
        final Helper filterskcatepart1 = (Helper) emppromptView.findViewById(R.id
                .filterqualipart1);
        final Helper filterskcate = (Helper) emppromptView.findViewById(R.id
                .filterqualilist);
        final Helper filterskill = (Helper) emppromptView.findViewById(R.id.filterskilllist);
        filterskcatepart1.setExpanded(true);
        filterskcate.setExpanded(true);
        filterskill.setExpanded(true);
        final TextView sl_skillheader = (TextView) emppromptView.findViewById(R.id.sl_specialiheader);
        sl_skillheader.setText(R.string.qualification);
        //skill list - back button (skillcategory view show & skill view hidden)
        final ImageButton sl_skillheader_back = (ImageButton) emppromptView.findViewById(R.id
                .sl_specifyheader_back);
        sl_skillheader_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryview.setVisibility(View.VISIBLE);
                skillview.setVisibility(View.GONE);
                //get|set the qualification with no specializationm items
                qpart1adapter = new ArrayAdapter<Qualification>(Register.this, R.layout
                        .qual_listview, qualiwithoutspecify) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            Context mContext = this.getContext();
                            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                                    .LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.qual_listview, parent, false);
                        }
                        CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                        textView.setText(qualiwithoutspecify.get(position).getQuali_name());
                        return textView;
                    }
                };
                filterskcatepart1.setAdapter(qpart1adapter);
                //get the selected quali names , split as "," separated
                String[] getLocationaded = SelectedqualificationList.toArray(new String[SelectedqualificationList
                        .size()]);
                String getlocationarray = Arrays.toString(getLocationaded);
                getQualification = getlocationarray.substring(1, getlocationarray.length() - 1);
                getQualification = getQualification.replace(", ", ",");
                String[] getqualiidaded = SelectedqualificationIDList.toArray(new String[SelectedqualificationIDList
                        .size()]);
                String getqualiifarray = Arrays.toString(getqualiidaded);
                getQualificationID = getqualiifarray.substring(1, getqualiifarray.length() - 1);
                getQualificationID = getQualificationID.replace(", ", ",");
                if (getQualification != null && !getQualification.isEmpty() && !getQualification.equalsIgnoreCase(getString(R.string.select))) {
                    List<String> rolelist = Arrays.asList(getQualification.split(","));
                    List<String> roleidlist = Arrays.asList(getQualificationID.split(","));
                    isCheckedQualiMain = new boolean[qualiwithoutspecify.size()];
                    int spec;
                    for (int i = 0; i < rolelist.size(); i++) {
                        for (int j = 0; j < qualiwithoutspecify.size(); j++) {
                            if (!(SelectedqualificationList.contains(rolelist.get(i)))) {
                                SelectedqualificationList.add(rolelist.get(i));
                                SelectedqualificationIDList.add(roleidlist.get(i));
                                SelectedspecializationIDList.add(null);
                            }
                            if (qualiwithoutspecify.get(j).getQuali_name().equals(rolelist.get(i))) {
                                spec = j;
                                isCheckedQualiMain[spec] = true;
                                filterskcatepart1.setItemChecked(spec, true);
                            }
                        }
                    }
                } else {
                    isCheckedQualiMain = new boolean[qualiwithoutspecify.size()];
                    Arrays.fill(isCheckedQualiMain, false);
                }
                //qualipart1 check|uncheck
                filterskcatepart1.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long
                            arg3) {
                        if (!isCheckedQualiMain[position]) {
                            isCheckedQualiMain[position] = true;
                            if (!(SelectedqualificationList.contains(qpart1adapter.getItem(position).getQuali_name()))) {
                                SelectedqualificationList.add(qpart1adapter.getItem(position).getQuali_name());
                                SelectedqualificationIDList.add(qpart1adapter.getItem(position).getId());
                                SelectedspecializationIDList.add(null);
                            }
                        } else {
                            isCheckedQualiMain[position] = false;
                            SelectedqualificationList.remove(qpart1adapter.getItem(position).getQuali_name());
                            SelectedqualificationIDList.remove(qpart1adapter.getItem(position).getId());
                            SelectedspecializationIDList.remove(null);
                        }
                    }
                });
                qadapter.notifyDataSetChanged();
                //get|set the skill category items
                qadapter = new ArrayAdapter<Qualification>(Register.this, R.layout
                        .skillcategory_row, qualificationList) {
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
                        textView.setText(qualificationList.get(position).getQuali_name());
                        String[] getspecifyadd = SelectedspecializationIDList.toArray(new String[SelectedspecializationIDList
                                .size()]);
                        String getspecifyarray = Arrays.toString(getspecifyadd);
                        getSpecialisationID = getspecifyarray.substring(1, getspecifyarray.length() - 1);
                        getSpecialisationID = getSpecialisationID.replace(", ", ",");
                        if (getSpecialisationID != null && !getSpecialisationID.isEmpty()) {
                            List<String> speciidlist = Arrays.asList(getSpecialisationID.split(","));
                            getQualificationID = qualificationList.get(position).getId();
                            specializationList = qualificationList.get(position).getSpecialization();
                            ArrayList<String> selectedskilllist = new ArrayList<>();
                            for (int i = 0; i < speciidlist.size(); i++) {
                                for (int j = 0; j < specializationList.size(); j++) {
                                    if (specializationList.get(j).getSpeciali_id().equals(speciidlist.get(i))) {
                                        String getSkillID = specializationList.get(j).getOd_occupations_list_id();
                                        if (getSkillID != null && getSkillID.equalsIgnoreCase(getQualificationID)) {
                                            selectedskilllist.add(specializationList.get(j).getSpeciali_name());
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
                filterskcate.setAdapter(qadapter);
            }
        });
        categoryview.setVisibility(View.VISIBLE);
        skillview.setVisibility(View.GONE);
        filterskcatepart1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        filterskill.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //get|set the qualification with no specializationm items
        qpart1adapter = new ArrayAdapter<Qualification>(Register.this, R.layout
                .qual_listview, qualiwithoutspecify) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.qual_listview, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                textView.setText(qualiwithoutspecify.get(position).getQuali_name());
                return textView;
            }
        };
        filterskcatepart1.setAdapter(qpart1adapter);
        String[] getqualipart = SelectedqualificationList.toArray(new String[SelectedqualificationList
                .size()]);
        String getqualipartarray = Arrays.toString(getqualipart);
        getQualification = getqualipartarray.substring(1, getqualipartarray.length() - 1);
        getQualification = getQualification.replace(", ", ",");
        if (getQualification != null && !getQualification.isEmpty() && !getQualification.equalsIgnoreCase(getString(R.string.select))) {
            List<String> rolelist = Arrays.asList(getQualification.split(","));
            List<String> roleidlist = Arrays.asList(getQualificationID.split(","));
            isCheckedQualiMain = new boolean[qualiwithoutspecify.size()];
            int spec;
            for (int i = 0; i < rolelist.size(); i++) {
                for (int j = 0; j < qualiwithoutspecify.size(); j++) {
                    if (!(SelectedqualificationList.contains(rolelist.get(i)))) {
                        SelectedqualificationList.add(rolelist.get(i));
                        SelectedqualificationIDList.add(roleidlist.get(i));
                        SelectedspecializationIDList.add(null);
                    }
                    if (qualiwithoutspecify.get(j).getQuali_name().equals(rolelist.get(i))) {
                        spec = j;
                        isCheckedQualiMain[spec] = true;
                        filterskcatepart1.setItemChecked(spec, true);
                    }
                }
            }
        } else {
            isCheckedQualiMain = new boolean[qualiwithoutspecify.size()];
            Arrays.fill(isCheckedQualiMain, false);
        }
        //qualipart1 check|uncheck
        filterskcatepart1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long
                    arg3) {
                if (!isCheckedQualiMain[position]) {
                    isCheckedQualiMain[position] = true;
                    if (!(SelectedqualificationList.contains(qpart1adapter.getItem(position).getQuali_name()))) {
                        SelectedqualificationList.add(qpart1adapter.getItem(position).getQuali_name());
                        SelectedqualificationIDList.add(qpart1adapter.getItem(position).getId());
                        SelectedspecializationIDList.add(null);
                    }
                } else {
                    isCheckedQualiMain[position] = false;
                    SelectedqualificationList.remove(qpart1adapter.getItem(position).getQuali_name());
                    SelectedqualificationIDList.remove(qpart1adapter.getItem(position).getId());
                    SelectedspecializationIDList.remove(null);
                }
            }
        });
        //get|set the qualification with no specializationm items
        qadapter = new ArrayAdapter<Qualification>(Register.this, R.layout
                .skillcategory_row, qualificationList) {
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
                textView.setText(qualificationList.get(position).getQuali_name());
                if (getSpecialisationID != null && !getSpecialisationID.isEmpty()) {
                    List<String> qualiidlist = Arrays.asList(getSpecialisationID.split(","));
                    getQualificationID = qualificationList.get(position).getId();
                    specializationList = qualificationList.get(position).getSpecialization();
                    ArrayList<String> selectedskilllist = new ArrayList<>();
                    for (int i = 0; i < qualiidlist.size(); i++) {
                        for (int j = 0; j < specializationList.size(); j++) {
                            if (specializationList.get(j).getSpeciali_id().equals(qualiidlist.get(i))) {
                                String getSkillID = specializationList.get(j).getOd_occupations_list_id();
                                if (getSkillID != null && getSkillID.equalsIgnoreCase(getQualificationID)) {
                                    selectedskilllist.add(specializationList.get(j).getSpeciali_name());
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
        filterskcate.setAdapter(qadapter);
        if (getQualificationID == null) {
            specializationList = qualificationList.get(0).getSpecialization();
        } else {
            for (int i = 0; i < qualificationList.size(); i++) {
                if (qualificationList.get(i).getId().equals(getQualificationID)) {
                    specializationList = qualificationList.get(i).getSpecialization();
                }
            }
        }
        //get|set the skills
        sadapter = new ArrayAdapter<Specialization>(this, R.layout.filter_listrow, specializationList) {
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
                textView.setText(specializationList.get(position).getSpeciali_name());
                return textView;
            }
        };
        filterskill.setAdapter(sadapter);
        //get|set the qualification check|uncheck
        filterskcate.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                getQualification = qualificationList.get(position).getQuali_name();
                getQualificationID = qualificationList.get(position).getId();
                sl_skillheader.setText(getQualification);
                specializationList = new ArrayList<>();
                for (int i = 0; i < qualificationList.size(); i++) {
                    if (qualificationList.get(i).getId().equals(getQualificationID)) {
                        specializationList = qualificationList.get(i).getSpecialization();
                    }
                }
                //get|set the skills
                sadapter = new ArrayAdapter<Specialization>(Register.this, R.layout
                        .filter_listrow, specializationList) {
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
                        textView.setText(specializationList.get(position).getSpeciali_name());
                        return textView;
                    }
                };
                filterskill.setAdapter(sadapter);
                categoryview.setVisibility(View.GONE);
                skillview.setVisibility(View.VISIBLE);
                String[] getspeciaded = SelectedspecializationIDList.toArray(new String[SelectedspecializationIDList
                        .size()]);
                String getspecarray = Arrays.toString(getspeciaded);
                getSpecialisationID = getspecarray.substring(1, getspecarray.length() - 1);
                getSpecialisationID = getSpecialisationID.replace(", ", ",");
                if (getSpecialisationID != null && !getSpecialisationID.isEmpty()) {
                    List<String> specidlist = Arrays.asList(getSpecialisationID.split(","));
                    indexspeci = -1;
                    for (int i = 0; i < specidlist.size(); i++) {
                        for (int j = 0; j < specializationList.size(); j++) {
                            if (specializationList.get(j).getSpeciali_id().equals(specidlist.get(i))) {
                                indexspeci = j;
                                filterskill.setItemChecked(indexspeci, true);
                            }
                        }
                    }
                } else {
                    indexspeci = -1;
                }
                //skills check|uncheck
                filterskill.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long
                            arg3) {
                        if (filterskill.isItemChecked(indexspeci)) {
                            filterskill.setItemChecked(position, false);
                            getSpecialisationID = null;
                            if (SelectedspecializationIDList.contains(sadapter.getItem(position)
                                    .getSpeciali_id())) {
                                SelectedspecializationIDList.remove(sadapter.getItem(position).getSpeciali_id());
                                SelectedqualificationIDList.remove(sadapter.getItem(position).getOd_occupations_list_id());
                                SelectedqualificationList.remove(sadapter.getItem(position).getOccupations_list_name());
                            }
                            indexspeci = -1;
                        } else {
                            filterskill.setItemChecked(position, true);
                            getSpecialisationID = specializationList.get(position).getSpeciali_id();
                            if ((!SelectedspecializationIDList.contains(sadapter.getItem(position)
                                    .getSpeciali_id()) && !(SelectedqualificationList.contains(sadapter.getItem(position).getOccupations_list_name())))) {
                                SelectedspecializationIDList.add(sadapter.getItem(position).getSpeciali_id());
                                SelectedqualificationIDList.add(sadapter.getItem(position).getOd_occupations_list_id());
                                SelectedqualificationList.add(sadapter.getItem(position).getOccupations_list_name());
                            } else {
                                if (SelectedqualificationList.contains(sadapter.getItem(position).getOccupations_list_name())) {
                                    for (int j = 0; j < SelectedqualificationList.size(); j++) {
                                        SelectedspecializationIDList.remove(j);
                                        SelectedspecializationIDList.add(j, sadapter.getItem(position).getSpeciali_id());
                                    }
                                }
                            }
                            indexspeci = filterskill.getCheckedItemPosition();
                        }
                    }
                });
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
        //skill selection reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getQualification = null;
                getQualificationID = null;
                getSpecialisationID = null;
                SelectedqualificationList.clear();
                SelectedqualificationIDList.clear();
                SelectedspecializationIDList.clear();
                if (qualificationList.size() > 0) {
                    isCheckedQuali = new boolean[qualificationList.size()];
                    Arrays.fill(isCheckedQuali, false);
                    isCheckedQualiMain = new boolean[qualificationList.size()];
                    Arrays.fill(isCheckedQualiMain, false);
                }
                js_r_quali_lay.setText(R.string.select);
                alertDialog.dismiss();
                QualificationAlert();
            }
        });
        //skill selection done and get the must have skill list and must have setion check|uncheck
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setQualification();
                alertDialog.dismiss();
            }
        });
    }

    private void setQualification() {
        String[] getLocationaded = SelectedqualificationIDList.toArray(new String[SelectedqualificationIDList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getQualificationID = getlocationarray.substring(1, getlocationarray.length() - 1);
        getQualificationID = getQualificationID.replace(", ", ",");
        String[] getQualiaded = SelectedqualificationList.toArray(new String[SelectedqualificationList
                .size()]);
        String getqualiarray = Arrays.toString(getQualiaded);
        getQualification = getqualiarray.substring(1, getqualiarray.length() - 1);
        getQualificationID = getQualificationID.replace(", ", ",");
        String[] getspeciaded = SelectedspecializationIDList.toArray(new String[SelectedspecializationIDList
                .size()]);
        String getspecarray = Arrays.toString(getspeciaded);
        getSpecialisationID = getspecarray.substring(1, getspecarray.length() - 1);
        getSpecialisationID = getSpecialisationID.replace(", ", ",");
        if (getQualificationID != null && !getQualificationID.isEmpty()) {
            js_r_quali_lay.setText(getQualification);
        } else {
            js_r_quali_lay.setText(R.string.select);
        }
    }

    //job seeker registration - before getting the verification code
    private class getJobSeekerRegister extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Name", getUsername).add("Gender",
                    getGender)
                    .add("Age", getAge).add("Mobile", getPhoneNo).add("EmailId", getEmail)
                    .add("Qualification", getQualificationID).add("Specialisation",
                            getSpecialisationID)
                    .add("Location", getJSLocation).add("job", getRole).add("Randomkey",
                            randomString).build();
            Request request = new Request.Builder().url(GlobalData.url + "job_register.php").post
                    (formBody).build();
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
            if (randomkeysendresponse != null && !randomkeysendresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(randomkeysendresponse);
                    getStatus = json.getString("message");
                    if (!(getStatus.equalsIgnoreCase("New User"))) {
                        //jobseeker - already exists - go to the forgot password alert
                        View promptView = View.inflate(Register.this, R.layout.register_fb, null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(promptView);
                        alertDialog.show();
                        final EditText input = (EditText) promptView.findViewById(R.id
                                .r_fb_mobilenumber);
                        TextView status = (TextView) promptView.findViewById(R.id.r_fpsubhead);
                        status.setText(getStatus);
                        Button fb_submit = (Button) promptView.findViewById(R.id.r_try_fb_submit);
                        Button fb_cancel = (Button) promptView.findViewById(R.id.r_try_fb_cancel);
                        input.setText(getPhoneNo);
                        fb_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                if (!(null == getPhoneNo || getPhoneNo.length() < 10)) {
                                    if (new UtilService().isNetworkAvailable(getApplicationContext
                                            ())) {
                                        new ForgotPasswordAsync().execute();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.pleaseenteravalidphno), Toast
                                                    .LENGTH_LONG).show();
                                }
                            }
                        });
                        fb_cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });
                    } else {
                        //job seeker new user - verification dialog form opens
                        showVerifyDialog();
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

    //employer registration
    class getEmployerRegister extends AsyncTask<String, String, String> {
        String employerregisterresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", getPhoneNo)
                    .add("EmailId", getEmail)
                    .add("location", getLocation)
                    .add("ContactPerson", getContactPerson)
                    .add("industry", getIndustry)
                    .add("Randomkey", randomString)
                    .add("Name", getCompName).build();
            Request request = new Request.Builder().url(GlobalData.url + "register.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                employerregisterresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (employerregisterresponse != null && !employerregisterresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(employerregisterresponse);
                    getStatus = json.getString("message");
                    if (!(getStatus.equalsIgnoreCase("New User"))) {
                        //jobseeker - already exists - go to the forgot password alert
                        View promptView = View.inflate(Register.this, R.layout.register_fb, null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(promptView);
                        alertDialog.show();
                        final EditText input = (EditText) promptView.findViewById(R.id
                                .r_fb_mobilenumber);
                        TextView status = (TextView) promptView.findViewById(R.id.r_fpsubhead);
                        status.setText(getStatus);
                        Button fb_submit = (Button) promptView.findViewById(R.id.r_try_fb_submit);
                        Button fb_cancel = (Button) promptView.findViewById(R.id.r_try_fb_cancel);
                        input.setText(getPhoneNo);
                        fb_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                if (!(null == getPhoneNo || getPhoneNo.length() < 10)) {
                                    if (new UtilService().isNetworkAvailable(getApplicationContext
                                            ())) {
                                        new EmpForgotPasswordAsync().execute();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.pleaseenteravalidphno), Toast
                                                    .LENGTH_LONG).show();
                                }
                            }
                        });
                        fb_cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });
                    } else {
                        //job seeker new user - verification dialog form opens
                        showVerifyEmpDialog();
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

    //employer forgot password
    class EmpForgotPasswordAsync extends AsyncTask<String, String, String> {
        String fbresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("MobileNo", getPhoneNo).add("languages", languages).build();
            Request request = new Request.Builder().url(GlobalData.url + "forgot_password.php")
                    .post(formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                fbresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (fbresponse != null && !fbresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(fbresponse);
                    getForgotPasswordStatus = responseObj.getString("success_message");
                    if (getForgotPasswordStatus.equalsIgnoreCase("Sucessfully Recovered " +
                            "Password")) {
                        Toast.makeText(Register.this, getString(R.string.empforgotpasswordsuccess),
                                Toast.LENGTH_LONG)
                                .show();
                        GlobalData.loginfrom = "Employer";
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Register.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("LOGINFROM", GlobalData.loginfrom);
                        editor.apply();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Register.this, getForgotPasswordStatus, Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Register.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Register.this, getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //employer registration
    class EmployerRegister extends AsyncTask<String, String, String> {
        String employerregisterresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", getPhoneNo).add("EmailId",
                    getEmail)
                    .add("location", getLocation).add("ContactPerson", getContactPerson).add
                            ("industry", getIndustry)
                    .add("Password", randomString).add("Name", getCompName).build();
            Request request = new Request.Builder().url(GlobalData.url + "register.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                employerregisterresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (employerregisterresponse != null && !employerregisterresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(employerregisterresponse);
                    getRegisterStatus = json.getString("message_register");
                    if ((getRegisterStatus.equalsIgnoreCase("User Added Sucessfully"))) {
                        //employer registered successfully - redirect to employer dashboard
                        startService(new Intent(getBaseContext(), AutoStartService.class));
                        Gson gson = new Gson();
                        JSONObject employerloginviareg = new JSONObject(employerregisterresponse);
                        Employer employer = gson.fromJson(employerloginviareg.getString("Details"),
                                new TypeToken<Employer>() {
                                }.getType());
                        GlobalData.emp_login_status = employer.getId();
                        GlobalData.empusername = employer.getCompanyName();
                        GlobalData.loginfrom = "Employer";
                        GlobalData.company_email = employer.getEmailId();
                        if (GlobalData.getremcompanyemail.size() > 0) {
                            if (!(GlobalData.getremcompanyemail.contains(GlobalData.company_email)
                            )) {
                                GlobalData.getremcompanyemail.add(GlobalData.company_email);
                            }
                        } else {
                            GlobalData.getremcompanyemail.add(GlobalData.company_email);
                        }
                        String[] getmostusedemail = new String[GlobalData.getremcompanyemail.size
                                ()];
                        for (int k = 0; k < GlobalData.getremcompanyemail.size(); k++) {
                            getmostusedemail[k] = GlobalData.getremcompanyemail.get(k);
                        }
                        if (getmostusedemail.length > 0) {
                            String mostviewedemail = Arrays.toString(getmostusedemail);
                            mostviewedemail = mostviewedemail.substring(1, mostviewedemail.length
                                    () - 1);
                            mostviewedemail = mostviewedemail.replace(", ", ",");
                            GlobalData.getRemCompEmail = mostviewedemail;
                        }
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Register.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("ELS", GlobalData.emp_login_status);
                        editor.putString("EUN", GlobalData.empusername);
                        editor.putString("EEMAIL", GlobalData.company_email);
                        editor.putString("LOGINFROM", GlobalData.loginfrom);
                        editor.putString("REE", GlobalData.getRemCompEmail);
                        editor.apply();
                        View emppromptView = View.inflate(Register.this,
                                R.layout.congrates, null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(emppromptView);
                        alertDialog.show();
                        TextView f_popupheader = (TextView) emppromptView
                                .findViewById(R.id.rs_popupheader);
                        f_popupheader.setText(R.string.welcomee);
                        TextView f_popupsubheader = (TextView) emppromptView
                                .findViewById(R.id.rs_popup_message);
                        f_popupsubheader.setText(R.string.empsignupmsg);
                        Button submit = (Button) emppromptView.findViewById(R.id.rs_popup);
                        submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //jobseeker registered successfully - redirect to landing page
                                startActivity(new Intent(Register.this, EmployerDashboard.class));
                                finish();
                                alertDialog.dismiss();
                            }
                        });
                    } else {
                        //employer register - invalid login details
                        View promptView = View.inflate(Register.this, R.layout.invalid_username,
                                null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(promptView);
                        alertDialog.show();
                        Button invalid_login_submit = (Button) promptView.findViewById(R.id
                                .try_invalid_login_submit);
                        TextView login_invalid_msg = (TextView) promptView.findViewById(R.id
                                .invalidmsg_login);
                        login_invalid_msg.setText(getRegisterStatus);
                        invalid_login_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (pg.isShowing()) {
            pg.dismiss();
        }
    }

    //job seeker registration - after entered the verification code
    class SignUpJobSeekerTask extends AsyncTask<String, String, String> {
        String registerresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
                pg = new ProgressDialog(Register.this, R.style.MyTheme);
                pg.setCancelable(false);
                pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pg.setIndeterminate(true);
                pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
                pg.show();
            } catch (Exception ignored) {
            }
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Name", getUsername).add("Gender",
                    getGender)
                    .add("Age", getAge).add("Mobile", getPhoneNo).add("EmailId", getEmail)
                    .add("Qualification", getQualificationID).add("Specialisation",
                            getSpecialisationID)
                    .add("Location", getJSLocation).add("job", getRole).add("Password",
                            randomString)
                    .add("registration_id", regId).add("login_status", "Y").build();
            Request request = new Request.Builder().url(GlobalData.url + "job_register.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                registerresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (registerresponse != null && !registerresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(registerresponse);
                    getRegisterStatus = responseObj.getString("message_register");
                    if ((getRegisterStatus.equalsIgnoreCase("User Added Sucessfully"))) {
                        Gson gson = new Gson();
                        User user = gson.fromJson(responseObj.getString("Details"), new
                                TypeToken<User>() {
                                }.getType());
                        GlobalData.mobilenumber = user.getMobile();
                        GlobalData.login_status = user.getId();
                        GlobalData.username = user.getUserName();
                        GlobalData.user = user;
                        GlobalData.loginfrom = "Jobseekar";
                        GlobalData.pageback = "Home";
                        //GlobalData.backfrom = "Home";
                        if (GlobalData.getremjsphone.size() > 0) {
                            if (!(GlobalData.getremjsphone.contains(GlobalData.mobilenumber))) {
                                GlobalData.getremjsphone.add(GlobalData.mobilenumber);
                            }
                        } else {
                            GlobalData.getremjsphone.add(GlobalData.mobilenumber);
                        }
                        String[] getmostusedphno = new String[GlobalData.getremjsphone.size()];
                        for (int k = 0; k < GlobalData.getremjsphone.size(); k++) {
                            getmostusedphno[k] = GlobalData.getremjsphone.get(k);
                        }
                        if (getmostusedphno.length > 0) {
                            String mostviewedphno = Arrays.toString(getmostusedphno);
                            mostviewedphno = mostviewedphno.substring(1, mostviewedphno.length() -
                                    1);
                            mostviewedphno = mostviewedphno.replace(", ", ",");
                            GlobalData.getRemJSPhone = mostviewedphno;
                        }
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Register.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("MN", GlobalData.mobilenumber);
                        editor.putString("LS", GlobalData.login_status);
                        editor.putString("NAME", GlobalData.username);
                        editor.putString("PAGEBACK", GlobalData.pageback);
                        //editor.putString("BACKFrom", GlobalData.backfrom);
                        editor.putString("LOGINFROM", GlobalData.loginfrom);
                        editor.putString("EJSPH", GlobalData.getRemJSPhone);
                        editor.apply();
                        GlobalData.pagefrom = "Home";
                        View emppromptView = View.inflate(Register.this,
                                R.layout.congrates, null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(emppromptView);
                        alertDialog.show();
                        TextView f_popupheader = (TextView) emppromptView
                                .findViewById(R.id.rs_popupheader);
                        f_popupheader.setText(R.string.welcomee);
                        TextView f_popupsubheader = (TextView) emppromptView
                                .findViewById(R.id.rs_popup_message);
                        f_popupsubheader.setText(R.string.signupmessage);
                        Button submit = (Button) emppromptView.findViewById(R.id.rs_popup);
                        submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //jobseeker registered successfully - redirect to landing page
                                startActivity(new Intent(Register.this, Homepage.class));
                                finish();
                                alertDialog.dismiss();
                            }
                        });
                    } else {
                        //jobseeker login - invalid details
                        View promptView = View.inflate(Register.this, R.layout.invalid_username,
                                null);
                        final Dialog alertDialog = new Dialog(Register.this,R.style.MyThemeDialog);
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setContentView(promptView);
                        alertDialog.show();
                        Button invalid_login_submit = (Button) promptView.findViewById(R.id
                                .try_invalid_login_submit);
                        TextView login_invalid_msg = (TextView) promptView.findViewById(R.id
                                .invalidmsg_login);
                        login_invalid_msg.setText(getStatus);
                        invalid_login_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
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

    //jobseeker - forgot password
    private class ForgotPasswordAsync extends AsyncTask<String, String, String> {
        String fbresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", getPhoneNo).build();
            Request request = new Request.Builder().url(GlobalData.url + "job_forgot_password" +
                    ".php").post(formBody)
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                fbresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (fbresponse != null && !fbresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(fbresponse);
                    String getForgotPasswordStatus = responseObj.getString("success_message");
                    if (getForgotPasswordStatus.equalsIgnoreCase("Sucessfully Recovered " +
                            "Password")) {
                        //jobseeker - forgot password - successfully entered mobile number -
                        // redirect to login page
                        Toast.makeText(getApplicationContext(), getString(R.string.jsforgotpasswordsuccess),
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getForgotPasswordStatus, Toast
                                .LENGTH_LONG).show();
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

    class getQualification extends AsyncTask<String, String, String> {
        String jobseekerreg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            Request request = new Request.Builder().url(GlobalData.url + "jobseeker_registerlist" +
                    ".php").build();
            if (!languages.equalsIgnoreCase("English")) {
                RequestBody formBody = new FormBody.Builder().add("languages", languages).build();
                request = new Request.Builder().url(GlobalData.url + "jobseeker_registerlist" +
                        ".php").post(formBody).build();
            }
            client = new OkHttpClient();
            Response response;

            try {
                response = client.newCall(request).execute();
                jobseekerreg = response.body().string();
            } catch (IOException ignored) {
            }

            try {
                genderList = new ArrayList<>();
                qualificationList = new ArrayList<>();
                specializationList = new ArrayList<>();
                JSONObject json = new JSONObject(jobseekerreg);
                Gson gson = new Gson();
                genderList = gson.fromJson(json.getString("genderlist"), new
                        TypeToken<ArrayList<Gender>>() {
                        }.getType());
                if (genderList.size() > 0) {
                    Gender gender = new Gender();
                    gender.setGender("Any");
                    if (genderList.contains(gender)) {
                        int rindexgender = genderList.indexOf(gender);
                        genderList.remove(rindexgender);
                    }
                }
                qualificationList = gson.fromJson(json.getString("list"), new
                        TypeToken<ArrayList<Qualification>>() {
                        }.getType());
                specializationList = qualificationList.get(0).getSpecialization();
                qualiwithoutspecify.addAll(qualificationList.subList(0, 3));
                qualificationList.removeAll(qualiwithoutspecify);
                isCheckedQuali = new boolean[qualificationList.size()];
                Arrays.fill(isCheckedQuali, false);
                isCheckedQualiMain = new boolean[qualiwithoutspecify.size()];
                Arrays.fill(isCheckedQualiMain, false);
                select_role = new ArrayList<>();
                select_role = gson.fromJson(json.getString("role_name"), new
                        TypeToken<ArrayList<Role>>() {
                        }.getType());
                JSONArray locgroups = json.getJSONArray("locations");
                for (int i = 0; i < locgroups.length(); i++) {
                    JSONObject c = locgroups.getJSONObject(i);
                    String occupations_list_name = c.getString("citi_name");
                    locationList.add(occupations_list_name);
                }
            } catch (JSONException ignored) {
            }
            return null;
        }

        protected void onPostExecute(final String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            qualivalue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qualificationList.size() > 0) {
                        QualificationAlert();
                    } else {
                        Toast.makeText(Register.this, getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
            ArrayAdapter<String> locAdapter = new ArrayAdapter<>(Register.this, R.layout
                    .spinner_item_text,
                    locationList);
            if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar") || (GlobalData.loginfrom
                    .equalsIgnoreCase("Jobseekar_D")
                    || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")
                    || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_M"))))) {
                jobseeker_location.setAdapter(locAdapter);
            } else {
                location.setAdapter(locAdapter);
            }
            js_r_role_lay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoleAlert();
                }
            });
        }
    }

    class getIndustry extends AsyncTask<String, String, String> {
        String industryresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Register.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            Request request = new Request.Builder().url(GlobalData.url + "employer_registerlist" +
                    ".php").build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                industryresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            try {
                JSONObject json = new JSONObject(industryresponse);
                industriesList = new ArrayList<>();
                JSONArray groups = json.getJSONArray("industries");
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject c = groups.getJSONObject(i);
                    String occupations_list_name = c.getString("industry_name");
                    industriesList.add(occupations_list_name);
                }
                locationList = new ArrayList<>();
                JSONArray locgroups = json.getJSONArray("locations");
                for (int i = 0; i < locgroups.length(); i++) {
                    JSONObject c = locgroups.getJSONObject(i);
                    String occupations_list_name = c.getString("citi_name");
                    locationList.add(occupations_list_name);
                }
                if (industriesList.size() > 0) {
                    if (industriesList.get(0).equalsIgnoreCase("All")) {
                        industriesList.remove(0);
                    }
                }
                ArrayAdapter<String> locAdapter = new ArrayAdapter<>(Register.this, R.layout
                        .spinner_item_text,
                        locationList);
                if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar") || (GlobalData.loginfrom
                        .equalsIgnoreCase("Jobseekar_D")
                        || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")
                        || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_M"))))) {
                    jobseeker_location.setAdapter(locAdapter);
                } else {
                    location.setAdapter(locAdapter);
                }
            } catch (JSONException ignored) {
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }
}