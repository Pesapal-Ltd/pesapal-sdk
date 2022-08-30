package com.pesapal.paymentgateway.payment.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.FragmentNewCardAddressBinding
import com.pesapal.paymentgateway.payment.setButtonEnabled

class CardFragmentNewAddress : Fragment() {

    private lateinit var binding: FragmentNewCardAddressBinding

    private var country = "Kenya"//TODO:  remove hard coded country
    private var countryCode = "KE"//TODO: remove hard coded country code

    private var isFirstNameFilled = false
    private var isSurnameFilled = false
    private var isEmailFilled = false
    private var isCountryFilled = false
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
            isEmailFilled = it.toString().isNotEmpty()
            checkFilled()

        }

        binding.etCountry.addTextChangedListener {
            isCountryFilled = it.toString().isNotEmpty()
            checkFilled()

        }
        binding.etPhoneNumber.addTextChangedListener {
            isPhonelFilled = it.toString().isNotEmpty()
            checkFilled()

        }
        binding.etAddress.addTextChangedListener {
            isAddressFilled = it.toString().isNotEmpty()
            checkFilled()

        }
        binding.etPostal.addTextChangedListener {
            isPostalCodeFilled = it.toString().isNotEmpty()
            checkFilled()

        }
        binding.etCity.addTextChangedListener {
            isCityFilled = it.toString().isNotEmpty()
            checkFilled()

        }

    }

    private fun handleClickListener(){
        binding.acbNextStep.setOnClickListener {
            if(enable){

            }else{

            }
        }
    }

    private fun checkFilled(){
        enable = isFirstNameFilled && isSurnameFilled && isEmailFilled && isCountryFilled && isPhonelFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
        binding.acbNextStep.setButtonEnabled(enable)
    }

    override fun onResume() {
        super.onResume()
    }

}