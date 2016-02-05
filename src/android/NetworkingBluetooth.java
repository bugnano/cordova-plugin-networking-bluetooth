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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

public class NetworkingBluetooth extends CordovaPlugin {
	public static final String TAG = "NetworkingBluetooth";
	public static final int REQUEST_ENABLE_BT = 1773;

	public BluetoothAdapter mBluetoothAdapter = null;
	public SparseArray<CallbackContext> mContextForRequest;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.mContextForRequest = new SparseArray<CallbackContext>();
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
		} else if (action.equals("isEnabled")) {
			if (this.mBluetoothAdapter.isEnabled()) {
				callbackContext.success(1);
			} else {
				callbackContext.success(0);
			}
			return true;
		} else if (action.equals("enable")) {
			if (!this.mBluetoothAdapter.isEnabled()) {
				// TO DO -- this.mContextForRequest may already contain a context,
				// in which case it should return an error (operation cancelled)
				// Still, the SparseArray as it is does not work (yet)
				this.mContextForRequest.put(REQUEST_ENABLE_BT, callbackContext);
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				cordova.startActivityForResult(this, enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				callbackContext.success();
			}
			return true;
		} else {
			callbackContext.error("Invalid action");
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		JSONArray args = new JSONArray();
		args.put(requestCode);
		args.put(resultCode);

		CallbackContext callbackContext = this.mContextForRequest.get(resultCode);
		if (callbackContext != null) {
			if (resultCode == Activity.RESULT_OK) {
				callbackContext.success();
			} else {
				callbackContext.error(0);
			}
		} else {
			Log.e(TAG, "BUG: onActivityResult -- (callbackContext == null)");
		}
		this.mContextForRequest.remove(resultCode);
	}

	private void coolMethod(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}
}

