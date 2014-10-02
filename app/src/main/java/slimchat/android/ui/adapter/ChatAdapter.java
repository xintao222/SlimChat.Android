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

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimRosterManager;
import slimchat.android.model.SlimMessage;
import slimchat.android.model.SlimRoom;
import slimchat.android.model.SlimUris;
import slimchat.android.model.SlimUser;

/**
 * ConversationFragment Adapter
 *
 * Created by feng on 14-9-23.
 */
public class ChatAdapter extends ArrayAdapter<SlimChat> {

    private int resourceId;

    private LayoutInflater inflater;

    public ChatAdapter(Context context, int resourceId, List<SlimChat> objects) {
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
            holder.unread = (TextView) convertView.findViewById(R.id.unread);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
            //holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(holder);
        }

        SlimChat chat = getItem(position);
        if(chat != null) {
            //holder.name.setText(chat.getTo());
            holder.unread.setText(chat.getUnread());

        }
        SlimMessage message = chat.getLastMessage();
        if(message != null) {
            holder.lastMessage.setText(message.getBody().getText());
            //holder.time.setText(String.valueOf(message.getTimestamp()));
        }

        //todo:
        SlimRosterManager roster = SlimRosterManager.getInstance();
        Uri uri = chat.getTo();
        String name = SlimUris.parseId(uri);

        if(SlimUris.isUserUri(uri)) {
            SlimUser buddy = roster.getBuddy(name);
            if(buddy != null) {
                holder.nick.setText(buddy.getNick());
                holder.avatar.setImageResource(R.drawable.male);
            }
        }else if(SlimUris.isRoomUri(uri)) {
            SlimRoom room = roster.getRoom(name);
            if(room != null) {
                holder.nick.setText(room.getNick());
                holder.avatar.setImageResource(R.drawable.room);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView unread;
        TextView lastMessage;
        TextView time;
        TextView nick;
    }

}
