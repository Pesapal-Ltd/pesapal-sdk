package com.pesapal.sdk.fragment.details

import DeviceFingerprint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pesapal.sdk.R
import com.pesapal.sdk.activity.PesapalSdkActivity.Companion.STATUS_CANCELLED
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.adapter.PaymentAdapter
import com.pesapal.sdk.databinding.FragmentPaymentMethodsBinding
import com.pesapal.sdk.fragment.DialogCard
import com.pesapal.sdk.fragment.card.viewmodel.CardViewModel
import com.pesapal.sdk.fragment.mobile_money.mpesa.pending.MpesaPendingViewModel
import com.pesapal.sdk.fragment.mobile_money.mpesa.stk.MpesaPesapalViewModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.card.CardDetails
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.submit.request.EnrollmentCheckResult
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.request.SubscriptionDetails
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.*
import com.pesapal.sdk.utils.CountryCodeEval.CARD

internal class PaymentMethodsFragment: Fragment(), PaymentAdapter.PaymentMethodInterface {
    private lateinit var binding: FragmentPaymentMethodsBinding

    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()
    private val viewModel: MpesaPesapalViewModel by viewModels()

    private var mobileProviders = listOf<Int>()
    lateinit var rvPayment: RecyclerView

    // Mobile Money
    var phoneNumber =  ""
    var mobileProvider = 0
    private lateinit var pDialog: ProgressDialog
    private var mobileMoneyResponse: MobileMoneyResponse? = null
    lateinit var paymentAdapter: PaymentAdapter

    private lateinit var mobileMoneyRequest: MobileMoneyRequest

    private var delayTime = 25000L

    private val mobilePendingViewModel: MpesaPendingViewModel by viewModels()
    // Mobile Money


    //Card

    private val cardViewModel: CardViewModel by viewModels()
    private lateinit var cardDetails: CardDetails
    private lateinit var cardOrderTrackingIdRequest: CardOrderTrackingIdRequest
    var tokenize = false

    //Card

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentMethodsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentDetails = pesapalSdkViewModel.paymentDetails!!
        billingAddress = pesapalSdkViewModel.billingAddress!!

        initData()
        handleCustomBackPress()
        demoViewInform()
        mobileProviders = evaluateRegionProvider()
        initRecycler()

        handleViewModel()

    }

    private fun evaluateRegionProvider(): List<Int> {
        val labelRegulated = binding.labelRegulated

         return when(paymentDetails.country){
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_KE ->{
                 CountryCodeEval.kenyaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_TZ -> {
                labelRegulated.visibility = View.INVISIBLE
                CountryCodeEval.tanzaniaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_UG -> {
                labelRegulated.text = getString(R.string.pesapal_is_regulated_by_the_uganda)
                 CountryCodeEval.ugandaProvider
            }
            else -> {
                labelRegulated.visibility = View.INVISIBLE
                listOf()
            }
        }
    }

    /**
     * If the application is in demo show text view indicating so
     */
    private fun demoViewInform(){
        val isLive = PrefManager.getBoolean(requireContext(), PrefManager.PREF_IS_URL_LIVE, true)
        binding.tvDemoVersion.isVisible =  !isLive
    }


    private fun handleCustomBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    // todo Move this to the activity to have a common method to return data

                    returnIntent(STATUS_CANCELLED, "Payment cancelled")
                }
            })
    }

    private fun initRecycler(){
        val payList = mutableListOf<CountryCode>()
        payList.add(CountryCode("Card",CARD,0) )
        mobileProviders.forEach{ providerInt ->
            val provider = CountryCodeEval.mappingAllCountries[providerInt]
            payList.add(provider!!)
        }

        rvPayment = binding.rvPaymentMethods
        paymentAdapter = PaymentAdapter( requireContext(),paymentDetails.currency, paymentDetails.amount, billingAddress, this, payList)
        rvPayment.adapter = paymentAdapter
    }


    private fun initData(){
        binding.tvAmount.text = "${paymentDetails.currency} ${paymentDetails.amount}"
        binding.tvMerchantName.text = paymentDetails.merchant_name
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

    override fun mobileMoneyRequest(action: Int, phoneNumber: String, mobileProvider: Int) {
        when(action){
            1 -> {

                this.phoneNumber = phoneNumber
                this.mobileProvider = mobileProvider

                val request = prepareMobileMoney()
                mobilePendingViewModel.sendMobileMoneyCheckOut(request, "Sending payment prompt ...")
            }
        }
    }


    private fun prepareMobileMoney(): MobileMoneyRequest {
        hideKeyboard()
        val phoneNumber =  phoneNumber
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
            notificationId = PrefManager.getIpnId(requireContext()),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = true
        );
    }

    override fun refreshRv() {
        if(!rvPayment.isComputingLayout){
            paymentAdapter.notifyDataSetChanged()
        }
        else{
            Handler()
                .postDelayed({ refreshRv() }, 500)
        }
    }

    /**
     * Mobile money view model
     */
    private fun handleViewModel(){
        mobilePendingViewModel.mobileMoneyResponse.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.setCancelable(false)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    pDialog.dismiss()
                    mobileMoneyResponse = it.data
                    showPendingMpesaPayment()
                }
                Status.ERROR -> {
                    var message = it.message
                    if(message!!.contains("payment has already been received")){
                        message = "Confirming payment status"
                        handleBackgroundConfirmation(0)
                    }
                    else{
                        it.message
                    }
                    showMessage(message)
                    pDialog.dismiss()
                }
            }
        }

        mobilePendingViewModel.transactionStatus.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                    }
                    val checkStatus = it.data!!
                    val success = (checkStatus.statusCode == 1)
                    proceedToTransactionResultScreen(checkStatus, success)
                }
                Status.ERROR -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                        showMessage(it.message!!)
