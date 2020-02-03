package com.martinboy.imagecropper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martinboy.imagecropper.R;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BottomDialog extends DialogFragment {

    private Activity mAct;
    private static final Class clz = DialogFragment.class;

    public static String TAG = BottomDialog.class.getSimpleName();
    private static BottomDialog dialog;

    public static BottomDialog instance() {
        if (dialog == null) {
            synchronized (BottomDialog.class) {
                if (dialog == null) {
                    dialog = new BottomDialog();
                }
            }
        }
        return dialog;
    }

    private View rootView;
    private String dialog_title, dialog_content, btn_positive_text, btn_negative_text;
    private int imageRes = -1;
    private TextView btn_positive, btn_negative, text_title, text_content;
    private ImageView img_warning;
    private View.OnClickListener setPositiveClick, setNegativeClick;
    private boolean backKeyEnable = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        // For DialogFragment To Gravity Bottom and Width MatchParent
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = getDialog().getWindow();
//        assert window != null;
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.gravity = Gravity.BOTTOM;
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);

        rootView = inflater.inflate(R.layout.bottom_dialog, container, false);
        img_warning = (ImageView) rootView.findViewById(R.id.img_warning);
        text_title = (TextView) rootView.findViewById(R.id.text_title);
        text_content = (TextView) rootView.findViewById(R.id.text_content);
        btn_positive = (TextView) rootView.findViewById(R.id.btn_positive);
        btn_negative = (TextView) rootView.findViewById(R.id.btn_negative);
        if(imageRes != -1) {
            img_warning.setBackgroundResource(imageRes);
        }
        text_title.setText(dialog_title);
        text_content.setText(dialog_content);
        btn_positive.setText(btn_positive_text);
        btn_negative.setText(btn_negative_text);
        btn_positive.setOnClickListener(setPositiveClick);
        btn_negative.setOnClickListener(setNegativeClick);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (backKeyEnable) {
                        getDialog().dismiss();
                    }

                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        // Remove default dialog frame, use custom layout
//        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View parent = (View) rootView.getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setWarningDialog(Activity mAct,
                                 int imageRes,
                                 String dialog_title,
                                 String dialog_content,
                                 String btn_positive_text,
                                 String btn_negative_text,
                                 View.OnClickListener setPositiveClick,
                                 View.OnClickListener setNegativeClick) {
        this.mAct = mAct;
        this.imageRes = imageRes;
        this.dialog_title = dialog_title;
        this.dialog_content = dialog_content;
        this.btn_positive_text = btn_positive_text;
        this.btn_negative_text = btn_negative_text;
        this.setPositiveClick = setPositiveClick;
        this.setNegativeClick = setNegativeClick;
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {

        if (getDialog() != null && getDialog().isShowing()) {
            // dialog is showing
        } else {
            //mDismissed = false;
            try {
                Field dismissed = clz.getDeclaredField("mDismissed");
                dismissed.setAccessible(true);
                dismissed.set(this, false);
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }
            //mShownByMe = true;
            try {
                Field shown = clz.getDeclaredField("mShownByMe");
                shown.setAccessible(true);
                shown.set(this, true);
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }

            FragmentTransaction ft = manager.beginTransaction();
            ft.add(BottomDialog.this, tag);
            ft.commitAllowingStateLoss();
        }

    }

}
