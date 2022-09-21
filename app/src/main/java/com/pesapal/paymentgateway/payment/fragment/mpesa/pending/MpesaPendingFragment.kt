package com.pesapal.paymentgateway.payment.fragment.mpesa.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentMpesaPendingBinding
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel

class MpesaPendingFragment : Fragment() {

    private lateinit var binding: FragmentMpesaPendingBinding
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMpesaPendingBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        handleClick()
    }

    private fun handleClick(){
        binding.btnSend.setOnClickListener {
            handleConfirmation()
        }
        binding.btnSendLipa.setOnClickListener {
            handleConfirmation()
        }

        binding.btnResendPrompt.setOnClickListener {
            binding.clLipaNaMpesa.visibility = View.GONE
            binding.clStkPush.visibility = View.VISIBLE
        }

        binding.btnLipa.setOnClickListener {
            binding.clStkPush.visibility = View.GONE
            binding.clLipaNaMpesa.visibility = View.VISIBLE
        }


    }

    private fun handleConfirmation(){

    }

    private fun handleResend(){
        viewModel.loadFragment("mpesa")
    }


}