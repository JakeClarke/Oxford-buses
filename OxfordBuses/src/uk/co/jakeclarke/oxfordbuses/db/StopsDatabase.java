package uk.co.jakeclarke.oxfordbuses.db;

import uk.co.jakeclarke.oxfordbuses.StopMapManager;
import uk.co.jakeclarke.oxfordbuses.StopMapManager.Stop;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

public class StopsDatabase {

	private static final String TAG = "StopProvider";

	private static final String DATABASE_NAME = "stop.db";
	private static final int DATABASE_VERSION = 4;
	private static final String STOP_TABLE_NAME = "stops";
	private static final String FAVOURITES_TABLE_NAME = "favourites";
	private static final String KEY_NAPTAN = "naptancode";
	private static final String KEY_SCN = "scn";
	private static final String KEY_COORDS_LAT = "lat";
	private static final String KEY_COORDS_LNG = "long";
	private static final String KEY_STOPNAME = "stopName";
	private static final String KEY_ID = " _id";

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private Context context;

	public StopsDatabase(Context ctx) {
		this.context = ctx;
		dbHelper = new DatabaseHelper(context);
	}

	static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + STOP_TABLE_NAME + " (" + KEY_NAPTAN
					+ " text PRIMARY KEY," + KEY_COORDS_LAT + " DOUBLE, "
					+ KEY_COORDS_LNG + " DOUBLE, " + KEY_STOPNAME + " text, "
					+ KEY_SCN + " text);");
			createFavouritesTable(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion) {
			// TODO Auto-generated method stub
			if (oldversion == 2 && newVersion == 3) {
				createFavouritesTable(db);
			} else {
				db.execSQL("DROP TABLE IF EXISTS" + STOP_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS" + FAVOURITES_TABLE_NAME);
			}

		}

		private void createFavouritesTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + FAVOURITES_TABLE_NAME + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAPTAN
					+ "  text);");
		}
	}

	public StopsDatabase open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long insertStop(Stop s) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_STOPNAME, s.Name);
		initialValues.put(KEY_NAPTAN, s.Naptan);
		initialValues.put(KEY_SCN, s.Scn);
		initialValues.put(KEY_COORDS_LAT, s.latlong.latitude);
		initialValues.put(KEY_COORDS_LNG, s.latlong.longitude);
		return db.insert(STOP_TABLE_NAME, null, initialValues);

	}

	public void insertStops(Stop[] stops) {
		db.beginTransaction();
		for (Stop s : stops) {
			insertStop(s);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public long insertFavourite(Stop s) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAPTAN, s.Naptan);
		return db.insert(FAVOURITES_TABLE_NAME, null, initialValues);
	}

	public boolean deleteStop(String naptan) {
		return db.delete(STOP_TABLE_NAME, KEY_NAPTAN + "='" + naptan + "'",
				null) > 0;
	}

	public boolean deleteFavourite(String naptan) {

		return db.delete(FAVOURITES_TABLE_NAME, KEY_NAPTAN + "='" + naptan
				+ "'", null) > 0;
	}

	public boolean clear() {
		return db.delete(STOP_TABLE_NAME, "1", null) > 0;
	}

	public boolean clearFavourites() {
		return db.delete(FAVOURITES_TABLE_NAME, "1", null) > 0;
	}

	public Stop[] getAllStops() {

		Cursor mCursor = db.query(true, STOP_TABLE_NAME, null, null, null,
				null, null, null, null);
		if (mCursor != null & mCursor.getCount() > 0) {
			mCursor.moveToFirst();

			Stop[] stops = new Stop[mCursor.getCount()];

			final int naptanColIndex = mCursor.getColumnIndex(KEY_NAPTAN);
			final int stopnameColIndex = mCursor.getColumnIndex(KEY_STOPNAME);
			final int latColIndex = mCursor.getColumnIndex(KEY_COORDS_LAT);
			final int lonColIndex = mCursor.getColumnIndex(KEY_COORDS_LNG);
			final int scnColIndex = mCursor.getColumnIndex(KEY_SCN);

			for (int i = 0; i < stops.length; i++) {
				stops[i] = new Stop();
				stops[i].latlong = new LatLng(mCursor.getDouble(latColIndex),
						mCursor.getDouble(lonColIndex));
				stops[i].Name = mCursor.getString(stopnameColIndex);
				stops[i].Scn = mCursor.getString(scnColIndex);
				stops[i].Naptan = mCursor.getString(naptanColIndex);
				mCursor.moveToNext();
			}

			mCursor.close();

			return stops;

		}

		return null;
	}

	public Cursor getAllFavourites() {
		Cursor favCursor = db.query(true, FAVOURITES_TABLE_NAME, new String[] {
				KEY_ID, KEY_NAPTAN, KEY_STOPNAME }, null, null, null, null,
				KEY_STOPNAME, null);
		if (favCursor != null)
			favCursor.moveToFirst();
		return favCursor;
	}

	public int NumberOfRows() {
		int res;
		Cursor c = db.query(STOP_TABLE_NAME, new String[] { KEY_NAPTAN,
				KEY_STOPNAME }, null, null, null, null, null, null);
		res = c.getCount();
		c.close();
		return res;

	}

	public int NumberOfFavourites() {
		int res;
		Cursor c = this.getAllFavourites();
		res = c.getCount();
		c.close();
		return res;
	}

}
