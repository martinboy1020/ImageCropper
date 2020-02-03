package com.martinboy.imagecropper.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.martinboy.imagecropper.R;

import androidx.annotation.Nullable;

public class HomeItem extends LinearLayout {

    private ImageView itemImage;
    private TextView itemText;

    public HomeItem(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_home_list, this);
        itemImage = findViewById(R.id.item_image);
        itemText = findViewById(R.id.item_text);
    }

    public HomeItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_home_list, this);
    }

    public HomeItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_home_list, this);
    }

    public void setItemImage(int resId) {
        itemImage.setBackgroundResource(resId);
    }

    public void setItemText(String text) {
        itemText.setText(text);
    }

}
