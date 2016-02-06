// Copyright 2016 Franco Bugnano
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package cordova.plugin.networking.bluetooth;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.SparseArray;

public class NetworkingBluetooth extends CordovaPlugin {
	public static final String TAG = "NetworkingBluetooth";
	public static final int REQUEST_ENABLE_BT = 1773;

	public BluetoothAdapter mBluetoothAdapter = null;
	public SparseArray<CallbackContext> mContextForRequest = new SparseArray<CallbackContext>();
	public CallbackContext mContextForAdapterStateChange;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (this.mBluetoothAdapter == null) {
			callbackContext.error("Device does not support Bluetooth");
			return false;
		}

		if (action.equals("coolMethod")) {
			String message = args.getString(0);
			this.coolMethod(message, callbackContext);
			return true;
		} else if (action.equals("registerAdapterStateChanged")) {
			this.mContextForAdapterStateChange = callbackContext;
			IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			cordova.getActivity().registerReceiver(this.mReceiver, filter);
			return true;
		} else if (action.equals("getAdapterState")) {
			this.getAdapterState(callbackContext, false);
			return true;
		} else if (action.equals("enable")) {
			if (!this.mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				this.prepareActivity(action, args, callbackContext, enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				callbackContext.success();
			}
			return true;
		} else if (action.equals("disable")) {
			if (this.mBluetoothAdapter.isEnabled()) {
				if (!this.mBluetoothAdapter.disable()) {
					callbackContext.error(0);
				} else {
					// TO DO -- The success callback should be called once the adapter has been succesfully disabled
					callbackContext.success();
				}
			} else {
				callbackContext.success();
			}
			return true;
		} else {
			callbackContext.error("Invalid action");
			return false;
		}
	}

	public void getAdapterState(CallbackContext callbackContext, boolean keepCallback) {
		try {
			JSONObject adapterState = new JSONObject();
			adapterState.put("address", this.mBluetoothAdapter.getAddress());
			adapterState.put("name", this.mBluetoothAdapter.getName());
			adapterState.put("discovering", this.mBluetoothAdapter.isDiscovering());
			adapterState.put("enabled", this.mBluetoothAdapter.isEnabled());
			adapterState.put("discoverable", this.mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, adapterState);
            pluginResult.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(pluginResult);
		} catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, 0);
            pluginResult.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(pluginResult);
		}
	}

	public void prepareActivity(String action, JSONArray args, CallbackContext callbackContext, Intent intent, int requestCode) {
		// First of all I have to check whether there already is another activity pending with the same requestCode
		CallbackContext oldContext = this.mContextForRequest.get(requestCode);
		this.mContextForRequest.remove(requestCode);

		// TO DO -- I have to cancel the activity here

		// If there already is another activity with this request code, call the error callback in order
		// to notify that the previous activity has been cancelled
		if (oldContext != null) {
			oldContext.error(1);
		}

		// Store the callbackContext, in order to send the result once the activity has been completed
		this.mContextForRequest.put(requestCode, callbackContext);

		// Store the callbackContext, in order to send the result once the activity has been completed
		cordova.startActivityForResult(this, intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		CallbackContext callbackContext = this.mContextForRequest.get(requestCode);
		this.mContextForRequest.remove(requestCode);

		if (callbackContext != null) {
			if (resultCode == Activity.RESULT_OK) {
				callbackContext.success();
			} else {
				callbackContext.error(0);
			}
		} else {
			Log.e(TAG, "BUG: onActivityResult -- (callbackContext == null)");
		}
	}

	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				getAdapterState(mContextForAdapterStateChange, true);
			}
		}
	};

	private void coolMethod(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}
}

