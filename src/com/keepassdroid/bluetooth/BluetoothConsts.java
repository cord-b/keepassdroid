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

import java.util.UUID;

public class BluetoothConsts {

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Extras for invoking the BluetoothAutotypeActivity
	public static final String BUNDLE_USERNAME = "un";
	public static final String BUNDLE_PASSWORD = "pw";

	// Unique UUID for this application
	public static final UUID BLUETOOTH_UUID =
			UUID.fromString("d892d4d2-6511-48cf-b729-806be1ae7f01");

	// we're doing nothing
	public static final int STATE_NONE = 0;
	// now initiating an outgoing connection
	public static final int STATE_CONNECTING = 1;
	// now connected to a remote device
	public static final int STATE_CONNECTED = 2;

}
