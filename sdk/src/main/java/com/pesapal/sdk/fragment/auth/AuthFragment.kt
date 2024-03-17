package com.pesapal.sdk.fragment.auth

import DeviceFingerprint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.activity.PesapalSdkActivity
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.databinding.FragmentAuthorizingBinding
import com.pesapal.sdk.model.accountinfo.AccountInfoRequest
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.card.CustomerData
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdk.utils.PESAPALAPI3SDK.ERR_INIT
import com.pesapal.sdk.utils.PESAPALAPI3SDK.ERR_NETWORK
import com.pesapal.sdk.utils.PESAPALAPI3SDK.STATUS_CANCELLED
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

    private val viewModel: AuthViewModel by viewModels()
    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()

    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

    private lateinit var binding: FragmentAuthorizingBinding


    var dataRequiredAvailable = true
    var errorMessage = ""

    var statCode = ""
    val errType = "INIT"

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
            returnIntent(networkError, ERR_NETWORK,"Error during Initialization")
        }
    }
    val networkError = "190"
    val parseError   = "191"
    val authError    = "193"
    val ipnError    = "194"


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
            returnIntent(parseError,errType,"Error during Init")
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
                    returnIntent(authError, ERR_NETWORK, it.message!!)
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
                    returnIntent(ipnError, ERR_NETWORK, it.message?:"")
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
                    returnIntent(networkError, ERR_NETWORK,it.message?:"Account")
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
             * 10210 = Amount is less than 1
             * 10202  = OrderId is missing
             * 10203  = Currency format is missing
             * 10204  = Account number is missing
             * 10205  = Callback url is missing
             * 10206  = IPN is missing
             * 10400  = Consumer data required
             */

            if (consumerKey.isNullOrEmpty()) {
                setErrorElements("10100","Error Code: 10100")
            }
            else if(consumerSecret.isNullOrEmpty()) {
                setErrorElements("10500","Error Code: 10500")
            }
            else if(amount.isNullOrEmpty()){
                setErrorElements("10201","Error Code: 10201")
            }
            else if(amount.toBigDecimal() < BigDecimal(1)){
                setErrorElements("10210","Error Code: 10210")
            }
            else if(orderId.isNullOrEmpty()){
                setErrorElements("10202","Error Code: 10202")
            }
            else if(currency.isNullOrEmpty()){
                setErrorElements("10203","Error Code: 10203")
            }
            else if(accountNumber.isNullOrEmpty()){
                setErrorElements("10204","Error Code: 10204")
            }
            else if(callbackUrl.isNullOrEmpty()){
                setErrorElements("10205","Error Code: 10205")
            }
            else if(ipnUrl.isNullOrEmpty()){
                setErrorElements("10206", "Error Code: 10206")
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

                billingAddress = BillingAddress()
                try{
                    val customerData = intent.getSerializableExtra(PESAPALAPI3SDK.USER_DATA) as CustomerData?
                    customerData?.let {
                        billingAddress = BillingAddress(it.line, it.countryCode, it.line2, it.emailAddress, it.city, it.lastName,
                            checkFormat(it.phoneNumber), it.state, it.middleName, checkFormat(it.postalCode), it.firstName, checkFormat(it.zipCode))
                    }

                }
                catch (_: Exception){}

                initData()
            }
            else{
                returnIntent(statCode, ERR_INIT, errorMessage)
            }

        } else {
            setErrorElements("10400","Error Code: 10400")               // Null intent
            returnIntent(statCode, ERR_INIT, errorMessage)
        }
    }

    private fun checkFormat(value: String?): String {
        var data = ""
        try{
            value?.let {
                if(it.isNotBlank()){
                    if("^[0-9]".toRegex().containsMatchIn(value)){
                        data =value
                    }
                }
            }
        }
        catch (_: Exception){

        }
        return data

    }

    private fun setErrorElements(errorCode:String, message: String){
        dataRequiredAvailable = false
        errorMessage = message
        statCode = errorCode
    }



    private fun registerIpn() {
        if(paymentDetails.ipn_url != null && paymentDetails.ipn_notification_type !=null) {
            val registerIpnRequest = RegisterIpnRequest(paymentDetails.ipn_url!!, paymentDetails.ipn_notification_type!!)
            viewModel.registerIpn(registerIpnRequest)
        }else{
            returnIntent("10206", ERR_INIT,"Ipn Data Required")
        }
    }



    private fun returnIntent(statusCode: String,errTypes:String, message : String){
        val data = TransactionStatusResponse(error = TransactionError(code = statusCode, errorType = errTypes, message = message))
        PesapalSdkActivity.returnIntent(requireActivity(), STATUS_CANCELLED, data)
    }


    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }

}