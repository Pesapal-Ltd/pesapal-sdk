package com.pesapal.paygateway.activities.payment.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.shared.models.Warning
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.pesapal.paygateway.BuildConfig
import com.pesapal.paygateway.R
import com.pesapal.paygateway.activities.payment.fragment.auth.AuthFragment
import com.pesapal.paygateway.activities.payment.fragment.card.address.CardFragmentAddressData
import com.pesapal.paygateway.activities.payment.fragment.card.data.CardFragmentCardData
import com.pesapal.paygateway.activities.payment.fragment.card.success.CardPaymentSuccessFragment
import com.pesapal.paygateway.activities.payment.fragment.main.MainPesapalFragment
import com.pesapal.paygateway.activities.payment.fragment.mpesa.pending.MpesaPendingFragment
import com.pesapal.paygateway.activities.payment.fragment.mpesa.stk.MpesaPesapalFragment
import com.pesapal.paygateway.activities.payment.fragment.mpesa.success.MpesaSuccessFragment
import com.pesapal.paygateway.activities.payment.model.card.BillingAddress
import com.pesapal.paygateway.activities.payment.model.payment.PaymentDetails
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.activities.payment.utils.Status
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import com.pesapal.paygateway.databinding.ActivityPesapalPayBinding
import org.json.JSONArray
import java.math.BigDecimal


class PesapalPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesapalPayBinding
    private var paymentDetails: PaymentDetails? = null
    private var billingAddress: BillingAddress? = null

    private var mobileMoneyRequest: MobileMoneyRequest? = null
    private var transactionStatusResponse: TransactionStatusResponse? = null
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
            if (PrefManager.getString("consumer_key",null) != null) {
                consumerKey = PrefManager.getString("consumer_key",null)!!
            }

            if (PrefManager.getString("consumer_secret",null) != null) {
                consumerSecret = PrefManager.getString("consumer_secret",null)!!
            }

            if (PrefManager.getString("account_number",null) != null) {
                accountNumber = PrefManager.getString("account_number",null)!!
            }

            if (PrefManager.getString("callback_url",null) != null) {
                callbackUrl = PrefManager.getString("callback_url",null)!!
            }

            if (PrefManager.getString("ipn_url",null) != null) {
                ipnUrl = PrefManager.getString("ipn_url",null)!!
            }


            val amount = intent.getStringExtra("amount")
            val orderId = intent.getStringExtra("order_id")
            val currency = intent.getStringExtra("currency")

            paymentDetails = PaymentDetails(
                amount = BigDecimal(amount),
                order_id = orderId,
                currency = currency,
                accountNumber = accountNumber,
                callbackUrl = callbackUrl,
                consumer_key = consumerKey,
                consumer_secret =  consumerSecret,
                ipn_url = ipnUrl,
            )

            val firstName = intent.getStringExtra("firstName")
            val lastName = intent.getStringExtra("lastName")
            val email = intent.getStringExtra("email")
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

            if (consumerKey != "" && consumerSecret != "") {
                loadFragment(AuthFragment.newInstance(paymentDetails!!))
            } else {
                showMessage("Consumer data required ...")
            }

        } else {
            showMessage("Consumer data required ...")
        }

    }

    private fun handleClick() {
        binding.cancelPayment.setOnClickListener {
            returnPaymentStatus("failed")
        }

        binding.tvClose.setOnClickListener {
            returnPaymentStatus("failed")
        }

    }

    private fun handleViewModel() {
        viewModel.authPaymentResponse.observe(this) {
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

        viewModel.registerIpnResponse.observe(this) {
            when (it.status) {
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

        viewModel.loadPendingMpesa.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    mobileMoneyRequest = it.data
                    viewModel.loadFragment("pending_mpesa")
                }
                else -> {}
            }
        }

        viewModel.loadSuccessMpesa.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
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
                    loadFragment(CardFragmentAddressData.newInstance(billingAddress!!))
                }

            }


        }

        viewModel.paymentDone.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    returnPaymentStatus(it.data!!)
                }
                else -> {

                }
            }
        }

        viewModel.completeCardPayment.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    transactionStatusResponse = it.data
                    binding.cancelPayment.visibility = View.GONE
                    loadFragment(CardPaymentSuccessFragment.newInstance(transactionStatusResponse!!))
                }
                else -> {

                }
            }
        }


        viewModel.loadCardDetails.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    billingAddress = it.data!!
                    loadFragment(CardFragmentCardData.newInstance(paymentDetails!!, billingAddress!!))
                }
                else -> {

                }
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
        if(transactionStatusResponse != null) {
            returnIntent.putExtra("result", transactionStatusResponse)
        }
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


}