package com.pesapal.paymentgateway.payment.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentAuthorizingBinding
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.utils.PrefManager
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AppViewModel by activityViewModels()

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
        val authRequestModel = AuthRequestModel("qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW","osGQ364R49cXKeOYSpaOnT++rHs=")
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }


}