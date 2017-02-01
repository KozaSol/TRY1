package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;

import static com.jobsearchtry.utils.GlobalData.SENDER_ID;
import static com.jobsearchtry.utils.GlobalData.getyesnoflag;
import static com.jobsearchtry.utils.GlobalData.login_status;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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
import com.jobsearchtry.adapter.AutoCompleteAdapter;
import com.jobsearchtry.adapter.CategoryList_Adpater;
import com.jobsearchtry.adapter.CityAdapter;
import com.jobsearchtry.adapter.JobList_Adpater;
import com.jobsearchtry.services.NotifiCountService;
import com.jobsearchtry.utils.DrawableClickListener;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.JS_CATEGORY;
import com.jobsearchtry.wrapper.Jobs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.jobsearchtry.receiver.NetworkUpdateReceiver;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.net.ConnectivityManager;
import android.content.IntentFilter;

public class Homepage extends Activity
        implements ConnectionCallbacks, com.google.android.gms.location.LocationListener,
        OnConnectionFailedListener {
    private TextView currentlocation, welcomeuser, homepage_emptymessage, cl_cityheader, changelangtamil,
            stateselectedloc, cityselectedloc, logout, homepagec_emptymessage, notify_count;
    private ImageView filter, logout_image, clear, back, search, usericon;
    private JobList_Adpater myjobsAdapter;
    private AutoCompleteTextView searchjobvalue;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private ArrayList<String> selectedLocationList = new ArrayList<>();
    private ArrayList<String> selectedLocationLocalList = null;
    private ArrayAdapter<City> cadapter, loccityAdapter;
    private int indexcity = -1;
    private ProgressDialog pg;
    private static int jobindex = 0, categoryindex = 0, getcount = 0,getrolecounts =0;
    private GridView jscategory;
    private CategoryList_Adpater mycategoryAdapter;
    private String getFilterStatus, getState, getStateID, getFrom = "State", regId, languages, getFilterStatusTamil;
    private static final String M_Filter_Job = "filter_job";
    public static Callback callback, callback1, callback2, callback3, callbacknotify;
    private boolean[] isCheckedLocation;
    private OkHttpClient client = null;
    private LocationRequest mLocationRequest;
    private Dialog alertDialog;
    private LinearLayout locstate_view, city_view, joblistview, categoryview,
            logout_lay, jssearchlistview, notify_count_lay, languages_lay,
            myprofile, notification, apply_history, changepassword_lay, acc_his;
    private ListView filterstate, filtercity, jobslist, jssearchlist;
    private AutoCompleteTextView locfilt_citysearch, autocity;
    private DrawerLayout landingpage;
    private AutoCompleteAdapter locAdapter;
    private boolean isCheckedKeyword = false;
    private NetworkUpdateReceiver receiver;
    private FrameLayout postjob_lay;

    @Override
    public void onBackPressed() {
        /*--when drawer layout(menu)  opens will close the menu--*/
        if (landingpage.isDrawerOpen(GravityCompat.START)) {
            landingpage.closeDrawer(GravityCompat.START);
        }
        /*--if the suggestion list is opens hide the suggestion view--*/
        else if (jssearchlistview.getVisibility() == View.VISIBLE) {
            jssearchlistview.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
            searchjobvalue.setCursorVisible(false);
            if (GlobalData.jobList.size() != 0) {
                myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
                jobslist.setAdapter(myjobsAdapter);
            }
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
            categoryview.setVisibility(View.VISIBLE);
            isVerifyCategoryAvailability();
            joblistview.setVisibility(View.GONE);
        }
        /*--we are in job listing page, goes to category(role)listing page--*/
        else if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
            //getRoleFrolJoblist();
            GlobalData.joblistfrom = "RL";
            // if(!GlobalData.getCRole.isEmpty() && GlobalData.getCRole != null) {
            GlobalData.getCRole = null;
            //  GlobalData.joblistfrom = "RL";
            //   new getRole().execute();
            //}else {
            setCategoryList();
            //  }
        }
        /*--we are in category(role)listing page,asking the popup to exit|continue the application*/
        else {
            final Dialog alertDialog = new Dialog(Homepage.this, R.style.MyThemeDialog);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCanceledOnTouchOutside(false);
            View emppromptView = View.inflate(Homepage.this,
                    R.layout.delete_popup, null);
            TextView f_popupheader = (TextView) emppromptView
                    .findViewById(R.id.d_popupheader);
            f_popupheader.setText(R.string.empexit);
            TextView f_popupsubheader = (TextView) emppromptView
                    .findViewById(R.id.d_popup_subheader);
            f_popupsubheader.setText(R.string.exitalert);
            Button no = (Button) emppromptView.findViewById(R.id.d_no);
            Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
            alertDialog.setContentView(emppromptView);
            alertDialog.show();
            yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
            });
            no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                }
            });
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        landingpage = (DrawerLayout) findViewById(R.id.landingpage);
        notify_count_lay = (LinearLayout) findViewById(R.id.notify_count_lay);
        //begore login the application, goes to employer login for posting a new job
        postjob_lay = (FrameLayout) findViewById(R.id.bl_postjob_lay);
        if (!login_status.equalsIgnoreCase("No user found")) {
            postjob_lay.setVisibility(View.GONE);
        } else {
            postjob_lay.setVisibility(View.VISIBLE);
        }
        postjob_lay.setOnClickListener(new OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               GlobalData.loginfrom = "Employer";
                                               GlobalData.fromlogin = "PostJob";
                                               SharedPreferences sharedPreferences = PreferenceManager
                                                       .getDefaultSharedPreferences(Homepage.this);
                                               Editor editor = sharedPreferences.edit();
                                               editor.putString("LOGINFROM", GlobalData.loginfrom);
                                               editor.apply();
                                               startActivity(new Intent(Homepage.this, Login.class));
                                           }
                                       }
        );
        //after logged in the application for job seeker,goes to notification listing page.
        notify_count_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homepage.this, JS_Notification.class));
            }
        });
        notify_count = (TextView) findViewById(R.id.notify_count);
        usericon = (ImageView) findViewById(R.id.js_r_h_usericon);
        usericon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (landingpage.isDrawerOpen(GravityCompat.START)) {
                    landingpage.closeDrawer(GravityCompat.START);
                } else {
                    landingpage.openDrawer(GravityCompat.START);
                }
            }
        });
        //clicking more, we can go to view my profile,jobs posted,my fav,change password and logout
        ImageButton empdash_moretab = (ImageButton) findViewById(R.id.js_r_h_menu);
        empdash_moretab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (landingpage.isDrawerOpen(GravityCompat.START)) {
                    landingpage.closeDrawer(GravityCompat.START);
                } else {
                    landingpage.openDrawer(GravityCompat.START);
                }
            }
        });
        joblistview = (LinearLayout) findViewById(R.id.joblistview);
        categoryview = (LinearLayout) findViewById(R.id.categoryview);
        homepagec_emptymessage = (TextView) findViewById(R.id.homepagec_emptymessage);
        jssearchlistview = (LinearLayout) findViewById(R.id.jssearchlistview);
        jssearchlist = (ListView) findViewById(R.id.jssearchlist);
        logout_image = (ImageView) findViewById(R.id.logout_image);
        changelangtamil = (TextView) findViewById(R.id.changelangtamil);
        if (languages.equalsIgnoreCase("English")) {
            changelangtamil.setVisibility(View.GONE);
        } else {
            changelangtamil.setVisibility(View.VISIBLE);
        }
        //jobseeker languages page
        final LinearLayout languages = (LinearLayout) findViewById(R.id.chooselanguages_lay);
        languages.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseLanguages().selectLanguagesPopup(Homepage.this);
            }
        });
        //jobseeker my profile page
        myprofile = (LinearLayout) findViewById(R.id.myprofile_lay);
        myprofile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GlobalData.pagefrom = "MyProfile";
                startActivity(new Intent(Homepage.this,
                        MyProfileActivity.class));
            }
        });
        //jobseeker notification
        notification = (LinearLayout) findViewById(R.id.my_notifi_lay);
        notification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homepage.this, JS_Notification.class));
            }
        });
        //jobseeker favourties
        acc_his = (LinearLayout) findViewById(R.id.my_fav_lay);
        acc_his.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this, MyFavorites.class));
            }
        });
        //jobseeker change password
        changepassword_lay = (LinearLayout) findViewById(R.id.changepassword_lay);
        changepassword_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        ChangePassword.class));
            }
        });
        //jobseeker applied history
        apply_history = (LinearLayout) findViewById(R.id.apply_history_lay);
        apply_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        AppliedHistory.class));
            }
        });
        //jobseeker terms and conditions
        LinearLayout termscondi = (LinearLayout) findViewById(R.id.termscondi_lay);
        termscondi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        TermsCondition.class));
            }
        });
        //jobseeker howtouse
        LinearLayout howtouse = (LinearLayout) findViewById(R.id.howtouse_lay);
        howtouse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        Howtouse.class));
            }
        });
        // jobseeker aboutus
        LinearLayout aboutus = (LinearLayout) findViewById(R.id.aboutus_lay);
        aboutus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        About_us.class));
            }
        });
        // jobseeker contactus
        LinearLayout contact = (LinearLayout) findViewById(R.id.contact_lay);
        contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        Contactus.class));
            }
        });
        // jobseeker feedback
        LinearLayout feedback = (LinearLayout) findViewById(R.id.feedback_lay);
        feedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Homepage.this,
                        Feedback.class));
            }
        });
        logout_lay = (LinearLayout) findViewById(R.id.logout_lay);
        logout = (TextView) findViewById(R.id.logout);
        ImageView locationimagehome = (ImageView) findViewById(R.id.locationimagehome);
        categoryview.setVisibility(View.VISIBLE);
        jscategory = (GridView) findViewById(R.id.jscategory);
        categoryindex = jscategory.getFirstVisiblePosition();
        welcomeuser = (TextView) findViewById(R.id.welcomeuser);
        homepage_emptymessage = (TextView) findViewById(R.id.homepage_emptymessage);
        jscategory.setVisibility(View.VISIBLE);
        jscategory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (GlobalData.select_role.size() > 0) {
                    int threshold = 1;
                    int count = GlobalData.select_role.size();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (jscategory.getLastVisiblePosition() >= count
                                - threshold) {
                            if (getrolecounts == 1) {
                                String rolenamescroll = GlobalData.select_role.get(count - 1).getRole_name();
                                if (new UtilService().isNetworkAvailable(Homepage.this)) {
                                    new MyRoleCategoryScrollJobs().execute(rolenamescroll);
                                } else {
                                    Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
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
        homepagec_emptymessage.setVisibility(View.GONE);
        homepage_emptymessage.setVisibility(View.GONE);
        currentlocation = (TextView) findViewById(R.id.homepage_currentlocation);
        //getSharedData
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (Homepage.this));
        GlobalData.getLocation = sharedPreferences.getString("F_L", GlobalData.getLocation);
        GlobalData.getFinalLocation = sharedPreferences.getString("F_FL", GlobalData.getFinalLocation);
        GlobalData.getPrevLocation = sharedPreferences.getString("F_PL", GlobalData
                .getPrevLocation);
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        GlobalData.username = sharedPreferences.getString("NAME", GlobalData.username);
        GlobalData.getHomeState = sharedPreferences.getString("F_HS", GlobalData.getHomeState);
        GlobalData.Landingkeyword = sharedPreferences.getString("LSKEY", GlobalData
                .Landingkeyword);
        GlobalData.getSearchKeyword = sharedPreferences.getString("JobKey", GlobalData.getSearchKeyword);
        GlobalData.getIndustry = sharedPreferences.getString("F_I", GlobalData.getIndustry);
        GlobalData.getRole = sharedPreferences.getString("F_R", GlobalData.getRole);
        GlobalData.getSSalary = sharedPreferences.getString("F_S", GlobalData.getSSalary);
        GlobalData.getJobType = sharedPreferences.getString("F_JT", GlobalData.getJobType);
        GlobalData.getJobIDType = sharedPreferences.getString("F_JTID", GlobalData.getJobIDType);
        GlobalData.getExperience = sharedPreferences.getString("F_E", GlobalData.getExperience);
        //GlobalData.getSkill = sharedPreferences.getString("F_SK", GlobalData.getSkill);
        GlobalData.getGender = sharedPreferences.getString("F_G", GlobalData.getGender);
        GlobalData.getHomeState = sharedPreferences.getString("F_HS", GlobalData.getHomeState);
        GlobalData.getViewedCategory = sharedPreferences.getString("VCATE", GlobalData
                .getViewedCategory);
        GlobalData.getViewCateCount = sharedPreferences.getString("VCATEC", GlobalData
                .getViewCateCount);
        GlobalData.getViewedJob = sharedPreferences.getString("VJID", GlobalData.getViewedJob);
        GlobalData.getSearchKeywords = sharedPreferences.getString("SKS", GlobalData.getSearchKeywords);
        GlobalData.getdefaultLocationHome = sharedPreferences.getBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        GlobalData.getNearLocation = sharedPreferences.getString("F_NL", GlobalData.getNearLocation);
        GlobalData.getroleresponse = sharedPreferences.getString("CATEGORY", GlobalData.getroleresponse);
        GlobalData.getyesnoflag = sharedPreferences.getString("YNFLAG", GlobalData.getyesnoflag);

        //initially location is set to previous location
        GlobalData.getPrevLocation = GlobalData.getLocation;
        SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                (Homepage.this);
        Editor editorr = sharedPreferencess.edit();
        editorr.putString("F_PL", GlobalData.getPrevLocation);
        editorr.apply();
        //getting the current location
        if (new UtilService().isNetworkAvailable(Homepage.this)) {
            if (GlobalData.getdefaultLocationHome) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(Homepage.this, android.Manifest
                            .permission.ACCESS_FINE_LOCATION) != PackageManager
                            .PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission
                                .ACCESS_FINE_LOCATION}, 1);
                    } else {
                        locationconnected();
                    }
                } else {
                    locationconnected();
                }
            }
        } else {
            final Dialog alertDialog = new Dialog(Homepage.this, R.style.MyThemeDialog);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCanceledOnTouchOutside(false);
            View emppromptView = View.inflate(Homepage.this,
                    R.layout.delete_popup, null);
            TextView f_popupheader = (TextView) emppromptView
                    .findViewById(R.id.d_popupheader);
            f_popupheader.setText(R.string.confirmation);
            TextView f_popupsubheader = (TextView) emppromptView
                    .findViewById(R.id.d_popup_subheader);
            f_popupsubheader.setText("Please enable the wifi/mobile data connection");
            Button no = (Button) emppromptView.findViewById(R.id.d_no);
            Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
            alertDialog.setContentView(emppromptView);
            alertDialog.setCancelable(false);
            alertDialog.show();
            yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (new UtilService().isNetworkAvailable(Homepage.this)) {
                        GlobalData.LandRefresh = "Home";
                        GlobalData.islocationAvail = "No";
                        GlobalData.getjsfilterdata = null;
                        if (GlobalData.getdefaultLocationHome) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ActivityCompat.checkSelfPermission(Homepage.this, android.Manifest
                                        .permission.ACCESS_FINE_LOCATION) != PackageManager
                                        .PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{android.Manifest.permission
                                            .ACCESS_FINE_LOCATION}, 1);
                                } else {
                                    locationconnected();
                                }
                            } else {
                                locationconnected();
                            }
                        }
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(Homepage.this, "You can go and enable the wifi/mobile internet connection,Try to click the Yes button", Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
            no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                    alertDialog.dismiss();
                }
            });
        }
        //location icon onclick - location available opens the alert dialog else get the list from sqlite database
        locationimagehome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVerifyLocationListAvailable();
            }
        });
        //location text onclick
        currentlocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVerifyLocationListAvailable();
            }
        });
        //filter onlick - goes to filter page
        filter = (ImageView) findViewById(R.id.js_home_filter);
        filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, JobSearch_Filter.class));
            }
        });
        //filter image visible/hide functionality
        showfilterImage();
        //display the current location with the selected count
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            getCityCount();
        } else {
            currentlocation.setText(R.string.allindia);
        }
        //getregistration id and login/logout
        getMenu();
        //job seeker menu show/hide the fields
        showhideMenu();
        //call back from notification service
        callbacknotify = new Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (!GlobalData.login_status.equals("No user found") && !GlobalData.getCount.equalsIgnoreCase("0")) {
                    notify_count.setText(GlobalData.getCount);
                    notify_count_lay.setVisibility(View.VISIBLE);
                } else {
                    notify_count_lay.setVisibility(View.GONE);
                }
                return false;
            }
        };
        callback2 = new Callback() {
            @Override
            public boolean handleMessage(Message message) {
                searchjobvalue.setCursorVisible(false);
                jssearchlistview.setVisibility(View.GONE);
                return false;
            }
        };
        //from joblist adapter - filter
        callback3 = new Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (GlobalData.roleJobList.size() != 0) {
                    GlobalData.jobList = new ArrayList<>();
                    GlobalData.jobList = GlobalData.roleJobList;
                    myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
                    jobslist.setAdapter(myjobsAdapter);
                }
                jssearchlistview.setVisibility(View.GONE);
                categoryview.setVisibility(View.GONE);
                joblistview.setVisibility(View.VISIBLE);
                Bundle b = message.getData();
                Integer value = b.getInt("KEY");
                if (value != 0) {
                    jobslist.setVisibility(View.VISIBLE);
                    homepage_emptymessage.setVisibility(View.GONE);
                } else {
                    homepage_emptymessage.setVisibility(View.VISIBLE);
                    jobslist.setVisibility(View.GONE);
                }
                if (joblistview.getVisibility() == View.VISIBLE) {
                    GlobalData.joblistfrom = "JL";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Homepage.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("JobListFrom", GlobalData.joblistfrom);
                    editor.apply();
                }
                return false;
            }
        };
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                jssearchlistview.setVisibility(View.GONE);
                categoryview.setVisibility(View.VISIBLE);
                isVerifyCategoryAvailability();
                joblistview.setVisibility(View.GONE);
                GlobalData.joblistfrom = "RL";
                SharedPreferences sharedlPreferences = getSharedPreferences
                        (M_Filter_Job,
                                MODE_PRIVATE);
                GlobalData.getroleresponse = sharedlPreferences.getString("CATEGORY", GlobalData
                        .getroleresponse);
                GlobalData.getCRole = null;
                GlobalData.Landingkeyword = " ";
                searchjobvalue.setText("");
                SharedPreferences jobfilterPreferences = getSharedPreferences
                        (M_Filter_Job,
                                MODE_PRIVATE);
                jobfilterPreferences.edit().putString("JobListFrom", GlobalData.joblistfrom)
                        .putString("LSKEY", GlobalData.Landingkeyword).apply();
                if (GlobalData.getrolepage.equalsIgnoreCase("DR")) {
                    getroleCategory();
                } else {
                    GlobalData.pagefrom = "Home";
                    landingpageRefresh();
                }
                return false;
            }
        };
        //logo
        ImageView homepage_h = (ImageView) findViewById(R.id.js_r_h);
        homepage_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getRoleFrolJoblist();
            }
        });
        searchjobvalue = (AutoCompleteTextView) findViewById(R.id.searchjobvalue);
        clear = (ImageView) findViewById(R.id.searchjobvalue_clear);
        back = (ImageView) findViewById(R.id.searchjobvalue_back);
        search = (ImageView) findViewById(R.id.searchjobvalue_search);
        searchjobvalue.setCursorVisible(false);
        back.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        //search layout - back btn functionality
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                jssearchlistview.setVisibility(View.GONE);
                GlobalData.getSearchKeyword = null;
                getSearchKeyword();
                GlobalData.Landingkeyword = "";
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("LSKEY", GlobalData.Landingkeyword);
                editor.apply();
                searchjobvalue.setText(GlobalData.Landingkeyword);
                clear.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                searchjobvalue.setCursorVisible(false);
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
                setCategoryList();
            }
        });
        //search layout - clear btn funct
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.getSearchKeyword = null;
                getSearchKeyword();
                GlobalData.Landingkeyword = "";
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("LSKEY", GlobalData.Landingkeyword);
                editor.apply();
                searchjobvalue.setText(GlobalData.Landingkeyword);
                clear.setVisibility(View.GONE);
                filter.setVisibility(View.VISIBLE);
            }
        });
        jobslist = (ListView) findViewById(R.id.joblist);
        jobslist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (GlobalData.jobList.size() > 0) {
                    int threshold = 1;
                    int count = jobslist.getCount();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (jobslist.getLastVisiblePosition() >= count
                                - threshold) {
                            if (getcount == 1) {
                                String job_id = GlobalData.jobList.get(count - 1).getJob_id();
                                if (new UtilService().isNetworkAvailable(Homepage.this)) {
                                    new getRoleJoblistScroll().execute(job_id);
                                } else {
                                    Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
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
    }

    private void showhideMenu() {
        if (GlobalData.login_status.equals("No user found")) {
            welcomeuser.setText(R.string.welcomeguest);
            postjob_lay.setVisibility(View.VISIBLE);
            myprofile.setVisibility(View.GONE);
            notification.setVisibility(View.GONE);
            apply_history.setVisibility(View.GONE);
            changepassword_lay.setVisibility(View.GONE);
            acc_his.setVisibility(View.GONE);
        } else {
            acc_his.setVisibility(View.VISIBLE);
            myprofile.setVisibility(View.VISIBLE);
            notification.setVisibility(View.VISIBLE);
            apply_history.setVisibility(View.VISIBLE);
            changepassword_lay.setVisibility(View.VISIBLE);
            postjob_lay.setVisibility(View.GONE);
            welcomeuser.setText(GlobalData.username);
            startService(new Intent(Homepage.this, NotifiCountService.class));
            //display the notification count
            if (!GlobalData.login_status.equals("No user found") && !GlobalData.getCount.equalsIgnoreCase("0")) {
                notify_count.setText(GlobalData.getCount);
                notify_count_lay.setVisibility(View.VISIBLE);
            } else {
                notify_count_lay.setVisibility(View.GONE);
            }
        }
    }

    private void locationconnected() {
        googleApiClient = new GoogleApiClient.Builder(Homepage.this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER).setInterval(10 * 1000)
                .setFastestInterval(1000);
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
                        if (googleApiClient.isConnected()) {
                            startLocationUpdates();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(Homepage.this, 1000);
                        } catch (IntentSender.SendIntentException ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    //if category(role) is available
    private void isVerifyCategoryAvailability() {
        if (GlobalData.select_role.size() > 0) {
            jscategory.setVisibility(View.VISIBLE);
            homepagec_emptymessage.setVisibility(View.GONE);
        } else {
            jscategory.setVisibility(View.GONE);
            homepagec_emptymessage.setVisibility(View.VISIBLE);
        }
    }

    private void backfromfilter() {
        jssearchlistview.setVisibility(View.GONE);
        if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                ("connectionFailure")) {
            try {
                JSONObject responseObj = new JSONObject(GlobalData.getroleresponse);
                getFilterStatus = responseObj.getString("message");
                if (!languages.equalsIgnoreCase("English")) {
                    getFilterStatusTamil = responseObj.getString("message_tamil");
                }
                String fromrolelist = responseObj.getString("frompage");
                String new_word = getFilterStatus.substring(getFilterStatus.length() - 6);
                String comparestring = "again.";
                if (new_word.equalsIgnoreCase(comparestring)) {
                    getFilterStatus = "failure";
                } else {
                    getFilterStatus = "success";
                }
                Gson gson = new Gson();
                if (GlobalData.joblistfrom.equalsIgnoreCase("RL")) {
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getLocation = responseObj.getString("citi_name");
                        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                            GlobalData.getNearLocation = responseObj.getString("cities_name");
                            GlobalData.getFinalLocation = responseObj.getString("cities_name");
                        }
                    }

                    if (GlobalData.islocationAvail.equalsIgnoreCase("No") && fromrolelist.equalsIgnoreCase("RL")) {
                        GlobalData.LocationList = new ArrayList<>();
                        GlobalData.LocationList = gson.fromJson(responseObj.getString
                                        ("locations"),
                                new TypeToken<ArrayList<FilterLocation>>() {
                                }.getType());
                        //citylist
                        GlobalData.locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                                TypeToken<ArrayList<City>>() {
                                }.getType());
                        GlobalData.MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                                TypeToken<ArrayList<City>>() {
                                }.getType());
                        if (GlobalData.LocationList.size() > 0) {
                            GlobalData.islocationAvail = "Yes";
                        }
                    }
                    if (fromrolelist.equalsIgnoreCase("RL")) {
                        //keywordlist
                        GlobalData.suggestionsList = new ArrayList<>();
                        GlobalData.MainSuggestionList = new ArrayList<>();
                        JSONArray keywordgroups = responseObj.getJSONArray("keywords");
                        for (int i = 0; i < keywordgroups.length(); i++) {
                            JSONObject c = keywordgroups.getJSONObject(i);
                            String occupations_list_name = c.getString("keyword");
                            GlobalData.suggestionsList.add(occupations_list_name);
                        }
                        GlobalData.MainSuggestionList.addAll(GlobalData.suggestionsList);
                    }
                }

                if (getFilterStatus.equalsIgnoreCase("success")) {
                    jssearchlistview.setVisibility(View.GONE);
                    if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
                        new getRoleJoblist().execute();
                        /*getcount = responseObj.getInt("count");
                        categoryview.setVisibility(View.GONE);
                        joblistview.setVisibility(View.VISIBLE);
                        if (GlobalData.jobList.size() == 0) {
                            GlobalData.jobList = new ArrayList<>();
                            GlobalData.jobList = gson.fromJson(responseObj.getString("all_search_view"),
                                    new TypeToken<ArrayList<Jobs>>() {
                                    }.getType());
                        }
                        myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
                        jobslist.setAdapter(myjobsAdapter);
                        if (GlobalData.jobList.size() != 0) {
                            getJobListResume();
                        }*/
                    } else {
                        categoryview.setVisibility(View.VISIBLE);
                        joblistview.setVisibility(View.GONE);
                        GlobalData.getCRole = null;
                        searchjobvalue.setText("");
                        getCategoryList();
                        if (!isCheckedKeyword) {
                            isCheckedKeyword = true;
                            getEditKeyword();
                        }
                    }
                } else {
                    resetNearFinalLocation();
                }
            } catch (Exception ignored) {
            }
        } else {
            resetNearFinalLocation();
        }
    }

    private void landingpageRefresh() {
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            if (((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) && GlobalData.getRole == null
                    && GlobalData.getSSalary == null
                    && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                    && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                    && GlobalData.getExperience == null
                    //&& (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                    && GlobalData.getGender == null) && (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getPrevLocation)) &&
                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getFinalLocation))) {
                getRolelist();
            } else {
                GlobalData.getFinalLocation = GlobalData.getLocation;
                final SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                Editor editor1 = sharedPreferences1.edit();
                editor1.putString("F_FL", GlobalData.getFinalLocation);
                editor1.apply();
                //if (GlobalData.getResultID.equalsIgnoreCase("No")) {
                //   new getFilterNearByLocData().execute(GlobalData.getResultID);
                // } else {
                getRolelist();
                // }
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void categoryonclick() {
        categoryindex = jscategory.getFirstVisiblePosition();
        jscategory.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (jssearchlistview.getVisibility() == View.VISIBLE) {
                    jssearchlistview.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    back.setVisibility(View.GONE);
                    searchjobvalue.setText("");
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    searchjobvalue.setCursorVisible(false);
                }
                GlobalData.joblistfrom = "JL";
                JS_CATEGORY item = (JS_CATEGORY) parent.getItemAtPosition(position);
                GlobalData.getCRole = item.getRole_name();
                if (!item.getRole_name().equalsIgnoreCase("All")) {
                    GlobalData.getCRole = item.getRole_name();
                    SharedPreferences sharedlPreferences = getSharedPreferences
                            (M_Filter_Job,
                                    MODE_PRIVATE);
                    GlobalData.getViewedCategory = sharedlPreferences.getString("VCATE",
                            GlobalData.getViewedCategory);
                    GlobalData.getViewCateCount = sharedlPreferences.getString("VCATEC",
                            GlobalData.getViewCateCount);
                    if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory
                            .isEmpty()) {
                        String[] viewcateadd = GlobalData.getViewedCategory.split(",");
                        String[] viewcateaddcount = GlobalData.getViewCateCount.split(",");
                        for (int i = 0; i < viewcateadd.length; i++) {
                            if (!GlobalData.getrolename.contains(viewcateadd[i])) {
                                GlobalData.getrolename.add(viewcateadd[i]);
                                GlobalData.getrolecount.add(Integer.parseInt(viewcateaddcount[i]));
                            }
                        }
                    }
                    if (GlobalData.getrolename.size() > 0) {
                        for (int i = 0; i < GlobalData.getrolename.size(); i++) {
                            if (GlobalData.getrolename.get(i).equalsIgnoreCase(GlobalData
                                    .getCRole)) {
                                int inccount = GlobalData.getrolecount.get(i) + 1;
                                GlobalData.getrolecount.set(i, inccount);
                            } else {
                                if (!(GlobalData.getrolename.contains(GlobalData.getCRole))) {
                                    GlobalData.getrolename.add(GlobalData.getCRole);
                                    GlobalData.getrolecount.add(1);
                                }
                            }
                        }
                    } else {
                        GlobalData.getrolename.add(GlobalData.getCRole);
                        GlobalData.getrolecount.add(1);
                    }
                } else {
                    if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                        GlobalData.getCRole = GlobalData.getRole;
                    } else {
                        GlobalData.getCRole = null;
                    }
                }
                int[] ret = new int[GlobalData.getrolecount.size()];
                String[] stringArray = new String[GlobalData.getrolecount.size()];
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = GlobalData.getrolecount.get(i);
                    stringArray[i] = GlobalData.getrolename.get(i);
                }
                int tmp0;
                String tmp1;
                for (int i = 0; i < ret.length; i++) {
                    for (int j = i + 1; j < ret.length; j++) {
                        if (ret[j] > ret[i]) {
                            // swap in int-Array
                            tmp0 = ret[i];
                            ret[i] = ret[j];
                            ret[j] = tmp0;
                            // swap in string-Array
                            tmp1 = stringArray[i];
                            stringArray[i] = stringArray[j];
                            stringArray[j] = tmp1;
                        } else if (ret[j] == ret[i]) {
                            // sorts alphabetically
                            if (stringArray[j].compareTo(stringArray[i]) < 0) {
                                tmp1 = stringArray[i];
                                stringArray[i] = stringArray[j];
                                stringArray[j] = tmp1;
                            }
                        }
                    }
                }
