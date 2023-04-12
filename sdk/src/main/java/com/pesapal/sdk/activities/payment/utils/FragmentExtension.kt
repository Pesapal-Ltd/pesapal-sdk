package com.pesapal.sdk.activities.payment.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

object FragmentExtension {

    fun Fragment.hideKeyboard(): Boolean {
        try {
            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return false
    }



}