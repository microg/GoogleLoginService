package com.google.android.gsf.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.AndroidAuth;
import com.google.android.AndroidAuth.AuthType;
import com.google.android.AndroidInfo;
import com.google.auth.Crypto;
import com.google.auth.DataField;
import com.google.auth.DataMapReader;

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
			final AndroidInfo dataSet = androidManager.getAndroidInfo(email);
			DataMapReader map = AndroidAuth.getMasterTokenResponse(dataSet,
					encryptedPassword, AuthType.EncryptedPassword);
			String auth = map.getData(DataField.MASTER_TOKEN);
			AuthType type = AuthType.MasterToken;
			if (auth == null || auth.isEmpty()) {
				Log.w(TAG,
						"Could not sign in using encryption, falling back to ssl-only encryption mode!");
				map = AndroidAuth.getMasterTokenResponse(dataSet, password,
						AuthType.Password);
				auth = map.getData(DataField.MASTER_TOKEN);
				if (auth == null || auth.isEmpty()) {
					Log.e(TAG,
							"Could not sign in using ssl-only encryption! Using password for future request instead of masterToken!");
					auth = password;
					type = AuthType.Password;
				}
			}
			Account account = androidManager.findAccount(email);
			if (account == null) {
				androidManager.addAccount(email, auth, type);
			} else {
				androidManager.updateAccount(account, auth, type);
			}
			account = androidManager.findAccount(email);
			final String sid = map.getData(DataField.SID);
			if (sid != null && !sid.isEmpty()) {
				androidManager.putAuthToken("SID", 0, null, null, sid, account);
			}
			final String lsid = map.getData(DataField.LSID);
			if (lsid != null && !lsid.isEmpty()) {
				androidManager.putAuthToken("LSID", 0, null, null, lsid,
						account);
			}
			return Activity.RESULT_OK;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			super.onPostExecute(result);
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
