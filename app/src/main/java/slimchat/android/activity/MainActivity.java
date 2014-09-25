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
import slimchat.android.SlimRosterEvent;
import slimchat.android.SlimRosterListener;
import slimchat.android.SlimRosterManager;
import slimchat.android.adapter.RosterAdapter;
import slimchat.android.core.SlimCallback;
import slimchat.android.core.SlimUser;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * 主窗口显示好友列表。
 *
 * TODO: Fragment activity to load:
 *
 * 1. ChatListFragment
 * 2. RosterFragment
 * 3. SettingFragement
 * @author slimpp.io
 * 
 */
public class MainActivity extends ListActivity implements SlimRosterListener {

	private RosterAdapter adapter;

	private boolean active = false;

	private CharSequence title;

    //TODO: Refactor Later: Should be array Adapter

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = this.getTitle();
		// setContentView(R.layout.activity_main);
		ListView buddyList = getListView();
		buddyList.setTextFilterEnabled(true);

		adapter = new RosterAdapter(this, R.layout.item_buddy, SlimRosterManager.getInstance());

		setListAdapter(adapter);

        SlimRosterManager.getInstance().addRosterListener(this);

        Log.d("MainActivity", "create....");

        try {
            SlimChat.getInstance().online(new SlimCallback() {

                @Override
                public void onSuccess() {
                    Log.d("MainActivity", "ONLINE Success");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // TODO:
                        }
                    });
                }

                @Override
                public void onFailure(String reason, Throwable error) {
                    Log.d("MainActivity", "ONLINE Failure");
                    // TODO Auto-generated method stub
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public void onResume() {
		super.onResume();
		active = true;
		// TODO: TRY reconnect
		reloadAdapter();
		Log.d("MainActivity", "resume....");
	}

	public void onPause() {
		super.onPause();
		active = false;
		Log.d("MainActivity", "pause....");

	}

	private void reloadAdapter() {

		if (active) {
			adapter = new RosterAdapter(this, R.layout.item_buddy, SlimRosterManager.getInstance());

			setListAdapter(adapter);

			adapter.notifyDataSetChanged();
		}

	}

	/**
	 * Listens for item clicks on the view
	 * 
	 * @param listView
	 *            The list view where the click originated from
	 * @param view
	 *            The view which was clicked
	 * @param position
	 *            The position in the list that was clicked
	 */
	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		if (adapter != null) {
			SlimUser buddy = (SlimUser) adapter.getItem(position);
			// start the connectionDetails activity to display the details about
			// the
			// selected connection
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext().getPackageName(),
					"slimchat.android.activity.ChatActivity");
			intent.putExtra("buddy", buddy.getId());
			startActivity(intent);
		}
	}

	@Override
	public void rosterChanged(SlimRosterEvent evnet) {
		Log.d("MainActivity", "rosterChanged");
		reloadAdapter();
	}

}
