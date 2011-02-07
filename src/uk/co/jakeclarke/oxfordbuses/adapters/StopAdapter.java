package uk.co.jakeclarke.oxfordbuses.adapters;

import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * List item adapter (renderer) for a list of bus Stops
 *
 */
public class StopAdapter extends BaseAdapter
{
	protected List<Stop> elements;
	protected Context c;

	public StopAdapter(Context c, List<Stop> stops)
	{
		this.elements = stops;
		this.c = c;
	}

	public int getCount()
	{
		return elements.size();
	}

	public Stop getItem(int position)
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return null;
	}
}
