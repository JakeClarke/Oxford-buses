package uk.co.jakeclarke.oxfordbuses.providers;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakeclarke.oxfordbuses.datatypes.Stop;
import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StopProvider {

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private Context context;

	public StopProvider(Context ctx) 
	{
		this.context = ctx;
		dbHelper = new DatabaseHelper(context);
	}

	private void open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
	}

	private void close()
	{
		dbHelper.close();
	}

	public long insertStop(Stop stop)
	{
		open();
		ContentValues initialValues = new ContentValues();
		initialValues.put(Constants.KEY_STOPNAME, stop.getStopName());
		initialValues.put(Constants.KEY_NAPTAN, stop.getNaptanCode());
		initialValues.put(Constants.KEY_COORDS, stop.getCoords());
		initialValues.put(Constants.KEY_STOPBEARING, stop.getStopBearing());
		initialValues.put(Constants.KEY_PARENTMAP, stop.getParentMap());
		long rowId = db.insert(Constants.TABLE_NAME, null, initialValues);
		close();
		return rowId;
	}

	public long insertFavourite(String stopname, String naptancode)
	{
		open();
		ContentValues initialValues = new ContentValues();
		initialValues.put(Constants.KEY_NAPTAN, naptancode);
		initialValues.put(Constants.KEY_STOPNAME, stopname);
		long rowId = db.insert(Constants.FAVOURITES_TABLE_NAME, null, initialValues);
		close();
		return rowId;
	}

	public boolean deleteFavourite(String naptan)
	{
		open();
		boolean deleted = db.delete(Constants.FAVOURITES_TABLE_NAME, Constants.KEY_NAPTAN + "='" + naptan + "'", null) > 0;
		close();
		return deleted;
	}

	public boolean clearStops()
	{
		open();
		boolean deleted = db.delete(Constants.TABLE_NAME, "1", null) > 0;
		close();
		return deleted;
	}

	public boolean clearFavourites()
	{
		open();
		boolean deleted = db.delete(Constants.FAVOURITES_TABLE_NAME, "1", null) > 0;
		close();
		return deleted;
	}

	public List<Stop> getAllStops()
	{
		open();
		ArrayList<Stop> stops = new ArrayList<Stop> ();
		Cursor mCursor = db.query(true,
				Constants.TABLE_NAME, 
				new String[]
				           {
				Constants.KEY_NAPTAN,
				Constants.KEY_COORDS,
				Constants.KEY_STOPNAME,
				Constants.KEY_STOPBEARING,
				Constants.KEY_PARENTMAP
				           },
				           null, null, null, null,
				           Constants.KEY_COORDS,
				           null);
		if(mCursor != null)
		{
			mCursor.moveToFirst();
			for (int i = 0 ; i < mCursor.getCount() ; i++)
			{
				stops.add(new Stop (mCursor));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		close();
		return stops;
	}

	public List<Stop> getAllFavourites()
	{
		open();
		ArrayList<Stop> stops = new ArrayList<Stop> ();
		Cursor favCursor = db.query(true,
				Constants.FAVOURITES_TABLE_NAME,
				new String[]
				           {
				Constants.KEY_ID,
				Constants.KEY_NAPTAN,
				Constants.KEY_STOPNAME
				           },
				           null, null, null, null,
				           Constants.KEY_STOPNAME,
				           null);
		if(favCursor != null)
		{
			favCursor.moveToFirst();
			for (int i = 0 ; i < favCursor.getCount() ; i++)
			{
				Stop current = new Stop ();
				current.setNaptanCode(favCursor.getString(1));
				current.setStopName(favCursor.getString(2));
				stops.add(current);
				favCursor.moveToNext();
			}
		}
		favCursor.close();
		close();
		return stops;
	}
}