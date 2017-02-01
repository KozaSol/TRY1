package com.jobsearchtry.services;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.jobsearchtry.Homepage;
import com.jobsearchtry.utils.GlobalData;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotifiCountService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!(GlobalData.login_status.equals("No user found"))) {
			new getPendingStatus().execute();
		} else {
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private class getPendingStatus extends AsyncTask<String, String, String> {
		String pendingresponse;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			RequestBody formBody = new FormBody.Builder()
					.add("action", "CountNotification")
					.add("jobseeker_id", GlobalData.login_status).build();
			Request request = new Request.Builder()
					.url(GlobalData.url + "updatenotification.php")
					.post(formBody).build();
			OkHttpClient client = new OkHttpClient();
			try {
				Response response;
				response = client.newCall(request).execute();
				pendingresponse = response.body().string();
			} catch (IOException ignored) {
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			super.onPostExecute(file_url);
			if (pendingresponse != null && !pendingresponse.contains("connectionFailure")) {
				try {
					JSONObject responseObj = new JSONObject(pendingresponse);
					GlobalData.getCount = responseObj.getString("count");
					if (!GlobalData.getCount.equalsIgnoreCase(null)) {
						new Handler(Homepage.callbacknotify).sendEmptyMessage(0);
					}
				} catch (Exception ignored) {
				}
			}
		}
	}
}
