package com.example.apk1;

import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.provider.Settings.SettingNotFoundException;


public class MainActivity extends Activity {

	public void onClickActivity(View v) {
	    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    intent.setPackage("com.google.zxing.client.android");
	    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    startActivityForResult(intent, 0);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	/**
	 * <p>Metoda ustawia automatyczn� lub manualn� warto�� jasno�ci ekranu.<br> 
	 * Automatyczn� je�li przyjmowany parametr ma warto�� TRUE, w przeciwnym wypadku manualn� warto��.</p>
	 * @param value typ: boolean
	 */
	void setAutoBrightness(boolean value) {
	    if (value) {
	    	android.provider.Settings.System.putInt(getContentResolver(), 
	    			android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, 
	    			android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	    } else {
	    	android.provider.Settings.System.putInt(getContentResolver(), 
	    			android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, 
	    			android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	    }
	    boolean isChecked=false;
	    // After brightness change we need to "refresh" current app brightness
	    if (isChecked) {
	        refreshBrightness(-1);
	    } else {
	        refreshBrightness(getBrightnessLevel());
	    }
	}
/**
 * <p>Metoda od�wie�aj�ca ekran.</p>
 * @param brightness typ: float
 */
	private void refreshBrightness(float brightness) {
	    WindowManager.LayoutParams lp = getWindow().getAttributes();
	    if (brightness < 0) {
	        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
	    } else {
	        lp.screenBrightness = brightness;
	    }
	    getWindow().setAttributes(lp);
	}
/**
 * <p>Metoda pobiera aktualn� jasno�� ekranu.</p>
 * @return value warto�� przechowuj�ca liczb� "b�d�c�" jasno�ci� ekranu.
 */
	int getBrightnessLevel() {
	    try {
	        int value = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
	        // convert brightness level to range 0..1
	        value = value / 255;
	        return value;
	    } catch (SettingNotFoundException e) {
	        return 0;
	    }
	}
	
	
	/**
	 * <p>W metodzie sprawdzana jest warto�� kodu przes�anego przez intent.<br> 
	 * Je�li watro�� zgadza si� z warto�ci� zmiennej ScanResultContent to <br>
	 * telefon przechodzi w tryb cichy, zmniejszana jest jasno�� ekranu <br>
	 * (zmiana trybu jasno�ci z manualnego na automatyczny), wy��cza WiFi <br>
	 * oraz Bluetooth, nast�pnie wy�wietla monit o wy��czeniu ustawie�.<br>
	 * Je�li jednak zawarto�� zeskanowanego kodu nie zgadza si� z warto�ci� <br>
	 * zmiennej to u�ytkownik mo�e zeskanowa� kod jeszcze raz.</p>
	 */
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final String ScanResultContent="SETTINGS";
		//final int currentBrightnessValue;
		if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            // Handle successful scan
	            if(contents.equals(ScanResultContent)){
	           
	            		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	            	    audio.setRingerMode(0);//zmiana trybu na cichy.
	            		
	            		setAutoBrightness(true);//zmiania jasnosci ekranu na auto.
	            	    
	            		WifiManager wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
	            	    wifiManager.setWifiEnabled(false);//wy��czanie wifi.
	            	             	    
	            	    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
	            	    mBluetoothAdapter.disable();//wy��czanie bluetooth
	            	    
	            	    
	            		//Wyswietla monit o zmianie ustawien.
	            		Context context = getApplicationContext();
	            		CharSequence text = "Zmieniono ustawienia";
	            		int duration = Toast.LENGTH_LONG;

	            		Toast toast = Toast.makeText(context, text, duration);
	            		toast.show();
	            }
	            
	        } else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        	Context context = getApplicationContext();
        		CharSequence text1 = "Nast�pi� b��d!";
        		int duration = Toast.LENGTH_LONG;

        		Toast toast = Toast.makeText(context, text1, duration);
        		toast.show();
	        	
	        }
	    }
	}
}
