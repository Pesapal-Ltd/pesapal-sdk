package com.pesapal.paymentgateway.payment.fragment.mpesa.stk

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.databinding.FragmentPesapalMpesaBinding
import com.pesapal.paymentgateway.payment.model.mobile_money.BillingAddress
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.utils.PrefManager
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.utils.hideKeyboard
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel
import java.util.*

class MpesaPesapalFragment : Fragment() {

    private lateinit var binding: FragmentPesapalMpesaBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var pDialog: ProgressDialog

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
        handleClicks()
    }

    private fun handleClicks(){
        binding.btnSend.setOnClickListener {
            val request = prepareMobileMoney()
            viewModel.sendMobileMoneyCheckOut(request )
        }
    }

    private fun prepareMobileMoney(): MobileMoneyRequest {
        hideKeyboard()
        val billingAddress = BillingAddress()

//       val billingAddress = BillingAddress(phoneNumber = "0716210311", emailAddress = "richiekaby@gmail.com", countryCode = "KE", firstName = "Richard",
//            middleName = "Kamere",
//            lastName = "K",
//            line = "",
//            line2 = "",
//            city = "Nairobi",
//            state = "",
//            postalCode = "",
//            zipCode = "")

        val random = Random().nextInt(61) + 20
        return MobileMoneyRequest(
            id = random.toString(),
            sourceChannel = 2,
            msisdn = "0112826460",
            paymentMethodId = 1,
            accountNumber = "1000101",
            currency = "KES",
            allowedCurrencies = "",
            amount = 2,
            description = "Express Order",
            callbackUrl = "http://localhost:56522",
            cancellationUrl = "",
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress
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
                    showMessage("Stk sent successfully")
                    pDialog.dismiss()
                    showPendingMpesaPayment()
                }
                Status.ERROR -> {
                    pDialog.dismiss()
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }
    }

    private fun showPendingMpesaPayment(){
        viewModel.loadFragment("pending_mpesa")
    }

    private fun showMessage(message: String){

    }




}