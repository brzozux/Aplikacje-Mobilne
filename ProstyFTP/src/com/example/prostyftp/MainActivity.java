package com.example.prostyftp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private MyFTPClientFunctions ftpclient = null;
	private Button Login, btnExpl;
	private EditText Adres, User, Pass;
	private EditText Port;
	private ProgressDialog pd;
	private String[] fileList;
	private ListView listview;
	String katalog;
	boolean status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Adres = (EditText) findViewById (R.id.AdresIP);
		Port = (EditText) findViewById (R.id.Port);
		User = (EditText) findViewById (R.id.User);
		Pass = (EditText) findViewById (R.id.Pass);
		Login = (Button) findViewById(R.id.Login);
		listview = (ListView) findViewById (R.id.listView1);
		ftpclient = new MyFTPClientFunctions();
	}
	
	public void Zaloguj (View v){
		if (isOnline(MainActivity.this)) {
			new LoginTask().execute();
			}
		else {
			Toast.makeText(MainActivity.this,"Sprawdz polaczenie internetowe!", Toast.LENGTH_LONG).show();
			}
		}
	
	private boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
			}
		return false;
		}
	
	private class LoginTask extends AsyncTask<Void,Void,Void>{
		
		final String adres = Adres.getText().toString().trim();
		final String porttym = Port.getText().toString().trim();
		final int portp = Integer.valueOf(Integer.parseInt(porttym));
		final String user = User.getText().toString().trim();
		final String pass = Pass.getText().toString().trim();
		
		protected void onPreExecute(){
			if (adres != null && adres.length() == 0) {
				Toast.makeText(MainActivity.this, "Wprowadz Adres IP serwera!", Toast.LENGTH_LONG).show();
			} 
			else if (porttym != null && porttym.length() == 0) {
				Toast.makeText(MainActivity.this, "Wprowadz Port serwera!",Toast.LENGTH_LONG).show();
			} 
			else if (user != null && user.length() == 0) {
				Toast.makeText(MainActivity.this, "Wprowadz Nazwe Uzytkownika!",Toast.LENGTH_LONG).show();
			} 
			else if (pass != null && pass.length() == 0) {
				Toast.makeText(MainActivity.this, "Wprowadz Haslo!",Toast.LENGTH_LONG).show();
			} 
			else {	
			pd = ProgressDialog.show(MainActivity.this, "", "Laczenie...",true, false);
			}
		}
		
		@Override
		protected Void doInBackground(Void... arg0)  {
			status = false;
			try {
				
				status = ftpclient.ftpConnect(adres, user, pass, portp);
				fileList = ftpclient.ftpPrintFilesList("/");
				
				Thread.sleep(1000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			pd.dismiss();
			ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, fileList);
			listview.setAdapter(fileListAdapter);
		}
	}
	
	public void NowyFolder (View v){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage("Nazwa katalogu: ");
		final EditText input = new EditText (this);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				katalog = input.getText().toString();
				new UtworzKat().execute(katalog);
				 }
			});
			  alert.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			  }
			});
		alert.show();
		}
	
	
	private class UtworzKat extends AsyncTask<String,Void,Void>{

		@Override
		protected Void doInBackground(String... params) {
			try {
				ftpclient.ftpMakeDirectory(katalog);
				fileList = ftpclient.ftpPrintFilesList("/");
				Thread.sleep(1000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			pd.dismiss();
			ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, fileList);
			listview.setAdapter(fileListAdapter);
		}
		
	}
	
	public void Odswiez (View v){
		new Refresh().execute(status);
	}
	
	private class Refresh extends AsyncTask<Boolean,Void,Void>{
		
		@Override
		protected Void doInBackground(Boolean... arg0)  {
			//boolean status = false;
			if (status){
			try {
				
				fileList = ftpclient.ftpPrintFilesList("/");
				
				Thread.sleep(1000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			}
			return null;
			
		}
		
		@Override
		protected void onPostExecute(Void result){
			pd.dismiss();
			ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item, fileList);
			listview.setAdapter(fileListAdapter);
		}
	}
	
	public void Wyloguj (View v){
		new Logout().execute();
	}
	
	private class Logout extends AsyncTask<Void,Void,Void>{
		
		@Override
		protected Void doInBackground(Void... arg0)  {
			try {
				ftpclient.ftpDisconnect();
				Thread.sleep(1000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			View v = findViewById(R.id.listView1);
			v.setVisibility(View.GONE);
		}
	}
	
	public void Aplold (View v){
		String[] listaplik = new File(".").list();
		
		if (status){
			final Dialog dialog = new Dialog(MainActivity.this);
			dialog.setContentView(R.layout.activity_aplold);
			dialog.setTitle("/ Directory File List");

			//TextView tvHeading = (TextView) dialog.findViewById(R.id.tvListHeading);
			//tvHeading.setText(":: File List ::");

			if (listaplik != null && listaplik.length > 0) {
				ListView listView = (ListView) dialog.findViewById(R.id.listUp);
				ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, listaplik);
				listView.setAdapter(fileListAdapter);
			} else {
				//tvHeading.setText(":: No Files ::");
			}

			Button dialogButton = (Button) dialog.findViewById(R.id.btnUp);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		else {
			Toast.makeText(MainActivity.this,"Polacz sie z serwerem!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
