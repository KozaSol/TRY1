package com.jobsearchtry.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import android.util.Log;
import java.util.concurrent.TimeUnit;

import android.os.CountDownTimer;

/**
 * Created by Administrator on 11/19/2016.
 */
public class DisplayToastMessage {
    public boolean isToastMessage(Context context, String getmessage) {
        final Toast toast = Toast.makeText(context,
                getmessage,
                Toast.LENGTH_LONG);
        toast.show();
        Log.e("length", "" + getmessage.length());
        if (getmessage.length() > 55) {
            new CountDownTimer(6500, 1000) {
                public void onTick(long millisUntilFinished) {
                    toast.show();
                }
                public void onFinish() {
                    toast.cancel();
                }
            }.start();
        } else {
            new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                    toast.show();
                }
                public void onFinish() {
                    toast.cancel();
                }
            }.start();
        }
        return false;
    }
}
