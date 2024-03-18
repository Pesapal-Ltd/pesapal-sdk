package com.pesapal.sdk.model.fingerprinting

import com.pesapal.sdk.BuildConfig


internal data class DeviceFingerPrintModel(
    var manufacturer: String = "",
    var imei: String = "",
    var device_id: String = "",
    var device_security: String = "",
    var deviceInfo: String = "",
    var channel: Int = 4,
    var version: Int = BuildConfig.VERSION_CODE_MAN,
)