package com.pesapal.sdk.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.databinding.ActivityPaymentSdkBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PesapalSdkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSdkBinding

    private val pesapalSdkViewModel: PesapalSdkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Sdkapp.setContextInstance(this)

    }

    companion object {
        val STATUS_COMPLETED = "COMPLETED"
        val STATUS_CANCELLED = "CANCELLED"
    }



}