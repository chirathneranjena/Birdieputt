package com.lumens98.birdiemakettest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lumens98.birdiemakettest.R;

public class ClubDetails extends ActionBarActivity {

	public static final int i_ID = 0;
	public static final int i_NAME = 1;
	public static final int i_REACH_HIGH = 2;
	public static final int i_REACH_LOW = 3;

	public static final int i_USE = 4;

	public static final int i_STATS_ENTRIES = 5;
	public static final int i_STATS_HIGH = 6;
	public static final int i_STATS_LOW = 7;
	public static final int i_STATS_AVG = 8;

	public static final int i_ACC_ENTRIES = 9;
	public static final int i_ACC_HIGH = 10;
	public static final int i_ACC_LOW = 11;
	public static final int i_ACC_AVG = 12;

	public static final int i_ANG_MAX = 13;
	public static final int i_ANG_MIN = 14;
	public static final int i_ANG_AVG = 15;

	boolean resumed = false;
	boolean nodata = false;
	String ClubData[];
	String StatDist[];
	String StatAng[];
	String ClubID;
	TextView tvClubName;
	TextView tvAvgDistance, tvAvgAngle, tvLongest, tvShortest;
	TextView tvMinAngle, tvMaxAngle, tvMinMiss, tvMaxMiss, tvAvgError,
			tvDistEntries, tvAccEntries;

