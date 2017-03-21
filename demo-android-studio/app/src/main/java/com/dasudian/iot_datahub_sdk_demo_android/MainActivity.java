package com.dasudian.iot_datahub_sdk_demo_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.dasudian.iot.sdk.ActionCallback;
import com.dasudian.iot.sdk.DataHubClient;
import com.dasudian.iot.sdk.Message;
import com.dasudian.iot.sdk.ServiceException;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	public static final String serverURL = "ssl://gary0755.oicp.net:25765";
//	public static final String instanceId = "dsd_9FmYSNiqpFmi69Bui0_A";// 测试instanceId，在正式使用时请联系大数点客服获取instanceId
//	public static final String instanceKey = "238f173d6cc0608a";// 测试instanceKey，在正式使用时请联系大数点客服获取instanceKey
	public static final String instanceId = "dsd_9IPsIAM3L8URamPFHk_A";
	public static final String instanceKey = "a3e31e6d183699b5";
	public static DataHubClient client;
	public static final int REQUEST_CODE = 2;
	private EditText et_topic;
	private EditText et_content;
	private EditText et_topic_publish;
	private ListView listView;
	private List<MyMessage> messages = new ArrayList<MyMessage>();
	private MessageAdapter adapter;
	public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		DF.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		createClient();
	}

	private void createClient() {
		String clientName = UUID.randomUUID().toString();
		String clientId = clientName;
		try {
			client = new DataHubClient.Builder(instanceId, instanceKey, clientName, clientId)
					.setServerURL(serverURL).setCallback(
					new MyCallback()).build();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// APP退出时断开连接
		if (client != null) {
			client.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		et_topic = (EditText) findViewById(R.id.et_topic);
		et_content = (EditText) findViewById(R.id.et_content);
		et_topic_publish = (EditText) findViewById(R.id.et_topic_publish);
		adapter = new MessageAdapter(this, R.layout.message_item, messages);
		listView = (ListView) findViewById(R.id.lv);
		listView.setAdapter(adapter);
	}

	/**
	 * 订阅一个topic
	 */
	public void subcrible(View v) {
		String topicName = et_topic.getText().toString();
		if (topicName == null || topicName.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				try {
					client.subscribe(params[0], 10);
					return true;
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					appendMessage("订阅成功");
				} else {
					appendMessage("订阅失败");
				}
			}
		}.execute(topicName);
	}

	/**
	 * 取消订阅一个topic
	 */
	public void unsubcrible(View v) {
		String topicName = et_topic.getText().toString();
		if (topicName == null || topicName.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				try {
					client.unsubscribe(params[0], 10);
					return true;
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					appendMessage("取消订阅成功");
				} else {
					appendMessage("取消订阅失败");
				}
			}
		}.execute(topicName);
	}

	/**
	 * 选择文件
	 */
	public void uploadImage(View view) {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex("_data");
				String imagePath = cursor.getString(columnIndex);
				cursor.close();
				cursor = null;
				if (imagePath == null || imagePath.equals("null")) {
					Util.showToast(this, "找不到图片");
					return;
				}
				uploadImage(imagePath);
			} else {
				File file = new File(selectedImage.getPath());
				if (!file.exists()) {
					Util.showToast(this, "找不到图片");
					return;
				}
				uploadImage(file.getAbsolutePath());
			}
		}
	}

	/**
	 * 上传文件
	 */
	private void uploadImage(String filePath) {
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String topic = "image";
				InputStream in = null;
				File file = new File(params[0]);
				try {
					in = new FileInputStream(file);
					Log.i(TAG, "file length -> " + file.length());
					byte[] content = new byte[(int) file.length()];
					// read file content
					while (in.read(content) != -1) {
					}
					// send content
					Message msg = new Message(content);
					client.uploadImage(topic, msg, 2, 30);
					return true;
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					return false;
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					appendMessage("上传图片成功");
				} else {
					appendMessage("上传图片失败");
				}
			}
		}.execute(filePath);
	}

	/**
	 * 发送消息
	 */
	public void publish(View v) {
		String content = et_content.getText().toString();
		String topic = et_topic_publish.getText().toString();
		if (content == null || content.trim().length() == 0 || topic == null || topic.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				Message message = new Message(params[1].getBytes());
				try {
					client.sendRequest(params[0], message, 2, 10);
					return true;
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				appendMessage("publish:" + result);
			}

		}.execute(topic, content);
	}

	class MyCallback extends ActionCallback {

		@Override
		public void onMessageReceived(final String topic, final byte[] payload) {
			appendMessage("onMessageReceived, topic:" + topic + ", content:" + new String(payload));
		}

		@Override
		public void onConnectionStatusChanged(boolean isConnected) {
			appendMessage("onConnectionStatusChanged:" + isConnected);
		}
	}

	private void appendMessage(final String content) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				MyMessage message = new MyMessage(DF.format(new Date()), content);
				messages.add(message);
				adapter.notifyDataSetChanged();
				Log.d(TAG, content);
			}
		});
	}
}
