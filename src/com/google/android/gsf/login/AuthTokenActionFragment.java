package com.google.android.gsf.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.util.Log;

import com.google.auth.AndroidClient;
import com.google.auth.AndroidDataSet;

public class AuthTokenActionFragment extends ActionFragment {

	private class ActionTask extends AsyncTask<String, Void, Integer> {

		public static final int RESULT_ERROR_DENIED = 32;
		public static final int RESULT_ERROR_PARAMS = 31;
		public static final int RESULT_ERROR_ACCOUNT = 33;
		public static final int RESULT_ERROR_PACKAGE = 34;
		public static final int RESULT_ERROR_TOKEN = 35;

		@Override
		protected Integer doInBackground(final String... params) {
			if (params.length < 3) {
				Log.w(TAG,
						"Not enough params, we need email, uid and forcePermission!");
				return RESULT_ERROR_PARAMS;
			}
			if (params[0] == null || params[0].isEmpty() || params[1] == null
					|| params[1].isEmpty() || params[2] == null
					|| params[2].isEmpty() || params[3] == null
					|| params[3].isEmpty()) {
				Log.w(TAG, "Params should not be null or empty!");
				return RESULT_ERROR_PARAMS;
			}
			String email = null;
			int uid = 0;
			String service = null;
			boolean forcePermission = false;
			try {
				email = params[0];
				uid = Integer.parseInt(params[1]);
				service = params[2];
				forcePermission = Boolean.parseBoolean(params[3]);
			} catch (final Throwable t) {
				Log.w(TAG, "Could not parse params as String,int,boolean!");
				return RESULT_ERROR_PARAMS;
			}
			AccountManager accountManager = AccountManager.get(getActivity());
			Account[] accounts = accountManager
					.getAccountsByType(getString(R.string.account_type));
			Account acc = null;
			for (Account account : accounts) {
				if (account.name.equalsIgnoreCase(email)) {
					acc = account;
				}
			}
			if (acc == null) {
				Log.w(TAG, "Could not find account for mail " + email);
				return RESULT_ERROR_ACCOUNT;
			}
			String masterToken = accountManager.getPassword(acc);
			String packageName = null;
			String packageSignature = null;
			PackageManager pm = getActivity().getPackageManager();
			if (uid != 0) {
				String[] packages = pm.getPackagesForUid(uid);
				for (String pkg : packages) {
					if (pkg != null && !pkg.isEmpty()) {
						packageName = pkg;
						break;
					}
				}
				if (packageName == null) {
					return RESULT_ERROR_PACKAGE;
				} else {
					try {
						PackageInfo info = pm.getPackageInfo(packageName,
								PackageManager.GET_SIGNATURES);
						if (info != null && info.signatures != null
								&& info.signatures.length > 0) {
							packageSignature = signatureDigest(info.signatures[0]);
						}
					} catch (Throwable t) {
						return RESULT_ERROR_PACKAGE;
					}
				}
			}
			// TODO get authToken from cache if available
			if (!forcePermission) {
				return RESULT_ERROR_DENIED;
			}

			AndroidDataSet dataSet = getContainer().getAndroidDataSet(email);
			String authToken = AndroidClient.getAuthToken(dataSet, masterToken,
					service, packageName, packageSignature, forcePermission);

			if (authToken == null) {
				return RESULT_ERROR_TOKEN;
			}
			getContainer().getOptions().putString(AccountManager.KEY_AUTHTOKEN,
					authToken);
			accountManager.setUserData(acc, "p-" + uid + "-" + service,
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
				AccountManager.KEY_AUTH_TOKEN_LABEL);
		final String callerUid = getContainer().getOptions().getInt(
				AccountManager.KEY_CALLER_UID, 0)
				+ "";
		final String force = getContainer().getOptions().getBoolean(
				LoginActivity.KEY_FORCE_PERMISSION, false)
				+ "";
		task.execute(email, callerUid, service, force);
	}

	public static String signatureDigest(final Signature signature) {
		byte temp[] = signature.toByteArray();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		if (md != null) {
			temp = md.digest(temp);
			if (temp != null) {
				return toHex(temp);
			}
		}
		return null;
	}

	public static String toHex(final byte buffer[]) {
		final StringBuffer stringbuffer = new StringBuffer(2 * buffer.length);
		for (final byte b : buffer) {
			final Object aobj[] = new Object[1];
			aobj[0] = Byte.valueOf(b);
			stringbuffer.append(String.format("%02x", aobj));
		}
		return stringbuffer.toString();
	}

}
