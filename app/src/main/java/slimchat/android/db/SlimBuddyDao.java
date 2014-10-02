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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import slimchat.android.model.SlimUser;

import android.content.Context;

/**
 * Roster Database Accessor
 * 
 * @author Feng Lee
 * 
 */
public class SlimBuddyDao {

	private Set<String> names;

	private Context appContext;

	private Map<String, SlimUser> buddies;

	public SlimBuddyDao() {
		names = new HashSet<String>();
		buddies = new HashMap<String, SlimUser>();
	}

	public void init(Context context) {
		appContext = context;
	}

    public List<SlimUser> all() {
        List<SlimUser> list = new ArrayList<SlimUser>();
        for (String name : names) {
            list.add(buddies.get(name));
        }
        return list;
    }

	public SlimUser getBuddy(String name) {
		return buddies.get(name);
	}

	public boolean hasBuddy(String name) {
		return buddies.containsKey(name);
	}

	public void add(SlimUser buddy) {
		if (!buddies.containsKey(buddy.getId())) {
			names.add(buddy.getId());
		}
		buddies.put(buddy.getId(), buddy);
	}

	public void update(SlimUser buddy) {
		buddies.put(buddy.getId(), buddy);
	}

	public void removeBuddy(String id) {
		buddies.remove(id);
		names.remove(id);
	}

	public int getCount() {
		return buddies.size();
	}

    public void clear() {
        buddies.clear();
    }

}
