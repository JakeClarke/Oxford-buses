package uk.co.jakeclarke.oxfordbuses;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class StopProvider {
	
	private static final String TAG = "StopProvider";

    private static final String DATABASE_NAME = "stop.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "stops";
    private static final String FAVOURITES_TABLE_NAME = "favourites";
    private static final String KEY_NAPTAN = "naptancode";
    private static final String KEY_COORDS = "coords";
    private static final String KEY_STOPNAME = "stopName";
    private static final String KEY_STOPBEARING = "stopBearing";
    private static final String KEY_PARENTMAP = "parentMap";
    private static final String KEY_ID = " _id";
    
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

	private Context context;
    
    public StopProvider(Context ctx) 
    {
        this.context = ctx;
        dbHelper = new DatabaseHelper(context);
    }

	static class DatabaseHelper extends SQLiteOpenHelper{


		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME , null, DATABASE_VERSION );
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + TABLE_NAME +
					" (" +
					KEY_NAPTAN + " text PRIMARY KEY," + 
					KEY_COORDS + " text, " +
					KEY_STOPNAME + " text, " +
					KEY_STOPBEARING + " integer, " +
					KEY_PARENTMAP + " integer);" 
					);
			createFavouritesTable(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion) {
			// TODO Auto-generated method stub
			if(oldversion == 2 && newVersion == 3)
			{
				createFavouritesTable(db);
			}
			else {
				db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS" + FAVOURITES_TABLE_NAME);
			}
			

		}
		
		private void createFavouritesTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + FAVOURITES_TABLE_NAME +
					" (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_NAPTAN + "  text, " +
					KEY_STOPNAME + " text);");
		}
	}
	
	public StopProvider open() throws SQLException 
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
	
	public void close() {
		dbHelper.close();
	}
	
	public long insertStop(String stopname, String naptancode, String coords, int stopBearing, int parentmap)
	{
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_STOPNAME, stopname);
		initialValues.put(KEY_NAPTAN, naptancode);
		initialValues.put(KEY_COORDS, coords);
		initialValues.put(KEY_STOPBEARING, stopBearing);
		initialValues.put(KEY_PARENTMAP, parentmap);
		return db.insert(TABLE_NAME, null, initialValues);
		
	}
	
	public long insertFavourite(String stopname, String naptancode)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAPTAN, naptancode);
		initialValues.put(KEY_STOPNAME, stopname);
		return db.insert(FAVOURITES_TABLE_NAME, null, initialValues);
	}
	
	public boolean deleteStop(String naptan)
	{
		return db.delete(TABLE_NAME, KEY_NAPTAN + "='" + naptan  + "'", null) > 0;
	}
	
	public boolean deleteFavourite(String naptan)
	{
		
		return db.delete(FAVOURITES_TABLE_NAME, KEY_NAPTAN + "='" + naptan + "'", null) > 0;
	}
	
	public boolean clear(){
		return db.delete(TABLE_NAME, "1", null) > 0;
	}
	
	public boolean clearFavourites()
	{
		return db.delete(FAVOURITES_TABLE_NAME, "1", null) > 0;
	}
	
	public Cursor getAllStops()
	{
		Cursor mCursor = db.query(true, TABLE_NAME, new String[]{KEY_NAPTAN, KEY_COORDS, KEY_STOPNAME,KEY_STOPBEARING, KEY_PARENTMAP} , null, null, null, null, KEY_COORDS, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor getAllFavourites()
	{
		Cursor favCursor = db.query(true, FAVOURITES_TABLE_NAME, new String[]{KEY_ID, KEY_NAPTAN, KEY_STOPNAME}, null, null, null, null, KEY_STOPNAME, null);
		if(favCursor != null)
			favCursor.moveToFirst();
		return favCursor;
	}
	
	public int NumberOfRows()
	{
		int res;
		Cursor c = db.query(TABLE_NAME, new String[]{KEY_NAPTAN, KEY_STOPNAME},null, null, null, null, null, null);
		res = c.getCount();
		c.close();
		return res;
		
	}
	
	public int NumberOfFavourites()
	{
		int res;
		Cursor c = this.getAllFavourites();
		res = c.getCount();
		c.close();
		return res;
	}
	
	

}
