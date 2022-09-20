package com.pesapal.paymentgateway.payment.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentPesapalMainBinding
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel

class MainPesapalFragment: Fragment() {

    private lateinit var binding: FragmentPesapalMainBinding
    private val viewModel: AppViewModel by activityViewModels()

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
            viewModel.loadFragment("mpesa")
//            findNavController().navigate(R.id.action_pesapalMainFragment_to_pesapalMpesaFragment)
        }

        binding.clCreditPayment.setOnClickListener {
            viewModel.loadFragment("card")

//            findNavController().navigate(R.id.action_pesapalMainFragment_to_pesapalCardFragmentAddress)
        }

    }


}