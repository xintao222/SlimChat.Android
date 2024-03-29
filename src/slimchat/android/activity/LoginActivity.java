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
package slimchat.android.activity;

import slimchat.android.R;
import slimchat.android.SlimChat;
import slimchat.android.core.SlimCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登陆窗口演示类。一般与APP集成不需要登陆演示。
 * 
 * @author slimpp.io
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {

	private EditText etUsername;

	private EditText etPassword;

	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		btnLogin.setEnabled(false);
		// 登录到聊天服务器
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();

		SlimChat.instance().login(username, password, new SlimCallback() {

			@Override
			public void onSuccess(Object data) {
				runOnUiThread(new Runnable() {
					public void run() {
						btnLogin.setEnabled(true);
						startActivity(new Intent(LoginActivity.this,
								MainActivity.class));
						Toast.makeText(LoginActivity.this, "Login Success!",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			}

			@Override
			public void onFailure(final String error, Throwable exception,
					Object extra) {
				runOnUiThread(new Runnable() {
					public void run() {
						btnLogin.setEnabled(true);
						Toast.makeText(LoginActivity.this,
								"Login Failure: " + error, Toast.LENGTH_SHORT)
								.show();
					}
				});
			}
		});
	}
}
