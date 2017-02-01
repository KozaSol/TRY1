package com.jobsearchtry;

import static com.jobsearchtry.utils.GlobalData.SERVER_URL;
import static com.jobsearchtry.utils.GlobalData.TAG;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.content.Context;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.jobsearchtry.utils.GlobalData;


public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
    static void register(final Context context, String jobseeker_id, final String regId,String login_status) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("jobseeker_id", GlobalData.login_status);
        params.put("registration_id", regId);
        params.put("login_status", "Y");
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                return;
            } catch (IOException e) {
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                
            }
        }
        }
    static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            //String message = context.getString(R.string.server_unregistered);
            GlobalData.displayMessage(context, "Unregistered");
        } catch (IOException e) {
            //String message = context.getString(R.string.server_unregister_error,
            //        e.getMessage());
        	GlobalData.displayMessage(context, "Unregistration is failure");
        }
    }
    
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {   	
        
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
        	Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}
