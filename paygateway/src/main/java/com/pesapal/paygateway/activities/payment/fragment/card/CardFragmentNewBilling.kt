package com.pesapal.paygateway.activities.payment.fragment.card

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paygateway.databinding.FragmentNewCardDetailsBinding
import com.pesapal.paygateway.activities.payment.setButtonEnabled
import com.pesapal.paygateway.activities.payment.utils.FragmentExtension.hideKeyboard
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel

class CardFragmentNewBilling : Fragment() {

    companion object {
        private const val MAX_LENGTH_CVV_CODE = 3
        private const val cardNumberLength = 19
    }

    private lateinit var binding: FragmentNewCardDetailsBinding

    private var isCardNumberFilled = false
    private var isExpiryFilled = false
    private var isCvvFilled = false
    private var isCardNameFilled = false
    private var expiryMonth = false
    private var expiryYear = false
    private var enable = false

    var numberCard = " ";
    var checkEnableVisa = true
    var cvv = ""
    var nameOnCard = ""
    private val viewModel: AppViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCardDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleChangeListener()
    }

    private fun handleChangeListener(){
        binding.etCardName.addTextChangedListener {
            isCardNameFilled = if(it != null && it.isNotEmpty()){
                it.isNotEmpty()
            }else{
                false
            }
            checkFilled()
        }

        binding.etNumberCard.addTextChangedListener {
            isCardNumberFilled = if(it != null && it.isNotEmpty()){
                setCardLogoByType(it)
                val isFilled = it.toString().length == cardNumberLength
                isFilled
            }else{
                false
            }
            checkFilled()
        }

        binding.etCvv.addTextChangedListener {
            isCvvFilled = if(it != null && it.isNotEmpty()){
                it.toString().length >= MAX_LENGTH_CVV_CODE
            }else{
                false
            }
            checkFilled()
        }

        binding.monthField.addTextChangedListener { it ->
            if(it != null && it.isNotEmpty()){
                var formattedMonth: String

                it.toString().let {
                    formattedMonth = formatCardExpiryMonth(it)
                    if (formattedMonth != it) {
                        binding.monthField.setText(formattedMonth)
                        binding.monthField.setSelection(formattedMonth.length)
                    }
                }

                expiryMonth = formattedMonth.isNotEmpty()

                if (formattedMonth.length == 2) {
                    binding.yearField.requestFocus()
                }
                setExpiryDateFilled()
            }else{
                expiryMonth = false
            }
            checkFilled()
        }
        binding.yearField.addTextChangedListener { it ->
            if(it != null && it.isNotEmpty()){
                var formattedYear = ""
                it.toString().let {
                    formattedYear = formatCardExpiryYear(it)
                    if (formattedYear != it) {
                        binding.yearField.setText(formattedYear)
                        binding.yearField.setSelection(formattedYear.length)
                    }
                }
                expiryYear = formattedYear.isNotEmpty()
                if (expiryYear && it.toString().length >= 2) {
                    binding.etCvv.requestFocus()
                }
                setExpiryDateFilled()
            }else{
                expiryYear = false
            }
            checkFilled()
        }

        binding.acbCreateCard.setOnClickListener {
            hideKeyboard()
            createRequestMakeCard()

        }
    }


    private fun setExpiryDateFilled() {
        isExpiryFilled = expiryMonth && expiryYear
        checkFilled()
    }

    private fun formatCardExpiryYear(year: String): String {
        var formattedYear = year
        if (formattedYear.isEmpty()) {
            return ""
        }

        if (formattedYear.take(1).toInt() < 2) {
            formattedYear = if (formattedYear.length > 1) {
                formattedYear[1].toString()
            } else {
                ""
            }
        }
        return formattedYear
    }

    override fun onResume() {
        super.onResume()
        checkFilled()
    }

    private fun setCardLogoByType(cardName: Editable) {
        val typeVisa = '4'
        val mastercard = '5'
        val isEmpty = cardName.isBlank()

        if (isEmpty) {
            setUnknownLogoVisible()
            return
        }
        when (cardName.first()) {
            typeVisa -> setVisaLogoVisible()
            mastercard -> setMastercardLogoVisible()
            else -> setUnknownLogoVisible()
        }
    }

    private fun formatCardExpiryMonth(month: String): String {
        var formattedString = month

        if (formattedString.isEmpty()) {
            return ""
        }

        if (formattedString.take(1).toInt() > 1) {
            formattedString = ("0" + month).take(2)
        }

        if (formattedString.toInt() > 12) {
            formattedString = "1"
        }

        return formattedString
    }

    private fun createRequestMakeCard() {
        numberCard = binding.etNumberCard.rawText.toString()
        checkEnableVisa = true
        cvv = binding.etCvv.text.toString()
        nameOnCard = binding.etCardName.text.toString()
    }

    private fun setVisaLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.VISIBLE
        binding.cardLogoMastercardImg.visibility = View.INVISIBLE
        binding.cardLogoUnknownImg.visibility = View.INVISIBLE
    }

    private fun setMastercardLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.INVISIBLE
        binding.cardLogoMastercardImg.visibility = View.VISIBLE
        binding.cardLogoUnknownImg.visibility = View.INVISIBLE
    }

    private fun setUnknownLogoVisible() {
        binding.cardLogoVisaImg.visibility = View.INVISIBLE
        binding.cardLogoMastercardImg.visibility = View.INVISIBLE
        binding.cardLogoUnknownImg.visibility = View.VISIBLE
    }

    private fun checkFilled(){
        Log.e("isCardNumberFilled"," ==> "+isCardNumberFilled)
        Log.e("isExpiryFilled"," ==> "+isExpiryFilled)
        Log.e("isCvvFilled"," ==> "+isCvvFilled)
        Log.e("isCardNameFilled"," ==> "+isCardNameFilled)
         enable = isCardNumberFilled && isExpiryFilled && isCvvFilled && isCardNameFilled
        binding.acbCreateCard.setButtonEnabled(enable)
    }

}