package com.jobsearchtry;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Howtouse extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView youTubeView;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private ProgressDialog pg;
    private LinearLayout htu_header, htu_yheader;
    private String getVideo;

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
        setContentView(R.layout.howtouse);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetStaticPages().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton terms_h = (ImageButton) findViewById(R.id.js_r_h);
        terms_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        youTubeView = (YouTubePlayerView) findViewById(R.id.hw_cotentdata);
        htu_header = (LinearLayout) findViewById(R.id.htu_header);
        htu_yheader = (LinearLayout) findViewById(R.id.htu_yheader);
        youTubeView.initialize(GlobalData.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(Howtouse.this, RECOVERY_DIALOG_REQUEST).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored && getVideo != null && !getVideo.isEmpty()) {
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.cueVideo(getVideo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(GlobalData.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.hw_cotentdata);
    }

    private void onbackclick() {
        if (!GlobalData.emp_login_status.equals("No user found")) {
            startActivity(new Intent(Howtouse.this,
                    EmployerDashboard.class));
            finish();
        } else {
            GlobalData.joblistfrom = "RL";
            startActivity(new Intent(Howtouse.this,
                    Homepage.class));
            finish();
        }
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) youTubeView.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            htu_header.setVisibility(View.GONE);
            htu_yheader.setVisibility(View.GONE);
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            htu_header.setVisibility(View.VISIBLE);
            htu_yheader.setVisibility(View.VISIBLE);
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private class GetStaticPages extends AsyncTask<String, String, String> {
        String statisresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Howtouse.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("key", "use").build();
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
                    JSONObject video = new JSONObject(statisresponse);
                    getVideo = video.getString("terms");
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
