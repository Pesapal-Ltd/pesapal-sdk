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
        myIntent.putExtra("consumer_key","qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW")
        myIntent.putExtra("consumer_secret","osGQ364R49cXKeOYSpaOnT++rHs=")
        myIntent.putExtra("amount",1)
        myIntent.putExtra("order_id","134212")
        myIntent.putExtra("currency","KES")
        myIntent.putExtra("accountNumber","1000101")
        myIntent.putExtra("callbackUrl","http://localhost:56522")
       startActivity(myIntent)
    }
}