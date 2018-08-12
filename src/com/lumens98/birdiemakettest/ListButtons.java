package com.lumens98.birdiemakettest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ListButtons extends Activity {

	private Context context;

	public ListButtons(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public void EditClub(String clubID) {
		try {

			Class<?> editclubclass = Class
					.forName("com.lumens98.birdiemakettest.EditClub");
			Intent editclubintent = new Intent(context, editclubclass);
			editclubintent.putExtra("CLUB_ID", clubID);
			context.startActivity(editclubintent);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "Class not Found", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void ClubDetails(String clubID) {
		try {

			Class<?> clubdetailsclass = Class
					.forName("com.lumens98.birdiemakettest.ClubDetails");
			Intent clubdetailsintent = new Intent(context, clubdetailsclass);
			clubdetailsintent.putExtra("CLUB_ID", clubID);
			context.startActivity(clubdetailsintent);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "Class not Found", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
