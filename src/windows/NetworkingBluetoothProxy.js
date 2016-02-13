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

cordova.commandProxy.add('NetworkingBluetooth', {
	getAdapterState: function (success, error, args) {
		if (args.length === 0 || typeof args[0] !== 'string' || args[0].length === 0) {
			error('Invalid arguments');
		} else {
			success(args[0]);
		}
	},

	requestEnable: function (success, error, args) {
	},

	enable: function (success, error, args) {
	},

	disable: function (success, error, args) {
	},

	getDevice: function (success, error, args) {
	},

	getDevices: function (success, error, args) {
	},

	startDiscovery: function (success, error, args) {
	},

	stopDiscovery: function (success, error, args) {
	},

	requestDiscoverable: function (success, error, args) {
	},

	connect: function (success, error, args) {
	},

	close: function (success, error, args) {
	},

	send: function (success, error, args) {
	},

	listenUsingRfcomm: function (success, error, args) {
	},

	// Events
	registerAdapterStateChanged: function (success, error, args) {
	},

	registerDeviceAdded: function (success, error, args) {
	},

	registerReceive: function (success, error, args) {
	},

	registerReceiveError: function (success, error, args) {
	},

	registerAccept: function (success, error, args) {
	},

	registerAcceptError: function (success, error, args) {
	}
});

