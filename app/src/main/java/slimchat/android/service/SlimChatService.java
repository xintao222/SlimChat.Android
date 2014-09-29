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
package slimchat.android.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import slimchat.android.SlimApiProvider;
import slimchat.android.SlimChatManager;
import slimchat.android.SlimConversation;
import slimchat.android.SlimRosterEvent;
import slimchat.android.SlimRosterManager;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.core.SlimPresence;
import slimchat.android.core.SlimUser;
import slimchat.android.http.SlimHttpClient.OnResponseHandler;
import slimchat.android.http.SlimHttpClient;
import slimchat.android.mqtt.SlimMqttClient;
import slimchat.android.service.SlimChatReceiver.MessageReceiver;
import slimchat.android.service.SlimChatReceiver.PresenceReceiver;

/**
 * Service to maintain push connection
 *
 * @author Feng Lee
 */
public class SlimChatService extends Service implements MqttCallback, MessageReceiver, PresenceReceiver{

    // Identifier for Intent
    static final String TAG = "SlimChatService";

    /**
     * Instance to judge if getService is alive
     */
    private static SlimChatService instance = null;

    private SlimChatSender sender;
    private String mqttd;
    private JSONObject userJson;
    private String userID;
    private String jsonpd;

    // An intent receiver to deal with changes in network connectivity
    private NetworkConnectionIntentReceiver networkConnectionMonitor;
    private boolean backgroundDataEnabled = true;
    private BroadcastReceiver backgroundDataPreferenceMonitor;

    public SlimHttpClient getHttpClient() {
        return httpClient;
    }


    /**
     * Service binder
     */
    public class ServiceBinder extends Binder {

        public SlimChatService getService() {
            return SlimChatService.this;
        }

    }

    private final IBinder binder = new ServiceBinder();

    /**
     * Service API Provider
     */
    private SlimApiProvider apiProvider;

    private SlimChatReceiver receiver;

    /**
     * HTTP Client
     */
    private SlimHttpClient httpClient = null;
    /**
     * MQTT Client
     */
    private SlimMqttClient mqttClient = null;

    /**
     * Current user
     */
    private SlimUser user;

    /**
     * Communication ticket
     */
    private String ticket;

    /**
     * Domain
     */
    private String domain;

    public static boolean isAlive() {
        return instance != null;
    }

    public String getUserID() {
        return user.getId();
    }

    public SlimChatService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        httpClient = new SlimHttpClient();
        httpClient.init(this);
        receiver = new SlimChatReceiver(this);
        receiver.setMessageReceiver(this);
        receiver.setPresenceReceiver(this);
        sender = new SlimChatSender(this);
        instance = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerBroadcastReceivers();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        receiver.setMessageReceiver(null);
        receiver.setPresenceReceiver(null);
        instance = null;

        unregisterBroadcastReceivers();

