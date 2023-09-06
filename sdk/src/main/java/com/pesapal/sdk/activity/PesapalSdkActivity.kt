package com.pesapal.sdk.activity

import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.pesapal.sdk.R
import com.pesapal.sdk.databinding.ActivityPaymentSdkBinding

class PesapalSdkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSdkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSdkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clToolbar = binding.clToolbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fvPesapalPayment) as NavHostFragment
        navHostFragment.findNavController().run {
            clToolbar.setupWithNavController(this, AppBarConfiguration(graph))
        }
        clToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

    }


}