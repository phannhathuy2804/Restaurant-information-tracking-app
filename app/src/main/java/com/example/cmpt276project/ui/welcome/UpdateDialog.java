/*
    This is the UpdateDialog class implementation.
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/22

    The reference of the implementation of Dialog is from the YouTuber (Coding in Flow).

    Citation
    1. YouTuber (Coding in Flow) - https://www.youtube.com/watch?v=r_87U6oHLFc
 */
// Package
package com.example.cmpt276project.ui.welcome;

// Import
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cmpt276project.R;

// UpdateDialog Class
public class UpdateDialog extends AppCompatDialogFragment {

    private YesClickedListener yesClickedListener;
    private CancelClickedListener cancelClickedListener;

    //----------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.update_dialog_data_update)
                .setMessage(R.string.update_dialog_message)
                .setPositiveButton(R.string.update_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yesClickedListener.updateDialogYesClicked();
                    }
                })
                .setNegativeButton(R.string.update_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelClickedListener.updateDialogCancelClicked();
                    }
                });
        return builder.create();
    }

    //----------------------------------------------------------------------------------------------
    public interface YesClickedListener {
        void updateDialogYesClicked();
    }

    public interface CancelClickedListener {
        void updateDialogCancelClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        yesClickedListener = (YesClickedListener) context;
        cancelClickedListener = (CancelClickedListener) context;
    }
}
