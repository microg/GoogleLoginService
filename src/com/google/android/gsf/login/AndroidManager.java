package com.google.android.gsf.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import com.google.android.AndroidAuth;
import com.google.android.AndroidContext;
import com.google.android.gsf.PrivacyExtension;
import com.google.auth.AuthType;
import com.google.tools.SignatureTools;

public class AndroidManager {

	public static final String KEY_AUTH_TOKEN_TYPE = "authTokenTypeKey";
	public static final String KEY_FORCE_PERMISSION = "forcePermissionKey";
	public static final String KEY_LOGIN_ACTION = "loginActionKey";
	public static final String KEY_AUTH_TYPE = "authTokenType";
	private static final String TAG = "AndroidManager";

	public static String getAuthTokenStore(final String service, final int uid, final String packageName,
										   final String packageSignature) {
		if (service.equals("SID") || service.equals("LSID") || service.equals("android") ||
			service.equals("androidsecure")) {
			return service;
		}
		return packageSignature + ":" + service;
	}

	public static String signatureDigest(final Signature signature) {
		return SignatureTools.signatureDigest(signature.toByteArray());
	}

	private final AccountManager accountManager;
	private final Context context;
	private final PackageManager packageManager;
	private final String type;

	public AndroidManager(final AccountManager accountManager, final PackageManager packageManager, final String type,
						  final Context context) {
		this.accountManager = accountManager;
		this.packageManager = packageManager;
		this.type = type;
		this.context = context;
	}

	public AndroidManager(final Context context) {
		this(AccountManager.get(context), context.getPackageManager(), context.getString(R.string.account_type),
			 context);
	}

	public void addAccount(final String email, final String auth, AuthType authType) {
		accountManager.addAccountExplicitly(new Account(email, type), auth, new Bundle());
		accountManager.setUserData(findAccount(email), KEY_AUTH_TYPE, authType.toInt() + "");
	}

	public void addOrUpdateAccount(final String email, final String auth, AuthType authType) {
		Account account = findAccount(email);
		if (account == null) {
			addAccount(email, auth, authType);
		} else {
			updateAccount(account, auth, authType);
		}
	}

	public void updateAccount(Account account, final String auth, AuthType authType) {
		accountManager.setPassword(account, auth);
		accountManager.setUserData(account, KEY_AUTH_TYPE, authType.toInt() + "");
	}

	public AuthType getAuthType(Account account) {
		try {
			return AuthType.fromInt(Integer.parseInt(accountManager.getUserData(account, KEY_AUTH_TYPE)));
		} catch (NumberFormatException e) {
			return AuthType.MasterToken;
		}
	}

	public Account findAccount(final String email) {
		final Account[] accounts = accountManager.getAccountsByType(type);
		for (final Account account : accounts) {
			if (account.name.equalsIgnoreCase(email)) {
				return account;
			}
		}
		return null;
	}

	public String getAuthToken(final String service, final int uid, final Account account) {
		final String packageName = getPackageNameForUid(uid);
		final String packageSignature = getFirstPackageSignatureDigest(packageName);
		return getAuthToken(service, uid, packageName, packageSignature, account);
	}

	public String getAuthToken(final String service, final int uid, final String packageName,
							   final String packageSignature, final Account account) {
		return accountManager.peekAuthToken(account, getAuthTokenStore(service, uid, packageName, packageSignature));
	}

	public String getFirstPackageSignatureDigest(final String packageName) {
		try {
			final PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			if (info != null && info.signatures != null && info.signatures.length > 0) {
				for (final Signature sig : info.signatures) {
					final String digest = signatureDigest(sig);
					if (digest != null) {
						return digest;
					}
				}
			}
		} catch (final Throwable t) {

		}
		return null;
	}

	public String getAuth(final Account account) {
		return accountManager.getPassword(account);
	}

	public String getAuth(final String email) {
		return getAuth(findAccount(email));
	}

	public String getPackageNameForUid(final int uid) {
		if (uid == 0) {
			return null;
		}
		final String[] packages = packageManager.getPackagesForUid(uid);
		for (final String pkg : packages) {
			if (pkg != null && !pkg.isEmpty()) {
				return pkg;
			}
		}
		throw null;
	}

	public void putAuthToken(final String service, final int uid, final String packageName,
							 final String packageSignature, final String authToken, final Account account) {
		if (!service.startsWith("weblogin:") && uid != 0) {
			accountManager
					.setAuthToken(account, getAuthTokenStore(service, uid, packageName, packageSignature), authToken);
		}
	}

	public AndroidContext getAndroidContext(String email) {
		return PrivacyExtension.getAndroidInfo(context).setEmail(email);
	}

	public AndroidAuth getAndroidAuth() {
		return new AndroidAuth();
	}

}