//                        proceedToTransactionResultScreen(it.data!!, false)

                    }
                }
            }
        }

        mobilePendingViewModel.transactionStatusBg.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                    }
                    proceedToTransactionResultScreen(it.data!!, true)
                }
                Status.ERROR -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                    }
                    val data = it.data
                    if(data == null){
                        // todo stop timer
                    }
                    else {
                        // todo for status code 0 maybe track a max of 4 calls to check status then display the error
                        when(data.statusCode){
                            1 -> {
                                proceedToTransactionResultScreen(it.data, true)
                            }
                            2,3 ->{
                                proceedToTransactionResultScreen(it.data, false)
                            }

                        }
                    }
                }
            }
        }

        cardViewModel.cardOrderTrackingIdResponse.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    val result = it.data!!
                    when (result.statusCode) {
                        2 -> {
                            proceedToTransactionResultScreen(TransactionStatusResponse(createdDate = result.createdDate,
                                paymentMethod = result.paymentMethod, currency = result.currency, amount = result.amount.toDouble(),
                                confirmationCode = result.confirmationCode, merchantReference = result.merchantReference,
                                paymentAccount = result.paymentAccount, orderTrackingId = result.orderTrackingId,status = result.status ), false)
                        }
                        else -> {
                            paymentDetails.order_tracking_id = result.orderTrackingId
                            submitCardRequest()
                        }
                    }
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }

            }
        }

        cardViewModel.submitCardResponse.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    checkCardPaymentStatus()
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()
                    checkCardPaymentStatus()

                }
            }
        }

        cardViewModel.cardPaymentStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    pDialog.dismiss()
                    proceedToTransactionResultScreen(it.data!!, it.data.statusCode == 1)
                }
                Status.ERROR -> {
                    pDialog.dismiss()
                    proceedToTransactionResultScreen(it.data!!, false)
                }

            }
        }
    }

    private fun checkCardPaymentStatus(){
        cardViewModel.checkCardPaymentStatus(paymentDetails.order_tracking_id!!)
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

        val fingerPrint = DeviceFingerprint(requireContext()).createFingerprint()
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
            tokenizeCard = tokenize,
            deviceId = fingerPrint.device_id
        )

        cardViewModel.submitCardRequest(submitCardRequest)
    }


    override fun handleResend(){
        delayTime = 1000L
        var mobileMoneyRequest: MobileMoneyRequest = mobileMoneyRequest
        mobileMoneyRequest.trackingId =  mobileMoneyRequest.trackingId
        viewModel.sendMobileMoneyCheckOut(mobileMoneyRequest,"Resending OTP ...")
    }


    private fun proceedToTransactionResultScreen(transactionStatusResponse: TransactionStatusResponse, isTxnSuccess: Boolean){
        pesapalSdkViewModel.merchantName = paymentDetails.merchant_name             //todo move to authFragment
        pesapalSdkViewModel.disableBgCheck = true

        val action = PaymentMethodsFragmentDirections.actionPaymentFragmentToPaymentStatusFragment(transactionStatusResponse,isTxnSuccess)
        findNavController().navigate(action)
    }

    private fun showPendingMpesaPayment(){
        mobileMoneyRequest = prepareMobileMoney()
        mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        paymentDetails.order_tracking_id = mobileMoneyResponse!!.orderTrackingId

        if(mobileMoneyResponse != null) {
            mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        }

        paymentAdapter.mobileMoneyUpdate(mobileMoneyResponse)
        handleBackgroundConfirmation()
    }


    override  fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun handleBackgroundConfirmation(autodelay:Long = delayTime){
        if(!pesapalSdkViewModel.disableBgCheck) {
            Handler().postDelayed({
                // todo store the mobile request in the view model
                mobilePendingViewModel.mobileMoneyTransactionStatusBackground(mobileMoneyRequest.trackingId)
            }, autodelay)
        }
    }

    override fun handleConfirmation(){
        mobilePendingViewModel.mobileMoneyTransactionStatus(mobileMoneyRequest.trackingId)
    }

    override fun generateCardOrderTrackingId(billingAddress: BillingAddress,tokenize:Boolean, cardNumber: String, year:Int, month: Int,cvv:String) {
        pesapalSdkViewModel.disableBgCheck = true
        this.billingAddress = billingAddress
        this.tokenize = tokenize
        hideKeyboard()
        cardDetails = CardDetails(
           cardNumber,
            year,
            month,
            cvv,
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
            notificationId = PrefManager.getIpnId(requireContext()),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = false
        )

        cardViewModel.generateCardOrderTrackingId(cardOrderTrackingIdRequest, " Processing request ...")

    }

    override fun showDialogFrag(dialogType: Int) {
        DialogCard(dialogType).show(parentFragmentManager, dialogType.toString())
    }


}