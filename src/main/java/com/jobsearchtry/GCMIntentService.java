package com.jobsearchtry;

import static com.jobsearchtry.utils.GlobalData.SENDER_ID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.jobsearchtry.utils.GlobalData;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	String regId;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		ServerUtilities.register(context, GlobalData.login_status,
				registrationId, "Y");
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// CommonUtils.displayMessage(context, "Unregistered");
		ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);
		String message = null;
		if (intent.getExtras().getString("status") != null) {
			String messaged = intent.getExtras().getString("status");
			String messagesplit[] = messaged.split(",", 2);
			message = messagesplit[1];
			GlobalData.jobid = messagesplit[0];
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor editor = sharedPreferences.edit();
			editor.putString("JOB_ID", GlobalData.jobid);
			editor.commit();
		}
		GlobalData.displayMessage(context, message);
		if (!GlobalData.login_status.equals("No user found")) {
			generateNotification(context, message);
		}

	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		// String message = getString(R.string.gcm_deleted, total);
		// displayMessage(context, message);
		// generateNotification(context,message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_recoverable_error,
		// errorId));
		return super.onRecoverableError(context, errorId);
	}

	@SuppressLint("NewApi")
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		//Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		GlobalData.pagefrom = "Notification";
		Intent notificationIntent = new Intent(context, JobSeeker_Detail.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		//notification.setLatestEventInfo(context, title, message, intent);
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
		Notification.Builder builder = new Notification.Builder(context);

		builder.setAutoCancel(false);
		//builder.setTicker("this is ticker text");
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setSmallIcon(icon);
		builder.setContentIntent(intent);
		builder.setOngoing(true);
		// builder.setSubText("This is subtext...");   //API level 16
		builder.setNumber(100);
		builder.build();

		//notification = builder.getNotification();
		notificationManager.notify(0,  builder.build());

	}

}
