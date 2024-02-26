package com.pesapal.sdk.fragment.details

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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.pesapal.sdk.activity.PesapalPayActivity
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.adapter.PaymentAdapter
import com.pesapal.sdk.databinding.FragmentPaymentMethodsBinding
import com.pesapal.sdk.fragment.card.viewmodel.CardViewModel
import com.pesapal.sdk.fragment.mobile_money.mpesa.pending.MpesaPendingFragment
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
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_KE
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_TZ
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_UG
import com.pesapal.sdk.utils.CountryCodeEval.CARD
import com.pesapal.sdk.utils.CountryCodeEval.MPESA
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_TZ
import com.pesapal.sdk.utils.CountryCodeEval.MTN_UG
import com.pesapal.sdk.utils.CountryCodeEval.TIGO_TANZANIA
import java.math.BigDecimal

internal class PaymentMethodsFragment: Fragment(), PaymentAdapter.PaymentMethodInterface {
    private lateinit var binding: FragmentPaymentMethodsBinding

    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()
    private val viewModel: MpesaPesapalViewModel by viewModels()

    private var mobileProviders = listOf<Int>()
    private var selectedChip: Int = -1

    lateinit var rvPayment: RecyclerView

    // Mobile Money
    var phoneNumber =  ""
    var mobileProvider = 0
    private lateinit var pDialog: ProgressDialog
    private var mobileMoneyResponse: MobileMoneyResponse? = null
    lateinit var paymentAdapter: PaymentAdapter

    private var timerStated = false
    private var timerStatus = MpesaPendingFragment.TimerStatus.STOPPED

    private lateinit var mobileMoneyRequest: MobileMoneyRequest

    private var delayTime = 1000L
    private val timeCountInMilliSeconds = 30000L

    private val mobilePendingvViewModel: MpesaPendingViewModel by viewModels()
    // Mobile Money


    //Card
    private val cardViewModel: CardViewModel by viewModels()



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
        paymentDetails = requireArguments().getSerializable("paymentDetails") as PaymentDetails
        billingAddress = requireArguments().getSerializable("billingAddress") as BillingAddress

        initData()
        handleCustomBackPress()
        demoViewInform()
        mobileProviders = evaluateRegionProvider()
        initRecycler()

