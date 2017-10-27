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

import com.dasudian.iot.sdk.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        DF.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    private static class MyCallback extends ActionCallback {
        //打印发布消息（主题和内容）
        @Override
        public void onMessageReceived(String topic, byte[] payload) {
            LOGGER.info("onMessageReceived,topic=" + topic + ",payload=" + new String(payload));
        }
        //打印服务器连接状态成功或者失败
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
        try {
            //客户订阅主题  参数为主题topic , 消息发布服务质量为QoS2(确保消息只有一次到达) 设置超时时间为10秒
            client.subscribe(topic, 2, 10);
            //打印订阅成功消息
            LOGGER.info("subscribe success");
            return true;
        } catch (ServiceException e) {
            e.printStackTrace();
           //这里表示订阅失败，并打印出消息
            LOGGER.info("subscribe failed " + e.getCode());
            try {
           //2秒后 方法执行完毕
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
            }
            return false;
        }
    }

    public static void main(String[] args) {
        // 大数点IoT DataHub云端地址，请联系大数点商务support@dasudian.com获取
        String server_url = "tcp://192.168.1.24:1883";
       //instance id, 标识客户的唯一ID，请联系大数点商务support@dasudian.com获取
        String instanceId = "dsd_9FbtxYWifMpGsnfDY8_A";
       //instance key, 与客户标识相对应的安全密钥，请联系大数点商务support@dasudian.com获取
        String instanceKey = "8ad981c84ffd9e8e";
        //客户端设备类型
        String clientType = "sensor";
        //客户端ID
        String clientId = UUID.randomUUID().toString();

        //客户端订阅主题,发送主题消息.
        try {
            //建立客户端
            DataHubClient client = new DataHubClient.Builder(instanceId, instanceKey, clientType, clientId)
                    .setServerURL(server_url)
                    .setCallback(new MyCallback()).build();
            //客户端 发送消息的主题
            String topic = "/dsd_9ITRIalNEYUJMm4Hr6_A/HZ/process_test_result";
            String type = Constants.TEXT;
            String messageString = "[\n" +
                    "    {\n" +
                    "        \"id\":\"01\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"01\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"011\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"01\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"03\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"04\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"05\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\":\"06\",\n" +
                    "        \"open\":false,\n" +
                    "        \"pId\":\"0\",\n" +
                    "        \"name\":\"A部门\"\n" +
                    "    }\n" +
                    "]\n" +
                    "\n";
            String str = "test publish";
            //打印日志消息，且若订阅成功则不再的订阅，失败则2秒后再次订阅直到订阅成功
            while (sub(topic, client) != true);
            //客户发送消息
            while (true) {
                try {
                    //客户的测试消息内容（payload）
                    Message msg = new Message(str.getBytes());
                    //客户发布的主题topic,属于该topic的消息（payload）,消息发布服务质量为QoS2,后面这个为超时时间

                    client.sendRequest(topic, msg, 2, 10, Constants.TEXT);
                    //打印客户消息发布成功
                    LOGGER.info("sent request success");
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                    //客户发布消息失败 2秒后 继续测试
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

