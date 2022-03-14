package com.app.smartwatchapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartwatchapp.Activities.HomeScreen;
import com.app.smartwatchapp.App;
import com.app.smartwatchapp.Listeners.SmartWatchListeners;
import com.app.smartwatchapp.Models.Watch;
import com.app.smartwatchapp.R;
import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterWatch extends RecyclerView.Adapter<AdapterWatch.ViewHolder> {
    List<Watch> watchList = new ArrayList<>();
    Context context;
    CRPBleClient mBleClient;
    public static CRPBleDevice mBleDevice;
    public static CRPBleConnection mBleConnection;
    ProgressDialog progressDialog;
    private int CONNECTION_STATE;
    public AdapterWatch(Context context) {
        this.context = context;
    }
    SendState sendState;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_watch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Watch watch = watchList.get(position);
        if (watch.getWatchName() != null) {
            holder.tvWatchName.setText(watch.getWatchName());
        }
        else {
            holder.tvWatchName.setText("Name not found");

        }
        holder.tvWatchMACAddress.setText(watch.getWatchMACAddress());
        holder.itemView.setOnClickListener(view -> {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Connecting to " + watch.getWatchName());
            progressDialog.setCancelable(false);
            mBleClient = App.getBleClient(context);
            mBleDevice = mBleClient.getBleDevice(watch.getWatchMACAddress());
            if (mBleDevice != null && !mBleDevice.isConnected()) {
                connect(watch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return watchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWatchName;
        TextView tvWatchMACAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWatchName = itemView.findViewById(R.id.tv_watch_name);
            tvWatchMACAddress = itemView.findViewById(R.id.tv_watch_mac_address);
        }
    }
    public void setData(List<Watch> watchList) {
        this.watchList = watchList;
        notifyDataSetChanged();
    }

    public void connect(Watch watch) {
        progressDialog.show();
        mBleConnection = mBleDevice.connect();
        mBleConnection.setConnectionStateListener(new CRPBleConnectionStateListener() {
            @Override
            public void onConnectionStateChange(int newState) {
                switch (newState) {
                    case CRPBleConnectionStateListener.STATE_CONNECTED:
                        CONNECTION_STATE = CRPBleConnectionStateListener.STATE_CONNECTED;
                        progressDialog.dismiss();
                        sendState.changeState(CONNECTION_STATE, watch);
                        break;
                    case CRPBleConnectionStateListener.STATE_CONNECTING:
                        CONNECTION_STATE = CRPBleConnectionStateListener.STATE_CONNECTING;
                        break;
                    case CRPBleConnectionStateListener.STATE_DISCONNECTED:
                        CONNECTION_STATE = CRPBleConnectionStateListener.STATE_DISCONNECTED;
                        progressDialog.dismiss();
                        break;
                }
            }
        });
    }

    public interface SendState {
        void changeState(int state, Watch watch);
    }

    public void setChangeStateCallback(SendState sendState) {
        this.sendState = sendState;
    }
}
