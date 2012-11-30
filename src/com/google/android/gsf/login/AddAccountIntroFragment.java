package com.google.android.gsf.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddAccountIntroFragment extends LoginFragment {

	@SuppressWarnings("unused")
	private final static String TAG = "GoogleAddAccountIntro";

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().showCancelButton();
		getContainer().showNextButton();
	}

	@Override
	public void onBackPressed() {
		getContainer().resultCancelled();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.add_account_intro, null);
		return view;
	}

	@Override
	public void onNextPressed() {
		getContainer().goUsernamePassword();
	}

}
