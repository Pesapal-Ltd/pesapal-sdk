package com.pesapal.sdk.fragment.card.address

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.R
import com.pesapal.sdk.databinding.FragmentNewCardAddressBinding
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.setButtonEnabled
import com.pesapal.sdk.utils.PrefManager
import java.math.BigDecimal


class CardFragmentAddressData : Fragment() {

    private lateinit var binding: FragmentNewCardAddressBinding
    private lateinit var billingAddress: BillingAddress
    private lateinit var paymentDetails: PaymentDetails
    private var isFirstNameFilled = false
    private var isSurnameFilled = false
    private var isEmailFilled = false
    private var isPhoneFilled = false
    private var isAddressFilled = false
    private var isPostalCodeFilled = false
    private var isCityFilled = false
    private var enable = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCardAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentData()
        handleClickListener()
        handleChangeListener()
        initData()
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


        } else {
            showMessage("Consumer data required ...")
        }

    }


    private fun initData(){
        binding.etFirstName.setText(billingAddress.firstName)
        binding.etSurname.setText(billingAddress.lastName)
        binding.etEmail.setText(billingAddress.emailAddress)
        binding.etPhoneNumber.setText(billingAddress.phoneNumber)
        binding.etAddress.setText(billingAddress.zipCode)
        binding.etPostal.setText(billingAddress.postalCode)
        binding.etCity.setText(billingAddress.city)
    }

    private fun handleChangeListener(){
        binding.etFirstName.addTextChangedListener {
                isFirstNameFilled = it.toString().isNotEmpty()
            checkFilled()

        }

        binding.etSurname.addTextChangedListener {
            isSurnameFilled = it.toString().isNotEmpty()
            checkFilled()

        }

        binding.etEmail.addTextChangedListener {
            if(it != null && it.isNotEmpty()) {
                isEmailFilled = checkValidEmail(it.toString())
                checkFilled()
            }else{
                isEmailFilled = false
            }

        }


        binding.etPhoneNumber.addTextChangedListener {

            if(it != null && it.isNotEmpty()){
                val minLength = 3
                val phoneText = it.toString()
                isPhoneFilled = phoneText.length > minLength
                checkFilled()
            }else{
                isPhoneFilled = false
            }


        }
        binding.etAddress.addTextChangedListener {
            isAddressFilled = it.toString().isNotEmpty()
            checkFilled()

        }
        binding.etPostal.addTextChangedListener {
            if(it != null && it.isNotEmpty()){
                val minPostalCodeLength = 2
                val postalCodeText = it.toString()
                isPostalCodeFilled = postalCodeText.length > minPostalCodeLength

                var postalCodeError = ""
                if (!isPostalCodeFilled) {
                    postalCodeError = "Postal Code Too Short"
                }
                binding.etPostal.error = postalCodeError.ifEmpty { null }
                isPostalCodeFilled = it.toString().isNotEmpty()
                checkFilled()
            }else{
                isPostalCodeFilled = false
            }


        }
        binding.etCity.addTextChangedListener {
            isCityFilled = it.toString().isNotEmpty()
            checkFilled()

        }
    }

    private fun handleClickListener(){
        binding.acbNextStep.setOnClickListener {
            val billingAddress = BillingAddress(
                phoneNumber = binding.etPhoneNumber.text.toString(),
                emailAddress = binding.etEmail.text.toString(),
                countryCode = binding.countryCodePicker.selectedCountryNameCode,
                firstName = binding.etFirstName.text.toString(),
                middleName = binding.etSurname.text.toString(),
                lastName = binding.etSurname.text.toString(),
                line = binding.etAddress.text.toString(),
                line2 = binding.etAddress.text.toString(),
                city = binding.etCity.text.toString(),
                state = " ",
                postalCode = binding.etPostal.text.toString(),
                zipCode = binding.etAddress.text.toString(),
                )

            val action = CardFragmentAddressDataDirections.actionPesapalCardFragmentAddressToPesapalCardFragmentCardData(paymentDetails,billingAddress)
            findNavController().navigate(action)
        }
    }

    private fun checkFilled(){
        enable = isFirstNameFilled && isSurnameFilled && isEmailFilled  && isPhoneFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
        binding.acbNextStep.setButtonEnabled(enable)
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val isEmpty = TextUtils.isEmpty(email)
        val isCorrectly = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return !isEmpty && isCorrectly
    }

    private fun checkValidEmail(email: String):Boolean {
        val isValidEmail = isValidEmail(email)
        if (!isValidEmail) {
            binding.etEmail.error = resources.getString(R.string.new_card_invalidEmail)
        }
        return isValidEmail
    }

    override fun onResume() {
        super.onResume()
        checkFilled()
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }


}