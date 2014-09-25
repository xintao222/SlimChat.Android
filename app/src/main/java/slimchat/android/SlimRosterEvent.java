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

/**
 * 好友列表变更事件。<br>
 * <p>事件类型:<br>
 * <ul>
 * <li>Type.ADDED: 添加好友</li>
 * <li>Type.REMOVED: 删除好友</li>
 * <li>Type.UPDATED: 更新好友</li>
 * </ul>
 * </p>
 * 
 * @author slimpp.io
 *
 */
public class SlimRosterEvent {

	/**
	 * 事件类型枚举
	 * 
	 */
	public enum Type {
		ADDED, REMOVED, UPDATED
	}

	private final Type type;

	private final SlimRosterManager roster;

	private final String buddyID;

	SlimRosterEvent(Type type, SlimRosterManager roster, String buddyID) {
		this.roster = roster;
		this.type = type;
		this.buddyID = buddyID;
	}

	/**
	 * 事件类型
	 * 
	 * @return 事件类型
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 好友管理对象
	 * 
	 * @return 好友管理对象
	 */
	public SlimRosterManager getRoster() {
		return roster;
	}

	/**
	 * 变更好友ID
	 * 
	 * @return 变更好友ID
	 */
	public String getBuddyID() {
		return buddyID;
	}

	public String toString() {
		return String.format("SlimRosterEvent[type=%s, buddyID=%s]",
				type.name(), buddyID);
	}

}
