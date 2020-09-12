/*
    This is the UpdateDialog class implementation.
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/23

    Citation
    The reference of the Loading Dialog
    1. YouTuber (Stevdza-San) - Custom Loading Alert Dialog - Android Studio Tutorial
    https://www.youtube.com/watch?v=tccoRIrMyhU
 */

package com.example.cmpt276project.ui.welcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.cmpt276project.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog (Activity activity) {
        this.activity = activity;
    }

    void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog() {
        alertDialog.dismiss();
    }
}
