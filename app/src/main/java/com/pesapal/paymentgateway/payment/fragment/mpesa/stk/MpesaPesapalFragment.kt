package com.pesapal.paymentgateway.payment.fragment.mpesa.stk

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentPesapalMpesaBinding
import com.pesapal.paymentgateway.payment.model.mobile_money.BillingAddress
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paymentgateway.payment.utils.PrefManager
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.utils.TextDrawable
import com.pesapal.paymentgateway.payment.utils.hideKeyboard
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel
import java.util.*

class MpesaPesapalFragment : Fragment() {

    private lateinit var binding: FragmentPesapalMpesaBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var pDialog: ProgressDialog
    private lateinit var order_id: String
    private lateinit var currency: String
    private lateinit var accountNumber: String
    private lateinit var callbackUrl: String
    private var mobileMoneyResponse: MobileMoneyResponse? = null
    private var amount: Int = 1

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
                viewModel.sendMobileMoneyCheckOut(request, "Sending stk push ...")
            }else{
                showMessage("All inputs required ...")
            }
        }
    }

    private fun prepareMobileMoney(): MobileMoneyRequest {
        hideKeyboard()
        val billingAddress = BillingAddress()
        val phoneNumber = "254"+binding.phone.text.toString()
//       val billingAddress = BillingAddress(phoneNumber = "0716210311", emailAddress = "richiekaby@gmail.com", countryCode = "KE", firstName = "Richard",
//            middleName = "Kamere",
//            lastName = "K",
//            line = "",
//            line2 = "",
//            city = "Nairobi",
//            state = "",
//            postalCode = "",
//            zipCode = "")

        return MobileMoneyRequest(
            id = order_id,
            sourceChannel = 2,
            msisdn = phoneNumber,
            paymentMethodId = 1,
            accountNumber = accountNumber,
            currency = currency,
            allowedCurrencies = "",
            amount = amount,
            description = "Express Order",
            callbackUrl = callbackUrl,
            cancellationUrl = "",
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = ""
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
                    showMessage("Payment prompt sent successfully")
                    pDialog.dismiss()
                    mobileMoneyResponse = it.data
//                    checkTransactionStatus()
                    showPendingMpesaPayment()
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }
    }

    private fun checkTransactionStatus(){
        val bf = CheckPaymentBsf()
        bf.show(requireActivity().supportFragmentManager, "mpesa_pending")
    }

    private fun showPendingMpesaPayment(){
        val mobileMoneyRequest = prepareMobileMoney()
        mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId

        if(mobileMoneyResponse != null) {
            mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        }
        viewModel.showPendingMpesaPayment(mobileMoneyRequest )
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object{

        fun newInstance(amount: Int, order_id: String, currency: String, accountNumber: String, callbackUrl: String): MpesaPesapalFragment{
            val fragment = MpesaPesapalFragment()
            fragment.amount = amount
            fragment.order_id = order_id
            fragment.currency = currency
            fragment.accountNumber = accountNumber
            fragment.callbackUrl = callbackUrl
            return fragment
        }
    }


}