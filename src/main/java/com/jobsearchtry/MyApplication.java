package com.jobsearchtry;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "", // will not be used
		formUri = "http://mandrillapp.com/api/1.0/messages/send.json"
)
//@ReportsCrashes(formKey = "", mailTo = "mercy.krishnaveni@gmail.com;krishnaveni44@yahoo.com",
// mode = ReportingInteractionMode.DIALOG)
public class MyApplication extends MultiDexApplication {
	private ReportsCrashes mReportsCrashes;

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		JsonSender mCrashErrorReporter = new JsonSender();
		mCrashErrorReporter.getInstance();
		// Activate the ErrorReporter
		mCrashErrorReporter.Init(getApplicationContext());
		mCrashErrorReporter.CheckCrashErrorAndSendMail(getApplicationContext());
	}
}

