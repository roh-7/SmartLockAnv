package com.example.smartlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity
{
	String _ssid;

	private static final String PREF_NAME = "SmartLockAnv";
	private static final int PRIVATE_MODE = 0;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
		editor = sharedPreferences.edit();

		WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null)
		{
			_ssid = info.getSSID();
		}
		// _ssid now is printing name of SSID in logs
		Log.v("SSID: ", "ssid: " + _ssid);
		set_ssid(_ssid);
	}

	public void set_ssid(String ssid)
	{
		editor.putString("SSID",ssid);
		editor.apply();
		editor.commit();
	}

	public String get_ssid()
	{
		// default ssid is set to "ssid"
		return sharedPreferences.getString("SSID","ssid");
	}
}