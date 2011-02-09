package uk.co.jakeclarke.oxfordbuses.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public final class OxontimeUtils {
	
	public static final String getTimesURL(String naptan, Context ctx) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean textOnly = prefs.getBoolean("oxontime_textmode", true);
		return getTimesURL(naptan, textOnly);
	}
	
	public static final String getTimesURL(String naptan, boolean textOnly) {
		return String.format(Constants.TIMES_URL_PATTERN, naptan, textOnly ? "1" : "0");
	}
	
	public static final Uri getTimesUri(String naptan, Context ctx) {
		return Uri.parse(getTimesURL(naptan, ctx));
	}
	
	public static final Uri getTimesUri(String naptan, boolean textOnly) {
		return Uri.parse(getTimesURL(naptan, textOnly));
	}

}
