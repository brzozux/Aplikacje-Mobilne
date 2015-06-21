package com.example.szyfraes;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SzyfrowanieAES extends Activity {
	static final String TAG = "Szyfrowanie";
	private Button Szyfr_wl;
	private EditText txt1;
	private TextView wynik, wynikd;
	String txt, enctxt;
	SecretKeySpec sks = null;
	byte[] encodedBytes = null;
	byte[] decodedBytes = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_szyfrowanieaes);
		
		//Szyfr_wl  = (Button) findViewById (R.id.btnszyfr);
		txt1 = (EditText) findViewById (R.id.deszyfredit);
		//wynikd = (TextView) findViewById (R.id.textView3);
		wynik = (TextView) findViewById (R.id.textView2);
		
	}
	public SecretKeySpec generujklucz() {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed("dane".getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec ((kg.generateKey()).getEncoded(), "AES");
		} catch (Exception e) {
			Log.e(TAG, "Blad podczas generowania klucza" );
		}
		return sks;
	}
	public void Szyfrujw (View v){
		txt = txt1.getText().toString();
		generujklucz();
		Szyfrowanie();
	}
	
	public byte[] Szyfrowanie(){
		
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, sks);
			encodedBytes = c.doFinal(txt.getBytes());
		}
		catch (Exception e) {
			Log.e(TAG, "Blad Szyfrowania!");
		}
		enctxt = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
		wynik.setText(enctxt);
		return encodedBytes;
	}
	
	public void Deszyfrujw(View v){
		Deszyfrowanie();
	}
	
	public byte[] Deszyfrowanie(){
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sks);
			decodedBytes = c.doFinal(encodedBytes);
		}
		catch (Exception e) {
			Log.e(TAG, "AES decryption error");
		}
		wynik.setText(new String(decodedBytes));
		return decodedBytes;
	}
	public void Kopiowanie (View v){
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Skopiowano do schowka", enctxt);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(SzyfrowanieAES.this, "Pomyœlnie skopiowano do schowka!",Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.szyfrowanie, menu);
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
