package com.pesapal.sdk.fragment.auth
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.databinding.FragmentAuthorizingBinding
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Status
import java.math.BigDecimal

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizingBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

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
        PrefManager.setToken(null)
        val authRequestModel = AuthRequestModel(paymentDetails.consumer_key,paymentDetails.consumer_secret)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.authPayment(authRequestModel)
        },800)
    }

    private fun handleViewModel(){
        viewModel.authResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.loader.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.loader.visibility = View.GONE
                    val token = it.data?.token
                    PrefManager.setToken(token)
                    registerIpn()
                }
                Status.ERROR -> {
                    binding.loader.visibility = View.GONE

                    showMessage(it.message!!)
                }

            }
        }

        viewModel.registerIpnResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val ipnId = it.data?.ipn_id
                    PrefManager.setIpnId(ipnId)
                    proceed()
                }

                Status.ERROR -> {
                }
                else -> {
                }
            }
        }

    }

    private fun proceed(){
        val action = AuthFragmentDirections.actionAuthFragmentToPesapalMainFragment(paymentDetails)
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
                initData()
            } else {
                showMessage("Consumer data required ...")
            }

        } else {
            showMessage("Consumer data required ...")
        }

    }


    private fun registerIpn() {
        if(paymentDetails.ipn_url != null && paymentDetails.ipn_notification_type !=null) {
            val registerIpnRequest = RegisterIpnRequest(paymentDetails.ipn_url!!, paymentDetails.ipn_notification_type!!)
            viewModel.registerIpn(registerIpnRequest)
        }else{
            showMessage("Ipn Data Required")
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }


}