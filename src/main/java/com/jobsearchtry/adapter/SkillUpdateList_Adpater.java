package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.R;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.wrapper.Skill;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SkillUpdateList_Adpater extends BaseAdapter {

    private final Activity activity;
    private final ArrayList<Skill> skillupdateList;
    private Dialog alertDialog;
    private String getExp, getexperience;
    private ProgressDialog pg;
    private int indexexp = -1;
    private static final String[] select_exp_years = {"<1 Yr", "1 Yr", "2 Yrs", "3 Yrs", "4 Yrs",
            "5 Yrs", "6 Yrs",
            "7 Yrs", "8 Yrs", "9 Yrs", "10 Yrs", "11 Yrs", "12 Yrs", "13 Yrs", "14 Yrs", "15 Yrs",
            "16 Yrs", "17 Yrs",
            "18 Yrs", "19 Yrs", "20 Yrs", "21 Yrs", "22 Yrs", "23 Yrs", "24 Yrs", ">25 Yrs"};

    public SkillUpdateList_Adpater(Activity a, ArrayList<Skill> skillupdateList) {
        activity = a;
        this.skillupdateList = skillupdateList;
    }

    public int getCount() {
        return skillupdateList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        Button skill_name, experience;
        LinearLayout courseexp_lay;
        TextView updateskillid, updateskillcategory;
        ImageButton skill_deleteicon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.skill_update_listrow, parent, false);
            holder = new ViewHolder();
            holder.skill_deleteicon = (ImageButton) convertView.findViewById(R.id
                    .skill_deleteicon);
            holder.skill_name = (Button) convertView.findViewById(R.id.js_edit_skillname);
            holder.experience = (Button) convertView.findViewById(R.id.js_edit_exp);
            holder.courseexp_lay = (LinearLayout) convertView.findViewById(R.id.js_edit_exp_lay);
            holder.updateskillid = (TextView) convertView.findViewById(R.id.updateskillid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.skill_deleteicon.setId(position);
        holder.courseexp_lay.setId(position);
        holder.updateskillid.setText(skillupdateList.get(position).getSkill_id());
        getExp = skillupdateList.get(position).getExperience();
        String getSkill = skillupdateList.get(position).getSkill_name();
        holder.skill_name.setText(getSkill);
        if (getExp != null && !getExp.isEmpty() && !getExp.equalsIgnoreCase("Experience")) {
            holder.experience.setText(getExp);
        } else {
            holder.experience.setText("Experience");
        }
        holder.courseexp_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getExp = holder.experience.getText().toString();
                ExpAlert(getExp, holder.experience);
            }
        });
        holder.skill_deleteicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View emppromptView = View.inflate(activity, R.layout.delete_popup,
                        null);
                alertDialog = new Dialog(activity,R.style.MyThemeDialog);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setContentView(emppromptView);
                TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.d_popupheader);
                f_popupheader.setText(R.string.deleteconfirm);
                TextView f_popupsubheader = (TextView) emppromptView.findViewById(R.id
                        .d_popup_subheader);
                f_popupsubheader.setText(R.string.delskill);
                Button no = (Button) emppromptView.findViewById(R.id.d_no);
                Button yes = (Button) emppromptView.findViewById(R.id.d_yes);
                alertDialog.show();
                GlobalData.skillid = skillupdateList.get(v.getId()).getSkill_id();
                final int getid = v.getId();
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        new deleteSkill(getid).execute(GlobalData.skillid);
                        alertDialog.dismiss();
                    }
                });
                no.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        return convertView;
    }

    //select exp role from job seeker skill updating page
    private void ExpAlert(String getExp, final Button experience) {
        getexperience = getExp;
        View emppromptView = View.inflate(activity, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(activity,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.experience);
        Button expdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterexp = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getexperience != null && !getexperience.isEmpty() && !getexperience.equalsIgnoreCase("Experience")) {
            indexexp = -1;
            for (int i = 0; i < select_exp_years.length; i++) {
                if (select_exp_years[i].equals(getexperience)) {
                    indexexp = i;
                }
            }
        } else {
            indexexp = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_text, select_exp_years) {
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
        alertDialog.show();
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExp(experience, getexperience);
                alertDialog.dismiss();
            }
        });
        filterexp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexexp != -1 && (indexexp == position)) {
                    getexperience = activity.getString(R.string.experience);
                    indexexp = -1;
                } else {
                    indexexp = position;
                    getexperience = select_exp_years[position];
                }
                setExp(experience, getexperience);
                adapter.notifyDataSetChanged();
            }
        });
        expdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExp(experience, getexperience);
                alertDialog.dismiss();
            }
        });
    }

    private void setExp(Button experience, String getexperience) {
        if (getexperience != null && !getexperience.isEmpty() && !getexperience.equalsIgnoreCase(activity.getString(R.string.experience))) {
            experience.setText(getexperience);
        } else {
            experience.setText(activity.getString(R.string.experience));
        }
    }

    private class deleteSkill extends AsyncTask<String, String, String> {
        String deleteresponse;
        int id = -1;

        deleteSkill(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(activity, R.style.MyTheme);
            pg.setCancelable(false);
            pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pg.setIndeterminate(true);
            pg.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
            pg.show();
        }

        protected String doInBackground(String... args) {
            String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
            MultipartBody.Builder paramsadd = new MultipartBody.Builder().setType(MultipartBody
                    .FORM);
            if (!languages.equalsIgnoreCase("English")) {
                paramsadd.addFormDataPart("languages", languages);
            }
            paramsadd.addFormDataPart("jobseeker_id", GlobalData.login_status);
            paramsadd.addFormDataPart("skill_id", args[0]);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "skills_deleted.php")
                    .post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
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
                    JSONObject responseObj = new JSONObject(deleteresponse);
                    int deletestatus = responseObj.getInt("status");
                    String getDeleteStatus = responseObj.getString("skill_status");
                    if (deletestatus == 1) {
                        skillupdateList.remove(id);
                        if(skillupdateList.size() == 0){
                            activity.finish();
                        }
                    }
                    notifyDataSetChanged();
                    Toast.makeText(activity, getDeleteStatus, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(activity, activity.getString(R.string.errortoparse), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.connectionfailure), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}