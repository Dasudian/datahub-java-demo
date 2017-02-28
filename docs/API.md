# 版本信息

| Date | Version | Note |
|---|---|---|
| 2/28/2017 | 1.1.1 | 修改了上传图片的API |
| 2/18/2017 | 1.1.0 | 1.添加了上传图片的api 2.修改了发送消息函数和接收消息函数的参数类型 |

# DataHub Java SDK

## Class DataHubClient

### 初始化

通过DataHubClient的Builder获取DataHubClient实例。

```
public DataHubClient.Builder(String instanceId, String instanceKey, String userName, 
                    String clientId) throws ServiceException 
Parameters:
instanceId - 用于大数点验证用户，保证客户端与服务器间的安全通信。demo中的instanceId仅可以用于测试大数点IoT DataHub功能使用，
如果您想正式使用大数点IoT服务，请联系大数点客服获取私有的instanceId。

instanceKey - 用于大数点验证用户，保证客户端与服务器间的安全通信。demo中的instanceKey仅可以用于测试大数点IoT DataHub功能使用，
如果您想正式使用大数点IoT服务，请联系大数点客服获取私有的instanceKey。

userName - 如果你有第三账号系统，并想将自己的账号系统与大数点服务器同步，那么你可以使用第三方账号的名字、昵称。
如果没有自己的账号系统，或者对该客户端名字不关心，可以使用随机的名字，但是不能填null。

clientId - 客户端id，用于服务器唯一标记一个客户端，服务器通过该id向客户端推送消息;
注意：不同的客户端的id必须不同，如果有两个客户端有相同的id，服务器会关掉其中的一个客户端的连接。
可以使用设备的mac地址，或者第三方账号系统的id（比如qq号，微信号）。如果没有自己的账号系统，
则可以随机生成一个不会重复的客户端id。或者自己指定客户端的id，只要能保证不同客户端id不同即可。

可选设置：
1. Builder setCallback(ActionCallback callback)
回调函数，用于监听SDK的各种状态，比如SDK连接断开，有新的消息到达，或者获取某条消息的发送结果。

2. Builder setServerURI(String serverURI) 
服务器地址，如果不设置，则默认使用大数点公有云测试服务器。

3. Builder setCleanSession(boolean cleanSession)
true:清除会话，在客户端断开连接后，订阅的topic不会在服务器保存。
false：不清除会话，断开连接后订阅的topic会保留。默认为false

4. Builder setConnectionTimeout(int connectionTimeout)
连接服务器的超时时间设置，默认是30s。

5. Builder setCommandTimeout(int commandTimeout)
设置sendRequest,subscribe,unsubscribe超时时间，默认是5秒。

6. Builder setCertificate(InputStream certificate)
如果使用私有证书，需要设置证书

7. Builder setIgnoreCertificate(boolean value)
是否忽略证书验证；true：忽略证书验证，false：不忽略证书验证，可以使用私有证书（目前服务器只支持私有证书），或者系统默认的证书

8. DataHubClient build()
获取到DataHubClient实例
```

### 连接服务器
```
public void connect()
             throws com.dasudian.iot.sdk.ServiceException
连接服务器，该方法是同步方法，会阻塞主线程。连接成功后，sdk会在连接断开后自动重连。
现在的自动重连的机制是：在连接断开后，sdk等待1秒后会尝试连接服务器，如果连接失败会等待双倍的时间后再连接服务器，
直到最后最多等待2分钟后，再次连接服务器。
Throws:
com.dasudian.iot.sdk.ServiceException - 连接服务器失败会抛出异常
```

### 获取当前连接状态
```
public boolean isConnected()
查看客户端连接状态 get the client connect status.
Returns:
处于连接状态返回true，连接断开返回false
```

### 异步发布消息
```
public void publish(Message msg)
             throws com.dasudian.iot.sdk.ServiceException
非阻塞的发送消息，发送的结果在回调函数中返回。在网络丢失时，sdk会最多cache 10个消息。
这10个消息在网络连接恢复后会发送给服务器。
msg - 要发布的消息。
Throws:
com.dasudian.iot.sdk.ServiceException - 发送失败时抛出异常
```

### 同步发布消息
```
public Message sendRequest(Message msg)
                    throws com.dasudian.iot.sdk.ServiceException
阻塞的发布消息，该方法会阻塞的等待消息发送完成，或则超时返回。超时时间有Message的timeout字段指定。
timeout = 0：表示一直等待；timeout = someValue：表示最多等待someValue毫秒。
Parameters:
msg - 要发送的消息
Message you want to send.
Returns:
成功时返回发送的消息
Throws:
com.dasudian.iot.sdk.ServiceException - 发送失败时抛出异常
```

### 订阅消息
```
public void subscribe(Topic topic)
               throws com.dasudian.iot.sdk.ServiceException
订阅一个topic，该方法是同步方法，会阻塞的等待服务器结果，最多阻塞5秒。
Parameters:
topic - 要订阅的topic
Throws:
com.dasudian.iot.sdk.ServiceException - 失败时抛出异常
```

### 取消订阅
```
public void unsubscribe(Topic topic)
                throws com.dasudian.iot.sdk.ServiceException
取消订阅，该方法是同步方法，会阻塞的等待服务器结果，最多阻塞5秒。
Parameters:
topic - 要取消的topic
Throws:
com.dasudian.iot.sdk.ServiceException - 失败时抛出异常
```

### 上传图片

```
public Message uploadImage(Message msg)
				throws com.dasudian.iot.sdk.ServiceException
上传图片。该方法会阻塞的等待消息发送完成，或则超时返回。超时时间有Message的timeout字段指定。
timeout = 0：表示一直等待；timeout = someValue：表示最多等待someValue毫秒
Parameters:
msg - 要发送的图片的消息内容，消息内容不能大于10M。
Returns:
成功时返回发送的消息
Throws:
com.dasudian.iot.sdk.ServiceException - 失败时抛出异常
```

### 与服务器断开连接
```
public void disconnect()
                throws com.dasudian.iot.sdk.ServiceException
断开与服务器的连接
Throws:
com.dasudian.iot.sdk.ServiceException - 失败时抛出异常
```