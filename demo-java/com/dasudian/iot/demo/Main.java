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
	//时间格式
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		//时间定为亚洲上海
		DF.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
	}

	private static class MyCallback extends ActionCallback {
		//打印发布的Topic和内容
		@Override
		public void onMessageReceived(String topic, byte[] payload) {
			LOGGER.info("onMessageReceived,topic=" + topic + ",payload=" + new String(payload));
		}
		//SDK与服务器的连接状态改变的信息
		@Override
		public void onConnectionStatusChanged(boolean isConnected) {
			LOGGER.info("current connect status = " + isConnected);
		}
	}

	/**
	 * 客户端订阅主题
	 * @param topic
	 * @param client
	 * @return
	 */
	private static boolean sub(String topic, DataHubClient client) {
		//客户发送订阅消息主题topic
		try {
			//客户订阅主题  参数为主题topic ,设置超时时间为10秒
			client.subscribe(topic, 10);
			LOGGER.info("subscribe success");
			return true;
		} catch (ServiceException e) {
			e.printStackTrace();
			//这里表示订阅失败，并打印出信息
			LOGGER.info("subscribe failed");
			try {
			//订阅失败后,等待2秒方法执行完毕
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}
			return false;
		}
	}

	public static void main(String[] args) {
		//instance id, 标识客户的唯一ID，请联系大数点商务support@dasudian.com获取
		String instanceId = "yourInstanceId";
		//instance key, 与客户标识相对应的安全密钥，请联系大数点商务support@dasudian.com获取
		String instanceKey = "yourInstanceKey";
		//客户端名字
		String clientName = UUID.randomUUID().toString();
		//客户端ID
		String clientId = clientName;

		//客户端订阅主题,发送主题消息.
		try {
			//建立客户端
			DataHubClient client = new DataHubClient.Builder(instanceId, instanceKey, clientName, clientId)
					.setCallback(new MyCallback()).build();
			//客户端 发送消息的主题
			String topic = "topic";
			//打印日志消息，且若订阅成功则不再订阅，失败则2秒后再次订阅直到订阅成功
			while (sub(topic, client) != true);

			//客户端发送消息(无论消息发送成功或者失败，等待2秒后继续发送)
			while (true) {
				try {
					//客户端的消息内容（payload）
					Message msg = new Message("this is message content".getBytes());
					//客户端发布的主题topic,属于该topic的消息（payload）,消息发布服务质量为QoS2,后面这个为超时时间
					client.sendRequest(topic, msg, 2, 10);
					//打印客户端消息发布成功
					LOGGER.info("sent request success");
					//等待2秒后继续发送
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						//客户端发布消息失败 2秒后 继续
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
