package com.pesapal.paygateway.activities.payment.fragment.card

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver
import com.google.gson.Gson
import com.pesapal.paygateway.activities.payment.model.cacontinie.*
import com.pesapal.paygateway.activities.payment.model.check3ds.BillingDetails
import com.pesapal.paygateway.activities.payment.model.check3ds.CardDetails3Ds
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.BillingAddress
import com.pesapal.paygateway.activities.payment.model.server_jwt.CardDetails
import com.pesapal.paygateway.activities.payment.model.server_jwt.RequestServerJwt
import com.pesapal.paygateway.activities.payment.model.server_jwt.response.OriginalPayloadModel
import com.pesapal.paygateway.activities.payment.model.server_jwt.response.ResponseServerJwt
import com.pesapal.paygateway.activities.payment.setButtonEnabled
import com.pesapal.paygateway.activities.payment.utils.FragmentExtension.hideKeyboard
import com.pesapal.paygateway.activities.payment.utils.Status
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import com.pesapal.paygateway.databinding.FragmentNewCardDetailsBinding
import org.json.JSONObject
import java.math.BigDecimal


class CardFragmentNewBilling : Fragment() {


    private var first_name: String? = ""
    private var last_name: String? = ""
    private var email: String? = ""
    private var phone: String? = ""
    private var amount: BigDecimal = BigDecimal.ONE
    private lateinit var order_id: String
    private lateinit var currency: String
    private lateinit var accountNumber: String
    private lateinit var callbackUrl: String
    var cardinal : Cardinal? = null
    var consumerSessionId : String? = null
    companion object {
        private const val MAX_LENGTH_CVV_CODE = 3
        private const val cardNumberLength = 19
        const val CARD_DATA = "data"
        fun newInstance(amount: BigDecimal, order_id: String, currency: String, accountNumber: String, callbackUrl: String, first_name: String?, last_name: String?, email: String?, phone: String? ): CardFragmentNewBilling {
            val fragment = CardFragmentNewBilling()
            fragment.amount = amount
            fragment.order_id = order_id
            fragment.currency = currency
            fragment.accountNumber = accountNumber
            fragment.callbackUrl = callbackUrl
            fragment.first_name = first_name
            fragment.last_name = last_name
            fragment.email = email
            fragment.phone = phone
            return fragment
        }
    }



    private lateinit var binding: FragmentNewCardDetailsBinding

    private var isCardNumberFilled = false
    private var isExpiryFilled = false
    private var isCvvFilled = false
    private var isCardNameFilled = false
    private var expiryMonth = false
    private var expiryYear = false
    private var enable = false

    var numberCard = " ";
    var checkEnableVisa = true
    var cvv = ""
    var nameOnCard = ""
    var expiryMonthData = ""
    var expiryYearData = ""
    var responseServerJwt: ResponseServerJwt? = null;
    private lateinit var pDialog: ProgressDialog

