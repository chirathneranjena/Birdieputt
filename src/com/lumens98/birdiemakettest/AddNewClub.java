package com.lumens98.birdiemakettest;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class AddNewClub extends ActionBarActivity {

	EditText etNewClub, etNewLow, etNewHigh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.newclub);
		
		etNewClub = (EditText) findViewById(R.id.etNewClub);
		etNewLow = (EditText) findViewById(R.id.etNewLow);
		etNewHigh = (EditText) findViewById(R.id.etNewHigh);
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
		if (checkEmpty(etNewClub) & checkEmpty(etNewLow)
				& checkEmpty(etNewHigh)) {

			String NewHigh = etNewHigh.getText().toString();
			String NewLow = etNewLow.getText().toString();

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
		String NewClub = etNewClub.getText().toString();
		String NewHigh = etNewHigh.getText().toString();
		String NewLow = etNewLow.getText().toString();

		ClubDatabase Clubs = new ClubDatabase(AddNewClub.this);
		Clubs.open();
		Clubs.createnewclub(NewClub, NewHigh, NewLow);
		Clubs.close();

	}

	public boolean checkEmpty(EditText etText) {
		if (etText.getText().toString().length() > 0)
			return true;
		else
			return false;
	}

}
