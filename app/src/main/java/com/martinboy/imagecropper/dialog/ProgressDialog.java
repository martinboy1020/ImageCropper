package com.martinboy.imagecropper.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martinboy.imagecropper.R;

public class ProgressDialog extends DialogFragment {

    public static String TAG = ProgressDialog.class.getSimpleName();
    private static ProgressDialog dialog;

    public static ProgressDialog instance() {
        if (dialog == null) {
            synchronized (ProgressDialog.class) {
                if (dialog == null) {
                    dialog = new ProgressDialog();
                }
            }
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}