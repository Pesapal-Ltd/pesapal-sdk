package com.pesapal.sdk.utils.sec.device.fingerprinting

import com.pesapal.sdk.BuildConfig


class DeviceFingerPrintModel(
    var manufacturer: String = "",
    var imei: String = "",
    var device_id: String = "",
    var device_security: String = "",
    var deviceInfo: String = "",
    var channel: Int = 4,
    var version: Int = 1,
)