package com.pesapal.sdk.fragment.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.activity.PesapalPayActivity
import com.pesapal.sdk.databinding.FragmentAuthorizingBinding
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Status
import java.math.BigDecimal

class AuthFragment : Fragment() {
    val TAG ="AuFr"

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress


    var dataRequiredAvailable = true
    var errorMessage = ""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("Auth", "onViewCreated called")
        handleViewModel()
        paymentData()
    }

    private fun initData(){
        PrefManager.setToken(null)
        val authRequestModel = AuthRequestModel(paymentDetails.consumer_key,paymentDetails.consumer_secret)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }

    private fun handleViewModel(){
        viewModel.authResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.loader.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.loader.visibility = View.GONE
                    val token = it.data?.token
                    PrefManager.setToken(token)
                    registerIpn()
                }
                Status.ERROR -> {
                    binding.loader.visibility = View.GONE

                    showMessage(it.message!!)
                }

            }
        }

        viewModel.registerIpnResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(ipnId)
                    proceed()
                }

                Status.ERROR -> {
                }
                else -> {
                }
            }
        }

    }

    private fun proceed(){
        val action = AuthFragmentDirections.actionAuthFragmentToPesapalMainFragment(paymentDetails, billingAddress)
       findNavController().navigate(action)
    }

    private fun paymentData() {
        val intent = requireActivity().intent
        if (intent != null) {
            var consumerKey: String? = null
            var consumerSecret: String? = null
            var ipnUrl: String? = null
            var accountNumber: String? = null
            var callbackUrl: String? = null
            if (PrefManager.getString(PrefManager.con_key) != null) {
                consumerKey = PrefManager.getString(PrefManager.con_key)!!
            }

            if (PrefManager.getString(PrefManager.con_sec) != null) {
                consumerSecret = PrefManager.getString(PrefManager.con_sec)!!
            }

            if (PrefManager.getString(PrefManager.acc_num) != null) {
                accountNumber = PrefManager.getString(PrefManager.acc_num)!!
            }

            if (PrefManager.getString(PrefManager.call_url) != null) {
                callbackUrl = PrefManager.getString(PrefManager.call_url)!!
            }

            if (PrefManager.getString(PrefManager.ipn_url) != null) {
                ipnUrl = PrefManager.getString(PrefManager.ipn_url)!!
            }


            val amount = intent.getStringExtra(PESAPALAPI3SDK.AMOUNT)
            val orderId = intent.getStringExtra( PESAPALAPI3SDK.ORDER_ID)
            val currency = intent.getStringExtra(PESAPALAPI3SDK.CURRENCY)
            var country : PESAPALAPI3SDK.COUNTRIES_ENUM? = null
            try {
                 country = intent.getSerializableExtra(PESAPALAPI3SDK.COUNTRY) as PESAPALAPI3SDK.COUNTRIES_ENUM
            }
            catch (exception: Exception){
//                setErrorElements("Error Code: 10207")
                // todo could show an info toast and proceed, It would show only the card

            }

            if (consumerKey.isNullOrEmpty()) {
                setErrorElements("Error Code: 10100")
//                setErrorElements("Consumer data required ...")
//                setErrorElements(ErrorCode.CONSUMER_KEY)
//                showMessage("Consumer data required ...")
            }
            else if(consumerSecret.isNullOrEmpty()) {
//                setErrorElements("Consumer data required ...")
                setErrorElements("Error Code: 10500")
//                setErrorElements(ErrorCode.CONSUMER_SECRET)
//                showMessage("Consumer data required ...")
            }
            else if(amount.isNullOrEmpty()){
//                setErrorElements("Data -> Amount is missing")
                setErrorElements("Error Code: 10201")
            }
            else if(amount.toInt() < 1){
//                setErrorElements("Data -> Amount is missing")
                setErrorElements("Error Code: 102011")
            }
            else if(orderId.isNullOrEmpty()){
//                setErrorElements("Data -> OrderId is missing")
                setErrorElements("Error Code: 10202")
            }
            else if(currency.isNullOrEmpty()){
//                setErrorElements("Data -> Currency format is missing")
                setErrorElements("Error Code: 10203")
            }
            else if(accountNumber.isNullOrEmpty()){
//                setErrorElements("Data -> Account number is missing")
                setErrorElements("Error Code: 10204")
            }
            else if(callbackUrl.isNullOrEmpty()){
//                setErrorElements("Data -> Callback url is missing")
                setErrorElements("Error Code: 10205")
            }
            else if(ipnUrl.isNullOrEmpty()){
//                setErrorElements("Data -> IPN is missing")
                setErrorElements("Error Code: 10206")
            }



            if(dataRequiredAvailable) {
                paymentDetails = PaymentDetails(
                    amount = BigDecimal(amount),
                    order_id = orderId,
                    currency = currency,
                    accountNumber = accountNumber,
                    callbackUrl = callbackUrl,
                    consumer_key = consumerKey,
                    consumer_secret = consumerSecret,
                    ipn_url = ipnUrl,
                    country = country
                )



                val firstName = intent.getStringExtra(PESAPALAPI3SDK.FIRST_NAME )
                val lastName = intent.getStringExtra(PESAPALAPI3SDK.LAST_NAME)
                val email = intent.getStringExtra(PESAPALAPI3SDK.EMAIL)
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
                Log.i(TAG, "ConKey: $consumerKey")
                Log.i(TAG, "ConSec: $consumerSecret")

                initData()
            }
            else{
                returnIntent(PesapalPayActivity.STATUS_CANCELLED, errorMessage)
            }

        } else {
//            showMessage("Consumer data required ...")
            setErrorElements("Error Code: 10400")

            returnIntent(PesapalPayActivity.STATUS_CANCELLED, errorMessage)

        }

    }

    private fun setErrorElements(message: String){
        dataRequiredAvailable = false
        errorMessage = message
    }


    private fun returnIntent(status: String, obj : Any){
        val returnIntent = Intent()
        returnIntent.putExtra("status", status)
        val data = if(obj is String){
            obj
        }
        else{
            obj as TransactionStatusResponse
        }
        returnIntent.putExtra("data", data)

        requireActivity().setResult(AppCompatActivity.RESULT_OK, returnIntent)
        requireActivity().finish()
    }


    private fun registerIpn() {
        if(paymentDetails.ipn_url != null && paymentDetails.ipn_notification_type !=null) {
            val registerIpnRequest = RegisterIpnRequest(paymentDetails.ipn_url!!, paymentDetails.ipn_notification_type!!)
            viewModel.registerIpn(registerIpnRequest)
        }else{
            showMessage("Ipn Data Required")
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }


}