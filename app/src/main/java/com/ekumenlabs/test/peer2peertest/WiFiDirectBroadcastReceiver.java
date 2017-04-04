package com.ekumenlabs.test.peer2peertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by ecorbellini on 3/20/17.
 */

/**
 * This class receives the broadcasts and calls the corresponding methods from it's listeners.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = WiFiDirectBroadcastReceiver.class.getSimpleName();
  private WifiP2pManager mManager;
  private WifiP2pManager.Channel mChannel;
  private P2pListener mListener;

  public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                     P2pListener listener) {
    super();
    mManager = manager;
    mChannel = channel;
    mListener = listener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();

    if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
      // WIFI_P2P_STATE_CHANGED_ACTION intent is broadcasted whenever the WiFi p2p is enabled or
      // disabled.
      int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
          WifiP2pManager.WIFI_P2P_STATE_DISABLED);
      callStatus(WifiP2pManager.WIFI_P2P_STATE_ENABLED == state);
    } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
      // Call WifiP2pManager.requestPeers() to get a list of current peers
      WifiP2pDeviceList peersList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
      callPeers(peersList);
    } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
      // Respond to new connection or disconnections
      Log.d(TAG, "Connection changed action!");
    } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
      // Respond to this device's wifi state changing
      Log.d(TAG, "This device's connection changed action!");
    }
  }

  private void callStatus(final boolean status) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Receiver Status: " + status);
        mListener.p2pStatus(status);
      }
    }).run();
  }

  private void callPeers(final WifiP2pDeviceList list) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        mListener.peersObtained(list);
      }
    }).run();
  }

  public interface P2pListener {
    void p2pStatus(boolean status);
    void peersObtained(WifiP2pDeviceList peersList);
  }
}
