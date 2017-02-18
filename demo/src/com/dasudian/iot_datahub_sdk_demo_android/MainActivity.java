package com.dasudian.iot_datahub_sdk_demo_android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import com.dasudian.iot.sdk.Topic;
import com.example.iot_datahub_sdk_demo_android.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	public static final String serverURL = "ssl://try.iotdatahub.net:8883";// 测试服务器地址，在正式使用时请联系大数点客服获取私有云服务器地址
	public static final String instanceId = "dsd_9FmYSNiqpFmi69Bui0_A";// 测试instanceId，在正式使用时请联系大数点客服获取instanceId
	public static final String instanceKey = "238f173d6cc0608a";// 测试instanceKey，在正式使用时请联系大数点客服获取instanceKey
	public static final int REQUEST_CODE = 2;
	private DataHubClient client = null;
	private EditText et_topic;
	private EditText et_content;
	private EditText et_topic_publish;
	private ListView listView;
	private List<MyMessage> messages = new ArrayList<MyMessage>();
	private MessageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		connect();
	}

	@Override
	protected void onDestroy() {
		// 断开服务器的连接
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (ServiceException e) {
		}
		super.onDestroy();
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
	 * 
	 * @param v
	 */
	public void subcrible(View v) {
		String topicName = et_topic.getText().toString();
		if (topicName == null || topicName.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		Topic topic = new Topic(topicName, 1);
		new AsyncTask<Topic, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Topic... params) {
				try {
					client.subscribe(params[0]);
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
					Util.showToast(MainActivity.this, "订阅成功");
					Log.d(TAG, "订阅成功");
				} else {
					Util.showToast(MainActivity.this, "订阅失败");
					Log.e(TAG, "订阅失败");
				}
			}
		}.execute(topic);
	}

	/**
	 * 取消订阅一个topic
	 * 
	 * @param v
	 */
	public void unsubcrible(View v) {
		String topicName = et_topic.getText().toString();
		if (topicName == null || topicName.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		Topic topic = new Topic(topicName, 1);
		new AsyncTask<Topic, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Topic... params) {
				try {
					client.unsubscribe(params[0]);
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
					Util.showToast(MainActivity.this, "取消订阅成功");
					Log.d(TAG, "取消订阅成功");
				} else {
					Util.showToast(MainActivity.this, "取消订阅失败");
					Log.e(TAG, "取消订阅失败");
				}
			}
		}.execute(topic);
	}

	/**
	 * 选择文件
	 * 
	 * @param view
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
	 * 
	 * @param view
	 */
	private void uploadImage(String filePath) {
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				Topic topic = new Topic("image", 2);
				try {
					client.uploadImage(topic, params[0]);
					return true;
				} catch (ServiceException e) {
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					Util.showToast(MainActivity.this, "上传图片成功");
					Log.d(TAG, "上传图片成功");
				} else {
					Util.showToast(MainActivity.this, "上传图片失败");
					Log.e(TAG, "上传图片失败");
				}
			}
		}.execute(filePath);
	}

	/**
	 * 发送消息
	 * 
	 * @param v
	 */
	public void publish(View v) {
		String content = et_content.getText().toString();
		String name = et_topic_publish.getText().toString();
		if (content == null || content.trim().length() == 0 || name == null || name.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		Topic topic = new Topic(name, 0);
		Message message = new Message(topic, content.getBytes());
		new AsyncTask<Message, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Message... params) {
				try {
					client.publish(params[0]);
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
					Util.showToast(MainActivity.this, "发布成功");
					Log.d(TAG, "发布成功");
				} else {
					Util.showToast(MainActivity.this, "发布失败");
					Log.e(TAG, "发布失败");
				}
			}
		}.execute(message);
	}

	class MyCallback implements ActionCallback {

		@Override
		public void onPublishSuccess(Message m) {
			Log.d(TAG, "onPublishSuccess");
		}

		@Override
		public void onPublishFailure(Message m, Throwable t) {
			Log.d(TAG, "onPublishFailure");
		}

		@Override
		public void onMessageReceived(final String topic, final byte[] payload) {
			Log.d(TAG, "onMessageReceived:topic=" + topic + ",payload=" + new String(payload));
			runOnUiThread(new Runnable() {
				public void run() {
					MyMessage message = new MyMessage(topic, new String(payload));
					messages.add(message);
					Util.showToast(MainActivity.this, "onMessageReceived");
					adapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void connectionLost(Throwable t) {
			System.out.println("connectionLost");
		}
	}

	/**
	 * 连接服务器
	 */
	private void connect() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String userName = UUID.randomUUID().toString();
					String clientId = userName;
					client = new DataHubClient.Builder(instanceId, instanceKey, userName, clientId)
							.setCallback(new MyCallback()).setServerURI(serverURL).setIgnoreCertificate(true).build();
					client.connect();
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
					Util.showToast(MainActivity.this, "链接服务器成功");
					Log.d(TAG, "链接服务器成功");
				} else {
					Util.showToast(MainActivity.this, "链接服务器失败");
					Log.e(TAG, "链接服务器失败");
				}
			}
		}.execute();
	}
}
