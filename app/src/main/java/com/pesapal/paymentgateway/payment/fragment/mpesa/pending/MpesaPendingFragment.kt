package com.pesapal.paymentgateway.payment.fragment.mpesa.pending

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentMpesaPendingBinding
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel
import java.math.BigDecimal

class MpesaPendingFragment : Fragment() {

    private lateinit var binding: FragmentMpesaPendingBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var mobileMoneyRequest: MobileMoneyRequest
    private lateinit var pDialog: ProgressDialog
    private var delayTime = 12000L

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
        handleBackgroundConfirmation(delayTime)
    }

    private fun initData(){
        handleClick()
        handleViewModel()
        handlePrefill()
    }

    private fun handlePrefill(){
        binding.tvInst1.text = "1. Check your phone ("+mobileMoneyRequest.msisdn+") \n     to complete this payment"
        binding.tvInstLipa4.text = "4. Enter Account No as "+mobileMoneyRequest.accountNumber
        binding.tvInstLipa5.text = "5. Enter the Amount "+mobileMoneyRequest.currency+" "+BigDecimal(mobileMoneyRequest.amount).setScale(2)
    }

    private fun handleClick(){
        binding.btnSend.setOnClickListener {
            handleConfirmation()
        }
        binding.btnSendLipa.setOnClickListener {
            handleConfirmation()
        }

        binding.btnResendPrompt.setOnClickListener {
            handleResend()
        }

        binding.btnLipa.setOnClickListener {
            binding.clStkPush.visibility = View.GONE
            binding.clLipaNaMpesa.visibility = View.VISIBLE
        }


    }

    private fun handleBackgroundConfirmation(delayTime: Long){
        Handler(Looper.getMainLooper()).postDelayed(
            {
               viewModel.mobileMoneyTransactionStatusBackground(mobileMoneyRequest.trackingId)
            }, delayTime
        )
    }

    private fun handleConfirmation(){
        viewModel.mobileMoneyTransactionStatus(mobileMoneyRequest.trackingId)
    }

    private fun handleResend(){
        viewModel.sendMobileMoneyCheckOut(mobileMoneyRequest, "Resending payment prompt ....")
    }

    private fun handleViewModel(){
        viewModel.mobileMoneyResponse.observe(requireActivity()){
            if(::pDialog.isInitialized) {
                when (it.status) {
                    Status.LOADING -> {
                        pDialog = ProgressDialog(requireContext())
                        pDialog.setMessage(it.message)
                        pDialog.show()
                    }
                    Status.SUCCESS -> {
                        showMessage("Stk resent successfully")
                        pDialog.dismiss()
                        binding.clLipaNaMpesa.visibility = View.GONE
                        binding.clStkPush.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                        showMessage(it.message!!)

                    }
                    else -> {
                        Log.e(" else ", " ====> auth")
                    }
                }
            }
        }

        viewModel.transactionStatus.observe(requireActivity()){
                when (it.status) {
                    Status.LOADING -> {
                        pDialog = ProgressDialog(requireContext())
                        pDialog.setMessage(it.message)
                        pDialog.show()
                    }
                    Status.SUCCESS -> {
                        if(::pDialog.isInitialized) {
                            showMessage("Payment confirmed successfully ")
                            pDialog.dismiss()
                            proceedToSuccessScreen(it.data!!)
                        }
                    }
                    Status.ERROR -> {
                        if(::pDialog.isInitialized) {
                            pDialog.dismiss()
                            showMessage(it.message!!)
                        }
                    }
                    else -> {
                        Log.e(" else ", " ====> auth")
                    }
                }
        }


        viewModel.transactionStatusBg.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    showMessage("Payment confirmed successfully ")
                    proceedToSuccessScreen(it.data!!)
                }
                Status.ERROR -> {
                    if(delayTime != 2000L){
                        delayTime -= 2000
                        handleBackgroundConfirmation(delayTime)
                    }

                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }



    }

    companion object{
        fun newInstance(mobileMoneyRequest: MobileMoneyRequest): MpesaPendingFragment {
            val mpesaPendingFragment = MpesaPendingFragment()
            mpesaPendingFragment.mobileMoneyRequest = mobileMoneyRequest
            return mpesaPendingFragment
        }

        fun newInstance(): MpesaPendingFragment {
            return MpesaPendingFragment()
        }
    }

    private fun proceedToSuccessScreen(transactionStatusResponse: TransactionStatusResponse){
        viewModel.showSuccessMpesaPayment(transactionStatusResponse)
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}