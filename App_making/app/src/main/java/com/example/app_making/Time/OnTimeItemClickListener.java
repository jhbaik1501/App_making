package com.example.app_making.Time;

import android.view.View;

import com.example.app_making.CustomerAdapter;

public interface OnTimeItemClickListener {
    public void onItemClick(TimeAdapter.ViewHolder holder, View view, int position);
}
