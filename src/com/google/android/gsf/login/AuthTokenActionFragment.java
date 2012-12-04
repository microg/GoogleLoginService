package com.google.android.gsf.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;

import com.google.auth.AndroidClient;
import com.google.auth.DataField;
import com.google.auth.DataMapReader;

public class AuthTokenActionFragment extends ActionFragment {

	private class ActionTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(final String... params) {
			final String email = params[0];
			final int uid = Integer.parseInt(params[1]);
			final String service = params[2];
			final boolean forcePermission = Boolean.parseBoolean(params[3]);
			final AndroidManager androidManager = new AndroidManager(
					getActivity());
			final Account account = androidManager.findAccount(email);
			final String packageName = androidManager.getPackageNameForUid(uid);
			final String packageSignature = androidManager
					.getFirstPackageSignatureDigest(packageName);
			String authToken = androidManager.getAuthToken(service, uid,
					packageName, packageSignature, account);
			if (authToken == null || forcePermission) {
				final DataMapReader map = AndroidClient.getAuthTokenResponse(
						androidManager.getAndroidDataSet(email),
						androidManager.getMasterToken(account), service,
						packageName, packageSignature, forcePermission);
				authToken = map.getData(DataField.AUTH_TOKEN);
				if (authToken == null) {
					return Activity.RESULT_CANCELED;
				}
				final String sid = map.getData(DataField.SID);
				if (sid != null && !sid.isEmpty()) {
					androidManager.putAuthToken("SID", uid, packageName,
							packageSignature, sid, account);
				}
				final String lsid = map.getData(DataField.LSID);
				if (lsid != null && !lsid.isEmpty()) {
					androidManager.putAuthToken("LSID", uid, packageName,
							packageSignature, lsid, account);
				}
				androidManager.putAuthToken(service, uid, packageName,
						packageSignature, authToken, account);
			}
			getContainer().getOptions().putString(AccountManager.KEY_AUTHTOKEN,
					authToken);
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

	@SuppressWarnings("unused")
	private static final String TAG = "GoogleAuthTokenAction";

	private ActionTask task;

	@Override
	protected void cancelAction() {
		task.cancel(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getContainer().goAuthTokenAskPermission();
	}

	@Override
	public void onNextPressed() {
		getContainer().resultAuthToken();
	}

	@Override
	protected void startAction() {
		task = new ActionTask();
		final String email = getContainer().getOptions().getString(
				AccountManager.KEY_ACCOUNT_NAME);
		final String service = getContainer().getOptions().getString(
				AndroidManager.KEY_AUTH_TOKEN_TYPE);
		final String callerUid = getContainer().getOptions().getInt(
				AccountManager.KEY_CALLER_UID, 0)
				+ "";
		final String force = getContainer().getOptions().getBoolean(
				AndroidManager.KEY_FORCE_PERMISSION, false)
				+ "";
		task.execute(email, callerUid, service, force);
	}

}
