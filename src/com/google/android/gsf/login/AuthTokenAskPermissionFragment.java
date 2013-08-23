package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AuthTokenAskPermissionFragment extends LoginFragment {
	@SuppressWarnings("unused")
	private final static String TAG = "GoogleAskPermission";
	@SuppressWarnings("unused")
	private ImageView accountImg;
	private TextView accountTxt;
	private ImageView appImg;

	private TextView appTxt;
	@SuppressWarnings("unused")
	private ImageView serviceImg;
	private TextView serviceTxt;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().showAllowButton();
		getContainer().showDenyButton();
		final String account = getContainer().getOptions().getString(AccountManager.KEY_ACCOUNT_NAME);
		final String service = getContainer().getOptions().getString(AndroidManager.KEY_AUTH_TOKEN_TYPE).split(":")[0];
		final int uid = getContainer().getOptions().getInt(AccountManager.KEY_CALLER_UID);

		CharSequence app = null;
		Drawable icon = null;

		if (uid != 0) {
			final PackageManager pm = getActivity().getPackageManager();
			final String[] packages = pm.getPackagesForUid(uid);
			for (final String pkg : packages) {
				if (pkg != null && !pkg.isEmpty()) {
					try {
						final ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
						if (info != null) {
							app = info.loadLabel(pm);
							icon = info.loadIcon(pm);
							if (app != null && app.length() > 0) {
								break;
							}
						}
					} catch (final NameNotFoundException e) {
					}
				}
			}
			if ((app == null || app.length() == 0) && packages.length > 0) {
				app = packages[0];
			}
		}

		accountTxt.setText(account);
		serviceTxt.setText(service);
		appTxt.setText(app);
		if (icon != null) {
			appImg.setImageDrawable(icon);
		} else {
		}
	}

	@Override
	public void onBackPressed() {
		getContainer().resultCancelled();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.ask_auth_token, null);
		appTxt = (TextView) view.findViewById(R.id.txt_app);
		accountTxt = (TextView) view.findViewById(R.id.txt_account);
		serviceTxt = (TextView) view.findViewById(R.id.txt_service);
		appImg = (ImageView) view.findViewById(R.id.img_app);
		accountImg = (ImageView) view.findViewById(R.id.img_account);
		serviceImg = (ImageView) view.findViewById(R.id.img_service);
		return view;
	}

	@Override
	public void onNextPressed() {
		getContainer().getOptions().putBoolean(AndroidManager.KEY_FORCE_PERMISSION, true);
		getContainer().goAuthTokenAction();
	}

}
