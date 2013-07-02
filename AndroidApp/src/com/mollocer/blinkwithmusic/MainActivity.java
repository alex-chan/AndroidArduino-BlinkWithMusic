package com.mollocer.blinkwithmusic;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import com.mollocer.blinkwithmusic.BlinkService.BlinkBinder;




import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
//import android.hardware.usb.UsbAccessory;
//import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity  {
	
	
    private final String TAG = "BlinkWithMusicMainActivity";
 	

	private ToggleButton mTbBlink, mTbService;
		

	
	boolean bBound = false;
	boolean bStopService = false;
	
	BlinkService mService ;
	
	private ServiceConnection mConnection = new ServiceConnection(){
		
		@Override
		public void onServiceConnected(ComponentName className,  IBinder service){
			Log.d(TAG,"onServiceConnected");
			BlinkBinder binder = (BlinkBinder)service;
			mService = binder.getService();
			bBound = true;
			
			mTbBlink.setChecked(true);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0){
			bBound = false;
		}
	};
	
	
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG,"onCreate");
        
        listenToSwitcher();

		Log.d(TAG, "listenToLedSwitch");
		
		bindBlinkService();

    }
	
	
	private void bindBlinkService(){
		
		if(!bBound){
						
			Intent intent = new Intent(this, BlinkService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);	
			
			
		}
		
	}
		
	private void unbindBlinkService(){
		if(bBound){
			mTbBlink.setChecked(false);
			unbindService(mConnection);
			bBound = false;
		}
						
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume");
		/*
		Intent intent = getIntent();
		Log.d(TAG,"get intent");
		if (mInputStream != null && mOutputStream != null) {
			Log.d(TAG,"mInput/OutputStream is Null, return");
			return;
		}
		
		
//		UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
// 		UsbAccessory accessory = UsbManager.getAccessory(intent);
		
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			Log.d(TAG,"accessory is Not null");
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
		*/
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		unbindBlinkService();
		super.onDestroy();
	}	


    private void listenToSwitcher() {
		
    	mTbBlink = (ToggleButton)findViewById(R.id.toggleButtonBlink);   
    	
    	
    	
    	mTbBlink.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
				if(bBound){
					if( isChecked){					
						
						mService.enableBlink(true);
						
					}else{
						
						mService.enableBlink(false);
					}	
					
					
				}else{
					Toast.makeText(
							MainActivity.this, 
							"Service not bounded, could not start blink service",
							Toast.LENGTH_SHORT).show();
					
				}
				
				
			}


		});
    	
    	
    	mTbService.setChecked(true);
    	
		
	}


    
    


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	
	
	

	
	





    
}
