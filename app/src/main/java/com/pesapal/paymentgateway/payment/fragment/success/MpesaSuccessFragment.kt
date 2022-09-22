package com.pesapal.paymentgateway.payment.fragment.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pesapal.paymentgateway.databinding.FragmentMpesaPaymentSuccessBinding
import com.pesapal.paymentgateway.payment.model.mobile_money.TransactionStatusResponse
import java.math.BigDecimal

class MpesaSuccessFragment : Fragment() {

    private lateinit var binding: FragmentMpesaPaymentSuccessBinding
    private lateinit var transactionStatusResponse: TransactionStatusResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMpesaPaymentSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        handleDisplay()
        handleClicks()
    }

    private fun handleClicks(){
        binding.btnDone.setOnClickListener {

        }

    }

    private fun handleDisplay(){
        binding.tvUserPhoneNumber.text = transactionStatusResponse.paymentAccount
        binding.tvUserAmount.text = transactionStatusResponse.currency +" "+BigDecimal(transactionStatusResponse.amount).setScale(2)
        binding.tvId.text = transactionStatusResponse.confirmationCode
    }


    companion object{
        fun newInstance(transactionStatusResponse: TransactionStatusResponse): MpesaSuccessFragment{
            val fragment = MpesaSuccessFragment()
            fragment.transactionStatusResponse = transactionStatusResponse
            return fragment
        }
    }

}