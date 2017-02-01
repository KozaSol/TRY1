package com.jobsearchtry.utils;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by Administrator on 11/21/2016.
 */
public class MyToast extends Toast {
    public MyToast(Context context) {
        super(context);
        //init(null);
    }

    public void setDuration(int duration) {
        super.setDuration(10000);
        duration = 10000;
    }

    public void getDuration(int duration) {
        Log.e("duration", " " + duration);
    }

    /*public static MyToast makeText(Context context, String text, int duration) {
        //   Log.e("duration", " " + context.getPackageName());
        // throw new RuntimeException("Stub!");
        Toast.makeText(context,
                text,
                10000).show();
        MyToast toast = MyToast.makeText(context, text, duration);
        //toast.show();
        return toast;
    }*/

    public void show() {
        super.show();
        super.show();
    }
}
