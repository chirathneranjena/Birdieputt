package com.lumens98.birdiemakettest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClubDatabase {

	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "club_name";
	public static final String KEY_REACH_HIGH = "club_reach_high";
	public static final String KEY_REACH_LOW = "club_reach_low";

	public static final String KEY_USE = "club_use";

	public static final String KEY_STATS_ENTRIES = "stats_entries";
	public static final String KEY_STATS_HIGH = "stats_high";
	public static final String KEY_STATS_LOW = "stats_low";
	public static final String KEY_STATS_AVG = "stats_avg";

	public static final String KEY_ACC_ENTRIES = "acc_entries";
	public static final String KEY_ACC_HIGH = "acc_high";
	public static final String KEY_ACC_LOW = "acc_low";
	public static final String KEY_ACC_AVG = "acc_avg";
	
	public static final String KEY_ANG_MAX = "ang_max";
	public static final String KEY_ANG_MIN = "ang_min";
	public static final String KEY_ANG_AVG = "ang_avg";

	private static final String DATABASE_NAME = "ClubDatabase";
	private static final String DATABASE_TABLE = "ClubTable";
	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper clubdbHelper;
	private final Context clubContext;
	private SQLiteDatabase clubdb;

	public static final int DATABASE_COLUMNS = 16;
	private static final String CLUBDATABASE_TABLE_CREATE = "CREATE TABLE "
			+ DATABASE_TABLE + " (" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ KEY_NAME + " TEXT NOT NULL, " 
			+ KEY_REACH_HIGH + " INTEGER, " 
			+ KEY_REACH_LOW	+ " INTEGER, " 
			+ KEY_USE + " TEXT, " 
			+ KEY_STATS_ENTRIES + " TEXT, "
			+ KEY_STATS_HIGH + " TEXT, " 
			+ KEY_STATS_LOW + " TEXT, "
			+ KEY_STATS_AVG + " TEXT, " 
			+ KEY_ACC_ENTRIES + " TEXT, "
			+ KEY_ACC_HIGH + " TEXT, " 
			+ KEY_ACC_LOW + " TEXT, " 
			+ KEY_ACC_AVG + " TEXT, "
			+ KEY_ANG_MAX + " TEXT, " 
			+ KEY_ANG_MIN + " TEXT, " 
			+ KEY_ANG_AVG + " TEXT);";

	private static final String[] TABLE_COLUMNS_IDS = { KEY_ID };
	private static final String[] TABLE_COLUMNS_NAMES = { KEY_ID, KEY_NAME };
	private static final String[] TABLE_COLUMNS_HIGHS = { KEY_ID,
			KEY_REACH_HIGH };
	private static final String[] TABLE_COLUMNS_LOWS = { KEY_ID, KEY_REACH_LOW };
	private static final String[] TABLE_COLUMNS_USE = { KEY_ID, KEY_USE };
	private static final String[] TABLE_COLUMNS_DETAILS = { KEY_ID,
			KEY_REACH_HIGH, KEY_REACH_LOW };

	private static final String[] TABLE_COLUMNS_STATS = { KEY_STATS_ENTRIES,
			KEY_STATS_HIGH, KEY_STATS_LOW, KEY_STATS_AVG };
	private static final String[] TABLE_COLUMNS_ACC = { KEY_ACC_ENTRIES,
			KEY_ACC_HIGH, KEY_ACC_LOW, KEY_ACC_AVG, KEY_ANG_MAX, KEY_ANG_MIN, KEY_ANG_AVG };

	private static final String[] TABLE_COLUMNS_ALL = { KEY_ID, KEY_NAME,
			KEY_REACH_HIGH, KEY_REACH_LOW, KEY_USE, KEY_STATS_ENTRIES,
			KEY_STATS_HIGH, KEY_STATS_LOW, KEY_STATS_AVG, KEY_ACC_ENTRIES,
			KEY_ACC_HIGH, KEY_ACC_LOW, KEY_ACC_AVG, KEY_ANG_MAX, KEY_ANG_MIN, KEY_ANG_AVG };

	private static final String CLUBDATABASE_TABLE_UPDATE = "DROP TABLE IF EXISTS "
			+ DATABASE_TABLE;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CLUBDATABASE_TABLE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(CLUBDATABASE_TABLE_UPDATE);
			onCreate(db);

		}

	}

	public ClubDatabase(Context c) {
		clubContext = c;
	}

	public ClubDatabase open() {
		clubdbHelper = new DatabaseHelper(clubContext);
		clubdb = clubdbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		clubdbHelper.close();
	}

	public String[] getClubIDs() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_IDS,
				null, null, null, null, KEY_REACH_HIGH + " DESC");
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iName = clubCursor.getColumnIndex(KEY_ID);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iName);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}
	}

	public String[] getClubNames() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_NAMES,
				null, null, null, null, KEY_REACH_HIGH + " DESC");
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iName = clubCursor.getColumnIndex(KEY_NAME);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iName);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}
	}

	public String[] getClubDetails() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_DETAILS,
				null, null, null, null, KEY_REACH_HIGH + " DESC");

		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iHigh = clubCursor.getColumnIndex(KEY_REACH_HIGH);
			int iLow = clubCursor.getColumnIndex(KEY_REACH_LOW);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {


				result[i] = clubCursor.getString(iLow) + " to "
						+ clubCursor.getString(iHigh) + " yds";

				i++;
			}
			clubCursor.deactivate();
			return result;
		}
	}

	public long createnewclub(String newClub, String newHigh, String newLow) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, newClub);
		cv.put(KEY_REACH_HIGH, newHigh);
		cv.put(KEY_REACH_LOW, newLow);
		cv.put(KEY_USE, "TRUE");
		cv.put(KEY_STATS_ENTRIES, "0");
		cv.put(KEY_STATS_HIGH, "0");
		cv.put(KEY_STATS_LOW, "0");
		cv.put(KEY_STATS_AVG, "0");
		cv.put(KEY_ACC_ENTRIES, "0");
		cv.put(KEY_ACC_HIGH, "0");
		cv.put(KEY_ACC_LOW, "0");
		cv.put(KEY_ACC_AVG, "0");
		cv.put(KEY_ANG_MAX, "0");
		cv.put(KEY_ANG_MIN, "0");
		cv.put(KEY_ANG_AVG, "0");

		return clubdb.insert(DATABASE_TABLE, null, cv);

	}

	public String[] getClubByID(String clubID) {
		// TODO Auto-generated method stub

		String[] result = new String[DATABASE_COLUMNS];

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_ALL,
				KEY_ID + "=" + clubID, null, null, null, null);
		clubCursor.moveToFirst();

		for (int i = 0; i < DATABASE_COLUMNS; i++) {
			result[i] = clubCursor.getString(i);
		}

		clubCursor.deactivate();
		return result;
	}

	public void UseStats(String clubID, String reachHigh, String reachLow) {
		// TODO Auto-generated method stub
		ContentValues cvUseStats = new ContentValues();
		cvUseStats.put(KEY_REACH_HIGH, reachHigh);
		cvUseStats.put(KEY_REACH_LOW, reachLow);

		clubdb.update(DATABASE_TABLE, cvUseStats, KEY_ID + "=" + clubID, null);

	}

	public void deleteClub(String clubID) {
		// TODO Auto-generated method stub

		clubdb.delete(DATABASE_TABLE, KEY_ID + "=" + clubID, null);
	}

	public void resetstats(String clubID) {
		// TODO Auto-generated method stub
		ContentValues cvResetStats = new ContentValues();

		cvResetStats.put(KEY_STATS_ENTRIES, "0");
		cvResetStats.put(KEY_STATS_HIGH, "0");
		cvResetStats.put(KEY_STATS_LOW, "0");
		cvResetStats.put(KEY_STATS_AVG, "0");
		cvResetStats.put(KEY_ACC_ENTRIES, "0");
		cvResetStats.put(KEY_ACC_HIGH, "0");
		cvResetStats.put(KEY_ACC_LOW, "0");
		cvResetStats.put(KEY_ACC_AVG, "0");
		cvResetStats.put(KEY_ANG_MAX, "0");
		cvResetStats.put(KEY_ANG_MIN, "0");
		cvResetStats.put(KEY_ANG_AVG, "0");

		clubdb.update(DATABASE_TABLE, cvResetStats, KEY_ID + "=" + clubID, null);

	}

	public void setUse(String clubID, boolean use) {
		// TODO Auto-generated method stub
		ContentValues cvUse = new ContentValues();
		if (use) {
			cvUse.put(KEY_USE, "TRUE");
		} else {
			cvUse.put(KEY_USE, "FALSE");
		}

		clubdb.update(DATABASE_TABLE, cvUse, KEY_ID + "=" + clubID, null);

	}

	public void EditClub(String clubID, String club, String reachHigh,
			String reachLow) {
		// TODO Auto-generated method stub
		ContentValues cvUse = new ContentValues();
		cvUse.put(KEY_NAME, club);
		cvUse.put(KEY_REACH_HIGH, reachHigh);
		cvUse.put(KEY_REACH_LOW, reachLow);

		clubdb.update(DATABASE_TABLE, cvUse, KEY_ID + "=" + clubID, null);

	}

	public void putDistEntry(String clubID, String clubDistance) {
		// TODO Auto-generated method stub
		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_STATS,
				KEY_ID + "=" + clubID, null, null, null, null);
		clubCursor.moveToFirst();

		// KEY_STATS_ENTRIES, KEY_STATS_HIGH, KEY_STATS_LOW, KEY_STATS_AVG

		int iEntries = clubCursor.getColumnIndex(KEY_STATS_ENTRIES);
		int iHigh = clubCursor.getColumnIndex(KEY_STATS_HIGH);
		int iLow = clubCursor.getColumnIndex(KEY_STATS_LOW);
		int iAvg = clubCursor.getColumnIndex(KEY_STATS_AVG);

		int Distance = Integer.parseInt(clubDistance);
		int Entries = Integer.parseInt(clubCursor.getString(iEntries));
		int High = Integer.parseInt(clubCursor.getString(iHigh));
		int Low = Integer.parseInt(clubCursor.getString(iLow));
		int Avg = Integer.parseInt(clubCursor.getString(iAvg));

		clubCursor.deactivate();

		if (Distance > High) {
			High = Distance;
		}

		if (Distance < Low | Low == 0) {
			Low = Distance;
		}

		int Total = Avg * Entries;
		Entries++;
		int newAvg = (int) ((Total + Distance) / Entries);

		ContentValues cvDist = new ContentValues();
		cvDist.put(KEY_STATS_ENTRIES, String.valueOf(Entries));
		cvDist.put(KEY_STATS_HIGH, String.valueOf(High));
		cvDist.put(KEY_STATS_LOW, String.valueOf(Low));
		cvDist.put(KEY_STATS_AVG, String.valueOf(newAvg));

		clubdb.update(DATABASE_TABLE, cvDist, KEY_ID + "=" + clubID, null);
	}

	public void putAccEntry(String clubID, String clubAccuracy, String clubAngle) {
		// TODO Auto-generated method stub
		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_ACC,
				KEY_ID + "=" + clubID, null, null, null, null);
		clubCursor.moveToFirst();

		// KEY_ACC_ENTRIES, KEY_ACC_HIGH, KEY_ACC_LOW, KEY_ACC_AVG

		int iEntries = clubCursor.getColumnIndex(KEY_ACC_ENTRIES);
		int iHigh = clubCursor.getColumnIndex(KEY_ACC_HIGH);
		int iLow = clubCursor.getColumnIndex(KEY_ACC_LOW);
		int iAvg = clubCursor.getColumnIndex(KEY_ACC_AVG);
		
		int iMax = clubCursor.getColumnIndex(KEY_ANG_MAX);
		int iMin = clubCursor.getColumnIndex(KEY_ANG_MIN);
		int iAngAvg = clubCursor.getColumnIndex(KEY_ANG_AVG);

		int Accuracy = Integer.parseInt(clubAccuracy);
		int Angle = Integer.parseInt(clubAngle);
		
		int Entries = Integer.parseInt(clubCursor.getString(iEntries));
		int High = Integer.parseInt(clubCursor.getString(iHigh));
		int Low = Integer.parseInt(clubCursor.getString(iLow));
		int Avg = Integer.parseInt(clubCursor.getString(iAvg));
		int Max = Integer.parseInt(clubCursor.getString(iMax));
		int Min = Integer.parseInt(clubCursor.getString(iMin));
		int AngAvg = Integer.parseInt(clubCursor.getString(iAngAvg));

		clubCursor.deactivate();

		if (Accuracy > High) {
			High = Accuracy;
		}

		if (Accuracy < Low | Low == 0) {
			Low = Accuracy;
		}
		
		if (Angle > Max) {
			Max = Angle;
		}

		if (Angle < Min | Min == 0) {
			Min = Angle;
		}

		int Total = Avg * Entries;
		int AngTotal = AngAvg * Entries;
		
		Entries++;
		int newAvg = (int) ((Total + Accuracy) / Entries);
		int newAngAvg = (int) ((AngTotal + Angle) / Entries);

		ContentValues cvAcc = new ContentValues();
		cvAcc.put(KEY_ACC_ENTRIES, String.valueOf(Entries));
		cvAcc.put(KEY_ACC_HIGH, String.valueOf(High));
		cvAcc.put(KEY_ACC_LOW, String.valueOf(Low));
		cvAcc.put(KEY_ACC_AVG, String.valueOf(newAvg));
		cvAcc.put(KEY_ANG_MAX, String.valueOf(Max));
		cvAcc.put(KEY_ANG_MIN, String.valueOf(Min));
		cvAcc.put(KEY_ANG_AVG, String.valueOf(newAngAvg));

		clubdb.update(DATABASE_TABLE, cvAcc, KEY_ID + "=" + clubID, null);
	}

	public String[] getHighs() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_HIGHS,
				null, null, null, null, KEY_REACH_HIGH + " DESC");
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iHigh = clubCursor.getColumnIndex(KEY_REACH_HIGH);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iHigh);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}

	}

	public String[] getLows() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_LOWS,
				null, null, null, null, KEY_REACH_HIGH + " DESC");
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iLow = clubCursor.getColumnIndex(KEY_REACH_LOW);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iLow);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}

	}

	public String[] getClubUse() {
		// TODO Auto-generated method stub

		Cursor clubCursor = clubdb.query(DATABASE_TABLE, TABLE_COLUMNS_USE,
				null, null, null, null, KEY_REACH_HIGH + " DESC");
		int count = clubCursor.getCount();
		if (count == 0) {
			clubCursor.deactivate();
			return null;
		} else {
			String[] result = new String[count];

			int i = 0;
			int iUse = clubCursor.getColumnIndex(KEY_USE);

			for (clubCursor.moveToFirst(); !clubCursor.isAfterLast(); clubCursor
					.moveToNext()) {
				result[i] = clubCursor.getString(iUse);
				i++;

			}
			clubCursor.deactivate();
			return result;
		}

	}

}
