package com.lumens98.birdiemakettest;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class ClubList extends ActionBarListActivity {

	SimpleAdapter adapter;
	
	SharedPreferences preferences; // Preferences variables
	public static String filename = "mySettings";

	String ClubIDs[];
	String ClubNames[];
	String ClubUse[];

	String ClubDistance, ClubAccuracy, Accuracy_inc, Angle;

	boolean resumed = false;

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.clublistlayout);

		Bundle extras = getIntent().getExtras();
		ClubDistance = extras.getString("DISTANCE");
		Accuracy_inc = extras.getString("ACC_INC");
		ClubAccuracy = extras.getString("ACCURACY");
		Angle = extras.getString("ANGLE");

		adapter = new SimpleAdapter(this, list, R.layout.clubpickrow,
				new String[] { "club" }, new int[] { R.id.clubname });

		ClubDatabase Clubs = new ClubDatabase(ClubList.this);
		Clubs.open();
		ClubIDs = Clubs.getClubIDs();
		ClubNames = Clubs.getClubNames();
		ClubUse = Clubs.getClubUse();
		Clubs.close();

		list.clear();
		if (ClubNames != null) {

			for (int i = 0; i < ClubNames.length; i++) {
				//Log.d(getLocalClassName(), ClubUse[i]);
				if (ClubUse[i].equals("TRUE")) {
					populateList(ClubNames[i], ClubUse[i]);
				}

			}

			setListAdapter(adapter);
		}

	}

	private void populateList(String clubName, String clubuse) {

		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("club", clubName);
		list.add(temp);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		String chosenclubID = ClubIDs[position];
		if (Accuracy_inc.equals("FALSE")) {
			ClubDatabase Clubs = new ClubDatabase(ClubList.this);
			Clubs.open();
			Clubs.putDistEntry(chosenclubID, ClubDistance);
			Clubs.close();

		} else {
			ClubDatabase Clubs = new ClubDatabase(ClubList.this);
			Clubs.open();
			Clubs.putDistEntry(chosenclubID, ClubDistance);
			Clubs.putAccEntry(chosenclubID, ClubAccuracy, Angle);
			Clubs.close();
			
			StatDatabase Stats = new StatDatabase(ClubList.this);
			Stats.open(chosenclubID);
			Stats.putEntry(ClubDistance, Accuracy_inc, ClubAccuracy, Angle);
			Stats.close();

		}
		
		preferences = getSharedPreferences(filename, 0);
		String st_bought = preferences.getString("ST_BOUGHT", "FALSE");
		if(st_bought.equals("TRUE")){
			Toast.makeText(this, "Shot Recorded!", Toast.LENGTH_SHORT).show();
			finish();
		}else{
			String tr_shots = preferences.getString("TR_SHOTS", "0");
			int shots = Integer.parseInt(tr_shots);
			shots++;
			SharedPreferences.Editor prefeditor = preferences.edit();
			prefeditor.putString("TR_SHOTS", String.valueOf(shots));
			prefeditor.commit();
			Toast.makeText(this, "Shot Recorded! " + String.valueOf(3-shots) + " Free Remaining", Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}

}
