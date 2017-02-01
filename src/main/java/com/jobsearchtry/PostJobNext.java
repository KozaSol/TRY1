package com.jobsearchtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.app.Dialog;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Jobs;
import com.jobsearchtry.wrapper.Skill;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostJobNext extends Activity {
    private ProgressDialog pg;
    private EditText aboutjob;
    private TextView miniquali, gender, experience;
    private Helper skill_req;
    private Button skillsreq;
    private LinearLayout qual_req_lay, gender_req_lay, exp_req_lay, skill_req_lay, musthavesection,
            postjob_skill_lay;
    private String getSkill = null, getAboutJob, getQuali = "N", getGender = "N", getExp
            = "N",
            getSkills = "N", getRequiredSkill = null,
            getGenderr = "Any", getExpp = "Not Needed", getQualii = "Not Needed";
    private boolean[] isCheckedSkill, isCheckedRequiredSkill;
    private ArrayList<Skill> skillList = new ArrayList<>();
    private ArrayList<String> SelectedskillList = new ArrayList<>();
    private ArrayList<String> RequiredSkillList = new ArrayList<>();
    private final ArrayList<String> SelectedRequiredSkillList = new ArrayList<>();
    private ArrayAdapter<Skill> skadapter;
    private OkHttpClient client = null;
    private int indexskillmust = -1;
    private ArrayAdapter<String> skill_must;

    @Override
    public void onBackPressed() {
        if(aboutjob.getText().toString().length() != 0) {
            new BackAlertDialog().isBackDialog(PostJobNext.this);
        }else{
            finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postjob_secondpart);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (PostJobNext.this);
        GlobalData.jobregid = sharedPreferences.getString("JRID", GlobalData.jobregid);
        musthavesection = (LinearLayout) findViewById(R.id.musthavesection);
        qual_req_lay = (LinearLayout) findViewById(R.id.qual_req_lay);
        gender_req_lay = (LinearLayout) findViewById(R.id.gender_req_lay);

        exp_req_lay = (LinearLayout) findViewById(R.id.exp_req_lay);
        skill_req_lay = (LinearLayout) findViewById(R.id.skill_req_lay);
        postjob_skill_lay = (LinearLayout) findViewById(R.id.postjob_skill_lay);
        miniquali = (TextView) findViewById(R.id.mini_qual_req);
        CheckBox quali_cb = (CheckBox) findViewById(R.id.checkbox_miniqual);
        experience = (TextView) findViewById(R.id.experience_req);
        CheckBox exp_cb = (CheckBox) findViewById(R.id.checkbox_experience);
        gender = (TextView) findViewById(R.id.gender_req);
        CheckBox gender_cb = (CheckBox) findViewById(R.id.checkbox_gender);
        skill_req = (Helper) findViewById(R.id.skill_req);
        skill_req.setExpanded(true);
        quali_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getQuali = "Y";
                } else {
                    getQuali = "N";
                }
            }
        });
        exp_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getExp = "Y";
                } else {
                    getExp = "N";
                }
            }
        });
        gender_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getGender = "Y";
                } else {
                    getGender = "N";
                }
            }
        });
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new GetJobDetails().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton pj_se_h = (ImageButton) findViewById(R.id.js_r_h);
        pj_se_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostJobNext.this, EmployerDashboard.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aboutjob.getText().toString().length() != 0) {
                    new BackAlertDialog().isBackDialog(PostJobNext.this);
                }else{
                    finish();
                }
            }
        });
        skillsreq = (Button) findViewById(R.id.emp_pj_sp_skill);
        postjob_skill_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    if (skillList.size() > 0) {
                        SkillAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
        aboutjob = (EditText) findViewById(R.id.emp_pj_sp_aboutjob);
        aboutjob.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.emp_pj_sp_aboutjob) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        Button submit = (Button) findViewById(R.id.tryEmpPostJob_SP);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedRequiredSkillList.size() > 0) {
                    String[] getskillmustaded = SelectedRequiredSkillList
                            .toArray(new String[SelectedRequiredSkillList.size()]);
                    String getskillmustarray = Arrays.toString(getskillmustaded);
                    getRequiredSkill = getskillmustarray.substring(1, getskillmustarray.length() -
                            1);
                    getRequiredSkill = getRequiredSkill.replace(", ", ",");
                    getSkills = "Y";
                } else {
                    getSkills = "N";
                    getRequiredSkill = "";
                }
                getAboutJob = aboutjob.getText().toString();
                if (!(getAboutJob.length() < 3)) {
                    if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                        new getPostNextJob().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(
                                R.string.checkconnection
                                ),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.aboutjobvalidation),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SkillAlert() {
        SelectedskillList = new ArrayList<>();
        View emppromptView = View.inflate(PostJobNext.this, R.layout.postjob_skill_popup, null);
        final Dialog alertDialog = new Dialog(PostJobNext.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_alert);
        final ListView filterskill = (ListView) emppromptView.findViewById(R.id.pj_filterskilllist);
        filterskill.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //get|set the skills
        skadapter = new ArrayAdapter<Skill>(this, R.layout.filter_listrow, skillList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.filter_listrow, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id.text1);
                textView.setText(skillList.get(position).getSkill_name());
                return textView;
            }
        };
        filterskill.setAdapter(skadapter);
        if (getSkill != null && !getSkill.isEmpty()) {
            List<String> locationlist = Arrays.asList(getSkill.split(","));
            isCheckedSkill = new boolean[skillList.size()];
            int indexskill = -1;
            for (int i = 0; i < locationlist.size(); i++)
                for (int j = 0; j < skillList.size(); j++) {
                    if (!(SelectedskillList.contains(locationlist.get(i)))) {
                        SelectedskillList.add(locationlist.get(i));
                    }
                    if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                        indexskill = j;
                        isCheckedSkill[indexskill] = true;
                        filterskill.setItemChecked(indexskill, true);
                        if (!(RequiredSkillList.contains(locationlist.get(i)))) {
                            RequiredSkillList.add(locationlist.get(i));
                        }
                    }
                }
            skillsreq.setText(getSkill);
            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                List<String> skillmustlist = Arrays.asList(getRequiredSkill.split(","));
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                indexskillmust = -1;
                for (int i = 0; i < skillmustlist.size(); i++) {
                    indexskillmust = RequiredSkillList.indexOf(skillmustlist.get(i));
                    if (indexskillmust != -1) {
                        isCheckedRequiredSkill[indexskillmust] = true;
                        skill_req.setItemChecked(indexskillmust, true);
                    }
                    if (!SelectedRequiredSkillList.contains(skillmustlist.get(i))) {
                        SelectedRequiredSkillList.add(skillmustlist.get(i));
                    }
                }
            } else {
                getRequiredSkill = "";
                indexskillmust = -1;
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                Arrays.fill(isCheckedRequiredSkill, false);
            }
        } else {
            skillsreq.setText(R.string.select);
            isCheckedSkill = new boolean[skillList.size()];
            Arrays.fill(isCheckedSkill, false);
        }
        //skills check|uncheck
        filterskill.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long
                    arg3) {
                if (!isCheckedSkill[position]) {
                    isCheckedSkill[position] = true;
                    if (!(SelectedskillList.contains(skadapter.getItem(position)
                            .getSkill_name()))) {
                        SelectedskillList.add(skadapter.getItem(position).getSkill_name());
                    }
                    if (!(RequiredSkillList.contains(skadapter.getItem(position)
                            .getSkill_name()))) {
                        RequiredSkillList.add(skadapter.getItem(position).getSkill_name());
                    }
                } else {
                    isCheckedSkill[position] = false;
                    SelectedskillList.remove(skadapter.getItem(position).getSkill_name());
                    RequiredSkillList.remove(skadapter.getItem(position).getSkill_name());
                }
            }
        });
        Button done_filter = (Button) emppromptView.findViewById(R.id.done_filter);
        Button resetall_filter = (Button) emppromptView.findViewById(R.id.resetall_filter);
        //skill selection reset all
        resetall_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetSkill();
                alertDialog.dismiss();
                SkillAlert();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doneSkill();
                alertDialog.dismiss();
            }
        });
        //skill selection done and get the must have skill list and must have setion check|uncheck
        done_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doneSkill();
                alertDialog.dismiss();
            }
        });
    }

    private void resetSkill() {
        getRequiredSkill = null;
        getRequiredSkill = "";
        SelectedRequiredSkillList.clear();
        SelectedskillList.clear();
        RequiredSkillList.clear();
        isCheckedSkill = new boolean[skillList.size()];
        Arrays.fill(isCheckedSkill, false);
        if (RequiredSkillList.size() > 0) {
            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
            Arrays.fill(isCheckedRequiredSkill, false);
        }
        getSkill = null;
        getSkill = "";
        skillsreq.setText(R.string.select);
        skill_req_lay.setVisibility(View.GONE);
        updateMusthave();
    }

    private void doneSkill() {
        String[] getLocationaded = SelectedskillList.toArray(new String[SelectedskillList
                .size()]);
        String getlocationarray = Arrays.toString(getLocationaded);
        getSkill = getlocationarray.substring(1, getlocationarray.length() - 1);
        getSkill = getSkill.replace(", ", ",");
        if (getSkill != null && !getSkill.isEmpty()) {
            skillsreq.setText(getSkill);
            skill_req_lay.setVisibility(View.VISIBLE);
        } else {
            skill_req_lay.setVisibility(View.GONE);
            skillsreq.setText(R.string.select);
        }
        skill_req.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        skill_must = new ArrayAdapter<String>(PostJobNext.this, R.layout.skill_helper,
                RequiredSkillList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.skill_helper, parent, false);
                }
                CheckedTextView textView = (CheckedTextView) v.findViewById(android.R.id
                        .text1);
                textView.setText(RequiredSkillList.get(position));
                return textView;
            }
        };
        skill_req.setAdapter(skill_must);
        if (getSkill != null && !getSkill.isEmpty()) {
            if (SelectedRequiredSkillList.size() > 0) {
                String[] getreqskilladed = SelectedRequiredSkillList
                        .toArray(new String[SelectedRequiredSkillList.size()]);
                String getreqskillarray = Arrays.toString(getreqskilladed);
                getRequiredSkill = getreqskillarray.substring(1, getreqskillarray.length()
                        - 1);
                getRequiredSkill = getRequiredSkill.replace(", ", ",");
            }
            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                List<String> skillmusttlist = Arrays.asList(getRequiredSkill.split(","));
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                indexskillmust = -1;
                for (int i = 0; i < skillmusttlist.size(); i++) {
                    indexskillmust = RequiredSkillList.indexOf(skillmusttlist.get(i));
                    if (indexskillmust != -1) {
                        isCheckedRequiredSkill[indexskillmust] = true;
                        skill_req.setItemChecked(indexskillmust, true);
                        if (!SelectedRequiredSkillList.contains(skillmusttlist.get(i))) {
                            SelectedRequiredSkillList.add(skillmusttlist.get(i));
                        }
                    } else {
                        SelectedRequiredSkillList.remove(skillmusttlist.get(i));
                    }
                }
            } else {
                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                Arrays.fill(isCheckedRequiredSkill, false);
            }
        } else {
            isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
            Arrays.fill(isCheckedRequiredSkill, false);
        }
        if (SelectedRequiredSkillList.size() > 0) {
            String[] getreqskilladed = SelectedRequiredSkillList
                    .toArray(new String[SelectedRequiredSkillList.size()]);
            String getreqskillarray = Arrays.toString(getreqskilladed);
            getRequiredSkill = getreqskillarray.substring(1, getreqskillarray.length() -
                    1);
            getRequiredSkill = getRequiredSkill.replace(", ", ",");
        }
        updateSkillMust();
        updateMusthave();

    }

    //must have section view show|hide
    private void updateMusthave() {
        if (getGender.equalsIgnoreCase("Any") && getExp.equalsIgnoreCase("Not Needed") &&
                getQualii.equalsIgnoreCase("Not Needed") && (getSkill == null || getSkill.isEmpty
                ())) {
            musthavesection.setVisibility(View.GONE);
        } else {
            musthavesection.setVisibility(View.VISIBLE);
        }
    }

    private class GetJobDetails extends AsyncTask<String, String, String> {
        String jobdetailresponse = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(PostJobNext.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("action", "JobPostSecond")
                    .add("job_id", GlobalData.jobregid).build();
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update" +
                    ".php").post(formBody)
                    .build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                jobdetailresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (jobdetailresponse != null && !jobdetailresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(jobdetailresponse);
                    Gson gson = new Gson();
                    Jobs jobdetail = gson.fromJson(responseObj.getString("list"), new TypeToken<Jobs>
                            () {
                    }.getType());
                    skillsreq.setText(jobdetail.getSkills());
                    getSkill = jobdetail.getSkills();
                    miniquali.setText(jobdetail.getMinimum_qual());
                    getGenderr = jobdetail.getJobgender();
                    getQualii = jobdetail.getMinimum_qual();
                    getExpp = jobdetail.getExperience();
                    if (jobdetail.getMinimum_qual().equalsIgnoreCase("Not Needed")) {
                        qual_req_lay.setVisibility(View.GONE);
                    } else {
                        qual_req_lay.setVisibility(View.VISIBLE);
                    }
                    experience.setText(jobdetail.getExperience());
                    if (jobdetail.getExperience().equalsIgnoreCase("Not Needed")) {
                        exp_req_lay.setVisibility(View.GONE);
                    } else {
                        exp_req_lay.setVisibility(View.VISIBLE);
                    }
                    gender.setText(jobdetail.getJobgender());
                    if (jobdetail.getJobgender().equalsIgnoreCase("Any")) {
                        gender_req_lay.setVisibility(View.GONE);
                    } else {
                        gender_req_lay.setVisibility(View.VISIBLE);
                    }
                    getRequiredSkill = jobdetail.getReq_skillname();
                    aboutjob.setText(jobdetail.getJob_description());
                    skillList = gson.fromJson(responseObj.getString("skills"), new
                            TypeToken<ArrayList<Skill>>() {
                            }.getType());
                    if (skillList.size() > 0) {
                        postjob_skill_lay.setVisibility(View.VISIBLE);
                        if (getSkill != null && !getSkill.isEmpty()) {
                            List<String> locationlist = Arrays.asList(getSkill.split(","));
                            for (int i = 0; i < locationlist.size(); i++)
                                for (int j = 0; j < skillList.size(); j++) {
                                    if (!(SelectedskillList.contains(locationlist.get(i)))) {
                                        SelectedskillList.add(locationlist.get(i));
                                    }
                                    if (skillList.get(j).getSkill_name().equals(locationlist.get(i))) {
                                        if (!(RequiredSkillList.contains(locationlist.get(i)))) {
                                            RequiredSkillList.add(locationlist.get(i));
                                        }
                                    }
                                }
                            skillsreq.setText(getSkill);
                            if (getRequiredSkill != null && !getRequiredSkill.isEmpty()) {
                                List<String> skillmustlist = Arrays.asList(getRequiredSkill.split(","));
                                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                                indexskillmust = -1;
                                for (int i = 0; i < skillmustlist.size(); i++) {
                                    indexskillmust = RequiredSkillList.indexOf(skillmustlist.get(i));
                                    if (indexskillmust != -1) {
                                        isCheckedRequiredSkill[indexskillmust] = true;
                                        skill_req.setItemChecked(indexskillmust, true);
                                    }
                                    if (!SelectedRequiredSkillList.contains(skillmustlist.get(i))) {
                                        SelectedRequiredSkillList.add(skillmustlist.get(i));
                                    }
                                }
                            } else {
                                getRequiredSkill = "";
                                indexskillmust = -1;
                                isCheckedRequiredSkill = new boolean[RequiredSkillList.size()];
                                Arrays.fill(isCheckedRequiredSkill, false);
                            }
                        } else {
                            skillsreq.setText(R.string.select);
                            isCheckedSkill = new boolean[skillList.size()];
                            Arrays.fill(isCheckedSkill, false);
                        }
                    } else {
                        resetSkill();
                        postjob_skill_lay.setVisibility(View.GONE);
                    }
                    if (SelectedskillList.size() > 0 && skillList.size() > 0) {
                        for (Skill u : skillList) {
                            if (!SelectedskillList.contains(u.getSkill_name())) {
                                SelectedskillList.remove(u.getSkill_name());
                            }
                        }
                    }
                    doneSkill();
                    isCheckedSkill = new boolean[skillList.size()];
                    Arrays.fill(isCheckedSkill, false);
                    if (getQualii.equalsIgnoreCase("Not Needed") && getExpp.equalsIgnoreCase("Not " +
                            "Needed") && getGenderr.equalsIgnoreCase("Any") && (getSkill == null
                            || getSkill.isEmpty())) {
                        musthavesection.setVisibility(View.GONE);
                    } else {
                        musthavesection.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionfailure), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private void updateSkillMust() {
        skill_req.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!isCheckedRequiredSkill[position]) {
                    isCheckedRequiredSkill[position] = true;
                    if (!(SelectedRequiredSkillList.contains(skill_must.getItem(position)))) {
                        SelectedRequiredSkillList.add(skill_must.getItem(position));
                    }
                } else {
                    isCheckedRequiredSkill[position] = false;
                    SelectedRequiredSkillList.remove(skill_must.getItem(position));
                }
            }
        });
    }

    private class getPostNextJob extends AsyncTask<String, String, String> {
        String postjobresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception ignored) {
            }
            pg = new ProgressDialog(PostJobNext.this, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder().add("action", "JobpostUpdate")
                    .add("job_id", GlobalData.jobregid).add("job_description", getAboutJob).add
                            ("Skills", getSkill)
                    .add("minimum_qualification_req", getQuali).add("gender_req", getGender)
                    .add("skills_req", getSkills).add("experience_req", getExp).add
                            ("req_skillname", getRequiredSkill)
                    .build();
            Request request = new Request.Builder().url(GlobalData.url + "employer_View_update" +
                    ".php").post(formBody)
                    .build();
            client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                postjobresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (pg != null && pg.isShowing())
                pg.dismiss();
            if (postjobresponse != null && !postjobresponse.contains("connectionFailure")) {
                try {
                    startActivity(new Intent(PostJobNext.this, PostJobFinalSubmit.class));
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
