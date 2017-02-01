package com.jobsearchtry.receiver;

/**
 * Created by Administrator on 12/20/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import com.jobsearchtry.Homepage;

public class NetworkUpdateReceiver extends BroadcastReceiver {

    Context context;

    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        if (new UtilService().isNetworkAvailable(context)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((context));
            GlobalData.changenetwork = sharedPreferences.getString("CHNET", GlobalData.changenetwork);
            if (GlobalData.changenetwork.equalsIgnoreCase("No")) {
               // new Handler(Homepage.callbackrefresh).sendEmptyMessage(0);
            }
        }
    }
}
