package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddAccountOutroFragment extends LoginFragment {

	private static final String highlightEnd = "</font>";
	private static final String highlightStart = "<font color=\"#33B5E5\">";
	@SuppressWarnings("unused")
	private final static String TAG = "GoogleAddAccountOutro";
	private TextView txt;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().hideBackButton();
		getContainer().showOkButton();
		final String email = getContainer().getOptions().getString(AccountManager.KEY_ACCOUNT_NAME);
		txt.setText(Html.fromHtml(
				getString(R.string.add_account_outro).replace("$m", highlightStart + email + highlightEnd)));
	}

	@Override
	public void onBackPressed() {
		onNextPressed();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.add_account_outro, null);
		txt = (TextView) view.findViewById(R.id.txt_info);
		return view;
	}

	@Override
	public void onNextPressed() {
		getContainer().resultAccountAdded();
	}

}
