
1. [版本信息](#version)
2. [创建](#create)
3. [订阅](#subscribe)
4. [取消订阅](#unsubscribe)
5. [异步发布](#publish)
6. [同步发布](#sendRequest)
7. [上传图片](#uploadImage)
8. [销毁](#destroy)
9. [ServiceException](#ServiceException)
10. [Message](#Message)
11. [ActionCallback](#ActionCallback)
12. [QoS说明](#QoS)

# Dasudian IoT DataHub Java SDK

## <a name="version">版本信息</a>

| Date | Version | Note |
|---|---|---|
| 3/13/2017 | 2.0.0 | 客户端全面升级，版本跟新为2.0.0 |
| 2/28/2017 | 1.1.1 | 修改了上传图片的API |
| 2/18/2017 | 1.1.0 | 1.添加了上传图片的api 2.修改了发送消息函数和接收消息函数的参数类型 |

## <a name="create">创建</a>

通过DataHubClient.Builder创建DataHubClient实例。

```
public static class DataHubClient.Builder {
	...

	/**
	 * 通过该Builder构建DataHubClient实例
	 * 
	 * @param instanceId
	 *            用于大数点验证用户，保证客户端与服务器间的安全通信。 demo中的instanceId仅可以用于测试大数点IoT
	 *            DataHub功能使用， 如果您想正式使用大数点IoT服务，请联系大数点客服获取私有的instanceId
	 * @param instanceKey
	 *            用于大数点验证用户，保证客户端与服务器间的安全通信。 demo中的instanceKey仅可以用于测试大数点IoT
	 *            DataHub功能使用， 如果您想正式使用大数点IoT服务，请联系大数点客服获取私有的instanceKey
	 * @param clientName
	 *            客户端名字，可以填写任意的utf-8字符。
	 *            如果你有第三账号系统，并想将自己的账号系统与大数点服务器同步，那么你可以使用第三方账号的名字、昵称。
	 *            如果没有自己的账号系统，或者对该客户端名字不关心，可以使用随机的名字，但是不能填null。
	 * @param clientId
	 *            客户端id，用于服务器唯一标记一个客户端，服务器通过该id向客户端推送消息;
	 *            注意：不同的客户端的id必须不同，如果有两个客户端有相同的id，服务器会关掉其中的一个客户端的连接。
	 *            可以使用设备的mac地址，或者第三方账号系统的id（比如qq号，微信号）。
	 *            如果没有自己的账号系统，则可以随机生成一个不会重复的客户端id。
	 *            或者自己指定客户端的id，只要能保证不同客户端id不同即可。
	 * @throws ServiceException
	 *             有参数为null或长度为0时抛出异常
	 */
	public Builder(String instanceId, String instanceKey, String clientName, String clientId)
			throws ServiceException {
		...
	}

	/**
	 * 设置回调函数，用于接收消息和监听SDK与服务器的连接状态
	 * 
	 * @param callback
	 *            回调函数
	 * @return Builder对象
	 */
	public Builder setCallback(ActionCallback callback) {
		...
	}

	/**
	 * 服务器地址，如果不设置，则默认使用大数点公有云测试服务器。
	 * 
	 * @param serverURL
	 *            服务器的地址
	 * @return Builder对象
	 */
	public Builder setServerURL(String serverURL) {
		...
	}

	/**
	 * 设置是否打开调试功能，默认为false
	 * 
	 * @param debug
	 *            true:打开调试；false:关闭调试
	 * @return Builder对象
	 */
	public Builder setDebug(boolean debug) {
		...
	}

	/**
	 * 获取到DataHubClient实例
	 * 
	 * @return DataHubClient实例
	 */
	public DataHubClient build() {
		...
	}
}
```


## <a name="subscribe">订阅</a>

```
/**
 * 订阅一个主题，该方法会阻塞的等待消息发送完成，或者超时返回。 timeout = 0，表示一直等待；否则等待timeout秒。
 * 
 * @param topic
 *            主题名
 * @param timeout
 *            超时时间，单位s
 * @throws ServiceException
 *             失败时抛出异常
 */
public void subscribe(String topic, long timeout) throws ServiceException
```

## <a name="unsubscribe">取消订阅</a>
```
/**
 * 取消订阅一个主题，该方法会阻塞的等待消息发送完成，或则超时返回。 timeout = 0，表示一直等待；否则等待timeout秒。
 * 
 * @param topic
 *            主题名
 * @param timeout
 *            超时时间，单位s
 * @throws ServiceException
 *             失败时抛出异常
 */
public void unsubscribe(String topic, long timeout) throws ServiceException
```

## <a name="publish">异步发布</a>
```
/**
 * 异步发送消息，SDK根据QoS设置来发送消息，无法知道消息发送成功或失败。
 * 
 * @param topic
 *            主题名
 * @param msg
 *            消息内容，长度不能超过512k
 * @param QoS
 *            服务质量
 * @throws ServiceException
 *             失败是抛出异常
 */
public void publish(String topic, Message msg, int QoS) throws ServiceException
```

## <a name="sendRequest">同步发布</a>
```
/**
 * 同步发送消息，该方法会阻塞的等待消息发送完成，或者超时返回。 timeout = 0，表示一直等待；否则等待timeout秒。
 * 
 * @param topic
 *            主题名
 * @param msg
 *            消息内容，长度不能超过512k
 * @param QoS
 *            服务质量
 * @param timeout
 *            超时时间，单位s
 * @throws ServiceException
 *             失败是抛出异常
 */
public void sendRequest(String topic, Message msg, int QoS, long timeout) throws ServiceException
```

## <a name="uploadImage">上传图片</a>
```
/**
 * 上传图片。该方法会阻塞的等待消息发送完成，或者超时返回。 timeout = 0，表示一直等待；否则等待timeout秒。
 * 
 * @param topic
 *            主题名
 * @param msg
 *            图片内容，最大支持10M
 * @param QoS
 *            服务质量
 * @param timeout
 *            超时时间，单位s
 * @throws ServiceException
 *             失败是抛出异常
 */
public void uploadImage(String topic, Message msg, int QoS, long timeout) throws ServiceException
```

## <a name="destroy">销毁</a>
```
/**
 * 销毁客户端，并断开与服务器的连接
 */
public void destroy()
```

## <a name="ServiceException">ServiceException</a>
```
public class ServiceException extends Exception {

	...	
	// 通用错误码
	public static final int ERROR_NONE = 0;// 没有错误
	public static final int ERROR_ILLEGAL_PARAMETERS = -1;// 非法参数
	public static final int ERROR_DISCONNECTED = -2;// 没有与服务器连接
	public static final int ERROR_UNACCEPT_PROTOCOL_VERSION = -3; // 协议版本不支持
	public static final int ERROR_IDENTIFIER_REJECTED = -4;// 标识符已拒绝
	public static final int ERROR_SERVER_UNAVAILABLE = -5;// 服务器不可用
	public static final int ERROR_BAD_USERNAME_OR_PASSWD = -6;// 错误的用户名和密码
	public static final int ERROR_UNAUTHORIZED = -7;// 未认证
	public static final int ERROR_AUTHORIZED_SERVER_UNAVAILABLE = -8;// 认证服务器不可用
	public static final int ERROR_OPERATION_FAILURE = -9;// 操作失败
	public static final int ERROR_MESSAGE_TOO_BIG = -10;// 消息太大
	public static final int ERROR_NETWORK_UNREACHABLE = -11;// 网络不可达
	public static final int ERROR_TIMEOUT = -12;// 超时
	// Java特有的错误码
	public static final int ERROR_IO = -300;// IO错误

	...

	/**
	 * 获取错误码
	 * @return 错误码
	 */
	public int getCode() {
	}
}
```

## <a name="Message">Message</a>
```
public class Message {

	...

	/**
	 * 构造一个消息
	 * 
	 * @param payload
	 *            消息的内容
	 */
	public Message(byte[] payload) {
		...
	}

	/**
	 * 获取消息内容
	 * @return 消息内容
	 */
	public byte[] getPayload() {
		...
	}

	/**
	 * 设置消息内容
	 * @param payload 消息内容
	 */
	public void setPayload(byte[] payload) {
		...
	}
}
```

## <a name="ActionCallback">ActionCallback</a>
```
public abstract class ActionCallback {
	/**
	 * 接收到发布的消息
	 * 
	 * @param topic
	 *            发布的topic
	 * @param payload
	 *            发布的内容
	 */
	public void onMessageReceived(String topic, byte[] payload) {
	}

	/**
	 * SDK与服务器的连接状态改变
	 * 
	 * @param isConnected
	 *            true:连接成功；false:连接丢失
	 */
	public void onConnectionStatusChanged(boolean isConnected) {
	}
}
```

## <a name="QoS">QoS</a>
```
0:最多分发一次；仅仅发送出去，不等待服务器的应答，删除该消息。
1:至少分发一次；发送给服务器，并等待服务器的应答，收到应答后删除该消息。如果一段时间内客户端没有收到服务器的应答，则再次发送该消息，所以服务器可能收到多条消息。
2:只分发一次；过程如下：
	client --> server(客户端向服务器发送消息)
	client <-- server(服务器向客户端发送pubrel)
	client --> server(客户端向服务器发送pubrel ack)
	client <-- server(服务器向客户端发送pubcom)
	client收到pubcom，删除消息
```
