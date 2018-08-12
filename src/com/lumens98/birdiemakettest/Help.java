package com.lumens98.birdiemakettest;

import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import com.lumens98.birdiemakettest.R;


public class Help extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		TabSpec tabspecs;
		
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		tabhost.setup();
		
		tabspecs = tabhost.newTabSpec("tag1");
		tabspecs.setContent(R.id.tab1);
		tabspecs.setIndicator("What's New");
		tabhost.addTab(tabspecs);
		
		tabspecs = tabhost.newTabSpec("tag2");
		tabspecs.setContent(R.id.tab2);
		tabspecs.setIndicator("Help");
		tabhost.addTab(tabspecs);
		
		tabspecs = tabhost.newTabSpec("tag3");
		tabspecs.setContent(R.id.tab3);
		tabspecs.setIndicator("Known Issues");
		tabhost.addTab(tabspecs);
	}
}