        handleViewModel()

    }

    private fun evaluateRegionProvider(): List<Int> {
         return when(paymentDetails.country){
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_KE ->{
                 CountryCodeEval.kenyaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_TZ -> {
                 CountryCodeEval.tanzaniaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_UG -> {
                 CountryCodeEval.ugandaProvider
            }
            else -> {
                listOf()
            }
        }
    }

    /**
     * If the application is in demo show text view indicating so
     */
    private fun demoViewInform(){
        val isLive = PrefManager.getBoolean(PrefManager.PREF_IS_URL_LIVE, true)
        binding.tvDemoVersion.isVisible =  !isLive
    }


    private fun handleCustomBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    returnIntent(PesapalPayActivity.STATUS_CANCELLED, "Payment cancelled")
                }
            })
    }

    private fun initRecycler(){
        val payList = mutableListOf<PaymentInterModel>()
        payList.add(PaymentInterModel(CARD, "Card"))
        mobileProviders.forEach{ providerInt ->
            val provider = CountryCodeEval.mappingAllCountries[providerInt]
            payList.add(PaymentInterModel(provider!!.paymentMethodId, provider.mobileProvider))
        }

        rvPayment = binding.rvPaymentMethods
        paymentAdapter = PaymentAdapter(billingAddress, requireContext(), this, payList)
        rvPayment.adapter = paymentAdapter
    }


    private fun initData(){
        binding.tvAmount.text = "${paymentDetails.currency} ${paymentDetails.amount}"
        binding.tvMerchantName.text = paymentDetails.merchant_name
    }


    private fun proceedSelected(i : Int){
        when (i) {
            CARD -> {
                // todo work on unselecting all other chips
                //    configureUnselectedChip(chipGroup.findViewById(R.id.mpesa))
                proceedToCard()
            }
            MPESA,AIRTEL_KE, MTN_UG, AIRTEL_UG, TIGO_TANZANIA, MPESA_TZ, AIRTEL_TZ -> {
//                    configureUnselectedChip(chipGroup.findViewById(R.id.card))
                proceedMpesa(i)
            }

        }
    }

    private fun proceedMpesa(i: Int) {
        val mobileProvider = CountryCodeEval.mappingAllCountries[i]
        val min = mobileProvider!!.minimumAmount
        val canProceedMinMeet = paymentDetails.amount >= min.toBigDecimal()
        if(canProceedMinMeet) {
            paymentDetails.mobile_provider = BigDecimal(i)
            val action = PaymentMethodsFragmentDirections.actionPesapalMainFragmentToNavGraphMpesa(
                paymentDetails,
                billingAddress
            )
            clearSelectionAndProceed(action)
        }
        else{
            Toast.makeText(requireContext(), "Minimum amount for ${mobileProvider.mobileProvider} is $min/=", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMinMaxAmount(mobileProvider: Int){
    }

    private fun proceedToCard(){
        val action = PaymentMethodsFragmentDirections.actionPesapalMainFragmentToPesapalCardFragment(paymentDetails, billingAddress)
        clearSelectionAndProceed(action)
    }

    /**
     * The chip group selection needs to cleared or it causes a loop and return to the selected chip navigating to the fragment
     */
    private fun clearSelectionAndProceed(action: NavDirections) {
//        binding.paymentOptionGroup.clearCheck()
        findNavController().navigate(action)
    }


    /**
     * todo delete. modifications to the style affect the color scheme
     *  Use this for payment method un selected. UI and data
     */
    private fun configureUnselectedChip(chip: Chip) {

    }

    /**
     * todo delete. modifications to the style affect the color scheme
     *  Use this for payment method selected. UI and data
     */
    private fun configureSelectedChip(chip: Chip) {
//
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
                mobilePendingvViewModel.sendMobileMoneyCheckOut(request, "Sending payment prompt ...")
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
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = true
        );
    }

    override fun refreshRv() {
        Log.e("Pa","Refresh")
        if(!rvPayment.isComputingLayout){
            paymentAdapter.notifyDataSetChanged()
        }
        else{
            Handler()
                .postDelayed({
                              refreshRv()

            }, 500)
        }
    }

    /**
     * Mobile money view model
     */
    private fun handleViewModel(){
        mobilePendingvViewModel.mobileMoneyResponse.observe(requireActivity()){
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
                    showMessage(it.message!!)
                    pDialog.dismiss()
                }
                else -> {

                }
            }
        }

        mobilePendingvViewModel.transactionStatus.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    pDialog = ProgressDialog(requireContext())
                    pDialog.setMessage(it.message)
                    pDialog.show()
                }
                Status.SUCCESS -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                        proceedToTransactionResultScreen(it.data!!, true)
                    }
                }
                Status.ERROR -> {
                    if(::pDialog.isInitialized) {
                        pDialog.dismiss()
                        showMessage(it.message!!)
                        proceedToTransactionResultScreen(it.data!!, false)

                    }
                }
            }
        }

        mobilePendingvViewModel.transactionStatusBg.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    if(!timerStated) {
                        timerStated = true
//                        handleBackgroundCheck()
                        Log.e("Meth","Started")
                    }
                }
                Status.SUCCESS -> {
//                    handleTimeStop()
                    proceedToTransactionResultScreen(it.data!!, true)
                }
                Status.ERROR -> {
                    if(delayTime != 30000L){
                        delayTime += 1000
                        handleBackgroundConfirmation()
                    }

                }
                else -> {
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

        cardViewModel.submitCardResponse.observe(requireActivity()){
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

        cardViewModel.cardPaymentStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    pDialog.dismiss()
                    proceedToTransactionResultScreen(it.data!!, true)
                }
                Status.ERROR -> {
                    showMessage(it.message!!)
                    pDialog.dismiss()

                    proceedToTransactionResultScreen(it.data!!, false)
                }

                else -> {
                    pDialog.dismiss()
                }
            }
        }

//        cardViewModel.serverJwt.observe(requireActivity()){
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
//        cardViewModel.dsToken.observe(requireActivity()){
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
//        cardViewModel.dsResponse.observe(requireActivity()){
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
            tokenizeCard = tokenize
        )
