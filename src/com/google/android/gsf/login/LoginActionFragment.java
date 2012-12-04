package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.auth.AndroidClient;
import com.google.auth.AndroidDataSet;
import com.google.auth.Crypto;

public class LoginActionFragment extends ActionFragment {

	private class ActionTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(final String... params) {
			final String email = params[0];
			final String password = params[1];
			final String encryptedPassword = Crypto.encryptPassword(email,
					password);

			final AndroidManager androidManager = new AndroidManager(
					getActivity());
			final AndroidDataSet dataSet = androidManager
					.getAndroidDataSet(email);

			String masterToken = AndroidClient.getMasterToken(dataSet,
					encryptedPassword, true);
			if (masterToken == null || masterToken.isEmpty()) {
				Log.w(TAG,
						"Could not sign in using encryption, falling back to direct password mode!");
				masterToken = AndroidClient.getMasterToken(dataSet, password);
				if (masterToken == null || masterToken.isEmpty()) {
					Log.e(TAG, "Could not sign in!");
					return Activity.RESULT_CANCELED;
				}
			}
			androidManager.addAccount(email, masterToken);
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
