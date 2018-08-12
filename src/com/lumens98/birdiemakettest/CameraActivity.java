package com.lumens98.birdiemakettest;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class CameraActivity extends ActionBarActivity implements
		OnTouchListener {

	// Variables for calculating Pin distance
	int TypZoomX = 4;
	int PixelAccuracy = 3;
	int ViewHeight = 480;
	int zoomlevel = 0;
	double Maxzoom = 0;
	double flagdistance = 0;
	float verticalangle;
	double maxpicturewidth;

	// Variables to get standard Pin height
	SharedPreferences preferences;
	public static String filename = "mySettings";
	int flagheight;

	// Variables for setting up the camera
	Camera camera = null;
	SurfaceHolder surfaceHolder;
	DrawOnTop mDraw;
	FrameLayout cameraoverlay;
	private Preview preview;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	List<Integer> zoomratios;

	// Variables for drawing the lines on screen
	// Screen divided to 20 parts
	int cW, cH;
	float blY = 15;
	float tlY = 3;
	float X1 = 2;
	float X2 = 15;
	// float radius = 1;
	float markerX, markerY, blineY;
	Bitmap marker;

	// Variables to figure out where the user touched the screen
	float TouchX, TouchY;

	// Variables for the view
	TextView tvDist, tvAccr;
	Button bPlus, bMinus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera);
		// Bitmap to show where to touch on screen
		marker = BitmapFactory.decodeResource(getResources(),
				R.drawable.finger_marker);

		// Set up where the lines on screen will show up
		// Screen divided to 20 parts
		markerX = X2 / 20;
		markerY = tlY / 20;
		blineY = blY / 20;

		// Set up camera and the overlay
		preview = new Preview(this);
		mDraw = new DrawOnTop(this);
		cameraoverlay = (FrameLayout) findViewById(R.id.preview);
		cameraoverlay.addView(preview);
		cameraoverlay.addView(mDraw);
		cameraoverlay.setOnTouchListener(this);

		// setup the text views
		tvDist = (TextView) findViewById(R.id.tvDistance);
		tvAccr = (TextView) findViewById(R.id.tvAccuracy);
		tvDist.setText(" 0 yds");

		// Get what the settings say about the Pin height
		preferences = getSharedPreferences(filename, 0);
		flagheight = Integer.parseInt(preferences.getString("FlagHeight", "7"));

		// Setup the zoom buttons on their on click listeners
		bPlus = (Button) findViewById(R.id.bPlus);
		bMinus = (Button) findViewById(R.id.bMinus);

		bPlus.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Check to see if the camera supports zoom and zoom in one
				// level
				// also update the distance being shown
				Camera.Parameters parameters = camera.getParameters();
				if (parameters.isSmoothZoomSupported()) {
					Maxzoom = parameters.getMaxZoom();
					zoomratios = parameters.getZoomRatios();
					if (zoomlevel < parameters.getMaxZoom()) {
						camera.startSmoothZoom(zoomlevel + 1);
						zoomlevel++;
						UpdateDistance();
					} else {
						Toast.makeText(CameraActivity.this, "Max Zoom",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(CameraActivity.this, "Zoom not supported",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		bMinus.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Check to see if the camera supports zoom and zoom out one
				// level
				// also update the distance being shown
				Camera.Parameters parameters = camera.getParameters();
				if (parameters.isSmoothZoomSupported()) {
					Maxzoom = parameters.getMaxZoom();
					zoomratios = parameters.getZoomRatios();
					if (zoomlevel > 0) {
						camera.startSmoothZoom(zoomlevel - 1);
						zoomlevel--;
						UpdateDistance();
					} else {
						Toast.makeText(CameraActivity.this, "Min Zoom",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(CameraActivity.this, "Zoom not supported",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater();
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

			finish();
			break;

		case R.id.menu_camera: // If camera then try to start the camera
								// activity
			// birdietracker.trackPageView("/Camera"); // Track people using the
			// camera
			// birdietracker.dispatch();

			break;

		case R.id.menu_golfbag: // If help then start the help activity

			// birdietracker.trackPageView("/Help"); // Track people using help
			// birdietracker.dispatch();
			try {
				Class<?> golfbagclass = Class
						.forName("com.lumens98.birdiemakettest.GolfBag");
				Intent golfbagintent = new Intent(CameraActivity.this,
						golfbagclass);
				startActivity(golfbagintent);
				finish();
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
				Intent settingsintent = new Intent(CameraActivity.this,
						settingsclass);
				startActivity(settingsintent);
				finish();

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
				Intent helpintent = new Intent(CameraActivity.this,
						helpclass);
				startActivity(helpintent);
				finish();
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
				Intent aboutusintent = new Intent(CameraActivity.this,
						aboutclass);
				startActivity(aboutusintent);
				finish();
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

	public void UpdateDistance() {

		// The method to calculate the distance to the Pin
		double picturewidth, relflagheight;

		// See how far apart the lines on the screen are
		float vflagheight = ViewHeight * (blineY - markerY);

		// If not zoom the cool. But if zoom then scale accordingly
		if (Maxzoom == 0) {
			picturewidth = maxpicturewidth;
		} else {
			double ratio = ((double) zoomratios.get(zoomlevel)) / 100;
			picturewidth = maxpicturewidth / ratio;
		}

		// This is the Pin height relative to the full resolution of camera
		relflagheight = vflagheight * picturewidth / ViewHeight;

		// Now calculate the distance to the Pin
		flagdistance = (maxpicturewidth / relflagheight)
				* (flagheight * 0.3048)
				/ (2 * Math.tan(0.5 * Math.toRadians(verticalangle)));

		// Now calculate accuracy
		double Accuracy = (PixelAccuracy / vflagheight) * flagdistance;

		// If the distance isn't outrageous then update the text views
		if (flagdistance < 600) {
			if (flagdistance > -600) {

				tvDist.setText(" "
						+ String.valueOf((int) (flagdistance * 1.0936133))
						+ " yds");

				tvAccr.setText("+/- "
						+ String.valueOf((int) (Accuracy * 1.0936133)) + " yds");
			}

		}

	}

	class DrawOnTop extends View {
		public DrawOnTop(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub

			// Method to draw the lines and the marker on screen
			cW = canvas.getWidth();
			cH = canvas.getHeight();

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.rgb(255, 136, 0));
			paint.setStrokeWidth(5);

			canvas.drawBitmap(marker, cW * markerX - marker.getWidth() / 2, cH
					* markerY - marker.getHeight() / 2, null);

			canvas.drawLine(cW * X1 / 20, cH * markerY,
					cW * markerX - marker.getWidth() / 2, cH * markerY, paint);

			canvas.drawLine(cW * X1 / 20, cH * blineY,
					cW * X2 / 20 - marker.getWidth() / 2, cH * blineY, paint);

			super.onDraw(canvas);

		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		// When the user touches the marker then move the marker
		// and also update the distance
		float markerXr = cW * markerX;
		float markerYr = cH * markerY;

		TouchX = event.getX();
		TouchY = event.getY();

		// This defines the size or area the user can touch to mover the marker
		int range = 48;

		UpdateDistance();

		if (TouchX > (markerXr - range)) {
			if (TouchX < (markerXr + range)) {
				if (TouchY > (markerYr - range * 2)) {
					if (TouchY < (markerYr + range * 2)) {

						if (TouchY > 0) {
							if (TouchY < v.getHeight()) {
								blineY += (markerY - TouchY / cH);
								markerY = TouchY / cH;

							}
						}
						cameraoverlay.removeView(mDraw);
						cameraoverlay.addView(mDraw);
					}
				}
			}
		}

		return true;

	}

	public class Preview extends SurfaceView implements SurfaceHolder.Callback {

		List<Size> mSupportedPreviewSizes;
		Size mPreviewSize;
		boolean previewing = false;
		Thread ourthread;
		Canvas canvas;

		@SuppressWarnings("deprecation")
		public Preview(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

			surfaceHolder = getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

			if (camera != null && holder.getSurface() != null) {
				try {
					camera.setPreviewDisplay(surfaceHolder);
				} catch (IOException e) {
					// / TODO Auto-generated catch block
					Log.e(getLocalClassName(), e.getMessage());
					e.printStackTrace();
				}

				if (!cameraConfigured) {

					Camera.Parameters parameters = camera.getParameters();

					Camera.Size size = getBestPreviewSize(height, width,
							parameters);

					if (size != null) {

						Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
								.getDefaultDisplay();

						if (display.getRotation() == Surface.ROTATION_0) {
							parameters.setPreviewSize(size.width, size.height);
							camera.setDisplayOrientation(90);
						}

						if (display.getRotation() == Surface.ROTATION_90) {
							parameters.setPreviewSize(size.height, size.width);
						}

						if (display.getRotation() == Surface.ROTATION_180) {
							parameters.setPreviewSize(size.width, size.height);
						}

						if (display.getRotation() == Surface.ROTATION_270) {
							parameters.setPreviewSize(size.height, size.width);
							camera.setDisplayOrientation(180);
						}

						List<Camera.Size> camerasizes = parameters
								.getSupportedPictureSizes();

						parameters.setPictureSize(
								camerasizes.get(camerasizes.size() - 1).width,
								camerasizes.get(camerasizes.size() - 1).height);

						ViewHeight = size.width; // Remember the view height for
													// later calculations

						camera.setParameters(parameters);

						verticalangle = parameters.getVerticalViewAngle(); // remember
																			// vertical
																			// angle
																			// for
																			// later
																			// calculations

						// remember maximum picture width/height for later
						// calculations
						maxpicturewidth = parameters.getPictureSize().width;
						cameraConfigured = true;

						camera.startPreview();
						inPreview = true;
					}
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub

			camera = Camera.open();

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

			if (inPreview) {
				camera.stopPreview();
			}

			camera.release();
			camera = null;
			inPreview = false;

		}
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

}
