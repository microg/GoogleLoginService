package com.google.android.gsf.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.google.auth.AndroidDataSet;

public class AndroidManager {

	public static final String KEY_AUTH_TOKEN_TYPE = "authTokenTypeKey";
	public static final String KEY_FORCE_PERMISSION = "forcePermissionKey";
	public static final String KEY_LOGIN_ACTION = "loginActionKey";

	public static String getAuthTokenStore(final String service, final int uid,
			final String packageName, final String packageSignature) {
		if (service.equals("SID") || service.equals("LSID")
				|| service.equals("android") || service.equals("androidsecure")) {
			return service;
		}
		return "p-" + uid + "-" + packageSignature + "-" + packageName + "-"
				+ service;
	}

	public static String signatureDigest(final Signature signature) {
		byte temp[] = signature.toByteArray();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (final NoSuchAlgorithmException e) {
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

	private final AccountManager accountManager;
	private final Context context;
	private final PackageManager packageManager;
	private final String type;

	public AndroidManager(final AccountManager accountManager,
			final PackageManager packageManager, final String type,
			final Context context) {
		this.accountManager = accountManager;
		this.packageManager = packageManager;
		this.type = type;
		this.context = context;
	}

	public AndroidManager(final Context context) {
		this(AccountManager.get(context), context.getPackageManager(), context
				.getString(R.string.account_type), context);
	}

	public void addAccount(final String email, final String masterToken) {
		accountManager.addAccountExplicitly(new Account(email, type),
				masterToken, null);
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

	public AndroidDataSet getAndroidDataSet(final String email) {
		final String country = context.getResources().getConfiguration().locale
				.getCountry().toLowerCase(Locale.getDefault());
		final String lang = context.getResources().getConfiguration().locale
				.getLanguage().toLowerCase(Locale.getDefault());
		final String sdkVersion = android.os.Build.VERSION.SDK_INT + "";
		final String androidId = null;
		return new AndroidDataSet(email, country, country, lang, sdkVersion,
				androidId);
	}

	public String getAuthToken(final String service, final int uid,
			final Account account) {
		final String packageName = getPackageNameForUid(uid);
		final String packageSignature = getFirstPackageSignatureDigest(packageName);
		return getAuthToken(service, uid, packageName, packageSignature,
				account);
	}

	public String getAuthToken(final String service, final int uid,
			final String packageName, final String packageSignature,
			final Account account) {
		return accountManager.getUserData(account,
				getAuthTokenStore(service, uid, packageName, packageSignature));
	}

	public String getFirstPackageSignatureDigest(final String packageName) {
		try {
			final PackageInfo info = packageManager.getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			if (info != null && info.signatures != null
					&& info.signatures.length > 0) {
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

	public String getMasterToken(final Account account) {
		return accountManager.getPassword(account);
	}

	public String getMasterToken(final String email) {
		return getMasterToken(findAccount(email));
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

	public void putAuthToken(final String service, final int uid,
			final String packageName, final String packageSignature,
			final String authToken, final Account account) {
		if (!service.startsWith("weblogin:") && uid != 0) {
			accountManager.setUserData(
					account,
					getAuthTokenStore(service, uid, packageName,
							packageSignature), authToken);
		}
	}

}
