package com.pesapal.sdk.model.card

import com.google.gson.annotations.SerializedName
import java.io.Serializable

internal data class BillingAddress(@SerializedName("line_1")
                          val line: String? = "",
                          @SerializedName("country_code")
                          val countryCode: String? = "",
                          @SerializedName("line_2")
                          val line2: String? = "",
                          @SerializedName("email_address")
                          val emailAddress: String? = "",
                          @SerializedName("city")
                          val city: String? = "",
                          @SerializedName("last_name")
                          val lastName: String? = "",
                          @SerializedName("phone_number")
                          val phoneNumber: String? = "",
                          @SerializedName("state")
                          val state: String? = "",
                          @SerializedName("middle_name")
                          val middleName: String? = "",
                          @SerializedName("postal_code")
                          val postalCode: String? = "",
                          @SerializedName("first_name")
                          val firstName: String? = "",
                          @SerializedName("zip_code")
                          val zipCode: String? = ""

): Serializable