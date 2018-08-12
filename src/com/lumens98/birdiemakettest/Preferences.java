package com.lumens98.birdiemakettest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class Preferences extends ActionBarActivity {

	public static String filename = "mySettings";

	EditText etFlagHeight;
	String FlagHeight;
	SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preferences);
		InitScreenVariables();
		getValues();
		setValues();

	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.prefmenu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_save:
			LoadStrings();
			if (Validate()) {
				saveValues();
				finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void InitScreenVariables() {

		etFlagHeight = (EditText) findViewById(R.id.etFlagHeight);

	}

	public void saveValues() {

		preferences = getSharedPreferences(filename, 0);
		SharedPreferences.Editor prefeditor = preferences.edit();
		prefeditor.putString("FlagHeight", FlagHeight);
		prefeditor.commit();
		
	}

	public void LoadStrings() {

		if(checkEmpty(etFlagHeight)){
			FlagHeight = etFlagHeight.getText().toString();
		}else{
			FlagHeight = null;
		}
		
		
	}

	public void getValues() {

		preferences = getSharedPreferences(filename, 0);
		FlagHeight = preferences.getString("FlagHeight", "7");

	}

	public void setValues() {

		etFlagHeight.setText(FlagHeight);

	}

	public boolean Validate() {
		if(FlagHeight != null){
			if(Integer.parseInt(FlagHeight) != 0){
				return true;
			}else{
				Toast.makeText(this, "Pin height cannot be 0", Toast.LENGTH_SHORT).show();
				return false;
			}
		}else{
			Toast.makeText(this, "Pin height cannot be empty", Toast.LENGTH_SHORT).show();
			return false;
		}
		


	}

	public boolean checkEmpty(EditText etText) {
		if (etText.getText().toString().length() > 0)
			return true;
		else
			return false;
	}



}
