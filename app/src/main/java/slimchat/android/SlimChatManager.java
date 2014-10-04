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

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import slimchat.android.db.SlimChatDb;
import slimchat.android.db.SlimMessageDb;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimUris;
import slimchat.android.model.SlimUser;

/**
 * SlimChatManager manage chat conversation.
 *
 * @author feng.lee@slimpp.io
 */
public class SlimChatManager extends SlimContextAware {

    static final String TAG = "SlimChatManager";


    public interface OnChatListener {
        void onChatOpen(Uri to);
        void onChatClose(Uri to);
        void onChatUpdate(Uri to);

    }

    /**
     * Converation Database
     */
    private SlimChatDb chatDb = null;

    /**
     * Conversation cache
     */
    private Map<Uri, SlimConversation> chats;

    /**
     * Message Database
     */
    private SlimMessageDb messageDb = null;

    /**
     * Current User
     */
    private SlimUser currentUser = null;

    /**
     * Listeners
     */
    private List<OnChatListener> listeners;

    SlimChatManager() {
        chatDb = new SlimChatDb();
        messageDb = new SlimMessageDb();
        chats = new HashMap<Uri, SlimConversation>();
        listeners = new ArrayList<OnChatListener>();
    }

    /**
     * Message Database
     *
     * @return message database
     */
    public SlimMessageDb messageDao() {
        return messageDb;
    }

    /**
     * Conversation Database
     *
     * @return conversation database
     */
    public SlimChatDb getChatDb() {
        return chatDb;
    }

    /**
     * Get all chat conversations
     *
     * @return conversations
     */
    public List<SlimConversation> getChats() {
        return new ArrayList<SlimConversation>(chats.values());
    }

    /**
     * Get chat conversation
     *
     * @param to to uri
     * @return chat conversation
     */
    public SlimConversation getChat(String to) {
        return chats.get(to);
    }

    /**
     * Open chat conversation
     *
     * @param to user or room uri
     * @return conversation
     */
    public synchronized SlimConversation open(Uri to) {
        SlimConversation chat = chats.get(to);
        if (chat == null) {
            chat = new SlimConversation(this, to);
            chats.put(to, chat);
            chatDb.add(to, chat);
            for(OnChatListener l : listeners) {
                l.onChatOpen(to);
            }
        }
        return chat;
    }

    /**
     * Close chat conversation
     *
     * @param to
     */
    public synchronized void close(Uri to) {
        SlimConversation chat = chats.get(to);
        if (chat != null) {
            chatDb.remove(to, chat);
            chats.remove(to);
            for(OnChatListener l : listeners) {
                l.onChatOpen(to);
            }
        }
    }

    /**
     * Get current user
     *
     * @return current user
     */
    public SlimUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Set current user
     *
     * @param user user
     */
    public void setCurrentUser(SlimUser user) {
        this.currentUser = user;
    }

    /**
     * Get current user id
     *
     * @return current user id
     */
    public String currentUid() {
        return currentUser.getId();
    }

    /**
     * Add listener
     */
    public void addListener(OnChatListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener
     */
    public void removeListener(OnChatListener listener) {
        listeners.remove(listener);
    }

    public void messageReceived(SlimMessage message) {
        String from = message.getFrom();
        Uri uri = SlimUris.userUri(from);
        SlimConversation chat = SlimChat.manager().open(uri);
        chat.addMessage(message);
        for(OnChatListener l : listeners) {
            l.onChatUpdate(uri);
        }
    }

}


