package com.example.bluetoothex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Vector;

public class ScanAdapter extends BaseAdapter {

    private Vector<Scan> scans;
    private LayoutInflater layoutInflater;

    public ScanAdapter(Vector<Scan> scans, LayoutInflater layoutInflater) {
        this.scans = scans;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return scans.size();
    }

    @Override
    public Object getItem(int position) {
        return  scans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScansHolder scansHolder;
        if (convertView == null) {
            scansHolder = new ScansHolder();
            convertView = layoutInflater.inflate(R.layout.row_scan, parent, false);
            scansHolder.name = convertView.findViewById(R.id.textview_name);
            scansHolder.address = convertView.findViewById(R.id.textview_address);
            scansHolder.rssi = convertView.findViewById(R.id.textview_rssi);
            scansHolder.UUID = convertView.findViewById(R.id.textview_uuid);
            convertView.setTag(scansHolder);
        } else {
            scansHolder = (ScansHolder) convertView.getTag();
        }

        scansHolder.name.setText("Name: " + scans.get(position).getName());
        scansHolder.address.setText("Address: " + scans.get(position).getAddress());
        scansHolder.rssi.setText("Rssi: " + scans.get(position).getRssi());
        scansHolder.UUID.setText("UUID: " + scans.get(position).getUuid());

        return convertView;
    }

    private class ScansHolder {
        TextView name;
        TextView address;
        TextView rssi;
        TextView UUID;
    }

    public String getAddress(int pos) {
        return scans.get(pos).getAddress();
    }

}