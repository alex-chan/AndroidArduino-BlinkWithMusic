package com.mollocer.blinkwithmusic;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;




import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.hardware.usb.UsbAccessory;
//import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity  {
	
	
    private final String TAG = "BlinkWithMusicMainActivity";
    private static final String ACTION_USB_PERMISSION = "com.mollocer.turnonoffled.action.USB_PERMISSION";
    private boolean mPermissionRequestPending;
    
	

	private ToggleButton mTbSwitcher;

	/*
	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	
    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;
	
    
    AudioManager mAdManager ;
    Visualizer mVisualizer;
    */
	
	Intent intent;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG,"onCreate");
        
//        mVisualizer = new Visualizer(0);
        
        listenToSwitcher();

        
		Log.d(TAG, "listenToLedSwitch");
		
		
		intent = new Intent(this, BlinkService.class);

    }
	
	/*
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}*/

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
//		closeAccessory();
	}

	@Override
	public void onDestroy() {
//		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}	


    private void listenToSwitcher() {
		
    	mTbSwitcher = (ToggleButton)findViewById(R.id.toggleButtonBlink);   
    	mTbSwitcher.setChecked(true);
    		
    	mTbSwitcher.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
				if( isChecked){					
					
					startService(intent);
					//mTbSwitcher.setTextOff("Off");
					//sendTurnOnLedCommand();
				}else{
					stopService(intent);
					//mTbSwitcher.setTextOn("On");					
					//sendTurnOffLedCommand();
				}
			}


		});
		
		
	}




	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	
	
	

	
	





    
}
