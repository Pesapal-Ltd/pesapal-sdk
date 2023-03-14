package com.pesapal.paygateway.activities.payment.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.databinding.FragmentAuthorizingBinding
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.payment.PaymentDetails
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var paymentDetails : PaymentDetails

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
        val authRequestModel = AuthRequestModel(paymentDetails.consumer_key,paymentDetails.consumer_secret)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }


    companion object{
        fun newInstance(paymentDetails: PaymentDetails): AuthFragment {
            val fragment = AuthFragment()
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }

}