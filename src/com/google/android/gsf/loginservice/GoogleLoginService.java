package com.google.android.gsf.loginservice;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gsf.GoogleLoginCredentialsResult;
import com.google.android.gsf.IGoogleLoginService;
import com.google.android.gsf.LoginData;
import com.google.android.gsf.login.LoginActivity;
import com.google.android.gsf.login.R;

public class GoogleLoginService extends Service {

	public class AccountAuthenticator extends AbstractAccountAuthenticator {

		public AccountAuthenticator() {
			super(GoogleLoginService.this);
		}

		@Override
		public Bundle addAccount(final AccountAuthenticatorResponse response,
				final String accountType, final String authTokenType,
				final String[] requiredFeatures, final Bundle options)
				throws NetworkErrorException {
			Log.d(TAG, "AccountAuthenticatorImpl.addAccount");
			if (accountType.equals(GoogleLoginService.this
					.getString(R.string.account_type))) {
				final Intent i = new Intent(GoogleLoginService.this,
						LoginActivity.class);
				i.putExtras(options);
				i.putExtra(LoginActivity.KEY_LOGIN_ACTION,
						LoginActivity.LOGIN_ACTION_ADD_ACCOUNT);
				i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
						response);
				i.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
				i.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType);
				final Bundle result = new Bundle();
				result.putParcelable(AccountManager.KEY_INTENT, i);
				return result;
			}
			return null;
		}

		@Override
		public Bundle confirmCredentials(
				final AccountAuthenticatorResponse response,
				final Account account, final Bundle options)
				throws NetworkErrorException {
			Log.w(TAG,
					"Not yet implemented: AccountAuthenticatorImpl.confirmCredentials");
			return null;
		}

		@Override
		public Bundle editProperties(
				final AccountAuthenticatorResponse response,
				final String accountType) {
			// TODO Auto-generated method stub
			Log.w(TAG,
					"Not yet implemented: AccountAuthenticatorImpl.editProperties");
			return null;
		}

		@Override
		public Bundle getAuthToken(final AccountAuthenticatorResponse response,
				final Account account, final String authTokenType,
				final Bundle options) throws NetworkErrorException {
			Log.d(TAG, "AccountAuthenticatorImpl.getAuthToken");
			if (account.type.equals(GoogleLoginService.this
					.getString(R.string.account_type))) {
				int uid = options.getInt("callerUid");
				if (uid != 0) {
					String p = accountManager.getUserData(account, "p-" + uid
							+ "-" + authTokenType);
					if (p != null && !p.isEmpty()) {
						Bundle result = new Bundle();
						result.putString(AccountManager.KEY_ACCOUNT_TYPE,
								account.type);
						result.putString(AccountManager.KEY_ACCOUNT_NAME,
								account.name);
						result.putString(AccountManager.KEY_AUTHTOKEN, p);
						return result;
					}
				}
				final Intent i = new Intent(GoogleLoginService.this,
						LoginActivity.class);
				i.putExtras(options);
				i.putExtra(LoginActivity.KEY_LOGIN_ACTION,
						LoginActivity.LOGIN_ACTION_AUTH_TOKEN);
				i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
						response);
				i.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				i.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
				i.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType);
				final Bundle result = new Bundle();
				result.putParcelable(AccountManager.KEY_INTENT, i);
				return result;
			}
			return null;
		}

		@Override
		public String getAuthTokenLabel(final String authTokenType) {
			// TODO Auto-generated method stub
			Log.w(TAG,
					"Not yet implemented: AccountAuthenticatorImpl.getAuthTokenLabel");
			return null;
		}

		@Override
		public Bundle hasFeatures(final AccountAuthenticatorResponse response,
				final Account account, final String[] features)
				throws NetworkErrorException {
			// TODO Auto-generated method stub
			Log.w(TAG,
					"Not yet implemented: AccountAuthenticatorImpl.hasFeatures");
			return null;
		}

		@Override
		public Bundle updateCredentials(
				final AccountAuthenticatorResponse response,
				final Account account, final String authTokenType,
				final Bundle options) throws NetworkErrorException {
			// TODO Auto-generated method stub
			Log.w(TAG,
					"Not yet implemented: AccountAuthenticatorImpl.updateCredentials");
			return null;
		}

	}

	public class LoginService extends IGoogleLoginService.Stub {

		@Override
		public GoogleLoginCredentialsResult blockingGetCredentials(
				final String username, final String service,
				final boolean notifyAuthFailure) throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG,
					"Not yet implemented: LoginService.blockingGetCredentials");
			return null;
		}

		@Override
		public void deleteAllAccounts() throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.deleteAllAccounts");

		}

		@Override
		public void deleteOneAccount(final String username)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.deleteOneAccount");

		}

		@Override
		public String getAccount(final boolean requireGoogle)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.getAccount");
			return null;
		}

		@Override
		public String[] getAccounts() throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.getAccounts");
			return null;
		}

		@Override
		public long getAndroidId() throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.getAndroidId");
			return 0;
		}

		@Override
		public String getPrimaryAccount() throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.getPrimaryAccount");
			return null;
		}

		@Override
		public void invalidateAuthToken(final String authtokenToInvalidate)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.invalidateAuthToken");

		}

		@Override
		public String peekCredentials(final String username,
				final String service) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public void saveAuthToken(final String account, final String service,
				final String authtoken) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public void saveNewAccount(final LoginData data) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public void saveUsernameAndPassword(final String username,
				final String password, final int flags) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public void tryNewAccount(final LoginData data) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public void updatePassword(final LoginData data) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public boolean verifyStoredPassword(final String username,
				final String password) throws RemoteException {
			throw new UnsupportedOperationException("Deprecated");
		}

		@Override
		public int waitForAndroidId() throws RemoteException {
			// TODO Auto-generated method stub
			Log.w(TAG, "Not yet implemented: LoginService.waitForAndroidId");
			return 0;
		}

	}

	private static final String TAG = "GoogleLoginService";

	private AccountAuthenticator accountAuthenticator;
	private AccountManager accountManager;
	private LoginService loginService;

	@Override
	public IBinder onBind(final Intent intent) {
		if (intent.getAction().equals(
				android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
			return accountAuthenticator.getIBinder();
		} else if (intent.getAction().equals(
				"com.google.android.gsf.action.GET_GLS")) {
			return loginService;
		}
		return null;
	}

	@Override
	public void onCreate() {
		accountAuthenticator = new AccountAuthenticator();
		loginService = new LoginService();
		accountManager = AccountManager.get(this);
	}

}
