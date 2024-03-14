package com.pesapal.sdk.fragment.card.data


import DeviceFingerprint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
//import com.cardinalcommerce.cardinalmobilesdk.Cardinal
//import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
//import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
//import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
//import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
//import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
//import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
//import com.cardinalcommerce.shared.models.Warning
//import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.google.gson.Gson
import com.pesapal.paygateway.activities.payment.model.cacontinie.Account
import com.pesapal.paygateway.activities.payment.model.cacontinie.CCAExtension
import com.pesapal.paygateway.activities.payment.model.cacontinie.Consumer
import com.pesapal.paygateway.activities.payment.model.cacontinie.OrderDetails
import com.pesapal.paygateway.activities.payment.model.cacontinie.PayloadCanContinueModel
import com.pesapal.paygateway.activities.payment.model.check3ds.BillingDetails
import com.pesapal.paygateway.activities.payment.model.check3ds.CardDetails3Ds
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.sdk.BuildConfig
import com.pesapal.sdk.model.card.CardDetails
import com.pesapal.sdk.model.card.submit.request.EnrollmentCheckResult
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.request.SubscriptionDetails
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.setButtonEnabled
import com.pesapal.sdk.utils.FragmentExtension.hideKeyboard
import com.pesapal.sdk.databinding.FragmentNewCardDetailsBinding
import com.pesapal.sdk.fragment.card.viewmodel.CardViewModel
import com.pesapal.sdk.model.card.RequestServerJwt
import com.pesapal.sdk.model.card.ResponseServerJwt
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class CardFragmentCardData : Fragment() {

    private val viewModel: CardViewModel by viewModels()
    private lateinit var cardOrderTrackingIdRequest: CardOrderTrackingIdRequest
    private lateinit var pDialog: ProgressDialog
    private lateinit var binding: FragmentNewCardDetailsBinding
    private lateinit var cardDetails: CardDetails
    private lateinit var billingAddress: BillingAddress
    private lateinit var paymentDetails: PaymentDetails
    private var isCardNumberFilled = false
    private var isExpiryFilled = false
    private var isCvvFilled = false
    private var expiryMonth = false
    private var expiryYear = false
    private var enable = false
//    var cardinal: Cardinal? = null

    var responseServerJwt: ResponseServerJwt? = null
    var consumerSessionId : String? = null

    var ip: String = ""


    companion object {
        const val MAX_LENGTH_CVV_CODE = 3
        const val cardNumberLength = 19
        internal fun newInstance(
            paymentDetails: PaymentDetails,
            billingAddress: BillingAddress
        ): CardFragmentCardData {
            val fragment = CardFragmentCardData()
            fragment.paymentDetails = paymentDetails
            fragment.billingAddress = billingAddress
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCardDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentDetails = arguments?.getSerializable("paymentDetails") as PaymentDetails
        billingAddress = arguments?.getSerializable("billingAddress") as BillingAddress
        initData()
    }

    private fun initData() {
//        cardinal = Cardinal.getInstance()
//        initCardinal()

//        launchIp()
        handleViewModel()
        handleChangeListener()
    }

//    private fun initCardinal() {
//        val cardinalConfigurationParameters = CardinalConfigurationParameters()
//        cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
//        cardinalConfigurationParameters.requestTimeout = 8000
//        cardinalConfigurationParameters.challengeTimeout = 5
//        val rTYPE = JSONArray()
//        rTYPE.put(CardinalRenderType.OTP)
//        rTYPE.put(CardinalRenderType.SINGLE_SELECT)
//        rTYPE.put(CardinalRenderType.MULTI_SELECT)
//        rTYPE.put(CardinalRenderType.OOB)
//        rTYPE.put(CardinalRenderType.HTML)
//        cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
//        cardinalConfigurationParameters.renderType = rTYPE
//
//        if (BuildConfig.DEBUG) {
//            cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
//            cardinalConfigurationParameters.isEnableLogging = true
//        } else {
//            cardinalConfigurationParameters.environment = CardinalEnvironment.PRODUCTION
//            cardinalConfigurationParameters.isEnableLogging = false
//        }
//
////     cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
////     cardinalConfigurationParameters.renderType = CardinalRenderType.OTP
////     cardinalConfigurationParameters.isLocationDataConsentGiven = true
//
//
//        val yourUICustomizationObject = UiCustomization()
//        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject
//        cardinal!!.configure(requireContext(), cardinalConfigurationParameters)
//        getAllWarnings()
//    }

//    private fun getAllWarnings() {
//        val warnings: List<Warning> = cardinal!!.warnings
//        for (warning in warnings) {
//            Log.e(" id ", warning.id);
//            Log.e(" severity ", warning.severity.toString());
//            Log.e(" message ", warning.message.toString());
//        }
//    }


    private fun generateCardOrderTackingId() {

        cardDetails = CardDetails(
            binding.etNumberCard.rawText.toString(),
            Integer.parseInt(binding.yearField.text.toString()),
            Integer.parseInt(binding.monthField.text.toString()),
            binding.etCvv.text.toString(),
        )

        cardOrderTrackingIdRequest = CardOrderTrackingIdRequest(
            id = paymentDetails.order_id!!,
            sourceChannel = 2,
            msisdn = billingAddress.phoneNumber,
            paymentMethodId = 1,
            accountNumber = paymentDetails.accountNumber!!,
            currency = paymentDetails.currency!!,
            allowedCurrencies = "",
            amount = paymentDetails.amount,
            description = "Express Order",
            callbackUrl = paymentDetails.callbackUrl!!,
            cancellationUrl = "",
            notificationId = com.pesapal.sdk.utils.PrefManager.getIpnId(requireContext()),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = false
        );


        viewModel.generateCardOrderTrackingId(cardOrderTrackingIdRequest, " Processing request ...")

//        val requestServerJwt = RequestServerJwt(BigDecimal("1500"),paymentDetails.currency!!, billingAddress = billingAddress, cardDetails = cardDetails)
//        viewModel.serverJwt(requestServerJwt)
    }


    private fun submitCardRequest() {
        val enrollmentCheckResult = EnrollmentCheckResult(
            authenticationResult = "",
            authenticationAttempted = false,
            directoryServerTransactionId = "",
            cavvAlgorithm = "",
            eci = "",
            eciRaw = "",
            cavv = "",
            reasonCode = "",
            processorTransactionId = "",
            xid = "",
            ucafCollectionIndicator = "",
            threeDSServerTransactionId = "",
            pareq = "",
            ucafAuthenticationData = "",
            veresEnrolled = "",
            aav = "",
            specificationVersion = "",
            commerceIndicator = "",
            paresStatus = "",
            checkoutSessionId = "",
        )

        val subscriptionDetails = SubscriptionDetails(
            endDate = "0001-01-01T00:00:00",
            startDate = "0001-01-01T00:00:00",
            amount = 0,
            accountReference = null,
            frequency = 0
        )

        val submitCardRequest = SubmitCardRequest(
            cvv = cardDetails.cvv,
            enrollmentCheckResult = enrollmentCheckResult,
            subscriptionDetails = subscriptionDetails,
            orderTrackingId = paymentDetails.order_tracking_id,
            expiryMonth = cardDetails.month.toString(),
            billingAddress = billingAddress,
            expiryYear = cardDetails.year.toString(),
            ipAddress = "1",
            cardNumber = cardDetails.cardNumber,
            deviceId = DeviceFingerprint(requireContext()).createFingerprint().device_id
        )

        viewModel.submitCardRequest(submitCardRequest)

//        val cardinalRequest = CardinalRequest(
//            paymentDetails.order_id!!, CardDetailsX(
//                cardDetails.cardNumber ,
//                cardDetails.cvv,
//                cardDetails.month,
//                cardDetails.year),
//            paymentDetails.order_tracking_id!!,
//            cardinal.sdkVersion,
//            ""
//        )
//        billingAddress
//        viewModel.getCardinalToken(cardinalRequest)
    }

    private fun checkCardPaymentStatus(){
        viewModel.checkCardPaymentStatus(paymentDetails.order_tracking_id!!)
    }

    private fun handleCompletePayment(transactionStatusResponse: TransactionStatusResponse){
        val action = CardFragmentCardDataDirections.actionPesapalCardFragmentCardDataToPesapalCardFragmentSuccess(transactionStatusResponse, true)
        findNavController().navigate(action)
    }


    private fun handleViewModel() {
        viewModel.cardOrderTrackingIdResponse.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    val result = it.data
                    paymentDetails.order_tracking_id = result!!.orderTrackingId
                    submitCardRequest()
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }

                else -> {
                    pDialog.dismiss()
                }
            }
        }

        viewModel.submitCardResponse.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    checkCardPaymentStatus()
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }

                else -> {
                    pDialog.dismiss()
                }
            }
        }

        viewModel.cardPaymentStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    pDialog.dismiss()
                    val result = it.data!!
                    handleCompletePayment(result)
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }

                else -> {
                    pDialog.dismiss()
                }
            }
        }

