package slimchat.android.service;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimPresence;

/**
 * Created by feng on 14-9-25.
 */
public class SlimChatReceiver implements MqttCallback {


    private final Context context;

    /**
     * 消息接收器
     *
     * @author slimpp.io
     *
     */
    public interface MessageReceiver {

        /**
         * 消息接收器
         */
        void messageArrived(SlimMessage message);

    }

    private MessageReceiver messageReceiver = null;

    /**
     * 现场接收器
     */
    public interface PresenceReceiver {

        /**
         * 接收现场
         *
         * @param presence 现场
         */
        void presenceArrived(SlimPresence presence);

    }

    private PresenceReceiver presenceReceiver = null;

    public SlimChatReceiver(Context context) {
        this.context = context;
    }

    public void setMessageReceiver(MessageReceiver receiver) {
        messageReceiver = receiver;
    }

    public void setPresenceReceiver(PresenceReceiver receiver) {
        presenceReceiver = receiver;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        Log.d("SlimChatClient", "MQTT Message from Topic: " + topic);
        Log.d("SlimChatClient", new String(msg.getPayload()));
        JSONObject json = new JSONObject(new String(msg.getPayload()));
        if (json.has("status")) {
            String status = json.getString("status");
            if ("ok".equalsIgnoreCase(status)) {
                JSONArray array = json.getJSONArray("presences");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    SlimPresence p = new SlimPresence(obj);
                    if (presenceReceiver != null) {
                        presenceReceiver.presenceArrived(p);
                    }
                }
                array = json.getJSONArray("messages");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    SlimMessage message = new SlimMessage(obj);
                    if (messageReceiver != null) {
                        messageReceiver.messageArrived(message);
                    }
                }
            } else {
                errorReceived(json);
            }
        } else {
            errorReceived(json);
        }
    }

    private void errorReceived(JSONObject json) {
        //TODO: how to handler error????
        Log.e("SlimChatReceiver", json.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        //TODO: nothing to do
    }

}
