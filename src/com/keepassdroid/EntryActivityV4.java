/*
 * Copyright 2010-2011 Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
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
package com.keepassdroid;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.keepass.R;
import com.keepassdroid.bluetooth.BluetoothAutotypeActivity;
import com.keepassdroid.bluetooth.BluetoothConsts;
import com.keepassdroid.database.PwEntryV4;
import com.keepassdroid.view.EntrySection;

public class EntryActivityV4 extends EntryActivity {

	@Override
	protected void setEntryView() {
		setContentView(R.layout.entry_view_v4);
	}

	@Override
	protected void setupEditButtons() {
		// No edit buttons yet
	}

	@Override
	protected void fillData() {
		super.fillData();

		ViewGroup group = (ViewGroup) findViewById(R.id.extra_strings);

		final PwEntryV4 entry = (PwEntryV4) mEntry;

		// Display custom strings
		if (entry.strings.size() > 0) {
			for (Map.Entry<String, String> pair : entry.strings.entrySet()) {
				String key = pair.getKey();

				if (!PwEntryV4.IsStandardString(key)) {
					View view = new EntrySection(this, null, key, pair.getValue());
					group.addView(view);
				}
			}
		}

		Button button = (Button) this.findViewById(R.id.button_bluetooth);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bluetoothActivity(entry.getUsername(), entry.getPassword());
			}
		});

	}

	protected void bluetoothActivity(String username, String password) {
		Intent intent = new Intent(this, BluetoothAutotypeActivity.class);
		intent.putExtra(BluetoothConsts.BUNDLE_USERNAME, username);
		intent.putExtra(BluetoothConsts.BUNDLE_PASSWORD, password);
		startActivity(intent);
	}

}
