package com.pesapal.paygateway.activities.payment.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.shared.models.Warning
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.pesapal.paygateway.BuildConfig
import com.pesapal.paygateway.R
import com.pesapal.paygateway.activities.payment.fragment.auth.AuthFragment
import com.pesapal.paygateway.activities.payment.fragment.card.CardFragmentNewAddress
import com.pesapal.paygateway.activities.payment.fragment.card.CardFragmentNewBilling
import com.pesapal.paygateway.activities.payment.fragment.main.MainPesapalFragment
import com.pesapal.paygateway.activities.payment.fragment.mpesa.pending.MpesaPendingFragment
import com.pesapal.paygateway.activities.payment.fragment.mpesa.stk.MpesaPesapalFragment
import com.pesapal.paygateway.activities.payment.fragment.success.MpesaSuccessFragment
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.activities.payment.utils.Status
import com.pesapal.paygateway.activities.payment.utils.TimeUtils
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import com.pesapal.paygateway.databinding.ActivityPesapalPayBinding
import org.json.JSONArray
import java.math.BigDecimal


class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding
    private var consumer_key: String = ""
    private var consumer_secret: String = ""
    private var amount: String? = "0"
    private var order_id: String? = null
    private var currency: String? = null
    private var accountNumber: String? = null
    private var callbackUrl: String? = null
    private var mobileMoneyRequest: MobileMoneyRequest? = null
    private var transactionStatusResponse: TransactionStatusResponse? = null
    private var ipn_url: String = "https://supertapdev.pesapalhosting.com/"
    private var ipn_notification_type: String = "GET"
    private var first_name: String? = ""
    private var last_name: String? = ""
    private var email: String? = ""
    private var phone: String? = ""
    private var cardinal: Cardinal = Cardinal.getInstance()

    private val viewModel: AppViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesapalPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        handleViewModel()
    }

    private fun initData(){
        fetchSharedData()
        handleClick()
        initCardinal()
    }

    private fun initCardinal(){
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
        if(BuildConfig.DEBUG){
            cardinalConfigurationParameters.environment =  CardinalEnvironment.STAGING
            cardinalConfigurationParameters.isEnableLogging =  true
        }else{
            cardinalConfigurationParameters.environment =  CardinalEnvironment.PRODUCTION
            cardinalConfigurationParameters.isEnableLogging =  false
        }

//     cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
//     cardinalConfigurationParameters.renderType = CardinalRenderType.OTP
//     cardinalConfigurationParameters.isLocationDataConsentGiven = true

        val yourUICustomizationObject = UiCustomization()
        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject
        cardinal.configure(this, cardinalConfigurationParameters)
        getAllWarnings()
    }

    private fun getAllWarnings(){
        val warnings: List<Warning> = cardinal.warnings

        for(warning in warnings){
            Log.e(" id ",warning.id);
            Log.e(" severity ",warning.severity.toString());
            Log.e(" message ",warning.message.toString());
        }

    }




    private fun handleClick(){
        binding.cancelPayment.setOnClickListener {
            returnPaymentStatus("failed")
        }

        binding.tvClose.setOnClickListener {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        val frag = fragment.toString()
        transaction.replace(R.id.frag_control, fragment,"list")
        transaction.addToBackStack(frag)
        transaction.commit()
    }

    private fun fetchSharedData(){
        val intent = intent
        if(intent != null){
            if(intent.getStringExtra("consumer_key") != null) {
                consumer_key = intent.getStringExtra("consumer_key")!!
            }
            if(intent.getStringExtra("consumer_secret") != null) {
                consumer_secret = intent.getStringExtra("consumer_secret")!!
            }

            amount = intent.getStringExtra("amount")
            order_id = intent.getStringExtra("order_id")
            currency = intent.getStringExtra("currency")
            accountNumber = intent.getStringExtra("accountNumber")
            callbackUrl = intent.getStringExtra("callbackUrl")
            first_name = intent.getStringExtra("firstName")
            last_name = intent.getStringExtra("lastName")
            email = intent.getStringExtra("email")
        }

        if(consumer_key != "" && consumer_secret != ""){
            val authRequestModel = AuthRequestModel(consumer_key,consumer_secret)
            authPayment(authRequestModel)
        }else{
            unableToAuth()
        }
    }

    private fun authPayment(authRequestModel: AuthRequestModel){
//        viewModel.authPayment(authRequestModel)
        viewModel.loadFragment("auth")
    }

    private fun registerIpn(){
        val registerIpnRequest = RegisterIpnRequest(ipn_url,ipn_notification_type)
        viewModel.registerIpn(registerIpnRequest)
    }

    private fun unableToAuth(){

    }

    private fun handleViewModel(){
        viewModel.authPaymentResponse.observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    val token = it.data?.token
                    PrefManager.setToken(token)
                    registerIpn()
                }
                Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }

        viewModel.registerIpnResponse.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(ipnId)
                    viewModel.loadFragment("choose")
                }
                Status.ERROR -> {
                }
                else -> {
                }
            }
        }

        viewModel.loadPendingMpesa.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    mobileMoneyRequest  = it.data
                    viewModel.loadFragment("pending_mpesa")
                }
                else ->{}
            }
        }

        viewModel.loadSuccessMpesa.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    transactionStatusResponse = it.data
                    viewModel.loadFragment("success_mpesa")
                }
                else ->{}
            }
        }

        viewModel.loadFragment.observe(this){
            when(it.message){
                "auth" -> {
                    loadFragment(AuthFragment.newInstance(consumer_key,consumer_secret))
                }
                "choose" -> {
                    val dateTime = TimeUtils.getCurrentDateTime()
                    loadFragment(MainPesapalFragment.newInstance(BigDecimal(amount).setScale(2).toString(),order_id!!,dateTime!!, currency!!))
                }
                "mpesa" -> {
                    loadFragment(MpesaPesapalFragment.newInstance(BigDecimal(amount),order_id!!,currency!!,accountNumber!!,callbackUrl!!, first_name, last_name, email, phone))
                }
                "success_mpesa" -> {
                    if(transactionStatusResponse != null) {
                        loadFragment(MpesaSuccessFragment.newInstance(transactionStatusResponse!!))
                    }
                }
                "pending_mpesa" -> {
                    if(mobileMoneyRequest != null) {
                        loadFragment(MpesaPendingFragment.newInstance(mobileMoneyRequest!!))
                    }else{
                        loadFragment(MpesaPendingFragment())
                    }
                }
                "card" -> {
                    loadFragment(CardFragmentNewAddress.newInstance(BigDecimal(amount),order_id!!,currency!!,accountNumber!!,callbackUrl!!,first_name,last_name,email,phone))
                }
                "card2" -> {
                    loadFragment(CardFragmentNewBilling.newInstance(BigDecimal(amount),order_id!!,currency!!,accountNumber!!,callbackUrl!!,first_name,last_name,email,phone))
                }
            }
        }

        viewModel.paymentDone.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    returnPaymentStatus(it.data!!)
                }
                else ->{}
            }
        }

    }


    private fun returnPaymentStatus(status: String){
        val returnIntent = Intent()
        returnIntent.putExtra("result", status)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

}