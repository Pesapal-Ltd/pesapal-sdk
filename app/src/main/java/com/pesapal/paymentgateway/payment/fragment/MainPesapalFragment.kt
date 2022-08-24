package com.pesapal.paymentgateway.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.pesapal.paygateway.R
import com.pesapal.paygateway.databinding.FragmentPesapalMainBinding

class MainPesapalFragment: Fragment() {

    private lateinit var binding: FragmentPesapalMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPesapalMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        binding.clMpesaPayment.setOnClickListener {
            findNavController().navigate(R.id.action_pesapalMainFragment_to_pesapalMpesaFragment)
        }

        binding.clCreditPayment.setOnClickListener {

        }

    }


}