package com.lumens98.birdiemakettest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StatDatabase {

	public static final String KEY_ID = "_id";
	public static final String KEY_DIST = "distance";
	public static final String KEY_ACC_INC = "accuracy_inc";
	public static final String KEY_ACC = "accuracy";
	public static final String KEY_ANG = "angle";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE = "StatTable";

	private static final String STATDATABASE_TABLE_CREATE = "CREATE TABLE "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DIST + " TEXT, "
			+ KEY_ACC_INC + " TEXT, " + KEY_ACC + " TEXT, " + KEY_ANG
			+ " TEXT);";

	private static final String[] TABLE_COLUMNS_DIST = { KEY_DIST };
	private static final String[] TABLE_COLUMNS_ANG = { KEY_ANG };

	private DatabaseHelper statdbHelper;
	private final Context statContext;
	private SQLiteDatabase statdb;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String Database_Name) {
			super(context, Database_Name, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(STATDATABASE_TABLE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);

		}

	}

	public StatDatabase(Context c) {
		statContext = c;
	}

	public StatDatabase open(String Database_Name) {
		statdbHelper = new DatabaseHelper(statContext, Database_Name);
		statdb = statdbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		statdbHelper.close();
	}

	public long putEntry(String clubDistance, String accuracy_inc,
			String clubAccuracy, String angle) {
		// TODO Auto-generated method stub

		ContentValues cv = new ContentValues();
		cv.put(KEY_DIST, clubDistance);
		cv.put(KEY_ACC_INC, accuracy_inc);
		cv.put(KEY_ACC, clubAccuracy);
		cv.put(KEY_ANG, angle);

		// Log.d("", clubDistance+" "+accuracy_inc+" "+clubAccuracy+" "+angle);

		return statdb.insert(DATABASE_TABLE, null, cv);

	}

	public String[] getDist() {
		// TODO Auto-generated method stub
		Cursor clubCursor = statdb.query(DATABASE_TABLE, TABLE_COLUMNS_DIST,
				null, null, null, null, null);
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iDist = clubCursor.getColumnIndex(KEY_DIST);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iDist);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}
	}

	public String[] getAng() {
		// TODO Auto-generated method stub
		Cursor clubCursor = statdb.query(DATABASE_TABLE, TABLE_COLUMNS_ANG,
				null, null, null, null, null);
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iAng = clubCursor.getColumnIndex(KEY_ANG);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iAng);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}

	}

	public void deleteAll() {
		// TODO Auto-generated method stub
		statdb.delete(DATABASE_TABLE, null, null);
	}

}
