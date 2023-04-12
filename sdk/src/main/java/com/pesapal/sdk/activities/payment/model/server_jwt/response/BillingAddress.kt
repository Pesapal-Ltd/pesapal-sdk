package com.pesapal.sdk.activities.payment.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class BillingAddress(@SerializedName("Phone1")
                          val phone: String = "",
                          @SerializedName("FirstName")
                          val firstName: String = "",
                          @SerializedName("State")
                          val state: String = "",
                          @SerializedName("FullName")
                          val fullName: String = "",
                          @SerializedName("Address2")
                          val address: String = "",
                          @SerializedName("PostalCode")
                          val postalCode: String = "",
                          @SerializedName("LastName")
                          val lastName: String = "",
                          @SerializedName("Address1")
                          val address1: String = "",
                          @SerializedName("City")
                          val city: String = "",
                          @SerializedName("MiddleName")
                          val middleName: String = "",
                          @SerializedName("CountryCode")
                          val countryCode: String = "")