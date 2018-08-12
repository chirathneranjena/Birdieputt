package com.lumens98.birdiemakettest;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class EditClub extends ActionBarActivity {

	EditText etClub, etReachLow, etReachHigh;
	String ClubID;
	String ClubData[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newclub);
		
		etClub = (EditText) findViewById(R.id.etNewClub);
		etReachLow = (EditText) findViewById(R.id.etNewLow);
		etReachHigh = (EditText) findViewById(R.id.etNewHigh);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ClubID = extras.getString("CLUB_ID");
			LoadValuesfromDatabase();
		}
	}

	private void LoadValuesfromDatabase() {
		// TODO Auto-generated method stub
		ClubDatabase Clubs = new ClubDatabase(EditClub.this);
		Clubs.open();
		ClubData = Clubs.getClubByID(ClubID);
		Clubs.close();
		
		etClub.setText(ClubData[1]);
		etReachHigh.setText(ClubData[2]);
		etReachLow.setText(ClubData[3]);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.addclubmenu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.menu_save:
			// Do something
			if (ValidateInputData()) {
				SaveData();
				finish();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private boolean ValidateInputData() {
		// TODO Auto-generated method stub
		if (checkEmpty(etClub) & checkEmpty(etReachLow)
				& checkEmpty(etReachHigh)) {

			String NewHigh = etReachHigh.getText().toString();
			String NewLow = etReachLow.getText().toString();

			int intHigh = Integer.parseInt(NewHigh);
			int intLow = Integer.parseInt(NewLow);

			if (intLow < intHigh) {
				return true;
			} else {
				Toast.makeText(this, "Low > High!", Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(this, "Incomplete Entry", Toast.LENGTH_SHORT).show();
			return false;
		}

	}

	private void SaveData() {
		// TODO Auto-generated method stub
		String Club = etClub.getText().toString();
		String ReachHigh = etReachHigh.getText().toString();
		String ReachLow = etReachLow.getText().toString();

		ClubDatabase Clubs = new ClubDatabase(EditClub.this);
		Clubs.open();
		Clubs.EditClub(ClubID, Club, ReachHigh, ReachLow);
		Clubs.close();

	}

	public boolean checkEmpty(EditText etText) {
		if (etText.getText().toString().length() > 0)
			return true;
		else
			return false;
	}
}
