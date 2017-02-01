package com.jobsearchtry;
/*--About Us Page
Created by : Krishnaveni veeman
Description - Try - About us - user want to know the try team--*/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class About_us extends Activity {
    private WebView abouttry, mission, vision, tryteam, fouderimg, founder2, founder2img;
    private TextView advisor, foundername, founderpost, profname, profpost,
            mdname, mdpost, entrpname, entrppost, nameoffounder, foundesg,
            founder2name, founder2post, architectname, architectpost;
    private ProgressDialog pg;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        //get the about us details from web services
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetStaticPages().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        abouttry = (WebView) findViewById(R.id.abouttry_details);
        mission = (WebView) findViewById(R.id.mission_details);
        vision = (WebView) findViewById(R.id.vision_details);
        tryteam = (WebView) findViewById(R.id.tryteam_details);
        fouderimg = (WebView) findViewById(R.id.founder_img);
        nameoffounder = (TextView) findViewById(R.id.foundername);
        founder2 = (WebView) findViewById(R.id.tryteam2_details);
        founder2img = (WebView) findViewById(R.id.founder2_img);
        founder2name = (TextView) findViewById(R.id.founder2name);
        founder2post = (TextView) findViewById(R.id.founder2post);
        foundesg = (TextView) findViewById(R.id.founder);
        advisor = (TextView) findViewById(R.id.Advi_comm);
        foundername = (TextView) findViewById(R.id.fmd_name);
        founderpost = (TextView) findViewById(R.id.fmd_desg);
        profname = (TextView) findViewById(R.id.prof_name);
        profpost = (TextView) findViewById(R.id.prof_deg);
        mdname = (TextView) findViewById(R.id.md_name);
        mdpost = (TextView) findViewById(R.id.md_desg);
        entrpname = (TextView) findViewById(R.id.entr_name);
        entrppost = (TextView) findViewById(R.id.entrp);
        architectname = (TextView) findViewById(R.id.arct_name);
        architectpost = (TextView) findViewById(R.id.architect);
        //header logo onclicking -go to landing page
        ImageButton js_contact = (ImageButton) findViewById(R.id.js_r_h);
        js_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        //header back onclicking -go to landing page
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
    }

    private void onbackclick() {
        //check whether it is job seeker or employer
        if (!GlobalData.emp_login_status.equals("No user found")) {
            //employer
            startActivity(new Intent(About_us.this,
                    EmployerDashboard.class));
            finish();
        } else {
            //job seeker
            GlobalData.joblistfrom = "RL";
            startActivity(new Intent(About_us.this,
                    Homepage.class));
            finish();
        }
    }

    private class GetStaticPages extends AsyncTask<String, String, String> {
        String statisresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(About_us.this,R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            Request request = new Request.Builder().url(GlobalData.url + "aboutus.php").build();
            OkHttpClient client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                statisresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (statisresponse != null && !statisresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(statisresponse);
                    String abouttryy = responseObj.getString("about_try");
                    String missiondetails = responseObj.getString("mission");
                    String visiontry = responseObj.getString("vision");
                    String found_img = responseObj.getString("try_teamimg");
                    String tryteamtry = responseObj.getString("try_team");
                    String tryteamtry2 = responseObj.getString("try_team2");
                    String found2_img = responseObj.getString("try_teamimg2");

                    String head1 = "<head><style>@font-face {font-family: 'Myfont';" +
                            "src: url('file:///android_asset/MyriadPro-Cond.ttf');}" +
                            "body {font-family: 'Myfont';color: '#666666';text-align: 'justify';font-size: 'medium';}</style></head>";
                    String aboutdetail = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (abouttryy)
                            + "</body></html>";
                    String missiond = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (missiondetails)
                            + "</body></html>";
                    String visiondetail = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (visiontry)
                            + "</body></html>";
                    String tryteamdeatl1 = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (tryteamtry)
                            + "</body></html>";
                    String tryteamdeatl2 = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (tryteamtry2)
                            + "</body></html>";
                    String img_founder = "<html><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url" +
                            "(\"file:///android_asset/MyriadPro-Cond.ttf\")}body {margin: 0px; padding: 0px;}"
                            + "</style></head>" + "<body>" + (found_img) + "</body></html>";
                    String img_founder2 = "<html><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url" +
                            "(\"file:///android_asset/MyriadPro-Cond.ttf\")}body {margin: 0px; padding: 0px;}"
                            + "</style></head>" + "<body>" + (found2_img) + "</body></html>";
                    abouttry.loadDataWithBaseURL(null, aboutdetail, "text/html", "utf-8", null);
                    mission.loadDataWithBaseURL(null, missiond, "text/html", "utf-8", null);
                    vision.loadDataWithBaseURL(null, visiondetail, "text/html", "utf-8", null);
                    fouderimg.loadDataWithBaseURL(null, img_founder, "text/html", "utf-8", null);
                    tryteam.loadDataWithBaseURL(null, tryteamdeatl1, "text/html", "utf-8", null);
                    founder2.loadDataWithBaseURL(null, tryteamdeatl2, "text/html", "utf-8", null);
                    founder2img.loadDataWithBaseURL(null, img_founder2, "text/html", "utf-8", null);
                    founder2name.setText(responseObj.getString("try_teamname2"));
                    founder2post.setText(responseObj.getString("try_teampost2"));
                    nameoffounder.setText(responseObj.getString("try_teamname"));
                    foundesg.setText(responseObj.getString("try_teampost"));
                    advisor.setText(responseObj.getString("commite_name"));
                    foundername.setText(responseObj.getString("md_name"));
                    founderpost.setText(responseObj.getString("md_post"));
                    profname.setText(responseObj.getString("prof_name"));
                    profpost.setText(responseObj.getString("prof_post"));
                    mdname.setText(responseObj.getString("cabmd_name"));
                    mdpost.setText(responseObj.getString("cabmd_post"));
                    entrpname.setText(responseObj.getString("entrp_name"));
                    entrppost.setText(responseObj.getString("entrp_post"));
                    architectname.setText(responseObj.getString("arct_name"));
                    architectpost.setText(responseObj.getString("arct_post"));
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
}

