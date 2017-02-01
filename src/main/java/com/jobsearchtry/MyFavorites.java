package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.MyFavJobList_Adpater;
import com.jobsearchtry.swipe.SwipeMenu;
import com.jobsearchtry.swipe.SwipeMenuCreator;
import com.jobsearchtry.swipe.SwipeMenuItem;
import com.jobsearchtry.swipe.SwipeMenuListView;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFavorites extends Activity {
    private SwipeMenuListView myfavlist;
    private MyFavJobList_Adpater myhistoryjobsAdapter;
    private ArrayList<Jobs> jobHistoryList = null;
    private ProgressDialog pg;
    private TextView emptyfavmsg;
    public static Callback callback;
    private OkHttpClient client = null;
    private String languages;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfavourities);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        //getting the list of job seeker favorites jobs
        if (new UtilService().isNetworkAvailable(MyFavorites.this)) {
            new My_FavJobs().execute();
        } else {
            Toast.makeText(MyFavorites.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        myfavlist = (SwipeMenuListView) findViewById(R.id.myfavlist);
        emptyfavmsg = (TextView) findViewById(R.id.favemptymsg);
        ImageButton myfav_h = (ImageButton) findViewById(R.id.js_r_h);
        myfav_h.setOnClickListener(new OnClickListener() {
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
        myfavlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.TRANSPARENT));
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                openItem.setWidth(width);
                openItem.setTitleSize(getResources().getDimensionPixelSize(R.dimen.textSizeLarge));
                menu.addMenuItem(openItem);
            }
        };
        myfavlist.setMenuCreator(creator);
        myfavlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalData.jobid = jobHistoryList.get(position).getJob_id();
                String jobstatus = jobHistoryList.get(position).getJobtoshowflag();
                if (jobstatus.equalsIgnoreCase("3")) {
                    Toast.makeText(getBaseContext(), getString(R.string.jobhasexpired), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    GlobalData.favpagefrom = "Fav";
                    GlobalData.pageback = "Home";
                    Intent in = new Intent(MyFavorites.this, JobSeeker_Detail.class);
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    Editor editor = sharedPreferences.edit();
                    editor.putString("JOB_ID", GlobalData.jobid);
                    editor.putString("PAGEBACK", GlobalData.pageback);
                    editor.apply();
                    startActivity(in);

                }
            }
        });

        myfavlist.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
            }

            @Override
            public void onSwipeEnd(int position) {
                confirmDelete(position);
            }
        });
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                emptyfavmsg.setVisibility(View.VISIBLE);
                return false;
            }
        };
    }

    private void onbackclick() {
        GlobalData.joblistfrom = "RL";
        startActivity(new Intent(MyFavorites.this, Homepage.class));
        finish();
    }

    private void confirmDelete(final int id) {
        try {
            GlobalData.jobid = jobHistoryList.get(id).getJob_id();
            final Dialog alertDialog = new Dialog(MyFavorites.this,R.style.MyThemeDialog);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            View emppromptView = View.inflate(MyFavorites.this, R.layout.delete_popup, null);
            TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.d_popupheader);
            f_popupheader.setText(R.string.deleteconfirm);
            TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                    .d_popup_subheader);
            f_popupsubheader.setText(R.string.jobdeletealert);
            Button no = (Button) emppromptView.findViewById(R.id.d_no);
            Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
            alertDialog.setContentView(emppromptView);
            alertDialog.show();
            yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (new UtilService().isNetworkAvailable(MyFavorites.this)) {
                        new deleteJob(id).execute();
                    } else {
                        Toast.makeText(MyFavorites.this, getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }
            });
            no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                    myhistoryjobsAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception ignored) {
        }
    }

    private class deleteJob extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteJob(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(MyFavorites.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("job_id", GlobalData.jobid)
                    .add("jobseeker_id", GlobalData.login_status).build();
            Request request = new Request.Builder().url(GlobalData.url + "forgot_fav.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                deleteresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (deleteresponse != null && !deleteresponse.contains("connectionFailure")) {
                try {
                    jobHistoryList.remove(id);
                    if (jobHistoryList.size() == 0) {
                        new Handler(MyFavorites.callback).sendEmptyMessage(0);
                    }
                    myhistoryjobsAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(MyFavorites.this)) {
            new My_FavJobs().execute();
        } else {
            Toast.makeText(MyFavorites.this, getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private class My_FavJobs extends AsyncTask<String, String, String> {
        String favresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(MyFavorites.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "list_favorite_jobs.php")
                    .post(requestBody)
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                favresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (favresponse != null && !favresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(favresponse);
                    Gson gson = new Gson();
                    jobHistoryList = new ArrayList<>();
                    jobHistoryList = gson.fromJson(responseObj.getString("favourite_list"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    if (jobHistoryList.size() == 0) {
                        emptyfavmsg.setVisibility(View.VISIBLE);
                    } else {
                        emptyfavmsg.setVisibility(View.GONE);
                        myhistoryjobsAdapter = new MyFavJobList_Adpater(MyFavorites.this,
                                jobHistoryList);
                        myfavlist.setAdapter(myhistoryjobsAdapter);
                    }
                } catch (Exception e) {
                    Toast.makeText(MyFavorites.this, getString(R.string.errortoparse), Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(MyFavorites.this, getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }
}
