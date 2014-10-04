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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import slimchat.android.core.SlimApi;
import slimchat.android.model.SlimCallback;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimPresence;
import slimchat.android.service.SlimChatService;

/**
 * SlimChat client to start service, send message, publish presence.
 *
 * @author  feng.lee@slimpp.io
 */
public class SlimChatClient extends SlimContextAware {

    static final String TAG = "SlimChatClient";

    //api provider
    private SlimApi.Provider apiProvider = null;


    /**
     * Service bound callback
     */
    interface ServiceBoundCallback {

        void onServiceBound();

        void onServiceUnbound();
    }

    //service bound?
    private boolean serviceBound = false;

    //chat service
    private SlimChatService service = null;

    private ServiceBoundCallback boundCallback = null;

    /**
     * Service binder connection
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "ChatService is bound");
            SlimChatService.ServiceBinder binder = (SlimChatService.ServiceBinder) service;
            SlimChatClient.this.service = binder.getService();
            SlimChatClient.this.service.setup(apiProvider);
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

    SlimChatClient() {}

    /**
     * Set ApiProvider
     *
     * @param apiProvider
     */
    public void setApiProvier(SlimApi.Provider apiProvider) {
        this.apiProvider = apiProvider;
    }

    /**
     * ChatService
     *
     * @return chat service
     */
    public SlimChatService getService() {
        return service;
    }

    public void startService(ServiceBoundCallback boundCallback) {
        if(isServiceRunning()) throw new IllegalStateException("Service is running.");
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

    public void stopService(ServiceBoundCallback boundCallback) {
        this.boundCallback = boundCallback;
        Intent intent = new Intent(appContext, SlimChatService.class);
        appContext.unbindService(connection);
        appContext.stopService(intent);
    }


    public boolean isConnected() {
        return service.isConnected();
    }

    public boolean isConnecting() {
        return service.isConnecting();
    }


    /**
     * Wrap service login
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
     * Wrap service online
     *
     * @param callback
     */
    public void online(Set<String> buddies, final SlimCallback callback) throws Exception {
        getService().online(buddies, callback);
    }


    /**
     * Wrap service getBuddies
     * @param id
     */
    public void getBuddies(String id) {
        Set<String> ids = new HashSet<String>();
        ids.add(id);
        getService().getBuddies(ids, null);
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
     * Send Message
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

}
