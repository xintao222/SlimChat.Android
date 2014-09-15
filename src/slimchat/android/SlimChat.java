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
package slimchat.android;

import org.json.JSONException;
import org.json.JSONObject;
import slimchat.android.client.SlimChatClient;
import slimchat.android.client.SlimChatClientListener;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimUser;
import android.app.NotificationManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * SlimChat入口类。
 * 
 * <p>
 * Android APP通过SlimChat单例集成即时消息服务。SlimChat维护全局的ChatManger, RosterManger,
 * ChatClient实例:<br>
 * <ul>
 * <li>SlimChatClient: 客户端类，网络连接和发送、接收消息。</li>
 * <li>SlimChatManger: 聊天会话管理类，打开关闭聊天会话，处理会话消息</li>
 * <li>SlimRosterManager: 好友管理类, 管理好友泪飙</li>
 * </ul>
 * </p>
 * 
 * @author slimpp.io
 * 
 */
public class SlimChat implements SlimChatClientListener {

	private static SlimChat instance = null;

	private Context appContext;

	private SlimChatClient client;

	private SlimChatManager manager;

	private SlimRosterManager roster;

	private SlimUser user = null;

	private SlimChat() {
		client = new SlimChatClient();
		manager = new SlimChatManager(client);
		roster = new SlimRosterManager(client);
		manager.setRoster(roster);
	}

	/**
	 * Single Instance
	 * 
	 * @return SlimChat
	 */
	public synchronized static SlimChat instance() {
		if (instance == null) {
			instance = new SlimChat();
		}
		return instance;
	}

	public void init(Context context) {
		appContext = context;
		client.init(context);
		manager.init(context);
		roster.init(context);
	}

	public SlimChatManager manager() {
		return manager;
	}

	public SlimChatClient client() {
		return client;
	}

	public SlimRosterManager roster() {
		return roster;
	}

	/**
	 * Power Manager
	 * 
	 * @return
	 */
	public PowerManager powerManager() {
		return (PowerManager) appContext
				.getSystemService(Context.POWER_SERVICE);
	}

	/**
	 * NotificationManager
	 * 
	 * @return
	 */
	public NotificationManager getNotificationManager() {
		return (NotificationManager) appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public SlimUser getUser() {
		return user;
	}

	/**
	 * Wrap client login
	 * 
	 * @param username
	 * @param password
	 * @param callback
	 */
	public void login(String username, String password, SlimCallback callback) {
		this.client.login(username, password, callback);
	}

	/**
	 * Online Without buddies
	 * 
	 * @param callback
	 */
	public void online(final SlimCallback callback) {
		this.online(new String[0], callback);
	}

	/**
	 * Wrap client online
	 * 
	 * @param slimCallback
	 */
	public void online(String[] buddies, final SlimCallback callback) {
		client.online(buddies, new SlimCallback() {
			@Override
			public void onSuccess(Object data) {
				try {
					JSONObject json = (JSONObject) data;
					SlimChat.this.user = new SlimUser(json
							.getJSONObject("user"));
					roster.feed(json.getJSONArray("buddies"));
					callback.onSuccess(data);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String error, Throwable exception,
					Object extra) {
				callback.onFailure(error, exception, extra);
			}
		});
	}

	/**
	 * Wrap client offline
	 * 
	 * @param callback
	 */
	public void offline(final SlimCallback callback) {
		client.offline(callback);
	}

}
