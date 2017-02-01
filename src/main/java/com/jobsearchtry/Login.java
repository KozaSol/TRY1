package com.jobsearchtry;


import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.services.AutoStartService;
import com.jobsearchtry.services.NotifiCountService;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Employer;
import com.jobsearchtry.wrapper.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.jobsearchtry.utils.GlobalData.SENDER_ID;

public class Login extends Activity {
    private EditText password;
    private String getMobileNumber, getPassword, getLoginStatus, getEmail, getForgotPasswordStatus,
            regId = "", getCheckedStatus, getECheckedStatus, languages;
    private ProgressDialog pg;
    private CheckBox checkbox_jd_login, checkbox_emp_login;
    private AutoCompleteTextView email, mobilenumber;
    private static final String M_JS_LOGIN = "m_jslogin";
    private static final String M_EMP_LOGIN = "m_emplogin";
    private OkHttpClient client = null;
    private ImageButton showpass;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences1 = PreferenceManager
                .getDefaultSharedPreferences((Login.this));
        GlobalData.getRemCompEmail = sharedPreferences1.getString("REE", GlobalData
                .getRemCompEmail);
        if (GlobalData.getRemCompEmail != null && !GlobalData.getRemCompEmail.isEmpty()) {
            String[] remcompemail = GlobalData.getRemCompEmail.split(",");
            for (String aRemcompemail : remcompemail) {
                if (!GlobalData.getremcompanyemail.contains(aRemcompemail)) {
                    GlobalData.getremcompanyemail.add(aRemcompemail);
                }
            }
        }
        GlobalData.getRemJSPhone = sharedPreferences1.getString("EJSPH", GlobalData.getRemJSPhone);
        if (GlobalData.getRemJSPhone != null && !GlobalData.getRemJSPhone.isEmpty()) {
            String[] remjsphno = GlobalData.getRemJSPhone.split(",");
            for (String aRemjsphno : remjsphno) {
                if (!GlobalData.getremjsphone.contains(aRemjsphno)) {
                    GlobalData.getremjsphone.add(aRemjsphno);
                }
            }
        }
        //find a login from where(whether jobseeker/employer)
        if (GlobalData.loginfrom != null && !GlobalData.loginfrom.isEmpty()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    (Login.this));
            GlobalData.loginfrom = sharedPreferences.getString("LOGINFROM", GlobalData.loginfrom);
        } else {
            GlobalData.loginfrom = "Jobseekar";
        }
        if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar") || (GlobalData.loginfrom
                .equalsIgnoreCase("Jobseekar_D")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")
                || (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_M"))))) {
            //if login page is jobseeker
            setContentView(R.layout.login);
            //get the gcm registration id for getting the notifications
            //get the gcm registration id for getting the notifications
            GCMRegistrar.checkDevice(Login.this);
            GCMRegistrar.checkManifest(Login.this);
            regId = GCMRegistrar.getRegistrationId(Login.this);
            if (regId.equals("")) {
                GCMRegistrar.register(Login.this, SENDER_ID);
            }
            //back to homepage when clicking the logo
            ImageButton login_h = (ImageButton) findViewById(R.id.js_r_h);
            login_h.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.joblistfrom = "RL";
                    startActivity(new Intent(Login.this, Homepage.class));
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
            mobilenumber = (AutoCompleteTextView) findViewById(R.id.mobilenumbervalue);
            if (GlobalData.getremjsphone.size() > 0) {
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(Login.this, R.layout
                        .spinner_item_text,
                        GlobalData.getremjsphone);
                mobilenumber.setAdapter(emailAdapter);
            }
            password = (EditText) findViewById(R.id.password);
            password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        jobseekerGo();
                        return true;
                    }
                    return false;
                }
            });
            checkbox_jd_login = (CheckBox) findViewById(R.id.checkbox_jd_login);
            SharedPreferences loginPreferences = getSharedPreferences(M_JS_LOGIN, Context
                    .MODE_PRIVATE);
            mobilenumber.setText(loginPreferences.getString("mobileno", ""));
            password.setText(loginPreferences.getString("password", ""));
            getCheckedStatus = loginPreferences.getString("isCheck", getCheckedStatus);
            if (getCheckedStatus != null && !getCheckedStatus.isEmpty()) {
                checkbox_jd_login.setChecked(true);
            } else {
                checkbox_jd_login.setChecked(false);
            }
            Button trylogin = (Button) findViewById(R.id.tryLogin);
            //job seeker - login button click
            trylogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    jobseekerGo();
                }
            });
            //go to job seeker register
            TextView tryregister = (TextView) findViewById(R.id.tryregister);
            tryregister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, Register.class));
                }
            });
            //job seeker forgot password dialog
            TextView forgotpassword = (TextView) findViewById(R.id.forgotpassword);
            forgotpassword.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog alertDialog = new Dialog(Login.this,R.style.MyThemeDialog);
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setCanceledOnTouchOutside(false);
                    View promptView = View.inflate(Login.this, R.layout.forgotpassword, null);
                    final EditText input = (EditText) promptView.findViewById(R.id
                            .fb_mobilenumber);
                    ImageButton exit = (ImageButton) promptView.findViewById(R.id.exit_js_fp);
                    Button fb_submit = (Button) promptView.findViewById(R.id.try_fb_submit);
                    fb_submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            getMobileNumber = input.getText().toString();
                            if (input.getText().toString().length() == 10) {
                                if (new UtilService().isNetworkAvailable(Login.this)) {
                                    new ForgotPasswordAsync().execute();
                                } else {
                                    Toast.makeText(Login.this, getString(R.string.checkconnection),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, getString(R.string.pleaseenteravalidphno), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }

                    });
                    alertDialog.setContentView(promptView);
                    alertDialog.show();
                    exit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            //employer login page
            setContentView(R.layout.employerlogin);
            //go back to landing page when clicking logo
            ImageButton login_h = (ImageButton) findViewById(R.id.js_r_h);
            login_h.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.joblistfrom = "RL";
                    startActivity(new Intent(Login.this, Homepage.class));
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
            email = (AutoCompleteTextView) findViewById(R.id.emailvalue);
            if (GlobalData.getremcompanyemail.size() > 0) {
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(Login.this, R.layout
                        .spinner_item_text,
                        GlobalData.getremcompanyemail);
                email.setAdapter(emailAdapter);
            }
            password = (EditText) findViewById(R.id.emp_password);
            password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        employerGo();
                        return true;
                    }
                    return false;
                }
            });
            checkbox_emp_login = (CheckBox) findViewById(R.id.checkbox_emp_login);
            SharedPreferences emploginPreferences = getSharedPreferences(M_EMP_LOGIN, Context
                    .MODE_PRIVATE);
            email.setText(emploginPreferences.getString("email", ""));
            password.setText(emploginPreferences.getString("password", ""));
            getECheckedStatus = emploginPreferences.getString("isCheck", getECheckedStatus);
            if (getECheckedStatus != null && !getECheckedStatus.isEmpty()) {
                checkbox_emp_login.setChecked(true);
            } else {
                checkbox_emp_login.setChecked(false);
            }
            //employer login button clicking
            Button trylogin = (Button) findViewById(R.id.tryEmpLogin);
            trylogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    employerGo();
                }
            });
            //go to employer register
            TextView tryregister = (TextView) findViewById(R.id.emp_tryregister);
            tryregister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.loginfrom = "Employer";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("LOGINFROM", GlobalData.loginfrom);
                    editor.apply();
                    startActivity(new Intent(Login.this, Register.class));
                }
            });
            //employer forgot password dialog
            TextView forgotpassword = (TextView) findViewById(R.id.emp_forgotpasswordd);
            forgotpassword.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog empalertD = new Dialog(Login.this,R.style.MyThemeDialog);
                    empalertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    empalertD.setCanceledOnTouchOutside(false);
                    View emppromptView = View.inflate(Login.this, R.layout.emp_forgotpassword,
                            null);
                    final EditText input = (EditText) emppromptView.findViewById(R.id
                            .emp_fb_email);
                    ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_emp_fp);
                    Button fb_submit = (Button) emppromptView.findViewById(R.id.emp_try_fb_submit);
                    fb_submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            String getMobileNo = input.getText().toString();
                            if (input.getText().toString().length() == 10) {
                                if (new UtilService().isNetworkAvailable(Login.this)) {
                                    new EmpForgotPasswordAsync().execute(getMobileNo);
                                } else {
                                    Toast.makeText(Login.this, getString(R.string.checkconnection),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, getString(R.string.pleaseenteravalidphno),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                    empalertD.setContentView(emppromptView);
                    empalertD.show();
                    exit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            empalertD.dismiss();
                        }
                    });
                }
            });
        }
        showpass = (ImageButton) findViewById(R.id.passeye);
        showpass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isClicked = isClicked ? false : true;
                if (isClicked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showpass.setImageResource(R.drawable.eye);
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showpass.setImageResource(R.drawable.uneye);
                }
            }
        });
    }

    private boolean checkEmail(String email) {
        return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private void jobseekerGo() {
        if (jobseekerloginValidation()) {
            getMobileNumber = mobilenumber.getText().toString();
            getPassword = password.getText().toString();
            if (new UtilService().isNetworkAvailable(Login.this)) {
                new getLogin().execute(getMobileNumber, getPassword);
            } else {
                Toast.makeText(Login.this,
                        getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void employerGo() {
        if (employerloginValidation()) {
            getEmail = email.getText().toString();
            getPassword = password.getText().toString();
            if (new UtilService().isNetworkAvailable(Login.this)) {
                new getEmpLogin().execute(getEmail, getPassword);
            } else {
                Toast.makeText(Login.this,
                        getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //employer login validation
    private boolean employerloginValidation() {
        getEmail = email.getText().toString();
        getPassword = password.getText().toString();
        if (null == getEmail || getEmail.length() != 10) {
            Toast.makeText(Login.this, getString(R.string.pleaseenteravalidphno), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == getPassword || getPassword.length() == 0) {
            Toast.makeText(Login.this, getString(R.string.passwordvalidation), Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == getPassword || getPassword.length() < 5) {
            Toast.makeText(Login.this, getString(R.string.passwordlimitationvalidation),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!new UtilService().isNetworkAvailable(Login.this)) {
            Toast.makeText(Login.this, getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //job seeker login validation
    private boolean jobseekerloginValidation() {
        getMobileNumber = mobilenumber.getText().toString();
        getPassword = password.getText().toString();
        if (null == getMobileNumber || getMobileNumber.length() != 10) {
            Toast.makeText(Login.this, getString(R.string.pleaseenteravalidphno), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (getPassword.length() == 0 || getPassword.isEmpty()) {
            Toast.makeText(Login.this, getString(R.string.passwordvalidation), Toast.LENGTH_LONG).show();
            return false;
        }
        if (getPassword.length() < 5 || getPassword.isEmpty()) {
            Toast.makeText(Login.this, getString(R.string.passwordlimitationvalidation),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!new UtilService().isNetworkAvailable(Login.this)) {
            Toast.makeText(Login.this, getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class getLogin extends AsyncTask<String, String, String> {
        String loginresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", params[0].trim())
                    .add("Password", params[1].trim()).add("registration_id", regId).add("languages", languages)
                    .add("login_status", "Y").build();
            Request request = new Request.Builder().url(GlobalData.url + "job_login.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                loginresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (loginresponse != null && !loginresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(loginresponse);
                    getLoginStatus = responseObj.getString("message");
                    if (getLoginStatus.equalsIgnoreCase("Logged In")) {
                        //job seeker logged in sucessfully
                        startService(new Intent(getBaseContext(), NotifiCountService.class));
                        Gson gson = new Gson();
                        User user = gson.fromJson(responseObj.getString("Details"), new
                                TypeToken<User>() {
                                }.getType());
                        GlobalData.pagefrom = "Home";
                        GlobalData.mobilenumber = user.getMobile();
                        GlobalData.login_status = user.getId();
                        GlobalData.username = user.getUserName();
                        GlobalData.user = user;
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
                                .getDefaultSharedPreferences(Login.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("MN", GlobalData.mobilenumber);
                        editor.putString("LS", GlobalData.login_status);
                        editor.putString("NAME", GlobalData.username);
                        editor.putString("EJSPH", GlobalData.getRemJSPhone);
                        editor.apply();
                        if (checkbox_jd_login.isChecked()) {
                            getCheckedStatus = "Checked";
                            SharedPreferences loginPreferences = getSharedPreferences(M_JS_LOGIN,
                                    Context.MODE_PRIVATE);
                            loginPreferences.edit().putString("mobileno", GlobalData.mobilenumber)
                                    .putString("password", password.getText().toString().trim())
                                    .putString("isCheck", getCheckedStatus).apply();
                        } else {
                            getCheckedStatus = null;
                            SharedPreferences loginPreferences = getSharedPreferences(M_JS_LOGIN,
                                    Context.MODE_PRIVATE);
                            loginPreferences.edit().clear().apply();
                        }
                        GlobalData.getCount = responseObj.getString("count");
                        if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar")) {
                            //job seeker login success - page from jobseeker menu - go to landing
                            // page
                            editor.putString("PAGEBACK", GlobalData.pageback);
                            editor.apply();
                            startActivity(new Intent(Login.this, Homepage.class));
                        } else if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_D")) {
                            //job seeker login success - page from job detail -login & apply  - go
                            // to job detail page
                            if (new UtilService().isNetworkAvailable(Login.this)) {
                                new ApplyJob().execute();
                            } else {
                                Toast.makeText(Login.this,
                                        getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else if (GlobalData.loginfrom.equalsIgnoreCase("Jobseekar_F")) {
                            //job seeker login success - page from job detail -set fav - go to job
                            // detail page
                            if (new UtilService().isNetworkAvailable(Login.this)) {
                                new AddJobToFavourite().execute();
                            } else {
                                Toast.makeText(Login.this,
                                        getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    } else {
                        //job seeker login failed for invalid details
                        final Dialog alertDialogBuilder = new Dialog(Login.this,R.style.MyThemeDialog);
                        alertDialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialogBuilder.setCanceledOnTouchOutside(false);
                        View promptView = View.inflate(Login.this, R.layout.invalid_username,
                                null);
                        Button invalid_login_submit = (Button) promptView.findViewById(R.id
                                .try_invalid_login_submit);
                        TextView login_invalid_msg = (TextView) promptView.findViewById(R.id
                                .invalidmsg_login);
                        login_invalid_msg.setText(getLoginStatus);
                        alertDialogBuilder.setContentView(promptView);
                        invalid_login_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                alertDialogBuilder.dismiss();
                            }
                        });
                        alertDialogBuilder.show();
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

    //job seeker forgot password
    class ForgotPasswordAsync extends AsyncTask<String, String, String> {
        String fbresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Mobile", getMobileNumber).add("languages", languages).build();
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
                    getForgotPasswordStatus = responseObj.getString("success_message");
                    if (getForgotPasswordStatus.equalsIgnoreCase("Sucessfully Recovered " +
                            "Password")) {
                        Toast.makeText(Login.this, getString(R.string.jsforgotpasswordsuccess),
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Login.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, getForgotPasswordStatus, Toast.LENGTH_LONG)
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

    class ApplyJob extends AsyncTask<String, String, String> {
        String applyjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("job_id", GlobalData.jobid)
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder().url(GlobalData.url + "apply_job.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                applyjobresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (applyjobresponse != null && !applyjobresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(applyjobresponse);
                    String getApplyStatus = json.getString("Status");
                    Toast.makeText(Login.this, getApplyStatus, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, JobSeeker_Detail.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(Login.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class AddJobToFavourite extends AsyncTask<String, String, String> {
        String setfavresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("job_id", GlobalData.jobid)
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder().url(GlobalData.url + "favourite_job.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                setfavresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (setfavresponse != null && !setfavresponse.contains("connectionFailure")) {
                try {
                    JSONObject json = new JSONObject(setfavresponse);
                    String getFavStatus = json.getString("Status");
                    Toast.makeText(getApplicationContext(), getFavStatus, Toast.LENGTH_SHORT)
                            .show();
                    startActivity(new Intent(Login.this, JobSeeker_Detail.class));
                    finish();
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

    //employer login button click
    private class getEmpLogin extends AsyncTask<String, String, String> {
        String employerloginresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody formBody = new FormBody.Builder().add("mobileno", params[0].trim())
                    .add("Password", params[1].trim()).add("languages", languages).build();
            Request request = new Request.Builder().url(GlobalData.url + "login.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                employerloginresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (employerloginresponse != null && !employerloginresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(employerloginresponse);
                    getLoginStatus = responseObj.getString("message");
                    if (getLoginStatus.equalsIgnoreCase("Logged In")) {
                        //employer login success - go to emploer dashboard
                        startService(new Intent(getBaseContext(), AutoStartService.class));
                        Gson gson = new Gson();
                        Employer employer = gson.fromJson(responseObj.getString("Details"), new
                                TypeToken<Employer>() {
                                }.getType());
                        GlobalData.emp_login_status = employer.getId();
                        GlobalData.empusername = employer.getCompanyName();
                        GlobalData.loginfrom = "Employer";
                        GlobalData.company_email = employer.getEmailId();
                        GlobalData.employer = employer;
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
                                .getDefaultSharedPreferences(Login.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("ELS", GlobalData.emp_login_status);
                        editor.putString("EUN", GlobalData.empusername);
                        editor.putString("EEMAIL", GlobalData.company_email);
                        editor.putString("LOGINFROM", GlobalData.loginfrom);
                        editor.putString("REE", GlobalData.getRemCompEmail);
                        editor.apply();
                        if (checkbox_emp_login.isChecked()) {
                            getECheckedStatus = "Checked";
                            SharedPreferences emploginPreferences = getSharedPreferences
                                    (M_EMP_LOGIN,
                                            Context.MODE_PRIVATE);
                            emploginPreferences.edit().putString("email", email.getText().toString
                                    ().trim())
                                    .putString("password", password.getText().toString().trim())
                                    .putString("isCheck", getECheckedStatus).apply();
                        } else {
                            getECheckedStatus = null;
                            SharedPreferences emploginPreferences = getSharedPreferences
                                    (M_EMP_LOGIN,
                                            Context.MODE_PRIVATE);
                            emploginPreferences.edit().clear().apply();
                        }
                        GlobalData.empNotiCount = responseObj.getString("count");
                        if (!GlobalData.empNotiCount.equalsIgnoreCase(null)) {
                            new Handler(EmployerDashboard.callback).sendEmptyMessage(0);
                        }
                        if (GlobalData.fromlogin != null && GlobalData.fromlogin.equalsIgnoreCase("PostJob")) {
                            startActivity(new Intent(Login.this, PostJob.class));
                        } else {
                            startActivity(new Intent(Login.this, EmployerDashboard.class));
                        }
                        finish();
                    } else {
                        //employer login failure
                        final Dialog alertDialogBuilder = new Dialog(Login.this,R.style.MyThemeDialog);
                        alertDialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialogBuilder.setCanceledOnTouchOutside(false);
                        View promptView = View.inflate(Login.this, R.layout.invalid_username,
                                null);
                        Button invalid_login_submit = (Button) promptView.findViewById(R.id
                                .try_invalid_login_submit);
                        TextView login_invalid_msg = (TextView) promptView.findViewById(R.id
                                .invalidmsg_login);
                        login_invalid_msg.setText(getLoginStatus);
                        alertDialogBuilder.setContentView(promptView);
                        invalid_login_submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                alertDialogBuilder.dismiss();
                            }
                        });
                        alertDialogBuilder.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Login.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else

            {
                Toast.makeText(Login.this, getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //employer forgot password
    class EmpForgotPasswordAsync extends AsyncTask<String, String, String> {
        String fbresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Login.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("MobileNo", args[0]).add("languages", languages).build();
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
                        Toast.makeText(Login.this, getString(R.string.empforgotpasswordsuccess),
                                Toast.LENGTH_LONG)
                                .show();
                        GlobalData.loginfrom = "Employer";
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Login.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("LOGINFROM", GlobalData.loginfrom);
                        editor.apply();
                        startActivity(new Intent(Login.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, getForgotPasswordStatus, Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Login.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}