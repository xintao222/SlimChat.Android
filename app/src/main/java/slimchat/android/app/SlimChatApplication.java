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
package slimchat.android.app;

import slimchat.android.SlimChat;
import slimchat.android.core.SlimApi;
import slimchat.android.core.SlimApi.Method;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * SlimChat演示App.
 *
 * @author slimpp.io
 */
public class SlimChatApplication extends Application {

    final static String TAG = "SlimChatApplication";

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        Log.d(TAG, "SlimConversation Application Created");
        SlimChat.init(appContext);
        SlimChat.setup(new SlimppApiProvider());
    }

    static class SlimppApiProvider implements SlimApi.Provider{

        //init api provider
        final String authUrl = "http://slimpp.io/login";

        final String apiUrl = "http://slimpp.io/api.php/v1";

        final Map<String, SlimApi> apiMap = new HashMap<String, SlimApi>();

        SlimppApiProvider() {
            apiMap.put("online", new SlimApi(Method.POST, apiUrl + "/online"));
            apiMap.put("message", new SlimApi(Method.POST, apiUrl + "/message"));
            apiMap.put("presence", new SlimApi(Method.POST, apiUrl + "/presence"));
            apiMap.put("status", new SlimApi(Method.POST, apiUrl + "/status"));
            apiMap.put("buddies", new SlimApi(Method.GET, apiUrl + "/buddies"));
            apiMap.put("offline", new SlimApi(Method.POST, apiUrl + "/offline"));
        }

        @Override
        public SlimApi authApi() {
            return new SlimApi(Method.POST, authUrl + "?client=android");
        }

        @Override
        public SlimApi serviceApi(String action) {
            return apiMap.get(action);
        }
    }


}


