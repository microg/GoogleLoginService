package com.google.android.gsf.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ActionFragment extends LoginFragment {

	protected abstract void cancelAction();

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().enableProgressBar();
		getContainer().hideNextButton();
		getContainer().showCancelButton();
		startAction();
	}

	@Override
	public void onBackPressed() {
		cancelAction();
		getContainer().disableProgressBar();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.progress_action, null);
		return view;
	}

	protected abstract void startAction();

}