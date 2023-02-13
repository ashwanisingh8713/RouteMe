package com.route.alert;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


public class ErrorDialog extends DialogFragment {

    public ErrorDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ErrorDialog newInstance(String title, String message) {
        ErrorDialog frag = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        if(message != null && message.contains("Failed to connect to")) {
            message = "Failed to connect to server.";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok", (dialog1, which) -> {
            getActivity().finish();
        });
        Dialog dialog = alertDialogBuilder.create();


        return dialog;
    }

    public static void showErrorDialog(FragmentManager fm, String title, String message) {
        if(message != null) {
            if(message.contains("Unable to resolve host")) {
                message = "Please check your internet connectivity.";
                title = "Connection Error!";
            }
        }
        ErrorDialog calendarViewDialogFragment = ErrorDialog.newInstance(title, message);
        calendarViewDialogFragment.show(fm, "errorDialog");
    }
}
