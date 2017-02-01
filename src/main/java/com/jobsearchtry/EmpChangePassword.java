package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//employer - change password
public class EmpChangePassword extends Activity {
    private EditText currentpassword, newpassword,
            confirmpassword;
    private String currentPassword, newPassword, confirmPassword, languages;
    private ProgressDialog pg;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_changepassword);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        //get the value employer id and employer name for send a email
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        GlobalData.empusername = sharedPreferences.getString("EUN",
                GlobalData.empusername);
        ImageButton emp_cp_header = (ImageButton) findViewById(R.id.js_r_h);
        emp_cp_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmpChangePassword.this,
                        EmployerDashboard.class));
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
        EditText cp_mobilenumber = (EditText) findViewById(R.id.emp_cp_compname);
        cp_mobilenumber.setText(GlobalData.empusername);
        currentpassword = (EditText) findViewById(R.id.emp_cp_currentpassword);
        newpassword = (EditText) findViewById(R.id.emp_cp_newpassword);
        confirmpassword = (EditText) findViewById(R.id.emp_cp_confirmpassword);
        Button change_submit = (Button) findViewById(R.id.emp_set_changepassword_Submit);
        change_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                currentPassword = currentpassword.getText().toString();
                newPassword = newpassword.getText().toString();
                confirmPassword = confirmpassword.getText().toString();
                if (employerchangepasswordValidation()) {
                    if (new UtilService()
                            .isNetworkAvailable(getApplicationContext())) {
                        new UpdatePasswordAsync().execute(currentPassword, newPassword);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean employerchangepasswordValidation() {
        if (null == currentPassword || currentPassword.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.currentpasswordvalidation), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == newPassword || newPassword.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.newpasswordvalidation),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (newPassword.length() < 5) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.passwordlimitationvalidation),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == confirmPassword || confirmPassword.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.confirmpasswordvalidtion), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(newPassword.equals(confirmPassword))) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.passwordmismatch), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private void onbackclick() {
        if (currentpassword.getText().toString().length() != 0 || newpassword.getText().toString().length() != 0
                || confirmpassword.getText().toString().length() != 0) {
            if (new BackAlertDialog().isBackDialog(EmpChangePassword.this)) {
                startActivity(new Intent(EmpChangePassword.this,
                        EmployerDashboard.class));
                finish();
            }
        } else {
            startActivity(new Intent(EmpChangePassword.this,
                    EmployerDashboard.class));
            finish();
        }
    }

    private class UpdatePasswordAsync extends AsyncTask<String, String, String> {
        String cpresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(EmpChangePassword.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("Password", args[0].trim());
            paramsadd.addFormDataPart("NewPassword", args[1].trim());
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "change_password.php")
                    .post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                cpresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (cpresponse != null && !cpresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(cpresponse);
                    String getCPStatus = responseObj.getString("success_message");
                    Toast.makeText(getApplicationContext(), getCPStatus,
                            Toast.LENGTH_LONG).show();
                    if (getCPStatus
                            .equalsIgnoreCase(getString(R.string.changepasswordsuccess))) {
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(EmpChangePassword.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EmpChangePassword.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
