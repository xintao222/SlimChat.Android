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
package slimchat.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import slimchat.android.SlimRosterEvent.Type;
import slimchat.android.model.SlimRoom;
import slimchat.android.model.SlimUser;
import slimchat.android.db.SlimBuddyDao;
import slimchat.android.db.SlimRoomDao;

/**
 * 好友、群组列表管理类。
 * 
 * @author slimpp.io
 * 
 */
public class SlimRosterManager extends SlimContextAware {

    private static final String TAG = "SlimRosterManager";

    private static SlimRosterManager instance = new SlimRosterManager();

    private String rosterVersion = "0";

    private final SlimRoomDao roomDao;

    private final SlimBuddyDao buddyDao;

	private List<OnRosterChangeListener> listeners = new ArrayList<OnRosterChangeListener>();

    private SlimRosterManager() {
		buddyDao = new SlimBuddyDao();
        roomDao = new SlimRoomDao();
	}

    public static SlimRosterManager getInstance() {
        return instance;
    }

    /**
	 * 初始化好友列表
	 * 
	 *            好友列表JSON数组
	 *
	 * @throws JSONException
	 */
	public void feed(JSONObject data) throws JSONException {
        this.buddyDao.clear();
        JSONArray array = data.getJSONArray("buddies");
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			SlimUser buddy = new SlimUser(json);
			this.addBuddyToDb(buddy);
		}
        array = data.getJSONArray("rooms");
        for(int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            SlimRoom room = new SlimRoom(json);
            this.addRoom(room);
        }
        notifyListeners(new SlimRosterEvent(Type.FEED, this));
	}

    /**
	 * 获取全部好友列表
	 * 
	 * @return 全部好友列表
	 */
	public List<SlimUser> getBuddies() {
		return buddyDao.all();
	}


    public List<SlimRoom> getRooms() {
        return roomDao.all();
    }

    public SlimRoom getRoom(String name) {
        return roomDao.get(name);
    }

    public void addRoom(SlimRoom room) {
        roomDao.add(room);
    }

    /**
	 * 根据名称获取一个好友
	 * 
	 * @param name 好友名称
	 * @return 好友
	 */
	public SlimUser getBuddy(String name) {
		return buddyDao.getBuddy(name);
	}
	
	/**
	 * 获取好友总数
	 * 
	 * @return 好友总数
	 */
	public int getBuddyCount() {
		return buddyDao.getCount();
	}


	/**
	 * 添加好友
	 * @param buddy 好友
	 */
	public void addBuddy(SlimUser buddy) {
        Type evtType = addBuddyToDb(buddy);

		notifyListeners(new SlimRosterEvent(evtType, this, buddy.getId()));
	}

    private Type addBuddyToDb(SlimUser buddy) {
        String name = buddy.getId();
        Type evtType;
        if (buddyDao.hasBuddy(name)) {
            buddyDao.update(buddy);
            evtType = Type.UPDATED;
        } else {
            buddyDao.add(buddy);
            evtType = Type.ADDED;
        }
        return evtType;
    }

    /**
	 * 根据好友ID删除好友
	 * 
	 * @param id 好友ID
	 */
	public void removeBuddy(String id) {
		if (buddyDao.hasBuddy(id)) {
			buddyDao.removeBuddy(id);
			notifyListeners(new SlimRosterEvent(Type.REMOVED, this, id));
		}
	}

	/**
	 * 添加好友列表变更监听器
	 * 
	 * @param listener 变更监听器
	 */
	public void addRosterListener(OnRosterChangeListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * 删除好友列表变更监听器
	 * 
	 * @param listener 变更监听器
	 */
	public void removeRosterListener(OnRosterChangeListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	// TODO: SHOULD Refactor Later
	public void updateUnread(String name) {
		Log.d("RosterManager",
                "udpatedUnread " + name + ", Dao: " + buddyDao.hasBuddy(name));
		if (buddyDao.hasBuddy(name)) {
			Log.d("SlimRosterManager", "updateUnread " + name);
			notifyListeners(new SlimRosterEvent(Type.UPDATED, this, name));
		}
	}

	private void notifyListeners(SlimRosterEvent event) {
		synchronized (listeners) {
			for (OnRosterChangeListener listener : listeners) {
				listener.onRosterChange(event);
			}
		}
	}

}
