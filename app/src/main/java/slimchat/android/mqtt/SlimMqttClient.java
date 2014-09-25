package slimchat.android.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Wrap MqttAsyncClient
 *
 * Created by feng on 14-9-23.
 */
public class SlimMqttClient extends BroadcastReceiver implements
        MqttCallback {


    /**
     * Client state
     */
    private ConnectionState state = ConnectionState.INITIALIZED;


    /**
     * MQTTD Server
     */
    private String mqttd;

    /**
     * MQTT Client
     */
    private MqttAndroidClient mqttc = null;

    public enum ConnectionState {

        INITIALIZED,

        CONNECTING,

        CONNERROR,

        CONNECTED,

        DISCONNECTED,

    }


    public boolean isConnected() {
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }


    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }



    @Override
    public void connectionLost(Throwable e) {
        Log.e("SlimChatClient", "MQTT Disconnected!");
        e.printStackTrace();
        //setState(ConnectionState.DISCONNECTED);
        // how to reconnect?
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("SlimChatClient", token.toString());
    }


}
