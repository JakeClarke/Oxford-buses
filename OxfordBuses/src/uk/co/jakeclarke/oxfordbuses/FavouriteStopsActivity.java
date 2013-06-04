package uk.co.jakeclarke.oxfordbuses;

import uk.co.jakeclarke.oxfordbuses.StopListFragment.SelectionListener;
import uk.co.jakeclarke.oxfordbuses.StopsProvider.Stop;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FavouriteStopsActivity extends FragmentActivity {
	private StopsProvider stopsProvider;
	private StopListFragment stopListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.stoplistactivity);

		this.stopListFragment = (StopListFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.stoplist);

		stopsProvider = new StopsProvider(this);

		this.stopListFragment.setSelectionListener(new SelectionListener() {

			@Override
			void onSelection(Stop selection) {
				Intent i = new Intent(FavouriteStopsActivity.this,
						DeparturesActivity.class);

				i.putExtra(DeparturesActivity.KEY_STOP, selection);
				FavouriteStopsActivity.this.startActivity(i);
			}
		});
	}

	@Override
	public void onResume() {
		this.stopListFragment.setStops(this.stopsProvider.getFavouriteStops());
		super.onResume();
	}
}
