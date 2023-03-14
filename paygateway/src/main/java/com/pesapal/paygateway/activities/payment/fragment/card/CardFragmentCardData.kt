package com.pesapal.paygateway.activities.payment.fragment.card

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
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
import com.google.gson.Gson
import com.pesapal.paygateway.activities.payment.model.card.CardDetails
import com.pesapal.paygateway.activities.payment.model.card.submit.request.EnrollmentCheckResult
import com.pesapal.paygateway.activities.payment.model.card.submit.request.SubmitCardRequest
import com.pesapal.paygateway.activities.payment.model.card.submit.request.SubscriptionDetails
import com.pesapal.paygateway.activities.payment.model.check3ds.CardDetails3Ds
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.paygateway.activities.payment.model.card.BillingAddress
import com.pesapal.paygateway.activities.payment.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.paygateway.activities.payment.model.payment.PaymentDetails
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.server_jwt.response.ResponseServerJwt
import com.pesapal.paygateway.activities.payment.setButtonEnabled
import com.pesapal.paygateway.activities.payment.utils.FragmentExtension.hideKeyboard
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.activities.payment.utils.Status
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import com.pesapal.paygateway.databinding.FragmentNewCardDetailsBinding

class CardFragmentCardData : Fragment() {

    private val viewModel: AppViewModel by activityViewModels()
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
    var cardinal: Cardinal? = null

    companion object {
        private const val MAX_LENGTH_CVV_CODE = 3
        private const val cardNumberLength = 19
        fun newInstance(
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
        initData()
    }

    private fun initData() {
        cardinal = Cardinal.getInstance()
        handleViewModel()
        handleChangeListener()
    }

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
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = false
        );


        viewModel.generateCardOrderTrackingId(cardOrderTrackingIdRequest, " Processing request ...")

    }
    private fun submitCardRequest() {
        var enrollmentCheckResult = EnrollmentCheckResult(
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

        var subscriptionDetails = SubscriptionDetails(
            endDate = "0001-01-01T00:00:00",
            startDate = "0001-01-01T00:00:00",
            amount = 0,
            accountReference = null,
            frequency = 0
        )

        var submitCardRequest = SubmitCardRequest(
            cvv = cardDetails.cvv,
            enrollmentCheckResult = enrollmentCheckResult,
            subscriptionDetails = subscriptionDetails,
            orderTrackingId = paymentDetails.order_tracking_id,
            expiryMonth = cardDetails.month.toString(),
            billingAddress = billingAddress,
            expiryYear = cardDetails.year.toString(),
            ipAddress = "1",
            cardNumber = cardDetails.cardNumber
        )

        viewModel.submitCardRequest(submitCardRequest)
    }

    private fun checkCardPaymentStatus(){
        viewModel.checkCardPaymentStatus(paymentDetails.order_tracking_id!!)
    }

    private fun handleCompletePayment(status: String){
        Handler().postDelayed({
            viewModel.handlePaymentStatus(status)
        },1000)
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
                    var result = it.data!!
                    showMessage(result.description!!)
                    handleCompletePayment("COMPLETED")
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

    }





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
            isCvvFilled = if (it != null && it.isNotEmpty()) {
                it.toString().length >= MAX_LENGTH_CVV_CODE
            } else {
                false
            }
            checkFilled()
        }

        binding.monthField.addTextChangedListener { it ->
            if (it != null && it.isNotEmpty()) {
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
            if (it != null && it.isNotEmpty()) {
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