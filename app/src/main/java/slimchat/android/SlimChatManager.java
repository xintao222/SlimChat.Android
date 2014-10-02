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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import slimchat.android.SlimChatApiProvider;
import slimchat.android.model.SlimCallback;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimPresence;
import slimchat.android.db.SlimChatDao;
import slimchat.android.db.SlimMessageDao;
import slimchat.android.service.SlimChatService;


/**
 * SlimChat入口类。
 * <p/>
 * <p>
 * Android APP通过SlimChat单例集成即时消息服务。SlimChat维护全局的ChatManger, RosterManger,
 * ChatService实例:<br>
 * <ul>
 * <li>SlimChatService: 客户端类，网络连接和发送、接收消息。</li>
 * <li>SlimChatManger: 聊天会话管理类，打开关闭聊天会话，处理会话消息</li>
 * <li>SlimRosterManager: 好友管理类, 管理好友泪飙</li>
 * </ul>
 * </p>
 *
 * @author slimpp.io
 *         <p/>
 *         <p/>
 *         /**
 *         聊天会话管理类。负责打开、管理、激活、去激活聊天会话窗口，发送消息、接收分发消息。
 * @author slimpp.io
 */
public class SlimChatManager extends SlimContextAware {

    static final String TAG = "SlimChatManager";

    private static final SlimChatManager instance = new SlimChatManager();
    /**
     * 会话存储
     */
    private final SlimChatDao chatDao;
    /**
     * 会话列表
     */
    private Map<Uri, SlimChat> chats;
    /**
     * 消息存储
     */
    private SlimMessageDao messageDao = null;

    //roster manager
    private SlimRosterManager roster = null;

    //global settings
    private SlimChatSetting setting = null;

    //chat service
    private SlimChatService service = null;

    //service bound?
    private boolean serviceBound = false;

    //cache api provider
    private SlimChatApiProvider apiProvider;
    private ServiceBoundCallback boundCallback = null;
    /**
     * Service binder connection
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "ChatService is bound");
            SlimChatService.ServiceBinder binder = (SlimChatService.ServiceBinder) service;
            SlimChatManager.this.service = binder.getService();
            SlimChatManager.this.service.setup(apiProvider);
            serviceBound = true;
            if (boundCallback != null) boundCallback.onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ChatService is unbound");
            service = null;
            serviceBound = false;
            if (boundCallback != null) boundCallback.onServiceUnbound();
        }
    };

    private SlimChatManager() {
        chats = new HashMap<Uri, SlimChat>();
        messageDao = new SlimMessageDao();
        chatDao = new SlimChatDao();

    }

    /**
     * Get single getInstance.
     *
     * @return single getInstance
     */
    public static SlimChatManager getInstance() {
        return instance;
    }

    public boolean isOffline() {
        return !service.isOnline();
    }

    @Override
    public void init(Context appContext) {
        super.init(appContext);
        getRoster().init(appContext);
        getSetting().init(appContext);
    }

    /**
     * API Provider
     *
     * @param provider
     */
    public void setup(SlimChatApiProvider provider) {
        this.apiProvider = provider;
    }

    /**
     * /**
     * RosterManager
     *
     * @return roster manager
     */
    public SlimRosterManager getRoster() {
        if (roster == null) {
            roster = SlimRosterManager.getInstance();
        }
        return roster;
    }

    /**
     * ChatSetting
     *
     * @return chatSetting
     */
    public SlimChatSetting getSetting() {
        if (setting == null) {
            setting = SlimChatSetting.getInstance();
        }
        return setting;
    }

    /**
     * ChatService
     *
     * @return chat service
     */
    public SlimChatService getService() {
        return service;
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
     * Start
     *
     * @param callback
     */
    public void start(final ServiceBoundCallback callback) {
        startService(callback);

    }

    private void startService(ServiceBoundCallback boundCallback) {
        this.boundCallback = boundCallback;
        Intent intent = new Intent(appContext, SlimChatService.class);
        appContext.startService(intent);
        appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public boolean isServiceBound() {
        return serviceBound;
    }

    public boolean isServiceRunning() {
        return serviceBound && SlimChatService.isAlive();
    }

    public void stop(final ServiceBoundCallback callback) {
        this.stopService(callback);
    }

    private void stopService(ServiceBoundCallback boundCallback) {
        this.boundCallback = boundCallback;
        Intent intent = new Intent(appContext, SlimChatService.class);
        appContext.unbindService(connection);
        appContext.stopService(intent);
    }

    /**
     * Wrap client login
     *
     * @param username
     * @param password
     * @param callback
     */
    public void login(String username, String password, SlimCallback callback) throws Exception {
        getService().login(username, password, callback);
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
        getService().online(buddies, callback);
    }

    /**
     * Send Message
     *
     * @param message messge
     * @throws Exception
     */
    public void send(SlimMessage message) throws Exception {
        this.send(message, null);
    }

    /**
     * 发送聊天消息
     *
     * @param message  聊天消息
     * @param callback 回调
     */
    public void send(SlimMessage message, SlimCallback callback) throws Exception {
        getService().send(message, callback);
    }

    /**
     * Publish presence
     *
     * @param presence
     * @param callback
     * @throws Exception
     */
    public void publish(SlimPresence presence, SlimCallback callback) throws Exception {
        getService().publish(presence, callback);
    }

    /**
     * Wrap client offline
     *
     * @param callback
     */
    public void offline(final SlimCallback callback) throws Exception {
        getService().offline(callback);
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
    public SlimChatDao getChatDao() {
        return chatDao;
    }

    public Set<Uri> chatUris() {
        return chats.keySet();
    }

    public List<SlimChat> getChats() {
        return new ArrayList<SlimChat>(chats.values());
    }

    /**
     * 打开聊天会话
     *
     * @param to 会话对象ID
     * @return 聊天会话实例
     */
    public SlimChat open(Uri to) {
        SlimChat chat = chats.get(to);
        if (chat == null) {
            chat = new SlimChat(service.getUserID(), to);
            chats.put(to, chat);
        }
        return chat;
    }

    public void close(Uri to) {
        //TODO: REMOVE from Dao
    }

    /**
     * 获取聊天会话
     *
     * @param to 会话对象ID
     * @return 聊天会话
     */
    public SlimChat getChat(String to) {
        //TODO: read db to init SlimChat
        return chats.get(to);
    }

    /**
     * Service bound callback
     */
    public interface ServiceBoundCallback {

        void onServiceBound();

        void onServiceUnbound();
    }

}
