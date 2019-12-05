package com.example.bluetoothex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Scan> mList;
    private LayoutInflater mInflater;

    public ScanAdapter(ArrayList<Scan> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mInflater = mInflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_scan, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView_number.setText(mList.get(position).getNumber());
        holder.textview_name.setText(mList.get(position).getName());
        holder.textview_address.setText(mList.get(position).getAddress());
        holder.textView_rssi.setText(mList.get(position).getRssi());
        holder.textView_UUID.setText(mList.get(position).getUuid());

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textview_name;
        TextView textview_address;
        TextView textView_rssi;
        TextView textView_UUID;
        TextView textView_number;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textView_number = itemView.findViewById(R.id.textview_number);
            this.textview_name = itemView.findViewById(R.id.textview_name);
            this.textview_address = itemView.findViewById(R.id.textview_address);
            this.textView_rssi = itemView.findViewById(R.id.textview_rssi);
            this.textView_UUID = itemView.findViewById(R.id.textview_uuid);


        }
    }

}