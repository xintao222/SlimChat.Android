package slimchat.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import slimchat.android.OnRosterChangeListener;
import slimchat.android.R;
import slimchat.android.SlimRosterEvent;
import slimchat.android.SlimRosterManager;
import slimchat.android.ui.OnChatListener;
import slimchat.android.ui.adapter.BuddyAdapter;
import slimchat.android.model.SlimUser;


/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 */
public class BuddyListFragment extends ListFragment implements OnRosterChangeListener {

    private static final String TAG = "BuddyListFragment";

    private OnChatListener listener;

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

        adapter = new BuddyAdapter(getActivity(), R.layout.item_buddy, SlimRosterManager.getInstance().getBuddies());

        SlimRosterManager.getInstance().addRosterListener(this);
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
        Log.d(TAG, "onAttach");
        try {
            listener = (OnChatListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        listener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "hidden changed: " + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
            refresh();
    }

    public void refresh() {
        if(!isHidden()) {
            adapter = new BuddyAdapter(getActivity(), R.layout.item_buddy, SlimRosterManager.getInstance().getBuddies());
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        SlimRosterManager.getInstance().removeRosterListener(this);
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id)
    {
        Log.d(TAG, "ItemClicked: " + id);
        if (null != listener) {
            SlimUser user = adapter.getItem((int)id);
            listener.startChat(user.getUri());
        }
    }

    @Override
    public void onRosterChange(SlimRosterEvent evnet) {
        refresh();
    }

}