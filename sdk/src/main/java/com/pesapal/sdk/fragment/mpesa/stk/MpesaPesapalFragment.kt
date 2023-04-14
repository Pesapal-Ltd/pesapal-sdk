package com.pesapal.sdk.fragment.mpesa.stk

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.sdk.utils.Status
import com.pesapal.sdk.databinding.FragmentPesapalMpesaBinding
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.hideKeyboard
import com.pesapal.sdk.viewmodel.AppViewModel
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
        paymentData()
        initData()
        handleViewModel()
    }

    private fun paymentData() {
        val intent = requireActivity().intent
        if (intent != null) {
            var consumerKey: String? = null
            var consumerSecret: String? = null
            var ipnUrl: String? = null
            var accountNumber: String? = null
            var callbackUrl: String? = null
            if (PrefManager.getString("consumer_key",null) != null) {
                consumerKey = PrefManager.getString("consumer_key",null)!!
            }

            if (PrefManager.getString("consumer_secret",null) != null) {
                consumerSecret = PrefManager.getString("consumer_secret",null)!!
            }

            if (PrefManager.getString("account_number",null) != null) {
                accountNumber = PrefManager.getString("account_number",null)!!
            }

            if (PrefManager.getString("callback_url",null) != null) {
                callbackUrl = PrefManager.getString("callback_url",null)!!
            }

            if (PrefManager.getString("ipn_url",null) != null) {
                ipnUrl = PrefManager.getString("ipn_url",null)!!
            }


            val amount = intent.getStringExtra("amount")
            val orderId = intent.getStringExtra("order_id")
            val currency = intent.getStringExtra("currency")

            paymentDetails = PaymentDetails(
                amount = BigDecimal(amount),
                order_id = orderId,
                currency = currency,
                accountNumber = accountNumber,
                callbackUrl = callbackUrl,
                consumer_key = consumerKey,
                consumer_secret =  consumerSecret,
                ipn_url = ipnUrl,
            )

            val firstName = intent.getStringExtra("firstName")
            val lastName = intent.getStringExtra("lastName")
            val email = intent.getStringExtra("email")
            val city = intent.getStringExtra("city")
            val address = intent.getStringExtra("address")
            val postalCode = intent.getStringExtra("postalCode")

            billingAddress = BillingAddress(
                firstName = firstName,
                lastName = lastName,
                middleName = lastName,
                emailAddress = email,
                line = address,
                line2 = address,
                postalCode = postalCode,
                city = city
            )

            if (consumerKey != "" && consumerSecret != "") {
                initData()
            } else {
                showMessage("Consumer data required ...")
            }

        } else {
            showMessage("Consumer data required ...")
        }

    }

    private fun initData(){
        prefillCountryCode()
        handleClicks()
    }

    private fun prefillCountryCode(){
        binding.phone.tag = "+254"
        binding.phone.setCompoundDrawables(
            com.pesapal.sdk.utils.TextDrawable(
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
            notificationId = com.pesapal.sdk.utils.PrefManager.getIpnId(),
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