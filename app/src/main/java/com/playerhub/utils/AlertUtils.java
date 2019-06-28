package com.playerhub.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.playerhub.listener.OnDialogListener;

public final class AlertUtils {


    public static void showDialog(Context context, final DialogInterface.OnClickListener okListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure want to clear all notification?");

        builder.setPositiveButton("YES", okListener);

        builder.setNegativeButton("NO", null);

        AlertDialog alert = builder.create();
        alert.show();


    }


}
