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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Employer;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.utils.BackAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*In this class we can view/edit/update the details of employer*/
public class Employer_Profile extends Activity {
    private EditText companame, email, phoneno, contactperson;
    private String getCompName, getEmail, getIndustry, getMobileNo, getContactPerson,
            getLocation, languages;
    private Button industry;
    private LinearLayout emp_u_r_industry_lay;
    private AutoCompleteTextView location;
    private ArrayList<String> locationList = null;
    private ProgressDialog pg;
    private OkHttpClient client = null;
    private ArrayList<Industry> industriesList = null;
    private ArrayAdapter<Industry> industryAdapter;
    private int indexindustry = -1;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_profile);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        getIndustry = getString(R.string.selectindustry);
        //get the company id from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        //get the list of industry,location from database
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getEmployerDetail().execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
        //logo clicking , go to dashboard page
        ImageButton emp_pro_header = (ImageButton) findViewById(R.id.js_r_h);
        emp_pro_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Employer_Profile.this,
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
        companame = (EditText) findViewById(R.id.emp_u_r_companyname);
        email = (EditText) findViewById(R.id.emp_u_r_emailaddress);
        phoneno = (EditText) findViewById(R.id.emp_u_r_phonenumber);
        contactperson = (EditText) findViewById(R.id.emp_u_r_contactperson);
        industry = (Button) findViewById(R.id.emp_u_r_industry);
        emp_u_r_industry_lay = (LinearLayout) findViewById(R.id.emp_u_r_industry_lay);
        location = (AutoCompleteTextView) findViewById(R.id.emp_u_r_location);
        location.setThreshold(1);
        Button submit = (Button) findViewById(R.id.emp_u_tryEmpRegister);
        //submit button clicking,validate the page then update the details to database
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmployerProfile()) {
                    if (new UtilService()
                            .isNetworkAvailable(getApplicationContext())) {
                        new EmployerProfileUpdate().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private boolean validateEmployerProfile() {
                getCompName = companame.getText().toString();
                getEmail = email.getText().toString();
                getLocation = location.getText().toString();
                getMobileNo = phoneno.getText().toString();
                getContactPerson = contactperson.getText().toString();
                if (null == getCompName || getCompName.length() < 3) {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.companynamevalidation),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (null == getMobileNo || getMobileNo.length() != 10) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.pleaseenteravalidphno),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.industryvalidation), Toast.LENGTH_LONG).show();
                    return false;
                }
                if (null == getLocation || getLocation.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.locationvalidation), Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (locationList.size() > 0) {
                    if (!locationList.contains(getLocation)) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.locationwrongtoast),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.checkconnection),
                            Toast.LENGTH_SHORT).show();
                }
                if (getEmail.length() > 0) {
                    if (null == getEmail || (checkEmail(getEmail))) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.pleaseentervalidemail), Toast.LENGTH_LONG)
                                .show();
                        return false;
                    }
                }
                if (null == getContactPerson || getContactPerson.length() < 3) {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.contactpersonvalidation),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            private boolean checkEmail(String email) {
                return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
            }
        });
    }

    private void onbackclick() {
        if (companame.getText().toString().length() != 0 ||
                phoneno.getText().toString().length() != 0 ||
                !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry)) ||
                location.getText().toString().length() != 0 ||
                contactperson.getText().toString().length() != 0) {
            new BackAlertDialog().isBackDialog(Employer_Profile.this);
        } else {
            finish();
        }
    }

    //profile update webservices
    private class EmployerProfileUpdate extends AsyncTask<String, String, String> {
        String employerregisterresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Employer_Profile.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("Mobile", getMobileNo);
            paramsadd.addFormDataPart("EmailId", getEmail);
            paramsadd.addFormDataPart("location", getLocation);
            paramsadd.addFormDataPart("ContactPerson", getContactPerson);
            paramsadd.addFormDataPart("industry", getIndustry);
            paramsadd.addFormDataPart("Name", getCompName);
            paramsadd.addFormDataPart("action", "Update");
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_View_update.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                employerregisterresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (employerregisterresponse != null
                    && !employerregisterresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(employerregisterresponse);
                    int statuscode = responseObj.getInt("status_code");
                    String getUpdateStatus = responseObj.getString("status");
                    Toast.makeText(getBaseContext(), getUpdateStatus,
                            Toast.LENGTH_SHORT).show();
                    if (statuscode == 1) {
                        GlobalData.company_email = getEmail;
                        GlobalData.empusername = getCompName;
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Employer_Profile.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("ELS", GlobalData.emp_login_status);
                        editor.putString("EUN", GlobalData.empusername);
                        editor.putString("EEMAIL", GlobalData.company_email);
                        editor.apply();
                        finish();
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

    //select industry from employer profile page
    private void IndustryAlert() {
        final Dialog alertDialog = new Dialog(Employer_Profile.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Employer_Profile.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectindustry);
        Button industrydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterindustry = (ListView) emppromptView.findViewById(R.id.spinnerlist);
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
        filterindustry.setAdapter(industryAdapter);
        filterindustry.setSelection(indexindustry);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
        filterindustry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            industry.setText(getIndustry);
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang(getIndustry);
            }
        } else {
            industry.setText(getString(R.string.selectindustry));
        }
    }

    private void setIndustryLocalLang(String getIndustry) {
        Industry localindustry = new Industry();
        localindustry.setIndustry_name(getIndustry);
        indexindustry = industriesList.indexOf(localindustry);
        String IndustryLocal = industriesList.get(indexindustry).getIndustry_name_local();
        industry.setText(IndustryLocal);
    }

    //get the details of employer,list of locations and industry from database
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
            pg = new ProgressDialog(Employer_Profile.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("action", "View");
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update.php")
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
                    Employer employer = gson.fromJson(
                            responseObj.getString("list"),
                            new TypeToken<Employer>() {
                            }.getType());
                    companame.setText(employer.getCompanyName());
                    email.setText(employer.getEmailId());
                    location.setText(employer.getLocation());
                    phoneno.setText(employer.getMobile());
                    contactperson.setText(employer.getContactPerson());
                    getIndustry = employer.getIndustry();
                    GlobalData.empusername = employer.getCompanyName();
                    try {
                        industriesList = new ArrayList<>();
                        industriesList = gson.fromJson(responseObj.getString("industries"), new
                                TypeToken<ArrayList<Industry>>() {
                                }.getType());
                        emp_u_r_industry_lay.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (industriesList != null && industriesList.size() > 0) {
                                    IndustryAlert();
                                }
                            }
                        });
                        if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                            industry.setText(getIndustry);
                            if (!languages.equalsIgnoreCase("English")) {
                                setIndustryLocalLang(getIndustry);
                            }
                        } else {
                            industry.setText(R.string.selectindustry);
                        }
                        locationList = new ArrayList<>();
                        JSONArray locationgroups = responseObj
                                .getJSONArray("locations");
                        for (int i = 0; i < locationgroups.length(); i++) {
                            JSONObject c = locationgroups.getJSONObject(i);
                            String occupations_list_name = c
                                    .getString("citi_name");
                            locationList.add(occupations_list_name);
                        }
                        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(
                                Employer_Profile.this,
                                R.layout.spinner_item_text, locationList);
                        location.setAdapter(locAdapter);
                    } catch (JSONException ignored) {
                    }
                } catch (Exception e) {
                    Toast.makeText(Employer_Profile.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Employer_Profile.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
