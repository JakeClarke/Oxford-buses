package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DeparturesActivity extends FragmentActivity {

	public static String KEY_STOP = "stop";

	private DeparturesFragment departures;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Stop s = (Stop) this.getIntent().getParcelableExtra(KEY_STOP);
		if (s == null) {
			Log.e("DeparturesActivity", "No stop passed to activity");
			return;
		}
		this.setContentView(R.layout.departuresactivity);

		this.departures = (DeparturesFragment) this.getSupportFragmentManager()
				.findFragmentById(R.id.departuresfrag);

		this.departures.setStop(s);

	}
}
