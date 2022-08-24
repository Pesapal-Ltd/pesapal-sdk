package com.pesapal.paymentgateway.payment.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.ActivityPesapalPayBinding

class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesapalPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}