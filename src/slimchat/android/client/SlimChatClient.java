/**
 * 
 * The MIT License (MIT)
 * Copyright (c) 2014 <slimpp.io>

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package slimchat.android.client;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.util.Log;

import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.core.SlimPresence;

/**
 * SlimChat的客户端类：<br>
 * <ul>
 * <li>1. 调用SLIMPP业务服务器的API接口，包括上线、发送消息、广播现场、离线等请求；</li>
 * <li>2. 创建并维护与SLIMPP消息服务器的网络连接； TODO: 重连处理</li>
 * <li>3. 接收并广播消息</li>
 * </ul>
 * 
 * @author Feng Lee<feng.lee@slimpp.io>
 * 
 */
public class SlimChatClient implements MqttCallback {

	/**
	 * Default API URL
	 */
	static final String DEFAULT_API_URL = "http://slimpp.io/api.php/v1";

	/**
	 * Default AUTH URL
	 */
	static final String DEFAULT_AUTH_URL = "http://slimpp.io/login";

	/**
	 * API URL
	 */
	private String apiURL = DEFAULT_API_URL;

	/**
	 * AUTH URL
	 */
	private String authURL = DEFAULT_AUTH_URL;

	/**
	 * Application Context
	 */
	private Context appContext;

	/**
	 * Communication ticket
	 */
	private String ticket;

	/**
	 * Domain
	 */
	private String domain;

	/**
	 * JSONPD Server
	 */
	// private String jsonpd;

	/**
	 * MQTTD Server
	 */
	private String mqttd;

	/**
	 * MQTT Client
	 */
	private MqttAndroidClient mqttc = null;

	public enum ConnectionState {

		INITIALIZED,

		CONNECTING,

		CONNERROR,

		ESTABLISHED,

		DISCONNECTED,

	}

	/**
	 * Client state
	 */
	private ConnectionState state = ConnectionState.INITIALIZED;

	/**
	 * Message Receiver
	 */
	private SlimMessageReceiver messageReceiver = null;

	/**
	 * Presence Receiver
	 */
	private SlimPresenceReceiver presenceReceiver = null;

	/**
	 * Slim Http Client
	 */
	private SlimHttpClient httpc;

	/**
	 * Current user
	 */
	private JSONObject user;

	/**
	 * Current user id
	 */
	private String userID;

	public SlimChatClient() {
		httpc = new SlimHttpClient();
	}

	/**
	 * Init Context
	 */
	public void init(Context appContext) {
		this.appContext = appContext;
		httpc.init(appContext);
	}

	public ConnectionState getState() {
		return state;
	}

	private void setState(ConnectionState state) {
		this.state = state;
	}

	/**
	 * Set apiURL
	 * 
	 * @param url
	 */
	public void setApiURL(String url) {
		this.apiURL = url;
	}

	/**
	 * Set authURL
	 * 
	 * @param url
	 */
	public void setAuthURL(String url) {
		this.authURL = url;
	}

	/**
	 * Set message receiver
	 * 
	 * @param receiver
	 */
	public void setMessageReceiver(SlimMessageReceiver receiver) {
		this.messageReceiver = receiver;
	}

	/**
	 * Set presence receiver
	 * 
	 * @param receiver
	 */
	public void setPresenceReceiver(SlimPresenceReceiver receiver) {
		this.presenceReceiver = receiver;
	}

