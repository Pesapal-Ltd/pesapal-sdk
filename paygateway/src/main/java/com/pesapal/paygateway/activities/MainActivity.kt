package com.pesapal.paygateway.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pesapal.paygateway.databinding.ActivityMainPesapalBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainPesapalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPesapalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}