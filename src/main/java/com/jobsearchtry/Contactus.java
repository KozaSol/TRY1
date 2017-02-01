package com.jobsearchtry;
/*--Contact Us Page
Created by : Krishnaveni veeman
Description - Try - Contact us - user contact to try team--*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Contactus extends Activity {

    private EditText name, email, phoneno, leaveusmessage;
    private String getName, getEmail, getPhoneno, getLeaveusmsg;
    private ProgressDialog pg;
    private ImageButton exit;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactus);
        name = (EditText) findViewById(R.id.r_Name);
        email = (EditText) findViewById(R.id.r_Email);
        phoneno = (EditText) findViewById(R.id.r_phoneNo);
        leaveusmessage = (EditText) findViewById(R.id.contactus_message);
        Button btnsubmit = (Button) findViewById(R.id.fd_submit);
        ImageButton js_contact = (ImageButton) findViewById(R.id.js_r_h);
        exit = (ImageButton) findViewById(R.id.exit_alert);
        //logo - clicking go to landing page
        js_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        //back - clicking go to landing page
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        //submit button - onclick functionality
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //form validation - success
                if (contactusValidation()) {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new ContactusReg().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkEmail(String email) {
        return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //contactus validation
    private boolean contactusValidation() {
        getName = name.getText().toString();
        getEmail = email.getText().toString();
        getPhoneno = phoneno.getText().toString();
        getEmail = email.getText().toString();
        getLeaveusmsg = leaveusmessage.getText().toString();
        if (null == getName || getName.length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.youmusthave3mincontactperson),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == getEmail || checkEmail(getEmail)) {
            Toast.makeText(getApplicationContext(), getString(R.string.pleaseentervalidemail),
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == getPhoneno || getPhoneno.length() != 10) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pleaseenteravalidphno), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == getLeaveusmsg || getLeaveusmsg.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.messageshouldnotbeempty), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    //check whether it is logged in from employer or jobseeker
    private void onbackclick() {
        if (name.getText().toString().length() != 0 ||
                email.getText().toString().length() != 0 ||
                phoneno.getText().toString().length() != 0 ||
                leaveusmessage.getText().toString().length() != 0) {
            if (new BackAlertDialog().isBackDialog(Contactus.this)) {
                goback();
            }
        } else {
            goback();
        }
    }

    private void goback() {
        if (!GlobalData.emp_login_status.equals("No user found")) {
            //employer
            startActivity(new Intent(Contactus.this,
                    EmployerDashboard.class));
            finish();
        } else {
            //jobseeker
            GlobalData.joblistfrom = "RL";
            startActivity(new Intent(Contactus.this,
                    Homepage.class));
            finish();
        }
    }

    private class ContactusReg extends AsyncTask<String, String, String> {
        String cpresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Contactus.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        @Override
        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("Name", getName).add("EmailId",
                    getEmail)
                    .add("Phone", getPhoneno).add("Message", getLeaveusmsg).build();
            Request request = new Request.Builder().url(
                    GlobalData.url + "contactus_sendEmail.php").post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                cpresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            try {
                View promptView = View.inflate(Contactus.this, R.layout.contactus_popup,
                        null);
                final Dialog alertDialog = new Dialog(Contactus.this,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(promptView);
                Button invalid_login_submit = (Button) promptView.findViewById(R.id
                        .contactus_popup);
                ImageButton exit = (ImageButton) promptView.findViewById(R.id.exit_alert);
                TextView popup_message = (TextView) promptView.findViewById(R.id.popup_message);
                popup_message.setText(getString(R.string.contact_us));
                invalid_login_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (!GlobalData.emp_login_status.equals("No user found")) {
                            //employer
                            startActivity(new Intent(Contactus.this,
                                    EmployerDashboard.class));
                            finish();
                        } else {
                            //jobseeker
                            GlobalData.joblistfrom = "RL";
                            startActivity(new Intent(Contactus.this,
                                    Homepage.class));
                            finish();
                        }
                    }
                });
                alertDialog.show();
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
