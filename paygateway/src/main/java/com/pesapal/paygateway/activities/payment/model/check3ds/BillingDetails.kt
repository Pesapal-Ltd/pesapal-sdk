package com.pesapal.paygateway.activities.payment.model.check3ds

import com.google.gson.annotations.SerializedName

data class BillingDetails(@SerializedName("country")
                          val country: String = "",
                          @SerializedName("city")
                          val city: String = "",
                          @SerializedName("street")
                          val street: String? = "",
                          @SerializedName("last_name")
                          val lastName: String = "",
                          @SerializedName("phone_number")
                          val phoneNumber: String = "",
                          @SerializedName("currency")
                          val currency: String? = "",
                          @SerializedName("state")
                          val state: String? = null,
                          @SerializedName("postal_code")
                          val postalCode: String? = "",
                          @SerializedName("first_name")
                          val firstName: String? = "",
                          @SerializedName("email")
                          val email: String? = "")