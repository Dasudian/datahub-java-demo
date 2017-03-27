package com.dasudian.iot_datahub_sdk_demo_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class Util {
	private static Toast toast;

	public static void showToast(Context context, String content) {
		if (toast == null) {
			toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		} else {
			toast.setText(content);
		}
		toast.show();
	}

	public static void saveUserInfo(Context context, String userId) {
		SharedPreferences sp = context.getSharedPreferences("userInfo",
				android.content.Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString("userId", userId);
		ed.commit();
	}

	public static String getUserInfo(Context context) {
		SharedPreferences sp = context.getSharedPreferences("userInfo",
				android.content.Context.MODE_PRIVATE);
		return sp.getString("userId", "");
	}

	private static ProgressDialog progressDialog;
	public static void showProgressDialog(Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
	//		progressDialog.setTitle("");
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	
	public static void disMissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = null;
	}
}
