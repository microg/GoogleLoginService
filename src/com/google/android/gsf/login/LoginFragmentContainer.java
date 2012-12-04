package com.google.android.gsf.login;

import android.os.Bundle;

public interface LoginFragmentContainer {
	public void disableProgressBar();

	public void enableProgressBar();

	public Bundle getOptions();

	public void goAddAccountIntro();

	public void goAddAccountOutro();

	public void goAuthTokenAction();

	public void goAuthTokenAskPermission();

	public void goLoginAction();

	public void goUsernamePassword();

	public void hideBackButton();

	public void hideNextButton();

	public void resultAccountAdded();

	public void resultAuthToken();

	public void resultCancelled();

	public void showAllowButton();

	public void showBackButton();

	public void showCancelButton();

	public void showDenyButton();

	public void showDisabledNextButton();

	public void showNextButton();

	public void showOkButton();
}