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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import slimchat.android.client.SlimChatClient;
import slimchat.android.client.SlimMessageReceiver;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.dao.SlimMessageDao;

/**
 * 聊天会话管理类。负责打开、管理、激活、去激活聊天会话窗口，发送消息、接收分发消息。
 * 
 * @author slimpp.io
 * 
 */
public class SlimChatManager implements SlimMessageReceiver {

	/**
	 * 会话列表
	 */
	private Map<String, SlimConversation> conversations;

	/**
	 * 消息存储
	 */
	private SlimMessageDao messageDao;

	/**
	 * 客户端
	 */
	private SlimChatClient client;

	/**
	 * 
	 */
	private SlimRosterManager roster;

	private Context appContext;

	
	SlimChatManager(SlimChatClient client) {
		this.client = client;
		messageDao = new SlimMessageDao();
		conversations = new HashMap<String, SlimConversation>();
		client.setMessageReceiver(this);
	}

	/**
	 * APP上下文初始化
	 * 
	 * @param context Application Context
	 */
	public void init(Context context) {
		appContext = context;
	}

	/**
	 * 消息存储DAO
	 * 
	 * @return 消息存储DAO
	 */
	public SlimMessageDao messageDao() {
		return messageDao;
	}

	/**
	 * 客户端
	 * 
	 * @return 客户端
	 */
	public SlimChatClient getClient() {
		return client;
	}
	

	/**
	 * 打开聊天会话
	 * 
	 * @param to 会话对象ID
	 * @return 聊天会话实例
	 */
	public SlimConversation open(String to) {
		SlimConversation conversation = conversations.get(to);
		if (conversation == null) {
			conversation = new SlimConversation(this, to);
			conversations.put(to, conversation);
		}
		return conversation;
	}

	/**
	 * 获取聊天会话
	 * 
	 * @param to 会话对象ID
	 * @return 聊天会话
	 */
	public SlimConversation getConversation(String to) {
		return conversations.get(to);
	}

	/**
	 * 获取当前用户ID
	 * 
	 * @return 当前用户ID
	 */
	public String getUserID() {
		return client.getUserID();
	}

	public SlimRosterManager getRoster() {
		return roster;
	}

	public void setRoster(SlimRosterManager roster) {
		this.roster = roster;
	}

	/**
	 * 发送聊天消息
	 * 
	 * @param message 聊天消息
	 * @param callback 回调
	 */
	public void send(SlimMessage message, SlimCallback callback) {
		client.send(message, callback);
	}

	@Override
	public void messageReceived(SlimMessage message) {
		String from = message.getFrom();
		SlimConversation conversation = open(from);
		conversation.messageReceived(message);
		Log.d("SlimChatManager", "chat: " + conversation.toString()
				+ ", unread: " + conversation.getUnread());
		if (conversation.getUnread() > 0) {
			getRoster().updateUnread(from);
		}
	}

}
