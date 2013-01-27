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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread for connecting with a device,
 * and a thread for performing data transmissions when connected.
 */
public class BluetoothConnectionManager {

	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private BluetoothConnectThread mConnectThread;
	private BluetoothConnectedThread mConnectedThread;
	private int mState;

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothConnectionManager(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = BluetoothConsts.STATE_NONE;
		mHandler = handler;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int newState) {
		int currentState = this.mState;
		if (currentState != newState) {
			mState = newState;
			// Give the new state to the Handler so the UI Activity can update
			mHandler.obtainMessage(BluetoothConsts.MESSAGE_STATE_CHANGE, newState, -1).sendToTarget();
		}
	}

	/** Return the current connection state. */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device) {
		cancelThreads();
		// Start the thread to connect with the given device
		mConnectThread = new BluetoothConnectThread(this, mAdapter, device);
		new Thread(mConnectThread).start();
		setState(BluetoothConsts.STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		cancelThreads();

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new BluetoothConnectedThread(this, mHandler, socket);
		new Thread(mConnectedThread).start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(BluetoothConsts.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothConsts.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(BluetoothConsts.STATE_CONNECTED);
	}

	/** Stop all threads and set state back to none */
	public synchronized void reset() {
		cancelThreads();
		setState(BluetoothConsts.STATE_NONE);
	}

	public synchronized void shutdown() {
		reset();
	}

	protected synchronized void cancelThreads() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see BluetoothConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		BluetoothConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != BluetoothConsts.STATE_CONNECTED) {
				return;
			}
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	protected void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoothConsts.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothConsts.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		reset();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	protected void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoothConsts.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothConsts.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		reset();
	}

}
