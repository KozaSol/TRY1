package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.SpeciAdapter;
import com.jobsearchtry.adapter.SpinnerAdapter;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.CourseType;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.Qualification;
import com.jobsearchtry.wrapper.Role;
import com.jobsearchtry.wrapper.Specialization;
import com.jobsearchtry.utils.BackAlertDialog;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfile_ADD_Education extends Activity {
    private Button qualification, specialization, coursetype, passout_year;
    private EditText institute_name;
    private ProgressDialog pg;
    private LinearLayout js_edit_edu_speci_lay, js_edit_edu_add_quali_lay, js_edit_edu_coursetype_lay;
    private String getInsname, getPyear, languages,
            getspecialization, selectedQuali_ID,
            getQuali, getCT;
    private OkHttpClient client = null;
    private SpeciAdapter spadapter;
    ArrayList<String> select_passedoutyear = null;
    private ArrayList<Qualification> select_qualification = new ArrayList<>();
    private ArrayList<CourseType> select_coursetype = null;
    private ArrayAdapter<CourseType> coursetypeadapter;
    private ArrayList<Specialization> specificationList = new ArrayList<>();
    private int indexqual = -1, indexspec = -1, indexcoursetype = -1, indexpassout = -1;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education_add);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        getPyear = getString(R.string.selectpassoutyear);
        getspecialization = getString(R.string.selectspec);
        selectedQuali_ID = getString(R.string.selectquali);
        getQuali = getString(R.string.selectquali);
        getCT = getString(R.string.selectcoursetype);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getQualification().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton edu_add_h = (ImageButton) findViewById(R.id.js_r_h);
        edu_add_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_ADD_Education.this, Homepage.class));
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
        qualification = (Button) findViewById(R.id.js_edit_edu_add_quali);
        js_edit_edu_add_quali_lay = (LinearLayout) findViewById(R.id.js_edit_edu_add_quali_lay);
        js_edit_edu_speci_lay = (LinearLayout) findViewById(R.id.js_edit_edu_speci_lay);
        js_edit_edu_speci_lay.setVisibility(View.GONE);
        specialization = (Button) findViewById(R.id.js_edit_edu_speci);
        coursetype = (Button) findViewById(R.id.js_edit_edu_coursetype);
        js_edit_edu_coursetype_lay = (LinearLayout)
                findViewById(R.id.js_edit_edu_coursetype_lay);
        institute_name = (EditText) findViewById(R.id.js_edit_edu_institutename);
        passout_year = (Button) findViewById(R.id.js_edit_edu_passoutyear);
        LinearLayout js_edit_edu_passoutyear_lay = (LinearLayout) findViewById(R.id
                .js_edit_edu_passoutyear_lay);
        Button addeducation = (Button) findViewById(R.id.js_try_Education_Add_Submit);
        select_passedoutyear = new ArrayList<>();
        for (int i = 2016; i > 1970; i--) {
            select_passedoutyear.add("" + i);
        }
        js_edit_edu_coursetype_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTypeAlert();
            }
        });
        js_edit_edu_passoutyear_lay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PassoutYearAlert();
            }
        });
        addeducation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (educationValidation()) {
                    new addEducation().execute();
                }
            }

            private boolean educationValidation() {
                getInsname = institute_name.getText().toString();
                if (getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pleaseselectqualification),
                            Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
                if (specificationList.size() > 0) {
                    if (getspecialization.equalsIgnoreCase(getString(R.string.selectspec)) || getspecialization.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.pleaseselectthespeci), Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                } else {
                    getspecialization = " ";
                }
                if (null == getInsname || getInsname.length() < 3) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.enterinstitutename), Toast
                                    .LENGTH_LONG).show();
                    return false;
                }
                if (getCT.equalsIgnoreCase(getString(R.string.selectcoursetype))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.selectcoursetypeverify), Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
                if (getPyear.equalsIgnoreCase(getString(R.string.selectpassoutyear))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pleaseselectthepassoutyear), Toast.LENGTH_SHORT)
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

    private void onbackclick() {
        if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali)) ||
                institute_name.getText().toString().length() != 0 ||
                !getCT.equalsIgnoreCase(getString(R.string.selectcoursetype)) ||
                !getPyear.equalsIgnoreCase(getString(R.string.selectpassoutyear))) {
            new BackAlertDialog().isBackDialog(MyProfile_ADD_Education.this);
        } else {
            finish();
        }
    }

    //select coursetype from add education page
    private void CourseTypeAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Education.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Education.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectcoursetype);
        Button coursetypedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filtercoursetype = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getCT != null && !getCT.isEmpty() && !getCT.equalsIgnoreCase(getString(R.string.selectcoursetype))) {
            indexcoursetype = -1;
            if (select_coursetype != null && select_coursetype.size() > 0) {
                CourseType localcoursename = new CourseType();
                localcoursename.setCoursename(getCT);
                indexcoursetype = select_coursetype.indexOf(localcoursename);
            }
        } else {
            indexcoursetype = -1;
        }
        coursetypeadapter = new ArrayAdapter<CourseType>(this, R.layout.spinner_item_text, select_coursetype) {
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
                String yourValue = select_coursetype.get(position).getCoursename();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_coursetype.get(position).getCoursename_local();
                }
                if (indexcoursetype != -1 && (indexcoursetype == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filtercoursetype.setAdapter(coursetypeadapter);
        filtercoursetype.setSelection(indexcoursetype);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCourseType();
                alertDialog.dismiss();
            }
        });
        filtercoursetype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexcoursetype != -1 && (indexcoursetype == position)) {
                    getCT = getString(R.string.selectcoursetype);
                    indexcoursetype = -1;
                } else {
                    indexcoursetype = position;
                    getCT = select_coursetype.get(position).getCoursename();
                }
                setCourseType();
                coursetypeadapter.notifyDataSetChanged();
            }
        });
        coursetypedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCourseType();
                alertDialog.dismiss();
            }
        });
    }

    private void setCourseType() {
        if (getCT != null && !getCT.isEmpty() && !getCT.equalsIgnoreCase(getString(R.string.selectcoursetype))) {
            coursetype.setText(getCT);
            if (!languages.equalsIgnoreCase("English")) {
                setCourseNameLocalLang();
            }
        } else {
            coursetype.setText(getString(R.string.selectcoursetype));
        }
    }

    private void setCourseNameLocalLang() {
        CourseType localcoursename = new CourseType();
        localcoursename.setCoursename(getCT);
        int indexcoursetype = select_coursetype.indexOf(localcoursename);
        String CoursenameLocal = select_coursetype.get(indexcoursetype).getCoursename_local();
        coursetype.setText(CoursenameLocal);
    }

    //select passoutyear from add education page
    private void PassoutYearAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Education.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Education.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectpassoutyear);
        Button passoutdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterpassoutyear = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getPyear != null && !getPyear.isEmpty() && !getPyear.equalsIgnoreCase(getString(R.string.selectpassoutyear))) {
            indexpassout = -1;
            for (int i = 0; i < select_passedoutyear.size(); i++) {
                if (select_passedoutyear.get(i).equals(getPyear)) {
                    indexpassout = i;
                }
            }
        } else {
            indexpassout = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_passedoutyear) {
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
                String yourValue = select_passedoutyear.get(position);
                if (indexpassout != -1 && (indexpassout == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterpassoutyear.setAdapter(adapter);
        filterpassoutyear.setSelection(indexpassout);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassoutYear();
                alertDialog.dismiss();
            }
        });
        filterpassoutyear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexpassout != -1 && (indexpassout == position)) {
                    getPyear = getString(R.string.selectpassoutyear);
                    indexpassout = -1;
                } else {
                    indexpassout = position;
                    getPyear = select_passedoutyear.get(position);
                }
                setPassoutYear();
                adapter.notifyDataSetChanged();
            }
        });
        passoutdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassoutYear();
                alertDialog.dismiss();
            }
        });
    }

    private void setPassoutYear() {
        if (getPyear != null && !getPyear.isEmpty() && !getPyear.equalsIgnoreCase(getString(R.string.selectpassoutyear))) {
            passout_year.setText(getPyear);
        } else {
            passout_year.setText(R.string.selectpassoutyear);
        }
    }

    private class addEducation extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_ADD_Education.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("Qualification", selectedQuali_ID);
            if (getspecialization != null && !getspecialization.isEmpty()) {
                paramsadd.addFormDataPart("Specilisation", getspecialization);
            }
            paramsadd.addFormDataPart("institute_name", getInsname);
            paramsadd.addFormDataPart("course_type", getCT);
            paramsadd.addFormDataPart("passout_year", getPyear);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "education_save.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                randomkeysendresponse = response.body().string();
            } catch (IOException e) {
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
                    JSONObject responseObj = new JSONObject(randomkeysendresponse);
                    String getStatus = responseObj.getString("status");
                    Toast.makeText(getBaseContext(), getStatus, Toast.LENGTH_SHORT).show();
                    finish();
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

    //select quali from add education page
    private void QualificationAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Education.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Education.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectquali);
        Button qualidone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (!getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            indexqual = -1;
            for (int i = 0; i < select_qualification.size(); i++) {
                if (select_qualification.get(i).getQuali_name().equals(getQuali)) {
                    indexqual = i;
                }
            }
        } else {
            indexqual = -1;
        }
        final SpinnerAdapter adapter = new SpinnerAdapter(MyProfile_ADD_Education.this, R.layout.spinner_item_text, select_qualification) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = MyProfile_ADD_Education.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = select_qualification.get(position).getQuali_name();
                if (indexqual != -1 && (indexqual == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterquali.setAdapter(adapter);
        filterquali.setSelection(indexqual);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int pos, long arg3) {
                if (indexqual != -1 && (indexqual == pos)) {
                    getQuali = getString(R.string.selectquali);
                    indexqual = -1;
                    js_edit_edu_speci_lay.setVisibility(View.GONE);
                } else {
                    indexqual = pos;
                    getQuali = select_qualification.get(pos).getQuali_name();
                    getspecialization = getString(R.string.selectspec);
                    specialization.setText(getspecialization);
                    if (getQuali.equals(select_qualification.get(pos).getQuali_name())) {
                        selectedQuali_ID = select_qualification.get(pos).getId();
                        specificationList = new ArrayList<>();
                        if (select_qualification.get(pos).getSpecialization() == null || select_qualification.get(pos).getSpecialization().isEmpty() || select_qualification.get(pos)
                                .getSpecialization().size() == 0) {
                            js_edit_edu_speci_lay.setVisibility(View.GONE);
                        } else {
                            js_edit_edu_speci_lay.setVisibility(View.VISIBLE);
                            specificationList = new ArrayList<>();
                            for (int i = 0; i < select_qualification.size(); i++) {
                                if (select_qualification.get(i).getId().equals(selectedQuali_ID)) {
                                    specificationList.addAll(select_qualification.get(i)
                                            .getSpecialization());
                                }
                            }
                            js_edit_edu_speci_lay.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (specificationList.size() > 0) {
                                        SpecializationAlert();
                                    }
                                }
                            });
                        }
                    }
                }
                setQualification();
                adapter.notifyDataSetChanged();
            }
        });
        qualidone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setQualification();
                alertDialog.dismiss();
            }
        });
    }

    private void setQualification() {
        if (getQuali != null && !getQuali.isEmpty() && !getQuali.equalsIgnoreCase(getString(R.string.selectquali))) {
            qualification.setText(getQuali);
        } else {
            qualification.setText(R.string.selectquali);
        }
    }

    //select specialization from add education page
    private void SpecializationAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Education.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Education.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectspec);
        Button specialidone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterspecifi = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getspecialization != null && !getspecialization.isEmpty() && !getspecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
            indexspec = -1;
            for (int i = 0; i < specificationList.size(); i++) {
                if (specificationList.get(i).getSpeciali_name().equals(getspecialization)) {
                    indexspec = i;
                }
            }
        } else {
            indexspec = -1;
        }
        final SpeciAdapter adapter = new SpeciAdapter(MyProfile_ADD_Education.this, R.layout.spinner_item_text, specificationList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = MyProfile_ADD_Education.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = specificationList.get(position).getSpeciali_name();
                if (indexspec != -1 && (indexspec == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterspecifi.setAdapter(adapter);
        filterspecifi.setSelection(indexspec);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpecialization();
                alertDialog.dismiss();
            }
        });
        filterspecifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexspec != -1 && (indexspec == position)) {
                    getspecialization = getString(R.string.selectspec);
                    indexspec = -1;
                } else {
                    indexspec = position;
                    getspecialization = specificationList.get(position).getSpeciali_name();
                }
                setSpecialization();
                adapter.notifyDataSetChanged();
            }
        });
        specialidone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpecialization();
                alertDialog.dismiss();
            }
        });
    }

    private void setSpecialization() {
        if (getspecialization != null && !getspecialization.isEmpty() && !getspecialization.equalsIgnoreCase(getString(R.string.selectspec))) {
            specialization.setText(getspecialization);
        } else {
            specialization.setText(R.string.selectspec);
        }
    }

    private class getQualification extends AsyncTask<String, String, String> {
        String getqualilresponse;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(MyProfile_ADD_Education.this, R.style.MyTheme);
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
                request = new Request.Builder().url(GlobalData.url + "getqualificationlist.php").post
                        (requestBody).build();
            } else {
                request = new Request.Builder().url(
                        GlobalData.url + "getqualificationlist.php").build();
            }
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                getqualilresponse = response.body().string();
            } catch (IOException e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            try {
                JSONObject json = new JSONObject(getqualilresponse);
                Gson gson = new Gson();
                //qualification
                select_qualification = new ArrayList<>();
                select_qualification.addAll((Collection<? extends Qualification>) gson.fromJson
                        (json.getString("list"),
                                new TypeToken<ArrayList<Qualification>>() {
                                }.getType()));
                js_edit_edu_add_quali_lay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (select_qualification.size() > 0) {
                            QualificationAlert();
                        }
                    }
                });
                //coursetype
                select_coursetype = new ArrayList<>();
                select_coursetype = gson.fromJson(json.getString("coursetypes"), new
                        TypeToken<ArrayList<CourseType>>() {
                        }.getType());
                js_edit_edu_coursetype_lay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (select_coursetype != null && select_coursetype.size() > 0) {
                            CourseTypeAlert();
                        }
                    }
                });
            } catch (JSONException ignored) {
            }
        }
    }
}

