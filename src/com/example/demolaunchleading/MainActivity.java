package com.example.demolaunchleading;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
/**
 * 
 * @author lijunqing
 *
 */
public class MainActivity extends Activity {
	MySurfaceView mView=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DisplayMetrics display = getResources().getDisplayMetrics();
		Constants.WINWIDTH = display.widthPixels;
		Constants.WINHEIGHT = display.heightPixels;
		mView = new MySurfaceView(this);
		setContentView(mView);
		Log.v("ljq", display.widthPixels +","+display.heightPixels);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v("activityLY", "start");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v("activityLY", "restart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v("activityLY", "resume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v("activityLY", "pause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.v("activityLY", "stop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mView=null;
		Log.v("activityLY", "destroy");
	}
	
}
