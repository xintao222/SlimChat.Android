/**
 *
 * The MIT License (MIT)
 *
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
package slimchat.android.proto.mqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SlimMqttClient implements  MqttCallback{

    public enum State {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
    }

    final static String TAG = "SlimMqttClient";

    final static int CONNECT_TIMEOUT = 60;

    final static int KEEP_ALIVE = 300;

    private Context context;

	private MqttAsyncClient mqttc;

    private AlarmPingSender pingSender;

    private State state = null;

    /**
     * Connection Options
     */
    private boolean cleanSession = true;

    private MqttConnectOptions connOpts = null;

    //callback
    private MqttCallback callback = null;

	public SlimMqttClient(Context context, String serverURI, String clientId) throws MqttException {
        this.context = context;
        this.pingSender = new AlarmPingSender(context);
        this.mqttc = new MqttAsyncClient(serverURI, clientId, new MemoryPersistence(), pingSender);
        this.mqttc.setCallback(this);
	}

    public void initConnOpts(String username, String password) {
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(cleanSession);
        connOpts.setConnectionTimeout(CONNECT_TIMEOUT);
        connOpts.setKeepAliveInterval(KEEP_ALIVE);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
    }

    public void setMqttCallback(MqttCallback callback) {
        this.callback = callback;
    }

	public synchronized void connect(IMqttActionListener listener) throws MqttException {
            mqttc.connect(connOpts, this, listener);
	}

    public boolean isConnected() {
        return mqttc.isConnected();
    }

	public synchronized  void disconnect(IMqttActionListener listener) throws MqttException {
        if(isConnected()) mqttc.disconnect(3000, listener);
	}

	public IMqttDeliveryToken publish(String topic, byte[] payload, int qos,
			boolean retained, IMqttActionListener callback) throws MqttException {
		return mqttc.publish(topic, payload, qos, retained, this, callback);
	}

	public void subscribe(final String topic, final int qos, IMqttActionListener callback) throws MqttException {
        mqttc.subscribe(topic, qos, this, callback);
	}

	public void unsubscribe(final String topic, IMqttActionListener callback) throws MqttException {
        mqttc.unsubscribe(topic, this, callback);
	}


    @Override
    public void connectionLost(Throwable cause) {
        callback.connectionLost(cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        callback.messageArrived(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, token.toString());
    }

    public void offline() throws MqttException {
        mqttc.disconnect();
        Exception e = new Exception("Android offline");
        connectionLost(e);
    }

    public void close() throws MqttException {
        mqttc.close();
    }

    /**
     * Default ping sender implementation on Android. It is based on AlarmManager.
     *
     * <p>This class implements the {@link org.eclipse.paho.client.mqttv3.MqttPingSender} pinger interface
     * allowing applications to send ping packet to server every keep alive interval.
     * </p>
     *
     * @see org.eclipse.paho.client.mqttv3.MqttPingSender
     */
    class AlarmPingSender implements MqttPingSender {
        // Identifier for Intents, log messages, etc..
        static final String TAG = "AlarmPingSender";

        static final String PING_ACTION = "SlimMqttClient.AlarmPingSender";

        private ClientComms comms;
        private Context context;
        private BroadcastReceiver alarmReceiver;
        private AlarmPingSender that;
        private PendingIntent pendingIntent;
        private volatile boolean hasStarted = false;

        public AlarmPingSender(Context context) {
            if (context == null) {
                throw new IllegalArgumentException(
                        "Neither getService nor client can be null.");
            }
            this.context = context;
            that = this;
        }

        @Override
        public void init(ClientComms comms) {
            this.comms = comms;
            this.alarmReceiver = new AlarmReceiver(context, comms);
        }

        @Override
        public void start() {
            context.registerReceiver(alarmReceiver, new IntentFilter(PING_ACTION));

            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                    PING_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);

            schedule(comms.getKeepAlive());
            hasStarted = true;
        }

        @Override
        public void stop() {
            // Cancel Alarm.
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Service.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            Log.d(TAG, "Unregister alarmreceiver to MqttService"+comms.getClient().getClientId());
            if(hasStarted){
                hasStarted = false;
                try{
                    context.unregisterReceiver(alarmReceiver);
                }catch(IllegalArgumentException e){
                    //Ignore unregister errors.
                }
            }
        }

        @Override
        public void schedule(long delayInMilliseconds) {
            long nextAlarmInMilliseconds = System.currentTimeMillis()
                    + delayInMilliseconds;
            Log.d(TAG, "Schedule next alarm at " + nextAlarmInMilliseconds);
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Service.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds,
                    pendingIntent);
        }

    }

    /*
       * This class sends PingReq packet to MQTT broker
       */
    class AlarmReceiver extends BroadcastReceiver {

        private final Context context;
        private final ClientComms comms;
        private WakeLock wakelock;
        private String wakeLockTag = SlimMqttClient.TAG + ".AlarmPingSender.WakeLog";

        public AlarmReceiver(Context context, ClientComms comms) {
            this.context = context;
            this.comms = comms;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            // According to the docs, "Alarm Manager holds a CPU wake lock as
            // long as the alarm receiver's onReceive() method is executing.
            // This guarantees that the phone will not sleep until you have
            // finished handling the broadcast.", but this class still get
            // a wake lock to wait for ping finished.
            int count = intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, -1);
            Log.d(TAG, "Ping " + count + " times.");

            Log.d(TAG, "Check time :" + System.currentTimeMillis());
            IMqttToken token = comms.checkForActivity();

            // No ping has been sent.
            if (token == null) {
                return;
            }

            // Assign new callback to token to execute code after PingResq
            // arrives. Get another wakelock even receiver already has one,
            // release it until ping response returns.
            if (wakelock == null) {
                PowerManager pm = (PowerManager) context
                        .getSystemService(Service.POWER_SERVICE);
                wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        wakeLockTag);
            }
            wakelock.acquire();
            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Success. Release lock(" + wakeLockTag + "):"
                            + System.currentTimeMillis());
                    //Release wakelock when it is done.
                    if(wakelock != null && wakelock.isHeld()){
                        wakelock.release();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "Failure. Release lock(" + wakeLockTag + "):"
                            + System.currentTimeMillis());
                    //Release wakelock when it is done.
                    if(wakelock != null && wakelock.isHeld()){
                        wakelock.release();
                    }
                }
            });
        }
    }


}
