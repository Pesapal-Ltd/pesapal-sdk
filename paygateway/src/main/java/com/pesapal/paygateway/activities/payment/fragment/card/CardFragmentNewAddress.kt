package com.pesapal.paygateway.activities.payment.fragment.card

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.R
import com.pesapal.paygateway.activities.payment.model.check3ds.BillingDetails
import com.pesapal.paygateway.databinding.FragmentNewCardAddressBinding
import com.pesapal.paygateway.activities.payment.setButtonEnabled
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import java.math.BigDecimal


class CardFragmentNewAddress : Fragment() {

    private lateinit var binding: FragmentNewCardAddressBinding

    private var first_name: String? = ""
    private var last_name: String? = ""
    private var email: String? = ""
    private var phone: String? = ""
    private var amount: BigDecimal = BigDecimal.ONE
    private lateinit var order_id: String
    private lateinit var currency: String
    private lateinit var accountNumber: String
    private lateinit var callbackUrl: String

    companion object {
        private const val cardNumberLength = 19
        const val CARD_DATA = "data"
        fun newInstance(amount: BigDecimal, order_id: String, currency: String, accountNumber: String, callbackUrl: String, first_name: String?, last_name: String?, email: String?, phone: String? ): CardFragmentNewAddress {
            val fragment = CardFragmentNewAddress()
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

    private var isFirstNameFilled = false
    private var isSurnameFilled = false
    private var isEmailFilled = false
    private var isPhonelFilled = false
    private var isAddressFilled = false
    private var isPostalCodeFilled = false
    private var isCityFilled = false
    private var enable = false




    private val viewModel: AppViewModel by activityViewModels()

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
        initData()
        binding.acbNextStep.setButtonEnabled(true)
    }

    private fun initData(){
        binding.etFirstName.setText(first_name)
        binding.etSurname.setText(last_name)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phone)
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
                viewModel.loadFragment("card2")
        }
    }


    private fun checkFilled(){
        enable = isFirstNameFilled && isSurnameFilled && isEmailFilled  && isPhonelFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
//        binding.acbNextStep.setButtonEnabled(enable)
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