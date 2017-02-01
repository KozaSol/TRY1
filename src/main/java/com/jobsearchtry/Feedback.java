package com.jobsearchtry;
/*--Feedback Page
Created by : Krishnaveni veeman
Description - Try - Feedback - user interest to give feedback to try team--*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.FeedbackType;

import android.view.View.OnClickListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

public class Feedback extends Activity {
    private EditText name, email, phoneno;
    private String getName, getEmail, getPhoneno, languages, getFeedback, getmessage;
    private Button feedbackType;
    private EditText message;
    private LinearLayout feedback_lay;
    private ProgressDialog pg;
    private int indexfdtype = -1;
    private ArrayList<FeedbackType> select_feedbacklist = null;
    private ArrayAdapter<FeedbackType> feedbackadapter;
    private ImageButton exit;
    private JSONObject json;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((getApplicationContext()));
        GlobalData.emp_login_status = sharedPreferences.getString("ELS",
                GlobalData.emp_login_status);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetFeedbackType().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        getFeedback = getString(R.string.selecttype);
        message = (EditText) findViewById(R.id.fd_message);
        feedbackType = (Button) findViewById(R.id.feedback_type);
        //select feedback type
        feedback_lay = (LinearLayout) findViewById(R.id.feedback_lay);
        feedback_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_feedbacklist != null && select_feedbacklist.size() > 0) {
                    FeedbackAlert();
                }
            }
        });
        Button btnsubmit = (Button) findViewById(R.id.feedback_submit);
        name = (EditText) findViewById(R.id.fd_Name);
        email = (EditText) findViewById(R.id.fd_Email);
        phoneno = (EditText) findViewById(R.id.fd_phoneNo);
        //logo clicking go to landing page
        ImageButton js_feedback = (ImageButton) findViewById(R.id.js_r_h);
        js_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onbackclick();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        //back clicking go to landing page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        //submit button clicking
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validation success
                if (feedbackValidation()) {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new feedbackReg().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //select feedback type from feedback form
    private void FeedbackAlert() {
        final Dialog alertDialog = new Dialog(Feedback.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(Feedback.this, R.layout.spinner, null);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selecttype);
        Button typedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterfeedback = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getFeedback != null && !getFeedback.isEmpty() && !getFeedback.equalsIgnoreCase(getString(R.string.selecttype))) {
            indexfdtype = -1;
            if (select_feedbacklist != null && select_feedbacklist.size() > 0) {
                FeedbackType tempfeedback = new FeedbackType();
                tempfeedback.setFeedback(getFeedback);
                indexfdtype = select_feedbacklist.indexOf(tempfeedback);
            }
        } else {
            indexfdtype = -1;
        }
        final ArrayAdapter<FeedbackType> adapter = new ArrayAdapter<FeedbackType>(this, R.layout.spinner_item_text, select_feedbacklist) {
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
                String yourValue = select_feedbacklist.get(position).getFeedback();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = select_feedbacklist.get(position).getFeedback_local();
                }
                if (indexfdtype != -1 && (indexfdtype == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterfeedback.setAdapter(adapter);
        filterfeedback.setSelection(indexfdtype);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFeedbackType();
                alertDialog.dismiss();
            }
        });
        filterfeedback.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexfdtype != -1 && (indexfdtype == position)) {
                    getFeedback = getString(R.string.selecttype);
                    indexfdtype = -1;
                } else {
                    indexfdtype = position;
                    getFeedback = select_feedbacklist.get(indexfdtype).getFeedback();
                }
                setFeedbackType();
                adapter.notifyDataSetChanged();
            }
        });
        typedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFeedbackType();
                alertDialog.dismiss();
            }
        });
    }

    private void setFeedbackType() {
        if (!getFeedback.equalsIgnoreCase(getString(R.string.selecttype))) {
            feedbackType.setText(getFeedback);
            if (!languages.equalsIgnoreCase("English")) {
                setFeedbackLocalLang();
            }
        } else {
            feedbackType.setText(getString(R.string.selecttype));
        }
    }

    private void setFeedbackLocalLang() {
        FeedbackType localfeedback = new FeedbackType();
        localfeedback.setFeedback(getFeedback);
        indexfdtype = select_feedbacklist.indexOf(localfeedback);
        String FeedbackLocal = select_feedbacklist.get(indexfdtype).getFeedback_local();
        feedbackType.setText(FeedbackLocal);
    }

    //check whether it is logged in from employer or job seeker
    private void onbackclick() {
        if (name.getText().toString().length() != 0 ||
                email.getText().toString().length() != 0 ||
                phoneno.getText().toString().length() != 0 ||
                !getFeedback.equalsIgnoreCase(getString(R.string.selecttype)) || message.getText().toString().length() != 0) {
            if (new BackAlertDialog().isBackDialog(Feedback.this)) {
                goback();
            }
        } else {
            goback();
        }
    }

    private void goback() {
        //employer
        if (!GlobalData.emp_login_status.equals("No user found")) {
            startActivity(new Intent(Feedback.this,
                    EmployerDashboard.class));
            finish();
        } else {
            //job seeker
            GlobalData.joblistfrom = "RL";
            startActivity(new Intent(Feedback.this,
                    Homepage.class));
            finish();
        }
    }

    //feedback form validation
    private boolean feedbackValidation() {
        getName = name.getText().toString();
        getEmail = email.getText().toString();
        getPhoneno = phoneno.getText().toString();
        getmessage = message.getText().toString();
        if (null == getName || getName.length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.youmusthave3mincontactperson),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == getEmail || checkEmail(getEmail)) {
            Toast.makeText(getApplicationContext(), getString(R.string.pleaseentervalidemail),
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (null == getPhoneno || getPhoneno.length() != 10) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pleaseenteravalidphno), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (getFeedback.equalsIgnoreCase(getString(R.string.selecttype))) {
            Toast.makeText(getApplicationContext(), getString(R.string.selecttype),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (null == getmessage || getmessage.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.messageshouldnotbeempty), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        return !GlobalData.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private class feedbackReg extends AsyncTask<String, String, String> {
        String fdresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Feedback.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestBody formBody = new FormBody.Builder().add("Name", getName).add("EmailId",
                    getEmail)
                    .add("Phone", getPhoneno).add("FeedType", getFeedback)
                    .add("Message", getmessage).build();
            Request request = new Request.Builder().url(
                    GlobalData.url + "Feedback_sendEmail.php").post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                fdresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            try {
                final Dialog alertDialog = new Dialog(Feedback.this,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                View promptView = View.inflate(Feedback.this, R.layout.contactus_popup,
                        null);
                Button invalid_login_submit = (Button) promptView.findViewById(R.id
                        .contactus_popup);
                ImageButton exit = (ImageButton) promptView.findViewById(R.id.exit_alert);
                TextView popup_message = (TextView) promptView.findViewById(R.id.popup_message);
                popup_message.setText(getString(R.string.contact_mes));
                alertDialog.setContentView(promptView);
                invalid_login_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (!GlobalData.emp_login_status.equals("No user found")) {
                            startActivity(new Intent(Feedback.this,
                                    EmployerDashboard.class));
                            finish();
                        } else {
                            //job seeker
                            GlobalData.joblistfrom = "RL";
                            startActivity(new Intent(Feedback.this,
                                    Homepage.class));
                            finish();
                        }
                    }
                });
                alertDialog.show();
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetFeedbackType extends AsyncTask<String, String, String> {
        String staticlanguage;
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(Feedback.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                if (!languages.equalsIgnoreCase("English")) {
                    MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                            .FORM);
                    paramsadd.addFormDataPart("languages", languages);
                    MultipartBody requestBody = paramsadd.build();
                    request = new Request.Builder().url(GlobalData.url + "getfeedbacklist.php").post
                            (requestBody).build();
                } else {
                    request = new Request.Builder().url(
                            GlobalData.url + "getfeedbacklist.php").build();
                }
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                staticlanguage = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (staticlanguage != null && !staticlanguage.isEmpty()) {
                try {
                    json = new JSONObject(staticlanguage);
                    select_feedbacklist = new ArrayList<>();
                    select_feedbacklist = gson.fromJson(json.getString("feedbacklist"), new
                            TypeToken<ArrayList<FeedbackType>>() {
                            }.getType());
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getString(R.string.errortoparse),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.connectionfailure),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