//        viewModel.serverJwt.observe(requireActivity()){
//            when (it.status) {
//                Status.LOADING -> {
//                    pDialog = ProgressDialog(requireContext())
//                    pDialog.setMessage(it.message)
//                    pDialog.show()
//                }
//                Status.SUCCESS -> {
//                    responseServerJwt = it.data
//                    initSdk(responseServerJwt!!.orderJwt)
//                }
//                Status.ERROR -> {
//                    showMessage(it.message!!)
//                    pDialog.dismiss()
//                }
//                else -> {
//                    Log.e(" else ", " ====> auth")
//                }
//            }
//        }
//
//        viewModel.dsToken.observe(requireActivity()){
//            when (it.status) {
//                Status.LOADING -> {
//
//                }
//                Status.SUCCESS -> {
//                    val token = it.data!!.token
//                    get3dsPayload(token);
//                }
//                Status.ERROR -> {
//
//                }
//                else -> {
//
//                }
//            }
//        }
//
//        viewModel.dsResponse.observe(requireActivity()){
//            when (it.status) {
//                Status.LOADING -> {
//                }
//                Status.SUCCESS -> {
//                    var response = it.data
//                    val gson = Gson()
//
//                    var responseString = gson.toJson(response)
//                    Log.e(" responseString ", responseString)
//
//                    var payAcsUrlload = response?.acsUrl
//                    var payload = response?.payload
//
//                    if(!response!!.authenticationTransactionId.isNullOrEmpty() && !payload.isNullOrEmpty() && payAcsUrlload!= null) {
//                        if (response.reasonCode == "475")
//                            handle3dSecure(
//                                response.authenticationTransactionId!!,
//                                payload,
//                                payAcsUrlload
//                            )
//                        else
//                        {
//                            Toast.makeText(requireContext(),"Go normal route without 3ds", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    else
//                        Toast.makeText(requireContext(),"Unable to complete. Err 600", Toast.LENGTH_LONG).show()
//                }
//                Status.ERROR -> {
//                }
//                else -> {
//                    Log.e(" else ", " ====> auth")
//                }
//            }
//        }
    }

