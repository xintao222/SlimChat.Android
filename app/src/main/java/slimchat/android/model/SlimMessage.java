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
package slimchat.android.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文本消息类
 * 
 * @author slimpp.io
 * 
 */
public class SlimMessage extends SlimPacket {


    public enum Type {
        CHAT,
        GRPCHAT,
    }

    public enum Direction {
        SEND,
        RECEIVE
    }

	/**
	 * 文本消息类型： chat | grpchat
	 */
	private Type type = Type.CHAT;

    private Direction direction = Direction.SEND;

	/**
	 * 发送者昵称
	 */
	private String nick;

	/**
	 * 消息主体
	 */
	private SlimBody body;

	/**
	 * 消息style
	 */
	private String style = "";

	private boolean offline = false;

	/**
	 * TODO: 消息事件戳
	 */
	private long timestamp;

	public SlimMessage(String from, String to) {
		super(from, to);
	}

	public SlimMessage(JSONObject json) throws JSONException {
		super(json.getString("from"), json.getString("to"));
		if (json.has("nick")) {
			setNick(json.getString("nick"));
		} else {
			setNick(json.getString("from"));
		}
		setBody(new SlimBody(json.getString("body")));
		// TODO: timestamp
	}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setBody(SlimBody body) {
        this.body = body;
    }

    public SlimBody getBody() {
        return body;
    }

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String toString() {
		return String.format("SlimMessage[from: %s, to: %s, body: %s]", from,
				to, body);
	}

}
