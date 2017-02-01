package com.jobsearchtry;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.adapter.SkillAdapter;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Skill;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfile_ADD_Skill extends Activity {
    private Button skill, skillexp;
    private String getSkill, getExp, languages;
    private ProgressDialog pg;
    private static final String[] select_exp_years = {"<1 Yr", "1 Yr", "2 Yrs", "3 Yrs", "4 Yrs",
            "5 Yrs", "6 Yrs",
            "7 Yrs", "8 Yrs", "9 Yrs", "10 Yrs", "11 Yrs", "12 Yrs", "13 Yrs", "14 Yrs", "15 Yrs",
            "16 Yrs", "17 Yrs",
            "18 Yrs", "19 Yrs", "20 Yrs", "21 Yrs", "22 Yrs", "23 Yrs", "24 Yrs", ">25 Yrs"};
    private OkHttpClient client = null;
    private LinearLayout js_add_skill_lay, js_add_exp_lay;
    private ArrayList<Skill> skillList = null;
    private int indexskill = -1, indexexp = -1;

    @Override
    public void onBackPressed() {
        onbackclick();
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_add);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        getSkill = getString(R.string.selectskill);
        getExp = getString(R.string.selectexp);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            if (!GlobalData.getRoleID.isEmpty() && GlobalData.getRoleID != null) {
                new getSkills().execute();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                        .LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        js_add_skill_lay = (LinearLayout) findViewById(R.id.js_add_skill_lay);
        js_add_exp_lay = (LinearLayout) findViewById(R.id.js_add_exp_lay);
        //skill click and get the selected skill
        js_add_skill_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skillList != null && skillList.size() > 0) {
                    SkillAlert();
                }
            }
        });
        //exp click and get the selected experience
        js_add_exp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperienceAlert();
            }
        });
        ImageButton skill_add_h = (ImageButton) findViewById(R.id.js_r_h);
        skill_add_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_ADD_Skill.this, Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackclick();
            }
        });
        skill = (Button) findViewById(R.id.js_add_skill);
        skillexp = (Button) findViewById(R.id.js_add_exp);
        Button submit = (Button) findViewById(R.id.js_try_Skill_Add_Submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyAddSkill()) {
                    new addSkill().execute();
                }
            }
        });
    }

    private void onbackclick() {
        if (!getSkill.equalsIgnoreCase(getString(R.string.selectskill)) ||
                !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            new BackAlertDialog().isBackDialog(MyProfile_ADD_Skill.this);
        } else {
            finish();
        }
    }

    private boolean verifyAddSkill() {
        if (getSkill.equalsIgnoreCase(getString(R.string.selectskill))) {
            Toast.makeText(getApplicationContext(), getString(R.string.skillvalidation), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            Toast.makeText(getApplicationContext(), getString(R.string.expvalidation), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (!new UtilService().isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class addSkill extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(MyProfile_ADD_Skill.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("skill_name", getSkill);
            paramsadd.addFormDataPart("experience", getExp);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "skills_save.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                randomkeysendresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (randomkeysendresponse != null && !randomkeysendresponse.contains
                    ("connectionFailure")) {
                try {
                    JSONObject addskillstatus = new JSONObject(randomkeysendresponse);
                    String getStatus = addskillstatus.getString("message");
                    int getVerify = addskillstatus.getInt("status");
                    Toast.makeText(getBaseContext(), getStatus, Toast.LENGTH_SHORT).show();
                    if (getVerify == 1) {
                        finish();
                    }
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

    //select skill from jobseeker add skill page
    private void SkillAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Skill.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Skill.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectskill);
        Button skilldone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterskill = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSkill != null && !getSkill.isEmpty() && !getSkill.equalsIgnoreCase(getString(R.string.selectskill))) {
            indexskill = -1;
            for (int i = 0; i < skillList.size(); i++) {
                if (skillList.get(i).getSkill_name().equals(getSkill)) {
                    indexskill = i;
                }
            }
        } else {
            indexskill = -1;
        }
        final SkillAdapter adapter = new SkillAdapter(MyProfile_ADD_Skill.this, R.layout.spinner_item_text, skillList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = MyProfile_ADD_Skill.this;
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.spinner_item_text, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.spinneritemqualification);
                String yourValue = skillList.get(position).getSkill_name();
                if (indexskill != -1 && (indexskill == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterskill.setAdapter(adapter);
        filterskill.setSelection(indexskill);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSkill();
                alertDialog.dismiss();
            }
        });
        filterskill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexskill != -1 && (indexskill == position)) {
                    getSkill = getString(R.string.selectskill);
                    indexskill = -1;
                } else {
                    indexskill = position;
                    getSkill = skillList.get(position).getSkill_name();
                }
                setSkill();
                adapter.notifyDataSetChanged();
            }
        });
        skilldone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSkill();
                alertDialog.dismiss();
            }
        });
    }

    private void setSkill() {
        if (getSkill != null && !getSkill.isEmpty() && !getSkill.equalsIgnoreCase(getString(R.string.selectskill))) {
            skill.setText(getSkill);
        } else {
            skill.setText(R.string.selectskill);
        }
    }

    //select experience from jobseeker add skill page
    private void ExperienceAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_ADD_Skill.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_ADD_Skill.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectexp);
        Button experiencedone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            indexexp = -1;
            for (int i = 0; i < select_exp_years.length; i++) {
                if (select_exp_years[i].equals(getExp)) {
                    indexexp = i;
                }
            }
        } else {
            indexexp = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyProfile_ADD_Skill.this, R.layout.spinner_item_text, select_exp_years) {
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
                String yourValue = select_exp_years[position];
                if (indexexp != -1 && (indexexp == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterexp.setAdapter(adapter);
        filterexp.setSelection(indexexp);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExp();
                alertDialog.dismiss();
            }
        });
        filterexp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexexp != -1 && (indexexp == position)) {
                    getExp = getString(R.string.selectexp);
                    indexexp = -1;
                } else {
                    indexexp = position;
                    getExp = select_exp_years[position];
                }
                setExp();
                adapter.notifyDataSetChanged();
            }
        });
        experiencedone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExp();
                alertDialog.dismiss();
            }
        });
    }

    private void setExp() {
        if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase(getString(R.string.selectexp))) {
            skillexp.setText(getExp);
        } else {
            skillexp.setText(R.string.selectexp);
        }
    }

    class getSkills extends AsyncTask<String, String, String> {
        String skilllistresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(MyProfile_ADD_Skill.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("jobseeker_id", GlobalData
                    .login_status).add("role_group_id", GlobalData.getRoleID).build();
            Request request = new Request.Builder().url(GlobalData.url + "getskillfromrole.php").post
                    (formBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                skilllistresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            try {
                JSONObject json = new JSONObject(skilllistresponse);
                Gson gson = new Gson();
                skillList = new ArrayList<>();
                skillList.addAll((Collection<? extends Skill>) gson.fromJson
                        (json.getString("skills"),
                                new TypeToken<ArrayList<Skill>>() {
                                }.getType()));
            } catch (JSONException ignored) {
            }
        }
    }
}
