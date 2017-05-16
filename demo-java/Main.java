/*
 * Licensed Materials - Property of Dasudian 
 * Copyright Dasudian Technology Co., Ltd. 2016-2017 
 */
package com.dasudian.iot.demo;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dasudian.iot.sdk.ActionCallback;
import com.dasudian.iot.sdk.DataHubClient;
import com.dasudian.iot.sdk.Message;
import com.dasudian.iot.sdk.ServiceException;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //时间格式

	static {
		DF.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));				    //时间定为亚洲上海
	}

	private static class MyCallback extends ActionCallback {
		//打印发布的Topic和内容 还有SDK与服务器的连接状态改变的信息
		@Override
		public void onMessageReceived(String topic, byte[] payload) {
			LOGGER.info("onMessageReceived,topic=" + topic + ",payload=" + new String(payload));
		}

		@Override
		public void onConnectionStatusChanged(boolean isConnected) {
			LOGGER.info("current connect status = " + isConnected);
		}
	}

	private static boolean sub(String topic, DataHubClient client) {
		//客户发送订阅消息主题topic
		try {
			client.subscribe(topic, 10);
			LOGGER.info("subscribe success");
			return true;
		} catch (ServiceException e) {
			e.printStackTrace();
			LOGGER.info("subscribe failed");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}
			return false;
		}
	}

	public static void main(String[] args) {
		String instanceId = "dsd_9FmYSNiqpFmi69Bui0_A";
		String instanceKey = "238f173d6cc0608a";

		String clientName = UUID.randomUUID().toString();	//客户名
		String clientId = clientName;				//客户ID

		try {
			DataHubClient client = new DataHubClient.Builder(instanceId, instanceKey, clientName, clientId)
					.setCallback(new MyCallback()).build();
			String topic = "topic";				//主题
			while (sub(topic, client) != true)
				;
			while (true) {
				try {
					Message msg = new Message("this is message content".getBytes()); //测试信息
					client.sendRequest(topic, msg, 2, 10);
					LOGGER.info("sent request success");
					Thread.sleep(2000);						//间隔2s发送一次信息
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
