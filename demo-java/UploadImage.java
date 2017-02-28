package com.dasudian.iot.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;

import com.dasudian.iot.sdk.ActionCallback;
import com.dasudian.iot.sdk.DataHubClient;
import com.dasudian.iot.sdk.Message;
import com.dasudian.iot.sdk.Topic;

public class UploadImage {
	private static final Logger LOGGER = Logger.getLogger(UploadImage.class.getSimpleName());
	
	private static class MyCallback implements ActionCallback {

		@Override
		public void onMessageReceived(String topic, byte[] payload) {
			LOGGER.info("onMessageReceived,topic=" + topic);
			
			// save file
			if (topic.equals("image")) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream("image-"+UUID.randomUUID().toString());
					fos.write(payload);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onPublishSuccess(Message m) {
			LOGGER.info("onPublishSucceess, topic= " + m.getTopic().getName() + ",content= " + m.getPayload());
		}

		@Override
		public void onPublishFailure(Message m, Throwable t) {
			LOGGER.info("onPublishFailure");
		}

		@Override
		public void connectionLost(Throwable t) {
			LOGGER.info("connectionLost");
		}
	}
	
	private static Message getMessage(Topic topic, String imagePath) {
		File file = new File(imagePath);
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int byteread = 0;
			LOGGER.info("file length -> " + file.length());
			byte[] content = new byte[(int) file.length()];
			// read file content
			while ((byteread = in.read(content)) != -1) {
			}
			// send content
			Message msg = new Message(topic, content);
			return msg;
		} catch (Exception e) {
			
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String serverURL = "ssl://try.iotdatahub.net:8883";
		String instanceId = "dsd_9FmYSNiqpFmi69Bui0_A";
		String instanceKey = "238f173d6cc0608a";
		String userName = UUID.randomUUID().toString();
		String clientId = userName;
		
		DataHubClient client = new DataHubClient.Builder(instanceId, instanceKey, userName, clientId).setCallback(
				new MyCallback()).setCleanSession(true).setServerURI(serverURL).setIgnoreCertificate(true).build();
		client.connect();
		Topic topic = new Topic("image", 2);
		client.subscribe(topic);
		
//		client.uploadImage(getMessage(topic, UploadImage.class.getClassLoader().getResource("1.jpg").getFile()));
//		client.uploadImage(getMessage(topic, UploadImage.class.getClassLoader().getResource("2.jpg").getFile()));
//		client.uploadImage(getMessage(topic, UploadImage.class.getClassLoader().getResource("3.jpg").getFile()));
//		client.uploadImage(getMessage(topic, UploadImage.class.getClassLoader().getResource("4.gif").getFile()));
//		client.uploadImage(getMessage(topic, UploadImage.class.getClassLoader().getResource("5.png").getFile()));
		LOGGER.info("upload image success");
		int i = 200000;
		while (i-- > 0) {
			LOGGER.info("running....");
			Thread.sleep(10*1000);
		}
		client.disconnect();
	}

}
