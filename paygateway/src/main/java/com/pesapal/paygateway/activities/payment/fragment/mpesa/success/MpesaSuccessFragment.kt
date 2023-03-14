package com.pesapal.paygateway.activities.payment.fragment.mpesa.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.databinding.FragmentMpesaPaymentSuccessBinding
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel

class MpesaSuccessFragment : Fragment() {

    private lateinit var binding: FragmentMpesaPaymentSuccessBinding
    private lateinit var transactionStatusResponse: TransactionStatusResponse
    private val viewModel: AppViewModel by activityViewModels()

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
            viewModel.handlePaymentStatus("COMPLETED")
        }
        binding.closeBtn.setOnClickListener {
            viewModel.handlePaymentStatus("COMPLETED")
        }

    }

    private fun handleDisplay(){
        binding.tvId.text = "TXN ID : "+transactionStatusResponse.confirmationCode
        binding.tvPaymentMethod.text = "PAYMENT METHOD : "+transactionStatusResponse.paymentMethod!!.uppercase()
        binding.tvAmount.text = "AMOUNT : "+transactionStatusResponse.currency +". "+transactionStatusResponse.amount
    }


    companion object{
        fun newInstance(transactionStatusResponse: TransactionStatusResponse): MpesaSuccessFragment {
            val fragment = MpesaSuccessFragment()
            fragment.transactionStatusResponse = transactionStatusResponse
            return fragment
        }
    }

}