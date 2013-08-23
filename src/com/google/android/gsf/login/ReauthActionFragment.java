package com.google.android.gsf.login;

import android.accounts.AccountManager;

public class ReauthActionFragment extends LoginActionFragment {
	@Override
	public void onBackPressed() {
		cancelAction();
		getContainer().disableProgressBar();
		getContainer().goReauth();
	}

	@Override
	public void onNextPressed() {
		getContainer().getOptions().remove(AccountManager.KEY_PASSWORD);
		getContainer().disableProgressBar();
		getContainer().goAuthTokenAction();
	}
}
