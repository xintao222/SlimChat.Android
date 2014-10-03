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
package slimchat.android.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import slimchat.android.model.SlimRoom;

/**
 * Created by feng on 14-10-1.
 */
public class SlimRoomDb {

    private Set<String> ids;

    private Map<String, SlimRoom> rooms;

    public SlimRoomDb() {
        ids = new HashSet<String>();
        rooms = new HashMap<String, SlimRoom>();
    }

    public void add(SlimRoom room) {
        ids.add(room.getId());
        rooms.put(room.getId(), room);
        Log.d("SlimRoomDao", "Add " + room.toString());
    }

    public SlimRoom get(String name) {
        return rooms.get(name);
    }

    public List<SlimRoom> all() {
        return  new ArrayList<SlimRoom>(rooms.values());
    }

    public void clear() {

    }

    public boolean hasRoom(String id) {
        return rooms.containsKey(id);
    }

    public void update(SlimRoom room) {
        rooms.put(room.getId(), room);
    }

    public void remove(String id) {
        ids.remove(id);
        rooms.remove(id);
    }
}
