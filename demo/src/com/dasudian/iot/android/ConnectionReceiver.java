package com.dasudian.iot.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.dasudian.iot.sdk.DataHubClient;

public class ConnectionReceiver extends BroadcastReceiver {

	private static DataHubClient client = null;
	public static NetworkInfo lastActiveNetworkInfo = null;
	public static WifiInfo lastWifiInfo = null;

	public static String TAG = "DataHub.ConnectionReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context == null || intent == null) {
			return;
		}
		Log.i(TAG, "ConnectionReceiver onReceive.");

		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;
		try {
			netInfo = mgr.getActiveNetworkInfo();
		} catch (Exception e) {
			Log.i(TAG, "getActiveNetworkInfo failed.");
		}

		checkConnInfo(context, netInfo);
	}

	public static void setDataHubClient(DataHubClient client) {
		ConnectionReceiver.client = client;
	}

	/**
	 * 在连接的网络变化时调用
	 * 
	 * @param context
	 */
	private static void onNetworkChange() {
		if (client != null && !client.isConnected()) {
			client.onNetworkChange();
		}
	}

	public void checkConnInfo(final Context context, final NetworkInfo activeNetInfo) {
		if (activeNetInfo == null) {
			lastActiveNetworkInfo = null;
			lastWifiInfo = null;
			Log.i(TAG, "checkConnInfo 1");
		} else if (activeNetInfo.getDetailedState() != NetworkInfo.DetailedState.CONNECTED) {
			lastActiveNetworkInfo = null;
			lastWifiInfo = null;
			Log.i(TAG, "checkConnInfo 2");
			if (isConnected(context)) {
				onNetworkChange();
			}
		} else {
			Log.i(TAG, "checkConnInfo 3");
			if (isNetworkChange(context, activeNetInfo)) {
				onNetworkChange();
			}
		}
	}

	public static boolean isConnected(Context context) {

		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = conMan.getActiveNetworkInfo();
		boolean connect = false;
		try {
			connect = activeNetInfo.isConnected();
		} catch (Exception e) {
		}
		return connect;
	}

	public boolean isNetworkChange(final Context context, final NetworkInfo activeNetInfo) {
		boolean isWifi = (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
		if (isWifi) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wi = wifiManager.getConnectionInfo();
			if (wi != null && lastWifiInfo != null && lastWifiInfo.getBSSID().equals(wi.getBSSID())
					&& lastWifiInfo.getSSID().equals(wi.getSSID()) && lastWifiInfo.getNetworkId() == wi.getNetworkId()) {
				Log.w(TAG, "Same Wifi, do not NetworkChanged");
				return false;
			}
			lastWifiInfo = wi;
		} else if (lastActiveNetworkInfo != null && lastActiveNetworkInfo.getExtraInfo() != null
				&& activeNetInfo.getExtraInfo() != null
				&& lastActiveNetworkInfo.getExtraInfo().equals(activeNetInfo.getExtraInfo())
				&& lastActiveNetworkInfo.getSubtype() == activeNetInfo.getSubtype()
				&& lastActiveNetworkInfo.getType() == activeNetInfo.getType()) {
			return false;
		} else if (lastActiveNetworkInfo != null && lastActiveNetworkInfo.getExtraInfo() == null
				&& activeNetInfo.getExtraInfo() == null
				&& lastActiveNetworkInfo.getSubtype() == activeNetInfo.getSubtype()
				&& lastActiveNetworkInfo.getType() == activeNetInfo.getType()) {
			Log.w(TAG, "Same Network, do not NetworkChanged");
			return false;
		}

		lastActiveNetworkInfo = activeNetInfo;

		return true;
	}

}
