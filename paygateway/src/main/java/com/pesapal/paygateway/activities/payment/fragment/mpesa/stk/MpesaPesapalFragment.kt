package com.pesapal.paygateway.activities.payment.fragment.mpesa.stk

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.activities.payment.utils.Status
import com.pesapal.paygateway.databinding.FragmentPesapalMpesaBinding
import com.pesapal.paygateway.activities.payment.model.card.BillingAddress
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.payment.PaymentDetails
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.activities.payment.utils.TextDrawable
import com.pesapal.paygateway.activities.payment.utils.hideKeyboard
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import java.math.BigDecimal

class MpesaPesapalFragment : Fragment() {

    private lateinit var binding: FragmentPesapalMpesaBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var pDialog: ProgressDialog
    private lateinit var billingAddress: BillingAddress
    private lateinit var paymentDetails: PaymentDetails
    private var mobileMoneyResponse: MobileMoneyResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPesapalMpesaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleViewModel()
    }

    private fun initData(){
        prefillCountryCode()
        handleClicks()
    }

    private fun prefillCountryCode(){
        binding.phone.tag = "+254"
        binding.phone.setCompoundDrawables(
            TextDrawable(
                binding.phone,
                "\u2706  " + 254
            ), null, null, null
        )
    }

    private fun handleClicks(){
        binding.btnSend.setOnClickListener {
            if(binding.phone.text.toString().isNotEmpty()) {
                val request = prepareMobileMoney()
                viewModel.sendMobileMoneyCheckOut(request, "Sending payment prompt ...")
            }else{
                showMessage("All inputs required ...")
            }
        }
    }

    private fun prepareMobileMoney(): MobileMoneyRequest {
        hideKeyboard()
        val phoneNumber = "254"+binding.phone.text.toString()
        return MobileMoneyRequest(
            id = paymentDetails.order_id!!,
            sourceChannel = 2,
            msisdn = phoneNumber,
            paymentMethodId = 1,
            accountNumber = paymentDetails.accountNumber!!,
            currency = paymentDetails.currency!!,
            allowedCurrencies = "",
            amount = paymentDetails.amount,
            description = "Express Order",
            callbackUrl = paymentDetails.callbackUrl!!,
            cancellationUrl = "",
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = true
        );
    }

    private fun handleViewModel(){
        viewModel.mobileMoneyResponse.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    pDialog.dismiss()
                    mobileMoneyResponse = it.data
                    showPendingMpesaPayment()
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }
                else -> {

                }
            }
        }
    }

    private fun showPendingMpesaPayment(){
        val mobileMoneyRequest = prepareMobileMoney()
        mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        paymentDetails.order_tracking_id = mobileMoneyResponse!!.orderTrackingId

        if(mobileMoneyResponse != null) {
            mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        }
        viewModel.showPendingMpesaPayment(mobileMoneyRequest )
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        fun newInstance(billingAddress: BillingAddress,paymentDetails: PaymentDetails): MpesaPesapalFragment {
            val fragment = MpesaPesapalFragment()
            fragment.billingAddress = billingAddress
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }


}