//    private fun get3dToken(){
//        val dsTokenRequest = DsTokenRequest("E71FC13D-5FD0-43EC-9E87-007586759EE0","677801C8-A971-46F8-957E-497213245E9B");
//        viewModel.getDsToken(dsTokenRequest)
//    }
//
//    private fun initSdk(serverJwt: String){
//        cardinal?.init(serverJwt, object: CardinalInitService {
//            /**
//             * You may have your Submit button disabled on page load. Once you are set up
//             * for CCA, you may then enable it. This will prevent users from submitting
//             * their order before CCA is ready.
//             */
//
//            override fun onSetupCompleted(consumerSessionIds: String) {
//                pDialog.dismiss()
//                consumerSessionId = consumerSessionIds
//                Log.e("TAG","COns $consumerSessionIds")
//                get3dToken()
//            }
//
//            /**
//             * If there was an error with setup, cardinal will call this function with
//             * validate response and empty serverJWT
//             * @param validateResponse
//             * @param serverJwt will be an empty
//             */
//            override fun onValidated(validateResponse: ValidateResponse, serverJwt: String?) {
//                pDialog.dismiss()
//
//            }
//        })
//
//    }
//
//
//    private fun get3dsPayload(token: String){
//        var cardDetails = CardDetails3Ds(
//            cardDetails.cvv,
//            cardDetails.cardNumber,
//            cardDetails.month.toString(),
//            cardDetails.year,
//            "001"
//        )
//
////        val billingAddress = BillingDetails(
////            phoneNumber = "0703318241",
////            email = "samuel@pesapal.com",
////            country = "KE",
////            currency = paymentDetails.currency,
////            firstName = "samuel",
////            lastName = "nyamai",
////            city = "Nairobi",
////            street = "Kasarani",
////            state = "Nairobi",
////            postalCode = "80300",
////            ipAddress = ip
////        )
//
//        val billingDetails = BillingDetails(
//            phoneNumber = billingAddress.phoneNumber!!,
//            email = billingAddress.emailAddress ,
//            country = billingAddress.countryCode!!,
//            currency = paymentDetails.currency,
//            firstName = billingAddress.firstName,
//            lastName = billingAddress.lastName!!,
//            city = billingAddress.city!!,
//            street = billingAddress.line,
//            state = billingAddress.city!!,
//            postalCode = billingAddress.postalCode,
//            ipAddress = ip
//        )
//
//        val checkDSecureRequest = CheckDSecureRequest(
//            cardDetails,
//            billingDetails,
//            paymentDetails.amount,
//            "",
//            consumerSessionId!!,
//            paymentDetails.currency!!,
//            0,
//            "",
//            "SDK",
//        )
//
//        viewModel.check3ds(checkDSecureRequest,token)
//    }
//
//
//    private suspend fun getIp(): String = withContext(Dispatchers.Default){
//        var ip = ""
//        try {
//            val url = URL("https://api.ipify.org")
//            val connection =
//                url.openConnection() as HttpURLConnection
//            connection.setRequestProperty(
//                "User-Agent",
//                "Mozilla/5.0"
//            ) // Set a User-Agent to avoid HTTP 403 Forbidden error
//            Scanner(connection.inputStream, "UTF-8").useDelimiter("\\A").use { s ->
//                ip = s.next()
//                Log.e("Main","Ip ap is $ip")
//            }
//            connection.disconnect()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        return@withContext ip
//
//    }
//
//    private fun launchIp(){
//        CoroutineScope(Dispatchers.Default).launch{
//            val ipFetched = getIp()
//            ip = ipFetched
//        }
//    }
//
//
//    private fun handle3dSecure(transactionId: String, payload: String, acsUrl: String? ){
//        try {
//
//            var orderDetails = OrderDetails(
//                "404",
//                "P",
//                amount = paymentDetails.amount.toString(),
//                orderNumber = "test_order",
//                transactionId = consumerSessionId!!
//            )
//
//            var account = Account(
//                nameOnAccount = billingAddress.firstName + "" + billingAddress.lastName,
//                cardCode = cardDetails.cvv,
//                expirationMonth = cardDetails.month.toString(),
//                expirationYear = "20" + cardDetails.year,
//                accountNumber =  cardDetails.cardNumber
//            )
//
//            var consumer = Consumer(
//                account = account
//            )
//
//            var ccaExtension = CCAExtension(
//                merchantName = "PESAPAL LTD"
//            )
//
//            var payloadCanContinueModel = PayloadCanContinueModel(
//                cCAExtension = ccaExtension,
//                consumer = consumer,
//                orderDetails = orderDetails,
//                payload = payload,
//                acsUrl = acsUrl,
//            )
//
//            val gson = Gson()
//            val stringPayload = gson.toJson(payloadCanContinueModel)
//            val stringPayloadv1 = stringPayload.replace("\\u0026", "&");
//            val stringPayloadv2 = stringPayloadv1.replace("\\u003d", "=");
//
//            Log.e(" payload ",stringPayloadv2)
//            Log.e(" transactionId ",transactionId)
//
//
//            cardinal?.cca_continue(transactionId,stringPayloadv2,requireActivity()) { p0, validateResponse, serverJWT ->
//                /**
//                 * This method is triggered when the transaction has been terminated. This is how SDK hands back
//                 * control to the merchant's application. This method will
//                 * include data on how the transaction attempt ended and
//                 * you should have your logic for reviewing the results of
//                 * the transaction and making decisions regarding next steps.
//                 * JWT will be empty if validate was not successful.
//                 *
//                 * @param validateResponse
//                 * @param serverJWT
//                 */
//                /**
//                 * This method is triggered when the transaction has been terminated. This is how SDK hands back
//                 * control to the merchant's application. This method will
//                 * include data on how the transaction attempt ended and
//                 * you should have your logic for reviewing the results of
//                 * the transaction and making decisions regarding next steps.
//                 * JWT will be empty if validate was not successful.
//                 *
//                 * @param validateResponse
//                 * @param serverJWT
//                 */
//
////                    handleValidation(validateResponse!!)
//                Log.e(" cca_continue ", " ===> " + validateResponse!!.errorDescription)
//            };
//
//
//        } catch (e: Exception) {
//            // Handle exception
//            Log.e(" Exception ", " ===> "+e.localizedMessage)
//
//        }
//    }


    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun setVisaLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.VISIBLE
        binding.cardLogoMastercardImg.visibility = View.INVISIBLE
        binding.cardLogoUnknownImg.visibility = View.INVISIBLE
    }

    private fun setMastercardLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.INVISIBLE
        binding.cardLogoMastercardImg.visibility = View.VISIBLE
        binding.cardLogoUnknownImg.visibility = View.INVISIBLE
    }

    private fun setUnknownLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.INVISIBLE
        binding.cardLogoMastercardImg.visibility = View.INVISIBLE
        binding.cardLogoUnknownImg.visibility = View.VISIBLE
    }

    private fun setCardLogoByType(cardName: Editable) {
        val typeVisa = '4'
        val mastercard = '5'
        val isEmpty = cardName.isBlank()

        if (isEmpty) {
            setUnknownLogoVisible()
            return
        }
        when (cardName.first()) {
            typeVisa -> setVisaLogoVisible()
            mastercard -> setMastercardLogoVisible()
            else -> setUnknownLogoVisible()
        }
    }

    private fun formatCardExpiryMonth(month: String): String {
        var formattedString = month

        if (formattedString.isEmpty()) {
            return ""
        }

        if (formattedString.take(1).toInt() > 1) {
            formattedString = ("0" + month).take(2)
        }

        if (formattedString.toInt() > 12) {
            formattedString = "1"
        }

        return formattedString
    }


    private fun formatCardExpiryYear(year: String): String {
        var formattedYear = year
        if (formattedYear.isEmpty()) {
            return ""
        }

        if (formattedYear.take(1).toInt() < 2) {
            formattedYear = if (formattedYear.length > 1) {
                formattedYear[1].toString()
            } else {
                ""
            }
        }
        return formattedYear
    }

    private fun checkFilled() {
        enable = isCardNumberFilled && isExpiryFilled && isCvvFilled
        binding.acbCreateCard.setButtonEnabled(enable)
    }

    private fun handleChangeListener() {

        binding.etNumberCard.addTextChangedListener {
            isCardNumberFilled = if (it != null && it.isNotEmpty()) {
                setCardLogoByType(it)
                val isFilled = it.toString().length == cardNumberLength
                isFilled
            } else {
                false
            }
            checkFilled()
        }

        binding.etCvv.addTextChangedListener {
            isCvvFilled = if (!it.isNullOrEmpty()) {
                it.toString().length >= MAX_LENGTH_CVV_CODE
            } else {
                false
            }
            checkFilled()
        }

        binding.monthField.addTextChangedListener { it ->
            if (!it.isNullOrEmpty()) {
                var formattedMonth: String

                it.toString().let {
                    formattedMonth = formatCardExpiryMonth(it)
                    if (formattedMonth != it) {
                        binding.monthField.setText(formattedMonth)
                        binding.monthField.setSelection(formattedMonth.length)
                    }
                }

                expiryMonth = formattedMonth.isNotEmpty()

                if (formattedMonth.length == 2) {
                    binding.yearField.requestFocus()
                }
                setExpiryDateFilled()
            } else {
                expiryMonth = false
            }
            checkFilled()
        }
        binding.yearField.addTextChangedListener { it ->
            if (!it.isNullOrEmpty()) {
                var formattedYear = ""
                it.toString().let {
                    formattedYear = formatCardExpiryYear(it)
                    if (formattedYear != it) {
                        binding.yearField.setText(formattedYear)
                        binding.yearField.setSelection(formattedYear.length)
                    }
                }
                expiryYear = formattedYear.isNotEmpty()
                if (expiryYear && it.toString().length >= 2) {
                    binding.etCvv.requestFocus()
                }
                setExpiryDateFilled()
            } else {
                expiryYear = false
            }
            checkFilled()
        }

        binding.acbCreateCard.setOnClickListener {
            hideKeyboard()
            generateCardOrderTackingId()
        }
    }

    private fun setExpiryDateFilled() {
        isExpiryFilled = expiryMonth && expiryYear
        checkFilled()
    }


    override fun onResume() {
        super.onResume()
        checkFilled()
    }


}