package com.google.android.gsf;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class GoogleLoginCredentialsResult implements Parcelable {

	public static final android.os.Parcelable.Creator<GoogleLoginCredentialsResult> CREATOR = new android.os.Parcelable.Creator<GoogleLoginCredentialsResult>() {

		@Override
		public GoogleLoginCredentialsResult createFromParcel(final Parcel source) {
			return new GoogleLoginCredentialsResult(source);
		}

		@Override
		public GoogleLoginCredentialsResult[] newArray(final int size) {
			return new GoogleLoginCredentialsResult[size];
		}
	};
	private String account;
	private Intent credentialsIntent;

	private String credentialsString;

	public GoogleLoginCredentialsResult() {
		// Everything left null!
	}

	public GoogleLoginCredentialsResult(final Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return credentialsIntent == null ? 0 : credentialsIntent
				.describeContents();
	}

	public String getAccount() {
		return account;
	}

	public Intent getCredentialsIntent() {
		return credentialsIntent;
	}

	public String getCredentialsString() {
		return credentialsString;
	}

	public void readFromParcel(final Parcel in) {
		account = in.readString();
		credentialsString = in.readString();
		final int hasIntent = in.readInt();
		credentialsIntent = null;
		if (hasIntent == 1) {
			credentialsIntent = new Intent();
			credentialsIntent.readFromParcel(in);
			credentialsIntent.setExtrasClassLoader(getClass().getClassLoader());
		}
	}

	public void setAccount(final String account) {
		this.account = account;
	}

	public void setCredentialsIntent(final Intent credentialsIntent) {
		this.credentialsIntent = credentialsIntent;
	}

	public void setCredentialsString(final String credentialsString) {
		this.credentialsString = credentialsString;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(account);
		dest.writeString(credentialsString);
		if (credentialsIntent == null) {
			dest.writeInt(0);
		} else {
			dest.writeInt(1);
			credentialsIntent.writeToParcel(dest, flags);
		}
	}

}
