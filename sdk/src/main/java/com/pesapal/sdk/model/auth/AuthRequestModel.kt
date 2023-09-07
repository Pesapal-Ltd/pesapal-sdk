package com.pesapal.sdk.model.auth

import androidx.annotation.Keep

@Keep
data class AuthRequestModel(
     var consumer_key: String?,
     var consumer_secret: String?,
)