    private val viewModel: AppViewModel by activityViewModels()


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
        cardinal = Cardinal.getInstance()
        handleChangeListener()
        handleViewModel()
        binding.acbCreateCard.setButtonEnabled(true)
    }

    private fun handleChangeListener(){
        binding.etCardName.addTextChangedListener {
            isCardNameFilled = if(it != null && it.isNotEmpty()){
                it.isNotEmpty()
            }else{
                false
            }
            checkFilled()
        }

        binding.etNumberCard.addTextChangedListener {
            isCardNumberFilled = if(it != null && it.isNotEmpty()){
                setCardLogoByType(it)
                val isFilled = it.toString().length == cardNumberLength
                isFilled
            }else{
                false
            }
            checkFilled()
        }

        binding.etCvv.addTextChangedListener {
            isCvvFilled = if(it != null && it.isNotEmpty()){
                it.toString().length >= MAX_LENGTH_CVV_CODE
            }else{
                false
            }
            checkFilled()
        }

        binding.monthField.addTextChangedListener { it ->
            if(it != null && it.isNotEmpty()){
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
            }else{
                expiryMonth = false
            }
            checkFilled()
        }
        binding.yearField.addTextChangedListener { it ->
            if(it != null && it.isNotEmpty()){
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
            }else{
                expiryYear = false
            }
            checkFilled()
        }

        binding.acbCreateCard.setOnClickListener {
            hideKeyboard()
            createRequestMakeCard()
        }
    }


    private fun setExpiryDateFilled() {
        isExpiryFilled = expiryMonth && expiryYear
        checkFilled()
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

    override fun onResume() {
        super.onResume()
        checkFilled()
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

    private fun createRequestMakeCard() {
        numberCard = binding.etNumberCard.rawText.toString()
        checkEnableVisa = true
        cvv = binding.etCvv.text.toString()
        nameOnCard = binding.etCardName.text.toString()
        expiryMonthData = binding.monthField.text.toString()
        expiryYearData = binding.yearField.text.toString()

        var cardDetails = CardDetails(
            Integer.parseInt(expiryMonthData),
            cvv,
            numberCard,
            Integer.parseInt(expiryYearData)
        )

        var orderTrackingId = ""
        var authenticationTransactionId = ""
        var veresEnrolled = ""
        var specificationVersion = "1.108"

        val billingAddress = BillingAddress(phoneNumber = phone, emailAddress = email, countryCode = "KE", firstName = first_name,
            middleName = last_name,
            lastName = last_name,
            line = "",
            line2 = "",
            city = "Nairobi",
            state = "",
            postalCode = "",
            zipCode = "")

        var requestServerJwt = RequestServerJwt(
            BigDecimal("1500"),currency, billingAddress = billingAddress, cardDetails = cardDetails
        )

//        initSdk();


        viewModel.serverJwt(requestServerJwt)


    }


    private fun handleViewModel(){
        viewModel.serverJwt.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    responseServerJwt = it.data
                    initSdk(responseServerJwt!!.orderJwt)
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

        viewModel.dsToken.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
//                    pDialog = ProgressDialog(requireContext())
//                    pDialog.setMessage(it.message)
//                    pDialog.show()
                }
                Status.SUCCESS -> {
                    val token = it.data!!.token
                    get3dsPayload(token);
                }
                Status.ERROR -> {
//                    showMessage(it.message!!)
//                    pDialog.dismiss()
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }

        viewModel.dsResponse.observe(requireActivity()){

            when (it.status) {
                Status.LOADING -> {
//                    pDialog = ProgressDialog(requireContext())
//                    pDialog.setMessage(it.message)
//                    pDialog.show()
                }
                Status.SUCCESS -> {
                    var response = it.data
                    val gson = Gson()

                    var responseString = gson.toJson(response)
                    Log.e(" responseString ", responseString)

                    var payAcsUrlload = response?.acsUrl
                    var payload = response?.payload

                    handle3dSecure(response!!.authenticationTransactionId,payload!!,payAcsUrlload!!);
                }
                Status.ERROR -> {
//                    showMessage(it.message!!)
//                    pDialog.dismiss()
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun initSdk(serverJwt: String){


        cardinal?.init(serverJwt, object: CardinalInitService {
            /**
             * You may have your Submit button disabled on page load. Once you are set up
             * for CCA, you may then enable it. This will prevent users from submitting
             * their order before CCA is ready.
             */

            override fun onSetupCompleted(consumerSessionIds: String) {
                pDialog.dismiss()
                consumerSessionId = consumerSessionIds
                get3dToken()
            }

            /**
             * If there was an error with setup, cardinal will call this function with
             * validate response and empty serverJWT
             * @param validateResponse
             * @param serverJwt will be an empty
             */
            override fun onValidated(validateResponse: ValidateResponse, serverJwt: String?) {
                pDialog.dismiss()

            }
        })

    }

    private fun get3dToken(){
        val dsTokenRequest = DsTokenRequest("E71FC13D-5FD0-43EC-9E87-007586759EE0","677801C8-A971-46F8-957E-497213245E9B");
        viewModel.getDsToken(dsTokenRequest)
    }

    private fun get3dsPayload(token: String){




        var cardDetails = CardDetails3Ds(
            cvv,
            numberCard,
            expiryMonthData,
            Integer.parseInt(expiryYearData),
            null
        )


        val billingAddress = BillingDetails(
            phoneNumber = phone!!,
            email = email!!,
            country = "KE",
            currency=currency,
            firstName = first_name!!,
            lastName = last_name!!,
            city = "Nairobi",
            state = "",
            postalCode = "",
        )

        val checkDSecureRequest = CheckDSecureRequest(
            cardDetails,
            billingAddress,
            amount,
            "",
            consumerSessionId!!,
            currency,
            0,
            "",
            "SDK"
        )
        viewModel.check3ds(checkDSecureRequest,token)
    }


    private fun handle3dSecure(transactionId: String, payload: String,acsUrl: String ){

        try {

            var orderDetails = OrderDetails(
                "404",
                "P",
                amount = amount.toString(),
                orderNumber = "test_order",
                transactionId = consumerSessionId!!
            )

            var account = Account(
                nameOnAccount = first_name+""+last_name,
                cardCode = cvv,
                expirationMonth = expiryMonthData,
                expirationYear = "20"+expiryYearData,
                accountNumber =  numberCard
            )

            var consumer = Consumer(
                account = account
            )

            var ccaExtension = CCAExtension(
                merchantName = "PESAPAL LTD"
            )

            var payloadCanContinueModel = PayloadCanContinueModel(
                cCAExtension = ccaExtension,
                consumer = consumer,
                orderDetails = orderDetails,
                payload = payload,
                acsUrl = acsUrl,
            )

            val gson = Gson()
            var stringPayload = gson.toJson(payloadCanContinueModel)
            var stringPayloadv1 = stringPayload.replace("\\u0026", "&");
            var stringPayloadv2 = stringPayloadv1.replace("\\u003d", "=");

            Log.e(" stringPayloadv2 ",stringPayloadv2)

//            var jsonObject = JSONObject(stringPayloadv2)
//
//            Log.e(" jsonObject s",jsonObject.toString())



            cardinal?.cca_continue(transactionId,stringPayloadv2,requireActivity(),object: CardinalValidateReceiver{
                override fun onValidated(p0: Context?, validateResponse: ValidateResponse?, serverJWT: String?) {
                    /**
                     * This method is triggered when the transaction has been terminated. This is how SDK hands back
                     * control to the merchant's application. This method will
                     * include data on how the transaction attempt ended and
                     * you should have your logic for reviewing the results of
                     * the transaction and making decisions regarding next steps.
                     * JWT will be empty if validate was not successful.
                     *
                     * @param validateResponse
                     * @param serverJWT
                     */
//                    handleValidation(validateResponse!!)
                    Log.e(" cca_continue ", " ===> "+validateResponse!!.errorDescription)



                }

            });


        } catch (e: Exception) {
            // Handle exception
            Log.e(" Exception ", " ===> "+e.localizedMessage)

        }
    }

//
//    private fun handleValidation(validateResponse: ValidateResponse){
//        when (validateResponse.getActionCode()) {
//            ValidateResponse.SUCCESS -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//            ValidateResponse.NOACTION -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//            ValidateResponse.FAILURE -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//            ValidateResponse.CANCEL -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//            ValidateResponse.ERROR -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//            ValidateResponse.TIMEOUT -> {
//                Log.e(" cca_continue ", " ===> "+validateResponse.toString())
//
//            }
//        }
//    }




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

    private fun checkFilled(){
        Log.e("isCardNumberFilled"," ==> "+isCardNumberFilled)
        Log.e("isExpiryFilled"," ==> "+isExpiryFilled)
        Log.e("isCvvFilled"," ==> "+isCvvFilled)
        Log.e("isCardNameFilled"," ==> "+isCardNameFilled)
         enable = isCardNumberFilled && isExpiryFilled && isCvvFilled && isCardNameFilled
//        binding.acbCreateCard.setButtonEnabled(enable)
    }

}