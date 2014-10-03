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
package slimchat.android.proto.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import slimchat.android.core.SlimApi;
import slimchat.android.model.SlimCallback;

/**
 * /**
 * 封装AsyncHttpClient
 *
 * @author Feng Lee <feng.lee@slimpp.io>
 */
public class SlimHttpClient {

    /**
     * HTTP请求的响应处理器
     *
     * @author slimpp.io
     *
     */
    public static class OnResponseHandler extends JsonHttpResponseHandler {

        protected final Context context;

        protected final SlimCallback callback;

        public OnResponseHandler(Context context, SlimCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public void onSuccess(int statusCode, Header[] headers,
                              final JSONObject response) {
            if (callback != null)
                callback.onSuccess();
        }

        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable error) {
            if (callback != null)
                callback.onFailure(String.format("statusCode: %d, responseString: %s", statusCode, responseString), error);
        }
    }

    /**
     * Default socket timeout(seconds)
     */
    private static final int DEFAULT_TIMEOUT = 60;

    /**
     * Cookie Store
     */
    private PersistentCookieStore cookieStore;

    /**
     * HTTP Client
     */
    private AsyncHttpClient httpc;

    public SlimHttpClient() {
        httpc = new AsyncHttpClient();
        httpc.setTimeout(DEFAULT_TIMEOUT * 1000);
    }

    public void init(Context appContext) {
        // store http cookie
        cookieStore = new PersistentCookieStore(appContext);
        httpc.setCookieStore(cookieStore);
    }

    /**
     * Timeout
     *
     * @return timeout
     */
    public int getTimeout() {
        return httpc.getTimeout() / 1000;
    }

    /**
     * Set timeout(seconds)
     *
     * @param timeout
     */
    public void setTimeout(int timeout) {
        httpc.setTimeout(timeout * 1000);
    }

    /**
     * HTTP Call
     * @param slimApi api
     * @param params params
     * @param handler callback handler
     */
    public void call(SlimApi slimApi, RequestParams params, OnResponseHandler handler) {
        switch (slimApi.getMethod()) {
            case POST:
                post(slimApi.getUrl(), params, handler);
                break;
            case GET:
                post(slimApi.getUrl(), params, handler);
                break;
            case PUT:
                put(slimApi.getUrl(), params, handler);
                break;
            case DELETE:
                delete(slimApi.getUrl(), params, handler);
                break;
        }
    }

    /**
     * HTTP POST
     *
     * @param url
     * @param params
     * @param handler
     */
    public void post(String url, RequestParams params,
                     OnResponseHandler handler) {
        Log.d("SlimHttpClient", "POST " + url);
        httpc.post(url, params, handler);
    }

    /**
     * HTTP GET
     *
     * @param url
     * @param params
     * @param handler
     */
    public void get(String url, RequestParams params,
                    OnResponseHandler handler) {
        Log.d("SlimHttpClient", "GET " + url);
        httpc.get(url, params, handler);
    }

    /**
     * HTTP PUT
     *
     * @param url
     * @param params
     * @param handler
     */
    public void put(String url, RequestParams params, OnResponseHandler handler) {
        Log.d("SlimHttpClient", "PUT " + url);
        httpc.put(url, params, handler);
    }

    /**
     * HTTP Delete
     *
     * @param url
     * @param params
     * @param handler
     */
    public void delete(String url, RequestParams params, OnResponseHandler handler) {
        Log.d("SlimHttpClient", "DELETE " + url);
        httpc.delete(url, handler);
    }

}
