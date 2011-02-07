package uk.co.jakeclarke.oxfordbuses.adapters;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * List item adapter (renderer) for the list of favourite bus Stops
 *
 */
public class FavouriteStopAdapter extends StopAdapter
{
	public FavouriteStopAdapter(Context c, List<Stop> stops)
	{
		super(c, stops);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout rowLayout;
		Stop s = elements.get(position);

		if (convertView == null)
		{
			rowLayout = (LinearLayout)LayoutInflater.from(c).inflate
			(R.layout.favouritestop, parent, false);
			TextView stopName = (TextView)rowLayout.findViewById(R.id.favstopname);
			TextView stopNaptanCode = (TextView)rowLayout.findViewById(R.id.favstopnaptan);
			stopName.setText(s.getStopName());
			stopNaptanCode.setText(s.getNaptanCode());
		}
		else
		{
			rowLayout = (LinearLayout)convertView;
		}
		return rowLayout;
	}
}
