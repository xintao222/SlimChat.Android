package slimchat.android.ui.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.SlimConversation;
import slimchat.android.SlimChatManager;
import slimchat.android.ui.adapter.ChatAdapter;


/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 */
public class ChatListFragment extends ListFragment implements SlimChatManager.OnChatListener {

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
        adapter = new ChatAdapter(getActivity(), R.layout.item_chat, SlimChat.manager().getChats());
        SlimChat.manager().addListener(this);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SlimChat.manager().addListener(this);
    }

    public void refresh() {
        if(!isHidden()) {
            adapter = new ChatAdapter(getActivity(), R.layout.item_chat, SlimChat.manager().getChats());
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
        SlimConversation chat = adapter.getItem((int)id);
        ( (MainActivity)getActivity()).startChat(chat.getTo());
    }

    @Override
    public void onChatOpen(Uri to) {
        refresh();
    }

    @Override
    public void onChatClose(Uri to) {
        refresh();
    }
}
