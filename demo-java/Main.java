package com.dasudian.iot.demo;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dasudian.iot.sdk.ActionCallback;
import com.dasudian.iot.sdk.DataHubClient;
import com.dasudian.iot.sdk.Message;
import com.dasudian.iot.sdk.Topic;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

	private static class MyCallback implements ActionCallback {

		@Override
		public void onMessageReceived(String topic, byte[] payload) {
			LOGGER.info("onMessageReceived,topic=" + topic + ",payload=" + new String(payload));
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

	public static void main(String[] args) {
		// test environment
		String serverURL = "ssl://try.iotdatahub.net:8883";
		String instanceId = "dsd_9FmYSNiqpFmi69Bui0_A";
		String instanceKey = "238f173d6cc0608a";
		String userName = UUID.randomUUID().toString();
		String clientId = userName;
		
		try {
			DataHubClient client = new DataHubClient.Builder(instanceId, instanceKey, userName, clientId).setCallback(
					new MyCallback()).setServerURI(serverURL).setIgnoreCertificate(true).build();
			client.connect();
			Topic topic = new Topic("topic", 2);
			client.subscribe(topic);
			while (true) {
				try {
					Message msg = new Message(topic, "this is message content".getBytes(), 10000);
					client.sendRequest(msg);
					LOGGER.info("sent request success");
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