	/**
	 * Login with username and password
	 * 
	 * @param username
	 * @param password
	 * @param callback
	 */
	public void login(String username, String password,
			final SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);
		SlimResponseHandler handler = new LoginResponseHandler(this, callback);
		httpc.post(authURL + "?client=android", params, handler);
	}

	/**
	 * User is online
	 * 
	 * @param buddies
	 * @param callback
	 */
	public void online(String[] buddies, SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("buddies", stringJoin(buddies, ","));
		OnlineResponseHandler handler = new OnlineResponseHandler(this,
				callback);
		httpc.post(apiURL + "/online", params, handler);
	}

	/**
	 * TODO: Online ready
	 * 
	 * @param response
	 * 
	 * @throws JSONException
	 */
	void ready(JSONObject response) throws JSONException {
		JSONObject conn = response.getJSONObject("connection");
		this.domain = conn.getString("domain");
		this.ticket = conn.getString("ticket");
		// this.jsonpd = conn.getString("jsonpd");
		this.mqttd = conn.getString("mqttd");
		this.user = response.getJSONObject("user");
		this.userID = user.getString("id");
	}

	/**
	 * TODO: Try to connect
	 * 
	 * @throws JSONException
	 * @throws MqttException
	 */
	void connect() {
		setState(ConnectionState.CONNECTING);
		if (mqttc == null) {
			String clientId = userID + "/android";
			mqttc = new MqttAndroidClient(appContext, mqttd, clientId,
					new MemoryPersistence());
		}
		if (!mqttc.isConnected()) {
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setConnectionTimeout(3000);
			connOpts.setKeepAliveInterval(10 * 60);
			connOpts.setUserName(this.domain);
			connOpts.setPassword(this.ticket.toCharArray());
			try {
				mqttc.setCallback(this);
				mqttc.connect(connOpts, null, new IMqttActionListener() {
					@Override
					public void onFailure(IMqttToken token, Throwable e) {
						e.printStackTrace();
						setState(ConnectionState.CONNERROR);
					}

					@Override
					public void onSuccess(IMqttToken token) {
						Log.d("SlimChatClinet", "MQTT Connected");
						setState(ConnectionState.ESTABLISHED);
					}
				});
			} catch (MqttException e) {
				e.printStackTrace();
				setState(ConnectionState.CONNERROR);
			}
		}
	}

	/**
	 * Fetch buddies
	 * 
	 * @param ids
	 * @param callback
	 */
	public void fetchBuddies(String[] ids, final SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("domain", domain);
		params.put("ticket", ticket);
		params.put("ids", stringJoin(ids, ","));
		BuddiesResponseHandler handler = new BuddiesResponseHandler(this,
				callback);
		httpc.get(apiURL + "/buddies", params, handler);
	}

	/**
	 * Send Message
	 * 
	 * @param message
	 * @param callback
	 */
	public void send(SlimMessage message, SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("domain", domain);
		params.put("ticket", ticket);
		params.put("to", message.getTo());
		params.put("from", message.getFrom());
		params.put("body", message.getBody());
		SlimResponseHandler handler = new SlimResponseHandler(this, callback);
		httpc.post(apiURL + "/message", params, handler);
	}

	/**
	 * Publish presence
	 * 
	 * @param presence
	 * @param callback
	 */
	public void publish(SlimPresence presence, SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("domain", domain);
		params.put("ticket", ticket);
		params.put("show", presence.getShow());
		params.put("status", presence.getStatus());
		SlimResponseHandler handler = new SlimResponseHandler(this, callback);
		httpc.post(apiURL + "/presence", params, handler);
	}

	/**
	 * User is offline
	 * 
	 * @param callback
	 */
	public void offline(SlimCallback callback) {
		RequestParams params = new RequestParams();
		params.put("domain", domain);
		params.put("ticket", ticket);
		SlimResponseHandler handler = new SlimResponseHandler(this, callback);
		httpc.post(apiURL + "/offline", params, handler);
	}

	@Override
	public void connectionLost(Throwable e) {
		Log.e("SlimChatClient", "MQTT Disconnected!");
		e.printStackTrace();
		setState(ConnectionState.DISCONNECTED);
		// how to reconnect?
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		Log.d("SlimChatClient", token.toString());
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		Log.d("SlimChatClient", "MQTT Message from Topic: " + topic);
		Log.d("SlimChatClient", new String(msg.getPayload()));
		JSONObject json = new JSONObject(new String(msg.getPayload()));
		if (json.has("status")) {
			String status = json.getString("status");
			if ("ok".equalsIgnoreCase(status)) {
				JSONArray array = json.getJSONArray("presences");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					SlimPresence p = new SlimPresence(obj);
					if (presenceReceiver != null) {
						presenceReceiver.presenceReceived(p);
					}
				}
				array = json.getJSONArray("messages");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					SlimMessage message = new SlimMessage(obj);
					if (messageReceiver != null) {
						messageReceiver.messageReceived(message);
					}
				}
			} else {
				onErrorReceived(json);
			}
		} else {
			onErrorReceived(json);
		}
	}

	protected void onErrorReceived(JSONObject data) {
		// TODO: how to handle?
		Log.e("SlimChatClient", "Error Data: " + data.toString());
	}

	private String stringJoin(String[] buddies, String sep) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : buddies) {
			if (first)
				first = false;
			else
				sb.append(sep);
			sb.append(s);
		}
		return sb.toString();
	}

	public String getUserID() {
		return userID;
	}

}
