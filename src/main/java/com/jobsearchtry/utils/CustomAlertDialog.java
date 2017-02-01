package com.jobsearchtry.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jobsearchtry.EmployerDashboard;
import com.jobsearchtry.JobsPosted;
import com.jobsearchtry.PostJobFinalSubmit;
import com.jobsearchtry.Edit_Job;
import com.jobsearchtry.R;

public class CustomAlertDialog {
    private Dialog alertDialog;

    public void isDisplayMessage(final Activity a, String getmessage) {
        final Dialog alertD = new Dialog(a,R.style.MyThemeDialog);
        alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertD.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(a,
                R.layout.custom_singlealertdialog, null);
        TextView f_popupheader = (TextView) emppromptView
                .findViewById(R.id.custompopupheader);
        f_popupheader.setText(R.string.popupheadersinglealert);
        TextView f_popupsubheader = (TextView) emppromptView
                .findViewById(R.id.custom_popup_message);
        f_popupsubheader.setText(getmessage);
        Button yes = (Button) emppromptView.findViewById(R.id.custom_popup);
        alertD.setContentView(emppromptView);
        alertD.show();
        //if yes exit the page
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (a.getClass().getSimpleName().equalsIgnoreCase("PostJobFinalSubmit")) {
                    a.startActivity(new Intent(a,
                            EmployerDashboard.class));
                    ((PostJobFinalSubmit) a).finish();
                }
                if (a.getClass().getSimpleName().equalsIgnoreCase("Edit_Job")) {
                    if (GlobalData.frompostjob.equalsIgnoreCase("PostJob")) {
                        a.startActivity(new Intent(a, PostJobFinalSubmit.class));
                    } else {
                        a.startActivity(new Intent(a, JobsPosted.class));
                    }
                    ((Edit_Job) a).finish();
                }
                alertD.dismiss();
            }
        });
    }
}
