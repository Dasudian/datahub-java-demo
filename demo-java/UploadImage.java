/*
 * Licensed Materials - Property of Dasudian 
 * Copyright Dasudian Technology Co., Ltd. 2016-2017 
 */
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

public class UploadImage {
	private static final Logger LOGGER = Logger.getLogger(UploadImage.class.getSimpleName());
	
	private static class MyCallback extends ActionCallback {

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
	}
	
	private static Message getMessage(String imagePath) {
		File file = new File(imagePath);
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			System.out.println("file length -> " + file.length());
			byte[] content = new byte[(int) file.length()];
			// read file content
			while (in.read(content) != -1);
			// send content
			Message msg = new Message(content);
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
				new MyCallback()).setServerURL(serverURL).build();
		String topic = "image";
		client.subscribe(topic, 10);
		client.uploadImage(topic, getMessage(UploadImage.class.getClassLoader().getResource("icon.png").getFile()), 2, 30);
		System.out.println("upload image success");
		int i = 200000;
		while (i-- > 0) {
			System.out.println("running....");
			Thread.sleep(10*1000);
		}
		client.destroy();
	}

}
