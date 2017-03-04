package com.dasudian.iot_datahub_sdk_demo_android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MessageAdapter extends ArrayAdapter<MyMessage> {

	private int resource;

	public MessageAdapter(Context context, int resource, List<MyMessage> messages) {
		super(context, resource, messages);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyMessage message = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resource, null);
			viewHolder = new ViewHolder();
			viewHolder.topic = (TextView) view.findViewById(R.id.tv_topic);
			viewHolder.content = (TextView) view.findViewById(R.id.tv_content);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.topic.setText(message.getTopic());
		viewHolder.content.setText(message.getContent());
		return view;

	}

	class ViewHolder {
		TextView topic;
		TextView content;
	}

}
