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

import slimchat.android.SlimChatApiProvider;
import slimchat.android.SlimChatManager;

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
        Log.d(TAG, "SlimChat Application Created");
        SlimChatManager.getInstance().init(appContext);
        SlimChatManager.getInstance().setup(new ApiProvider());
    }

    static class ApiProvider implements SlimChatApiProvider {

        //init api provider
        final static String AUTH_URl = "http://slimpp.io/login";

        final static String API_URL = "http://slimpp.io/api.php/v1";

        final static Map<String, SlimChatApiProvider.SlimApi> API_MAP = new HashMap<String, SlimChatApiProvider.SlimApi>();

        static {
            API_MAP.put("online", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.POST, API_URL + "/online"));
            API_MAP.put("message", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.POST, API_URL + "/message"));
            API_MAP.put("presence", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.POST, API_URL + "/presence"));
            API_MAP.put("status", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.POST, API_URL + "/status"));
            API_MAP.put("buddies", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.GET, API_URL + "/buddies"));
            API_MAP.put("offline", new SlimChatApiProvider.SlimApi(SlimChatApiProvider.Method.POST, API_URL + "/offline"));
        }

        @Override
        public SlimApi authApi() {
            return new SlimApi(Method.POST, AUTH_URl + "?client=android");
        }

        @Override
        public SlimApi serviceApi(String action) {
            return API_MAP.get(action);
        }
    }


}


