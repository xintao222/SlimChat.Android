package slimchat.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import slimchat.android.service.SlimChatService;

/**
 * Service delegator
 *
 * 1. bind, unbind service
 * 2. call service
 *
 * Created by feng on 14-9-24.
 */
public class SlimServiceDelegator extends SlimContextAware {

    /**
     * Single Instance
     */
    private static SlimServiceDelegator instance = new SlimServiceDelegator();

    /**
     * Service
     */
    private SlimChatService chatService =  null;

    /**
     * Bound
     */
    private boolean serviceBound = false;


    /**
     * Service bound listener
     */
    public interface ServiceBoundCallback {

        void onServiceBound();

        void onServiceUnbound();
    }

    private ServiceBoundCallback boundCallback = null;
    /**
     * Service binder connection
     */
    protected ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SlimChatService.ServiceBinder binder = (SlimChatService.ServiceBinder) service;
            chatService = binder.getService();
            serviceBound = true;
            Log.d("Delegator", "ChatService is bound");
            if(boundCallback != null) boundCallback.onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            chatService = null;
            serviceBound = false;
            Log.d("Delegator", "ChatService is bound");
            if(boundCallback != null) boundCallback.onServiceUnbound();
        }
    };

    private SlimServiceDelegator() {
    }

    public static SlimServiceDelegator getInstance() {
        return instance;
    }

    public SlimChatService getChatService() {
        return chatService;
    }

    public void setBoundCallback(ServiceBoundCallback boundCallback) {
        this.boundCallback = boundCallback;
    }

    //TODO: should only call once???
    public void startService(ServiceBoundCallback callback) { //TODO: NEED Callback?
        setBoundCallback(callback);
        Intent intent = new Intent(appContext, SlimChatService.class);
        appContext.startService(intent);
        appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public boolean isServiceBound() {
        return serviceBound;
    }

    public boolean isServiceAlive() {
        return SlimChatService.isAlive();

    }

    public void stopService(ServiceBoundCallback callback) {
        setBoundCallback(callback);
        Intent intent = new Intent(appContext, SlimChatService.class);
        appContext.unbindService(connection);
        appContext.stopService(intent);
    }
}
