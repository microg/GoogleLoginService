package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class UsernamePasswordFragment extends LoginFragment implements
		TextWatcher {

	@SuppressWarnings("unused")
	private static final String TAG = "GoogleUsernamePassword";
	protected String email;
	protected EditText emailEdit;
	protected String password;
	protected EditText passwordEdit;

	@Override
	public void afterTextChanged(final Editable s) {
		readInput();
		if (checkInput()) {
			getContainer().showNextButton();
		} else {
			getContainer().showDisabledNextButton();
		}
	}

	@Override
	public void beforeTextChanged(final CharSequence s, final int start,
			final int count, final int after) {
		// Nothing...
	}

	protected boolean checkInput() {
		return (email != null && email.contains("@") && password != null && !password
				.isEmpty());
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().showBackButton();
		getContainer().showDisabledNextButton();
		final Bundle b = getContainer().getOptions();
		if (b.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
			emailEdit.setText(b.getString(AccountManager.KEY_ACCOUNT_NAME));
		}
		if (b.containsKey(AccountManager.KEY_PASSWORD)) {
			passwordEdit.setText(b.getString(AccountManager.KEY_PASSWORD));
		}
		readInput();
	}

	@Override
	public void onBackPressed() {
		getContainer().goAddAccountIntro();
		getContainer().getOptions().remove(AccountManager.KEY_ACCOUNT_NAME);
		getContainer().getOptions().remove(AccountManager.KEY_PASSWORD);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.username_password, null);
		emailEdit = (EditText) view.findViewById(R.id.edit_email);
		emailEdit.addTextChangedListener(this);
		passwordEdit = (EditText) view.findViewById(R.id.edit_password);
		passwordEdit.addTextChangedListener(this);
		return view;
	}

	@Override
	public void onNextPressed() {
		readInput();
		if (checkInput()) {
			final Bundle b = getContainer().getOptions();
			b.putString(AccountManager.KEY_ACCOUNT_NAME, email);
			b.putString(AccountManager.KEY_PASSWORD, password);
			getContainer().goLoginAction();
		}
	}

	@Override
	public void onTextChanged(final CharSequence s, final int start,
			final int before, final int count) {
		// Nothing...
	}

	protected void readInput() {
		email = emailEdit.getText().toString();
		password = passwordEdit.getText().toString();
	}
}
