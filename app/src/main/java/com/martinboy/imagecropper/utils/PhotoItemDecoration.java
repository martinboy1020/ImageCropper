package com.martinboy.imagecropper.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PhotoItemDecoration extends RecyclerView.ItemDecoration {
    private int leftRight;
    private int topBottom;

    public PhotoItemDecoration(int leftRight, int topBottom) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int spanCount = layoutManager.getSpanCount();
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            //判斷是否在第一排
            if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排的需要top
                outRect.top = topBottom;
            }
            outRect.bottom = topBottom;
            //這裡忽略和合併項的問題，只考慮佔滿和單一的問題
            if (lp.getSpanSize() == spanCount) {
                //佔滿
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * leftRight);
                outRect.right = (int) (((float) leftRight * (spanCount + 1) / spanCount) - outRect.left);
            }
        } else {
            if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排需要left
                outRect.left = leftRight;
            }
            outRect.right = leftRight;
            //這裡忽略和合併項的問題，只考慮佔滿和單一的問題
            if (lp.getSpanSize() == spanCount) {
                //佔滿
                outRect.top = topBottom;
                outRect.bottom = topBottom;
            } else {
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * topBottom);
                outRect.bottom = (int) (((float) topBottom * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }
}