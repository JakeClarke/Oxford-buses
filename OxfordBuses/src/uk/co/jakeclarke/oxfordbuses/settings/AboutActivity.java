package uk.co.jakeclarke.oxfordbuses.settings;

import uk.co.jakeclarke.oxfordbuses.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);

		TextView os = (TextView) findViewById(R.id.opensource);
		Linkify.addLinks(os, Linkify.ALL);

		TextView tv = (TextView) findViewById(R.id.legal);
		tv.setText(GooglePlayServicesUtil
				.getOpenSourceSoftwareLicenseInfo(this));
	}
}
