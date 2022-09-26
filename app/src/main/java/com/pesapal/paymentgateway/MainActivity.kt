package com.pesapal.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.pesapal.paymentgateway.databinding.ActivityMainBinding
import com.pesapal.paymentgateway.payment.activity.PesapalPayActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var PAYMENT_REQUEST = 100001

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
        myIntent.putExtra("order_id","122323253")
        myIntent.putExtra("currency","KES")
        myIntent.putExtra("accountNumber","1000101")
        myIntent.putExtra("PAYMENT_REQUEST","PAYMENT_REQUEST")
        myIntent.putExtra("callbackUrl","http://localhost:56522")
        startActivityForResult(myIntent,PAYMENT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")
                Log.e(" result ", " ==> result"+result)
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }


}