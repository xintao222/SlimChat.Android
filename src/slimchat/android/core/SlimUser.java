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
package slimchat.android.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SlimChat用户对象
 * 
 * @author slimpp.io
 * 
 */
public class SlimUser extends SlimObject {

	protected String presence = "online";

	protected String avatar;

	protected String status = "";

	private String show = "available";

	private String url = "#";

	public SlimUser(String id, String nick) {
		super(id);
		this.setNick(nick);
	}

	public SlimUser(JSONObject json) throws JSONException {
		super(json.getString("id"));
		setNick(json.getString("nick"));
		setPresence(json.getString("presence"));
		setShow(json.getString("show"));
		if (json.has("avatar")) {
			setAvatar(json.getString("avatar"));
		}
		if (json.has("url")) {
			setUrl(json.getString("url"));
		}
		if (json.has("status")) {
			setStatus("status");
		}
	}

	public String getPresence() {
		return presence;
	}

	public void setPresence(String presence) {
		this.presence = presence;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getShow() {
		return this.show;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return String.format(
				"SlimUser[id=%s, nick=%s, presence=%s, show=%s, avatar=%s]",
				id, nick, presence, show, avatar);
	}

}
