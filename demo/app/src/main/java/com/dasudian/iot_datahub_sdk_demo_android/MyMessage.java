package com.dasudian.iot_datahub_sdk_demo_android;

public class MyMessage {

	private String topic;
	private String content;

	public MyMessage(String topic, String content) {
		super();
		this.topic = topic;
		this.content = content;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
