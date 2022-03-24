package com.example.app_making.Time;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_making.Customer;
import com.example.app_making.OnCustomerItemClickListener;
import com.example.app_making.R;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder>
                            implements OnTimeItemClickListener {

    ArrayList<Time> items = new ArrayList<Time>();

    OnTimeItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.time_item, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Time item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Time item) {
        items.add(item);
    }

    public void setItems(ArrayList<Time> items) {
        this.items = items;
    }

    public Time getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Time item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(OnTimeItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {


        TextView textView3;
        TextView textView4;
        TextView textView;

        public ViewHolder(View itemView, final OnTimeItemClickListener listener) {
            super(itemView);


            textView3 = itemView.findViewById(R.id.textView_Name);
            textView4 = itemView.findViewById(R.id.textView_Time);
            textView = itemView.findViewById(R.id.textView_Rank);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(Time item) {

            textView3.setText(item.getName());
            textView4.setText(item.getTime());
            textView.setText( String.valueOf( item.getResId() ) );
        }

    }

}
