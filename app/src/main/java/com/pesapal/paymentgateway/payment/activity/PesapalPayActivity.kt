package com.pesapal.paymentgateway.payment.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.ActivityPesapalPayBinding
import com.pesapal.paymentgateway.payment.fragment.mpesa.MainPesapalFragment
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel

class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding
    private var consumer_key: String = ""
    private var consumer_secret: String = ""
    private lateinit var viewModel: AppViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesapalPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        handleViewModel()
    }

    private fun initData(){
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        fetchSharedData()
        loadFragment(MainPesapalFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_control, fragment,"list")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun fetchSharedData(){
        val intent = intent
        if(intent != null){
            if(intent.getStringExtra("consumer_key") != null) {
                consumer_key = intent.getStringExtra("consumer_key")!!
            }
            if(intent.getStringExtra("consumer_secret") != null) {
                consumer_secret = intent.getStringExtra("consumer_secret")!!
            }
        }

        if(consumer_key != "" && consumer_secret != ""){
            val authRequestModel = AuthRequestModel(consumer_key,consumer_secret)
            authPayment(authRequestModel)
        }else{
            unableToAuth()
        }
    }

    private fun authPayment(authRequestModel: AuthRequestModel){
        viewModel.authPayment(authRequestModel)
    }

    private fun unableToAuth(){

    }

    private fun handleViewModel(){
        viewModel.authPaymentResponse.observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(" SUCCESS ", " ====> SUCCESS")
                }
                Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else ", " ====> else")
                }
            }
        }
    }

}