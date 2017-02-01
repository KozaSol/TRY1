package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jobsearchtry.services.AutoStartService;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.User;

import android.os.Handler.Callback;

import java.util.ArrayList;

/*Dashboard of employer.From we can search a jobseeker profile,posting a job,jobs posted history
and what are the responses received*/
public class EmployerDashboard extends Activity {
    private DrawerLayout drawerLayout;
    public static Callback callback;
    private String languages;
    //back pressed opens alert dialog and asks the exit the app or not
    @Override
    public void onBackPressed() {
        final Dialog alertDialog = new Dialog(EmployerDashboard.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = getLayoutInflater().inflate(R.layout.delete_popup,
                new LinearLayout(EmployerDashboard.this), false);
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

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_dashboard);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        getKeywordssavedData();
        ImageView emp_searchprofile = (ImageView) findViewById(R.id.employer_searchprofile);
        ImageView responses = (ImageView) findViewById(R.id.emp_responses);
        ImageView postajob = (ImageView) findViewById(R.id.employer_postajob);
        ImageView jobsposted = (ImageView) findViewById(R.id.employer_jobsposted);
        TextView welcomeuser = (TextView) findViewById(R.id.employerdashboard_welcomemsg);
        welcomeuser.setText(GlobalData.empusername);
        TextView changelangtamil = (TextView) findViewById(R.id.emp_changelangtamil);
        if (languages.equalsIgnoreCase("English")) {
            changelangtamil.setVisibility(View.GONE);
        } else {
            changelangtamil.setVisibility(View.VISIBLE);
        }
        //responses pending count
        final TextView empd_resText_count = (TextView) findViewById(R.id.empd_resText_count);
        if (GlobalData.empNotiCount.equalsIgnoreCase("0")) {
            empd_resText_count.setVisibility(View.GONE);
        } else {
            empd_resText_count.setVisibility(View.VISIBLE);
            empd_resText_count.setText(GlobalData.empNotiCount);
        }
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (GlobalData.empNotiCount.equalsIgnoreCase("0")) {
                    empd_resText_count.setVisibility(View.GONE);
                } else {
                    empd_resText_count.setVisibility(View.VISIBLE);
                    empd_resText_count.setText(GlobalData.empNotiCount);
                }
                return false;
            }
        };
        drawerLayout = (DrawerLayout) findViewById(R.id.emphome_drawerLayout);
        ScrollView jsmenu = (ScrollView) findViewById(R.id.emphome_jsmenu);
        jsmenu.requestDisallowInterceptTouchEvent(true);
        //logo clicking same page refresh
        ImageButton emp_dash_h = (ImageButton) findViewById(R.id.js_r_h);
        emp_dash_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        //clicking more, we can go to view my profile,jobs posted,my fav,change password and logout
        ImageButton empdash_moretab = (ImageButton) findViewById(R.id.emp_r_h_menu);
        empdash_moretab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //GlobalData.postpagefrom = "MyProfile";
                //gotoNextPage();
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        ImageView emp_r_h_user = (ImageView) findViewById(R.id.emp_r_h_user);
        emp_r_h_user.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //GlobalData.postpagefrom = "MyProfile";
                //gotoNextPage();
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        //socket started for getting the pending count of responses
        Intent myIntent = new Intent(EmployerDashboard.this,
                AutoStartService.class);
        startService(myIntent);
        //we can view the list of job seeker profile
        emp_searchprofile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.employeeList.size() > 0) {
                    GlobalData.employeeList.clear();
                    GlobalData.employeeList = new ArrayList<User>();
                }
                GlobalData.employerfilterresponse = null;
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(EmployerDashboard.this);
                Editor editor = sharedPreferences.edit();
                editor.putString("SF", GlobalData.employerfilterresponse);
                editor.apply();
                startActivity(new Intent(EmployerDashboard.this,
                        EmployeeListing.class));
            }
        });

        //list of respones which are job seeker applied for our posted jobs
        responses.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        Responses.class));
            }
        });
        //posting a job
        postajob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        PostJob.class));
            }
        });
        //getting the list jobs which are posted by employer(my company)
        jobsposted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        JobsPosted.class));
            }
        });
        LinearLayout myprofile = (LinearLayout) findViewById(R.id.emp_myprofile_lay);
        myprofile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(EmployerDashboard.this,
                        Employer_Profile_Detail.class));
            }
        });
        LinearLayout changelanguages = (LinearLayout) findViewById(R.id.emp_chooselanguages_lay);
        changelanguages.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseLanguages().selectLanguagesPopup(EmployerDashboard.this);
            }
        });
        LinearLayout myfavourites = (LinearLayout) findViewById(R.id.emp_favourities_lay);
        myfavourites.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        Employer_Favourties.class));
            }
        });
        LinearLayout aboutus = (LinearLayout) findViewById(R.id.emp_aboutus_lay);
        aboutus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(EmployerDashboard.this,
                        About_us.class));
            }
        });
        LinearLayout contact = (LinearLayout) findViewById(R.id.emp_contact_lay);
        contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(EmployerDashboard.this,
                        Contactus.class));
            }
        });
        LinearLayout feedback = (LinearLayout) findViewById(R.id.emp_feedback_lay);
        feedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        Feedback.class));
            }
        });
        LinearLayout emp_changepassword_lay = (LinearLayout) findViewById(R.id.emp_changepassword_lay);
        emp_changepassword_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployerDashboard.this,
                        EmpChangePassword.class));
            }
        });
        LinearLayout logout_lay = (LinearLayout) findViewById(R.id.emp_logout_lay);
        final TextView logout = (TextView) findViewById(R.id.emp_logout);
        ImageView logout_image = (ImageView) findViewById(R.id.logout_image);
        if (GlobalData.emp_login_status.equals("No user found")) {
            logout.setText(R.string.login);
            logout_image.setImageResource(R.drawable.mylogin_icon);
        } else {
            logout.setText(R.string.logout);
            logout_image.setImageResource(R.drawable.logout_icon);
        }
        logout_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (logout.getText().toString().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(EmployerDashboard.this,
                            LoginHome.class));
                } else {
                    GlobalData.emp_login_status = "No user found";
                    GlobalData.login_status = "No user found";
                    GlobalData.ftime = 0;
                    // GlobalData.pageback = "Home";
                    GlobalData.joblistfrom = "RL";
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(EmployerDashboard.this);
                    Editor editor = sharedPreferences.edit();
                    editor.putString("ELS", GlobalData.emp_login_status);
                    editor.putString("LS", GlobalData.login_status);
                    // editor.putString("PAGEBACK", GlobalData.pageback);
                    editor.putLong("FTIME", GlobalData.ftime);
                    editor.apply();
                    startActivity(new Intent(EmployerDashboard.this,
                            Homepage.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKeywordssavedData();
    }

    private void getKeywordssavedData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (EmployerDashboard.this);
        //get the user name of employer from session
        GlobalData.empusername = sharedPreferences.getString("EUN",
                GlobalData.empusername);
        GlobalData.getHomeState = sharedPreferences.getString("F_HS", GlobalData.getHomeState);
        GlobalData.ResKeyword = sharedPreferences.getString("RESKEY", GlobalData.ResKeyword);
        GlobalData.SEKeyword = sharedPreferences.getString("SEKEY", GlobalData.SEKeyword);
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences
                (EmployerDashboard.this);
        Editor editor = sharedPreferences1.edit();
        if (GlobalData.ResKeyword != null && !GlobalData.ResKeyword.isEmpty()) {
            GlobalData.ResKeyword = " ";
            editor.putString("RESKEY", GlobalData.ResKeyword);
        }
        if (GlobalData.SEKeyword != null && !GlobalData.SEKeyword.isEmpty()) {
            GlobalData.SEKeyword = " ";
            editor.putString("SEKEY", GlobalData.SEKeyword);
        }
        editor.apply();
    }
}
