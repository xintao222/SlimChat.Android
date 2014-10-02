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

import slimchat.android.model.SlimBody;
import slimchat.android.model.SlimCallback;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimUris;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天会话实例
 *
 * @author slimpp.io
 */
public class SlimChat {

    private String thread = null;

    /**
     * 会话类型
     */
    public enum Type {
        USER,
        ROOM,
    }

    private static final String TAG = "SlimChat";

    /**
     * 当前用户ID
     */
    private final String uid;

    /**
     * 会话对象URI
     */
    private final Uri to;
    /**
     * 未读消息数
     */
    private int unread = 0;
    /**
     * 会话是否激活
     */
    private boolean active = false;
    /**
     * 最后一条聊天消息
     */
    private SlimMessage lastMessage = null;
    /**
     * TODO: 消息缓存, 应该存储APP的本地数据库。
     */
    private List<SlimMessage> messages;
    /**
     * 消息最终处理对象，一般为Activity界面类。
     */
    private OnMessageListener listener = null;

    /**
     * 创建聊天会话
     *
     * @param uid  当前用户ID
     * @param to   会话对象Uri
     */
    public SlimChat(String uid, Uri to) {
        this.uid = uid;
        this.to = to;
        this.messages = new ArrayList<SlimMessage>();
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * 会话对象ID
     *
     * @return 会话对象ID
     */
    public Uri getTo() {
        return to;
    }

    public SlimMessage getLastMessage() {
        return lastMessage;
    }

    /**
     * 设置消息监听对象
     *
     * @param listener 消息监听对象
     */
    public void setMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    /**
     * 读取全部消息
     *
     * @return 全部消息列表
     */
    public List<SlimMessage> getMessages() {
        return messages;
    }

    /**
     * 读取消息总数量
     *
     * @return 消息总数
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * 读取一条消息
     *
     * @param index 索引
     * @return 消息
     */
    public SlimMessage getMessage(int index) {
        return messages.get(index);
    }

    /**
     * 激活聊天会话，打开聊天窗口时调用。
     */
    public void activate() {
        unread = 0;
        active = true;
    }

    /**
     * 会话是否激活?
     *
     * @return 是否激活
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 去激活聊天会话，退出聊天窗口调用
     */
    public void deactivate() {
        active = false;
    }

    /**
     * 未读消息数
     *
     * @return 未读消息数
     */
    public int getUnread() {
        return unread;
    }

    /**
     * 清空未读消息
     */
    public void clearUnread() {
        unread = 0;
    }

    public void sendMessage(String input) throws Exception {
        final SlimMessage message = newMessage(input);

        //TODO: FIX Later
        SlimChatManager.getInstance().send(message, new SlimCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "sent " + message.getId());
            }

            @Override
            public void onFailure(String reason, Throwable error
            ) {
                Log.d(TAG, reason);
            }
        });
    }

    public SlimMessage newMessage(String text) {
        SlimMessage message = new SlimMessage(uid, SlimUris.parseId(to));
        message.setDirection(SlimMessage.Direction.SEND);
        SlimUris.Type toType = SlimUris.parseType(to);
        if (toType == SlimUris.Type.ROOM) {
            message.setType(SlimMessage.Type.GRPCHAT);
        } else {
            message.setType(SlimMessage.Type.CHAT);
        }
        message.setBody(new SlimBody(text));
        return message;
    }

    /**
     * 加入一条消息
     *
     * @param message 消息文本
     */
    public void addMessage(SlimMessage message) {
        messages.add(message);
        lastMessage = message;
        if (message.getDirection() == SlimMessage.Direction.SEND) {
            if (listener != null) listener.onMessageSent(message.getId());
        } else {//received
            if (!active) {
                unread++;
            }
            if (listener != null) listener.onMessageReceived(message.getId());
        }
    }



    /**
     * Message Listener
     */
    public interface OnMessageListener {

        /**
         * Message Sent
         *
         * @param msgID message ID
         */
        void onMessageSent(String msgID);

        /**
         * Message Received
         *
         * @param msgID message ID
         */
        void onMessageReceived(String msgID);

    }


}
