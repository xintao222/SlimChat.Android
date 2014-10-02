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
package slimchat.android.ui.adapter;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimChatManager;
import slimchat.android.model.SlimUser;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 联系人列表Adapter
 * 
 * @author slimpp.io
 *
 */
public class BuddyAdapter extends ArrayAdapter<SlimUser> {

	private int resourceId;

	private LayoutInflater inflater;

	public BuddyAdapter(Context context, int resourceId,
                        List<SlimUser> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
		inflater = LayoutInflater.from(context);
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.nick = (TextView) convertView.findViewById(R.id.nick);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.unread = (TextView) convertView.findViewById(R.id.unread);
            convertView.setTag(holder);
        }
		SlimUser user = getItem(position);
		if (user != null) {
            holder.nick.setText(user.getNick() + "(" + user.getPresence() + ")");
			// tvStatus.setText(user.getStatus());
			SlimChat chat = SlimChatManager.getInstance()
					.getChat(user.getId());
			if (chat != null && chat.getUnread() > 0) {
                holder.unread.setText(String.valueOf(chat.getUnread()));
                holder.unread.setVisibility(View.VISIBLE);
			} else {
                holder.unread.setVisibility(View.INVISIBLE);
			}
		}
		return convertView;
	}

    private static class ViewHolder {
        ImageView avatar;
        TextView nick;
        TextView status;
        TextView unread;
    }


}
