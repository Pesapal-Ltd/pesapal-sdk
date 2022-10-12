package com.pesapal.paygateway.activities.payment.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.activities.payment.fragment.mpesa.stk.MpesaPesapalFragment
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.databinding.FragmentAuthorizingBinding
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import java.math.BigDecimal

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AppViewModel by activityViewModels()
    private var consumerKey : String = ""
    private var consumerSecret : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        PrefManager.setToken(null)
        val authRequestModel = AuthRequestModel(consumerKey,consumerSecret)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }


    companion object{
        fun newInstance(consumer_key: String, consumer_secret: String, ): AuthFragment {
            val fragment = AuthFragment()
            fragment.consumerKey = consumer_key
            fragment.consumerSecret = consumer_secret
            return fragment
        }
    }

}