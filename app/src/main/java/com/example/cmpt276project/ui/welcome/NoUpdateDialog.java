/*
    This is the NoUpdateDialog class implementation.
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

// NoUpdateDialog Class
public class NoUpdateDialog extends AppCompatDialogFragment {

    private YesClickedListener yesClickedListener;

    //----------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.no_update_dialog_no_update_required)
                .setMessage(R.string.no_update_dialog_message)
                .setPositiveButton(R.string.no_update_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yesClickedListener.noUpdateDialogYesClicked();
                    }
                });
        return builder.create();
    }

    //----------------------------------------------------------------------------------------------
    public interface YesClickedListener {
        void noUpdateDialogYesClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        yesClickedListener = (YesClickedListener) context;
    }
}
