package com.jobsearchtry.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoStartService extends Service {

	public IBinder onBind(Intent in) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {
			startService(new Intent(getBaseContext(), ResponseCountService.class));
		} catch (Exception ignored) {
		}
		stopSelf();

		return super.onStartCommand(intent, flags, startId);
	}

}
