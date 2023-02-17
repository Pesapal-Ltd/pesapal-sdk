package com.pesapal.paygateway.activities.payment.model.check3ds

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CheckDSecureRequest(@SerializedName("card_details")
                               val cardDetails: CardDetails3Ds,
                               @SerializedName("billing_details")
                               val billingDetails: BillingDetails,
                               @SerializedName("amount")
                               val amount: BigDecimal = BigDecimal.ONE,
                               @SerializedName("merchant_reference")
                               val merchantReference: String = "",
                               @SerializedName("reference_field")
                               val referenceField: String = "",
                               @SerializedName("currency")
                               val currency: String = "",
                               @SerializedName("processor")
                               val processor: Int = 0,
                               @SerializedName("mcc_code")
                               val mccCode: String = "",
                               @SerializedName("device_channel")
                               val deviceChannel: String = "",


)