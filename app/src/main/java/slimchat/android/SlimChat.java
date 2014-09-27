/**
 * 
 * The MIT License (MIT)
 * Copyright (c) 2014 SLIMPP.IO

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

import slimchat.android.app.SlimChatApiProvider;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.service.SlimChatService;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

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
public class SlimChat extends SlimContextAware  {

	private static SlimChat instance = new SlimChat();

	private SlimServiceDelegator delegator = null;

	private SlimChatManager manager = null;

	private SlimRosterManager roster = null;

    private SlimChatSetting setting = null;

    private SlimChatApiProvider apiProvider;

    //IoC Injection later???
    private SlimChat() {
        delegator = SlimServiceDelegator.getInstance();
		manager = SlimChatManager.getInstance();
		roster = SlimRosterManager.getInstance();
        setting = SlimChatSetting.getInstance();
	}

	/**
	 * Single Instance
	 * 
	 * @return SlimChat
	 */
	public static SlimChat getInstance() {
		return instance;
	}

    @Override
	public void init(Context appContext) {
        super.init(appContext);
        delegator.init(appContext);
		manager.init(appContext);
		roster.init(appContext);
        setting.init(appContext);
	}

    public void activate(Context context, final SlimCallback callback) {
        delegator.startService(new SlimServiceDelegator.ServiceBoundCallback() {
            @Override
            public void onServiceBound() {
                delegator.getChatService().configure(apiProvider);
                callback.onSuccess();
            }

            @Override
            public void onServiceUnbound() {
                callback.onFailure("Service Cannot be bound", null);
            }
        });
    }

    public boolean isActivated() {
        return delegator.isServiceBound() && delegator.isServiceAlive();
    }

    /**
     * API Provider
     *
     * @param provider
     */
    public void setup(SlimChatApiProvider provider) {
        this.apiProvider = provider;
    }

	public SlimChatManager getManager() {
		return manager;
	}

	public SlimRosterManager getRoster() {
		return roster;
	}

    public SlimServiceDelegator getDelegator() {
        return delegator;
    }

    public SlimChatService getService() throws Exception {
        if(delegator.isServiceBound() && delegator.isServiceAlive()) {
            return delegator.getChatService();
        }
        throw new Exception("Service is not bound or alive");
    }

	/**
	 * Power Manager
	 * 
	 * @return
	 */
	public PowerManager getPowerManager() {
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

	/**
	 * Wrap client login
	 * 
	 * @param username
	 * @param password
	 * @param callback
	 */
	public void login(String username, String password, SlimCallback callback) throws Exception {
		this.getService().login(username, password, callback);
	}

	/**
	 * Online Without buddies
	 * 
	 * @param callback
	 */
	public void online(final SlimCallback callback) throws Exception {
		this.online(new HashSet<String>(0), callback);
	}

	/**
	 * Wrap client online
	 * 
	 * @param callback
	 */
	public void online(Set<String> buddies, final SlimCallback callback) throws Exception {
        this.getService().online(buddies, callback);
	}

    /**
     * 发送聊天消息
     *
     * @param message 聊天消息
     * @param callback 回调
     */
    public void send(SlimMessage message, SlimCallback callback) throws Exception {
        getService().send(message, callback);
    }

    /**
     * Wrap client offline
     *
     * @param callback
     */
    public void offline(final SlimCallback callback) throws Exception {
        getService().offline(callback);
    }

}