        // disconnect immediately
        try {
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public String getDomain() {
        return domain;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * API
     * ---------------------------------------------------------------------------------------------
     */
    public void setup(SlimApiProvider provider) {
        this.apiProvider = provider;
    }

    /**
     * Login with username and password
     *
     * @param username
     * @param password
     * @param callback
     */
    public void login(String username, String password,
                      final SlimCallback callback) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        OnResponseHandler handler = new LoginResponseHandler(this, callback);
        httpClient.call(apiProvider.authApi(), params, handler);
    }

    /**
     * Login Handler
     */
    private class LoginResponseHandler extends OnResponseHandler {

        LoginResponseHandler(SlimChatService service, SlimCallback callback) {
            super(service, callback);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("LoginReturn", response.toString());
            try {
                String status = response.getString("status");
                if ("ok".equalsIgnoreCase(status)) {
                    if (callback != null)
                        callback.onSuccess();
                } else if ("error".equalsIgnoreCase(status)) {
                    if (callback != null)
                        callback.onFailure(response.getString("message"), null);
                }
            } catch (JSONException e) {
                callback.onFailure("BadJson: " + response.toString(), e);
            }
        }

    }

    /**
     * User is online
     *
     * @param buddies
     * @param callback
     */
    public void online(Set<String> buddies, SlimCallback callback) {
        RequestParams params = new RequestParams();
        params.put("buddies", stringJoin(buddies, ","));
        OnlineResponseHandler handler = new OnlineResponseHandler(this,
                callback);
        httpClient.call(apiProvider.serviceApi("online"), params, handler);
    }

    private class OnlineResponseHandler extends OnResponseHandler {

        OnlineResponseHandler(SlimChatService service, SlimCallback callback) {
            super(service, callback);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("OnlineReturn", response.toString());
            try {
                SlimChatService.this.ready(response);
            } catch (JSONException e) {
                if (callback != null) {
                    callback.onFailure("bad response json", e);
                }
                return;
            }
            if (callback != null) {
                callback.onSuccess();
            }

            try {
                SlimChatService.this.connect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO: Online ready
     *
     * @param response
     * @throws JSONException
     */
    void ready(JSONObject response) throws JSONException {
        JSONObject conn = response.getJSONObject("connection");
        this.domain = conn.getString("domain");
        this.ticket = conn.getString("ticket");
        this.jsonpd = conn.getString("jsonpd");
        this.mqttd = conn.getString("mqttd");
        this.userJson = response.getJSONObject("user");
        this.userID = userJson.getString("id");
        SlimRosterManager.getInstance().feed(response);
    }

    /**
     * TODO: Try to connect
     *
     * @throws JSONException
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    void connect() throws MqttException {
        if (mqttClient == null) {
            mqttClient = new SlimMqttClient(this, mqttd, userID + "/android");
            mqttClient.setMqttCallback(this);
        }
        if (!mqttClient.isConnected()) {
            try {
                mqttClient.initConnOpts(this.domain, this.ticket);
                mqttClient.connect(new IMqttActionListener() {
                    @Override
                    public void onFailure(IMqttToken token, Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(IMqttToken token) {
                        Log.d("SlimChatClinet", "MQTT Connected");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO:
    void reconnect() {

    }

    /**
     * Fetch buddies
     *  @param ids
     * @param callback
     */
    public void fetchBuddies(Set<String> ids, final SlimCallback callback) {
        RequestParams params = new RequestParams();
        params.put("domain", domain);
        params.put("ticket", ticket);
        params.put("ids", stringJoin(ids, ","));
        BuddiesResponseHandler handler = new BuddiesResponseHandler(this,
                callback);
        httpClient.call(apiProvider.serviceApi("buddies"), params, handler);
    }

    /**
     * Buddies Response Handler
     */
    private class BuddiesResponseHandler extends OnResponseHandler {

        BuddiesResponseHandler(SlimChatService service, SlimCallback callback) {
            super(service, callback);
        }

        public void onSuccess(int statusCode, Header[] headers,
                              final JSONArray response) {
            Log.d("BuddiesReturn", response.toString());
            SlimChatService.this.onLoadBuddies(response);
            if (callback != null)
                callback.onSuccess();
        }

    }

    private void onLoadBuddies(JSONArray json) {
        //TODO:

    }


    /**
     * Send Message
     *
     * @param message
     * @param callback
     */
    public void send(SlimMessage message, SlimCallback callback) {
        //TODO: store Message first...
        OnResponseHandler handler = new OnResponseHandler(this, callback);
        sender.send(apiProvider.serviceApi("message"), message, handler);
    }

    /**
     * Publish presence
     *
     * @param presence
     * @param callback
     */
    public void publish(SlimPresence presence, SlimCallback callback) {
        OnResponseHandler handler = new OnResponseHandler(this, callback);
        sender.publish(apiProvider.serviceApi("presence"), presence, handler);
    }

    /**
     * User is offline
     *
     * @param callback
     */
    public void offline(SlimCallback callback) {
        RequestParams params = new RequestParams();
        params.put("domain", domain);
        params.put("ticket", ticket);
        OnResponseHandler handler = new OnResponseHandler(this, callback);
        httpClient.call(apiProvider.serviceApi("offline"), params, handler);
        stopSelf();
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    protected void onErrorReceived(JSONObject data) {
        // TODO: how to handle?
        Log.e("SlimChatClient", "Error Data: " + data.toString());
    }

    private String stringJoin(Set<String> buddies, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : buddies) {
            if (first)
                first = false;
            else
                sb.append(sep);
            sb.append(s);
        }
        return sb.toString();
    }

    public enum Status {

        ONLINE,

        //connect? disconnect?
        OFFLINE,
    }


    private void feedBuddies(JSONArray response) {
       //TODO: 存储
    }

    /**
     * TODO: this method should be called by SlimChatService
     * @param message 即时消息
     */
    @Override
    public void messageArrived(SlimMessage message) {
        //TODO:
        Log.d("SlimChatService", "messageArrived: " + message.toString());
            //1. Store the message
            //2. Open the conversation
            //3. Update unread
            //4. broadcast intent
            String from = message.getFrom();
            SlimConversation conversation = SlimChatManager.getInstance().open(from);
            conversation.addMessage(message);
            Log.d("SlimChatManager", "chat: " + conversation.toString()
                    + ", unread: " + conversation.getUnread());
        /*
		if (conversation.getUnread() > 0) {
			getRoster().updateUnread(from);
		}
		*/
    }

    /**
     * TODO: 处理现场消息。
     */
    @Override
    public void presenceArrived(SlimPresence presence) {
        Log.d("SlimChatService", "presenceArrived: " + presence.toString());
            //1. update database
            //2. update memory
            //3. broadcast intent
            /*
            String from = presence.getFrom();
            SlimUser buddy = rosterDao.getBuddy(from);
            if (buddy == null) {
                // TODO: should load buddy from server
            } else {
                // TODO: online, offline presence event??
                buddy.setPresence(presence.getType());
                buddy.setShow(presence.getShow());
                rosterDao.update(buddy);
                notifyListeners(new SlimRosterEvent(SlimRosterEvent.Type.UPDATED, this,
                        buddy.getId()));
            }
            */
    }

    @SuppressWarnings("deprecation")
    private void registerBroadcastReceivers() {
        if (networkConnectionMonitor == null) {
            networkConnectionMonitor = new NetworkConnectionIntentReceiver();
            registerReceiver(networkConnectionMonitor, new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION));
        }

        if (Build.VERSION.SDK_INT < 14 /**Build.VERSION_CODES.ICE_CREAM_SANDWICH**/) {
            // Support the old system for background data preferences
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            backgroundDataEnabled = cm.getBackgroundDataSetting();
            if (backgroundDataPreferenceMonitor == null) {
                backgroundDataPreferenceMonitor = new BackgroundDataPreferenceReceiver();
                registerReceiver(
                        backgroundDataPreferenceMonitor,
                        new IntentFilter(
                                ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED));
            }
        }
    }


    private void unregisterBroadcastReceivers(){
        if(networkConnectionMonitor != null){
            unregisterReceiver(networkConnectionMonitor);
            networkConnectionMonitor = null;
        }

        if (Build.VERSION.SDK_INT < 14 /**Build.VERSION_CODES.ICE_CREAM_SANDWICH**/) {
            if(backgroundDataPreferenceMonitor != null){
                unregisterReceiver(backgroundDataPreferenceMonitor);
            }
        }
    }

    //TODO: NETWORK MONITOR

    class ReconnectManager {

    }


    /**
     * @return whether the android getService can be regarded as online
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected() && backgroundDataEnabled) {
            return true;
        }

        return false;
    }

    /*
  * Called in response to a change in network connection - after losing a
  * connection to the server, this allows us to wait until we have a usable
  * data connection again
  */
    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // we protect against the phone switching off
            // by requesting a wake lock - we request the minimum possible wake
            // lock - just enough to keep the CPU running until we've finished
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
            wl.acquire();

            if (isOnline()) {
                // we have an internet connection - have another try at
                // connecting
                reconnect();
            } else {
                try {
                    mqttClient.offline();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            wl.release();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public String getTicket() {
        return ticket;
    }

    /**
     * Detect changes of the Allow Background Data setting - only used below
     * ICE_CREAM_SANDWICH
     */
    private class BackgroundDataPreferenceReceiver extends BroadcastReceiver {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm.getBackgroundDataSetting()) {
                if (!backgroundDataEnabled) {
                    backgroundDataEnabled = true;
                    // we have the Internet connection - have another try at
                    // connecting
                    reconnect();
                }
            } else {
                backgroundDataEnabled = false;
                try {
                    mqttClient.offline();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
