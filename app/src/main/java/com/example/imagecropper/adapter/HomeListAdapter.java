package com.example.imagecropper.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagecropper.R;
import com.example.imagecropper.activity.HomeActivity;
import com.example.imagecropper.custom_ui.HomeItem;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    private HomeActivity mAct;
    private String[] list;

    public HomeListAdapter(HomeActivity act) {
        this.mAct = act;
        this.list = act.getResources().getStringArray(R.array.home_list_text);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new HomeItem(mAct));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.mHomeItem.setItemText(list[holder.getAdapterPosition()]);

        if(holder.getAdapterPosition() == 0) {
            holder.mHomeItem.setItemImage(R.drawable.ic_crop);
        } else if (holder.getAdapterPosition() == 1) {
            holder.mHomeItem.setItemImage(R.drawable.ic_file_upload);
        } else if (holder.getAdapterPosition() == 2) {
            holder.mHomeItem.setItemImage(R.drawable.ic_history);
        } else {
            holder.mHomeItem.setItemImage(R.drawable.ic_clear_all);
        }

        holder.mHomeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAct.goToOtherPage(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        HomeItem mHomeItem;

        ViewHolder(View itemView) {
            super(itemView);
            mHomeItem = (HomeItem) itemView;
        }
    }
}