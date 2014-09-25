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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.dao.SlimConversationDao;
import slimchat.android.dao.SlimMessageDao;

/**
 * 聊天会话管理类。负责打开、管理、激活、去激活聊天会话窗口，发送消息、接收分发消息。
 * 
 * @author slimpp.io
 * 
 */
public class SlimChatManager extends SlimContextAware {

    private static final SlimChatManager instance = new SlimChatManager();

    /**
	 * 会话列表
	 */
	private Map<String, SlimConversation> conversations;

	/**
	 * 消息存储
	 */
	private SlimMessageDao messageDao = null;

    /**
     * 会话存储
     */
    private final SlimConversationDao conversationDao;

    private String userId;

	private SlimChatManager() {
        conversations = new HashMap<String, SlimConversation>();
		messageDao = new SlimMessageDao();
        conversationDao = new SlimConversationDao();
		//client.setMessageReceiver(this);
        //TODO: broadcast receiver?? SlimChatService 打开关闭会话。这里不需要处理接受消息的Intent，应该在activity中接收
	}

    /**
     * Get single getInstance.
     *
     * @return single getInstance
     */
    public static SlimChatManager getInstance() {
        return instance;
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
     * 会话存储Dao
     *
     * @return 会话存储Dao
     */
    public SlimConversationDao getConversationDao() {
        return conversationDao;
    }

    public Set<String> names() {
        return conversations.keySet();
    }

    public Collection<SlimConversation> conversations() {
        return conversations.values();
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
			conversation = new SlimConversation(userId, to);
			conversations.put(to, conversation);
		}
		return conversation;
	}

    public void close(String to) {
        //TODO: REMOVE from Dao
    }

	/**
	 * 获取聊天会话
	 * 
	 * @param to 会话对象ID
	 * @return 聊天会话
	 */
	public SlimConversation getConversation(String to) {
		//TODO: read db to init SlimConversation
        return conversations.get(to);
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
