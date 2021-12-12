package com.example.app_making;

import android.view.View;

public interface OnProductItemClickListener {
    public void onItemClick(ProductAdapter.ViewHolder holder, View view, int position);
}
