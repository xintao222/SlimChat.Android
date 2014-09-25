package slimchat.android.service;

import com.loopj.android.http.RequestParams;

import java.util.LinkedList;
import java.util.Queue;

import slimchat.android.SlimApiProvider;
import slimchat.android.SlimApiProvider.SlimApi;
import slimchat.android.SlimChat;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimMessage;
import slimchat.android.core.SlimPresence;
import slimchat.android.http.SlimHttpClient.OnResponseHandler;

/**
 * Created by feng on 14-9-25.
 */
public class SlimChatSender {

    Queue<SlimMessage> pendingMessageQueue;

    Queue<SlimPresence> pendingPresenceQueue;

    SlimChatService service;

    public SlimChatSender(SlimChatService service) {
        this.service = service;
        pendingMessageQueue = new LinkedList<SlimMessage>();
        pendingPresenceQueue = new LinkedList<SlimPresence>();
    }

    public void send(SlimApi api, SlimMessage message, OnResponseHandler handler) {
        pendingMessageQueue.add(message);
        RequestParams params = new RequestParams();
        params.put("domain", service.getDomain());
        params.put("ticket", service.getTicket());
        params.put("to", message.getTo());
        params.put("from", message.getFrom());
        params.put("body", message.getBody());

        service.getHttpClient().call(api, params, handler);
        //TODO: delete from queue if success
    }

    public void publish(SlimApi api, SlimPresence presence, OnResponseHandler handler) {
        pendingPresenceQueue.add(presence);
        RequestParams params = new RequestParams();
        params.put("domain", service.getDomain());
        params.put("ticket", service.getTicket());
        params.put("show", presence.getShow());
        params.put("status", presence.getStatus());
        service.getHttpClient().call(api, params, handler);
        //TODO: delete from queue if success
    }
}
