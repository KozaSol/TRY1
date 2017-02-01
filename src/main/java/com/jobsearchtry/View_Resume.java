package com.jobsearchtry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;
import com.jobsearchtry.utils.GlobalData;

public class View_Resume extends Activity {
	//view the resume
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewresume);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		ImageButton vr_h = (ImageButton) findViewById(R.id.js_r_h);
		vr_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalData.pageback = "Home";
				//GlobalData.backfrom = "Home";
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(View_Resume.this);
				Editor editor = sharedPreferences.edit();
				editor.putString("PAGEBACK", GlobalData.pageback);
				//editor.putString("BACKFrom", GlobalData.backfrom);
				editor.apply();
				startActivity(new Intent(View_Resume.this, Homepage.class));
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
		WebView fullview = (WebView) findViewById(R.id.viewresumefile);
		String getresume = GlobalData.url + GlobalData.resume;
		fullview.setWebChromeClient(new WebChromeClient());
		final Activity activity = this;
		fullview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				//noinspection deprecation
				activity.setProgress(progress * 1000);
			}
		});
		//noinspection deprecation
		fullview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String
					failingUrl) {
				Toast.makeText(activity, "Please wait, the remaining pages are being loaded", Toast
                        .LENGTH_SHORT).show();
			}

			@TargetApi(android.os.Build.VERSION_CODES.M)
			public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError
                    failingUrl) {
				Toast.makeText(activity, "Please wait, the remaining pages are being loaded"
                       , Toast.LENGTH_SHORT).show();
			}
		});
		fullview.getSettings().setJavaScriptEnabled(true);
		fullview.getSettings().setDomStorageEnabled(true);
		fullview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		String GoogleDocs = "http://docs.google.com/gview?embedded=true&url=";
		getresume = getresume.replace(" ","%20");
		fullview.loadUrl(GoogleDocs + getresume);
		fullview.setVisibility(View.VISIBLE);
	}
}
