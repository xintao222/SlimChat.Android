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
import slimchat.android.SlimChat;
import slimchat.android.SlimConversation;
import slimchat.android.SlimRosterEvent;
import slimchat.android.SlimRosterListener;
import slimchat.android.SlimRosterManager;
import slimchat.android.core.SlimUser;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 联系人列表Adapter
 * 
 * @author slimpp.io
 *
 */
public class SlimRosterAdapter extends BaseAdapter implements
		SlimRosterListener {

	private int resource;

	private Context context;

	private LayoutInflater inflater;

	private SlimRosterManager roster;

	public SlimRosterAdapter(Context context, int resource,
			SlimRosterManager roster) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.resource = resource;
		this.roster = roster;
	}

	@Override
	public int getCount() {
		return roster.getBuddyCount();
	}

	@Override
	public Object getItem(int position) {
		return roster.getBuddies().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = inflater.inflate(resource, null);
		TextView tvNick = (TextView) convertView.findViewById(R.id.nick);
		TextView tvStatus = (TextView) convertView.findViewById(R.id.status);
		TextView tvUnread = (TextView) convertView.findViewById(R.id.unread);
		SlimUser user = (SlimUser) getItem(position);
		if (user != null) {
			tvNick.setText(user.getNick() + "(" + user.getPresence() + ")");
			// tvStatus.setText(user.getStatus());
			SlimConversation conversation = SlimChat.instance().manager()
					.getConversation(user.getId());
			if (conversation != null && conversation.getUnread() > 0) {
				tvUnread.setText(String.valueOf(conversation.getUnread()));
				tvUnread.setVisibility(View.VISIBLE);
			} else {
				tvUnread.setVisibility(View.INVISIBLE);
			}
		}
		return convertView;
	}

	@Override
	public void rosterChanged(SlimRosterEvent evnet) {
		this.notifyDataSetChanged();
	}

}
