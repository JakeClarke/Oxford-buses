package uk.co.jakeclarke.oxfordbuses.settings;

import uk.co.jakeclarke.oxfordbuses.R;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about, container, false);
		TextView os = (TextView) v.findViewById(R.id.opensource);
		Linkify.addLinks(os, Linkify.ALL);
		TextView tv = (TextView) v.findViewById(R.id.legal);
		tv.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this
				.getActivity()));
		return v;
	}
}
