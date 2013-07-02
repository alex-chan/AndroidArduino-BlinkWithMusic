package com.mollocer.blinkwithmusic;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

public class BlinkService extends Service implements Runnable {

	
	private static final String TAG = "BlinkService";
	private static final String ACTION_USB_PERMISSION = "com.mollocer.turnonoffled.action.USB_PERMISSION";
	   
	private boolean bBlinking = false;

	
	private boolean mPermissionRequestPending;
	
	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	
    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;
	
    
    AudioManager mAdManager ;
    Visualizer mVisualizer = new Visualizer(0);	
	
    Thread thread = null;
    
    private  Binder mBlinkBinder = new BlinkBinder(); 
    
    public class BlinkBinder extends Binder{
    	BlinkService getService(){
    		return BlinkService.this;    		
    	}
    }
    
	public BlinkService(){
		super();
		Log.d(TAG,"Construct blinkservice");
		
	}
	
	@Override
	public void onCreate(){
		Log.d(TAG,"onCreate");
        mUsbManager = UsbManager.getInstance(this);
        
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		

		Log.d(TAG, "mUsbReceiver Registered");
		

		mAdManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		Thread thread = new Thread(null, this, "BlinkWithMusic");
        thread.start();
        
	}
	
		
	

	@Override
	public void onDestroy() {
		closeAccessory();
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}	
	
	
	
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	

        	Log.d(TAG, "Received Broadcast");
            String action = intent.getAction();
            Log.d(TAG, "onReceived action:"+action);
            
            
            
            if(ACTION_USB_PERMISSION.equals(action)){


                synchronized (this){
                	
//                	UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                	UsbAccessory accessory = UsbManager.getAccessory(intent);	
            		
                    if( intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                        openAccessory(accessory);
                    }else{
                        Log.d(TAG, "permission denied for accessory"
                                + accessory);
                    }
                    mPermissionRequestPending = false;
                }

            }else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                
//                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (accessory != null && accessory.equals(mAccessory)) {
                    closeAccessory();
                }
            }

        }
    };
    


    private void openAccessory(UsbAccessory accessory) {
    	
    	Log.d(TAG,"openAccessory");
    	
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            Log.d(TAG, "accessory opened");

            
        } else {
            Log.d(TAG, "accessory open fail");

        }
    }

    private void closeAccessory() {
    	Log.d(TAG,"closeAccessory");
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }		

    
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//    	Toast.makeText(this, "Blink starting..", Toast.LENGTH_SHORT).show();
//    	
//    	if( thread == null ){
//    		thread = new Thread(null, this, "BlinkService");    		
//    	}
//    	
//    	
//    	return START_STICKY;
//    }
    
    public void enableBlink(boolean shouldBlink){
    	String msg = shouldBlink ? "Enable blink" : "Disable blink";
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    	
    	bBlinking = shouldBlink;
    }
    
    
    public boolean isBlinking(){
    	return bBlinking;
    }

	public void run() {
		//
		Log.d(TAG,"run in thread");
		int iCaptureLen = 128;
		
		mVisualizer.setEnabled(false);
		mVisualizer.setCaptureSize(iCaptureLen);
		mVisualizer.setEnabled(true);
		
		
		//int maxVol = mAdManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		while(bBlinking){
			
		
			byte normal = 0;
			if(  mInputStream!=null && mOutputStream!=null){
				
				byte wave[] = new byte[iCaptureLen];
				int total = 0;
				int average = 0;
				if( mVisualizer.getWaveForm(wave) == Visualizer.SUCCESS){
					for(int i=0;i<iCaptureLen;i++){
						total += wave[i];						
					}
					
					average =  (total / iCaptureLen) + 128;
					Log.d(TAG, "Total :" +  total );
					Log.d(TAG, "Average:" +  average );
					Log.d(TAG, "Before: " + (int)(average/256.0 * 5) );
					normal = (byte)( int  ) ( average / 256.0 * 5);
					Log.d(TAG, "After :" + normal);
				}
				
				
				//int volume = mAdManager.getStreamVolume(AudioManager.STREAM_MUSIC);				
				//normal =  (byte)(int) ( volume / (float)maxVol * 5); // 5 LED
				
			}
			
			//Log.d(TAG, "checked" + mTbSwitcher.isChecked() );
			//if(normal==0)continue;
			
			sendCommand(normal);
			
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
		
		
		
	}
		
	
	
    
	public void sendCommand(byte command){
		byte[] buffer = new byte[1];
		buffer[0] = command;
		if (mOutputStream != null && buffer[0] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			
			}
		}		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBlinkBinder;
	}



	
	
}
