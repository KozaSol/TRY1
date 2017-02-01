package com.jobsearchtry;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.MyPostedJobList_Adpater;
import com.jobsearchtry.swipe.SwipeMenu;
import com.jobsearchtry.swipe.SwipeMenuCreator;
import com.jobsearchtry.swipe.SwipeMenuItem;
import com.jobsearchtry.swipe.SwipeMenuListView;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*getting the list of jobs which posted by logged in the employer*/
public class JobsPosted extends Activity {
    private ProgressDialog pg;
    private SwipeMenuListView jobspostedlist;
    private TextView emptylistmsg;
    public static Callback callback;
    private MyPostedJobList_Adpater mypostedjobsAdapter;
    private ArrayList<Jobs> jobPostedList = null;
    private OkHttpClient client = null;
    private static int jobindex = 0, getcount = 0;
    private String languages;
    @Override
    public void onBackPressed() {
        startActivity(new Intent(JobsPosted.this, EmployerDashboard.class));
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobsposted);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        //get the company id from session
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        jobspostedlist = (SwipeMenuListView) findViewById(R.id.mypostedjobslist);
        emptylistmsg = (TextView) findViewById(R.id.jobspostedemptymsg);
        ImageButton jp_h = (ImageButton) findViewById(R.id.js_r_h);
        jp_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsPosted.this,
                        EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobsPosted.this, EmployerDashboard.class));
                finish();
            }
        });
        jobspostedlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
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
        jobspostedlist.setMenuCreator(creator);
        //clicking the item, it will go to detail job page(here we can change the status
        // (active/inactive) and view/edit/update the details of job)
        jobspostedlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                GlobalData.ejobid = jobPostedList.get(position).getJob_id();
                Intent in = new Intent(JobsPosted.this, PostedJob_Detail.class);
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                Editor editor = sharedPreferences.edit();
                editor.putString("EJOB_ID", GlobalData.ejobid);
                editor.apply();
                startActivity(in);
            }
        });
        //swipe left/right.if right means confirmation alert dialog opens for delete a selected job
        jobspostedlist
                .setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                    @Override
                    public void onSwipeStart(int position) {
                    }

                    @Override
                    public void onSwipeEnd(int position) {
                        confirmDelete(position);
                    }
                });
        jobspostedlist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (jobPostedList.size() > 0) {
                    int threshold = 1;
                    int count = jobPostedList.size();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (jobspostedlist.getLastVisiblePosition() >= count
                                - threshold) {
                            if (getcount == 1) {
                                String job_id = jobPostedList.get(count - 1).getJob_id();
                                if (new UtilService().isNetworkAvailable(JobsPosted.this)) {
                                    new My_PostedScrollJobs().execute(job_id);
                                } else {
                                    Toast.makeText(JobsPosted.this, getString(R.string.checkconnection), Toast
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

//if no jobs found,empty message shows
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                emptylistmsg.setVisibility(View.VISIBLE);
                return false;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        jobindex = jobspostedlist.getFirstVisiblePosition();
    }

    //delete a select job alert dialog
    private void confirmDelete(final int id) {
        try {
            GlobalData.ejobid = jobPostedList.get(id).getJob_id();
            final Dialog alertDialog = new Dialog(JobsPosted.this,R.style.MyThemeDialog);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            View emppromptView = View.inflate(JobsPosted.this,
                    R.layout.delete_popup, null);
            TextView f_popupheader = (TextView) emppromptView
                    .findViewById(R.id.d_popupheader);
            f_popupheader.setText(R.string.deleteconfirm);
            TextView f_popupsubheader = (TextView) emppromptView
                    .findViewById(R.id.d_popup_subheader);
            f_popupsubheader
                    .setText(R.string.deletejobsposted);
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
                    mypostedjobsAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
        } catch (Exception ignored) {
        }
    }

    //removed a job from database
    private class deleteJob extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteJob(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(JobsPosted.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("employer_id", GlobalData.emp_login_status)
                    .add("job_id", GlobalData.ejobid).build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "delete_job.php").post(formBody)
                    .build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
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
                    jobPostedList.remove(id);
                    if (jobPostedList.size() == 0) {
                        new Handler(JobsPosted.callback).sendEmptyMessage(0);
                    }
                    mypostedjobsAdapter.notifyDataSetChanged();
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

    //get the list of posted jobs by logged in employer
    @Override
    protected void onResume() {
        super.onResume();
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new My_PostedJobs().execute();
        } else {
            Toast.makeText(JobsPosted.this,
                    getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void isVerifyListAvailable() {
        //if the list size ==0 show the empty message,else hidden
        if (jobPostedList.size() == 0) {
            emptylistmsg.setVisibility(View.VISIBLE);
        } else {
            emptylistmsg.setVisibility(View.GONE);
            int currentPosition = jobspostedlist.getFirstVisiblePosition();
            mypostedjobsAdapter = new MyPostedJobList_Adpater(
                    JobsPosted.this, jobPostedList);
            jobspostedlist.setAdapter(mypostedjobsAdapter);
            jobspostedlist.setSelection(jobindex);
            jobspostedlist.setSelectionFromTop(currentPosition, 0);
        }
    }

    private class My_PostedJobs extends AsyncTask<String, String, String> {
        String pjresponse, jobid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(JobsPosted.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_job_listing.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                pjresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (pjresponse != null && !pjresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(pjresponse);
                    Gson gson = new Gson();
                    getcount = responseObj.getInt("count");
                    jobPostedList = new ArrayList<>();
                    jobPostedList = gson.fromJson(
                            responseObj.getString("tasks"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    //if the list size ==0 show the empty message,else hidden
                    isVerifyListAvailable();
                } catch (Exception e) {
                    Toast.makeText(JobsPosted.this, getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JobsPosted.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class My_PostedScrollJobs extends AsyncTask<String, String, String> {
        String pjresponse, jobid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(JobsPosted.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("company_id", GlobalData.emp_login_status);
            paramsadd.addFormDataPart("job_id", args[0]);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "employer_job_listing.php")
                    .post(requestBody).build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                pjresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (pjresponse != null && !pjresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(pjresponse);
                    Gson gson = new Gson();
                    getcount = responseObj.getInt("count");
                    ArrayList<Jobs> jobpostedlisttemp = gson.fromJson(
                            responseObj.getString("tasks"),
                            new TypeToken<ArrayList<Jobs>>() {
                            }.getType());
                    jobPostedList.addAll(jobpostedlisttemp);
                    //if the list size ==0 show the empty message,else hidden
                    isVerifyListAvailable();
                } catch (Exception e) {
                    Toast.makeText(JobsPosted.this, getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(JobsPosted.this,
                        getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
