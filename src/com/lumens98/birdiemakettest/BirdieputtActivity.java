package com.lumens98.birdiemakettest;

import java.util.ArrayList;
import java.util.List;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.lumens98.birdiemakettest.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BirdieputtActivity extends ActionBarMapActivity implements
		LocationListener {
	/** Called when the activity is first created. */

	// Setup Variables

	MapView map; // Google maps variables
	MapController mapcontroller;

	Button bClear, bClub, bClubTracker; // View variables
	TextView tvDistance, tvAccuracy;

	LocationManager locationmanager; // GPS variables
	MyLocationOverlay myLocationOverlay;

	List<Overlay> overlaylist; // Overlays on the map
	Drawable pointblue, pointyellow, pointred;
	Drawable btracknormal, btrackred;

	// List of geo points that have been tapped. First element is GPS location
	List<GeoPoint> TappedPoints = new ArrayList<GeoPoint>();
	List<GeoPoint> LongTappedPoints = new ArrayList<GeoPoint>();
	List<Integer> LongTapOverlayLocs = new ArrayList<Integer>();

	double TotalDistance = 0; // Keeps track of the total distance between
								// tapped points

	SharedPreferences preferences; // Preferences variables
	public static String filename = "mySettings";

	int Clubcolor = -1; // Variable to generate various colors for different
						// clubs
	int overlayitems = 0;
	int overlaymark = 3;

	double accuracy;
	double MeterstoYards = 1.0936133;
	int initialZoomlevel = 18;

	GoogleAnalyticsTracker birdietracker;

	boolean locationmarked = false;
	boolean targetmarked = false;
	GeoPoint startmark, targetmark;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		birdietracker = GoogleAnalyticsTracker.getInstance();

		// Initialize on screen stuff
		map = (MapView) findViewById(R.id.mapview);
		bClear = (Button) findViewById(R.id.bClear);
		bClub = (Button) findViewById(R.id.bClub);
		bClubTracker = (Button) findViewById(R.id.bTrack);
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);

		tvDistance.setText(" 0 yds");
		tvAccuracy.setText(" +/- 0 yds");

		// Setup Google maps and map controller
		map.setBuiltInZoomControls(true);
		map.setSatellite(true);
		map.setTraffic(false);
		mapcontroller = map.getController();

		// Setup GPS location service
		myLocationOverlay = new MyLocationOverlay(this, map);
		// myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				map.getController()
						.animateTo(myLocationOverlay.getMyLocation());
				map.getController().setZoom(initialZoomlevel);
			}
		});

		// Setup separate GPS service for our own
		locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Add GPS location to map
		map.getOverlays().add(myLocationOverlay);

		// Start a new Google Analytics Session
		// birdietracker.startNewSession("UA-32105445-1", this);
		// birdietracker.setDebug(true);
		// birdietracker.trackPageView("/MapActivity");
		// birdietracker.dispatch();

		// Add touched points to map
		Touched touchy = new Touched();
		overlaylist = map.getOverlays();
		overlaylist.add(touchy);
		overlayitems = 1;
		pointblue = getResources().getDrawable(R.drawable.ic_location_blue);
		pointyellow = getResources().getDrawable(R.drawable.ic_location_yellow);
		pointred = getResources().getDrawable(R.drawable.ic_location_red);

		btrackred = getResources().getDrawable(R.drawable.b_track_red);
		btracknormal = getResources().getDrawable(R.drawable.b_track);

		RunForFirstTime();

		// Setup listener for the clear button
		bClear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClearScreen();
			}
		});

		bClub.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				SelectClub(); // Goto select club method below
			}
		});

		bClubTracker.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				preferences = getSharedPreferences(filename, 0);
				String st_bought = preferences.getString("ST_BOUGHT", "FALSE");
				if (st_bought.equals("TRUE")) {
					ClubTracker();
				} else {
					String tr_shots = preferences.getString("TR_SHOTS", "0");
					if (Integer.parseInt(tr_shots) < 3) {
						ClubTracker();
					} else {
						try {
							Class<?> purchaseclass = Class
									.forName("com.lumens98.birdiemakettest.PurchaseActivity");
							Intent purchaseintent = new Intent(
									BirdieputtActivity.this, purchaseclass);
							startActivity(purchaseintent);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(BirdieputtActivity.this,
									"Class not Found", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}

			}

		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// birdietracker.trackPageView("/OnDestroy");
		// birdietracker.dispatch();
		// birdietracker.stopSession();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater(); // Start Menu
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			break;

		case R.id.menu_map: // If cross hairs then center the map on to the GPS
							// location

			myLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					map.getController().animateTo(
							myLocationOverlay.getMyLocation());
					map.getController().setZoom(initialZoomlevel);
				}
			});
			break;

		case R.id.menu_camera: // If camera then try to start the camera
								// activity
			// birdietracker.trackPageView("/Camera"); // Track people using the
			// camera
			// birdietracker.dispatch();
			try {
				Class<?> cameraclass = Class
						.forName("com.lumens98.birdiemakettest.CameraActivity");
				Intent cameraintent = new Intent(BirdieputtActivity.this,
						cameraclass);
				startActivity(cameraintent);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case R.id.menu_golfbag: // If help then start the help activity

			// birdietracker.trackPageView("/Help"); // Track people using help
			// birdietracker.dispatch();
			try {
				Class<?> golfbagclass = Class
						.forName("com.lumens98.birdiemakettest.GolfBag");
				Intent golfbagintent = new Intent(BirdieputtActivity.this,
						golfbagclass);
				startActivity(golfbagintent);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT)
						.show();
				e1.printStackTrace();
			}
			break;

		case R.id.menu_settings: // If settings then start the settings activity

			// birdietracker.trackPageView("/Settings");
			// birdietracker.dispatch();
			try {
				Class<?> settingsclass = Class
						.forName("com.lumens98.birdiemakettest.Preferences");
				Intent settingsintent = new Intent(BirdieputtActivity.this,
						settingsclass);
				startActivity(settingsintent);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case R.id.menu_help: // If about then start the about activity
			// birdietracker.trackPageView("/About"); // Track people going to
			// about page
			// birdietracker.dispatch();
			try {
				Class<?> helpclass = Class
						.forName("com.lumens98.birdiemakettest.Help");
				Intent helpintent = new Intent(BirdieputtActivity.this,
						helpclass);
				startActivity(helpintent);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case R.id.menu_about: // If about then start the about activity
			// birdietracker.trackPageView("/About"); // Track people going to
			// about page
			// birdietracker.dispatch();
			try {
				Class<?> aboutclass = Class
						.forName("com.lumens98.birdiemakettest.AboutUs");
				Intent aboutusintent = new Intent(BirdieputtActivity.this,
						aboutclass);
				startActivity(aboutusintent);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		myLocationOverlay.disableMyLocation(); // Stop all GPS procedures
		locationmanager.removeUpdates(this);

		// birdietracker.trackPageView("/OnPause"); // Track how many times the
		// phone goes in to pause.
		// birdietracker.dispatch();

		super.onPause();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		myLocationOverlay.enableMyLocation(); // Restart all the GPS procedures
		locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				500, 1, this);

		super.onResume();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class Touched extends Overlay {

		private boolean isPinch = false;
		private boolean shorttap = false;
		private long starttime, stoptime;
		private long tapthreshold = 250;

		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			// Event handler to ensure pinches don't result in Taps
			int fingers = e.getPointerCount();
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				isPinch = false; // Touch DOWN, don't know if it's a pinch yet
			}
			if (e.getAction() == MotionEvent.ACTION_MOVE && fingers == 2) {
				isPinch = true; // Two fingers, definitely a pinch
			}

			if (isPinch) {

			} else {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					starttime = e.getEventTime();
				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					stoptime = e.getEventTime();
					if ((stoptime - starttime) < tapthreshold) {
						shorttap = true;
					} else {
						shorttap = false;
					}
				}

			}

			return super.onTouchEvent(e, mapView);
		}

		@Override
		public boolean onTap(GeoPoint p, MapView m) {

			if (isPinch) {
				return false;
			} else {
				if (shorttap) {

					GeoPoint currentlocation;

					// Add market to tapped location
					OverlayItem overlayItem = new OverlayItem(p, "Hello1",
							"Hello2");
					CustomPinPoint custom = new CustomPinPoint(pointblue,
							BirdieputtActivity.this);
					custom.insertPinpoint(overlayItem);
					overlaylist.add(custom);
					overlayitems++;

					// Find out current position
					currentlocation = myLocationOverlay.getMyLocation();

					// If GPS has acquired a location
					if (currentlocation != null) {

						accuracy = myLocationOverlay.getLastFix().getAccuracy();
						tvAccuracy
								.setText("+/- "
										+ String.valueOf((int) (accuracy * MeterstoYards))
										+ " yds");

						// Let first position be the current location
						overlayitems++;
						if (TappedPoints.isEmpty()) {
							TappedPoints.add(currentlocation);
							overlaymark = overlayitems;
						}

						// Add the new tapped location
						TappedPoints.add(p);

						int numTapped = TappedPoints.size();

						// Get distance between points
						double newdist = CalcDistance(
								TappedPoints.get(numTapped - 2),
								TappedPoints.get(numTapped - 1));
						TotalDistance += newdist;

						// Update distance text view
						if (TotalDistance < 1000) {
							tvDistance.setText(" "
									+ String.valueOf((int) TotalDistance)
									+ " yds ");
						} else {
							tvDistance.setText(" 999+yds ");
						}

						// Draw line between

						overlaylist.add(new DirectionPathOverlay(TappedPoints
								.get(numTapped - 2), TappedPoints
								.get(numTapped - 1), newdist));

					} else {
						// Mention that GPS is busted
						Toast.makeText(BirdieputtActivity.this,
								"Waiting to acquire GPS location.",
								Toast.LENGTH_SHORT).show();

					}

				} else { // If long tap

					GeoPoint currentlocation;

					// Add market to tapped location
					OverlayItem overlayItem = new OverlayItem(p, "Hello1",
							"Hello2");
					CustomPinPoint custom = new CustomPinPoint(pointyellow,
							BirdieputtActivity.this);
					custom.insertPinpoint(overlayItem);
					overlaylist.add(custom);
					overlayitems++;

					// Find out current position
					currentlocation = myLocationOverlay.getMyLocation();

					if (currentlocation != null) {

						accuracy = myLocationOverlay.getLastFix().getAccuracy();
						tvAccuracy
								.setText("+/- "
										+ String.valueOf((int) (accuracy * MeterstoYards))
										+ " yds");

						// Get distance between points
						double newdist = CalcDistance(currentlocation, p);

						// Draw line between

						overlaylist.add(new DistanceOverlay(p, newdist));
						overlayitems++;
						LongTappedPoints.add(p);
						LongTapOverlayLocs.add(overlayitems);
					} else {
						// Mention that GPS is busted
						Toast.makeText(BirdieputtActivity.this,
								"Waiting to acquire GPS location.",
								Toast.LENGTH_SHORT).show();

					}

				}

				return false;
			}
		}

	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		// Setup utility variables
		GeoPoint currentlocation;

		// Get current GPS location
		currentlocation = myLocationOverlay.getMyLocation();

		// Display location accuracy
		accuracy = myLocationOverlay.getLastFix().getAccuracy();
		tvAccuracy.setText("+/- "
				+ String.valueOf((int) (accuracy * MeterstoYards)) + " yds");

		// Unless there are tapped locations
		if (!TappedPoints.isEmpty()) {

			// Subtract old distance from the list of Tapped points
			TotalDistance -= CalcDistance(TappedPoints.get(0),
					TappedPoints.get(1));
			// Set first location on the list to GPS location
			TappedPoints.set(0, currentlocation);
			// Calculate the new distance and update total distance
			double newdist = CalcDistance(TappedPoints.get(0),
					TappedPoints.get(1));
			TotalDistance += newdist;

			// Display distance
			tvDistance.setText(" " + String.valueOf((int) TotalDistance)
					+ " yds");
			// Draw a new line from GPS location to first tapped point
			overlaylist.set(
					overlaymark,
					new DirectionPathOverlay(TappedPoints.get(0), TappedPoints
							.get(1), newdist));

		}

		int longtaps = LongTappedPoints.size();
		if (longtaps != 0) {
			for (int i = 0; i < longtaps; i++) {
				ModLongTapDist(LongTappedPoints.get(i), currentlocation,
						LongTapOverlayLocs.get(i));
			}
		}
		map.invalidate();

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "GPS service is disabled", Toast.LENGTH_SHORT)
				.show();
		// birdietracker.setCustomVar(0, "GPS", "GPS DISABLED");
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	// Method that calculated distance between to points
	public double CalcDistance(GeoPoint GP1, GeoPoint GP2) {

		double distance;

		Location locationA = new Location("point A");
		locationA.setLatitude(GP1.getLatitudeE6() / 1E6);
		locationA.setLongitude(GP1.getLongitudeE6() / 1E6);

		Location locationB = new Location("point B");
		locationB.setLatitude(GP2.getLatitudeE6() / 1E6);
		locationB.setLongitude(GP2.getLongitudeE6() / 1E6);

		distance = Math.round(locationA.distanceTo(locationB) * MeterstoYards); // Converts
																				// to
																				// yards

		return distance;
	}

	// Class and Method that draws lines between two points
	public class DirectionPathOverlay extends Overlay {

		private GeoPoint gp1;
		private GeoPoint gp2;
		private double distance;

		public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2, double distance) {
			this.gp1 = gp1;
			this.gp2 = gp2;
			this.distance = distance;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection();
			if (shadow == false) {

				Paint paint = new Paint(); // Setup drawing parameters
				paint.setAntiAlias(true);
				paint.setColor(Color.rgb(0, 153, 204));
				paint.setStrokeWidth(4);

				Point point = new Point(); // Get location of first point
				projection.toPixels(gp1, point);

				Point point2 = new Point(); // Get location of secong point
				projection.toPixels(gp2, point2);

				// Draw line
				canvas.drawLine((float) point.x, (float) point.y,
						(float) point2.x, (float) point2.y, paint);

				// Setup drawing parameters and draw the distance in the middle
				// of the line
				paint.setTextSize(25);
				paint.setColor(Color.rgb(255, 136, 0));
				canvas.drawText(String.valueOf((int) distance),
						(point.x + point2.x) / 2, (point.y + point2.y) / 2,
						paint);

			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub

			super.draw(canvas, mapView, shadow);
		}
	}

	// Class and Method that draws distance for independent points
	public class DistanceOverlay extends Overlay {

		private GeoPoint gp;

		private double distance;

		public DistanceOverlay(GeoPoint gp, double distance) {
			this.gp = gp;

			this.distance = distance;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection();
			if (shadow == false) {

				Paint paint = new Paint(); // Setup drawing parameters
				paint.setAntiAlias(true);
				paint.setStrokeWidth(2);

				Point point = new Point(); // Get location of first point
				projection.toPixels(gp, point);

				// Setup drawing parameters and draw the distance in the middle
				// of the line
				paint.setTextSize(25);
				paint.setColor(Color.rgb(255, 136, 0));
				canvas.drawText(String.valueOf((int) distance), point.x + 20,
						point.y + 10, paint);

			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub

			super.draw(canvas, mapView, shadow);
		}
	}

	public void SelectClub() {
		// Method to get club distance from saved preferences, figure out which
		// one match and draw their ranges on map

		// Don't bother if there's no tap on the map first
		if (TotalDistance == 0) {
			Toast.makeText(this, "Tap on map first", Toast.LENGTH_SHORT).show();
		} else {

			String ClubNames[];
			String ClubHighs[];
			String ClubLows[];
			String ClubUse[];

			ClubDatabase Clubs = new ClubDatabase(BirdieputtActivity.this);
			Clubs.open();
			ClubNames = Clubs.getClubNames();
			ClubHighs = Clubs.getHighs();
			ClubLows = Clubs.getLows();
			ClubUse = Clubs.getClubUse();
			Clubs.close();

			int count = ClubNames.length;

			// Get the distance to the first tapped location
			double newdist = CalcDistance(TappedPoints.get(0),
					TappedPoints.get(1));

			// Go through the club set to see which one matches
			boolean noRightClubs = true;
			int highestclubindex = 0;

			for (int i = 0; i < count; i++) {

				if (Integer.parseInt(ClubLows[i]) <= newdist) {
					if (Integer.parseInt(ClubHighs[i]) >= newdist) {
						if (ClubUse[i].equals("TRUE")) {
							// If there's a match then draw the clubs range
							DrawClubRange(ClubNames[i], ClubLows[i],
									ClubHighs[i]);
							// Make note: we have found at least one club that
							// works
							noRightClubs = false;
						}
					}

				}

			}
			// If not matching clubs were found then say so...
			if (noRightClubs) {
				for (int i = 0; i < count; i++) {
					if (ClubUse[i].equals("TRUE")) {
						highestclubindex = i;
						break;
					}
				}
				if (Integer.parseInt(ClubHighs[highestclubindex]) < newdist) {
					DrawClubRange(ClubNames[highestclubindex],
							ClubLows[highestclubindex],
							ClubHighs[highestclubindex]);
					Toast.makeText(this, "Oh you wish! :)", Toast.LENGTH_SHORT)
							.show();
				} else {

					Toast.makeText(this, "No clubs match this distance",
							Toast.LENGTH_SHORT).show();
				}
			}
			// Return the club color to original
			Clubcolor = -1;
		}
	}

	public void DrawClubRange(String Club, String low, String high) {
		// Method to setup the variable to be sent to drawing the arch range for
		// that club

		double Clublow = Integer.parseInt(low);
		double ClubHigh = Integer.parseInt(high);

		// Make sure the club colors are within the ranges
		Clubcolor++;
		if (Clubcolor > 3)
			Clubcolor = 0;

		overlaylist
				.add(new RangeArcOverlay(Club, Clublow, ClubHigh, Clubcolor));

	}

	public class RangeArcOverlay extends Overlay {
		// Method to Draw the ranges for a specific club

		private String Club;
		private double Low;
		private double High;
		private int color;

		public RangeArcOverlay(String Club, double Low, double High, int color) {
			this.Club = Club;
			this.Low = Low;
			this.High = High;
			this.color = color;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			// TODO Auto-generated method stub

			// First get the map projection
			Projection projection = mapView.getProjection();
			if (shadow == false) {

				// Setup the variables
				float ArcAngle = 20;
				float StartAngleMod = 0;
				float SweepAngleMod = 0;
				float LineAngle = 0;

				// Get the GPS location and user tapped point
				GeoPoint GP1 = TappedPoints.get(0);
				GeoPoint GP2 = TappedPoints.get(1);

				// Setup the drawing parameters
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStrokeWidth(2);
				paint.setStyle(Paint.Style.STROKE);

				// Get first point
				Point point = new Point();
				projection.toPixels(GP1, point);

				// Get second point
				Point point2 = new Point();
				projection.toPixels(GP2, point2);

				// Get real distance and the distance on screen
				double GPdistance = CalcDistance(TappedPoints.get(0),
						TappedPoints.get(1));
				double Overlaydistance = Math.sqrt((Math.pow(
						(point2.x - point.x), 2) + Math.pow(
						(point2.y - point.y), 2)));

				// Get the Low of the High ranges of the club as a ratio to
				// maintain correct length when zooming
				int LowRadius = (int) (Low * Overlaydistance / GPdistance);
				int HighRadius = (int) (High * Overlaydistance / GPdistance);

				// Figure out the angle at which the two points lie.
				float Angle = AzimuthAngle(point, point2);

				// Figure out in which color to draw the range
				// Consecutive clubs are drawn on either side of the tapped
				// location in different colors
				if (color == 0) {
					paint.setColor(Color.rgb(255, 136, 0));
					StartAngleMod = 0;
					SweepAngleMod = 5;
					LineAngle = Angle - ArcAngle;
				}

				if (color == 1) {
					paint.setColor(Color.YELLOW);
					StartAngleMod = 5;
					SweepAngleMod = 5;
					LineAngle = Angle + ArcAngle;
				}

				if (color == 2) {
					paint.setColor(Color.WHITE);
					StartAngleMod = 0;
					SweepAngleMod = 5;
					LineAngle = Angle - ArcAngle;
				}

				if (color == 3) {
					paint.setColor(Color.BLACK);
					StartAngleMod = 5;
					SweepAngleMod = 5;
					LineAngle = Angle + ArcAngle;
				}

				// Setup variables for drawing the arc
				RectF Lowrecf = new RectF(point.x - LowRadius, point.y
						- LowRadius, point.x + LowRadius, point.y + LowRadius);

				RectF Highrecf = new RectF(point.x - HighRadius, point.y
						- HighRadius, point.x + HighRadius, point.y
						+ HighRadius);

				// Draw the arc
				canvas.drawArc(Lowrecf, (Angle - ArcAngle) + StartAngleMod,
						(ArcAngle * 2) - SweepAngleMod, false, paint);
				canvas.drawArc(Highrecf, (Angle - ArcAngle) + StartAngleMod,
						(ArcAngle * 2) - SweepAngleMod, false, paint);

				// Now figure out the locations of the ends of the arc
				float x1 = (float) (point.x + LowRadius
						* Math.cos(Math.toRadians(LineAngle)));
				float y1 = (float) (point.y + LowRadius
						* Math.sin(Math.toRadians(LineAngle)));
				float x2 = (float) (point.x + HighRadius
						* Math.cos(Math.toRadians(LineAngle)));
				float y2 = (float) (point.y + HighRadius
						* Math.sin(Math.toRadians(LineAngle)));
				// Draw a line from ends of the low arc to high arc
				canvas.drawLine(x1, y1, x2, y2, paint);

				// setup the drawing parameters and say which club and range and
				// the center of the line
				paint.setTextSize(25);
				paint.setStyle(Paint.Style.FILL);

				float textX = 0;
				float textY = 0;

				switch (color) {
				case 0:
					textX = 4;
					textY = 25;
					break;

				case 1:
					textX = 4;
					textY = 55;
					break;

				case 2:
					textX = 4;
					textY = 85;
					break;

				case 3:
					textX = 4;
					textY = 115;
					break;

				}

				canvas.drawText(Club + ": " + String.valueOf((int) Low) + " - "
						+ String.valueOf((int) High), textX, textY, paint);

				map.invalidate();

			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub

			super.draw(canvas, mapView, shadow);
		}
	}

	public float AzimuthAngle(Point p1, Point p2) {
		// method to figure out azimuth angle between two points on the phone
		// screen
		double dx, dy, result;

		dx = p2.x - p1.x;
		dy = p2.y - p1.y;

		if (dy > 0) {
			result = Math.atan2(dy, dx);
		} else {
			if (dy < 0) {
				result = (2 * Math.PI) + Math.atan2(dy, dx);
			} else {
				if (dx > 0) {
					result = 0;
				} else {
					if (dx < 0) {
						result = Math.PI;
					} else {
						result = 0;
					}
				}
			}
		}
		return (float) Math.toDegrees(result);
	}

	public void ClubTracker() {

		GeoPoint CurrentLocation;

		if (!locationmarked) {
			CurrentLocation = myLocationOverlay.getMyLocation();
			if (CurrentLocation != null) {
				startmark = CurrentLocation;
				locationmarked = true;

				bClubTracker.setBackgroundDrawable(btrackred);

				OverlayItem overlayItem = new OverlayItem(startmark, "Hello1",
						"Hello2");
				CustomPinPoint custom = new CustomPinPoint(pointred,
						BirdieputtActivity.this);
				custom.insertPinpoint(overlayItem);
				overlaylist.add(custom);

				if (!TappedPoints.isEmpty()) {
					targetmark = TappedPoints.get(1);
					targetmarked = true;

					overlayItem = new OverlayItem(targetmark, "Hello1",
							"Hello2");
					custom = new CustomPinPoint(pointred,
							BirdieputtActivity.this);
					custom.insertPinpoint(overlayItem);
					overlaylist.add(custom);

					Toast.makeText(BirdieputtActivity.this,
							"location and target marked...", Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(BirdieputtActivity.this,
							"location marked...", Toast.LENGTH_SHORT).show();
					targetmarked = false;
				}

				map.invalidate();

			} else {
				Toast.makeText(BirdieputtActivity.this,
						"Waiting to acquire GPS location.", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			CurrentLocation = myLocationOverlay.getMyLocation();
			double distancetostart = CalcDistance(CurrentLocation, startmark);

			if (targetmarked) {
				double distancetotarget = CalcDistance(startmark, targetmark);
				double distancemissed = CalcDistance(CurrentLocation,
						targetmark);
				int kudosdistance = 10;
				String dialogtitle;
				if (distancemissed < kudosdistance) {
					dialogtitle = "Great Shot!";
				} else {
					dialogtitle = "Shot Tracker";
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(
						BirdieputtActivity.this);
				builder.setTitle(dialogtitle);
				builder.setMessage("Shot Distance: "
						+ String.valueOf((int) distancetostart)
						+ " yds \nTargeted Distance: "
						+ String.valueOf((int) distancetotarget)
						+ " yds \nMissed by: "
						+ String.valueOf((int) distancemissed) + " yds");
				builder.setPositiveButton("Track",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								boolean includeacc = true;
								ShowClubList(includeacc);
								ClearScreen();

								dialog.cancel();
							}

						});
				builder.setNeutralButton("Forget",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ClearScreen();

								dialog.cancel();

							}
						});
				builder.setNegativeButton("Refresh",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
								ClubTracker();

							}
						});

				AlertDialog alert = builder.create();
				alert.show();

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BirdieputtActivity.this);
				builder.setTitle("Shot Tracker");
				builder.setMessage("Shot Distance: "
						+ String.valueOf((int) distancetostart) + " yds");

				builder.setPositiveButton("Track",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								boolean includeacc = false;
								ShowClubList(includeacc);
								ClearScreen();

								dialog.cancel();
							}
						});
				builder.setNeutralButton("Forget",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								ClearScreen();
								dialog.cancel();
							}
						});
				builder.setNegativeButton("Refresh",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
								ClubTracker();

							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}

		}

	}

	public void ClearScreen() {

		// Clear map, tapped points and distance reads
		overlaylist.clear();
		map.invalidate();
		TappedPoints.clear();
		TotalDistance = 0;
		tvDistance.setText(" 0 yds");

		// Add map overlays back
		Touched touchy = new Touched();
		overlaylist.add(touchy);
		map.getOverlays().add(myLocationOverlay);

		// Return Club color to original status
		Clubcolor = -1;

		locationmarked = false;
		targetmarked = false;

		bClubTracker.setBackgroundDrawable(btracknormal);

		overlayitems = 1;
		overlaymark = 3;

		LongTappedPoints.clear();
		LongTapOverlayLocs.clear();

	}

	private void ShowClubList(boolean includeAcc) {

		// TODO Auto-generated method stub
		double distancemissed;
		double angletarget, anglehit, errorangle;

		GeoPoint CurrentLocation = myLocationOverlay.getMyLocation();
		double distancetostart = CalcDistance(CurrentLocation, startmark);

		if (includeAcc) {

			distancemissed = CalcDistance(CurrentLocation, targetmark);
			Point point1 = new Point();
			point1.x = startmark.getLatitudeE6();
			point1.y = startmark.getLongitudeE6();

			Point point2 = new Point();
			point2.x = targetmark.getLatitudeE6();
			point2.y = targetmark.getLongitudeE6();

			Point point3 = new Point();
			point3.x = CurrentLocation.getLatitudeE6();
			point3.y = CurrentLocation.getLongitudeE6();

			angletarget = AzimuthAngle(point1, point2);
			anglehit = AzimuthAngle(point1, point3);
			errorangle = anglehit - angletarget;
			if (errorangle < -180) {
				errorangle = 360 + errorangle;
			}

			if (errorangle > 180) {
				errorangle = errorangle - 360;
			}

		} else {
			distancemissed = 0;
			errorangle = 0;

		}

		String shotdistance = String.valueOf((int) distancetostart);
		String accuracy = String.valueOf((int) distancemissed);
		String angle = String.valueOf((int) errorangle);

		Log.d(getLocalClassName(), angle);

		try {
			Class<?> clublistclass = Class
					.forName("com.lumens98.birdiemakettest.ClubList");
			Intent clublistintent = new Intent(BirdieputtActivity.this,
					clublistclass);
			clublistintent.putExtra("DISTANCE", shotdistance);
			if (includeAcc) {
				clublistintent.putExtra("ACC_INC", "TRUE");
			} else {
				clublistintent.putExtra("ACC_INC", "FALSE");
			}

			clublistintent.putExtra("ACCURACY", accuracy);
			clublistintent.putExtra("ANGLE", angle);
			startActivity(clublistintent);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Class not Found", Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
		}

	}

	private void ModLongTapDist(GeoPoint p, GeoPoint currentlocation, int index) {
		// TODO Auto-generated method stub

		double newdist = CalcDistance(currentlocation, p);
		overlaylist.set(index, new DistanceOverlay(p, newdist));

	}

	private void RunForFirstTime() {
		// TODO Auto-generated method stub

		String CheckFirst;
		preferences = getSharedPreferences(filename, 0);
		CheckFirst = preferences.getString("FIRST_TIME", "TRUE");
		if (CheckFirst.equals("TRUE")) {

			String Club[] = new String[12];
			String ClubLow[] = new String[12];
			String ClubHigh[] = new String[12];

			Club[0] = preferences.getString("Club1", "Driver");
			Club[1] = preferences.getString("Club2", "3 Wood");
			Club[2] = preferences.getString("Club3", "5 Wood");
			Club[3] = preferences.getString("Club4", "3 Hybrid");
			Club[4] = preferences.getString("Club5", "4 Hybrid");
			Club[5] = preferences.getString("Club6", "5 Iron");
			Club[6] = preferences.getString("Club7", "6 Iron");
			Club[7] = preferences.getString("Club8", "7 Iron");
			Club[8] = preferences.getString("Club9", "8 Iron");
			Club[9] = preferences.getString("Club10", "9 Iron");
			Club[10] = preferences.getString("Club11", "Pitching Wedge");
			Club[11] = preferences.getString("Club12", "Sand Wedge");

			ClubLow[0] = preferences.getString("Club1Low", "200");
			ClubLow[1] = preferences.getString("Club2Low", "180");
			ClubLow[2] = preferences.getString("Club3Low", "170");
			ClubLow[3] = preferences.getString("Club4Low", "160");
			ClubLow[4] = preferences.getString("Club5Low", "150");
			ClubLow[5] = preferences.getString("Club6Low", "140");
			ClubLow[6] = preferences.getString("Club7Low", "130");
			ClubLow[7] = preferences.getString("Club8Low", "120");
			ClubLow[8] = preferences.getString("Club9Low", "110");
			ClubLow[9] = preferences.getString("Club10Low", "95");
			ClubLow[10] = preferences.getString("Club11Low", "80");
			ClubLow[11] = preferences.getString("Club12Low", "60");

			ClubHigh[0] = preferences.getString("Club1High", "250");
			ClubHigh[1] = preferences.getString("Club2High", "215");
			ClubHigh[2] = preferences.getString("Club3High", "195");
			ClubHigh[3] = preferences.getString("Club4High", "180");
			ClubHigh[4] = preferences.getString("Club5High", "170");
			ClubHigh[5] = preferences.getString("Club6High", "160");
			ClubHigh[6] = preferences.getString("Club7High", "150");
			ClubHigh[7] = preferences.getString("Club8High", "140");
			ClubHigh[8] = preferences.getString("Club9High", "130");
			ClubHigh[9] = preferences.getString("Club10High", "115");
			ClubHigh[10] = preferences.getString("Club11High", "105");
			ClubHigh[11] = preferences.getString("Club12High", "80");

			ClubDatabase Clubs = new ClubDatabase(BirdieputtActivity.this);
			Clubs.open();

			for (int i = 0; i < 12; i++) {
				Clubs.createnewclub(Club[i], ClubHigh[i], ClubLow[i]);
			}

			Clubs.close();

			SharedPreferences.Editor prefeditor = preferences.edit();
			prefeditor.putString("FIRST_TIME", "FALSE");
			prefeditor.commit();
		}

	}
}
