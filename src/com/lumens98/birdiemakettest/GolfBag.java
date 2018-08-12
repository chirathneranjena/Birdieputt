package com.lumens98.birdiemakettest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class GolfBag extends ActionBarListActivity {

	private static EfficientAdapter adap;

	static String ClubIDs[];
	static String ClubNames[];
	static String ClubDetails[];
	static String ClubUse[];

	ClubDatabase Clubs;
	boolean resumed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.clublistlayout);

		Clubs = new ClubDatabase(GolfBag.this);
		Clubs.open();
		ClubIDs = Clubs.getClubIDs();
		ClubNames = Clubs.getClubNames();
		ClubDetails = Clubs.getClubDetails();
		ClubUse = Clubs.getClubUse();
		Clubs.close();

		adap = new EfficientAdapter(this);
		setListAdapter(adap);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!resumed) {
			resumed = true;
		} else {
			Clubs = new ClubDatabase(GolfBag.this);
			Clubs.open();
			ClubIDs = Clubs.getClubIDs();
			ClubNames = Clubs.getClubNames();
			ClubDetails = Clubs.getClubDetails();
			ClubUse = Clubs.getClubUse();
			Clubs.close();

			adap.notifyDataSetChanged();

		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater(); // Start Menu
		menuInflater.inflate(R.menu.golfbagmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.menu_add:
			try {
				Class<?> addnewclubclass = Class
						.forName("com.lumens98.birdiemakettest.AddNewClub");
				Intent addnewclubintent = new Intent(this, addnewclubclass);
				startActivity(addnewclubintent);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class EfficientAdapter extends BaseAdapter implements
			Filterable {
		private LayoutInflater mInflater;
		private Context context;

		public EfficientAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			convertView = mInflater.inflate(R.layout.clublistrow, null);
			convertView.setClickable(true);
			convertView.setFocusable(true);
			convertView
					.setBackgroundResource(android.R.drawable.menuitem_background);
			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();

			holder.club = (TextView) convertView.findViewById(R.id.clubtext);
			holder.clubdetails = (TextView) convertView
					.findViewById(R.id.detailstext);
			holder.cbuse = (CheckBox) convertView.findViewById(R.id.cbuseclub);
			holder.bedit = (Button) convertView.findViewById(R.id.beditclub);
			holder.btrash = (Button) convertView.findViewById(R.id.btrashclub);

			if (ClubUse[position].equals("TRUE")) {
				holder.cbuse.setChecked(true);
			} else {
				holder.cbuse.setChecked(false);
			}

			convertView.setOnClickListener(new OnClickListener() {
				private int pos = position;

				public void onClick(View v) {
					ListButtons lb = new ListButtons(context);
					lb.ClubDetails(ClubIDs[pos]);
				}
			});
			
			holder.cbuse.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				private int pos = position;

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					ClubDatabase Clubs = new ClubDatabase(context);
					Clubs.open();
					Clubs.setUse(ClubIDs[pos], isChecked);

					ClubIDs = Clubs.getClubIDs();
					ClubNames = Clubs.getClubNames();
					ClubDetails = Clubs.getClubDetails();
					ClubUse = Clubs.getClubUse();
					Clubs.close();

					adap.notifyDataSetChanged();

					
				}
				
			});

			holder.bedit.setOnClickListener(new OnClickListener() {
				private int pos = position;

				public void onClick(View v) {
					ListButtons lb = new ListButtons(context);
					lb.EditClub(ClubIDs[pos]);

				}
			});
			holder.btrash.setOnClickListener(new OnClickListener() {
				private int pos = position;

				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("Deleting Club...");
					builder.setMessage("Are you sure you want to delete this club?"); 
					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									
									ClubDatabase Clubs = new ClubDatabase(context);
									Clubs.open();
									Clubs.deleteClub(ClubIDs[pos]);
									Clubs.close();
									dialog.cancel();
									
									Clubs = new ClubDatabase(context);
									Clubs.open();
									ClubIDs = Clubs.getClubIDs();
									ClubNames = Clubs.getClubNames();
									ClubDetails = Clubs.getClubDetails();
									Clubs.close();

									adap.notifyDataSetChanged();
									
								}
							});
					builder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

					AlertDialog alert = builder.create();
					alert.show();
				}
			});

			convertView.setTag(holder);

			holder.club.setText(ClubNames[position]);
			holder.clubdetails.setText(ClubDetails[position]);

			return convertView;
		}

		static class ViewHolder {
			TextView club;
			TextView clubdetails;
			CheckBox cbuse;
			Button bedit;
			Button btrash;

		}

		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return ClubIDs.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	}

}
