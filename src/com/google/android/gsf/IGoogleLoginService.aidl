package com.google.android.gsf;

import com.google.android.gsf.GoogleLoginCredentialsResult;
import com.google.android.gsf.LoginData;

interface IGoogleLoginService {
	GoogleLoginCredentialsResult blockingGetCredentials(String username, String service, boolean notifyAuthFailure);
	void deleteAllAccounts();
	void deleteOneAccount(String username);
	String getAccount(boolean requireGoogle);
	String[] getAccounts();
	long getAndroidId();
	String getPrimaryAccount();
	void invalidateAuthToken(String authtokenToInvalidate);
	String peekCredentials(String username, String service);
	void saveAuthToken(String account, String service, String authtoken);
	void saveNewAccount(in LoginData data);
	void saveUsernameAndPassword(String username, String password, int flags);
	void tryNewAccount(in LoginData data);
	void updatePassword(in LoginData data);
	boolean verifyStoredPassword(String username, String password);
	int waitForAndroidId();
}