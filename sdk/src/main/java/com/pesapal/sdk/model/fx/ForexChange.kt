package com.pesapal.sdk.model.fx

import java.math.BigDecimal

data class ForexChange (
     var source_currency: String = "",
     var destination_currency: String = "",
     var amount:BigDecimal = BigDecimal.ZERO
)