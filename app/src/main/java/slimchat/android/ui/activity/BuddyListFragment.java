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
import slimchat.android.ui.adapter.BuddyAdapter;
import slimchat.android.model.SlimUser;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class BuddyListFragment extends ListFragment implements SlimChatRoster.OnBuddyChangeListener {

    private static final String TAG = "BuddyListFragment";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BuddyAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuddyListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new BuddyAdapter(getActivity(), R.layout.item_buddy, SlimChat.roster().getBuddies());

        SlimChat.roster().setBuddyListener(this);

        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buddy_list, container, false);

        setListAdapter(adapter);

        Log.d(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "hidden changed: " + hidden);
        super.onHiddenChanged(hidden);
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        if (!isHidden()) {
            adapter = new BuddyAdapter(getActivity(), R.layout.item_buddy, SlimChat.roster().getBuddies());
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        SlimChat.roster().setBuddyListener(null);
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
        Log.d(TAG, "ItemClicked: " + id);
        SlimUser user = adapter.getItem((int) id);
        ((MainActivity) getActivity()).startChat(user.getUri());
    }

    @Override
    public void onBuddyChange(SlimChatRoster.EventType evtType, String id) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                refresh();
            }
        });

    }
}