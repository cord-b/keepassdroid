/*
 * Copyright 2009 Brian Pellin.
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
package com.keepassdroid.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.keepass.R;

public class ServiceHandler extends Handler {

	private final BluetoothAutotypeActivity mActivity;
	private String mConnectedDeviceName;

	public ServiceHandler(BluetoothAutotypeActivity activity) {
		this.mActivity = activity;
		this.mConnectedDeviceName = "";
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case BluetoothConsts.MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case BluetoothConsts.STATE_CONNECTED:
				mActivity.setStatus(mActivity.getString(R.string.title_connected_to, mConnectedDeviceName));
				break;
			case BluetoothConsts.STATE_CONNECTING:
				mActivity.setStatus(R.string.title_connecting);
				break;
			case BluetoothConsts.STATE_NONE:
				mActivity.setStatus(R.string.title_not_connected);
				break;
			}
			break;
		case BluetoothConsts.MESSAGE_WRITE:
			break;
		case BluetoothConsts.MESSAGE_READ:
			break;
		case BluetoothConsts.MESSAGE_DEVICE_NAME:
			// save the connected device's name
			mConnectedDeviceName = msg.getData().getString(BluetoothConsts.DEVICE_NAME);
			Toast.makeText(mActivity.getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
			break;
		case BluetoothConsts.MESSAGE_TOAST:
			Toast.makeText(mActivity.getApplicationContext(), msg.getData().getString(BluetoothConsts.TOAST),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
