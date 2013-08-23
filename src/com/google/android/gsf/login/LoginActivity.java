package com.google.android.gsf.login;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends FragmentActivity implements LoginFragmentContainer, OnClickListener {

	public static final int LOGIN_ACTION_ADD_ACCOUNT = 1;
	public static final int LOGIN_ACTION_AUTH_TOKEN = 2;

	private static final String TAG = "GoogleLoginActivity";
	private LoginFragment activeFragment;
	private Button backButton;
	private FragmentManager fragmentManager;
	private Button nextButton;
	private Bundle options;
	private View titleBar;
	private TextView titleBarText;
	private View titleProgressBar;
	private TextView titleProgressBarText;

	@Override
	public void disableProgressBar() {
		titleProgressBar.setVisibility(View.GONE);
		titleBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void enableProgressBar() {
		titleBar.setVisibility(View.GONE);
		titleProgressBar.setVisibility(View.VISIBLE);
	}

	private void finishResult(final int result) {
		setResult(result);
		finish();
	}

	private void finishResult(final int result, final Intent intent) {
		setResult(result, intent);
		finish();
	}

	@Override
	public Bundle getOptions() {
		return options;
	}

	@Override
	public void goAddAccountIntro() {
		goFragment(new AddAccountIntroFragment());
	}

	@Override
	public void goAddAccountOutro() {
		goFragment(new AddAccountOutroFragment());
	}

	@Override
	public void goAuthTokenAction() {
		goFragment(new AuthTokenActionFragment());
	}

	@Override
	public void goAuthTokenAskPermission() {
		goFragment(new AuthTokenAskPermissionFragment());
	}

	private void goFragment(final LoginFragment fragment) {
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
		activeFragment = fragment;
	}

	@Override
	public void goLoginAction() {
		goFragment(new LoginActionFragment());
	}

	@Override
	public void goUsernamePassword() {
		goFragment(new UsernamePasswordFragment());
	}

	@Override
	public void hideBackButton() {
		backButton.setVisibility(View.GONE);
	}

	@Override
	public void hideNextButton() {
		nextButton.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		activeFragment.onBackPressed();
	}

	@Override
	public void onClick(final View v) {
		if (v == nextButton) {
			activeFragment.onNextPressed();
		} else if (v == backButton) {
			activeFragment.onBackPressed();
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_main);
		fragmentManager = getSupportFragmentManager();
		backButton = (Button) findViewById(R.id.btn_back);
		backButton.setOnClickListener(this);
		nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(this);
		titleBar = findViewById(R.id.title);
		titleProgressBar = findViewById(R.id.title_progress);
		titleBarText = (TextView) findViewById(R.id.txt_title);
		titleProgressBarText = (TextView) findViewById(R.id.txt_title_progress);
		options = getIntent().getExtras();
		if (options == null) {
			options = new Bundle();
		}
		parseOptions();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		return false;
	}

	private void parseOptions() {
		if (options.containsKey(AndroidManager.KEY_LOGIN_ACTION)) {
			final int action = options.getInt(AndroidManager.KEY_LOGIN_ACTION, LOGIN_ACTION_ADD_ACCOUNT);
			switch (action) {
				case LOGIN_ACTION_ADD_ACCOUNT:
					setTitle(R.string.activity_login_title);
					goAddAccountIntro();
					break;
				case LOGIN_ACTION_AUTH_TOKEN:
					setTitle(R.string.activity_permission_title);
					goAuthTokenAction();
					break;
				default:
					Log.w(TAG, "Unknown loginAction: " + action);
					finish();
					break;
			}
		}
	}

	@Override
	public void resultAccountAdded() {
		if (getOptions().containsKey(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)) {
			final AccountAuthenticatorResponse response =
					getOptions().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			if (response != null) {
				response.onResult(null);
			}
		}
		finishResult(RESULT_OK);
	}

	@Override
	public void resultAuthToken() {
		final Intent intent = new Intent();
		intent.putExtras(getOptions());
		if (getOptions().containsKey(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)) {
			final AccountAuthenticatorResponse response =
					getOptions().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			if (response != null) {
				response.onResult(intent.getExtras());
			}
		}
		finishResult(RESULT_OK, intent);
	}

	@Override
	public void resultCancelled() {
		if (getOptions().containsKey(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)) {
			final AccountAuthenticatorResponse response =
					getOptions().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			if (response != null) {
				response.onError(400, "Cancelled.");
			}
		}
		finishResult(RESULT_CANCELED);
	}

	@Override
	public void setTitle(final CharSequence title) {
		super.setTitle(title);
		titleBarText.setText(title);
		titleProgressBarText.setText(title);
	}

	@Override
	public void showAllowButton() {
		nextButton.setVisibility(View.VISIBLE);
		nextButton.setEnabled(true);
		nextButton.setText(R.string.allow);
	}

	@Override
	public void showBackButton() {
		backButton.setVisibility(View.VISIBLE);
		backButton.setText(R.string.back);
	}

	@Override
	public void showCancelButton() {
		backButton.setVisibility(View.VISIBLE);
		backButton.setText(android.R.string.cancel);
	}

	@Override
	public void showDenyButton() {
		backButton.setVisibility(View.VISIBLE);
		backButton.setText(R.string.deny);
	}

	@Override
	public void showDisabledNextButton() {
		nextButton.setVisibility(View.VISIBLE);
		nextButton.setEnabled(false);
		nextButton.setText(R.string.next);
	}

	@Override
	public void showNextButton() {
		nextButton.setVisibility(View.VISIBLE);
		nextButton.setEnabled(true);
		nextButton.setText(R.string.next);
	}

	@Override
	public void showOkButton() {
		nextButton.setVisibility(View.VISIBLE);
		nextButton.setEnabled(true);
		nextButton.setText(android.R.string.ok);
	}

	@Override
	public void goReauth() {
		goFragment(new ReauthFragment());
	}

	@Override
	public void goReauthAction() {
		goFragment(new ReauthActionFragment());
	}
}
