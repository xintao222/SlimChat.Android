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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import slimchat.android.db.SlimBuddyDb;
import slimchat.android.db.SlimRoomDb;
import slimchat.android.model.SlimRoom;
import slimchat.android.model.SlimUser;

/**
 * Roster Manager to manage buddy, room list.
 * 
 * @author feng.lee@slimpp.io
 * 
 */
public class SlimChatRoster extends SlimContextAware {

    static final String TAG = "SlimChatRoster";


    public enum EventType {
        INITED, ADDED, REMOVED, UPDATED
    }

    public interface OnBuddyChangeListener {

        void onBuddyChange(EventType evtType, String id);

    }

    public interface OnRoomChangeListener {

        void onRoomChange(EventType eventType, String id);
    }

    private String rosterVersion = "0";

    private final SlimRoomDb roomDb;

    private final SlimBuddyDb buddyDb;

    private OnBuddyChangeListener buddyListener = null;

    private OnRoomChangeListener roomListener = null;

    SlimChatRoster() {
		buddyDb = new SlimBuddyDb();
        roomDb = new SlimRoomDb();
	}

    /**
     * Roster Version
     *
     * @return roster version
     */
    public String getRosterVersion() {
        return rosterVersion;
    }

    /**
     * Set roster version
     *
     * @param rosterVersion
     */
    public void setRosterVersion(String rosterVersion) {
        this.rosterVersion = rosterVersion;
    }

    /**
     * Set buddy listener
     *
     * @param buddyListener
     */
    public void setBuddyListener(OnBuddyChangeListener buddyListener) {
        this.buddyListener = buddyListener;
    }

    /**
     * Set room listener
     *
     * @param roomListener
     */
    public void setRoomListener(OnRoomChangeListener roomListener) {
        this.roomListener = roomListener;
    }


    /**
	 * Initialize roster
	 * 
	 * @throws JSONException
	 */
	public void feed(JSONObject data) throws JSONException {

        feedBuddies(data);

        feedRooms(data);
	}

    private void feedBuddies(JSONObject data) throws JSONException {
        this.buddyDb.clear();
        JSONArray array = data.getJSONArray("buddies");
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            SlimUser buddy = new SlimUser(json);
            this.addBuddyToDb(buddy);
        }
        if(buddyListener != null) {
            buddyListener.onBuddyChange(EventType.INITED, null);
        }
    }

    private void feedRooms(JSONObject data) throws JSONException {
        this.roomDb.clear();
        JSONArray array = data.getJSONArray("rooms");
        for(int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            SlimRoom room = new SlimRoom(json);
            this.addRoomToDb(room);
        }
        if(roomListener != null) {
            roomListener.onRoomChange(EventType.INITED, null);
        }
    }

    /**
	 * Get all buddies
	 * 
	 * @return all buddies
	 */
	public List<SlimUser> getBuddies() {
		return buddyDb.all();
	}


    /**
     * Get buddy count
     *
     * @return buddy count
     */
    public int getBuddyCount() {
        return buddyDb.getCount();
    }

    /**
     * Get one buddy.
     *
     * @param id buddy id
     * @return buddy
     */
    public SlimUser getBuddy(String id) {
        return buddyDb.getBuddy(id);
    }

    /**
     * Add buddy
     * @param buddy
     */
    public void addBuddy(SlimUser buddy) {
        EventType evtType = addBuddyToDb(buddy);
        if(buddyListener != null) {
            buddyListener.onBuddyChange(evtType, buddy.getId());
        }
    }

    private EventType addBuddyToDb(SlimUser buddy) {
        if (buddyDb.hasBuddy(buddy.getId())) {
            buddyDb.update(buddy);
            return EventType.UPDATED;
        }
        buddyDb.add(buddy);
        return EventType.ADDED;
    }

    /**
     * remove buddy
     *
     * @param id buddy id
     */
    public void removeBuddy(String id) {
        if (buddyDb.hasBuddy(id)) {
            buddyDb.remove(id);
            if(buddyListener != null) {
                buddyListener.onBuddyChange(EventType.REMOVED, id);
            }
        }
    }


    /**
     * Get all rooms
     *
     * @return all rooms
     */
    public List<SlimRoom> getRooms() {
        return roomDb.all();
    }

    /**
     * Get one room
     *
     * @param id room id
     * @return
     */
    public SlimRoom getRoom(String id) {
        return roomDb.get(id);
    }

    public void addRoom(SlimRoom room) {
        EventType evtType = addRoomToDb(room);
        if(roomListener != null) {
            roomListener.onRoomChange(evtType, room.getId());
        }
    }

    private EventType addRoomToDb(SlimRoom room) {
        if(roomDb.hasRoom(room.getId())) {
            roomDb.update(room);
            return EventType.UPDATED;
        }
        roomDb.add(room);
        return EventType.ADDED;
    }

    public void removeRoom(String id) {
        if(roomDb.hasRoom(id)) {
            roomDb.remove(id);
            if(roomListener != null) {
                roomListener.onRoomChange(EventType.REMOVED, id);
            }
        }
    }


}
