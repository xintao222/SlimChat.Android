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

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import slimchat.android.core.SlimCallback;

/**
 * 上线请求的响应处理
 * 
 * @author slimpp.io
 * 
 */
class OnlineResponseHandler extends SlimResponseHandler {

	OnlineResponseHandler(SlimChatClient client, SlimCallback callback) {
		super(client, callback);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		try {
			client.ready(response);
		} catch (JSONException e) {
			if (callback != null) {
				callback.onFailure("bad response json", e, response);
			}
			return;
		}
		if (callback != null) {
			callback.onSuccess(response);
		}

		client.connect();
	}

}
