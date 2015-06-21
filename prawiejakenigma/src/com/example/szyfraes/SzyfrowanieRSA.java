package com.example.szyfraes;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.support.v7.app.ActionBarActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

public class SzyfrowanieRSA extends ActionBarActivity {
	static final String TAG = "SzyfrowanieRSA";
	private Button Deszyfr_wl;
	private EditText txtrsa;
	private TextView wynik;
	String enctxt, txt;
	Key publicKey = null;
	Key privateKey = null;
	byte[] encodedBytes = null;
	byte[] decodedBytes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_szyfrowaniersa);
		
		txtrsa = (EditText) findViewById (R.id.deszyfredit);
		wynik = (TextView) findViewById (R.id.textView2);

	}
	public void Szyfrujrsa (View v){		
		txt = txtrsa.getText().toString();
		Generujkluczrsa();
		Szyfrowaniersa();
	}
	
	public void Deszyfrujrsa (View v){
		Deszyfrowaniersa();
	}
	
	public void Kopiowaniersa (View v){
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Skopiowano do schowka", enctxt);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(SzyfrowanieRSA.this, "Pomyœlnie skopiowano do schowka!",Toast.LENGTH_LONG).show();
	}
	
	public Key Generujkluczrsa(){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			KeyPair kp = kpg.genKeyPair();
			publicKey = kp.getPublic();
			privateKey = kp.getPrivate();		
		} catch (Exception e) {
			Log.e(TAG, "RSA key pair error");
		}
		return privateKey;
	}
	
	public byte[] Szyfrowaniersa(){
		try {
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.ENCRYPT_MODE, privateKey);
			encodedBytes = c.doFinal(txt.getBytes());
		} catch (Exception e) {
			Log.e(TAG, "RSA encryption error");
		}
		enctxt = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
		wynik.setText(enctxt);
		return encodedBytes;
	}
	
	public byte[] Deszyfrowaniersa(){
		try {
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE, publicKey);
			decodedBytes = c.doFinal(encodedBytes);
		} catch (Exception e) {
			Log.e(TAG, "RSA decryption error");
		}
		wynik.setText(new String(decodedBytes));
		return decodedBytes;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deszyfrowanie, menu);
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
