package uk.co.jakeclarke.oxfordbuses;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}

}
