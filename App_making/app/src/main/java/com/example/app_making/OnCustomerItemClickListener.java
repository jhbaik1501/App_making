package com.example.app_making;

import android.view.View;

public interface OnCustomerItemClickListener {
    public void onItemClick(CustomerAdapter.ViewHolder holder, View view, int position);
}
