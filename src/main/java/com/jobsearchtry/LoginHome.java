package com.jobsearchtry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jobsearchtry.utils.GlobalData;

public class LoginHome extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginhome);
		ImageView jobseekar = (ImageView) findViewById(R.id.jobseekar);
		ImageButton lh_h = (ImageButton) findViewById(R.id.js_r_h);
		lh_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalData.pageback = "Home";
				//GlobalData.backfrom = "Home";
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(LoginHome.this);
				Editor editor = sharedPreferences.edit();
				editor.putString("PAGEBACK", GlobalData.pageback);
				//editor.putString("BACKFrom", GlobalData.backfrom);
				editor.apply();
				startActivity(new Intent(LoginHome.this, Homepage.class));
				finish();
			}
		});
		ImageButton back = (ImageButton) findViewById(R.id.js_r_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		ImageView employer = (ImageView) findViewById(R.id.employer);
		jobseekar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalData.loginfrom = "Jobseekar";
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(LoginHome.this);
				Editor editor = sharedPreferences.edit();
				editor.putString("LOGINFROM", GlobalData.loginfrom);
				editor.apply();
				startActivity(new Intent(LoginHome.this, Login.class));
			}
		});
		employer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalData.loginfrom = "Employer";
				SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(LoginHome.this);
				Editor editor = sharedPreferences.edit();
				editor.putString("LOGINFROM", GlobalData.loginfrom);
				editor.apply();
				startActivity(new Intent(LoginHome.this, Login.class));
			}
		});
	}
}
