/*
 * Copyright 2009 Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.keepass;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bouncycastle1.crypto.InvalidCipherTextException;

import com.android.keepass.keepasslib.InvalidKeyFileException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends Activity {

	public static final String LAST_FILENAME = "lastFile";
	public static final String LAST_KEYFILE = "lastKey";
	
	private static final int MENU_HOMEPAGE = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);

		Button confirmButton = (Button) findViewById(R.id.pass_ok);
		confirmButton.setOnClickListener(new ClickHandler(this));
		
		loadDefaultPrefs();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Clear password on Database state
		setEditText(R.id.pass_password, "");
		Database.clear(); 
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}

	private void loadDefaultPrefs() {
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		String lastFile = settings.getString(LAST_FILENAME, "");
		String lastKey = settings.getString(LAST_KEYFILE,"");
		
		if (lastFile == "") {
			lastFile = "/sdcard/keepass/keepass.kdb";
		}
		
		setEditText(R.id.pass_filename, lastFile);
		setEditText(R.id.pass_keyfile, lastKey);
	}
	
	private void saveDefaultPrefs() {
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LAST_FILENAME, getEditText(R.id.pass_filename));
		editor.putString(LAST_KEYFILE, getEditText(R.id.pass_keyfile));
		editor.commit();
	}
	
	
	private void errorMessage(CharSequence text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void errorMessage(int resId)
	{
		Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
	}
	
	private class ClickHandler implements View.OnClickListener {
		private Activity mAct;
				
		ClickHandler(Activity act) {
			mAct = act;
		}
		
		public void onClick(View view) {
			String pass = getEditText(R.id.pass_password);
			String key = getEditText(R.id.pass_keyfile);
			if ( pass.length() == 0 && key.length() == 0 ) {
				errorMessage(R.string.error_nopass);
				return;
			}
			
			try {

				Database.LoadData(getEditText(R.id.pass_filename), pass, key);
				saveDefaultPrefs();
				GroupActivity.Launch(mAct, null);

			} catch (InvalidCipherTextException e) {
				errorMessage(R.string.InvalidPassword);
			} catch (FileNotFoundException e) {
				errorMessage(R.string.FileNotFound);
			} catch (IOException e) {
				errorMessage("Unknown error.");
			} catch (InvalidKeyFileException e) {
				errorMessage(e.getMessage());
			}
		}			
	}
	
	private String getEditText(int resId) {
		EditText te =  (EditText) findViewById(resId);
		assert(te == null);
		
		if (te != null) {
			return te.getText().toString();
		} else {
			return "";
		}
	}
	
	private void setEditText(int resId, String str) {
		EditText te =  (EditText) findViewById(resId);
		assert(te == null);
		
		if (te != null) {
			te.setText(str);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_HOMEPAGE, 0, R.string.menu_homepage);
		menu.findItem(MENU_HOMEPAGE).setIcon(android.R.drawable.ic_menu_upload);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case MENU_HOMEPAGE:
			Util.gotoUrl(this, getText(R.string.homepage).toString());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

}