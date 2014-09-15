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
package slimchat.android.client;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * 封装AsyncHttpClient
 * 
 * @author Feng Lee
 * 
 */
public class SlimHttpClient {

	/**
	 * Default socket timeout(seconds)
	 */
	private static final int DEFAULT_TIMEOUT = 30;

	/**
	 * Cookie Store
	 */
	private PersistentCookieStore cookieStore;

	/**
	 * HTTP Client
	 */
	private AsyncHttpClient httpc;

	SlimHttpClient() {
		httpc = new AsyncHttpClient();
		httpc.setTimeout(DEFAULT_TIMEOUT * 1000);
	}

	public void init(Context appContext) {
		// store http cookie
		cookieStore = new PersistentCookieStore(appContext);
		httpc.setCookieStore(cookieStore);
	}

	public int getTimeout() {
		return httpc.getTimeout() / 1000;
	}

	public void setTimeout(int timeout) {
		httpc.setTimeout(timeout * 1000);
	}

	public void post(String url, RequestParams params,
			SlimResponseHandler handler) {
		Log.d("SlimHttpClient", "POST " + url);
		httpc.post(url, params, handler);
	}

	public void get(String url, RequestParams params,
			SlimResponseHandler handler) {
		Log.d("SlimHttpClient", "GET " + url);
		httpc.get(url, params, handler);
	}

}
