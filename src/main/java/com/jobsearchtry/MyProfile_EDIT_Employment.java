package com.jobsearchtry;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import android.app.Dialog;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobsearchtry.utils.BackAlertDialog;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.wrapper.Employment;
import com.jobsearchtry.wrapper.Industry;
import com.jobsearchtry.wrapper.Role;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfile_EDIT_Employment extends Activity {
    private EditText jobtitle, companyname, salary;
    private Button industry, role, start_year, start_month, end_year, end_month;
    private CheckBox samecomp;
    private LinearLayout endyearlay, js_emp_edit_industry_lay, js_emp_edit_role_lay,
            js_emp_edit_startyear_lay,
            js_emp_edit_startmonth_lay, js_emp_edit_endyear_lay, js_emp_edit_endmonth_lay;
    private ProgressDialog pg;
    private String getJobTitle, getCompname, getIndustry, getRole, getSYear, getSMonth, getEYear,
            getEMonth, getSMonthID, getEMonthID, getCWS, getSalary, languages;
    private ArrayList<String> select_year;
    private static final String[] select_month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String[] select_month_id = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12"};
    private int indexendmonth = -1, indexstartmonth = -1, indexstartyear = -1, indexendyear = -1, indexrole = -1, indexindustry = -1;
    private OkHttpClient client = null;
    private ArrayList<Role> roleList = null;
    private ArrayList<Industry> industriesList = null;
    private ArrayAdapter<Role> roleadapter;
    private ArrayAdapter<Industry> industryAdapter;

    @Override
    public void onBackPressed() {
        new BackAlertDialog().isBackDialog(MyProfile_EDIT_Employment.this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employment_edit);
        languages = getResources().getConfiguration().locale.getDisplayLanguage();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                (getApplicationContext()));
        GlobalData.employmentid = sharedPreferences.getString("EMP_ID", GlobalData.employmentid);
        GlobalData.login_status = sharedPreferences.getString("LS", GlobalData.login_status);
        if (new UtilService().isNetworkAvailable(getApplicationContext())) {
            new getEmploymentDetail().execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast
                    .LENGTH_SHORT).show();
        }
        ImageButton emp_edit_h = (ImageButton) findViewById(R.id.js_r_h);
        emp_edit_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.joblistfrom = "RL";
                startActivity(new Intent(MyProfile_EDIT_Employment.this, Homepage.class));
                finish();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackAlertDialog().isBackDialog(MyProfile_EDIT_Employment.this);
            }
        });
        jobtitle = (EditText) findViewById(R.id.js_emp_edit_jobtitle);
        companyname = (EditText) findViewById(R.id.js_emp_edit_compname);
        salary = (EditText) findViewById(R.id.js_emp_edit_salary);
        industry = (Button) findViewById(R.id.js_emp_edit_industry);
        js_emp_edit_industry_lay = (LinearLayout) findViewById(R.id.js_emp_edit_industry_lay);
        role = (Button) findViewById(R.id.js_emp_edit_role);
        js_emp_edit_role_lay = (LinearLayout) findViewById(R.id.js_emp_edit_role_lay);
        start_year = (Button) findViewById(R.id.js_emp_edit_startyear);
        js_emp_edit_startyear_lay = (LinearLayout) findViewById(R.id.js_emp_edit_startyear_lay);
        start_month = (Button) findViewById(R.id.js_emp_edit_startmonth);
        js_emp_edit_startmonth_lay = (LinearLayout) findViewById(R.id.js_emp_edit_startmonth_lay);
        end_year = (Button) findViewById(R.id.js_emp_edit_endyear);
        js_emp_edit_endyear_lay = (LinearLayout) findViewById(R.id.js_emp_edit_endyear_lay);
        end_month = (Button) findViewById(R.id.js_emp_edit_endmonth);
        js_emp_edit_endmonth_lay = (LinearLayout) findViewById(R.id.js_emp_edit_endmonth_lay);
        samecomp = (CheckBox) findViewById(R.id.checkbox_emp_edit);
        endyearlay = (LinearLayout) findViewById(R.id.endyearlay_editemp);
        select_year = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = year; i > 1981; i--) {
            select_year.add("" + i);
        }
        samecomp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    endyearlay.setVisibility(View.GONE);
                    getCWS = "1";
                    Format formatter = new SimpleDateFormat("MMM");
                    getEMonth = formatter.format(new Date());
                } else {
                    getCWS = "0";
                    endyearlay.setVisibility(View.VISIBLE);
                }
            }
        });
        js_emp_edit_startyear_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StartYearAlert();
            }
        });
        js_emp_edit_startmonth_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StartMonthAlert();
            }
        });
        js_emp_edit_endyear_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EndYearAlert();
            }
        });
        js_emp_edit_endmonth_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EndMonthAlert();
            }
        });
        js_emp_edit_role_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleList.size() > 0) {
                    RoleAlert();
                }
            }
        });
        js_emp_edit_industry_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (industriesList.size() > 0) {
                    IndustryAlert();
                }
            }
        });

        Button submit = (Button) findViewById(R.id.js_try_Employment_Edit_Submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("MM");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                Date date = new Date();
                String getmonthid = dateFormat.format(date);
                String getyear = yearFormat.format(date);
                getJobTitle = jobtitle.getText().toString();
                getSalary = salary.getText().toString();
                getCompname = companyname.getText().toString();
                if (!(null == getJobTitle || getJobTitle.length() < 3)) {
                    if (!(null == getCompname || getCompname.length() < 3)) {
                        if (!(getIndustry.equalsIgnoreCase(getString(R.string.selectindustry)))) {
                            if (!(getRole.equalsIgnoreCase(getString(R.string.selectrole)))) {
                                if (!(null == getSalary || getSalary.length() < 1 || Integer.parseInt(getSalary) == 0)) {
                                    if (!(Integer.parseInt(getSalary) > 200000)) {
                                        if (!(getCWS.equalsIgnoreCase("1"))) {
                                            if (!getSYear.equalsIgnoreCase(getString(R.string.year))) {
                                                if (!getEYear.equalsIgnoreCase(getString(R.string.year))) {
                                                    if (getSYear.equalsIgnoreCase(getEYear)) {
                                                        if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                            if (!getEMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                if (Integer.parseInt(getSMonthID) < Integer.parseInt
                                                                        (getEMonthID)) {
                                                                    if (getyear.equalsIgnoreCase(getSYear)) {
                                                                        if (Integer.parseInt(getSMonthID) <= Integer
                                                                                .parseInt(getmonthid)) {
                                                                            if (getyear.equalsIgnoreCase(getEYear)) {
                                                                                if (Integer.parseInt(getEMonthID) <=
                                                                                        Integer
                                                                                                .parseInt(getmonthid)) {
                                                                                    addemp();
                                                                                } else {
                                                                                    Toast.makeText(getApplicationContext(),
                                                                                            getString(R.string.endmonthyearsamecurrentmonthexists),
                                                                                            Toast.LENGTH_LONG).show();
                                                                                }
                                                                            } else {
                                                                                addemp();
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(),
                                                                                    getString(R.string.startmonthnotgreatercurrentmonth),
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                    } else {
                                                                        addemp();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(),
                                                                            getString(R.string.startendmonthmonthyearcantsame),
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {
                                                                Toast.makeText(getApplicationContext(),
                                                                        getString(R.string.endingmonthvalidation),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getApplicationContext(),
                                                                    getString(R.string.startingmonthvalidation),
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        if (Integer.parseInt(getSYear) < Integer.parseInt
                                                                (getEYear)) {
                                                            if (getyear.equalsIgnoreCase(getSYear)) {
                                                                if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                    if (Integer.parseInt(getSMonthID) <= Integer
                                                                            .parseInt(getmonthid)) {
                                                                        if (getyear.equalsIgnoreCase(getEYear)) {
                                                                            if (!getEMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                                if (Integer.parseInt(getEMonthID) <=
                                                                                        Integer
                                                                                                .parseInt(getmonthid)) {
                                                                                    addemp();
                                                                                } else {
                                                                                    Toast.makeText(getApplicationContext(),
                                                                                            getString(R.string.endmonthcurrentmonthexits),
                                                                                            Toast.LENGTH_LONG).show();
                                                                                }
                                                                            } else {
                                                                                Toast.makeText(getApplicationContext(),
                                                                                        getString(R.string.endingmonthvalidation),
                                                                                        Toast.LENGTH_LONG).show();
                                                                            }
                                                                        } else {
                                                                            addemp();
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(),
                                                                                getString(R.string.startmonthcurrentmonthexits),
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(),
                                                                            getString(R.string.startingmonthvalidation),
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {
                                                                if (getyear.equalsIgnoreCase(getEYear)) {
                                                                    if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                        if (!getEMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                            if (Integer.parseInt(getEMonthID) <= Integer
                                                                                    .parseInt(getmonthid)) {
                                                                                addemp();
                                                                            } else {
                                                                                Toast.makeText(getApplicationContext(),
                                                                                        getString(R.string.endmonthcurrentmonthexits),
                                                                                        Toast.LENGTH_LONG).show();
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(),
                                                                                    getString(R.string.endingmonthvalidation),
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(),
                                                                                getString(R.string.startingmonthvalidation),
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                } else {
                                                                    if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                        if (!getEMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                                            addemp();
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(),
                                                                                    getString(R.string.endingmonthvalidation),
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(),
                                                                                getString(R.string.startingmonthvalidation),
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            }
                                                        } else {

                                                            Toast.makeText(getApplicationContext(),
                                                                    getString(R.string.startendyearvalidation),
                                                                    Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(),
                                                            getString(R.string.endingyearvalidation),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        getString(R.string.startingyearvalidation),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            if (!getSYear.equalsIgnoreCase(getString(R.string.year))) {
                                                if (getyear.equalsIgnoreCase(getSYear)) {
                                                    if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                        if ((Integer.parseInt(getSMonthID) <= Integer.parseInt
                                                                (getmonthid))) {
                                                            addemp();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(),
                                                                    getString(R.string.yearsamecurrentmonthexiststartmonthvalidation),
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),
                                                                getString(R.string.startingmonthvalidation),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                                                        addemp();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),
                                                                getString(R.string.startingmonthvalidation),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        getString(R.string.startingyearvalidation),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.salaryvalidationmaximum),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.salaryvalidation),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.jobrolevalidation),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.industryvalidation),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.companynamevalidation), Toast
                                        .LENGTH_LONG).show();
                    }
                } else

                {
                    Toast.makeText(getApplicationContext(), getString(R.string.designationvalidation),
                            Toast.LENGTH_SHORT).show();
                }
            }

            void addemp() {
                if (new UtilService().isNetworkAvailable(getApplicationContext())) {
                    new updateEmployment().execute();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.checkconnection), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    //select job role from edit emp page
    private void RoleAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectrole);
        Button roledone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterrole = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            indexrole = -1;
            if (roleList != null && roleList.size() > 0) {
                Role localrole = new Role();
                localrole.setRole_name(getRole);
                indexrole = roleList.indexOf(localrole);
            }
        } else {
            indexrole = -1;
        }
        roleadapter = new ArrayAdapter<Role>(this, R.layout.spinner_item_text, roleList) {
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
                String yourValue = roleList.get(position).getRole_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = roleList.get(position).getRole_name_local();
                }
                if (indexrole != -1 && (indexrole == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterrole.setAdapter(roleadapter);
        filterrole.setSelection(indexrole);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
            }
        });
        filterrole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexrole != -1 && (indexrole == position)) {
                    getRole = getString(R.string.selectrole);
                    indexrole = -1;
                } else {
                    indexrole = position;
                    getRole = roleList.get(position).getRole_name();
                }
                setRole();
                roleadapter.notifyDataSetChanged();
            }
        });
        roledone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRole();
                alertDialog.dismiss();
            }
        });
    }

    private void setRole() {
        if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
            role.setText(getRole);
            if (!languages.equalsIgnoreCase("English")) {
                setRoleLocalLang();
            }
        } else {
            role.setText(getString(R.string.selectrole));
        }
    }

    private void setRoleLocalLang() {
        Role localrole = new Role();
        localrole.setRole_name(getRole);
        int indexrole = roleList.indexOf(localrole);
        String RoleLocal = roleList.get(indexrole).getRole_name_local();
        role.setText(RoleLocal);
    }

    //select industry from add emp page
    private void IndustryAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.selectindustry);
        Button industrydone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterindustry = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            indexindustry = -1;
            if (industriesList != null && industriesList.size() > 0) {
                Industry localindustry = new Industry();
                localindustry.setIndustry_name(getIndustry);
                indexindustry = industriesList.indexOf(localindustry);
            }
        } else {
            indexindustry = -1;
        }
        industryAdapter = new ArrayAdapter<Industry>(this, R.layout.spinner_item_text, industriesList) {
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
                String yourValue = industriesList.get(position).getIndustry_name();
                if (!languages.equalsIgnoreCase("English")) {
                    yourValue = industriesList.get(position).getIndustry_name_local();
                }
                if (indexindustry != -1 && (indexindustry == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterindustry.setAdapter(industryAdapter);
        filterindustry.setSelection(indexindustry);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
        filterindustry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexindustry != -1 && (indexindustry == position)) {
                    getIndustry = getString(R.string.selectindustry);
                    indexindustry = -1;
                } else {
                    indexindustry = position;
                    getIndustry = industriesList.get(position).getIndustry_name();
                }
                setIndustry();
                industryAdapter.notifyDataSetChanged();
            }
        });
        industrydone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndustry();
                alertDialog.dismiss();
            }
        });
    }

    private void setIndustry() {
        if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
            industry.setText(getIndustry);
            if (!languages.equalsIgnoreCase("English")) {
                setIndustryLocalLang();
            }
        } else {
            industry.setText(getString(R.string.selectindustry));
        }
    }

    private void setIndustryLocalLang() {
        Industry localindustry = new Industry();
        localindustry.setIndustry_name(getIndustry);
        indexindustry = industriesList.indexOf(localindustry);
        String IndustryLocal = industriesList.get(indexindustry).getIndustry_name_local();
        industry.setText(IndustryLocal);
    }

    //select start year from edit emp page
    private void StartYearAlert() {
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.year);
        Button startyeardone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterstartyear = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSYear != null && !getSYear.isEmpty() && !getSYear.equalsIgnoreCase(getString(R.string.year))) {
            indexstartyear = -1;
            for (int i = 0; i < select_year.size(); i++) {
                if (select_year.get(i).equals(getSYear)) {
                    indexstartyear = i;
                }
            }
        } else {
            indexstartyear = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_year) {
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
                String yourValue = select_year.get(position);
                if (indexstartyear != -1 && (indexstartyear == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterstartyear.setAdapter(adapter);
        filterstartyear.setSelection(indexstartyear);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartYear();
                alertDialog.dismiss();
            }
        });
        filterstartyear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexstartyear != -1 && (indexstartyear == position)) {
                    getSYear = getString(R.string.year);
                    indexstartyear = -1;
                } else {
                    indexstartyear = position;
                    getSYear = select_year.get(position);
                }
                setStartYear();
                adapter.notifyDataSetChanged();
            }
        });
        startyeardone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartYear();
                alertDialog.dismiss();
            }
        });
    }

    private void setStartYear() {
        if (getSYear != null && !getSYear.isEmpty() && !getSYear.equalsIgnoreCase(getString(R.string.year))) {
            start_year.setText(getSYear);
        } else {
            start_year.setText(getString(R.string.year));
        }
    }

    //select end year from edit emp page
    private void EndYearAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.year);
        Button endyeardone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterendyear = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getEYear != null && !getEYear.isEmpty() && !getEYear.equalsIgnoreCase(getString(R.string.year))) {
            indexendyear = -1;
            for (int i = 0; i < select_year.size(); i++) {
                if (select_year.get(i).equals(getEYear)) {
                    indexendyear = i;
                }
            }
        } else {
            indexendyear = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_year) {
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
                String yourValue = select_year.get(position);
                if (indexendyear != -1 && (indexendyear == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterendyear.setAdapter(adapter);
        filterendyear.setSelection(indexendyear);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndYear();
                alertDialog.dismiss();
            }
        });
        filterendyear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexendyear != -1 && (indexendyear == position)) {
                    getEYear = getString(R.string.year);
                    indexendyear = -1;
                } else {
                    indexendyear = position;
                    getEYear = select_year.get(position);
                }
                setEndYear();
                adapter.notifyDataSetChanged();
            }
        });
        endyeardone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndYear();
                alertDialog.dismiss();
            }
        });
    }

    private void setEndYear() {
        if (getEYear != null && !getEYear.isEmpty() && !getEYear.equalsIgnoreCase(getString(R.string.year))) {
            end_year.setText(getEYear);
        } else {
            end_year.setText(getString(R.string.year));
        }
    }

    //select start month from edit emp page
    private void StartMonthAlert() {
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.month);
        Button startmonthdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterstartmonth = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getSMonth != null && !getSMonth.isEmpty() && !getSMonth.equalsIgnoreCase(getString(R.string.month))) {
            indexstartmonth = -1;
            for (int i = 0; i < select_month.length; i++) {
                if (select_month[i].equals(getSMonth)) {
                    indexstartmonth = i;
                }
            }
        } else {
            indexstartmonth = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_month) {
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
                String yourValue = select_month[position];
                if (indexstartmonth != -1 && (indexstartmonth == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterstartmonth.setAdapter(adapter);
        filterstartmonth.setSelection(indexstartmonth);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartMonth();
                alertDialog.dismiss();
            }
        });
        filterstartmonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexstartmonth != -1 && (indexstartmonth == position)) {
                    getSMonth = getString(R.string.month);
                    indexstartmonth = -1;
                } else {
                    indexstartmonth = position;
                    getSMonth = select_month[position];
                    int index = Arrays.asList(select_month).indexOf(getSMonth);
                    getSMonthID = select_month_id[index];
                }
                setStartMonth();
                adapter.notifyDataSetChanged();
            }
        });
        startmonthdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartMonth();
                alertDialog.dismiss();
            }
        });
    }

    private void setStartMonth() {
        if (!getSMonth.equalsIgnoreCase(getString(R.string.month))) {
            start_month.setText(getSMonth);
        } else {
            start_month.setText(getString(R.string.month));
        }
    }

    //select end month from edit emp page
    private void EndMonthAlert() {
        View emppromptView = View.inflate(MyProfile_EDIT_Employment.this, R.layout.spinner, null);
        final Dialog alertDialog = new Dialog(MyProfile_EDIT_Employment.this,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        TextView f_popupheader = (TextView) emppromptView.findViewById(R.id.spinn_popupheader);
        f_popupheader.setText(R.string.month);
        Button endmonthdone = (Button) emppromptView.findViewById(R.id.spinner_done);
        ImageButton exit = (ImageButton) emppromptView.findViewById(R.id.exit_spinner);
        final ListView filterendmonth = (ListView) emppromptView.findViewById(R.id.spinnerlist);
        if (getEMonth != null && !getEMonth.isEmpty()) {
            indexendmonth = -1;
            for (int i = 0; i < select_month.length; i++) {
                if (select_month[i].equals(getEMonth)) {
                    indexendmonth = i;
                }
            }
        } else {
            indexendmonth = -1;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, select_month) {
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
                String yourValue = select_month[position];
                if (indexendmonth != -1 && (indexendmonth == position)) {
                    textView.setBackgroundColor(Color.parseColor("#a7c1cc"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#dedede"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterendmonth.setAdapter(adapter);
        filterendmonth.setSelection(indexendmonth);
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndMonth();
                alertDialog.dismiss();
            }
        });
        filterendmonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexendmonth != -1 && (indexendmonth == position)) {
                    getEMonth = getString(R.string.month);
                    indexendmonth = -1;
                } else {
                    indexendmonth = position;
                    getEMonth = select_month[position];
                    int index = Arrays.asList(select_month).indexOf(getEMonth);
                    getEMonthID = select_month_id[index];
                }
                setEndMonth();
                adapter.notifyDataSetChanged();
            }
        });
        endmonthdone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndMonth();
                alertDialog.dismiss();
            }
        });
    }

    private void setEndMonth() {
        if (!getEMonth.equalsIgnoreCase(getString(R.string.month))) {
            end_month.setText(getEMonth);
        } else {
            end_month.setText(getString(R.string.month));
        }
    }

    private class updateEmployment extends AsyncTask<String, String, String> {
        String randomkeysendresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pg = new ProgressDialog(MyProfile_EDIT_Employment.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("job_title", getJobTitle);
            paramsadd.addFormDataPart("company_name", getCompname);
            paramsadd.addFormDataPart("manufacturing", getIndustry);
            paramsadd.addFormDataPart("role", getRole);
            paramsadd.addFormDataPart("salary", getSalary);
            paramsadd.addFormDataPart("start_year", getSYear);
            paramsadd.addFormDataPart("start_month", getSMonth);
            if (getCWS.equalsIgnoreCase("1")) {
                getEYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
                int getmonth = Calendar.getInstance().get(Calendar.MONTH);
                getEMonth = select_month[getmonth];
            }
            paramsadd.addFormDataPart("end_year", getEYear);
            paramsadd.addFormDataPart("end_month", getEMonth);
            paramsadd.addFormDataPart("currently_work_here", getCWS);
            paramsadd.addFormDataPart("jobseeker_id", GlobalData
                    .login_status);
            paramsadd.addFormDataPart("employment_id", GlobalData
                    .employmentid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employment_update.php").post
                    (requestBody).build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                randomkeysendresponse = response.body().string();
            } catch (IOException e) {
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
                    JSONObject responseObj = new JSONObject(randomkeysendresponse);
                    int getStatusCode = responseObj.getInt("status_code");
                    String getStatus = responseObj.getString("status");
                    Toast.makeText(getBaseContext(), getStatus, Toast.LENGTH_SHORT).show();
                    if (getStatusCode == 1) {
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

    private class getEmploymentDetail extends AsyncTask<String, String, String> {
        String empeditresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                if (pg != null && pg.isShowing())
                    pg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pg = new ProgressDialog(MyProfile_EDIT_Employment.this, R.style.MyTheme);
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
            paramsadd.addFormDataPart("employment_id", GlobalData.employmentid);
            MultipartBody requestBody = paramsadd.build();
            Request request = new Request.Builder().url(GlobalData.url + "employment_view_edit" +
                    ".php").post(requestBody)
                    .build();
            client = new OkHttpClient();
            Response response;
            try {
                response = client.newCall(request).execute();
                empeditresponse = response.body().string();
            } catch (IOException e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            if (empeditresponse != null && !empeditresponse.contains("connectionFailure")) {
                try {
                    JSONObject responseObj = new JSONObject(empeditresponse);
                    Gson gson = new Gson();
                    Employment employment = gson.fromJson(responseObj.getString("edu_view"),
                            new TypeToken<Employment>() {
                            }.getType());
                    jobtitle.setText(employment.getJob_title());
                    companyname.setText(employment.getCompany_name());
                    salary.setText(employment.getSalary());
                    getIndustry = employment.getManufacturing();
                    getRole = employment.getRole();
                    getSYear = employment.getStart_year();
                    getSMonth = employment.getStart_month();
                    int index = Arrays.asList(select_month).indexOf(getSMonth);
                    getSMonthID = select_month_id[index];
                    getEYear = employment.getEnd_year();
                    getEMonth = employment.getEnd_month();
                    int index1 = Arrays.asList(select_month).indexOf(getEMonth);
                    getEMonthID = select_month_id[index1];
                    getCWS = employment.getCurrently_work_here();
                    getSalary = employment.getSalary();
                    if (getCWS.equalsIgnoreCase("1")) {
                        Format formatter = new SimpleDateFormat("MMM");
                        getEMonth = formatter.format(new Date());
                        samecomp.setChecked(true);
                    } else {
                        samecomp.setChecked(false);
                    }
                    // role
                    roleList = new ArrayList<>();
                    roleList = gson.fromJson(responseObj.getString("role_name"), new
                            TypeToken<ArrayList<Role>>() {
                            }.getType());
                    // industry
                    industriesList = new ArrayList<>();
                    industriesList = gson.fromJson(responseObj.getString("industries"), new
                            TypeToken<ArrayList<Industry>>() {
                            }.getType());
                    js_emp_edit_role_lay.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (roleList != null && roleList.size() > 0) {
                                RoleAlert();
                            }
                        }
                    });
                    if (getIndustry != null && !getIndustry.isEmpty() && !getIndustry.equalsIgnoreCase(getString(R.string.selectindustry))) {
                        industry.setText(getIndustry);
                        if (!languages.equalsIgnoreCase("English")) {
                            setIndustryLocalLang();
                        }
                    } else {
                        industry.setText(R.string.selectindustry);
                    }
                    if (getRole != null && !getRole.isEmpty() && !getRole.equalsIgnoreCase(getString(R.string.selectrole))) {
                        role.setText(getRole);
                        if (!languages.equalsIgnoreCase("English")) {
                            setRoleLocalLang();
                        }
                    } else {
                        role.setText(R.string.selectrole);
                    }
                    if (getSYear != null && !getSYear.isEmpty() && !getSYear.equalsIgnoreCase(getString(R.string.year))) {
                        start_year.setText(getSYear);
                    } else {
                        start_year.setText(R.string.year);
                    }
                    if (getEYear != null && !getEYear.isEmpty() && !getEYear.equalsIgnoreCase(getString(R.string.year))) {
                        end_year.setText(getEYear);
                    } else {
                        end_year.setText(R.string.year);
                    }
                    if (getSMonth != null && !getSMonth.isEmpty() && !getSMonth.equalsIgnoreCase(getString(R.string.month))) {
                        start_month.setText(getSMonth);
                    } else {
                        start_month.setText(R.string.month);
                    }
                    if (getEMonth != null && !getEMonth.isEmpty() && !getEMonth.equalsIgnoreCase(getString(R.string.month))) {
                        end_month.setText(getEMonth);
                    } else {
                        end_month.setText(R.string.month);
                    }
                } catch (Exception e) {
                    Toast.makeText(MyProfile_EDIT_Employment.this, getString(R.string.errortoparse), Toast
                            .LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MyProfile_EDIT_Employment.this, getString(R.string.connectionfailure),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            if (pg != null && pg.isShowing())
                pg.dismiss();
        }
    }
}
