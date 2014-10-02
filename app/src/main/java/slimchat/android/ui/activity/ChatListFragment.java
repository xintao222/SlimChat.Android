package slimchat.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimChatManager;
import slimchat.android.ui.OnChatListener;
import slimchat.android.ui.adapter.ChatAdapter;


/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 */
public class ChatListFragment extends ListFragment {

    private OnChatListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ChatAdapter(getActivity(), R.layout.item_chat, SlimChatManager.getInstance().getChats());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        setListAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChatListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refresh() {
        if(!isHidden()) {
            adapter = new ChatAdapter(getActivity(), R.layout.item_chat, SlimChatManager.getInstance().getChats());
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
        if (null != mListener) {
            //TODO: START CHAT
            SlimChat chat = adapter.getItem((int)id);
            mListener.startChat(chat.getTo());
        }
    }

}
