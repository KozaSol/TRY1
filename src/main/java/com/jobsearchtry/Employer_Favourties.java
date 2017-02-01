package com.jobsearchtry;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.EmpFavJobList_Adpater;
import com.jobsearchtry.swipe.SwipeMenu;
import com.jobsearchtry.swipe.SwipeMenuCreator;
import com.jobsearchtry.swipe.SwipeMenuItem;
import com.jobsearchtry.swipe.SwipeMenuListView;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.User;

public class Employer_Favourties extends Activity {
    private SwipeMenuListView myfavlist;
    private EmpFavJobList_Adpater myhistoryempAdapter;
    private ArrayList<User> EmpFavHistoryList = new ArrayList<>();
    private ImageView jd_setfav;
    private ProgressDialog pg;
    private TextView emptyfavmsg;
    public static Callback callback;
    private OkHttpClient client = null;
    private String languages;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Employer_Favourties.this, EmployerDashboard.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_favourties);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        myfavlist = (SwipeMenuListView) findViewById(R.id.emp_myfavlist);
        jd_setfav = (ImageView) findViewById(R.id.jd_setfav);
        ImageButton emp_fav_header = (ImageButton) findViewById(R.id.js_r_h);
        emp_fav_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Employer_Favourties.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Employer_Favourties.this, EmployerDashboard.class));
                finish();
            }
        });
        myfavlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @SuppressLint("NewApi")
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.TRANSPARENT));
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                openItem.setWidth(width);
                openItem.setTitleSize(getResources().getDimensionPixelSize(
                        R.dimen.textSizeLarge));
                menu.addMenuItem(openItem);
            }
        };
        myfavlist.setMenuCreator(creator);
        myfavlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                GlobalData.empid = EmpFavHistoryList.get(position).getId();
                Intent in = new Intent(Employer_Favourties.this,
                        JobSeeker_DetailView.class);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                Editor editor = sharedPreferences.edit();
                editor.putString("EMP_ID", GlobalData.empid);
                editor.apply();
                startActivity(in);
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
        emptyfavmsg = (TextView) findViewById(R.id.emp_favemptymsg);
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                emptyfavmsg.setVisibility(View.VISIBLE);
                return false;
            }
        };
    }

    private void confirmDelete(final int id) {
        try {
            GlobalData.empid = EmpFavHistoryList.get(id).getId();
            final Dialog alertDialog = new Dialog(Employer_Favourties.this,R.style.MyThemeDialog);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCanceledOnTouchOutside(false);
            View emppromptView = View.inflate(Employer_Favourties.this,
                    R.layout.delete_popup, null);
            TextView f_popupheader = (TextView) emppromptView
                    .findViewById(R.id.d_popupheader);
            f_popupheader.setText(R.string.deleteconfirm);
            TextView f_popupsubheader = (TextView) emppromptView
                    .findViewById(R.id.d_popup_subheader);
            f_popupsubheader
                    .setText(R.string.empfavdelalert);
            Button no = (Button) emppromptView.findViewById(R.id.d_no);
            Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
            alertDialog.setContentView(emppromptView);
            alertDialog.show();
            yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    new deleteJob(id).execute();
                    alertDialog.dismiss();
                }
            });
            no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    myhistoryempAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
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
            pg = new ProgressDialog(Employer_Favourties.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("action", "remove_fav")
                    .add("employer_id", GlobalData.emp_login_status)
                    .add("jobseeker_id", GlobalData.empid).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "favourite_jobseeker.php")
                    .post(formBody).build();
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
            if (deleteresponse != null
                    && !deleteresponse.contains("connectionFailure")) {
                try {
                    EmpFavHistoryList.remove(id);
                    if (EmpFavHistoryList.size() == 0) {
                        new Handler(Employer_Favourties.callback)
                                .sendEmptyMessage(0);
                    }
                    myhistoryempAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(),
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(Employer_Favourties.this)) {
            new My_FavJobs().execute();
        } else {
            Toast.makeText(Employer_Favourties.this,
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
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
            pg = new ProgressDialog(Employer_Favourties.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("employer_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("action", "list_fav");
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "favourite_jobseeker.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                favresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (favresponse != null
                    && !favresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(favresponse);
                    Gson gson = new Gson();
                    EmpFavHistoryList = gson.fromJson(
                            responseObj.getString("favourite_list"),
                            new TypeToken<ArrayList<User>>() {
                            }.getType());
                    if (EmpFavHistoryList.size() == 0) {
                        emptyfavmsg.setVisibility(View.VISIBLE);
                    } else {
                        emptyfavmsg.setVisibility(View.GONE);
                        myhistoryempAdapter = new EmpFavJobList_Adpater(
                                Employer_Favourties.this, EmpFavHistoryList);
                        myfavlist.setAdapter(myhistoryempAdapter);
                    }
                } catch (Exception e) {
                    Toast.makeText(Employer_Favourties.this,
                            getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Employer_Favourties.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
