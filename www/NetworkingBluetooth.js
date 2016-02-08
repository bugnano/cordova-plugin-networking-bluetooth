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

var
	exec = require('cordova/exec'),
	channel = require('cordova/channel'),
	Event = require('cordova-plugin-networking-bluetooth.event')
;

exports.getAdapterState = function (success, error) {
	exec(success, error, 'NetworkingBluetooth', 'getAdapterState', []);
};

exports.requestEnable = function (success, error) {
	exec(success, error, 'NetworkingBluetooth', 'requestEnable', []);
};

exports.enable = function (success, error) {
	exec(success, error, 'NetworkingBluetooth', 'enable', []);
};

exports.disable = function (success, error) {
	exec(success, error, 'NetworkingBluetooth', 'disable', []);
};

// Events
exports.onAdapterStateChanged = Object.create(Event);
exports.onAdapterStateChanged.init();

channel.onCordovaReady.subscribe(function() {
	exec(function (adapterState) {
		exports.onAdapterStateChanged.fire(adapterState);
	}, null, 'NetworkingBluetooth', 'registerAdapterStateChanged', []);
});