	FrameLayout StatsOverlay;
	DrawOnTop ClubStatsGraphic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clubdetails);

		InitiateScreenVariables();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ClubID = extras.getString("CLUB_ID");
			LoadValuesfromDatabase();

		}

		ClubStatsGraphic = new DrawOnTop(this);
		StatsOverlay = (FrameLayout) findViewById(R.id.StatsGraphic);
		StatsOverlay.addView(ClubStatsGraphic);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!resumed) {
			resumed = true;
		} else {
			LoadValuesfromDatabase();
		}
	}

	private void LoadValuesfromDatabase() {
		// TODO Auto-generated method stub
		ClubDatabase Clubs = new ClubDatabase(ClubDetails.this);
		Clubs.open();
		ClubData = Clubs.getClubByID(ClubID);
		Clubs.close();

		SetupScreenVariables();

		StatDatabase Stats = new StatDatabase(ClubDetails.this);
		Stats.open(ClubID);
		StatDist = Stats.getDist();
		StatAng = Stats.getAng();
		Stats.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater(); // Start Menu
		menuInflater.inflate(R.menu.clubdetailsmenu, menu);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			break;

		case R.id.menu_use:
			UseStatsData();
			break;

		case R.id.menu_reset:
			ResetStats();
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void ResetStats() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(ClubDetails.this);
		builder.setTitle("Warning!");
		builder.setMessage("Are you sure you want to delete all recorded stats for this club?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				ClubDatabase Clubs = new ClubDatabase(ClubDetails.this);
				Clubs.open();
				Clubs.resetstats(ClubID);
				Clubs.close();

				StatDatabase Stats = new StatDatabase(ClubDetails.this);
				Stats.open(ClubID);
				Stats.deleteAll();
				Stats.close();

				LoadValuesfromDatabase();
				StatsOverlay.removeAllViews();
				StatsOverlay.addView(ClubStatsGraphic);
				dialog.cancel();

			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	private void UseStatsData() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(ClubDetails.this);
		builder.setTitle("Wait!");
		builder.setMessage("Use statistics data for highest and lowest reach values for this club?");
		builder.setPositiveButton("Go for it",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String ReachHigh = ClubData[6];
						String ReachLow = ClubData[7];
						ClubDatabase Clubs = new ClubDatabase(ClubDetails.this);
						Clubs.open();
						Clubs.UseStats(ClubID, ReachHigh, ReachLow);
						Clubs.close();
						LoadValuesfromDatabase();
						StatsOverlay.removeAllViews();
						StatsOverlay.addView(ClubStatsGraphic);
						dialog.cancel();
					}
				});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	private void InitiateScreenVariables() {
		// TODO Auto-generated method stub
		// TextView tvClubName, tvLongest, tvShortest;
		// TextView tvAvgDistance, tvAvgError, tvEntries, tvAngle;

		tvClubName = (TextView) findViewById(R.id.tvClubName);

		tvAvgDistance = (TextView) findViewById(R.id.tvAvgDist);
		tvAvgAngle = (TextView) findViewById(R.id.tvAvgAngle);
		tvLongest = (TextView) findViewById(R.id.tvLong);
		tvShortest = (TextView) findViewById(R.id.tvShort);

		tvMinAngle = (TextView) findViewById(R.id.tvminAngle);
		tvMaxAngle = (TextView) findViewById(R.id.tvmaxAngle);
		tvMinMiss = (TextView) findViewById(R.id.tvminMiss);
		tvMaxMiss = (TextView) findViewById(R.id.tvmaxMiss);
		tvAvgError = (TextView) findViewById(R.id.tvAvgError);
		tvDistEntries = (TextView) findViewById(R.id.tvDistEntries);
		tvAccEntries = (TextView) findViewById(R.id.tvAccEntries);

		// TextView tvAvgDistance, tvAvgAngle, tvLongest, tvShortest;
		// TextView tvMinAngle, tvMaxAngle, tvMinMiss, tvMaxMiss, tvAvgError,
		// tvEntries;

		// tvClubName, tvAvgDist, tvAvgAngle, tvLong, tvShort, tvminAngle,
		// tvmaxAngle, tvminMiss, tvmaxMiss, tvAvgError, tvEntries

	}

	private void SetupScreenVariables() {
		// TODO Auto-generated method stub
		tvClubName.setText(" " + ClubData[i_NAME]);

		tvAvgDistance.setText("Average club distance: " + ClubData[i_STATS_AVG]
				+ " yds");

		int angle = Integer.parseInt(ClubData[i_ANG_AVG]);
		if (angle < 0) {
			tvAvgAngle.setText("Average club angle: " + String.valueOf(-angle)
					+ " deg to the left");
		} else {
			if (angle > 0) {
				tvAvgAngle.setText("Average shot angle: " + ClubData[i_ANG_AVG]
						+ " deg to the right");
			} else {
				tvAvgAngle.setText("Average shot angle: 0 deg (Straight)");
			}
		}

		tvLongest.setText("Longest shot: " + ClubData[i_STATS_HIGH] + " yds");
		tvShortest.setText("Shortest: " + ClubData[i_STATS_LOW] + " yds");

		int maxangle = Integer.parseInt(ClubData[i_ANG_MAX]);
		int minangle = Integer.parseInt(ClubData[i_ANG_MIN]);

		if (maxangle > 0) {
			tvMaxAngle.setText("Maximum deviation to the right: "
					+ ClubData[i_ANG_MAX] + " deg");
		} else {
			tvMaxAngle.setText("Maximum deviation to the right: 0 deg");
		}

		if (minangle < 0) {
			tvMinAngle.setText("Maximum deviation to the left: "
					+ String.valueOf(-minangle) + " deg");
		} else {
			tvMinAngle.setText("Maximum deviation to the left: 0 deg");
		}

		tvMinMiss.setText("Closest shot to target: " + ClubData[i_ACC_LOW]
				+ " yds");
		tvMaxMiss.setText("Furthest shot from target: " + ClubData[i_ACC_HIGH]
				+ " yds");

		tvAvgError.setText("On average the club misses target by: "
				+ ClubData[i_ACC_AVG] + " yds");
		tvDistEntries.setText("Distance entries: " + ClubData[i_STATS_ENTRIES]);
		tvAccEntries.setText("Distance and accuracy entries: "
				+ ClubData[i_ACC_ENTRIES]);

		// TextView tvAvgDistance, tvAvgAngle, tvLongest, tvShortest;
		// TextView tvMinAngle, tvMaxAngle, tvMinMiss, tvMaxMiss, tvAvgError,
		// tvEntries;

	}

	class DrawOnTop extends View {
		public DrawOnTop(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub

			float HeightScope;
			float cx, cy;
			int canvaswidth = canvas.getWidth();
			int canvasheight = canvas.getHeight();

			int ReachHigh = Integer.parseInt(ClubData[i_REACH_HIGH]);
			int ReachLow = Integer.parseInt(ClubData[i_REACH_LOW]);
			int ShotHigh = Integer.parseInt(ClubData[i_STATS_HIGH]);

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			// paint.setColor(Color.rgb(51, 181, 229));
			paint.setColor(Color.rgb(255, 68, 68));
			paint.setStrokeWidth(5);

			float circlex = canvaswidth * 5 / 10;
			float circley = canvasheight * 6 / 10;
			float circleradius = 10;
			float TopStart = 30;
			float MaxFOV = 30;
			float ReachArcAng = 20;
			float drawheight = circley - TopStart;

			canvas.drawCircle(circlex, circley, circleradius, paint);

			if (ShotHigh > ReachHigh) {
				HeightScope = ShotHigh;
			} else {
				HeightScope = ReachHigh;
			}

			float HighRadius = drawheight * ReachHigh / HeightScope;
			float LowRadius = HighRadius * ReachLow / ReachHigh;

			// float FOV = (float) Math.toDegrees(Math.asin(circlex /
			// drawheight));
			// if(FOV>MaxFOV){
			float FOV = MaxFOV;
			// }

			RectF Highrecf = new RectF(circlex - HighRadius, circley
					- HighRadius, circlex + HighRadius, circley + HighRadius);
			RectF Lowrecf = new RectF(circlex - LowRadius, circley - LowRadius,
					circlex + LowRadius, circley + LowRadius);

			float x1 = (float) (circlex + drawheight
					* Math.cos(Math.toRadians(270 - FOV)));
			float y1 = (float) (circley + drawheight
					* Math.sin(Math.toRadians(270 - FOV)));

			float x2 = (float) (circlex + drawheight
					* Math.cos(Math.toRadians(270 + FOV)));
			float y2 = (float) (circley + drawheight
					* Math.sin(Math.toRadians(270 + FOV)));

			paint.setColor(Color.rgb(110, 110, 110));
			canvas.drawLine(circlex - circleradius, circley - circleradius, x1,
					y1, paint);
			canvas.drawLine(circlex + circleradius, circley - circleradius, x2,
					y2, paint);

			// paint.setColor(Color.rgb(150, 150, 150));
			paint.setColor(Color.rgb(51, 181, 229));
			paint.setStrokeWidth(2);

			canvas.drawArc(Highrecf, 270 - ReachArcAng, 2 * ReachArcAng, false,
					paint);
			canvas.drawArc(Lowrecf, 270 - ReachArcAng, 2 * ReachArcAng, false,
					paint);

			paint.setColor(Color.rgb(200, 200, 200));
			canvas.drawLine(circlex, circley - circleradius - 5, circlex,
					TopStart, paint);

			float markerloc;
			// paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(15);
			for (int marker = 50; marker <= HeightScope; marker += 50) {
				markerloc = circley - (drawheight * marker / HeightScope);
				paint.setColor(Color.rgb(110, 110, 110));
				canvas.drawLine(circlex - 3, markerloc, circlex + 3, markerloc,
						paint);
				paint.setColor(Color.rgb(255, 136, 0));
				canvas.drawText(String.valueOf(marker), circlex + 5,
						markerloc + 3, paint);
			}

			paint.setColor(Color.rgb(255, 68, 68));
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.FILL);
			if (StatDist != null) {
				for (int i = 0; i < StatDist.length; i++) {
					float distance = Integer.parseInt(StatDist[i]) * HighRadius
							/ ReachHigh;
					int angle = Integer.parseInt(StatAng[i]);

					cx = (float) (circlex + distance
							* Math.cos(Math.toRadians(270 + angle)));
					cy = (float) (circley + distance
							* Math.sin(Math.toRadians(270 + angle)));

					canvas.drawCircle(cx, cy, 5, paint);

				}
			}

			paint.setColor(Color.rgb(255, 136, 0));
			paint.setStrokeWidth(6);
			paint.setTextSize(20);

			paint.setStyle(Paint.Style.FILL);

			cx = (float) (circlex + HighRadius
					* Math.cos(Math.toRadians(270 + ReachArcAng + 2)));
			cy = (float) (circley + HighRadius
					* Math.sin(Math.toRadians(270 + ReachArcAng + 2)));

			canvas.drawText(ClubData[2] + " yds", cx - 25, cy + 15, paint);

			cx = (float) (circlex + LowRadius
					* Math.cos(Math.toRadians(270 + ReachArcAng + 2)));
			cy = (float) (circley + LowRadius
					* Math.sin(Math.toRadians(270 + ReachArcAng + 2)));

			canvas.drawText(ClubData[3] + " yds", cx - 25, cy + 15, paint);

			// paint.setTextSize(15);
			// paint.setStyle(Paint.Style.FILL_AND_STROKE);
			cx = (float) (circlex + drawheight / 2
					* Math.cos(Math.toRadians(270 + FOV + 4)));
			cy = (float) (circley + drawheight / 2
					* Math.sin(Math.toRadians(270 + FOV + 4)));

			canvas.drawText("+" + String.valueOf((int) FOV) + " deg", cx + 1,
					cy, paint);

			cx = (float) (circlex + drawheight / 2
					* Math.cos(Math.toRadians(270 - FOV - 4)));
			cy = (float) (circley + drawheight / 2
					* Math.sin(Math.toRadians(270 - FOV - 4)));

			canvas.drawText("-" + String.valueOf((int) FOV) + " deg", cx - 65,
					cy, paint);

			float avgdistance = Integer.parseInt(ClubData[i_STATS_AVG])
					* HighRadius / ReachHigh;
			int avgangle = Integer.parseInt(ClubData[i_ANG_AVG]);

			if (avgdistance != 0) {
				cx = (float) (circlex + avgdistance
						* Math.cos(Math.toRadians(270 + avgangle)));
				cy = (float) (circley + avgdistance
						* Math.sin(Math.toRadians(270 + avgangle)));
				paint.setColor(Color.rgb(153, 204, 0));
				canvas.drawCircle(cx, cy, 7, paint);
				paint.setColor(Color.rgb(255, 136, 0));
				canvas.drawText("AVG", cx+8, cy+8, paint);

			}
			super.onDraw(canvas);

		}
	}

}
