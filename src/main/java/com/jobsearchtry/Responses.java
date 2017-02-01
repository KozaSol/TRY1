package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.ProfileAutoCompleteAdapter;
import com.jobsearchtry.adapter.ResponsesAutoCompleteAdapter;
import com.jobsearchtry.adapter.ResponsesList_Adpater;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Responses extends Activity {
    private static final String M_RF = "responsesfilter";
    private static int profileindex = 0, getcount = 0;
    private ImageView filter, clear, back, search;
    private TextView responses_emptymessage;
    private ResponsesList_Adpater myresponsesAdapter;
    private EditText searchvalue;
    private ListView responselist, profilesearchlist;
    private LinearLayout profilesearchlistview;
    public static Callback callback;
    private ResponsesAutoCompleteAdapter searchAdapter;
    private ProgressDialog pg;
    private String getGender, getQualification, getQualificationID, getExperience, getAge,
            getStatus, getJobTitle, languages,
            getFilterStatus, getStatusChar, getLanguages;
    private String getRole, getSkill;
    private boolean isCheckedKeyword = false;

    @Override
    public void onBackPressed() {
        if (profilesearchlistview.getVisibility() == View.VISIBLE) {
            profilesearchlistview.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
            searchvalue.setCursorVisible(false);
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        } else {
            startActivity(new Intent(Responses.this, EmployerDashboard.class));
        }
    }

    //This class implements an application that getting the joblist who are applied a job
    // (employer-responses).
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.responses);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(
                (Responses.this));
        GlobalData.ResKeyword = sharedPreferences1.getString("RESKEY", GlobalData.ResKeyword);
        GlobalData.SEKeyword = sharedPreferences1.getString("SEKEY", GlobalData.SEKeyword);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (Responses.this);
        Editor editor = sharedPreferences.edit();
        clear = (ImageView) findViewById(R.id.responses_searchjobvalue_clear);
        if (GlobalData.SEKeyword != null && !GlobalData.SEKeyword.isEmpty()) {
            GlobalData.SEKeyword = " ";
            editor.putString("SEKEY", GlobalData.SEKeyword);
            clear.setVisibility(View.GONE);
        }
        editor.apply();
        profilesearchlistview = (LinearLayout) findViewById(R.id.res_profilesearchlistview);
        profilesearchlist = (ListView) findViewById(R.id.res_profilesearchlist);
        back = (ImageView) findViewById(R.id.responses_searchjobvalue_back);
        search = (ImageView) findViewById(R.id.responses_searchjobvalue_search);
        responselist = (ListView) findViewById(R.id.responseslist);
        responses_emptymessage = (TextView) findViewById(R.id.responses_emptymessage);
        responselist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (GlobalData.responsesList.size() > 0) {
                    int threshold = 1;
                    int count = GlobalData.responsesList.size();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (responselist.getLastVisiblePosition() >= count
                                - threshold) {
                            if (getcount == 1) {
                                String apply_id = GlobalData.responsesList.get(count - 1).getApply_id();
                                if (new UtilService().isNetworkAvailable(Responses.this)) {
                                    new getResponseslistScroll().execute(apply_id);
                                } else {
                                    Toast.makeText(Responses.this, getString(R.string.checkconnection), Toast
                                            .LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        searchvalue = (AutoCompleteTextView) findViewById(R.id.responsessearchvalue);
        searchvalue.setCursorVisible(false);
        back.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                profilesearchlistview.setVisibility(View.GONE);
                GlobalData.ResKeyword = null;
                searchvalue.setText("");
                getSearchKeyword();
                clear.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                searchvalue.setCursorVisible(false);
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
                //if (GlobalData.employeeList.size() > 0) {
                //    setEmployeeListing();
                //} else {
                if (new UtilService().isNetworkAvailable(Responses.this)) {
                    new My_Responses().execute();
                } else {
                    Toast.makeText(Responses.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT)
                            .show();
                }
                // }
            }
        });

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.ResKeyword = null;
                getSearchKeyword();
                searchvalue.setText("");
                clear.setVisibility(View.GONE);
                filter.setVisibility(View.VISIBLE);
            }
        });
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message message) {
                profilesearchlistview.setVisibility(View.GONE);
                Bundle b = message.getData();
                Integer value = b.getInt("KEY");
                if (value != 0) {
                    responselist.setVisibility(View.VISIBLE);
                    responses_emptymessage.setVisibility(View.GONE);
                } else {
                    responses_emptymessage.setVisibility(View.VISIBLE);
                    responselist.setVisibility(View.GONE);
                }
                return false;
            }
        };

        ImageButton respo_h = (ImageButton) findViewById(R.id.js_r_h);
        respo_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Responses.this, EmployerDashboard.class));
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        filter = (ImageView) findViewById(R.id.responses_home_filter);
        filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Responses.this, Responses_Filter.class));
            }
        });
        getResponseData();
    }

    private void getSearchKeyword() {
        GlobalData.ResKeyword = searchvalue.getText().toString().trim();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Responses.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("RESKEY", GlobalData.ResKeyword);
        editor.apply();
    }

    private void getEditKeyword() {
        profilesearchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.spinneritemqualification);
                searchvalue.setText(tv.getText().toString());
                getFilterJobs();
            }
        });
        searchvalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getFilterJobs();
                    return true;
                }
                return false;
            }
        });
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                searchvalue.setCursorVisible(true);
                searchKeywordTouch();
            }
        });
        searchvalue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchvalue.setCursorVisible(true);
                searchKeywordTouch();
                return false;
            }
        });

        searchvalue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int
                    count) {
                GlobalData.responsesuggestionList = new ArrayList<>();
                if (GlobalData.responsesuggestion.size() > 0) {
                    GlobalData.responsesuggestionList.addAll(GlobalData.responsesuggestion);
                }
                Set<String> hs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                hs.addAll(GlobalData.responsesuggestionList);
                GlobalData.responsesuggestionList.clear();
                GlobalData.responsesuggestionList.addAll(hs);
                if (s.toString().length() > 0 && GlobalData.responsesuggestionList.size() > 0) {
                    clear.setVisibility(View.VISIBLE);
                    filter.setVisibility(View.GONE);
                    getSearchKeyword();
                    searchAdapter = new ResponsesAutoCompleteAdapter(Responses.this, GlobalData.responsesuggestionList);
                    profilesearchlist.setAdapter(searchAdapter);
                    searchAdapter.filter(s.toString());
                } else {
                    GlobalData.ResKeyword = null;
                    getSearchKeyword();
                    clear.setVisibility(View.GONE);
                    filter.setVisibility(View.VISIBLE);
                    if (GlobalData.responsesuggestion.size() > 0 && searchvalue.getText().toString().trim().length() == 0) {
                        searchAdapter = new ResponsesAutoCompleteAdapter(Responses.this, GlobalData.responsesuggestion);
                        profilesearchlist.setAdapter(searchAdapter);
                        searchAdapter.filter("");
                    } else {
                        if (GlobalData.responsesuggestionList.size() > 0 && searchAdapter != null) {
                            searchAdapter.clearallitems();
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int
                    after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchKeywordTouch() {
        searchvalue.setCursorVisible(true);
        profilesearchlistview.setVisibility(View.VISIBLE);
        search.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        if (GlobalData.responsesuggestion.size() == 0 && searchvalue.getText().toString().trim().length() == 0) {
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
        } else if (GlobalData.responsesuggestion.size() > 0 && searchvalue.getText().toString().trim().length() == 0) {
            searchAdapter = new ResponsesAutoCompleteAdapter(Responses.this, GlobalData.responsesuggestion);
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
            profilesearchlist.setAdapter(searchAdapter);
        } else if (searchvalue.getText().toString().trim().length() > 0 && GlobalData.responsesuggestionList.size() > 0) {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
            searchAdapter = new ResponsesAutoCompleteAdapter(Responses.this, GlobalData.responsesuggestionList);
            profilesearchlist.setAdapter(searchAdapter);
            searchAdapter.filter(searchvalue.getText().toString().trim());
        } else {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
        }
    }

    private void getFilterJobs() {
        profilesearchlistview.setVisibility(View.GONE);
        GlobalData.ResKeyword = searchvalue.getText().toString().trim();
        searchvalue.setCursorVisible(false);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        GlobalData.responsesuggestionList = new ArrayList<>();
        if (GlobalData.responsesuggestion.size() > 0) {
            GlobalData.responsesuggestionList.addAll(GlobalData.responsesuggestion);
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(GlobalData.responsesuggestionList);
        GlobalData.responsesuggestionList.clear();
        GlobalData.responsesuggestionList.addAll(hs);

        getSearchKeyword();
        if (new UtilService().isNetworkAvailable(Responses.this)) {
            new My_Responses().execute();
        } else {
            Toast.makeText(Responses.this, getString(R.string.checkconnection), Toast.LENGTH_SHORT).show();
        }
        if (GlobalData.getResponseSearchKeywords != null && !GlobalData.getResponseSearchKeywords
                .isEmpty()) {
            String[] viewcateadd = GlobalData.getResponseSearchKeywords.split(",");
            Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(GlobalData.responsesuggestion);
            for (String aViewcateadd : viewcateadd) {
                if (!set.contains(aViewcateadd)) {
                    GlobalData.responsesuggestion.add(aViewcateadd);
                }
            }
        }
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(GlobalData.responsesuggestion);
        if (!set.contains(GlobalData
                .ResKeyword) && GlobalData.ResKeyword.length() > 0) {
            if (GlobalData.responsesuggestion.size() < 2) {
                GlobalData.responsesuggestion.add(0, GlobalData.ResKeyword);
            } else {
                GlobalData.responsesuggestion.add(0, GlobalData.ResKeyword);
                GlobalData.responsesuggestion.remove(2);
            }
        }
        if (GlobalData.responsesuggestion.size() > 0) {
            String[] getkeywords = new String[GlobalData.responsesuggestion.size()];
            for (int k = 0; k < GlobalData.responsesuggestion.size(); k++) {
                getkeywords[k] = GlobalData.responsesuggestion.get(k);
            }
            if (getkeywords.length > 0) {
                String mostviewedrole = Arrays.toString(getkeywords);
                mostviewedrole = mostviewedrole.substring(1, mostviewedrole.length() - 1);
                mostviewedrole = mostviewedrole.replace(", ", ",");
                GlobalData.getResponseSearchKeywords = mostviewedrole;
                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(Responses.this);
                Editor editor1 = sharedPreferences1.edit();
                editor1.putString("RKS", GlobalData.getResponseSearchKeywords);
                editor1.apply();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        profileindex = responselist.getFirstVisiblePosition();
    }

    private void getResponseData() {
        SharedPreferences responsefilterPreferences = getSharedPreferences(M_RF,
                Context.MODE_PRIVATE);
        getJobTitle = responsefilterPreferences.getString("F_R_J", getJobTitle);
        getQualification = responsefilterPreferences.getString("F_R_Q", getQualification);
        getQualificationID = responsefilterPreferences.getString("F_R_QID", getQualificationID);
        getGender = responsefilterPreferences.getString("F_R_G", getGender);
        getExperience = responsefilterPreferences.getString("F_R_E", getExperience);
        getAge = responsefilterPreferences.getString("F_R_A", getAge);
        // getRole = responsefilterPreferences.getString("F_R_R", getRole);
        //getSkill = responsefilterPreferences.getString("F_R_SK", getSkill);
        getLanguages = responsefilterPreferences.getString("F_R_L", getLanguages);
        getStatus = responsefilterPreferences.getString("F_R_S", getStatus);
        getStatusChar = responsefilterPreferences.getString("F_R_SS", getStatusChar);
        GlobalData.responsesfilterresponse = responsefilterPreferences.getString("RF",
                GlobalData.responsesfilterresponse);
        GlobalData.ResKeyword = responsefilterPreferences.getString("RESKEY", GlobalData
                .ResKeyword);
        GlobalData.login_status = responsefilterPreferences.getString("ELS", GlobalData
                .login_status);
        if ((getJobTitle == null || getJobTitle.isEmpty()) && (getQualification == null ||
                getQualification.isEmpty())
                && (getQualificationID == null || getQualificationID.isEmpty())
                && (getGender == null || getGender.isEmpty())
                //&& (getRole == null || getRole.isEmpty())
                && (getExperience == null || getExperience.isEmpty())
                && (getAge == null || getAge.isEmpty())
                //&& (getSkill == null || getSkill.isEmpty())
                && (getLanguages == null || getLanguages.isEmpty())
                && (getStatus == null || getStatus.isEmpty()) && (getStatusChar == null ||
                getStatusChar.isEmpty())) {
            filter.setImageResource(R.drawable.filter_icon);
            if (new UtilService().isNetworkAvailable(Responses.this)) {
                new My_Responses().execute();
            } else {
                Toast.makeText(Responses.this, getString(R.string.checkconnection), Toast
                        .LENGTH_SHORT).show();
            }
        } else {
            filter.setImageResource(R.drawable.filter_tick);
            getFilterResponse();
        }
    }

    private void getFilterResponse() {
        if (!isCheckedKeyword) {
            getEditKeyword();
            isCheckedKeyword = true;
        }
        if (GlobalData.responsesfilterresponse != null
                && !GlobalData.responsesfilterresponse.contains("connectionFailure")) {
            try {
                JSONObject responseObj = new JSONObject(GlobalData.responsesfilterresponse);
                String getFilterStatus = responseObj.getString("message");
                getcount = responseObj.getInt("count");
                if (getFilterStatus.equalsIgnoreCase("success")) {
                    GlobalData.responsesList = new ArrayList<>();
                    Gson gson = new Gson();
                    GlobalData.responsesList = gson.fromJson(responseObj.getString
                                    ("jobseeker_profile"),
                            new TypeToken<ArrayList<User>>() {
                            }.getType());
                    checkEmptyrespose();
                } else {
                    responselist.setVisibility(View.GONE);
                    responses_emptymessage.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
            }
        } else {
            filter.setImageResource(R.drawable.filter_icon);
            if (new UtilService().isNetworkAvailable(Responses.this)) {
                new My_Responses().execute();
            } else {
                Toast.makeText(Responses.this, getString(R.string.checkconnection), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private void checkEmptyrespose() {
        if (GlobalData.responsesList.size() != 0) {
            int currentPosition = responselist.getFirstVisiblePosition();
            myresponsesAdapter = new ResponsesList_Adpater(Responses.this, GlobalData
                    .responsesList);
            responselist.setAdapter(myresponsesAdapter);
            responselist.setSelection(profileindex);
            responselist.setSelectionFromTop(currentPosition, 0);
            if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
                //myresponsesAdapter.filter(GlobalData.ResKeyword);
                if (!GlobalData.ResKeyword.equalsIgnoreCase(" ")) {
                    searchvalue.setText(GlobalData.ResKeyword);
                    clear.setVisibility(View.VISIBLE);
                } else {
                    searchvalue.setText("");
                    clear.setVisibility(View.GONE);
                }
            }
            responselist.setVisibility(View.VISIBLE);
            responses_emptymessage.setVisibility(View.GONE);
        } else {
            responselist.setVisibility(View.GONE);
            responses_emptymessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getResponseData();
        searchvalue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.responsesfilterresponse);
                    getFilterStatus = responseObj.getString("message");
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        //  myresponsesAdapter.filter(s.toString());
                        if (s.toString().length() > 0) {
                            clear.setVisibility(View.VISIBLE);
                        } else {
                            clear.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private class My_Responses extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Responses.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("language", languages);
            }
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
            /*if (getSkill != null && !getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", getSkill);
            }*/
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            if (getStatusChar != null && !getStatusChar.isEmpty()) {
                paramsadd.addFormDataPart("Status", getStatusChar);
            }
            GlobalData.ResKeyword = searchvalue.getText().toString().trim();
            if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
                paramsadd.addFormDataPart("keyword", GlobalData.ResKeyword);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "applied_job_search.php")
                    .post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
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
            if (!isCheckedKeyword) {
                getEditKeyword();
                isCheckedKeyword = true;
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (Responses.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("RF", GlobalData.responsesfilterresponse);
            editor.apply();
            if (GlobalData.responsesfilterresponse != null
                    && !GlobalData.responsesfilterresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.responsesfilterresponse);
                    getFilterStatus = responseObj.getString("message");
                    getcount = responseObj.getInt("count");
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        GlobalData.responsesList = new ArrayList<>();
                        Gson gson = new Gson();
                        GlobalData.responsesList = gson.fromJson(responseObj.getString
                                        ("jobseeker_profile"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        checkEmptyrespose();
                    } else {
                        responselist.setVisibility(View.GONE);
                        responses_emptymessage.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(Responses.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(Responses.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }


    private class getResponseslistScroll extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Responses.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("language", languages);
            }
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("scroll", "scroll");
            paramsadd.addFormDataPart("apply_id", args[0]);
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
            /*if (getSkill != null && !getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", getSkill);
            }*/
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            if (getStatusChar != null && !getStatusChar.isEmpty()) {
                paramsadd.addFormDataPart("Status", getStatusChar);
            }
            GlobalData.ResKeyword = searchvalue.getText().toString().trim();
            if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
                paramsadd.addFormDataPart("keyword", GlobalData.ResKeyword);
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "applied_job_search.php")
                    .post(requestBody).build();

            OkHttpClient client = new OkHttpClient();
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
            if (!isCheckedKeyword) {
                getEditKeyword();
                isCheckedKeyword = true;
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (Responses.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("RF", GlobalData.responsesfilterresponse);
            editor.apply();
            if (GlobalData.responsesfilterresponse != null
                    && !GlobalData.responsesfilterresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.responsesfilterresponse);
                    getFilterStatus = responseObj.getString("message");
                    getcount = responseObj.getInt("count");
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        ArrayList<User> resposnsestemp = gson.fromJson(responseObj.getString
                                        ("jobseeker_profile"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        GlobalData.responsesList.addAll(resposnsestemp);
                        checkEmptyrespose();
                    } else {
                        responselist.setVisibility(View.GONE);
                        responses_emptymessage.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(Responses.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(Responses.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}