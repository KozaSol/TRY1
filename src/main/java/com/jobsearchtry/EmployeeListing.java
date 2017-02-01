package com.jobsearchtry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.EmployeeList_Adpater;
import com.jobsearchtry.adapter.ProfileAutoCompleteAdapter;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Handler.Callback;

public class EmployeeListing extends Activity implements GoogleApiClient.ConnectionCallbacks, com
        .google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private ImageView filter, clear, back, search;
    private TextView employeelist_empty;
    private EmployeeList_Adpater myemployeesAdapter;
    private AutoCompleteTextView searchvalue;
    private ListView employeelist, profilesearchlist;
    private LinearLayout profilesearchlistview;
    private ProgressDialog pg;
    private String getQualification, getQualificationID, getAge, getGender, getSkill, getExp, getRole,
            getIndustry, getLocation, languages,
            getLanguages, getFilterStatus;
    private static final String M_SP = "searchprofile";
    private OkHttpClient client = null;
    private int profileindex = 0, getcount = 0;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private ProfileAutoCompleteAdapter searchAdapter;
    private boolean isCheckedKeyword = false;
    public static Callback callback;

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
            startActivity(new Intent(EmployeeListing.this, EmployerDashboard.class));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeelist);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(
                (EmployeeListing.this));
        GlobalData.ResKeyword = sharedPreferences1.getString("RESKEY", GlobalData.ResKeyword);
        GlobalData.SEKeyword = sharedPreferences1.getString("SEKEY", GlobalData.SEKeyword);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (EmployeeListing.this);
        Editor editor = sharedPreferences.edit();
        clear = (ImageView) findViewById(R.id.emp_searchjobvalue_clear);
        if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
            GlobalData.ResKeyword = " ";
            editor.putString("RESKEY", GlobalData.ResKeyword);
        }
        editor.apply();
        profilesearchlistview = (LinearLayout) findViewById(R.id.profilesearchlistview);
        profilesearchlist = (ListView) findViewById(R.id.profilesearchlist);
        back = (ImageView) findViewById(R.id.emp_searchjobvalue_back);
        search = (ImageView) findViewById(R.id.emp_searchjobvalue_search);
        employeelist = (ListView) findViewById(R.id.employeelist);
        employeelist_empty = (TextView) findViewById(R.id.employeelist_empty);
        employeelist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (GlobalData.employeeList.size() > 0) {
                    int threshold = 1;
                    int count = GlobalData.employeeList.size();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (employeelist.getLastVisiblePosition() >= count
                                - threshold) {
                            if (getcount == 1) {
                                String jobseeker_id = GlobalData.employeeList.get(count - 1).getId();
                                if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                                    new My_EmployeesScroll().execute(jobseeker_id);
                                } else {
                                    Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast
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

        searchvalue = (AutoCompleteTextView) findViewById(R.id.emp_searchjobvalue);
        searchvalue.setCursorVisible(false);
        back.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                profilesearchlistview.setVisibility(View.GONE);
                GlobalData.SEKeyword = null;
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
                if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                    new My_Employees().execute();
                } else {
                    Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT)
                            .show();
                }
                // }
            }
        });

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.SEKeyword = null;
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
                    employeelist.setVisibility(View.VISIBLE);
                    employeelist_empty.setVisibility(View.GONE);
                } else {
                    employeelist_empty.setVisibility(View.VISIBLE);
                    employeelist.setVisibility(View.GONE);
                }
                return false;
            }
        };

        ImageButton emplist_head = (ImageButton) findViewById(R.id.js_r_h);
        emplist_head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployeeListing.this, EmployerDashboard.class));
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeListing.this, EmployerDashboard.class));
            }
        });
        filter = (ImageView) findViewById(R.id.emp_home_filter);
        filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.getdefaultLocation = false;
                startActivity(new Intent(EmployeeListing.this, SearchProfile_Filter.class));
            }
        });
        if (GlobalData.getdefaultLocation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(EmployeeListing.this, android.Manifest.permission
                        .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission
                            .ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationconnected();
                }
            } else {
                locationconnected();
            }
        }
    }

    private void getSearchKeyword() {
        GlobalData.SEKeyword = searchvalue.getText().toString().trim();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(EmployeeListing.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("SEKEY", GlobalData.SEKeyword);
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
                if (GlobalData.profilemainsuggestionList.size() > 0) {
                    GlobalData.profilesuggestionList = new ArrayList<>();
                    if (GlobalData.profilesuggestion.size() > 0) {
                        GlobalData.profilesuggestionList.addAll(GlobalData.profilesuggestion);
                    }
                    GlobalData.profilesuggestionList.addAll(GlobalData.profilemainsuggestionList);
                    Set<String> hs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    hs.addAll(GlobalData.profilesuggestionList);
                    GlobalData.profilesuggestionList.clear();
                    GlobalData.profilesuggestionList.addAll(hs);
                }
                if (s.toString().length() > 0 && GlobalData.profilesuggestionList.size() > 0) {
                    clear.setVisibility(View.VISIBLE);
                    filter.setVisibility(View.GONE);
                    getSearchKeyword();
                    searchAdapter = new ProfileAutoCompleteAdapter(EmployeeListing.this, GlobalData.profilesuggestionList);
                    profilesearchlist.setAdapter(searchAdapter);
                    searchAdapter.filter(s.toString());
                } else {
                    GlobalData.SEKeyword = null;
                    getSearchKeyword();
                    clear.setVisibility(View.GONE);
                    filter.setVisibility(View.VISIBLE);
                    if (GlobalData.profilesuggestion.size() > 0 && searchvalue.getText().toString().trim().length() == 0) {
                        searchAdapter = new ProfileAutoCompleteAdapter(EmployeeListing.this, GlobalData.profilesuggestion);
                        profilesearchlist.setAdapter(searchAdapter);
                        searchAdapter.filter("");
                    } else {
                        if (GlobalData.profilesuggestionList.size() > 0 && searchAdapter != null) {
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
        if (GlobalData.profilesuggestion.size() == 0 && searchvalue.getText().toString().trim().length() == 0) {
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
        } else if (GlobalData.profilesuggestion.size() > 0 && searchvalue.getText().toString().trim().length() == 0) {
            searchAdapter = new ProfileAutoCompleteAdapter(EmployeeListing.this, GlobalData.profilesuggestion);
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
            profilesearchlist.setAdapter(searchAdapter);
        } else if (searchvalue.getText().toString().trim().length() > 0 && GlobalData.profilesuggestionList.size() > 0) {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
            searchAdapter = new ProfileAutoCompleteAdapter(EmployeeListing.this, GlobalData.profilesuggestionList);
            profilesearchlist.setAdapter(searchAdapter);
            searchAdapter.filter(searchvalue.getText().toString().trim());
        } else {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
        }
    }

    private void getFilterJobs() {
        /*if (employeelist_empty.getVisibility() == View.VISIBLE) {
            if (GlobalData.employeeList.size() > 0) {
                setEmployeeListing();
            } else {
                if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                    new My_Employees().execute();
                } else {
                    Toast.makeText(EmployeeListing.this, "Please check the internet connection", Toast
                            .LENGTH_SHORT)
                            .show();
                }
            }
        }*/
        profilesearchlistview.setVisibility(View.GONE);
        GlobalData.SEKeyword = searchvalue.getText().toString().trim();
        searchvalue.setCursorVisible(false);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (GlobalData.profilemainsuggestionList.size() > 0) {
            GlobalData.profilesuggestionList = new ArrayList<>();
            if (GlobalData.profilesuggestion.size() > 0) {
                GlobalData.profilesuggestionList.addAll(GlobalData.profilesuggestion);
            }
            GlobalData.profilesuggestionList.addAll(GlobalData.profilemainsuggestionList);
            Set<String> hs = new HashSet<>();
            hs.addAll(GlobalData.profilesuggestionList);
            GlobalData.profilesuggestionList.clear();
            GlobalData.profilesuggestionList.addAll(hs);
        }
        getSearchKeyword();
        // if (GlobalData.SEKeyword.length() > 0) {
        if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
            new My_Employees().execute();
        } else {
            Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT)
                    .show();
        }
        if (GlobalData.getProfileSearchKeywords != null && !GlobalData.getProfileSearchKeywords
                .isEmpty()) {
            String[] viewcateadd = GlobalData.getProfileSearchKeywords.split(",");
            Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(GlobalData.profilesuggestion);
            for (String aViewcateadd : viewcateadd) {
                if (!set.contains(aViewcateadd)) {
                    GlobalData.profilesuggestion.add(aViewcateadd);
                }
            }
        }
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(GlobalData.profilesuggestion);
        if (!set.contains(GlobalData
                .SEKeyword) && GlobalData.SEKeyword.length() > 0) {
            if (GlobalData.profilesuggestion.size() < 2) {
                GlobalData.profilesuggestion.add(0, GlobalData.SEKeyword);
            } else {
                GlobalData.profilesuggestion.add(0, GlobalData.SEKeyword);
                GlobalData.profilesuggestion.remove(2);
            }
        }
        if (GlobalData.profilesuggestion.size() > 0) {
            String[] getkeywords = new String[GlobalData.profilesuggestion.size()];
            for (int k = 0; k < GlobalData.profilesuggestion.size(); k++) {
                getkeywords[k] = GlobalData.profilesuggestion.get(k);
            }
            if (getkeywords.length > 0) {
                String mostviewedrole = Arrays.toString(getkeywords);
                mostviewedrole = mostviewedrole.substring(1, mostviewedrole.length() - 1);
                mostviewedrole = mostviewedrole.replace(", ", ",");
                GlobalData.getProfileSearchKeywords = mostviewedrole;
                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(EmployeeListing.this);
                Editor editor1 = sharedPreferences1.edit();
                editor1.putString("SPKS", GlobalData.getProfileSearchKeywords);
                editor1.apply();
            }
        }
//        } else {
//            profilesearchlistview.setVisibility(View.GONE);
//            employeelist_empty.setVisibility(View.VISIBLE);
//            employeelist.setVisibility(View.GONE);
//        }
    }

    private void locationconnected() {
        googleApiClient = new GoogleApiClient.Builder(EmployeeListing.this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(10 * 1000).setFastestInterval(1000);
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(EmployeeListing.this, 1000);
                        } catch (IntentSender.SendIntentException ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        profileindex = employeelist.getFirstVisiblePosition();
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(EmployeeListing.this, android.Manifest
                    .permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission
                        .ACCESS_FINE_LOCATION}, 1);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                        mLocationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    mLocationRequest, this);
        }
        displayLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (googleApiClient.isConnected()) {
                            startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        GlobalData.getdefaultLocation = false;
                        break;
                }
            }
        } else {
            GlobalData.getdefaultLocation = false;
            if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                new My_Employees().execute();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    locationconnected();
                } else {
                    GlobalData.getdefaultLocation = false;
                    Toast.makeText(EmployeeListing.this, "Permission Denied,You cannot access location " +
                            "data", Toast.LENGTH_SHORT).show();
                    GlobalData.getLocation = null;
                    filter.setImageResource(R.drawable.filter_icon);
                    if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                        new My_Employees().execute();
                    } else {
                        Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
        }
    }

    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (googleApiClient.isConnected()) {
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                if (latitude == 0.0d || longitude == 0.0d) {
                    Toast.makeText(EmployeeListing.this, "Empty Lat/Lon", Toast.LENGTH_SHORT).show();
                } else {
                    if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                        new CityAsyncTask(latitude, longitude).execute();
                    } else {
                        Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(EmployeeListing.this, android.Manifest
                            .permission.ACCESS_FINE_LOCATION) != PackageManager
                            .PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission
                                .ACCESS_FINE_LOCATION}, 1);
                    } else {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                                mLocationRequest, this);
                    }
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                            mLocationRequest, this);
                }
            }
        }
    }

    private class CityAsyncTask extends AsyncTask<String, String, String> {
        final double latitude;
        final double longitude;
        String getlocationresponse = null, addresses1 = "";

        private CityAsyncTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + this
                            .latitude + "," + this.longitude + "&sensor=true")
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                getlocationresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (getlocationresponse != null) {
                    JSONObject jsonObj = new JSONObject(getlocationresponse);
                    if (jsonObj.getString("status").equalsIgnoreCase("OK")) {
                        JSONArray address_components = jsonObj.getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("address_components");
                        for (int i = 0; i < address_components.length(); i++) {
                            JSONObject zero2 = address_components.getJSONObject(i);
                            String Type = zero2.getJSONArray("types").getString(0);
                            if (Type.equalsIgnoreCase("locality")) {
                                addresses1 = zero2.getString("long_name");
                            }
                            if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                GlobalData.getHomeState = zero2.getString("long_name");
                                SharedPreferences jobfilterPreferences = PreferenceManager.getDefaultSharedPreferences(
                                        (EmployeeListing.this));
                                jobfilterPreferences.edit().putString("F_HS", GlobalData
                                        .getHomeState).apply();
                            }
                        }
                        if (getLocation == null || getLocation.isEmpty()) {
                            if (GlobalData.getdefaultLocation) {
                                if (addresses1 == null || addresses1.isEmpty()) {
                                    if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                                        new My_Employees().execute();
                                    } else {
                                        Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLocation = addresses1;
                                    SharedPreferences jobfilterPreferences = getSharedPreferences(M_SP,
                                            Context.MODE_PRIVATE);
                                    jobfilterPreferences.edit().putString("F_P_L", getLocation)
                                            .apply();
                                    filter.setImageResource(R.drawable.filter_tick);
                                    if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                                        new My_Employees().execute();
                                    } else {
                                        Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException ignored) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences searchprofilefilterPreferences = getSharedPreferences(M_SP,
                Context.MODE_PRIVATE);
        getQualification = searchprofilefilterPreferences.getString("F_P_Q", getQualification);
        getQualificationID = searchprofilefilterPreferences.getString("F_P_QID",
                getQualificationID);
        getGender = searchprofilefilterPreferences.getString("F_P_G", getGender);
        getAge = searchprofilefilterPreferences.getString("F_P_A", getAge);
        getSkill = searchprofilefilterPreferences.getString("F_P_S", getSkill);
        getExp = searchprofilefilterPreferences.getString("F_P_E", getExp);
        getRole = searchprofilefilterPreferences.getString("F_P_R", getRole);
        getIndustry = searchprofilefilterPreferences.getString("F_P_I", getIndustry);
        getLocation = searchprofilefilterPreferences.getString("F_P_L", getLocation);
        getLanguages = searchprofilefilterPreferences.getString("F_P_LA", getLanguages);
        GlobalData.getHomeState = searchprofilefilterPreferences.getString("F_HS", GlobalData
                .getHomeState);
        GlobalData.SEKeyword = searchprofilefilterPreferences.getString("SEKEY", GlobalData
                .SEKeyword);
        GlobalData.employerfilterresponse = searchprofilefilterPreferences.getString("SF",
                GlobalData.employerfilterresponse);
        GlobalData.emp_login_status = searchprofilefilterPreferences.getString("ELS", GlobalData
                .emp_login_status);
        GlobalData.getProfileSearchKeywords = searchprofilefilterPreferences.getString("SPKS", GlobalData.getProfileSearchKeywords);
        if ((getLocation == null || getLocation.isEmpty()) && (getGender == null || getGender
                .isEmpty())
                && (getAge == null || getAge.isEmpty()) && (getQualification == null ||
                getQualification.isEmpty())
                && (getQualificationID == null || getQualificationID.isEmpty())
                && (getSkill == null || getSkill.isEmpty()) && (getExp == null || getExp.isEmpty())
                && (getLanguages == null || getLanguages.isEmpty()) && (getRole == null || getRole
                .isEmpty())
                && (getIndustry == null || getIndustry.isEmpty())) {
            filter.setImageResource(R.drawable.filter_icon);
        } else {
            filter.setImageResource(R.drawable.filter_tick);
        }
        if (GlobalData.employeeList.size() > 0) {
            setEmployeeListing();
        } else {
            if (new UtilService().isNetworkAvailable(EmployeeListing.this)) {
                new My_Employees().execute();
            } else {
                Toast.makeText(EmployeeListing.this, getString(R.string.checkconnection), Toast
                        .LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*private void displayProfiles() {
        try {
            if (!isCheckedKeyword) {
                getEditKeyword();
                isCheckedKeyword = true;
            }
            JSONObject responseObj = new JSONObject(GlobalData.employerfilterresponse);
            getFilterStatus = responseObj.getString("message");
            getcount = responseObj.getInt("count");
            if (getFilterStatus.equalsIgnoreCase("success")) {
                GlobalData.employeeList = new ArrayList<>();
                Gson gson = new Gson();
                GlobalData.employeeList = gson.fromJson(responseObj.getString("profile_view"),
                        new TypeToken<ArrayList<User>>() {
                        }.getType());
            }
            setEmployeeListing();
        } catch (Exception ignored) {
        }
    }*/

    private class My_Employees extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(EmployeeListing.this, R.style.MyTheme);
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
            if (getGender != null && !getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", getGender);
            }
            if (getAge != null && !getAge.isEmpty()) {
                paramsadd.addFormDataPart("Age", getAge);
            }
            if (getExp != null && !getExp.isEmpty()) {
                paramsadd.addFormDataPart("experience", getExp);
            }
            GlobalData.SEKeyword = searchvalue.getText().toString().trim();
            if (GlobalData.SEKeyword != null && !GlobalData.SEKeyword.isEmpty()) {
                paramsadd.addFormDataPart("keyword", GlobalData.SEKeyword);
            }
            if (getQualificationID != null && !getQualificationID.isEmpty()) {
                paramsadd.addFormDataPart("Qualification", getQualificationID);
            }
            if (getRole != null && !getRole.isEmpty()) {
                paramsadd.addFormDataPart("role", getRole);
            }
            if (getIndustry != null && !getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", getIndustry);
            }
            if (getLocation != null && !getLocation.isEmpty()) {
                paramsadd.addFormDataPart("location", getLocation);
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            if (GlobalData.isspkeywordsavail.equalsIgnoreCase("No")) {
                paramsadd.addFormDataPart("keywordflag", "No");
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "profile_search.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.employerfilterresponse = response.body().string();
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
            if (GlobalData.employerfilterresponse != null
                    && !GlobalData.employerfilterresponse.contains("connectionFailure")) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(EmployeeListing.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("SF", GlobalData.employerfilterresponse);
                editor.apply();
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.employerfilterresponse);
                    if (GlobalData.isspkeywordsavail.equalsIgnoreCase("No")) {
                        //keywordlist
                        GlobalData.profilesuggestionList = new ArrayList<>();
                        GlobalData.profilemainsuggestionList = new ArrayList<>();
                        JSONArray keywordgroups = responseObj.getJSONArray("keywords");
                        for (int i = 0; i < keywordgroups.length(); i++) {
                            JSONObject c = keywordgroups.getJSONObject(i);
                            String occupations_list_name = c.getString("keywords");
                            GlobalData.profilesuggestionList.add(occupations_list_name);
                        }
                        GlobalData.profilemainsuggestionList.addAll(GlobalData.profilesuggestionList);
                        if (GlobalData.profilemainsuggestionList.size() > 0) {
                            GlobalData.isspkeywordsavail = "Yes";
                        }
                    }
                    getFilterStatus = responseObj.getString("message");
                    getcount = responseObj.getInt("count");
                    if (getLocation != null && !getLocation.isEmpty()) {
                        filter.setImageResource(R.drawable.filter_tick);
                        getLocation = responseObj.getString("citi_name");
                        SharedPreferences jobfilterPreferences = getSharedPreferences(M_SP,
                                Context.MODE_PRIVATE);
                        jobfilterPreferences.edit().putString("F_P_L", getLocation)
                                .apply();
                    } else {
                        filter.setImageResource(R.drawable.filter_icon);
                    }
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        GlobalData.employeeList = new ArrayList<>();
                        Gson gson = new Gson();
                        GlobalData.employeeList = gson.fromJson(responseObj.getString
                                        ("profile_view"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                    } else {
                        if (GlobalData.employeeList.size() > 0) {
                            GlobalData.employeeList.clear();
                        }
                    }
                    setEmployeeListing();
                } catch (Exception e) {
                    Toast.makeText(EmployeeListing.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(EmployeeListing.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private class My_EmployeesScroll extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(EmployeeListing.this, R.style.MyTheme);
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
            if (getGender != null && !getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", getGender);
            }
            if (getAge != null && !getAge.isEmpty()) {
                paramsadd.addFormDataPart("Age", getAge);
            }
            if (getExp != null && !getExp.isEmpty()) {
                paramsadd.addFormDataPart("experience", getExp);
            }
            if (getQualificationID != null && !getQualificationID.isEmpty()) {
                paramsadd.addFormDataPart("Qualification", getQualificationID);
            }
            if (getRole != null && !getRole.isEmpty()) {
                paramsadd.addFormDataPart("role", getRole);
            }
            if (getIndustry != null && !getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", getIndustry);
            }
            if (getLocation != null && !getLocation.isEmpty()) {
                paramsadd.addFormDataPart("location", getLocation);
            }
            if (getLanguages != null && !getLanguages.isEmpty()) {
                paramsadd.addFormDataPart("languages", getLanguages);
            }
            paramsadd.addFormDataPart("scroll", "scroll");
            paramsadd.addFormDataPart("jobseeker_id", args[0]);
            if (GlobalData.isspkeywordsavail.equalsIgnoreCase("No")) {
                paramsadd.addFormDataPart("keywordflag", "No");
            }
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "profile_search.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.employerfilterresponse = response.body().string();
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
            if (GlobalData.employerfilterresponse != null
                    && !GlobalData.employerfilterresponse.contains("connectionFailure")) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(EmployeeListing.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("SF", GlobalData.employerfilterresponse);
                editor.apply();
                try {
                    JSONObject responseObj = new JSONObject(GlobalData.employerfilterresponse);
                    if (GlobalData.isspkeywordsavail.equalsIgnoreCase("No")) {
                        //keywordlist
                        GlobalData.profilesuggestionList = new ArrayList<>();
                        GlobalData.profilemainsuggestionList = new ArrayList<>();
                        JSONArray keywordgroups = responseObj.getJSONArray("keywords");
                        for (int i = 0; i < keywordgroups.length(); i++) {
                            JSONObject c = keywordgroups.getJSONObject(i);
                            String occupations_list_name = c.getString("keywords");
                            GlobalData.profilesuggestionList.add(occupations_list_name);
                        }
                        GlobalData.profilemainsuggestionList.addAll(GlobalData.profilesuggestionList);
                        if (GlobalData.profilemainsuggestionList.size() > 0) {
                            GlobalData.isspkeywordsavail = "Yes";
                        }
                    }
                    getFilterStatus = responseObj.getString("message");
                    getcount = responseObj.getInt("count");
                    if (getLocation != null && !getLocation.isEmpty()) {
                        filter.setImageResource(R.drawable.filter_tick);
                    } else {
                        filter.setImageResource(R.drawable.filter_icon);
                    }
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        ArrayList<User> tempemployeelist = gson.fromJson(responseObj.getString
                                        ("profile_view"),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        GlobalData.employeeList.addAll(tempemployeelist);
                    }
                    setEmployeeListing();
                } catch (Exception e) {
                    Toast.makeText(EmployeeListing.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(EmployeeListing.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }

        }
    }

    private void setEmployeeListing() {
        if (!isCheckedKeyword) {
            getEditKeyword();
            isCheckedKeyword = true;
        }
        try {
            JSONObject responseObj = new JSONObject(GlobalData.employerfilterresponse);
            getFilterStatus = responseObj.getString("message");
            getcount = responseObj.getInt(("count"));
            if (getFilterStatus.equalsIgnoreCase("success")) {
                setEmployee();
            } else {
                if (GlobalData.employeeList.size() > 0) {
                    setEmployee();
                } else {
                    GlobalData.employeeList.clear();
                    employeelist.setVisibility(View.GONE);
                    employeelist_empty.setVisibility(View.VISIBLE);
                    Toast.makeText(EmployeeListing.this, getString(R.string.emplistemptymsg), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(EmployeeListing.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setEmployee() {
        myemployeesAdapter = new EmployeeList_Adpater(EmployeeListing.this, GlobalData
                .employeeList);
        int currentPosition = employeelist.getFirstVisiblePosition();
        employeelist.setAdapter(myemployeesAdapter);
        employeelist.setSelection(profileindex);
        employeelist.setSelectionFromTop(currentPosition, 0);
        employeelist.setVisibility(View.VISIBLE);
        employeelist_empty.setVisibility(View.GONE);
        /*if (GlobalData.SEKeyword != null && !GlobalData.SEKeyword.isEmpty() && !GlobalData.SEKeyword.equalsIgnoreCase("")) {
            if (!GlobalData.SEKeyword.equalsIgnoreCase(" ")) {
                searchvalue.setText(GlobalData.SEKeyword);
                clear.setVisibility(View.VISIBLE);
            } else {
                searchvalue.setText("");
                clear.setVisibility(View.GONE);
            }
        }*/
    }

    @Override
    public void onLocationChanged(Location arg0) {
        mLastLocation = arg0;
        displayLocation();
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (googleApiClient.isConnected()) {
            displayLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
