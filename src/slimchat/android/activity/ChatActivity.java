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
package slimchat.android.activity;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimConversation;
import slimchat.android.adapter.SlimMessageAdapter;
import slimchat.android.core.SlimCallback;
import android.app.Activity;
import android.database.DataSetObserver;
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
		OnKeyListener {

	private SlimConversation conversation;

	private SlimMessageAdapter adapter;

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

		conversation = SlimChat.instance().manager()
				.open(getIntent().getStringExtra("buddy"));
		setTitle("Chating with " + conversation.getTo());

		adapter = new SlimMessageAdapter(this, R.layout.item_message,
				conversation);
		msgList.setAdapter(adapter);
		msgList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		conversation.setMessageHandler(adapter);
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
		conversation.activate();
		Log.d("ChatActivity", "Chat is activated: " + conversation);
	}

	protected void onPaused() {
		super.onPause();
		conversation.deactivate();
		Log.d("ChatActivity", "Chat is deactivated: " + conversation);
	}
	
	public void onBackPressed() {
		super.onBackPressed();
		conversation.deactivate();
		Log.d("ChatActivity", "Chat is back: " + conversation);
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
		etInput.setText("");
		if (input.trim().length() == 0)
			return;
		conversation.sendMessage(input, new SlimCallback() {
			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				Log.d("ChatActivity", data.toString());
			}

			@Override
			public void onFailure(String error, Throwable exception,
					Object extra) {
				Log.d("ChatActivity", error);
			}
		});
	}

}
