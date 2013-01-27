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

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * This thread runs while attempting to make an outgoing connection with a
 * device. It runs straight through; the connection either succeeds or fails.
 */
public class BluetoothConnectThread implements Runnable {

	private final BluetoothConnectionManager mService;
	private final BluetoothAdapter mLocalDevice;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;

	public BluetoothConnectThread(BluetoothConnectionManager service, BluetoothAdapter localDevice, BluetoothDevice device) {
		this.mService = service;
		this.mLocalDevice = localDevice;
		this.mmDevice = device;

		BluetoothSocket tmp = null;

		// Get a BluetoothSocket for a connection with the
		// given BluetoothDevice
		try {
			tmp = device.createRfcommSocketToServiceRecord(BluetoothConsts.BLUETOOTH_UUID);
		} catch (IOException e) {
			Log.e("BTConnectThread", "Socket Type: Secure create() failed", e);
		}
		mmSocket = tmp;
	}

	public void run() {
		Log.i("BTConnectThread", "BEGIN mConnectThread SocketType:Secure");

		// Always cancel discovery because it will slow down a connection
		mLocalDevice.cancelDiscovery();

		// Make a connection to the BluetoothSocket
		try {
			// This is a blocking call and will only return on a
			// successful connection or an exception
			mmSocket.connect();
		} catch (IOException e) {
			// Close the socket
			try {
				mmSocket.close();
			} catch (IOException e2) {
				Log.e("BTConnectThread", "unable to close() Secure socket during connection failure", e2);
			}
			mService.connectionFailed();
			return;
		}

		// Start the connected thread
		mService.connected(mmSocket, mmDevice);
	}

	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.e("BTConnectThread", "close() of connect Secure socket failed", e);
		}
	}
}