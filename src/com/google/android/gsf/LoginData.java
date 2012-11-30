package com.google.android.gsf;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginData implements Parcelable {
	public enum Status {
		ACCOUNT_DISABLED, BAD_REQUEST, BAD_USERNAME, CANCELLED, CAPTCHA, DELETED_GMAIL, DMAGENT, LOGIN_FAIL, MISSING_APPS, NETWORK_ERROR, NO_GMAIL, OAUTH_MIGRATION_REQUIRED, SERVER_ERROR, SUCCESS;
	}

	public static final android.os.Parcelable.Creator<LoginData> CREATOR = new android.os.Parcelable.Creator<LoginData>() {

		@Override
		public LoginData createFromParcel(final Parcel source) {
			return new LoginData(source);
		}

		@Override
		public LoginData[] newArray(final int size) {
			return new LoginData[size];
		}
	};

	public String authToken;
	public String captchaAnswer;
	public byte captchaData[];
	public String captchaMimeType;
	public String captchaToken;
	public String encryptedPassword;
	public int flags;
	public String jsonString;
	public String oAuthAccessToken;
	public String password;
	public String service;
	public String sid;
	public Status status;
	public String username;

	public LoginData() {
		// TODO Auto-generated constructor stub
	}

	public LoginData(final Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readFromParcel(final Parcel in) {
		username = in.readString();
		encryptedPassword = in.readString();
		password = in.readString();
		service = in.readString();
		captchaToken = in.readString();
		final int len = in.readInt();
		if (len != -1) {
			captchaData = new byte[len];
			in.readByteArray(captchaData);
		} else {
			captchaData = null;
		}
		captchaMimeType = in.readString();
		captchaAnswer = in.readString();
		flags = in.readInt();
		final String statusRead = in.readString();
		if (statusRead != null) {
			status = Status.valueOf(statusRead);
		} else {
			status = null;
		}
		jsonString = in.readString();
		sid = in.readString();
		authToken = in.readString();
		oAuthAccessToken = in.readString();
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags_) {
		dest.writeString(username);
		dest.writeString(encryptedPassword);
		dest.writeString(password);
		dest.writeString(service);
		dest.writeString(captchaToken);
		if (captchaData != null) {
			dest.writeInt(captchaData.length);
			dest.writeByteArray(captchaData);
		} else {
			dest.writeInt(-1);
		}
		dest.writeString(captchaMimeType);
		dest.writeString(captchaAnswer);
		dest.writeInt(flags);
		if (status != null) {
			dest.writeString(status.name());
		} else {
			dest.writeString(null);
		}
		dest.writeString(jsonString);
		dest.writeString(sid);
		dest.writeString(authToken);
		dest.writeString(oAuthAccessToken);
	}
}
