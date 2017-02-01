package com.jobsearchtry.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jobsearchtry.EmployerDashboard;
import com.jobsearchtry.Homepage;
import com.jobsearchtry.LauncherActivity;
import com.jobsearchtry.PostJob;
import com.jobsearchtry.R;

public class BackAlertDialog {
    private boolean getstatus = false;
    private Dialog alertDialog;

    public boolean isBackDialog(final Activity a) {
        alertDialog = new Dialog(a,R.style.MyThemeDialog);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        View emppromptView = View.inflate(a,
                R.layout.delete_popup, null);
        alertDialog.setContentView(emppromptView);
        TextView f_popupheader = (TextView) emppromptView
                .findViewById(R.id.d_popupheader);
        f_popupheader.setText(R.string.confirmation);
        TextView f_popupsubheader = (TextView) emppromptView
                .findViewById(R.id.d_popup_subheader);
        f_popupsubheader.setText(R.string.exitform);
        Button no = (Button) emppromptView.findViewById(R.id.d_no);
        Button yes = (Button) emppromptView
                .findViewById(R.id.d_yes);
        alertDialog.show();
        //if yes exit the page
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (a.getClass().getSimpleName().equalsIgnoreCase("PostJob")) {
                    ((PostJob) a).finish();
                    GlobalData.loginfrom = null;
                    a.startActivity(new Intent(a, EmployerDashboard.class));
                } else {
                    a.finish();
                }
                alertDialog.dismiss();
                getstatus = true;
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alertDialog.dismiss();
                getstatus = false;
            }
        });
        return getstatus;
    }
}
