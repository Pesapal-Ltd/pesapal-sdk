package com.pesapal.sdk.fragment.auth

import DeviceFingerprint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.activity.PesapalSdkActivity.Companion.STATUS_CANCELLED
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.databinding.FragmentAuthorizingBinding
import com.pesapal.sdk.model.accountinfo.AccountInfoRequest
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Status
import com.pesapal.sdk.utils.sec.ParseUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec

class AuthFragment : Fragment() {
    val TAG ="AuFr"

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AuthViewModel by viewModels()
    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()
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
        handleViewModel()
        paymentData()
    }

    private fun initData(){
        PrefManager.setToken(requireContext(), null)
        val authRequestModel = AuthRequestModel(paymentDetails.consumer_key, paymentDetails.consumer_secret,
            DeviceFingerprint(requireContext()).createFingerprint())
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }

    private fun checkTokenVerificationData(it: AuthResponseModel?) {
        if (it?.status == "200") {
            extractData(it)
        } else {
//            handleError(it?.message!!)
        }
    }


    fun parseRSAKeyValue(modulus1: String, exponent1: String): PublicKey {
        val modulusBytes = Base64.decode(modulus1, Base64.DEFAULT)
        val exponentBytes = Base64.decode(exponent1, Base64.DEFAULT)

        val modulus = BigInteger(1, modulusBytes)
        val exponent = BigInteger(1, exponentBytes)

        val spec = RSAPublicKeySpec(modulus, exponent)
        val keyFactory = KeyFactory.getInstance("RSA")

        val publicKey = keyFactory.generatePublic(spec)
        return publicKey;
    }

    private fun extractData(it: AuthResponseModel?) {
        try {
            val publicKey = parseRSAKeyValue(it?.keyInfo?.modulus!!, it.keyInfo.exponent);
            ParseUtil.parsePublicKey(publicKey)
            proceedAfterVerification(it)
        } catch (e: Exception) {
            Log.e("SecAu" ,e.localizedMessage ?: "Unable to proceed, Please try again later ..")
            // Handle any exceptions that might occur during key extraction or parsing.
        }
    }

    private fun proceedAfterVerification(it: AuthResponseModel?){
        val token = it?.token
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3VzZXJkYXRhIjoiZWQ2MTkwMGYtZGNiMy00NjM2LWIxNGUtY2U1MGQwYzk2M2I1IiwidWlkIjoicWtpbzFCR0dZQVhUdTJKT2ZtN1hTWE5ydW9ac3JxRVciLCJuYmYiOjE3MTAzNDUxOTUsImV4cCI6MTcxMDM0Njk5NSwiaWF0IjoxNzEwMzQ1MTk1LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjUzLyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NTIyNTMvIn0.6nKCEyQhuz10BXil7OG7vnXF1ZCis_u_rtMAIIi9e_8"
        PrefManager.setToken(requireContext(),token)
        registerIpn()
    }

    private fun handleViewModel(){
        viewModel.authResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.loader.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    checkTokenVerificationData(it.data)
                }
                Status.ERROR -> {
                    binding.loader.visibility = View.GONE
                    returnIntent(STATUS_CANCELLED, it.message!!)
                }
            }
        }

        viewModel.registerIpnResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(requireContext(),ipnId)
                    viewModel.retrieveAccountInfo(AccountInfoRequest(paymentDetails.consumer_key,paymentDetails.consumer_secret))
                }

                Status.ERROR -> {
                    binding.loader.visibility = View.GONE
                    returnIntent(STATUS_CANCELLED, it.message?:"")
                }
                else -> {
                    binding.loader.visibility = View.VISIBLE

                }
            }
        }

        viewModel.accountResp.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    paymentDetails.merchant_name = it.data?.merchantName?:"Pesapal"
                    proceed()
                }
                Status.ERROR -> {
                    binding.loader.visibility = View.GONE
                    returnIntent(STATUS_CANCELLED, it.message?:"")
                }
                else -> {
                    binding.loader.visibility = View.VISIBLE

                }
            }
        }
    }

    private fun proceed(){
        val action = AuthFragmentDirections.actionAuthFragmentToPesapalMainFragment()
        pesapalSdkViewModel.paymentDetails = paymentDetails
        pesapalSdkViewModel.billingAddress = billingAddress
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
            if (PrefManager.getString(requireContext(), PrefManager.con_key) != null) {
                consumerKey = PrefManager.getString(requireContext(), PrefManager.con_key)!!
            }

            if (PrefManager.getString(requireContext(), PrefManager.con_sec) != null) {
                consumerSecret = PrefManager.getString(requireContext(), PrefManager.con_sec)!!
            }

            if (PrefManager.getString(requireContext(), PrefManager.acc_num) != null) {
                accountNumber = PrefManager.getString(requireContext(), PrefManager.acc_num)!!
            }

            if (PrefManager.getString(requireContext(), PrefManager.call_url) != null) {
                callbackUrl = PrefManager.getString(requireContext(), PrefManager.call_url)!!
            }

            if (PrefManager.getString(requireContext(), PrefManager.ipn_url) != null) {
                ipnUrl = PrefManager.getString(requireContext(), PrefManager.ipn_url)!!
            }


            val amount = intent.getStringExtra(PESAPALAPI3SDK.AMOUNT)
            val orderId = intent.getStringExtra( PESAPALAPI3SDK.ORDER_ID)
            val currency = intent.getStringExtra(PESAPALAPI3SDK.CURRENCY)
            var country : PESAPALAPI3SDK.COUNTRIES_ENUM? = null

            pesapalSdkViewModel.orderID = orderId

            try {
                 country = intent.getSerializableExtra(PESAPALAPI3SDK.COUNTRY) as PESAPALAPI3SDK.COUNTRIES_ENUM
            }
            catch (exception: Exception){
//                setErrorElements("Error Code: 10207")
                // todo could show an info toast and proceed, It would show only the card

            }

            /**
             * 10100  = ErrorCode.CONSUMER_KEY
             * 10500  = ErrorCode.CONSUMER_SECRET
             * 10201  = Data -> Amount is missing
             * 102011 = Amount is less than 1
             * 10202  = OrderId is missing
             * 10203  = Currency format is missing
             * 10204  = Account number is missing
             * 10205  = Callback url is missing
             * 10206  = IPN is missing
             * 10400  = Consumer data required
             */

            if (consumerKey.isNullOrEmpty()) {
                setErrorElements("Error Code: 10100")
            }
            else if(consumerSecret.isNullOrEmpty()) {
                setErrorElements("Error Code: 10500")
            }
            else if(amount.isNullOrEmpty()){
                setErrorElements("Error Code: 10201")
            }
            else if(amount.toBigDecimal() < BigDecimal(1)){
                setErrorElements("Error Code: 102011")
            }
            else if(orderId.isNullOrEmpty()){
                setErrorElements("Error Code: 10202")
            }
            else if(currency.isNullOrEmpty()){
                setErrorElements("Error Code: 10203")
            }
            else if(accountNumber.isNullOrEmpty()){
                setErrorElements("Error Code: 10204")
            }
            else if(callbackUrl.isNullOrEmpty()){
                setErrorElements("Error Code: 10205")
            }
            else if(ipnUrl.isNullOrEmpty()){
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

                initData()
            }
            else{
                returnIntent(STATUS_CANCELLED, errorMessage)
            }

        } else {
            setErrorElements("Error Code: 10400")
            returnIntent(STATUS_CANCELLED, errorMessage)

        }
    }

    private fun setErrorElements(message: String){
        dataRequiredAvailable = false
        errorMessage = message
    }


    private fun registerIpn() {
        if(paymentDetails.ipn_url != null && paymentDetails.ipn_notification_type !=null) {
            val registerIpnRequest = RegisterIpnRequest(paymentDetails.ipn_url!!, paymentDetails.ipn_notification_type!!)
            viewModel.registerIpn(registerIpnRequest)
        }else{
            returnIntent(STATUS_CANCELLED, "Ipn Data Required")
        }
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


    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }

}