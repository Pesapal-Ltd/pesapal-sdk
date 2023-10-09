package com.pesapal.sdk.fragment.mobile_money.mpesa.stk

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.R
import com.pesapal.sdk.databinding.FragmentPesapalMpesaBinding
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.utils.CountryCodeEval
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Status
import com.pesapal.sdk.utils.hideKeyboard


class MpesaPesapalFragment : Fragment() {

    private lateinit var binding: FragmentPesapalMpesaBinding
    private val viewModel: MpesaPesapalViewModel by viewModels()
    private lateinit var pDialog: ProgressDialog
    private lateinit var billingAddress: BillingAddress
    private lateinit var paymentDetails: PaymentDetails
    private var mobileMoneyResponse: MobileMoneyResponse? = null
    var  mobileProvider: Int = 0

    var countryCode: Int? = null

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

        paymentDetails = requireArguments().getSerializable("paymentDetails") as PaymentDetails
        billingAddress = requireArguments().getSerializable("billingAddress") as BillingAddress


        initData()
        handleViewModel()
    }


    private fun initData(){
        mobileProvider = paymentDetails.mobile_provider!!.toInt()

        val mobileProvider = CountryCodeEval.mappingAllCountries[mobileProvider]
        val mobileProviderName = mobileProvider!!.mobileProvider
        countryCode = mobileProvider.countryCode


        binding.tvInst1.text = getString(R.string.provide_mobile_money, mobileProviderName)
        binding.tvInst3.text = getString(R.string.provide_mobile_pin,   mobileProviderName)
        binding.tvInst5.text = getString(R.string.enter_mobile_number,  mobileProviderName)

        prefillCountryCode()

        binding.phone.setOnFocusChangeListener { _, hasFocus ->
            binding.phoneLayout.hint = if (hasFocus) "Phone" else "700123456"
        }

        handleClicks()
    }

    private fun prefillCountryCode(){
        binding.phone.tag = "+" + countryCode.toString()
        binding.phone.setCompoundDrawables(
            com.pesapal.sdk.utils.TextDrawable(
                binding.phone,
                "\u2706  $countryCode"
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
        val phoneNumber = countryCode.toString() + binding.phone.text.toString()
        return MobileMoneyRequest(
            id = paymentDetails.order_id!!,
            sourceChannel = 2,
            msisdn = phoneNumber,
            paymentMethodId = mobileProvider,
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
        viewModel.resetMobileResponse()

        val mobileMoneyRequest = prepareMobileMoney()
        mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        paymentDetails.order_tracking_id = mobileMoneyResponse!!.orderTrackingId

        if(mobileMoneyResponse != null) {
            mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        }
        val action = MpesaPesapalFragmentDirections.actionMpesaPesapalFragmentToMpesaPendingFragment(mobileMoneyRequest)
        findNavController().navigate(action)
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        internal fun newInstance(billingAddress: BillingAddress, paymentDetails: PaymentDetails): MpesaPesapalFragment {
            val fragment = MpesaPesapalFragment()
            fragment.billingAddress = billingAddress
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }


}