package uk.co.jakeclarke.oxfordbuses.settings;

import uk.co.jakeclarke.oxfordbuses.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		TextView tv = (TextView) findViewById(R.id.legal);
		tv.setText(GooglePlayServicesUtil
				.getOpenSourceSoftwareLicenseInfo(this));
	}
}
