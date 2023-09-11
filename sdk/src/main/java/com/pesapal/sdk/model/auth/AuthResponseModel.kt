package com.pesapal.sdk.model.auth

import com.pesapal.sdk.model.txn_status.TransactionError

internal data class AuthResponseModel(val expiryDate: String = "",
                             val error: TransactionError,
                             val message: String = "",
                             val token: String = "",
                             val status: String?)