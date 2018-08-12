package com.lumens98.birdiemakettest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.lumens98.birdiemakettest.R;

public class PurchaseActivity extends ActionBarActivity implements
		OnClickListener {
	/** Called when the activity is first created. */

	Button bPurchase;

	SharedPreferences preferences; // Preferences variables
	public static String filename = "mySettings";

	private static final String TAG = "BillingService";
	private Context mContext;

	public final String mProductId = "shot_track_001";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);

		mContext = this;

		startService(new Intent(mContext, BillingService.class));
		BillingHelper.setCompletedHandler(mTransactionHandler);

		bPurchase = (Button) findViewById(R.id.bPurchase);

		bPurchase.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BillingHelper.stopService();
		super.onDestroy();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.bPurchase:

			if (BillingHelper.isBillingSupported()) {
				BillingHelper.requestPurchase(mContext, mProductId);
			}

			break;

		}

	}

	public Handler mTransactionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.i(TAG, "Transaction complete");
			Log.i(TAG, "Transaction status: "
					+ BillingHelper.latestPurchase.purchaseState);
			Log.i(TAG, "Item attempted purchase is: "
					+ BillingHelper.latestPurchase.productId);

			preferences = getSharedPreferences(filename, 0);
			SharedPreferences.Editor prefeditor = preferences.edit();
			prefeditor.putString("ST_BOUGHT", "TRUE");
			prefeditor.commit();

			Toast.makeText(PurchaseActivity.this, "Shot tracking activated!",
					Toast.LENGTH_SHORT).show();
			finish();

			// tvResult.setText("Purchase Complete! Purchase Item: "+
			// BillingHelper.latestPurchase.productId);

			if (BillingHelper.latestPurchase.isPurchased()) {

			} else {
				// Failure
			}
		};
	};

}