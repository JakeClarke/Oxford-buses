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

public class StopProvider {
	
	private static final String TAG = "StopProvider";

    private static final String DATABASE_NAME = "stop.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "stops";
    private static final String KEY_NAPTAN = "naptancode";
    private static final String KEY_COORDS = "coords";
    private static final String KEY_STOPNAME = "stopName";
    private static final String KEY_STOPBEARING = "stopBearing";
    private static final String KEY_PARENTMAP = "parentMap";
    
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
					" (" + KEY_NAPTAN + " text primary key," + 
					KEY_COORDS + " text, " +
					KEY_STOPNAME + " text, " +
					KEY_STOPBEARING + " integer, " +
					KEY_PARENTMAP + " integer);" 
					);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);

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
	
	public boolean deleteStop(String naptan)
	{
		return db.delete(TABLE_NAME, KEY_NAPTAN + "=" + naptan , null) > 0;
	}
	
	public boolean clear(){
		return db.delete(TABLE_NAME, "1", null) > 0;
	}
	
	public Cursor getAllStops()
	{
		Cursor mCursor = db.query(true, TABLE_NAME, new String[]{KEY_NAPTAN, KEY_COORDS, KEY_STOPNAME,KEY_STOPBEARING, KEY_PARENTMAP} , null, null, null, null, KEY_COORDS, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public int NumberOfRows()
	{
		int res;
		Cursor c = db.query(TABLE_NAME, new String[]{KEY_NAPTAN, KEY_STOPNAME},null, null, null, null, null, null);
		res = c.getCount();
		c.close();
		return res;
		
	}
	
	

}
