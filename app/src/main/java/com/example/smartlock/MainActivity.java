package com.example.smartlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import co.aenterhy.toggleswitch.ToggleSwitchButton;

public class MainActivity extends AppCompatActivity
{
	private static final String PREF_NAME = "SmartLockAnv";
	private static final int PRIVATE_MODE = 0;
	String _ssid;
	String url = "http://192.168.0.100/toggle.php";
	StringRequest request;
	RequestQueue queue;

	ToggleSwitchButton toggle;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		queue = Volley.newRequestQueue(MainActivity.this);

		sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = sharedPreferences.edit();

		toggle = (ToggleSwitchButton) findViewById(R.id.toggle);

		WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null)
		{
			_ssid = info.getSSID();
		}
		// _ssid now is printing name of SSID in logs
		Log.v("SSID: ", "ssid: " + _ssid);
		set_ssid(_ssid);

		toggle.setOnTriggerListener(new ToggleSwitchButton.OnTriggerListener()
		{
			@Override
			public void toggledUp()
			{
				Log.v("toggled", "up");
				toggle();
			}

			@Override
			public void toggledDown()
			{
				Log.v("toggled", "down");
				toggle();
			}
		});
	}

	public String get_pref_ssid()
	{
		return sharedPreferences.getString("PREF_SSID", "pref_ssid");
	}

	public void set_pref_ssid(String ssid)
	{
		editor.putString("PREF_SSID", ssid);
		editor.apply();
		editor.commit();
	}

	public String get_ssid()
	{
		// default ssid is set to "ssid"
		return sharedPreferences.getString("SSID", "ssid");
	}

	public void set_ssid(String ssid)
	{
		editor.putString("SSID", ssid);
		editor.apply();
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	public void toggle()
	{
		if (get_pref_ssid().equals("pref_ssid"))
		{
			// default ssid of house not given
			Toast.makeText(MainActivity.this, "Please choose ssid of home", Toast.LENGTH_SHORT).show();
		}
		else
		{
			// Volley request
			request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response)
				{
					try
					{
						JSONObject root = new JSONObject(response);
						if (root.optString("status").equals("success"))
						{
							// status toggled.
							Toast.makeText(MainActivity.this, "Status toggled successfully", Toast.LENGTH_SHORT).show();
						}
						else
						{
							// some error
							Toast.makeText(MainActivity.this, "Some error", Toast.LENGTH_SHORT).show();
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					// some error
					Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
				}
			});

			queue.add(request);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		switch (id)
		{
			case R.id.action_ssid:
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("Enter ssid")
						.setCancelable(true)
						.setView(R.layout.ssid_input_layout)
						.setPositiveButton("Enter", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								// take input of ssid from user and use set_ssid method to store ssid in shared pref.
								EditText ssid = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.ssid_edittext);
								set_pref_ssid(ssid.getText().toString().trim());
							}
						}).show();

				return true;
		}
		return false;
	}
}