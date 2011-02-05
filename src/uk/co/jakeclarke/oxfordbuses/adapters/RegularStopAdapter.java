package uk.co.jakeclarke.oxfordbuses.adapters;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.R;
import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegularStopAdapter extends BaseAdapter
{
	private List<Stop> elements;
	private Context c;

	public RegularStopAdapter(Context c, List<Stop> stops)
	{
		this.elements = stops;
		this.c = c;
	}

	public int getCount()
	{
		return elements.size();
	}

	public Object getItem(int position)
	{
		return elements.get(position);
	}

	public long getItemId(int id)
	{
		return id;
	}

	public void Remove(int id)
	{
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		LinearLayout rowLayout;
		Stop s = elements.get(position);

		if (convertView == null)
		{
			rowLayout = (LinearLayout)LayoutInflater.from(c).inflate(R.layout.item_renderer, parent, false);
			TextView stopName = (TextView)rowLayout.findViewById(R.id.stopname);
			TextView stopNaptanCode = (TextView)rowLayout.findViewById(R.id.stopnaptan);
			TextView stopDirection = (TextView)rowLayout.findViewById(R.id.stopdirection);
			stopName.setText(s.getStopName());
			stopNaptanCode.setText(s.getNaptanCode());
			stopDirection.setText(c.getString(R.string.stop_direction, s.getDirection()));
		}
		else
		{
			rowLayout = (LinearLayout)convertView;
		}
		return rowLayout;
	}
}
