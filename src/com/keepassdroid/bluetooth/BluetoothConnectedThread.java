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
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * This thread runs during a connection with a remote device. It handles all
 * incoming and outgoing transmissions.
 */
public class BluetoothConnectedThread implements Runnable {

	private final BluetoothConnectionManager mService;
	private final Handler mHandler;
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;

	public BluetoothConnectedThread(BluetoothConnectionManager service, Handler handler, BluetoothSocket socket) {
		mService = service;
		mHandler = handler;
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			Log.e("BTConnectedThread", "temp sockets not created", e);
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {
		Log.i("BTConnectedThread", "BEGIN mConnectedThread");
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI Activity
				mHandler.obtainMessage(BluetoothConsts.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e("BTConnectedThread", "disconnected", e);
				mService.connectionLost();
				// Start the service over to restart listening mode
				mService.reset();
				break;
			}
		}
	}

	/**
	 * Write to the connected OutStream.
	 * 
	 * @param buffer
	 *            The bytes to write
	 */
	public void write(byte[] buffer) {
		try {
			mmOutStream.write(buffer);
			// Share the sent message back to the UI Activity
			mHandler.obtainMessage(BluetoothConsts.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
		} catch (IOException e) {
			Log.e("BTConnectedThread", "Exception during write", e);
		}
	}

	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.e("BTConnectedThread", "close() of connect socket failed", e);
		}
	}
}