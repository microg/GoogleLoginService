package com.google.android.gsf.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class LoginFragment extends Fragment {

	private LoginFragmentContainer container;

	public LoginFragment() {
		super();
	}

	public LoginFragmentContainer getContainer() {
		return container;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (container == null) {
			if (getActivity() instanceof LoginFragmentContainer) {
				container = (LoginFragmentContainer) getActivity();
			}
		}
	}

	public abstract void onBackPressed();

	public abstract void onNextPressed();
}
