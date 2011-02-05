package uk.co.jakeclarke.oxfordbuses.providers;

import uk.co.jakeclarke.oxfordbuses.utils.Constants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper
{
	public DatabaseHelper(Context context)
	{
		super(context, Constants.DATABASE_NAME , null, Constants.DATABASE_VERSION );
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + Constants.TABLE_NAME +
				" (" +
				Constants.KEY_NAPTAN + " text PRIMARY KEY," + 
				Constants.KEY_COORDS + " text, " +
				Constants.KEY_STOPNAME + " text, " +
				Constants.KEY_STOPBEARING + " integer, " +
				Constants.KEY_PARENTMAP + " integer);" 
		);
		createFavouritesTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion)
	{
		if(oldversion == 2 && newVersion == 3)
		{
			createFavouritesTable(db);
		}
		else
		{
			db.execSQL("DROP TABLE IF EXISTS" + Constants.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS" + Constants.FAVOURITES_TABLE_NAME);
		}
	}

	private void createFavouritesTable(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + Constants.FAVOURITES_TABLE_NAME +
				" (" + Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Constants.KEY_NAPTAN + "  text, " +
				Constants.KEY_STOPNAME + " text);");
	}
}