package com.jobsearchtry.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.jobsearchtry.EmployerDashboard;
import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResponseCountService extends Service {
	private boolean serviceStopped;
	private Handler mHandler;
	private final Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			if (!serviceStopped) {
				if (!(GlobalData.emp_login_status.equals("No user found"))) {
					if (new UtilService()
							.isNetworkAvailable(getApplicationContext())) {
						new getPendingStatus().execute();
					}
				} else {
					stopSelf();
				}
			}
			queueRunnable();
		}
	};

	private void queueRunnable() {
		mHandler.postDelayed(updateRunnable, 5000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		serviceStopped = false;
		mHandler = new Handler();
		queueRunnable();
	}

	@Override
	public void onDestroy() {
		serviceStopped = true;
	}

	@Override
	public void onStart(Intent intent, int startid) {
	}

	private class getPendingStatus extends AsyncTask<String, String, String> {
		String pendingresponse;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			RequestBody formBody = new FormBody.Builder()
					.add("action", "CountNotificationNew")
					.add("company_id", GlobalData.emp_login_status).build();
			Request request = new Request.Builder()
					.url(GlobalData.url + "updatenotification.php")
					.post(formBody).build();
			OkHttpClient client = new OkHttpClient();
			Response response;
			try {
				response = client.newCall(request).execute();
				pendingresponse = response.body().string();
			} catch (IOException ignored) {
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			super.onPostExecute(file_url);
			if (pendingresponse != null
					&& !pendingresponse.contains("connectionFailure")) {
				try {
					JSONObject responseObj = new JSONObject(pendingresponse);
					GlobalData.empNotiCount = responseObj.getString("count");
					if (!GlobalData.empNotiCount.equalsIgnoreCase(null)) {
						new Handler(EmployerDashboard.callback)
								.sendEmptyMessage(0);
					}
				} catch (Exception ignored) {
				}
			}
		}
	}
}
