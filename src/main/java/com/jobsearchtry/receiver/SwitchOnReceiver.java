package com.jobsearchtry.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jobsearchtry.services.AutoStartService;

public class SwitchOnReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		try {
			if ((intent.getAction() != null)
					&& (intent.getAction()
					.equals("android.intent.action.BOOT_COMPLETED"))) {
				context.startService(new Intent(context,
						AutoStartService.class));
			}
		} catch (Exception ignored) {
		}
	}
}
