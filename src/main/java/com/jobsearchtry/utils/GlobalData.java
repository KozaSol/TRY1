package com.jobsearchtry.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jobsearchtry.wrapper.City;
import com.jobsearchtry.wrapper.Employer;
import com.jobsearchtry.wrapper.FilterLocation;
import com.jobsearchtry.wrapper.JS_CATEGORY;
import com.jobsearchtry.wrapper.Jobs;
import com.jobsearchtry.wrapper.Skill;
import com.jobsearchtry.wrapper.SkillCategory;
import com.jobsearchtry.wrapper.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class GlobalData {
    /*emp_login_status - company id of employer app
    login_status - user id of job seeker
    empusername - company name of employer app
    frompostjob - back page identify from Jobs Posted and Edit Job(PostJob - final submit detail)
    empNotiCount - employer getting a total no of responses count
    Respones.java,Login - PostJob)
    getRemCompEmail - collecting the employer email address for autocomplete
     getRemJSPhone - collecting the job seeker phone numbers for autocomplete
     getjsfilterdata - collection the list of location,industry,role,.. for the all fields of jobsearch filter page
     isspkeywordsavail - search profile getting the list of keywords availability flag
     getdefaultLocationHome - location is already enabled or not enable*/
    public static final String DEVELOPER_KEY = "AIzaSyDut4FsYS7kQpwizVsd33qhy5JTI3DqMF8";
    // YouTube video id
    public static String emp_login_status = "No user found", LandRefresh = "Home",
            login_status = "No user found", mobilenumber, getRoleID,
            getroleresponse = null, loginfrom = "Jobseekar",
            skillid, username, empusername, jobid, ejobid,
            empid, applicantid = null, educationid, employmentid,
            jobdetailresponse, personalresponse = null, company_email,
            pagefrom = "Home", jobregid = null, resume, getyesnoflag = "Yes",
            applicantjobid, favpagefrom = "Home", getCount = "0",
            empNotiCount = "0", company_id, jobseeker_id, getLanguageFlag = "No",
            getLanguageName = "English", frompostjob = "PostJob", pageback = "Home", fromlogin = null,
            responsesfilterresponse, getHomeState,
            getrolepage = "Home", joblistfrom = "RL",
            Landingkeyword = null, SEKeyword = null, ResKeyword, getLocation = null,
            getNearLocation = null, getFinalLocation = null, getPrevLocation = null,
            getRole, getCRole, getJobType, getJobIDType,
            getIndustry, getSSalary, getGender, getExperience,
            employerfilterresponse = null, changenetwork = "No",
            getSkill, getViewedCategory = null, getViewCateCount = null, getViewedJob =
            null, findPage = "Home",
            getRemCompEmail, getRemJSPhone, getSearchKeyword, getSearchKeywords, getjsfilterdata = null, islocationAvail = "No",
            isspkeywordsavail = "No", getProfileSearchKeywords, getResponseSearchKeywords;
    public static User user;
    public static Employer employer;
    //call functionality time
    public static final String[] select_fromm = {"1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM",
            "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM",
            "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM",
            "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM", "12:00 " +
            "AM"};
    //email pattern
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    public static boolean getdefaultLocation = true,
            getdefaultLocationHome = true;
    public static long ftime = 0;
    public static Jobs jobdetail;
    public static Date lastNotificationDateEmp;
    public static Date lastNotificationDateJob;
    public static ArrayList<JS_CATEGORY> select_role = new ArrayList<>();
    public static final String url = "http://136.243.155.14/~tryntrytest/try/";
    public static final String imageurl = "http://136.243.155.14/~tryntrytest";
    //  public static final String url = "http://202.66.172.21/~tryntrytest/try/";
    // public static final String imageurl = "http://202.66.172.21/~tryntrytest";
    public static final String SENDER_ID = "637196643930";
    public static ArrayList<Jobs> jobList = new ArrayList<>();
    public static ArrayList<Jobs> roleJobList = new ArrayList<>();
    //public static ArrayList<Jobs> alljobList = new ArrayList<>();
    public static ArrayList<User> employeeList = new ArrayList<>();
    public static ArrayList<User> responsesList = new ArrayList<>();
    public static final ArrayList<String> getrolename = new ArrayList<>();
    public static final ArrayList<Integer> getrolecount = new ArrayList<>();
    public static final ArrayList<String> getjobid = new ArrayList<>();
    public static final ArrayList<String> jobSuggestions = new ArrayList<>();
    public static ArrayList<String> suggestionsList, MainSuggestionList = new ArrayList<>(), profilesuggestionList,
            profilemainsuggestionList, profilesuggestion = new ArrayList<>(),
            responsesuggestion = new ArrayList<>(),
            responsesuggestionList;
    //get company email arraylist
    public static final ArrayList<String> getremcompanyemail = new ArrayList<>();
    //get job seeker phone arraylist
    public static final ArrayList<String> getremjsphone = new ArrayList<>();
    private SharedPreferences settings = null;
    public static ArrayList<FilterLocation> LocationList = new ArrayList<>();
    public static ArrayList<City> locationCityList = new ArrayList<>();
    public static ArrayList<City> MainlocationCityList = new ArrayList<>();
    public static ArrayList<City> CityList = new ArrayList<>();
    static final String DISPLAY_MESSAGE_ACTION = "com.jobsearchtry.DISPLAY_MESSAGE";
    public static final String SERVER_URL = url + "Login_logout_status.php";
    public static final String TAG = "Try GCM";
    public static ArrayList<Skill> skillEditList = new ArrayList<>();
    public static ArrayList<SkillCategory> skillCategoryList = new ArrayList<>();
    public static final String[] select_gender = {"Female", "Male", "Transgender"};

    public static void displayMessage(Context paramContext, String paramString) {
        Intent localIntent = new Intent("com.jobsearchtry.DISPLAY_MESSAGE");
        localIntent.putExtra("message", paramString);
        paramContext.sendBroadcast(localIntent);
    }

    public GlobalData(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getPreferenceString(String prefUserid, String username, String getLanguageFlag, String getyesnoflag, String changenetwork,
                                      String getLanguageName, String mobileno, String getLocation, String getNearLocation, String getResultID, String getFinalLocation, String getroleresponse,
                                      String empuserid, String empusername, String empemail,
                                      String joblistfrom, String pageback, String
                                              getViewedCategory, String getViewCateCount, Boolean getdefaultLocationHome) {
        settings.getString("LS", GlobalData.login_status);
        settings.getString("NAME", GlobalData.username);
        settings.getString("LANGFLAG", GlobalData.getLanguageFlag);
        settings.getString("YNFLAG", GlobalData.getyesnoflag);
        settings.getString("CHNET", GlobalData.changenetwork);
        settings.getString("LANGNAME", GlobalData.getLanguageName);
        settings.getString("MN", GlobalData.mobilenumber);
        settings.getString("F_L", GlobalData.getLocation);
        settings.getString("F_NL", GlobalData.getNearLocation);
        settings.getString("F_FL", GlobalData.getFinalLocation);
        settings.getString("CATEGORY", GlobalData.getroleresponse);
        settings.getString("ELS", GlobalData.emp_login_status);
        settings.getString("EUN", GlobalData.empusername);
        settings.getString("EEMAIL", GlobalData.company_email);
        settings.getString("JobListFrom", GlobalData.joblistfrom);
        settings.getString("PAGEBACK", GlobalData.pageback);
        settings.getString("VCATE", GlobalData.getViewedCategory);
        settings.getString("VCATEC", GlobalData.getViewCateCount);
        settings.getBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        return null;
    }

    public void setPreferenceString(String prefUserid, String username, String getLanguageFlag, String getyesnoflag, String changenetwork,
                                    String getLanguageName, String mobileno, String getLocation, String getNearLocation, String getResultID,
                                    String getFinalLocation, String getroleresponse,
                                    String empuserid, String empusername, String empemail,
                                    String joblistfrom, String pageback, String getViewedCategory,
                                    String getViewCateCount, Boolean getdefaultLocationHome) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LS", GlobalData.login_status);
        editor.putString("NAME", GlobalData.username);
        editor.putString("LANGFLAG", GlobalData.getLanguageFlag);
        editor.putString("YNFLAG", GlobalData.getyesnoflag);
        editor.putString("CHNET", GlobalData.changenetwork);
        editor.putString("LANGNAME", GlobalData.getLanguageName);
        editor.putString("MN", GlobalData.mobilenumber);
        editor.putString("F_L", GlobalData.getLocation);
        editor.putString("F_NL", GlobalData.getNearLocation);
        editor.putString("F_FL", GlobalData.getFinalLocation);
        editor.putString("CATEGORY", GlobalData.getroleresponse);
        editor.putString("ELS", GlobalData.emp_login_status);
        editor.putString("EUN", GlobalData.empusername);
        editor.putString("EEMAIL", GlobalData.company_email);
        editor.putString("JobListFrom", GlobalData.joblistfrom);
        editor.putString("PAGEBACK", GlobalData.pageback);
        editor.putString("VCATE", GlobalData.getViewedCategory);
        editor.putString("VCATEC", GlobalData.getViewCateCount);
        editor.putBoolean("JSLOC", GlobalData.getdefaultLocationHome);
        editor.apply();
    }
}
