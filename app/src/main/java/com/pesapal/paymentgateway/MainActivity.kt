package com.pesapal.paymentgateway

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.pesapal.paymentgateway.payment.activity.PesapalPayActivity
import com.pesapal.paymentgateway.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClicks()
    }

    private fun initClicks(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding.btnPurchase.setOnClickListener {
            initPayment()
        }
    }

    private fun initPayment(){
       val myIntent = Intent(this, PesapalPayActivity::class.java)
       startActivity(myIntent)
    }
}