//output
                String[] getmostviewedrole = new String[ret.length];
                int[] getmostviewedrolecount = new int[ret.length];
                for (int k = 0; k < ret.length; k++) {
                    getmostviewedrole[k] = stringArray[k];
                    getmostviewedrolecount[k] = ret[k];
                }
                if (getmostviewedrole.length > 0) {
                    String mostviewedrole = Arrays.toString(getmostviewedrole);
                    mostviewedrole = mostviewedrole.substring(1, mostviewedrole.length() - 1);
                    mostviewedrole = mostviewedrole.replace(", ", ",");
                    GlobalData.getViewedCategory = mostviewedrole;
                    String strNumbers = Arrays.toString(getmostviewedrolecount);
                    strNumbers = strNumbers.substring(1, strNumbers.length() - 1);
                    GlobalData.getViewCateCount = strNumbers.replace(", ", ",");
                }
                // GlobalData.pagefrom = "Home";
                GlobalData.getrolepage = "DR";
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("F_L", GlobalData.getLocation);
                editor.putString("ROLEPAGE", GlobalData.getrolepage);
                editor.putString("JobListFrom", GlobalData.joblistfrom);
                editor.putString("VCATE", GlobalData.getViewedCategory);
                editor.putString("VCATEC", GlobalData.getViewCateCount);
                editor.apply();
                String categorynojob = "C";
                if (new UtilService().isNetworkAvailable(Homepage.this)) {
                    GlobalData.joblistfrom = "JL";
                    new getRoleJoblist().execute();
                } else {
                    Toast.makeText(Homepage.this,
                            getString(R.string.checkconnection), Toast
                                    .LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        jobindex = jobslist.getFirstVisiblePosition();
        categoryindex = jscategory.getFirstVisiblePosition();
        Log.e("cpos",""+categoryindex);
    }

    @Override
    public void onResume() {
        super.onResume();
        showhideMenu();
        getMenu();
        SharedPreferences sharedPreferences = getSharedPreferences(M_Filter_Job,
                MODE_PRIVATE);
        GlobalData.getPrevLocation = sharedPreferences.getString("F_PL", GlobalData
                .getPrevLocation);
        GlobalData.getIndustry = sharedPreferences.getString("F_I", GlobalData.getIndustry);
        GlobalData.getRole = sharedPreferences.getString("F_R", GlobalData.getRole);
        GlobalData.getSSalary = sharedPreferences.getString("F_S", GlobalData.getSSalary);
        GlobalData.getJobType = sharedPreferences.getString("F_JT", GlobalData.getJobType);
        GlobalData.getJobIDType = sharedPreferences.getString("F_JTID", GlobalData.getJobIDType);
        GlobalData.getExperience = sharedPreferences.getString("F_E", GlobalData.getExperience);
        // GlobalData.getSkill = sharedPreferences.getString("F_SK", GlobalData.getSkill);
        GlobalData.getGender = sharedPreferences.getString("F_G", GlobalData.getGender);
        GlobalData.getHomeState = sharedPreferences.getString("F_HS", GlobalData.getHomeState);
        GlobalData.getViewedCategory = sharedPreferences.getString("VCATE", GlobalData
                .getViewedCategory);
        GlobalData.getViewCateCount = sharedPreferences.getString("VCATEC", GlobalData
                .getViewCateCount);
        GlobalData.getViewedJob = sharedPreferences.getString("VJID", GlobalData.getViewedJob);
        GlobalData.getPrevLocation = GlobalData.getLocation;
        GlobalData.getSearchKeyword = sharedPreferences.getString("JobKey", GlobalData.getSearchKeyword);
        GlobalData.getSearchKeywords = sharedPreferences.getString("SKS", GlobalData.Landingkeyword);
        GlobalData.Landingkeyword = sharedPreferences.getString("LSKey", GlobalData.getSearchKeywords);
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(
                (Homepage.this));
        GlobalData.getroleresponse = sharedPreferences1.getString("CATEGORY", GlobalData.getroleresponse);
        GlobalData.getLocation = sharedPreferences1.getString("F_L", GlobalData.getLocation);
        GlobalData.getFinalLocation = sharedPreferences1.getString("F_FL", GlobalData.getFinalLocation);
        GlobalData.getNearLocation = sharedPreferences1.getString("F_NL", GlobalData.getNearLocation);
        GlobalData.getyesnoflag = sharedPreferences.getString("YNFLAG", GlobalData.getyesnoflag);
        GlobalData.getdefaultLocationHome = sharedPreferences1.getBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                (Homepage.this);
        Editor editorr = sharedPreferencess.edit();
        editorr.putString("F_PL", GlobalData.getPrevLocation);
        editorr.apply();
        if (GlobalData.LandRefresh.equalsIgnoreCase("Home")) {
            GlobalData.LandRefresh = "Refresh";
            GlobalData.joblistfrom = "RL";
            editorr.putString("JobListFrom", GlobalData.joblistfrom);
            editorr.apply();
            if ((GlobalData.getLocation != null) || !GlobalData.getdefaultLocationHome) {
                if (new UtilService().isNetworkAvailable(Homepage.this)) {
                    getRolelist();
                } else {
                    Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                }
            }
        } else {
            if (new UtilService().isNetworkAvailable(Homepage.this)) {
                backfromfilter();
            } else {
                Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                        .LENGTH_SHORT).show();
            }
        }
        showfilterImage();
        if (!(GlobalData.getLocation == null || GlobalData.getLocation.isEmpty())) {
            getCityCount();
        } else {
            currentlocation.setText(R.string.allindia);
        }
    }

    private void getJobListResume() {
        if (GlobalData.jobList.size() != 0) {
            Log.e("home-jl", "" + GlobalData.jobList.size());
            homepage_emptymessage.setVisibility(View.GONE);
            jobslist.setVisibility(View.VISIBLE);
        } else {
            alertmessages();
            homepage_emptymessage.setVisibility(View.VISIBLE);
            jobslist.setVisibility(View.GONE);
        }
        myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
        jobslist.setAdapter(myjobsAdapter);
        jobslist.setSelection(jobindex);
        if (GlobalData.Landingkeyword != null && !GlobalData.Landingkeyword.isEmpty
                ()) {
            if (!GlobalData.Landingkeyword.equalsIgnoreCase(" ")) {
                searchjobvalue.setText(GlobalData.Landingkeyword);
            } else {
                searchjobvalue.setText("");
            }
        } else {
            searchjobvalue.setText("");
        }
    }

    private void getEditKeyword() {
        jssearchlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.spinneritemqualification);
                searchjobvalue.setText(tv.getText().toString());
                getFilterJobs();
            }
        });
        searchjobvalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getFilterJobs();
                    return true;
                }
                return false;
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                searchjobvalue.setCursorVisible(true);
                searchKeywordTouch();
            }
        });
        searchjobvalue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchjobvalue.setCursorVisible(true);
                searchKeywordTouch();
                return false;
            }
        });

        searchjobvalue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int
                    count) {
                if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
                    if (GlobalData.getCRole == null || GlobalData.getCRole.isEmpty()) {
                        GlobalData.getCRole = GlobalData.getRole;
                    }
                }
                if (GlobalData.MainSuggestionList.size() > 0) {
                    GlobalData.suggestionsList = new ArrayList<>();
                    if (GlobalData.jobSuggestions.size() > 0) {
                        GlobalData.suggestionsList.addAll(GlobalData.jobSuggestions);
                    }
                    GlobalData.suggestionsList.addAll(GlobalData.MainSuggestionList);
                    Set<String> hs = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    hs.addAll(GlobalData.suggestionsList);
                    GlobalData.suggestionsList.clear();
                    GlobalData.suggestionsList.addAll(hs);
                }
                //myjobsAdapter.filter(s.toString());
                if (s.toString().length() > 0 && GlobalData.suggestionsList.size() > 0) {
                    clear.setVisibility(View.VISIBLE);
                    filter.setVisibility(View.GONE);
                    getSearchKeyword();
                    locAdapter = new AutoCompleteAdapter(Homepage.this, GlobalData.suggestionsList);
                    jssearchlist.setAdapter(locAdapter);
                    locAdapter.filter(s.toString());
                } else {
                    GlobalData.getSearchKeyword = null;
                    getSearchKeyword();
                    clear.setVisibility(View.GONE);
                    filter.setVisibility(View.VISIBLE);
                    if (GlobalData.jobSuggestions.size() > 0 && searchjobvalue.getText().toString().length() == 0) {
                        locAdapter = new AutoCompleteAdapter(Homepage.this, GlobalData.jobSuggestions);
                        jssearchlist.setAdapter(locAdapter);
                        locAdapter.filter("");
                    } else {
                        if (GlobalData.suggestionsList.size() > 0 && locAdapter != null) {
                            locAdapter.clearallitems();
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
        searchjobvalue.setCursorVisible(true);
       /* ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchmargin.getLayoutParams();
        int myMarginPx = getResources().getDimensionPixelSize(R.dimen.searchtopp);
        params.setMargins(0, myMarginPx, 0, 0);
        searchmargin.setLayoutParams(params);*/
        jssearchlistview.setVisibility(View.VISIBLE);
        if (GlobalData.joblistfrom.equalsIgnoreCase("RL")) {
            categoryview.setVisibility(View.VISIBLE);
            isVerifyCategoryAvailability();
            joblistview.setVisibility(View.GONE);
        } else {
            categoryview.setVisibility(View.GONE);
            joblistview.setVisibility(View.VISIBLE);
        }
        search.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        if (GlobalData.jobSuggestions.size() == 0 && searchjobvalue.getText().toString().length() == 0) {
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
        } else if (GlobalData.jobSuggestions.size() > 0 && searchjobvalue.getText().toString().length() == 0) {
            locAdapter = new AutoCompleteAdapter(Homepage.this, GlobalData.jobSuggestions);
            filter.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
            jssearchlist.setAdapter(locAdapter);
        } else if (searchjobvalue.getText().toString().length() > 0 && GlobalData.suggestionsList.size() > 0) {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
            locAdapter = new AutoCompleteAdapter(Homepage.this, GlobalData.suggestionsList);
            jssearchlist.setAdapter(locAdapter);
            locAdapter.filter(searchjobvalue.getText().toString());
        } else {
            clear.setVisibility(View.VISIBLE);
            filter.setVisibility(View.GONE);
        }
    }

    private void getFilterJobs() {
        GlobalData.joblistfrom = "JL";
        GlobalData.Landingkeyword = searchjobvalue.getText().toString().trim();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Homepage.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("JobListFrom", GlobalData.joblistfrom);
        editor.putString("LSKEY", GlobalData.Landingkeyword);
        editor.apply();
        jssearchlistview.setVisibility(View.GONE);
        categoryview.setVisibility(View.GONE);
        joblistview.setVisibility(View.VISIBLE);
        GlobalData.getSearchKeyword = searchjobvalue.getText().toString().trim();
        searchjobvalue.setCursorVisible(false);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (GlobalData.MainSuggestionList.size() > 0) {
            GlobalData.suggestionsList = new ArrayList<>();
            if (GlobalData.jobSuggestions.size() > 0) {
                GlobalData.suggestionsList.addAll(GlobalData.jobSuggestions);
            }
            GlobalData.suggestionsList.addAll(GlobalData.MainSuggestionList);
            Set<String> hs = new HashSet<>();
            hs.addAll(GlobalData.suggestionsList);
            GlobalData.suggestionsList.clear();
            GlobalData.suggestionsList.addAll(hs);
        }
        if (GlobalData.getSearchKeyword.length() > 0) {
            // myjobsAdapter.filter(GlobalData.getSearchKeyword);
            new getRoleJoblist().execute();
            getSearchKeyword();
            if (GlobalData.getSearchKeywords != null && !GlobalData.getSearchKeywords
                    .isEmpty()) {
                String[] viewcateadd = GlobalData.getSearchKeywords.split(",");
                Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                set.addAll(GlobalData.jobSuggestions);
                for (String aViewcateadd : viewcateadd) {
                    if (!set.contains(aViewcateadd)) {
                        GlobalData.jobSuggestions.add(aViewcateadd);
                    }
                }
            }
            Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(GlobalData.jobSuggestions);
            if (!set.contains(GlobalData
                    .getSearchKeyword) && GlobalData.getSearchKeyword.length() > 0) {
                if (GlobalData.jobSuggestions.size() < 2) {
                    GlobalData.jobSuggestions.add(0, GlobalData.getSearchKeyword);
                } else {
                    GlobalData.jobSuggestions.add(0, GlobalData.getSearchKeyword);
                    GlobalData.jobSuggestions.remove(2);
                }
            }
            if (GlobalData.jobSuggestions.size() > 0) {
                String[] getkeywords = new String[GlobalData.jobSuggestions.size()];
                for (int k = 0; k < GlobalData.jobSuggestions.size(); k++) {
                    getkeywords[k] = GlobalData.jobSuggestions.get(k);
                }
                if (getkeywords.length > 0) {
                    String mostviewedrole = Arrays.toString(getkeywords);
                    mostviewedrole = mostviewedrole.substring(1, mostviewedrole.length() - 1);
                    mostviewedrole = mostviewedrole.replace(", ", ",");
                    GlobalData.getSearchKeywords = mostviewedrole;
                    SharedPreferences sharedPreferences1 = PreferenceManager
                            .getDefaultSharedPreferences(Homepage.this);
                    Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("F_L", GlobalData.getLocation);
                    editor1.putString("SKS", GlobalData.getSearchKeywords);
                    editor1.apply();
                }
            }
        } else {
            GlobalData.getSearchKeyword = null;
            getSearchKeyword();
        }
    }

    private void showfilterImage() {
        if ((GlobalData.getLocation == null || GlobalData.getLocation.isEmpty())
                && (GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty())
                && (GlobalData.getRole == null || GlobalData.getRole.isEmpty())
                && (GlobalData.getSSalary == null || GlobalData.getSSalary.isEmpty())
                && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                && (GlobalData.getExperience == null || GlobalData.getExperience.isEmpty())
                //&& (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                && (GlobalData.getGender == null || GlobalData.getGender.isEmpty())) {
            filter.setImageResource(R.drawable.filter_icon);
        } else {
            filter.setImageResource(R.drawable.filter_tick);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    locationconnected();
                } else {
                    Toast.makeText(Homepage.this, getString(R.string.locdenied), Toast.LENGTH_SHORT).show();
                    GlobalData.getLocation = null;
                    currentlocation.setText(getString(R.string.allindia));
                    savelocationpopup();
                    filter.setImageResource(R.drawable.filter_icon);
                    if (new UtilService().isNetworkAvailable(Homepage.this)) {
                        getRolelist();
                    } else {
                        Toast.makeText(Homepage.this, getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
        }
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
                        savelocationpopup();
                        break;
                }
            }
        } else {
            savelocationpopup();
            if (new UtilService().isNetworkAvailable(Homepage.this)) {
                getRolelist();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savelocationpopup() {
        GlobalData.getdefaultLocationHome = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (Homepage.this);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        editor.apply();
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Homepage.this, android.Manifest
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

    private void getRolelist() {
        if (new UtilService().isNetworkAvailable(Homepage.this)) {
            new getRole().execute(GlobalData.url + "jobsearch_new.php");
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.e("prev-load-time-yes", currentDateTimeString);
        } else {
            Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    public void onDestroy() {
        ///locationManager.rLemoveUpdates(locationListener);
        // LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,);
    }

    private class getRole extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected Integer doInBackground(String... params) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", removealltext);
            }
            if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
                paramsadd.addFormDataPart("locationflag", "No");
            }
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            }
            /* else {
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                }
            }*/
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            /*if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", GlobalData.getSkill);
            };*/
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "jobsearch_new.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.getroleresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.e("after-load-time-yes", currentDateTimeString + " " + GlobalData.getroleresponse);
            if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                    getrolecounts = getrolelocresponse.getInt("count");
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getLocation = getrolelocresponse.getString("citi_name");
                        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                            GlobalData.getNearLocation = getrolelocresponse.getString("cities_name");
                            GlobalData.getFinalLocation = getrolelocresponse.getString("cities_name");
                        }
                    }
                    getCityCount();
                } catch (JSONException ignored) {
                }
                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                sharedPreferences1.edit().putString("CATEGORY", GlobalData.getroleresponse)
                        .putString("F_L", GlobalData.getLocation).putString("F_FL", GlobalData.getFinalLocation).
                        putString("F_NL", GlobalData.getNearLocation).apply();
                if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
                    backfromfilter();
                } else {
                    getroleCategory();
                }
            } else {
                Toast.makeText(Homepage.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private class MyRoleCategoryScrollJobs extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected Integer doInBackground(String... params) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", removealltext);
            }
            //   if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
            paramsadd.addFormDataPart("locationflag", "Yes");
            // }
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            }
            /* else {
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                }
            }*/
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            paramsadd.addFormDataPart("scroll", "scroll");
            paramsadd.addFormDataPart("rolenamescroll", params[0]);
            /*if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", GlobalData.getSkill);
            };*/
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "jobsearch_new.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.getroleresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.e("after-load-time-yes", currentDateTimeString + " " + GlobalData.getroleresponse);
            if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                    getrolecounts = getrolelocresponse.getInt("count");
                    JSONArray groups = getrolelocresponse.getJSONArray("role_name");
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject c = groups.getJSONObject(i);
                        JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                        jscategoryitem.setRole_id(c.getString("role_group_id"));
                        jscategoryitem.setRole_name(c.getString("role_name"));
                        jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                        jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                        GlobalData.select_role.add(jscategoryitem);
                    }
                    GlobalData.joblistfrom = "RL";
                    categoryview.setVisibility(View.VISIBLE);
                    joblistview.setVisibility(View.GONE);
                    if (GlobalData.select_role.size() != 0) {
                        jscategory.setVisibility(View.VISIBLE);
                        homepagec_emptymessage.setVisibility(View.GONE);
                        if(jscategory.getAdapter() == null) {
                            mycategoryAdapter = new CategoryList_Adpater(Homepage.this, R.layout.jscategory_row,
                                    GlobalData.select_role);
                            jscategory.setAdapter(mycategoryAdapter);
                        }
                        else{
                            ((CategoryList_Adpater)jscategory.getAdapter()).refill(GlobalData.select_role);
                        }
                        Log.e("pos",""+categoryindex);
                        int currentPosition = jscategory.getFirstVisiblePosition();
                        jscategory.setSelection(currentPosition);
                        Log.e("pos-curr",""+currentPosition);
                        jscategory.setSelectionFromTop(currentPosition, 0);
                        //jscategory.smoothScrollToPosition(currentPosition);
                        categoryonclick();
                    } else {
                        jscategory.setVisibility(View.GONE);
                        homepagec_emptymessage.setVisibility(View.VISIBLE);
                    }
                    isVerifyCategoryAvailability();
                    if (!isCheckedKeyword) {
                        getEditKeyword();
                        isCheckedKeyword = true;
                    }
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getLocation = getrolelocresponse.getString("citi_name");
                        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                            GlobalData.getNearLocation = getrolelocresponse.getString("cities_name");
                            GlobalData.getFinalLocation = getrolelocresponse.getString("cities_name");
                        }
                    }
                    getCityCount();
                } catch (JSONException ignored) {
                }
                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                sharedPreferences1.edit().putString("CATEGORY", GlobalData.getroleresponse)
                        .putString("F_L", GlobalData.getLocation).putString("F_FL", GlobalData.getFinalLocation).
                        putString("F_NL", GlobalData.getNearLocation).apply();
            } else {
                Toast.makeText(Homepage.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private class getRoleJoblist extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected Integer doInBackground(String... params) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", removealltext);
            }
            if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
                paramsadd.addFormDataPart("locationflag", "No");
            }
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            }
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            String keywords = searchjobvalue.getText().toString().trim();
            if (keywords != null && !keywords.isEmpty()) {
                paramsadd.addFormDataPart("keyword", searchjobvalue.getText().toString().trim());
            }
             /*  if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", GlobalData.getSkill);
            }*/
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "joblisting.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.getroleresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getLocation = getrolelocresponse.getString("citi_name");
                        GlobalData.getNearLocation = getrolelocresponse.getString("cities_name");
                        GlobalData.getFinalLocation = getrolelocresponse.getString("cities_name");
                        getCityCount();
                    }
                    SharedPreferences sharedPreferences1 = PreferenceManager
                            .getDefaultSharedPreferences(Homepage.this);
                    sharedPreferences1.edit().putString("CATEGORY", GlobalData.getroleresponse)
                            .putString("F_L", GlobalData.getLocation).putString("F_FL", GlobalData.getFinalLocation).
                            putString("F_NL", GlobalData.getNearLocation).apply();
                    JSONObject responseObj = new JSONObject(GlobalData.getroleresponse);
                    Gson gson = new Gson();
                    jssearchlistview.setVisibility(View.GONE);
                    categoryview.setVisibility(View.GONE);
                    joblistview.setVisibility(View.VISIBLE);
                    GlobalData.jobList = new ArrayList<>();
                    GlobalData.jobList = gson.fromJson(responseObj.getString
                                    ("all_search_view"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    getcount = responseObj.getInt("count");
                    getJobListResume();
                    if (!isCheckedKeyword) {
                        isCheckedKeyword = true;
                        getEditKeyword();
                    }
                } catch (JSONException ignored) {
                }
            }
        }
    }

    private class getRoleJoblistScroll extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected Integer doInBackground(String... params) {
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            paramsadd.addFormDataPart("action", "filter");
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", removealltext);
            }
            if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
                paramsadd.addFormDataPart("locationflag", "No");
            }
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            }
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            paramsadd.addFormDataPart("job_id", params[0]);
            paramsadd.addFormDataPart("scroll", "Scroll");
            String keywords = searchjobvalue.getText().toString();
            if (keywords != null && !keywords.isEmpty()) {
                paramsadd.addFormDataPart("keyword", keywords);
            }
             /*  if (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty()) {
                paramsadd.addFormDataPart("skill", GlobalData.getSkill);
            }*/
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "joblisting.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                GlobalData.getroleresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                    if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                        GlobalData.getLocation = getrolelocresponse.getString("citi_name");
                        GlobalData.getNearLocation = getrolelocresponse.getString("cities_name");
                        GlobalData.getFinalLocation = getrolelocresponse.getString("cities_name");
                        getCityCount();
                    }
                    SharedPreferences sharedPreferences1 = PreferenceManager
                            .getDefaultSharedPreferences(Homepage.this);
                    sharedPreferences1.edit().putString("CATEGORY", GlobalData.getroleresponse)
                            .putString("F_L", GlobalData.getLocation).putString("F_FL", GlobalData.getFinalLocation).
                            putString("F_NL", GlobalData.getNearLocation).apply();
                    JSONObject responseObj = new JSONObject(GlobalData.getroleresponse);
                    Gson gson = new Gson();
                    jssearchlistview.setVisibility(View.GONE);
                    categoryview.setVisibility(View.GONE);
                    joblistview.setVisibility(View.VISIBLE);
                    // GlobalData.jobList = new ArrayList<>();
                    ArrayList<Jobs> scrollList = gson.fromJson(responseObj.getString
                                    ("all_search_view"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    getcount = responseObj.getInt("count");
                    if (scrollList.size() > 0) {
                        GlobalData.jobList.addAll(scrollList);
                    }
                    if (GlobalData.jobList.size() != 0) {
                        Log.e("home-jl", "" + GlobalData.jobList.size());
                        homepage_emptymessage.setVisibility(View.GONE);
                        jobslist.setVisibility(View.VISIBLE);
                    } else {
                        alertmessages();
                        homepage_emptymessage.setVisibility(View.VISIBLE);
                        jobslist.setVisibility(View.GONE);
                    }
                    int currentPosition = jobslist.getFirstVisiblePosition();
                    myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
                    jobslist.setAdapter(myjobsAdapter);
                    jobslist.setSelection(jobindex);
                    jobslist.setSelectionFromTop(currentPosition, 0);
                    if (GlobalData.Landingkeyword != null && !GlobalData.Landingkeyword.isEmpty
                            ()) {
                        if (!GlobalData.Landingkeyword.equalsIgnoreCase(" ")) {
                            searchjobvalue.setText(GlobalData.Landingkeyword);
                            //  myjobsAdapter.filter(GlobalData.Landingkeyword);
                        } else {
                            // myjobsAdapter.filter("");
                            searchjobvalue.setText("");
                        }
                    } else {
                        // myjobsAdapter.filter("");
                        searchjobvalue.setText("");
                    }
                    if (!isCheckedKeyword) {
                        isCheckedKeyword = true;
                        getEditKeyword();
                    }
                } catch (JSONException ignored) {
                }
            }
        }
    }

    private void getroleCategory() {
        jssearchlistview.setVisibility(View.GONE);
        //from home
        if (!(GlobalData.getrolepage.equalsIgnoreCase("DR"))) {
            try {
                if (GlobalData.getroleresponse != null && !GlobalData.getroleresponse.isEmpty()) {
                    final JSONObject getrolelocresponse = new JSONObject(GlobalData.getroleresponse);
                    JSONObject responseObj = new JSONObject(GlobalData.getroleresponse);
                    getFilterStatus = responseObj.getString("message");
                    if (!languages.equalsIgnoreCase("English")) {
                        getFilterStatusTamil = responseObj.getString("message_tamil");
                    }
                    Gson gson = new Gson();
                    if ((!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No")) || GlobalData.islocationAvail.equalsIgnoreCase("No")) {
                        GlobalData.LocationList = new ArrayList<>();
                        GlobalData.LocationList = gson.fromJson(getrolelocresponse.getString
                                        ("locations"),
                                new TypeToken<ArrayList<FilterLocation>>() {
                                }.getType());
                        //citylist
                        GlobalData.locationCityList = gson.fromJson(getrolelocresponse.getString("filterlocations"), new
                                TypeToken<ArrayList<City>>() {
                                }.getType());
                        GlobalData.MainlocationCityList = gson.fromJson(getrolelocresponse.getString("filterlocations"), new
                                TypeToken<ArrayList<City>>() {
                                }.getType());
                        if (GlobalData.LocationList.size() > 0) {
                            GlobalData.islocationAvail = "Yes";
                        }
                        if (!languages.equalsIgnoreCase("English")) {
                            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                                getCityCount();
                            } else {
                                currentlocation.setText(R.string.allindia);
                            }
                        }
                    }
                    //keywordlist
                    GlobalData.suggestionsList = new ArrayList<>();
                    GlobalData.MainSuggestionList = new ArrayList<>();
                    JSONArray keywordgroups = responseObj.getJSONArray("keywords");
                    for (int i = 0; i < keywordgroups.length(); i++) {
                        JSONObject c = keywordgroups.getJSONObject(i);
                        String occupations_list_name = c.getString("keyword");
                        GlobalData.suggestionsList.add(occupations_list_name);
                    }
                    GlobalData.MainSuggestionList.addAll(GlobalData.suggestionsList);
                    if (getFilterStatus.equalsIgnoreCase("success")) {
                        getcount = responseObj.getInt("count");
                        if (getStateID != null && !getStateID.isEmpty()) {
                            for (int i = 0; i < GlobalData.LocationList.size(); i++) {
                                if (GlobalData.LocationList.get(i).getId().equals(getStateID)) {
                                    GlobalData.CityList = GlobalData.LocationList.get(i)
                                            .getCities();
                                }
                            }
                        } else {
                            GlobalData.CityList = GlobalData.LocationList.get(0).getCities();
                        }
                        GlobalData.joblistfrom = "RL";
                        GlobalData.getyesnoflag = "Yes";
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                (Homepage.this);
                        Editor editor = sharedPreferences.edit();
                        editor.putString("JobListFrom", GlobalData.joblistfrom);
                        editor.putString("YNFLAG", GlobalData.getyesnoflag);
                        editor.apply();
                        /*SharedPreferences jobfilterPreferences = getSharedPreferences
                                (M_Filter_Job,
                                        MODE_PRIVATE);
                        jobfilterPreferences.edit().putString("JobListFrom", GlobalData.joblistfrom)
                                .apply();*/
                        GlobalData.select_role = new ArrayList<>();
                        JSONArray groups = responseObj.getJSONArray("role_name");
                        for (int i = 0; i < groups.length(); i++) {
                            JSONObject c = groups.getJSONObject(i);
                            JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                            jscategoryitem.setRole_id(c.getString("role_group_id"));
                            jscategoryitem.setRole_name(c.getString("role_name"));
                            jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                            jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                            GlobalData.select_role.add(jscategoryitem);
                        }
                        getCategoryList();
                        categoryview.setVisibility(View.VISIBLE);
                        joblistview.setVisibility(View.GONE);
                        isVerifyCategoryAvailability();
                        if (!isCheckedKeyword) {
                            getEditKeyword();
                            isCheckedKeyword = true;
                        }
                    } else {
                        showfilterImage();
                        String new_word = getFilterStatus.substring(getFilterStatus.length() - 6);
                        String comparestring = "again.";
                        if (new_word.equalsIgnoreCase(comparestring)) {
                            alertDialog = new Dialog(Homepage.this, R.style.MyThemeDialog);
                            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialog.setCanceledOnTouchOutside(false);
                            View emppromptView = View.inflate(Homepage.this, R
                                    .layout.popup_single, null);
                            TextView f_popupheader = (TextView) emppromptView.findViewById(R.id
                                    .ds_popupheader);
                            f_popupheader.setText(R.string.confirmation);
                            TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                                    .ds_popup_subheader);
                            if ((GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                                    ((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty())
                                            && (GlobalData.getRole == null || GlobalData.getRole.isEmpty())
                                            && (GlobalData.getSSalary == null || GlobalData.getSSalary.isEmpty())
                                            && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                                            && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                                            && (GlobalData.getExperience == null || GlobalData.getExperience.isEmpty())
                                            //|| !(GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                                            && (GlobalData.getGender == null || GlobalData.getGender.isEmpty()))) {
                                if (languages.equalsIgnoreCase("English")) {
                                    f_popupsubheader.setText(getFilterStatus);
                                } else {
                                    f_popupsubheader.setText(R.string.nojobfoundstate);
                                }
                            } else if ((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty())
                                    || (GlobalData.getRole != null && !GlobalData.getRole.isEmpty())
                                    || (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty())
                                    || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                                    || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                                    || (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty())
                                    //|| !(GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                                    || (GlobalData.getGender != null && !GlobalData.getGender.isEmpty())) {
                                f_popupsubheader.setText(R.string.nojobfoundfilcondwoloc);
                            } else {
                                f_popupsubheader.setText(R.string.nojobfoundfilcondwoloc);
                            }
                            Button no = (Button) emppromptView.findViewById(R.id.d_ok);
                            alertDialog.setContentView(emppromptView);
                            alertDialog.show();
                            no.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    showfilterImage();
                                    categoryview.setVisibility(View.GONE);
                                    joblistview.setVisibility(View.VISIBLE);
                                    homepage_emptymessage.setVisibility(View.VISIBLE);
                                    jobslist.setVisibility(View.GONE);
                                    GlobalData.getyesnoflag = "Yes";
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                            (Homepage.this);
                                    Editor editor = sharedPreferences.edit();
                                    editor.putString("YNFLAG", GlobalData.getyesnoflag);
                                    editor.apply();
                                    alertmessages();
                                    resetNearFinalLocation();
                                    alertDialog.dismiss();
                                }
                            });
                        } else {
                            if (((GlobalData.getIndustry == null || GlobalData.getIndustry.isEmpty()) && GlobalData.getRole == null
                                    && GlobalData.getSSalary == null
                                    && (GlobalData.getJobType == null || GlobalData.getJobType.isEmpty())
                                    && (GlobalData.getJobIDType == null || GlobalData.getJobIDType.isEmpty())
                                    && GlobalData.getExperience == null
                                    // && (GlobalData.getSkill == null || GlobalData.getSkill.isEmpty())
                                    && GlobalData.getGender == null) && (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) &&
                                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getPrevLocation)) &&
                                    (GlobalData.getLocation.equalsIgnoreCase(GlobalData.getFinalLocation))
                                    && getyesnoflag.equalsIgnoreCase("No")) {
                                GlobalData.select_role = new ArrayList<>();
                                try {
                                    JSONArray groups = getrolelocresponse.getJSONArray("role_name");
                                    for (int i = 0; i < groups.length(); i++) {
                                        JSONObject c = groups.getJSONObject(i);
                                        JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                                        jscategoryitem.setRole_id(c.getString("role_group_id"));
                                        jscategoryitem.setRole_name(c.getString("role_name"));
                                        jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                                        jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                                        GlobalData.select_role.add(jscategoryitem);
                                    }
                                } catch (JSONException ignored) {
                                }
                                backfromfilter();
                            } else {
                                alertDialog = new Dialog(Homepage.this, R.style.MyThemeDialog);
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCanceledOnTouchOutside(false);
                                View emppromptView = View.inflate(Homepage.this, R
                                        .layout.delete_popup, null);
                                TextView f_popupheader = (TextView) emppromptView.findViewById(R
                                        .id.d_popupheader);
                                f_popupheader.setText(R.string.confirmation);
                                TextView f_popupsubheader = (TextView) emppromptView.findViewById
                                        (R.id.d_popup_subheader);
                                f_popupsubheader.setText(getFilterStatus);
                                if (!languages.equalsIgnoreCase("English")) {
                                    f_popupsubheader.setText(getFilterStatusTamil);
                                }
                                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                                alertDialog.setContentView(emppromptView);
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                                yes.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        GlobalData.select_role = new ArrayList<>();
                                        try {
                                            JSONArray groups = getrolelocresponse.getJSONArray("role_name");
                                            for (int i = 0; i < groups.length(); i++) {
                                                JSONObject c = groups.getJSONObject(i);
                                                JS_CATEGORY jscategoryitem = new JS_CATEGORY();
                                                jscategoryitem.setRole_id(c.getString("role_group_id"));
                                                jscategoryitem.setRole_name(c.getString("role_name"));
                                                jscategoryitem.setRole_name_local(c.getString("role_name_tamil"));
                                                jscategoryitem.setImg(GlobalData.imageurl + c.getString("img"));
                                                GlobalData.select_role.add(jscategoryitem);
                                            }
                                        } catch (JSONException ignored) {
                                        }
                                        backfromfilter();
                                        GlobalData.getyesnoflag = "No";
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                                (Homepage.this);
                                        Editor editor = sharedPreferences.edit();
                                        editor.putString("YNFLAG", GlobalData.getyesnoflag);
                                        editor.apply();
                                        alertDialog.dismiss();
                                    }
                                });
                                no.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        GlobalData.getroleresponse = null;
                                        GlobalData.getyesnoflag = "Yes";
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                                                (Homepage.this);
                                        Editor editor = sharedPreferences.edit();
                                        editor.putString("CATEGORY", GlobalData.getroleresponse);
                                        editor.putString("YNFLAG", GlobalData.getyesnoflag);
                                        editor.apply();
                                        resetNearFinalLocation();
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (JSONException ignored) {
            }
        } else {
            GlobalData.joblistfrom = "RL";
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(Homepage.this);
            Editor editor = sharedPreferences.edit();
            editor.putString("JobListFrom", GlobalData.joblistfrom);
            editor.apply();
            mycategoryAdapter = new CategoryList_Adpater(Homepage.this, R.layout.jscategory_row,
                    GlobalData.select_role);
            jscategory.setAdapter(mycategoryAdapter);
            if (GlobalData.select_role.size() != 0) {
                mycategoryAdapter.setGridData(GlobalData.select_role);
            }
            jscategory.setSelection(categoryindex);
            if (!isCheckedKeyword) {
                getEditKeyword();
                isCheckedKeyword = true;
            }
            categoryonclick();
        }
        if (GlobalData.jobList.size() != 0) {
            myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
            jobslist.setAdapter(myjobsAdapter);
            homepage_emptymessage.setVisibility(View.GONE);
            jobslist.setVisibility(View.VISIBLE);
        } else {
            alertmessages();
            homepage_emptymessage.setVisibility(View.VISIBLE);
            jobslist.setVisibility(View.GONE);
        }
        categoryonclick();
    }

    private void getCategoryList() {
        GlobalData.Landingkeyword = searchjobvalue.getText().toString().trim();
        setCategoryList();
    }

    private void setCategoryList() {
        GlobalData.joblistfrom = "RL";
        categoryview.setVisibility(View.VISIBLE);
        joblistview.setVisibility(View.GONE);
        if (GlobalData.select_role.size() != 0) {
            jscategory.setVisibility(View.VISIBLE);
            homepagec_emptymessage.setVisibility(View.GONE);
            mycategoryAdapter = new CategoryList_Adpater(Homepage.this, R.layout.jscategory_row,
                    GlobalData.select_role);
            jscategory.setAdapter(mycategoryAdapter);
            jscategory.setSelection(categoryindex);
            categoryonclick();
        } else {
            jscategory.setVisibility(View.GONE);
            homepagec_emptymessage.setVisibility(View.VISIBLE);
        }
    }

    private void resetNearFinalLocation() {
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            GlobalData.getNearLocation = GlobalData.getLocation;
            GlobalData.getFinalLocation = GlobalData.getLocation;
            SharedPreferences sharedPreferences1 = PreferenceManager
                    .getDefaultSharedPreferences(Homepage.this);
            Editor editor1 = sharedPreferences1.edit();
            editor1.putString("F_NL", GlobalData.getNearLocation);
            editor1.putString("F_FL", GlobalData.getFinalLocation);
            editor1.apply();
        }
        if (GlobalData.select_role.size() > 0) {
            GlobalData.select_role.clear();
        }
        if (GlobalData.jobList.size() > 0) {
            GlobalData.jobList.clear();
        }
        if (GlobalData.joblistfrom.equalsIgnoreCase("JL")) {
            alertmessages();
            categoryview.setVisibility(View.GONE);
            joblistview.setVisibility(View.VISIBLE);
            homepage_emptymessage.setVisibility(View.GONE);
            homepagec_emptymessage.setVisibility(View.GONE);
            jobslist.setVisibility(View.GONE);
        } else {
            categoryview.setVisibility(View.VISIBLE);
            joblistview.setVisibility(View.GONE);
            homepagec_emptymessage.setVisibility(View.GONE);
            homepage_emptymessage.setVisibility(View.GONE);
            jscategory.setVisibility(View.GONE);
        }
    }


    private void isVerifyLocationListAvailable() {
        if (new UtilService().isNetworkAvailable(Homepage.this)) {
            if (GlobalData.LocationList.size() > 0) {
                LocationAlert();
            } else {
                new getLocation().execute();
            }
        } else {
            Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void LocationAlert() {
        GlobalData.getrolepage = "Home";
        GlobalData.joblistfrom = "RL";
        GlobalData.getdefaultLocationHome = false;
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            GlobalData.getPrevLocation = GlobalData.getLocation;
            GlobalData.getdefaultLocationHome = false;
            SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                    (Homepage.this);
            Editor editorr = sharedPreferencess.edit();
            editorr.putString("F_PL", GlobalData.getPrevLocation);
            editorr.apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (Homepage.this);
        Editor editor = sharedPreferences.edit();
        editor.putString("ROLEPAGE", GlobalData.getrolepage);
        editor.putString("JobListFrom", GlobalData.joblistfrom);
        // editor.putString("F_PL", GlobalData.getPrevLocation);
        editor.putBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        editor.apply();
        final Dialog alertDialog = new Dialog(Homepage.this, R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Homepage.this, R.layout
                .location_filter_popup, null);
        locstate_view = (LinearLayout) emppromptView.findViewById(R.id.locstate_view);
        city_view = (LinearLayout) emppromptView.findViewById(R.id.city_view);
        filterstate = (ListView) emppromptView.findViewById(R.id.filterlocstatelist);
        filtercity = (ListView) emppromptView.findViewById(R.id.filterloccitylist);
        cl_cityheader = (TextView) emppromptView.findViewById(R.id.cl_cityheader);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ImageButton cl_cityheader_back = (ImageButton) emppromptView.findViewById(R.id
                .cl_cityheader_back);
        autocity = (AutoCompleteTextView) emppromptView.findViewById(R.id.locfil_citysearch);
        locfilt_citysearch = (AutoCompleteTextView) emppromptView.findViewById(R.id
                .locfilt_citysearch);
        stateselectedloc = (TextView) emppromptView.findViewById(R.id.loc_locationadded);
        cityselectedloc = (TextView) emppromptView.findViewById(R.id.loccity_locationadded);
        autocity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autocity.setFocusableInTouchMode(true);
                locfilt_citysearch.setFocusableInTouchMode(false);
                return false;
            }
        });
        locfilt_citysearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                locfilt_citysearch.setFocusableInTouchMode(true);
                autocity.setFocusableInTouchMode(false);
                return false;
            }
        });
        selectedLocationList = new ArrayList<>();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            getCityCount();
            List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
            for (int i = 0; i < locationlist.size(); i++) {
                if (!(selectedLocationList.contains(locationlist.get(i)))) {
                    selectedLocationList.add(locationlist.get(i));
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            currentlocation.setText(R.string.allindia);
        }
        //city back button
        cl_cityheader_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                locstate_view.setVisibility(View.VISIBLE);
                city_view.setVisibility(View.GONE);
                setStateAdapter();
            }
        });
        locstate_view.setVisibility(View.VISIBLE);
        city_view.setVisibility(View.GONE);
        filterstate.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        filtercity.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //state list
        setStateAdapter();
        //statelist onclick
        filterstate.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                getState = GlobalData.LocationList.get(position).getState_name();
                getStateID = GlobalData.LocationList.get(position).getId();
                autocity.setFocusableInTouchMode(false);
                locfilt_citysearch.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocity.getWindowToken(), 0);
                getCityListAdapter();
            }
        });
        //city - autocomplete textview
        if (GlobalData.locationCityList.size() > 0) {
            /*ArrayAdapter<String> locAdapter = new ArrayAdapter<>(Homepage.this, R.layout
                    .spinner_item_text,
                    GlobalData.locationCityList);*/
            final CityAdapter loccityAdapter = new CityAdapter(Homepage.this, R.layout.spinner_item_text, GlobalData.locationCityList) {
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
                    String yourValue = GlobalData.locationCityList.get(position).getCiti_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        yourValue = GlobalData.locationCityList.get(position).getCity_name_local();
                    }
                    textView.setText(yourValue);
                    return textView;
                }
            };
            autocity.setAdapter(loccityAdapter);
            autocity.setThreshold(1);
            autocity.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    autocity.setText("");
                    if (languages.equalsIgnoreCase("English")) {
                        if (!selectedLocationList.contains(loccityAdapter.getItem(i).getCiti_name())) {
                            selectedLocationList.add(loccityAdapter.getItem(i).getCiti_name());
                            setStateAdapter();
                        }
                    } else {
                        if (!selectedLocationList.contains(loccityAdapter.getItem(i).getCiti_name())) {
                            selectedLocationList.add(loccityAdapter.getItem(i).getCiti_name());
                            setStateAdapter();
                        }
                    }
                }
            });
        }

        Button done_filter = (Button) emppromptView.findViewById(R.id.locdone_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.locresetall_filter);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDone();
                alertDialog.dismiss();
            }
        });
        //location - reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getState = null;
                getStateID = null;
                if (selectedLocationList.size() > 0) {
                    selectedLocationList.clear();
                    if (!languages.equalsIgnoreCase("English")) {
                        selectedLocationLocalList.clear();
                    }
                }
                if (GlobalData.CityList.size() > 0) {
                    isCheckedLocation = new boolean[GlobalData.CityList.size()];
                    Arrays.fill(isCheckedLocation, false);
                }
                GlobalData.getLocation = null;
                GlobalData.getNearLocation = null;
                GlobalData.getFinalLocation = null;
                if (GlobalData.getdefaultLocationHome) {
                    GlobalData.getdefaultLocationHome = false;
                    SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                            (Homepage.this);
                    Editor editorr = sharedPreferencess.edit();
                    editorr.putBoolean("JSLOC", GlobalData
                            .getdefaultLocationHome).apply();
                }
                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(Homepage.this);
                sharedPreferences1.edit().putString("F_L", GlobalData.getLocation).putString("F_NL", GlobalData.getNearLocation)
                        .putString("F_FL", GlobalData.getFinalLocation).apply();
                currentlocation.setText(R.string.allindia);
                showfilterImage();
                alertDialog.dismiss();
                LocationAlert();
                //locationDone();
                //isVerifyLocationListAvailable();
            }
        });
        //location - done
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                locationDone();
                autocity.setFocusableInTouchMode(false);
                locfilt_citysearch.setFocusableInTouchMode(false);
                alertDialog.dismiss();
            }
        });
        //statelistedittext keyboard done
        autocity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    locationDone();
                    autocity.setFocusableInTouchMode(false);
                    locfilt_citysearch.setFocusableInTouchMode(false);
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        //citylistedittext keyboard done
        locfilt_citysearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    locationDone();
                    autocity.setFocusableInTouchMode(false);
                    locfilt_citysearch.setFocusableInTouchMode(false);
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        if (selectedLocationList.size() > 0) {
            stateselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                @Override
                public boolean onDrawableClick() {
                    getFrom = "State";
                    getSelectedCities();
                    return true;
                }
            });
        }
    }

    private void setSelectedLocation(TextView stateselectedloc, TextView cityselectedloc) {
        selectedLocationLocalList = new ArrayList<>();
        List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
        for (int i = 0; i < locationlist.size(); i++) {
            City cityname = new City();
            cityname.setCiti_name(locationlist.get(i));
            int j = GlobalData.MainlocationCityList.indexOf(cityname);
            if (j != -1) {
                selectedLocationLocalList.add(GlobalData.MainlocationCityList.get(j).getCity_name_local());
            }
        }
        String[] getlocationaded = selectedLocationLocalList.toArray(new String[selectedLocationLocalList.size
                ()]);
        String getlocationarray = Arrays.toString(getlocationaded);
        String getLocationTamil = getlocationarray.substring(1, getlocationarray.length() - 1);
        getLocationTamil = getLocationTamil.replace(", ", ",");
        stateselectedloc.setText(getLocationTamil);
        cityselectedloc.setText(getLocationTamil);
    }

    private void locationDone() {
        String[] getLocationaded = selectedLocationList.toArray(new
                String[selectedLocationList.size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() -
                1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        GlobalData.getFinalLocation = GlobalData.getLocation;
        GlobalData.getNearLocation = GlobalData.getLocation;
        SharedPreferences sharedPreferences1 = PreferenceManager
                .getDefaultSharedPreferences(Homepage.this);
        sharedPreferences1.edit().putString("F_L", GlobalData.getLocation).
                putString("F_FL", GlobalData.getFinalLocation)
                .putString("F_NL", GlobalData.getNearLocation).apply();
        GlobalData.findPage = "Home";
        showfilterImage();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            getCityCount();
            if (GlobalData.getPrevLocation == null || GlobalData.getPrevLocation.isEmpty()) {
                GlobalData.getPrevLocation = GlobalData.getLocation;
                GlobalData.getdefaultLocationHome = false;
                SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences
                        (Homepage.this);
                Editor editorr = sharedPreferencess.edit();
                editorr.putString("F_PL", GlobalData.getPrevLocation).putBoolean("JSLOC", GlobalData
                        .getdefaultLocationHome);
                editorr.apply();
            }
            landingpageRefresh();
            // getRolelist();
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            currentlocation.setText(R.string.allindia);
            getRolelist();
        }
    }

    private void getCityListAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        GlobalData.CityList = new ArrayList<>();
        for (int i = 0; i < GlobalData.LocationList.size(); i++) {
            if (GlobalData.LocationList.get(i).getId().equals(getStateID)) {
                GlobalData.CityList = GlobalData.LocationList.get(i).getCities();
            }
        }
        locstate_view.setVisibility(View.GONE);
        city_view.setVisibility(View.VISIBLE);
        cl_cityheader.setText(getState);
        locfilt_citysearch.setText(autocity.getText().toString());
        //get the city list depends upon selected state
        cadapter = new ArrayAdapter<City>(Homepage.this, R.layout.filter_listrow, GlobalData
                .CityList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                textView.setText(GlobalData.CityList.get(position).getCiti_name());
                if (!languages.equalsIgnoreCase("English")) {
                    textView.setText(GlobalData.CityList.get(position).getCity_name_local());
                }
                String locclick = GlobalData.CityList.get(position).getIsavailable();
                if (locclick == null) {
                    v.setEnabled(false);
                    textView.setCheckMarkDrawable(0);
                } else {
                    v.setEnabled(true);
                    textView.setCheckMarkDrawable(R.drawable.custom_checkbox);
                }
                return textView;
            }
        };
        filtercity.setAdapter(cadapter);
        getCityAdapter();
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            stateselectedloc.setVisibility(View.VISIBLE);
            stateselectedloc.setText(GlobalData.getLocation);
            cityselectedloc.setVisibility(View.VISIBLE);
            cityselectedloc.setText(GlobalData.getLocation);
            if (!languages.equalsIgnoreCase("English")) {
                setSelectedLocation(stateselectedloc, cityselectedloc);
            }
            getCityCount();
            List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            indexcity = -1;
            for (int i = 0; i < locationlist.size(); i++) {
                for (int j = 0; j < GlobalData.CityList.size(); j++) {
                    if (!(selectedLocationList.contains(locationlist.get(i)))) {
                        selectedLocationList.add(locationlist.get(i));
                    }
                    if (GlobalData.CityList.get(j).getCiti_name().equals(locationlist.get(i))) {
                        indexcity = j;
                        isCheckedLocation[indexcity] = true;
                        filtercity.setItemChecked(indexcity, true);
                    }
                }
            }
        } else {
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
        }
        //city list item check|uncheck
        filtercity.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedLocation[position]) {
                    isCheckedLocation[position] = true;
                    if (!(selectedLocationList.contains(cadapter.getItem(position).getCiti_name())
                    )) {
                        selectedLocationList.add(cadapter.getItem(position).getCiti_name());
                    }
                } else {
                    isCheckedLocation[position] = false;
                    selectedLocationList.remove(cadapter.getItem(position).getCiti_name());
                }
                String[] getLocationaded = selectedLocationList.toArray(new
                        String[selectedLocationList.size()]);
                String getlocationarray = Arrays.toString(getLocationaded);
                GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() -
                        1);
                GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
                getCityCount();
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(GlobalData.getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(GlobalData.getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
                } else {
                    stateselectedloc.setVisibility(View.GONE);
                    cityselectedloc.setVisibility(View.GONE);
                }
                if (selectedLocationList.size() > 0) {
                    cityselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                        @Override
                        public boolean onDrawableClick() {
                            getFrom = "City";
                            getSelectedCities();
                            return true;
                        }
                    });
                }
            }
        });
    }

    private void setStateAdapter() {
        String[] getLocationaded = selectedLocationList.toArray(new String[selectedLocationList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray.length() - 1);
        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
        ArrayAdapter<FilterLocation> sadapter = new ArrayAdapter<FilterLocation>(Homepage.this, R.layout.skillcategory_row,
                GlobalData.LocationList) {
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
                textView.setText(GlobalData.LocationList.get(position).getState_name());
                if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                    stateselectedloc.setVisibility(View.VISIBLE);
                    stateselectedloc.setText(GlobalData.getLocation);
                    cityselectedloc.setVisibility(View.VISIBLE);
                    cityselectedloc.setText(GlobalData.getLocation);
                    if (!languages.equalsIgnoreCase("English")) {
                        setSelectedLocation(stateselectedloc, cityselectedloc);
                    }
                    getCityCount();
                    List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
                    getStateID = GlobalData.LocationList.get(position).getId();
                    GlobalData.CityList = GlobalData.LocationList.get(position).getCities();
                    ArrayList<String> selectedcitylist = new ArrayList<>();
                    for (int i = 0; i < locationlist.size(); i++) {
                        for (int j = 0; j < GlobalData.CityList.size(); j++) {
                            if (GlobalData.CityList.get(j).getCiti_name().equals(locationlist.get
                                    (i))) {
                                String getCityID = GlobalData.CityList.get(j).getCiti_county_id();
                                if (languages.equalsIgnoreCase("English")) {
                                    selectedcitylist.add(GlobalData.CityList.get(j).getCiti_name());
                                } else {
                                    selectedcitylist.add(GlobalData.CityList.get(j).getCity_name_local());
                                }
                            }
                        }
                        String[] getskilladed = selectedcitylist.toArray(new
                                String[selectedcitylist.size()]);
                        if (getskilladed.length > 0) {
                            selectedskill.setText(Arrays.toString(getskilladed));
                        } else {
                            selectedskill.setText("");
                        }
                    }
                } else {
                    currentlocation.setText(R.string.allindia);
                }
                return root;
            }
        };
        filterstate.setAdapter(sadapter);
        if (selectedLocationList.size() > 0) {
            stateselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                @Override
                public boolean onDrawableClick() {
                    getFrom = "State";
                    getSelectedCities();
                    return true;
                }
            });
        }
    }

    private void getSelectedCities() {
        if (selectedLocationList.size() > 0) {
            selectedLocationList.clear();
            isCheckedLocation = new boolean[GlobalData.CityList.size()];
            Arrays.fill(isCheckedLocation, false);
            stateselectedloc.setVisibility(View.GONE);
            cityselectedloc.setVisibility(View.GONE);
            GlobalData.getLocation = null;
            GlobalData.getNearLocation = null;
            GlobalData.getFinalLocation = null;
            if (getFrom.equalsIgnoreCase("State")) {
                setStateAdapter();
            } else {
                getCityListAdapter();
            }
            currentlocation.setText(R.string.allindia);
            savelocationpopup();
        }
    }

    private void getCityAdapter() {
        if (GlobalData.CityList.size() > 0) {
            final ArrayList<City> getcityforstate = new ArrayList<>();
            getcityforstate.addAll(GlobalData.CityList);
            loccityAdapter = new CityAdapter(Homepage.this, R.layout.spinner_item_text, getcityforstate) {
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
                    String yourValue = getcityforstate.get(position).getCiti_name();
                    if (!languages.equalsIgnoreCase("English")) {
                        yourValue = getcityforstate.get(position).getCity_name_local();
                    }
                    textView.setText(yourValue);
                    return textView;
                }
            };
            locfilt_citysearch.setAdapter(loccityAdapter);
            locfilt_citysearch.setThreshold(1);
            locfilt_citysearch.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    locfilt_citysearch.setText("");
                    if (!selectedLocationList.contains(loccityAdapter.getItem(i).getCiti_name())) {
                        selectedLocationList.add(loccityAdapter.getItem(i).getCiti_name());
                        for (int j = 0; j < GlobalData.CityList.size(); j++) {
                            if (GlobalData.CityList.get(j).getCiti_name().equals(loccityAdapter
                                    .getItem(i).getCiti_name())) {
                                indexcity = j;
                                isCheckedLocation[indexcity] = true;
                                filtercity.setItemChecked(indexcity, true);
                            }
                        }
                        String[] getLocationaded = selectedLocationList.toArray(new
                                String[selectedLocationList.size()]);
                        String getlocationarray = Arrays.toString(getLocationaded);
                        GlobalData.getLocation = getlocationarray.substring(1, getlocationarray
                                .length() - 1);
                        GlobalData.getLocation = GlobalData.getLocation.replace(", ", ",");
                        stateselectedloc.setVisibility(View.VISIBLE);
                        stateselectedloc.setText(GlobalData.getLocation);
                        cityselectedloc.setVisibility(View.VISIBLE);
                        cityselectedloc.setText(GlobalData.getLocation);
                        if (!languages.equalsIgnoreCase("English")) {
                            setSelectedLocation(stateselectedloc, cityselectedloc);
                        }
                        getCityCount();
                    }
                }
            });
            if (selectedLocationList.size() > 0) {
                cityselectedloc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(stateselectedloc) {
                    @Override
                    public boolean onDrawableClick() {
                        getFrom = "City";
                        getSelectedCities();
                        return true;
                    }
                });
            }
        }
    }
    //@Override
    //public void onSaveInstanceState(Bundle outState) {
    //    super.onSaveInstanceState(outState);
    //}

    private void getCityCount() {
        if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
            String removealltext = GlobalData.getLocation.replaceAll("All,", "");
            String[] separated = removealltext.split(",");
            if (separated.length > 1) {
                currentlocation.setText(separated[0] + " " + "+" + (separated.length - 1));
            } else {
                currentlocation.setText(separated[0]);
            }
            if (!languages.equalsIgnoreCase("English")) {
                selectedLocationLocalList = new ArrayList<>();
                List<String> locationlist = Arrays.asList(GlobalData.getLocation.split(","));
                for (int i = 0; i < locationlist.size(); i++) {
                    City cityname = new City();
                    cityname.setCiti_name(locationlist.get(i));
                    int j = GlobalData.MainlocationCityList.indexOf(cityname);
                    if (j != -1 && GlobalData.MainlocationCityList.get(j).getCity_name_local() != null) {
                        selectedLocationLocalList.add(GlobalData.MainlocationCityList.get(j).getCity_name_local());
                    }
                }
                String[] getlocationaded = selectedLocationLocalList.toArray(new String[selectedLocationLocalList.size
                        ()]);
                if (selectedLocationLocalList.size() > 0) {
                    String getlocationarray = Arrays.toString(getlocationaded);
                    String getLocationTamil = getlocationarray.substring(1, getlocationarray.length() - 1);
                    getLocationTamil = getLocationTamil.replace(", ", ",");
                    if (getLocationTamil != null && !getLocationTamil.isEmpty()) {
                        separated = getLocationTamil.split(",");
                        if (separated.length > 1) {
                            currentlocation.setText(separated[0] + " " + "+" + (separated.length - 1));
                        } else {
                            currentlocation.setText(separated[0]);
                        }
                    }
                }
            }
        } else {
            currentlocation.setText(R.string.allindia);
        }
    }

    private class CityAsyncTask extends AsyncTask<String, String, String> {
        final double latitude;
        final double longitude;
        String getlocationresponse = null, addresses1 = "";

        CityAsyncTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            Request requestloc = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyC-BAK5ADr4W1l05AgfrYQ-qjx4sdxKq_s&latlng=" + this
                            .latitude + "," + this.longitude + "&sensor=true")
                    .build();
          /*  Request request = new Request.Builder()
                    .url("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + this
                            .latitude + "," + this.longitude + "&sensor=true")
                    .build();*/
            client = new OkHttpClient();
            try {
                Response responseloc = client.newCall(requestloc).execute();
                responseloc.code();
                getlocationresponse = responseloc.body().string();
            } catch (IOException e) {
                e.printStackTrace();
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
                                SharedPreferences jobfilterPreferences = getSharedPreferences(M_Filter_Job, MODE_PRIVATE);
                                jobfilterPreferences.edit().putString("F_HS", GlobalData
                                        .getHomeState).apply();
                            }
                        }
                        if (GlobalData.getLocation == null || GlobalData.getLocation.isEmpty()) {
                            if (GlobalData.getdefaultLocationHome) {
                                if (addresses1 == null || addresses1.isEmpty()) {
                                    currentlocation.setText(R.string.allindia);
                                } else {
                                    GlobalData.getLocation = addresses1;
                                    GlobalData.getFinalLocation = addresses1;
                                    SharedPreferences sharedPreferences1 = PreferenceManager
                                            .getDefaultSharedPreferences(Homepage.this);
                                    sharedPreferences1.edit().putString("F_L", GlobalData
                                            .getLocation).putString("F_FL", GlobalData
                                            .getFinalLocation).apply();
                                    filter.setImageResource(R.drawable.filter_tick);
                                    if (new UtilService().isNetworkAvailable(Homepage.this)) {
                                        getRolelist();
                                    } else {
                                        currentlocation.setText(R.string.allindia);
                                        Toast.makeText(Homepage.this,
                                                getString(R.string.checkconnection), Toast
                                                        .LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            }
                        }
                    } else {
                        if (new UtilService().isNetworkAvailable(Homepage.this)) {
                            getRolelist();
                        } else {
                            Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                                    .LENGTH_SHORT).show();
                        }
                    }
                }
                /*else{
                    if (new UtilService().isNetworkAvailable(Homepage.this)) {
                        getRolelist();
                    } else {
                        Toast.makeText(Homepage.this, getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }*/
            } catch (JSONException ignored) {
            }
        }
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

    private void getSearchKeyword() {
        GlobalData.getSearchKeyword = searchjobvalue.getText().toString();
        SharedPreferences sharedkeyPreferences = PreferenceManager
                .getDefaultSharedPreferences(Homepage.this);
        Editor editor = sharedkeyPreferences.edit();
        editor.putString("JobKey", GlobalData.getSearchKeyword);
        editor.apply();
    }

    private void alertmessages() {
        if ((GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty())
                || (GlobalData.getRole != null && !GlobalData.getRole.isEmpty())
                || (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty())
                || (GlobalData.getJobType != null && !GlobalData.getJobType.isEmpty())
                || (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty())
                || (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty())
                // || (GlobalData.getSkill != null && !GlobalData.getSkill.isEmpty())
                || (GlobalData.getGender != null && !GlobalData.getGender.isEmpty())) {
            homepage_emptymessage.setText(R.string.nojobfoundfilcond);
        } else {
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                homepage_emptymessage.setText("No job found in " + GlobalData.getLocation + ". " +
                        "Please change the location and TRY again.");
            } else {
                homepage_emptymessage.setText(R.string.nojobfoundstate);
            }
        }
    }

    private void getMenu() {
        GCMRegistrar.checkDevice(Homepage.this);
        GCMRegistrar.checkManifest(Homepage.this);
        regId = GCMRegistrar.getRegistrationId(Homepage.this);
        if (regId.equals("")) {
            GCMRegistrar.register(Homepage.this, SENDER_ID);
        }
        if (GlobalData.login_status.equals("No user found")) {
            logout_image.setImageResource(R.drawable.mylogin_icon);
            logout.setText(R.string.login);
        } else {
            logout_image.setImageResource(R.drawable.logout_icon);
            logout.setText(R.string.logout);
        }
        logout_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (logout.getText().toString().equalsIgnoreCase(getString(R.string.login))) {
                    startActivity(new Intent(Homepage.this, LoginHome.class));
                } else {
                    if (new UtilService()
                            .isNetworkAvailable(getApplicationContext())) {
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Homepage.this);
                        Editor editor = sharedPreferences.edit();
                        GlobalData.ftime = 0;
                        GlobalData.pagefrom = "Home";
                        editor.putLong("FTIME", GlobalData.ftime);
                        editor.putString("PAGEBACK", GlobalData.pagefrom);
                        editor.apply();
                        new getUpdateStatus().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.checkconnection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private class getUpdateStatus extends AsyncTask<String, String, String> {
        String gsonresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("jobseeker_id", GlobalData.login_status)
                    .add("registration_id", regId).add("login_status", "N")
                    .build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "Login_logout_status.php")
                    .post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
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
                    GlobalData.login_status = "No user found";
                    GlobalData.emp_login_status = "No user found";
                    GlobalData.personalresponse = null;
                    // GlobalData.pageback = "Home";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(Homepage.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("LS", GlobalData.login_status);
                    editor.putString("ELS", GlobalData.emp_login_status);
                    editor.putString("USER_DETAILS",
                            GlobalData.personalresponse);
                    //editor.putString("PAGEBACK", GlobalData.pageback);
                    editor.apply();
                    GlobalData.joblistfrom = "RL";
                    startActivity(new Intent(Homepage.this,
                            Homepage.class));
                } catch (Exception e) {
                    Toast.makeText(Homepage.this, getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Homepage.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class getLocation extends AsyncTask<String, String, String> {
        String rresponse;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(Homepage.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {

            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody.FORM);
            String languages = getResources().getConfiguration().locale.getDisplayLanguage();
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            if (!GlobalData.login_status.equalsIgnoreCase("No user found")) {
                paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            }
            if (GlobalData.getIndustry != null && !GlobalData.getIndustry.isEmpty()) {
                paramsadd.addFormDataPart("industry", GlobalData.getIndustry);
            }
            if (GlobalData.getHomeState != null && !GlobalData.getHomeState.isEmpty()) {
                paramsadd.addFormDataPart("state", GlobalData.getHomeState);
            }
            if (GlobalData.getLocation != null && !GlobalData.getLocation.isEmpty()) {
                String removealltext = GlobalData.getFinalLocation.replaceAll("All,", "");
                paramsadd.addFormDataPart("location", GlobalData.getLocation);
            }
            paramsadd.addFormDataPart("locationflag", "Yes");
            //if (GlobalData.islocationAvail.equalsIgnoreCase("No") || (!languages.equalsIgnoreCase("English") && GlobalData.islocationAvail.equalsIgnoreCase("No"))) {
            //    paramsadd.addFormDataPart("locationflag", "No");
            //}
            if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                paramsadd.addFormDataPart("role", GlobalData.getCRole);
            }
            if (GlobalData.getRole != null && !GlobalData.getRole.isEmpty()) {
                paramsadd.addFormDataPart("role_category", GlobalData.getRole);
            } else {
                if (GlobalData.getCRole != null && !GlobalData.getCRole.isEmpty()) {
                    paramsadd.addFormDataPart("role_category", GlobalData.getCRole);
                }
            }
            if (GlobalData.getViewedCategory != null && !GlobalData.getViewedCategory.isEmpty()) {
                paramsadd.addFormDataPart("viewedcategory", GlobalData.getViewedCategory);
            }
            if (GlobalData.getSSalary != null && !GlobalData.getSSalary.isEmpty()) {
                paramsadd.addFormDataPart("salary", GlobalData.getSSalary);
            }
            if (GlobalData.getJobIDType != null && !GlobalData.getJobIDType.isEmpty()) {
                paramsadd.addFormDataPart("job_type", GlobalData.getJobIDType);
            }
            if (GlobalData.getExperience != null && !GlobalData.getExperience.isEmpty()) {
                paramsadd.addFormDataPart("experience", GlobalData.getExperience);
            }
            if (GlobalData.getGender != null && !GlobalData.getGender.isEmpty()) {
                paramsadd.addFormDataPart("Gender", GlobalData.getGender);
            }
            MultipartBody requestBody = paramsadd.build();
            request = new Request.Builder().url(GlobalData.url + "filterlocation.php").post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                rresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (rresponse != null && !rresponse.contains("connectionFailure")) {
                try {
                    GlobalData.LocationList = new ArrayList<>();
                    JSONObject responseObj = new JSONObject(rresponse);
                    Gson gson = new Gson();
                    GlobalData.LocationList = gson.fromJson(responseObj.getString("locations"),
                            new TypeToken<ArrayList<FilterLocation>>() {
                            }.getType());
                    GlobalData.locationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                    GlobalData.MainlocationCityList = gson.fromJson(responseObj.getString("filterlocations"), new
                            TypeToken<ArrayList<City>>() {
                            }.getType());
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(Homepage.this, getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void getRoleFrolJoblist() {
        GlobalData.joblistfrom = "RL";
        jssearchlistview.setVisibility(View.GONE);
        categoryview.setVisibility(View.VISIBLE);
        joblistview.setVisibility(View.GONE);
       /* GlobalData.jobList = new ArrayList<>();
        if (GlobalData.jobList.size() > 0) {
            myjobsAdapter = new JobList_Adpater(Homepage.this, GlobalData.jobList);
            jobslist.setAdapter(myjobsAdapter);
            jobslist.setSelection(jobindex);
        }*/
        GlobalData.getCRole = null;
        searchjobvalue.setText("");
        if (!isCheckedKeyword) {
            isCheckedKeyword = true;
            getEditKeyword();
        }
        jscategory.setVisibility(View.GONE);
        if (GlobalData.select_role.size() != 0) {
            jscategory.setVisibility(View.VISIBLE);
            homepagec_emptymessage.setVisibility(View.GONE);
            if (GlobalData.select_role.size() != 0) {
                setCategoryList();
                jscategory.setVisibility(View.VISIBLE);
                homepagec_emptymessage.setVisibility(View.GONE);
                mycategoryAdapter = new CategoryList_Adpater(Homepage.this, R.layout.jscategory_row,
                        GlobalData.select_role);
                jscategory.setAdapter(mycategoryAdapter);
                jscategory.setSelection(categoryindex);
                categoryonclick();
            } else {
                homepagec_emptymessage.setVisibility(View.VISIBLE);
                jscategory.setVisibility(View.GONE);
            }
//            mycategoryAdapter = new CategoryList_Adpater(Homepage.this, R.layout.jscategory_row,
//                    GlobalData.select_role);
//            jscategory.setAdapter(mycategoryAdapter);
//            jscategory.setSelection(categoryindex);
            categoryonclick();
        } else {
            jscategory.setVisibility(View.GONE);
            homepagec_emptymessage.setVisibility(View.VISIBLE);
        }
    }

    private void displayLocation() {
        if (googleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                if (latitude == 0.0d || longitude == 0.0d) {
                    Toast.makeText(Homepage.this, "Empty Lat/Lon", Toast.LENGTH_SHORT).show();
                } else {
                    if (new UtilService().isNetworkAvailable(Homepage.this)) {
                        new CityAsyncTask(latitude, longitude).execute();
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(Homepage.this, android.Manifest
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
        } else {
            locationconnected();
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




