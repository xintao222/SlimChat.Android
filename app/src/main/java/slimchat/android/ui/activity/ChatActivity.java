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
package slimchat.android.ui.activity;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimConversation;
import slimchat.android.SlimChatManager;
import slimchat.android.SlimConversation.OnMessageListener;
import slimchat.android.ui.adapter.MessageAdapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * 聊天窗口演示类。
 */
public class ChatActivity extends Activity implements OnClickListener,
		OnKeyListener, OnMessageListener {

    private Uri uri;

	private SlimConversation chat;

	private MessageAdapter adapter;

	private ListView msgList;

	private EditText etInput;

	private Button btnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		msgList = (ListView) findViewById(R.id.list);
		//msgList.setBottom(bottom)

		etInput = (EditText) findViewById(R.id.et_input);
		etInput.setOnKeyListener(this);

		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(this);

        uri = getIntent().getData();

		chat = SlimChat.manager().open(uri);

		setTitle("Chating with " + chat.getTo());

		adapter = new MessageAdapter(this, R.layout.item_message,
                chat);
		msgList.setAdapter(adapter);
		msgList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		chat.setMessageListener(this);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				msgList.setSelection(adapter.getCount() - 1);
			}
		});
	}

	protected void onResume() {
		super.onResume();
		chat.activate();
        //TODO:
        refresh();
		Log.d("ChatActivity", "Chat is activated: " + chat);
	}

    public void refresh() {
        //TODO: set messageAdapter
    }

    protected void onPaused() {
		super.onPause();
		chat.deactivate();
		Log.d("ChatActivity", "Chat is deactivated: " + chat);
	}

    protected void onDestroy() {
        super.onDestroy();
        chat.setMessageListener(null);
    }
	
	public void onBackPressed() {
		super.onBackPressed();
		chat.deactivate();
		Log.d("ChatActivity", "Chat is back: " + chat);
	}
	
	@Override
	public void onClick(View view) {
		sendChatMessage();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
			sendChatMessage();
			return true;
		}
		return false;
	}

	private void sendChatMessage() {
		String input = etInput.getText().toString();
		if (input.trim().length() == 0) {
            return;
        }
        try {
            chat.sendMessage(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        etInput.setText("");
    }

    @Override
    public void onMessageSent(String msgID) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageReceived(String msgID) {
        adapter.notifyDataSetChanged();
    }

}
