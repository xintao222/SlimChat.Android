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
package slimchat.android.adapter;

import slimchat.android.R;
import slimchat.android.SlimConversation;
import slimchat.android.core.SlimMessage;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 消息Adapter
 * 
 * @author slimpp.io
 *
 */
public class MessageAdapter extends BaseAdapter {

	private int resourceId;

	private Context context;

	private LayoutInflater inflater;

	private SlimConversation conversation;

	public MessageAdapter(Context context, int resourceId,
                          SlimConversation conversation) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.resourceId = resourceId;
		this.conversation = conversation;
	}

	@Override
	public int getCount() {
		return conversation.getMessageCount();
	}

	@Override
	public Object getItem(int position) {
		return conversation.getMessage(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(resourceId, parent, false);
		}
		// TODO: XML
		LinearLayout messageContainer = (LinearLayout) convertView
				.findViewById(R.id.messageContainer);
		SlimMessage message = (SlimMessage) getItem(position);
		TextView chatText = (TextView) convertView
				.findViewById(R.id.messageText);
		chatText.setText(message.getBody().getText());
		boolean isSendMsg = message.getTo().equals(conversation.getTo());
		chatText.setBackgroundResource(isSendMsg ? R.drawable.bubble_a
				: R.drawable.bubble_b);
		messageContainer.setGravity(isSendMsg ? Gravity.RIGHT : Gravity.LEFT);
		return convertView;
	}

}
