package com.google.android.gsf.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.auth.AndroidClient;
import com.google.auth.AndroidDataSet;
import com.google.auth.Crypto;

public class LoginActionFragment extends ActionFragment {

	private class ActionTask extends AsyncTask<String, Void, Integer> {

		public int RESULT_ERROR_ACCOUNT = 24;
		public int RESULT_ERROR_PARAMS = 21;
		public int RESULT_ERROR_TOKEN = 23;

		@Override
		protected Integer doInBackground(final String... params) {
			if (params.length < 2) {
				Log.w(TAG, "Not enough params, we need email and password!");
				return RESULT_ERROR_PARAMS;
			}
			final String email = params[0];
			final String password = params[1];
			if (email == null || email.isEmpty() || password == null
					|| password.isEmpty()) {
				Log.w(TAG, "Params should not be null or empty!");
				return RESULT_ERROR_PARAMS;
			}
			String encryptedPassword = null;
			try {
				encryptedPassword = Crypto.encryptPassword(email,
						password);
			} catch (final Throwable t) {
				Log.w(TAG, t);
			}
			if (encryptedPassword == null || encryptedPassword.isEmpty()) {
				Log.w(TAG, "Could not encrypt password!");
				encryptedPassword = null;
			}

			AndroidDataSet dataSet = getContainer().getAndroidDataSet(email);

			String masterToken = null;
			if (encryptedPassword != null) {
				try {
					masterToken = AndroidClient.getMasterToken(dataSet,
							encryptedPassword, true);
				} catch (final Throwable t) {
					Log.w(TAG, t);
				}
			}
			if (masterToken == null || masterToken.isEmpty()) {
				Log.w(TAG,
						"Could not sign in using encryption, falling back to direct password mode!");
				encryptedPassword = null;
			}
			if (encryptedPassword == null) {
				try {
					masterToken = AndroidClient.getMasterToken(dataSet,
							password);
				} catch (final Throwable t) {
					Log.w(TAG, t);
				}
			}
			if (masterToken == null || masterToken.isEmpty()) {
				Log.w(TAG, "Could not sign in!");
				return RESULT_ERROR_TOKEN;
			}
			try {
				final AccountManager accountManager = AccountManager
						.get(getActivity());
				final Account account = new Account(email,
						getString(R.string.account_type));
				accountManager.addAccountExplicitly(account, masterToken, null);
			} catch (final Throwable t) {
				Log.w(TAG, t);
				return RESULT_ERROR_ACCOUNT;
			}
			return Activity.RESULT_OK;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			super.onPostExecute(result);
			Log.d(TAG, "Result: " + result);
			switch (result) {
			case Activity.RESULT_OK:
				onNextPressed();
				break;
			default:
				onBackPressed();
				break;
			}
		}
	}

	private static final String TAG = "GoogleLoginAction";

	private ActionTask task;

	@Override
	protected void cancelAction() {
		task.cancel(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getContainer().goUsernamePassword();
	}

	@Override
	public void onNextPressed() {
		getContainer().getOptions().remove(AccountManager.KEY_PASSWORD);
		getContainer().disableProgressBar();
		getContainer().goAddAccountOutro();
	}

	@Override
	protected void startAction() {
		task = new ActionTask();
		final String email = getContainer().getOptions().getString(
				AccountManager.KEY_ACCOUNT_NAME);
		final String password = getContainer().getOptions().getString(
				AccountManager.KEY_PASSWORD);
		task.execute(email, password);
	}

}
