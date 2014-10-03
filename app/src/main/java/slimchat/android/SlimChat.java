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

import android.app.NotificationManager;
import android.content.Context;
import android.os.PowerManager;

import slimchat.android.core.SlimApi;
import slimchat.android.model.SlimCallback;

/**
 * SlimChat is the main entry point of this library.
 * <p/>
 * Use SlimChat to:
 * <p/>
 * <ul>
 * <li>Init application context</li>
 * <li>Init SlimChatClient instance</li>
 * <li>Init SlimChatManager instance</li>
 * <li>Init SlimChatRoster instance</li>
 * <li>Init SlimChatSetting instance</li>
 * </ul>
 */
public class SlimChat {

    //application context
    static Context appContext = null;

    //slimchat client
    static SlimChatClient client = new SlimChatClient();

    //roster
    static SlimChatRoster roster = new SlimChatRoster();

    //chat manager
    static SlimChatManager manager = new SlimChatManager();

    //setting
    static SlimChatSetting setting = new SlimChatSetting();

    //notifier
    static SlimChatNotifier notifier = new SlimChatNotifier();

    private SlimChat() {}

    /**
     * @param context
     */
    public static void init(Context context) {
        appContext = context;
        manager.init(context);
        client.init(context);
        roster.init(context);
        setting.init(context);
    }


    /**
     * @param apiProvider
     */
    public static void setup(SlimApi.Provider apiProvider) {
        client.setApiProvier(apiProvider);
    }

    /**
     * Service is running?
     *
     * @return running
     */
    public static boolean isRunning() {
        return client.isServiceRunning();
    }

    /**
     * Start and bound chat service.
     *
     * @param callback service bound callback
     */
    public static void startup(final SlimCallback callback) {
        client.startService(new SlimChatClient.ServiceBoundCallback() {
            @Override
            public void onServiceBound() {
                callback.onSuccess();
            }

            @Override
            public void onServiceUnbound() {
                callback.onFailure("Service unbound", null);
            }
        });
    }

    /**
     * SlimChatClient's single instance
     *
     * @return SlimChatClient
     */
    public static SlimChatClient client() {
        return client;
    }

    /**
     * SlimChatManager's single instance
     *
     * @return manager
     */
    public static SlimChatManager manager() {
        return manager;
    }

    /**
     * SlimChatRoster's single instance
     *
     * @return SlimChatRoster
     */
    public static SlimChatRoster roster() {
        return roster;
    }

    /**
     * SlimChatSetting
     *
     * @return SlimChatSetting
     */
    public static SlimChatSetting setting() {
        return setting;
    }

    /**
     * Power Manager
     *
     * @return
     */
    public static PowerManager powerManager() {
        return (PowerManager) appContext
                .getSystemService(Context.POWER_SERVICE);
    }

    /**
     * NotificationManager
     *
     * @return
     */
    public static NotificationManager notificationManager() {
        return (NotificationManager) appContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
