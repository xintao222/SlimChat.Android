package slimchat.android.service;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import slimchat.android.SlimChat;
import slimchat.android.SlimConversation;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimPresence;
import slimchat.android.model.SlimUris;

/**
 * Created by feng on 14-9-25.
 */
public class SlimChatReceiver implements MqttCallback {

    public static final String TAG = "SlimChatReceiver";
    private final SlimChatService service;

    public SlimChatReceiver(SlimChatService service) {
        this.service = service;
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
                    handlePresence(p);
                }
                array = json.getJSONArray("messages");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    SlimMessage message = new SlimMessage(obj);
                    handleMessage(message);
                }
            } else {
                errorReceived(json);
            }
        } else {
            errorReceived(json);
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {
        service.connectionLost(throwable);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        //TODO: nothing to do
    }

    private void errorReceived(JSONObject json) {
        //TODO: how to handler error????
        Log.e("SlimChatReceiver", json.toString());
    }

    /**
     * TODO: this method should be called by SlimChatService
     * @param message 即时消息
     */
    private void handleMessage(SlimMessage message) {
        Log.d(TAG, "handleMessage: " + message.toString());
        SlimChat.manager().messageReceived(message);
        //4. broadcast intent
    }

    /**
     * TODO: 处理现场消息。
     */
    private void handlePresence(SlimPresence presence) {
        Log.d(TAG, "presenceArrived: " + presence.toString());
        SlimChat.roster().presenceReceived(presence);
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


}
