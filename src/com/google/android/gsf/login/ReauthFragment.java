package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ReauthFragment extends UsernamePasswordFragment {

	@Override
	public void onBackPressed() {
		getContainer().resultCancelled();
	}

	@Override
	public void onNextPressed() {
		readInput();
		if (checkInput()) {
			final Bundle b = getContainer().getOptions();
			b.putString(AccountManager.KEY_ACCOUNT_NAME, email);
			b.putString(AccountManager.KEY_PASSWORD, password);
			getContainer().goReauthAction();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().hideBackButton();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.reauth, null);
		emailEdit = (EditText) view.findViewById(R.id.edit_email);
		emailEdit.addTextChangedListener(this);
		passwordEdit = (EditText) view.findViewById(R.id.edit_password);
		passwordEdit.addTextChangedListener(this);
		return view;
	}

}
