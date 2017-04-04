package com.ekumenlabs.test.peer2peertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements WiFiDirectBroadcastReceiver.P2pListener, WifiP2pManager.ActionListener {
  public static final String TAG = MainActivity.class.getSimpleName();
  private WifiP2pManager mManager;
  private WifiP2pDeviceList mPeerList;
  private WifiP2pManager.Channel mChannel;
  private BroadcastReceiver mReceiver;
  private final IntentFilter mIntentFilter = new IntentFilter();
  private Button mBtn;
  private TextView mText;

  private long mStartTime = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    mChannel = mManager.initialize(this, getMainLooper(), null);

    mText = (TextView) findViewById(R.id.textview);

    mBtn = (Button) findViewById(R.id.btn_peers);
    mBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        btnClick();
      }
    });
  }

  private void btnClick() {
    mStartTime = System.currentTimeMillis();
    mManager.discoverPeers(mChannel, this);
  }

  @Override
  public void onResume() {
    super.onResume();
    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
    registerReceiver(mReceiver, mIntentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(mReceiver);
  }

  @Override
  public void p2pStatus(boolean status) {
    Log.d(TAG, "P2P Status: " + status);
  }

  @Override
  public void peersObtained(WifiP2pDeviceList peersList) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("P2P devices:\n");
    for (WifiP2pDevice device : peersList.getDeviceList()) {
      Log.d(TAG, "P2P List: " + device.deviceName);
      stringBuilder.append(device.deviceName).append("\n");
    }
    mText.setText(stringBuilder.toString());
    long time = System.currentTimeMillis() - mStartTime;
    Log.d(TAG, "Time: " + time);
  }

  @Override
  public void onSuccess() {
    Log.d(TAG, "Success! Send a broadcast");
  }

  @Override
  public void onFailure(int reason) {
    Log.d(TAG, "Failed because of " + reason);
  }
}
