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
package slimchat.android.ui.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.model.SlimCallback;


/**
 * MainActivity
 */
public class MainActivity  extends FragmentActivity implements
        ActionBar.TabListener {

    static final String TAG = "MainActivity";

    /**
     * {@link SectionsPagerAdapter} that is used to get pages to display
     */
    SectionsPagerAdapter sectionsPagerAdapter;
    /**
     * {@link ViewPager} object allows pages to be flipped left and right
     */
    ViewPager viewPager;

    /** The currently selected tab **/
    private int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        setupView();
        registerListeners();
            try {
                SlimChat.client().online(new SlimCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "online success");
                    }

                    @Override
                    public void onFailure(String reason, Throwable error) {
                        Log.e(TAG, "online failure: " + reason);
                        error.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        //TODO: broadcast receiver?? SlimChatService 打开关闭会话。这里不需要处理接受消息的Intent，应该在activity中接收

    }

    private void setupView() {
        // Create the adapter that will return a fragment for each of the pages
        sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the action bar for tab navigation
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // add the sectionsPagerAdapter
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        // select the tab that represents the current page
                        actionBar.setSelectedNavigationItem(position);

                    }
                });

        // Create the tabs for the screen
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = actionBar.newTab();
            tab.setText(sectionsPagerAdapter.getPageTitle(i));
            tab.setTabListener(this);
            actionBar.addTab(tab);
        }
    }

    private void registerListeners() {

    }

    protected void onResume() {
        Log.d(TAG, "MainActivity is resumed");
        super.onResume();

        //TODO: CHECK SERVICE STATUS
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterListeners();
    }

    private void unregisterListeners() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
        selected = tab.getPosition();
        //TODO: maybe refresh
        // invalidate the options menu so it can be updated
        //invalidateOptionsMenu();
        // history fragment is at position zero so get this then refresh its
        // view
        //((HistoryFragment) sectionsPagerAdapter.getItem(0)).refresh();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //nothing to do
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //nothing to do
    }

    public void startChat(Uri uri) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * Provides the Activity with the pages to display for each tab
     *
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        // Stores the instances of the pages
        private ArrayList<Fragment> fragments = null;

        /**
         * Only Constructor, requires a the activity's fragment managers
         *
         * @param fragmentManager
         */
        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<Fragment>();
            fragments.add(new ChatListFragment());
            fragments.add(new BuddyListFragment());
            fragments.add(new RoomListFragment());
            fragments.add(new SettingFragment());

        }

        /**
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /**
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         *
         * @see FragmentPagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0 :
                    return getString(R.string.title_chat_list).toUpperCase();
                case 1 :
                    return getString(R.string.title_buddy_list).toUpperCase();
                case 2:
                    return getString(R.string.title_room_list).toUpperCase();
                case 3 :
                    return getString(R.string.title_setting).toUpperCase();
            }
            // return null if there is no title matching the position
            return null;
        }


    }

}
