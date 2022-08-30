package com.pesapal.paymentgateway.payment.fragment.card

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.FragmentNewCardAddressBinding
import com.pesapal.paymentgateway.payment.model.BillingDetails
import com.pesapal.paymentgateway.payment.setButtonEnabled


class CardFragmentNewAddress : Fragment() {

    private lateinit var binding: FragmentNewCardAddressBinding

    companion object {
        private const val cardNumberLength = 19
        const val CARD_DATA = "data"
    }

    private var isFirstNameFilled = false
    private var isSurnameFilled = false
    private var isEmailFilled = false
    private var isPhonelFilled = false
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
        handleClickListener()
        handleChangeListener()
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
                isPhonelFilled = phoneText.length > minLength
                checkFilled()
            }else{
                isPhonelFilled = false
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

//        binding.countryCodePicker.selectedCountryName

    }

    private fun handleClickListener(){
        binding.acbNextStep.setOnClickListener {
            if(enable){
                val bundle = Bundle()
                val billingDetails = createBillingDetails()
                bundle.putParcelable(CARD_DATA, billingDetails)
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_pesapalCardFragmentAddress_to_pesapalCardFragmentBilling, bundle)
                }

            }else{
                showMessage("All inputs required ...")
            }
        }
    }


    private fun createBillingDetails(): BillingDetails {
        val details = BillingDetails()
        details.postalCode = binding.etPostal.text.toString()
        details.city = binding.etCity.text.toString()
        details.email = binding.etEmail.text.toString()
        details.street = binding.etAddress.text.toString()
        details.msisdn = binding.etPhoneNumber.rawText.toString()
        details.firstName = binding.etFirstName.text.toString()
        details.lastName = binding.etSurname.text.toString()
        details.country = binding.countryCodePicker.selectedCountryName
        details.countryCode = binding.countryCodePicker.selectedCountryCode
        return details
    }


    private fun checkFilled(){
        enable = isFirstNameFilled && isSurnameFilled && isEmailFilled  && isPhonelFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
        binding.acbNextStep.setButtonEnabled(enable)
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
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

}