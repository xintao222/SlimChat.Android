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
 * Chat Conversation.
 *
 * @author feng.lee@slimpp.io
 */
public class SlimConversation {

    static final String TAG = "SlimConversation";

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

    /**
     * Conversation Type
     */
    public enum Type {
        USER,
        ROOM,
    }

    private final SlimChatManager manager;

    /**
     * chat to URI
     */
    private final Uri to;


    //chat thread
    private String thread = null;

    /**
     * unread count
     */
    private int unread = 0;

    /**
     * active
     */
    private boolean active = false;

    /**
     * last message
     */
    private SlimMessage lastMessage = null;

    /**
     * message cache, should be in database
     */
    private List<SlimMessage> messages;

    /**
     * message listener, activity to implement
     */
    private OnMessageListener listener = null;

    /**
     * Create a chat conversation
     * @param manager chat manager
     * @param to chat to
     */
    public SlimConversation(SlimChatManager manager, Uri to) {
        this.manager = manager;
        this.to = to;
        this.messages = new ArrayList<SlimMessage>();
    }

    /**
     * Get chat thread
     *
     * @return chat thread
     */
    public String getThread() {
        return thread;
    }

    /**
     * Set chat thread
     *
     * @param thread
     */
    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * Get uri chatting with
     *
     * @return uri chatting with
     */
    public Uri getTo() {
        return to;
    }

    /**
     * Get Last Message
     *
     * @return last message
     */
    public SlimMessage getLastMessage() {
        return lastMessage;
    }

    /**
     * Set message listener
     *
     * @param listener message listener
     */
    public void setMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    /**
     * Get all messages
     *
     * @return all messages
     */
    public List<SlimMessage> getMessages() {
        return messages;
    }

    /**
     * Get message count
     *
     * @return message count
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Get one message
     *
     * @param index
     * @return messge
     */
    public SlimMessage getMessage(int index) {
        return messages.get(index);
    }

    /**
     * Activate this conversation when chat activity resumed.
     */
    public void activate() {
        cleanUnread();
        active = true;
    }

    /**
     * active?
     *
     * @return active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Deactiate this conversation when chat activity paused.
     */
    public void deactivate() {
        active = false;
    }

    /**
     * unread count
     *
     * @return unread count
     */
    public int getUnread() {
        return unread;
    }

    /**
     * clean unread
     */
    public synchronized void cleanUnread() {
        if(unread > 0) {
            unread = 0;
            manager.onChatUpdate(to);
        }
    }

    /**
     * Send message
     *
     * @param input
     * @throws Exception
     */
    public void sendMessage(String input) throws Exception {

        final SlimMessage message = newMessage(input);
        this.addMessage(message);

        SlimChat.client().send(message, new SlimCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "sent " + message.getId());
            }

            @Override
            public void onFailure(String reason, Throwable error
            ) {
                error.printStackTrace();
                Log.d(TAG, reason);
            }

        });
    }

    /**
     * Create a message
     *
     * @param text message text body
     * @return message
     */
    public SlimMessage newMessage(String text) {
        SlimMessage message = new SlimMessage(manager.currentUid(), SlimUris.parseId(to));
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
     * add one message
     *
     * @param message
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


}
