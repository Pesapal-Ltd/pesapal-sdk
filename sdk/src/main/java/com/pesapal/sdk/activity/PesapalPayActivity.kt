package com.pesapal.sdk.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.shared.models.Warning
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.pesapal.sdk.BuildConfig
import com.pesapal.sdk.R
import com.pesapal.sdk.fragment.card.data.CardFragmentCardData
import com.pesapal.sdk.fragment.mobile_money.mpesa.pending.MpesaPendingFragment
import com.pesapal.sdk.fragment.mobile_money.mpesa.stk.MpesaPesapalFragment
import com.pesapal.sdk.fragment.mobile_money.mpesa.success.MpesaSuccessFragment
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.viewmodel.AppViewModel
import com.pesapal.sdk.databinding.ActivityPesapalPayBinding
import com.pesapal.sdk.fragment.card.address.CardFragmentAddressData
import com.pesapal.sdk.fragment.details.MainPesapalFragment
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdk.utils.PrefManager
import org.json.JSONArray
import java.math.BigDecimal

/**
 * Author : Richard Kamere
 * Deprecated. Do not delete. Contains CARDINAL Logic
 * Previous implementation before using nav graph
 */

class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding
    private var paymentDetails: PaymentDetails? = null
    private var billingAddress: BillingAddress? = null
    private var mobileMoneyRequest: MobileMoneyRequest? = null
    private var transactionStatusResponse: TransactionStatusResponse? = null
    private var transactionErrorMessage: String? = null
    private var cardinal: Cardinal = Cardinal.getInstance()
    private val viewModel: AppViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesapalPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        handleViewModel()
    }

    private fun initData() {
        getPaymentData()
        handleClick()
        initCardinal()
    }

    private fun getPaymentData() {
        val intent = intent
        if (intent != null) {
            var consumerKey: String? = null
            var consumerSecret: String? = null
            var ipnUrl: String? = null
            var accountNumber: String? = null
            var callbackUrl: String? = null
            if (PrefManager.getString(this, PrefManager.con_key) != null) {
                consumerKey = PrefManager.getString(this, PrefManager.con_key)!!
            }

            if (PrefManager.getString(this, PrefManager.con_sec) != null) {
                consumerSecret = PrefManager.getString(this, PrefManager.con_sec)!!
            }

            if (PrefManager.getString(this, PrefManager.acc_num) != null) {
                accountNumber = PrefManager.getString(this, PrefManager.acc_num)!!
            }

            if (PrefManager.getString(this, PrefManager.call_url) != null) {
                callbackUrl = PrefManager.getString(this, PrefManager.call_url)!!
            }

            if (PrefManager.getString(this, PrefManager.ipn_url) != null) {
                ipnUrl = PrefManager.getString(this, PrefManager.ipn_url)!!
            }


            val amount = intent.getStringExtra(PESAPALAPI3SDK.AMOUNT)
            val orderId = intent.getStringExtra( PESAPALAPI3SDK.ORDER_ID)
            val currency = intent.getStringExtra(PESAPALAPI3SDK.CURRENCY)


            if (consumerKey.isNullOrEmpty() || consumerSecret.isNullOrEmpty()) {

                setErrorElements("Consumer data required ...")
                showMessage("Consumer data required ...")

            }
            else if(amount.isNullOrEmpty()){
                setErrorElements("Data -> Amount is missing")
            }
            else if(orderId.isNullOrEmpty()){
                setErrorElements("Data -> OrderId is missing")
            }
            else if(currency.isNullOrEmpty()){
                setErrorElements("Data -> Currency format is missing")
            }
            else if(accountNumber.isNullOrEmpty()){
                setErrorElements("Data -> Account number is missing")
            }
            else if(callbackUrl.isNullOrEmpty()){
                setErrorElements("Data -> Callback url is missing")
            }
            else if(ipnUrl.isNullOrEmpty()){
                setErrorElements("Data -> IPN is missing")
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
                )

                initData()

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

            }
            else{
                returnIntent(STATUS_CANCELLED, errorMessage)
            }


        } else {
            showMessage("Consumer data required ...")
        }

    }

    var dataRequiredAvailable = true
    var errorMessage = ""

    private fun setErrorElements(message: String){
        dataRequiredAvailable = false
        errorMessage = message
    }



    private fun handleClick() {
        binding.cancelPayment.setOnClickListener {
            handleError("CANCEL","Are you sure you want to cancel this transaction.")
        }

        binding.tvClose.setOnClickListener {
            handleError("CANCEL","Are you sure you want to cancel this transaction.")
        }

    }

    private fun handleViewModel() {
        viewModel.authPaymentResponse.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    val token = it.data?.token
                    PrefManager.setToken(this, token)
                    registerIpn()
                }
                com.pesapal.sdk.utils.Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }

        viewModel.registerIpnResponse.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(this, ipnId)
                    Log.e(" ipnId "," ==> " +ipnId);
                    viewModel.loadFragment("choose")
                }
                com.pesapal.sdk.utils.Status.ERROR -> {
                }
                else -> {
                }
            }
        }

        viewModel.loadPendingMpesa.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    mobileMoneyRequest = it.data
                    viewModel.loadFragment("pending_mpesa")
                }
                else -> {}
            }
        }

        viewModel.loadSuccessMpesa.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    binding.cancelPayment.visibility = View.GONE
                    transactionStatusResponse = it.data
                    viewModel.loadFragment("success_mpesa")
                }
                else -> {}
            }
        }

        viewModel.loadFragment.observe(this) {
            when (it.message) {
                "choose" -> {
                    loadFragment(MainPesapalFragment.newInstance(paymentDetails!!))
                }
                "mpesa" -> {
                    loadFragment(MpesaPesapalFragment.newInstance(billingAddress!!,paymentDetails!!))
                }
                "success_mpesa" -> {
                    if (transactionStatusResponse != null) {
                        binding.cancelPayment.visibility = View.GONE
                        loadFragment(MpesaSuccessFragment.newInstance(transactionStatusResponse!!))
                    }
                }
                "pending_mpesa" -> {
                    if (mobileMoneyRequest != null) {
                        loadFragment(MpesaPendingFragment.newInstance(mobileMoneyRequest!!))
                    } else {
                        loadFragment(MpesaPendingFragment())
                    }
                }
                "card" -> {
//                    loadFragment(CardFragmentAddressData.newInstance(billingAddress!!))
                }

            }


        }

        viewModel.paymentDone.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    Log.e(" status ", " == "+transactionStatusResponse)
                    returnPaymentStatus("COMPLETED")
                }
                else -> {

                }
            }
        }



        viewModel.loadCardDetails.observe(this) {
            when (it.status) {
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    billingAddress = it.data!!
                    loadFragment(CardFragmentCardData.newInstance(paymentDetails!!, billingAddress!!))
                }
                else -> {

                }
            }
        }

        viewModel.handleError.observe(this){
            when(it.status){
                com.pesapal.sdk.utils.Status.SUCCESS -> {
                    var result = it.data
                    handleError("ERROR",result!!.message!!)
                }
                com.pesapal.sdk.utils.Status.ERROR -> {
                    transactionErrorMessage = it.message!!
                    handleError("ERROR",transactionErrorMessage!!)
                }

                else -> {}
            }
        }


    }

    private fun registerIpn() {
        if(paymentDetails!!.ipn_url != null && paymentDetails!!.ipn_notification_type !=null) {
            val registerIpnRequest = RegisterIpnRequest(paymentDetails!!.ipn_url!!,paymentDetails!!.ipn_notification_type!!)
            viewModel.registerIpn(registerIpnRequest)
        }else{
            showMessage("Ipn Data Required")
        }
    }


    private fun returnPaymentStatus(status: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("status", status)
        when(status){
            "COMPLETED" -> {
                returnIntent.putExtra("data", transactionStatusResponse)
            }
            "ERROR" -> {
                returnIntent.putExtra("data", transactionErrorMessage)
            }
            "CANCELLED" -> {
                returnIntent.putExtra("data", "Transaction Cancelled")
            }
            else -> {
                returnIntent.putExtra("data", "An error occurred, Please try again later ...")
            }
        }

        setResult(RESULT_OK, returnIntent)
        finish()
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

        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        val frag = fragment.toString()
        transaction.replace(R.id.frag_control, fragment, "list")
        transaction.addToBackStack(frag)
        transaction.commit()
    }

    private fun handleError(header: String, message: String){
        var status = header
        if(header == "CANCEL"){
            status = "CANCELLED"
            returnPaymentStatus(status)
        }else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(header)
            builder.setCancelable(false)
            builder.setMessage(message)
            //Button One : Yes
            builder.setPositiveButton(
                "Okay"
            ) { dialog, which ->
                dialog.cancel()
                if (header == "CANCEL") {
                    status = "CANCELLED"
                }
                returnPaymentStatus(status)
            }

            val diag: AlertDialog = builder.create()
            diag.show()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getAllWarnings() {
        val warnings: List<Warning> = cardinal.warnings
        for (warning in warnings) {
            Log.e(" id ", warning.id);
            Log.e(" severity ", warning.severity.toString());
            Log.e(" message ", warning.message.toString());
        }
    }

    private fun initCardinal() {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
        cardinalConfigurationParameters.requestTimeout = 8000
        cardinalConfigurationParameters.challengeTimeout = 5
        val rTYPE = JSONArray()
        rTYPE.put(CardinalRenderType.OTP)
        rTYPE.put(CardinalRenderType.SINGLE_SELECT)
        rTYPE.put(CardinalRenderType.MULTI_SELECT)
        rTYPE.put(CardinalRenderType.OOB)
        rTYPE.put(CardinalRenderType.HTML)
        cardinalConfigurationParameters.renderType = rTYPE
        cardinalConfigurationParameters.uiType = CardinalUiType.BOTH

        if (BuildConfig.DEBUG) {
            cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
            cardinalConfigurationParameters.isEnableLogging = true
        } else {
            cardinalConfigurationParameters.environment = CardinalEnvironment.PRODUCTION
            cardinalConfigurationParameters.isEnableLogging = false
        }

//     cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
//     cardinalConfigurationParameters.renderType = CardinalRenderType.OTP
//     cardinalConfigurationParameters.isLocationDataConsentGiven = true

        val yourUICustomizationObject = UiCustomization()
        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject
        cardinal.configure(this, cardinalConfigurationParameters)
        getAllWarnings()
    }

    companion object {
        val STATUS_COMPLETED = "COMPLETED"
        val STATUS_CANCELLED = "CANCELLED"
    }


}