package com.pesapal.sdk

import android.widget.Button

fun Button.setButtonEnabled(isEnabled: Boolean) {
    val alphaVisible = 1F
    val alphaInvisible = 0.2F

    if (isEnabled) {
        this.isEnabled = true
        this.alpha = alphaVisible
    } else {
        this.isEnabled = false
        this.alpha = alphaInvisible
    }
}
