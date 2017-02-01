package com.jobsearchtry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.WindowManager;
import com.jobsearchtry.utils.GlobalData;
import android.view.Gravity;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;

public class ChooseLanguages {
    String[] languages = {"English", "தமிழ்"};
    String[] languageseng = {"English", "Tamil"};
    String selectedLanguages, languagename;
    int indexlang = -1;

    public void selectLanguagesPopup(final Context context) {
        languagename = context.getResources().getConfiguration().locale.getDisplayLanguage();
        final Dialog alertDialog = new Dialog(context,R.style.MyThemeDialog);
        View emppromptView = View.inflate(context, R.layout.choose_languages, null);
        final Button languagesdone = (Button) emppromptView.findViewById(R.id.languages_submit);
        final ListView filterquali = (ListView) emppromptView.findViewById(R.id.languageslist);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.languages_listrow, languages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.languages_listrow, parent, false);
                }
                TextView textView = (TextView) v.findViewById(R.id.languagesname);
                String yourValue = languages[position];
                for (int i = 0; i < languages.length; i++) {
                    if (languageseng[i].equals(languagename)) {
                        indexlang = i;
                        selectedLanguages = languages[i];
                    }
                }
                if (indexlang != -1 && (indexlang == position)) {
                    textView.setBackgroundResource(R.drawable.border_lang_select);
                    textView.setTextColor(Color.parseColor("#0093b6"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#fdc201"));
                    textView.setTextColor(Color.parseColor("#3f3f3f"));
                }
                textView.setText(yourValue);
                return textView;
            }
        };
        filterquali.setAdapter(adapter);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setContentView(emppromptView);
        alertDialog.show();
        filterquali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (indexlang != -1 && (indexlang == position)) {
                    selectedLanguages = context.getString(R.string.selectt);
                    languagename = context.getString(R.string.selectt);
                    indexlang = -1;
                } else {
                    indexlang = position;
                    selectedLanguages = languages[position];
                    languagename = languageseng[position];
                }
                adapter.notifyDataSetChanged();
            }
        });
        languagesdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLanguages != null && !selectedLanguages.isEmpty() && !selectedLanguages.equalsIgnoreCase(context.getString(R.string.selectt))) {
                    Resources res = context.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    if (selectedLanguages.equalsIgnoreCase("English")) {
                        conf.locale = new Locale("en");
                    } else {
                        conf.locale = new Locale("ta");
                    }
                    GlobalData.getLanguageName = conf.locale.getDisplayLanguage();
                    res.updateConfiguration(conf, dm);
                    GlobalData.LandRefresh = "Home";
                    GlobalData.islocationAvail = "No";
                    GlobalData.getjsfilterdata = null;
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("LANGNAME", GlobalData.getLanguageName);
                    editor.apply();
                    if (context.getClass().getSimpleName().equalsIgnoreCase("LauncherActivity")) {
                        ((LauncherActivity) context).finish();
                        Intent i = new Intent(context, Homepage.class);
                        context.startActivity(i);
                    } else if (context.getClass().getSimpleName().equalsIgnoreCase("Homepage")) {
                        Intent i = new Intent(context, Homepage.class);
                        context.startActivity(i);
                    } else {
                        Intent i = new Intent(context, EmployerDashboard.class);
                        context.startActivity(i);
                    }
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.chooselanguageverify), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}