//{
//  "order_tracking_id": "33578938-582a-4044-a199-dd8c23567740",
//  "merchant_reference": "43A885C5",
//  "error": {
//    "error_type": "channel_error",
//    "code": "transaction_declined",
//    "message": ""
//  },
//  "status": "500",
//  "call_back_url": "http://localhost:56522"
//}
        cardViewModel.submitCardRequest(submitCardRequest)

    }

    override fun handleResend(){
        delayTime = 1000L
        var mobileMoneyRequest: MobileMoneyRequest = mobileMoneyRequest
        mobileMoneyRequest.trackingId =  mobileMoneyRequest.trackingId
        viewModel.sendMobileMoneyCheckOut(mobileMoneyRequest,"Resending OTP ...")
    }


//    private fun hideDialog() {
//        timerStated = false
//        if(mobileProvider.contains(CountryCodeEval.MPESA_PROV_NAME)) {
//            showLipaNaMpesa()
//        }
//        else{
//            binding.btnSendLipab.visibility = View.VISIBLE
//
//        }
//    }


//    private fun handleBackgroundCheck(){
//        startStop()
//    }


//    private fun startStop() {
//        if (timerStatus == MpesaPendingFragment.TimerStatus.STOPPED) {
//            // call to initialize the progress bar values
//            setProgressBarValues(binding.progressBarCircle)
//            // showing the reset icon
//            timerStatus = MpesaPendingFragment.TimerStatus.STARTED
//            // call to start the count down timer
//            startCountDownTimer()
//        } else {
//
//            // changing the timer status to stopped
//            timerStatus = MpesaPendingFragment.TimerStatus.STOPPED
//            stopCountDownTimer()
//        }
//
//    }

//    private fun startCountDownTimer() {
//        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000L) {
//            override fun onTick(millisUntilFinished: Long) {
//                val milis = hmsTimeFormatter(millisUntilFinished)
//                binding.tvTime.text = "00:$milis"
//                val progress = millisUntilFinished / 1000
//                binding.progressBarCircle.progress = progress.toInt()
//                //                checkMpesa();
//            }
//
//            override fun onFinish() {
//                binding.tvTime.text = hmsTimeFormatter(timeCountInMilliSeconds)
//                // call to initialize the progress bar values
//                setProgressBarValues(binding.progressBarCircle)
//                timerStatus = MpesaPendingFragment.TimerStatus.STOPPED
//                stopCountDownTimer()
//            }
//        }.start()
//    }



    private fun proceedToTransactionResultScreen(transactionStatusResponse: TransactionStatusResponse, isTxnSuccess: Boolean){
        pesapalSdkViewModel.merchantName = paymentDetails.merchant_name             //todo move to authFragment
        var action = PaymentMethodsFragmentDirections.actionPaymentFragmentToPaymentStatusFragment(transactionStatusResponse,isTxnSuccess)
        findNavController().navigate(action)

        // todo change the mpesa success page
    }

    private fun showPendingMpesaPayment(){
//        mobilePendingvViewModel.resetMobileResponse()   todo transfer logic to other viewmode

        mobileMoneyRequest = prepareMobileMoney()
        mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        paymentDetails.order_tracking_id = mobileMoneyResponse!!.orderTrackingId

        if(mobileMoneyResponse != null) {
            mobileMoneyRequest.trackingId = mobileMoneyResponse!!.orderTrackingId
        }

        paymentAdapter.mobileMoneyUpdate()
        handleBackgroundConfirmation()
    }


    override  fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun handleBackgroundConfirmation(){
        Handler().postDelayed({
            // todo store the mobile request in the view model
            mobilePendingvViewModel.mobileMoneyTransactionStatusBackground(mobileMoneyRequest.trackingId)
        },delayTime)
    }

    override fun handleConfirmation(){
        mobilePendingvViewModel.mobileMoneyTransactionStatus(mobileMoneyRequest.trackingId)
    }


    private lateinit var cardDetails: CardDetails
    private lateinit var cardOrderTrackingIdRequest: CardOrderTrackingIdRequest

    override fun generateCardOrderTrackingId(billingAddress: BillingAddress,tokenize:Boolean, cardNumber: String, year:Int, month: Int,cvv:String) {
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
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress,
            trackingId = "",
            chargeRequest = false
        )

        cardViewModel.generateCardOrderTrackingId(cardOrderTrackingIdRequest, " Processing request ...")

    }
    var tokenize = false

}