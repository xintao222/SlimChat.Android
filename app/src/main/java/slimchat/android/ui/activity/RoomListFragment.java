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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimChatRoster;
import slimchat.android.ui.adapter.RoomAdapter;
import slimchat.android.model.SlimRoom;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class RoomListFragment extends ListFragment implements SlimChatRoster.OnRoomChangeListener {

    private static final String TAG = "RoomListFragment";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private RoomAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        adapter = new RoomAdapter(getActivity(), R.layout.item_room, SlimChat.roster().getRooms());

        SlimChat.roster().setRoomListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        setListAdapter(adapter);

        Log.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "hidden: " + hidden);
        super.onHiddenChanged(hidden);
        refresh();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        SlimChat.roster().setRoomListener(null);
    }

    public void refresh() {
        if (!isHidden()) {
            adapter = new RoomAdapter(getActivity(), R.layout.item_room, SlimChat.roster().getRooms());
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
        Log.d(TAG, "ItemClicked: " + id);
        SlimRoom room = adapter.getItem((int) id);
        ((MainActivity) getActivity()).startChat(room.getUri());
    }


    @Override
    public void onRoomChange(SlimChatRoster.EventType eventType, String id) {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                refresh();
            }});
        }

    }
