package slimchat.android.ui.activity;

import slimchat.android.SlimChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import slimchat.android.R;
import slimchat.android.model.SlimCallback;

/**
 *
 */
public class WelcomeActivity extends Activity implements SlimCallback {

    TextView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);

        footer = (TextView) findViewById(R.id.progress);

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
        if (SlimChat.isRunning()) {
            ready();
        } else {
            SlimChat.startup(this);
        }
    }

    public void ready() {
        String username = SlimChat.setting().getUsername();
        String password = SlimChat.setting().getPassword();
        if (!(username == null || password == null)) {
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

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                footer.setText("Service bound...");
                WelcomeActivity.this.ready();
            }
        });
    }

    @Override
    public void onFailure(String reason, Throwable error) {
        runOnUiThread(new Runnable() {
            public void run() {
                footer.setText("Service unbound...");
            }
        });
    }
}
