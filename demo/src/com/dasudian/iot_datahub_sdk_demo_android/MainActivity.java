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

import com.dasudian.iot.android.ConnectionReceiver;
import com.dasudian.iot.sdk.ActionCallback;
import com.dasudian.iot.sdk.DataHubClient;
import com.dasudian.iot.sdk.Message;
import com.dasudian.iot.sdk.ServiceException;
import com.dasudian.iot.sdk.Topic;

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
	public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		DF.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		connect();
	}

	@Override
	protected void onDestroy() {
		// APP退出时断开连接
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (ServiceException e) {
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 通知SDK APP已经在前台运行了，此时SDK会检测连接是否正常。如果连接丢失，SDK会马上开始重连。
		if (client != null) {
			client.onForeground();
		}
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
					appendMessage("订阅成功");
				} else {
					appendMessage("订阅失败");
				}
			}
		}.execute(topic);
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
					appendMessage("取消订阅成功");
				} else {
					appendMessage("取消订阅失败");
				}
			}
		}.execute(topic);
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
				Topic topic = new Topic("image", 2);
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
					Message msg = new Message(topic, content);
					client.uploadImage(msg);
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
		String name = et_topic_publish.getText().toString();
		if (content == null || content.trim().length() == 0 || name == null || name.trim().length() == 0) {
			Util.showToast(this, "内容不能为空");
			return;
		}
		Topic topic = new Topic(name, 0);
		Message message = new Message(topic, content.getBytes());
		try {
			// 调用异步发送方法
			client.publish(message);
		} catch (ServiceException e) {
			appendMessage("发布失败 " + e.getMessage());
		}
	}

	class MyCallback implements ActionCallback {

		@Override
		public void onPublishSuccess(Message m) {
			appendMessage("onPublishSuccess");
		}

		@Override
		public void onPublishFailure(Message m, Throwable t) {
			appendMessage("onPublishFailure");
		}

		@Override
		public void onMessageReceived(final String topic, final byte[] payload) {
			appendMessage("onMessageReceived, topic:" + topic + ", content:" + new String(payload));	
		}

		@Override
		public void connectionLost(Throwable t) {
			appendMessage("connectionLost");
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
							.setCallback(new MyCallback()).setAutomaticReconnect(true).setServerURI(serverURL)
							.setIgnoreCertificate(true).build();
					ConnectionReceiver.setDataHubClient(client);
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
					appendMessage("链接服务器成功");
				} else {
					appendMessage("链接服务器失败");
				}
			}
		}.execute();
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
