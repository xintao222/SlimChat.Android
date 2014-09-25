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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * SlimChat Global Setting
 *
 * @author Feng Lee feng.lee@slimpp.io
 */
public class SlimChatSetting extends SlimContextAware {

    static final String USERNAME = "username";

    static final String PASSWORD = "password";

    private static SlimChatSetting instance = new SlimChatSetting();

    private String password = null;

    private String username = null;

    private SlimChatSetting() {
    }

    public static SlimChatSetting getInstance() {
        return instance;
    }

    public String getUsername() {
        if (username == null) {
            username = getStringFromPreference(USERNAME);
        }
        return username;
    }

    public void setUsername(String username) {
        if (username != null && saveToPreference(USERNAME, username)) {
            this.username = username;
        }
    }

    public String getPassword() {
        if (password == null) {
            password = getStringFromPreference(PASSWORD);
        }
        return password;
    }

    public void setPassword(String password) {
        if (password != null && saveToPreference(PASSWORD, password)) {
            this.password = password;
        }
    }

    private boolean saveToPreference(String name, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = preferences.edit();
        return editor.putString(name, value).commit();
    }

    private String getStringFromPreference(String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return preferences.getString(name, null);
    }

}
