package com.jobsearchtry;
/*--Terms & Conditions
Created by : Krishnaveni veeman
Description - Try - Terms & Conditions - terms and conditions of try team--*/

import java.io.IOException;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TermsCondition extends Activity {
    private WebView contentdata;

    private static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private static String pagefrom = null;
    private ProgressDialog pg;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onbackclick();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsconditions);
        pagefrom = getIntent().getStringExtra("Terms");
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetStaticPages().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton terms_h = (ImageButton) findViewById(R.id.js_r_h);
        terms_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        contentdata = (WebView) findViewById(R.id.contentdataweb);
        contentdata.setWebChromeClient(new WebChromeClient());
        final Activity activity = this;
        contentdata.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        contentdata.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError
                    failingUrl) {
            }
        });
        contentdata.setWebChromeClient(new WebChromeClient());
    }

    private void onbackclick() {
        if (pagefrom != null && pagefrom.equalsIgnoreCase("REG")) {
            finish();
        } else if (!GlobalData.emp_login_status.equals("No user found")) {
            startActivity(new Intent(TermsCondition.this,
                    EmployerDashboard.class));
            finish();
        } else {
            GlobalData.joblistfrom = "RL";
            startActivity(new Intent(TermsCondition.this,
                    Homepage.class));
            finish();
        }
    }

    private class GetStaticPages extends AsyncTask<String, String, String> {
        String statisresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(TermsCondition.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("key", "terms").build();
            Request request = new Request.Builder().url(GlobalData.url + "pages.php").post
                    (formBody).build();
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
                    String terms = responseObj.getString("terms");
                    String removehtml = html2text(terms);
                    /*String text = "<html><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/MyriadPro-Cond/.ttf\")}body {font-family: " +
                            "MyFont;src: url(\"file:///android_asset/MyriadPro-Cond/.ttf\");text-size:3;text-align: left;}body{color: #595959;}"
                            + "</style></head>" + "<body>" + removehtml + "</body></html>";
                    contentdata.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);*/
                    String head1 = "<head><style>@font-face {font-family: 'Myfont';" +
                            "src: url('file:///android_asset/MyriadPro-Cond.ttf');}" +
                            "body {font-family: 'Myfont';color: '#666666';text-align: 'justify';font-size: 'medium';}</style></head>";
                    String text = "<html>" + head1
                            + "<body style=\"font-family: Myfont\";\"color: #666666\">" + (removehtml)
                            + "</body></html>";
                    contentdata.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    contentdata.getSettings().setDomStorageEnabled(true);
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
