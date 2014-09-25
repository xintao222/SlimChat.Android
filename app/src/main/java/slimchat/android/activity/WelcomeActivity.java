package slimchat.android.activity;

import slimchat.android.SlimChat;
import slimchat.android.SlimChatSetting;
import slimchat.android.activity.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import slimchat.android.R;
import slimchat.android.core.SlimCallback;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class WelcomeActivity extends Activity {

    TextView footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);

        footer = (TextView)findViewById(R.id.progress);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SlimChat.getInstance().isActivated()) {
            ready();
        } else {
            SlimChat.getInstance().activate(this, new SlimCallback() {
                @Override
                public void onSuccess() {
                    footer.setText("Service started...");
                    WelcomeActivity.this.ready();
                }

                @Override
                public void onFailure(String reason, Throwable error) {
                    footer.setText(reason);
                }
            });
        }
    }

    public void ready() {
        String username = SlimChatSetting.getInstance().getUsername();
        String password = SlimChatSetting.getInstance().getPassword();
        if( !(username == null || password == null) ) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else { // need login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
