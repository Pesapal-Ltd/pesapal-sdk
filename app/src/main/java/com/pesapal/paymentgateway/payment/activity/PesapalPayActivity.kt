package com.pesapal.paymentgateway.payment.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.ActivityPesapalPayBinding
import com.pesapal.paymentgateway.payment.fragment.auth.AuthFragment
import com.pesapal.paymentgateway.payment.fragment.card.CardFragmentNewAddress
import com.pesapal.paymentgateway.payment.fragment.card.CardFragmentNewBilling
import com.pesapal.paymentgateway.payment.fragment.main.MainPesapalFragment
import com.pesapal.paymentgateway.payment.fragment.mpesa.pending.MpesaPendingFragment
import com.pesapal.paymentgateway.payment.fragment.mpesa.stk.MpesaPesapalFragment
import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnRequest
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.utils.PrefManager
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel

class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding
    private var consumer_key: String = ""
    private var consumer_secret: String = ""
    private var mobileMoneyRequest: MobileMoneyRequest? = null
    private var ipn_url: String = "https://supertapdev.pesapalhosting.com/"
    private var ipn_notification_type: String = "GET"

    private val viewModel: AppViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesapalPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        handleViewModel()
    }

    private fun initData(){
        fetchSharedData()
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
//        viewModel.authPayment(authRequestModel)
        viewModel.loadFragment("auth")
    }

    private fun registerIpn(){
        val registerIpnRequest = RegisterIpnRequest(ipn_url,ipn_notification_type)
        viewModel.registerIpn(registerIpnRequest)
    }

    private fun unableToAuth(){

    }

    private fun handleViewModel(){
        viewModel.authPaymentResponse.observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    val token = it.data?.token
                    PrefManager.setToken(token)
                    registerIpn()
                    Log.e(" SUCCESS ", " ====> SUCCESS")
                }
                Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }

        viewModel.registerIpnResponse.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(ipnId)
                    loadFragment(MainPesapalFragment())
                    Log.e(" SUCCESS ", " ====> SUCCESS")
                }
                Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else "," ====> register")
                }
            }
        }

        viewModel.loadPendingMpesa.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    mobileMoneyRequest  = it.data
                    viewModel.loadFragment("pending_mpesa")
                }
                else ->{}
            }
        }

        viewModel.loadFragment.observe(this){
            when(it.message){
                "auth" -> {
                    loadFragment(AuthFragment())
                }
                "choose" -> {
                    loadFragment(MainPesapalFragment())
                }
                "mpesa" -> {
                    loadFragment(MpesaPesapalFragment())
                }
                "pending_mpesa" -> {
                    if(mobileMoneyRequest != null) {
                        loadFragment(MpesaPendingFragment.newInstance(mobileMoneyRequest!!))
                    }else{
                        loadFragment(MpesaPendingFragment())
                    }
                }
                "card" -> {
                    loadFragment(CardFragmentNewAddress())
                }
                "card2" -> {
                    loadFragment(CardFragmentNewBilling())
                }
            }
        }


    }

}