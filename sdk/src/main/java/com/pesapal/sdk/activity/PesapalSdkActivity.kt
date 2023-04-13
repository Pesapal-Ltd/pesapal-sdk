package com.pesapal.sdk.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pesapal.sdk.databinding.ActivityPaymentSdkBinding

class PesapalSdkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSdkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}