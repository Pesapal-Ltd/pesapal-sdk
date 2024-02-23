package com.pesapal.sdk.activity

import androidx.lifecycle.ViewModel

class PesapalSdkViewModel : ViewModel() {
    var merchantName: String? = ""
    var trackingId: String? = ""
    var orderID:String? = ""
}