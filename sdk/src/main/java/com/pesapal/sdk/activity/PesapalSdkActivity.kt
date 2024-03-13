package com.pesapal.sdk.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.databinding.ActivityPaymentSdkBinding
import com.pesapal.sdk.utils.sec.initializeSecurity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PesapalSdkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSdkBinding

    private val pesapalSdkViewModel: PesapalSdkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(initializeSecurity(this)) {
            binding = ActivityPaymentSdkBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Sdkapp.setContextInstance(this)
        }

    }

    companion object {
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"
    }



}