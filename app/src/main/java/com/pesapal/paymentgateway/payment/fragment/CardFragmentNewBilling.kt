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
import androidx.fragment.app.Fragment
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.FragmentNewCardDetailsBinding

class CardFragmentNewBilling : Fragment() {

    companion object {
        private const val MAX_LENGTH_CVV_CODE = 3
        private const val cardNumberLength = 19
        const val CARD_DATA = "data"
    }

    private lateinit var binding: FragmentNewCardDetailsBinding

    private var isCardNumberFilled = false
    private var isExpiryFilled = false
    private var isCvvFilled = false
    private var isCardNameFilled = false

    private var expiryMonth: Int = -1
    private var expiryYear: Int = -1

    var numberCard = " ";
    var checkEnableVisa = true
    var cvv = ""
    var nameOnCard = ""

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
    }


    override fun onResume() {
        super.onResume()
        setSaveButtonEnabled()
    }

    private fun setSaveButtonEnabled() {
        val isEnabled = isCardNumberFilled && isExpiryFilled && isCvvFilled && isCardNameFilled

        if (isEnabled)
            binding.acbCreateCard.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.view_button
                )
            )
        else
            binding.acbCreateCard.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.view_button_inactive
                )
            )

        binding.acbCreateCard.isEnabled = isEnabled
    }

    fun setCvvFilled(cvv: Editable) {
        isCvvFilled = cvv.length >= MAX_LENGTH_CVV_CODE
        setSaveButtonEnabled()
    }

    fun setCardNameFilled(cardName: Editable) {
        isCardNameFilled = !cardName.isBlank()
        setSaveButtonEnabled()
    }

    private fun setCardNumberFilled(cardNumber: Editable) {
        val isFilled = cardNumber.toString().length == cardNumberLength
        isCardNumberFilled = isFilled
        setSaveButtonEnabled()
    }

    private fun setExpiryDateFilled() {
        isExpiryFilled = expiryMonth > 0 && expiryYear >= 20
        setSaveButtonEnabled()
    }

    fun setCardLogoByType(cardName: Editable) {
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

        setCardNumberFilled(cardName)
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


//    private fun initListeners() {
//        binding.acbCreateCard.setOnClickListener {
//            this.hideKeyboard()
//            createRequestMakeCard()
//        }
//
//        binding.edDate.setOnClickListener {
//            showExpiryDialog()
//        }
//
//        binding.edDate.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                showExpiryDialog()
//            }
//        }
//
//        binding.cvvInfoIcon.setOnClickListener {
//            showInfoCvvDialog()
//        }
//
//
//        binding.monthField.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                binding.monthField.removeTextChangedListener(this)
//                var formattedMonth = ""
//                s?.toString()?.let {
////                    formattedMonth = formatCardExpiryMonth(it)
//                    if (formattedMonth != it) {
//                        binding.monthField.setText(formattedMonth)
//                        binding.monthField.setSelection(formattedMonth.length)
//                    }
//                }
//                expiryMonth = if (formattedMonth.isNotEmpty()) {
//                    formattedMonth.toInt()
//                } else {
//                    -1
//                }
//                setExpiryDateFilled()
//
//                if (formattedMonth.length == 2) {
//                    binding.yearField.requestFocus()
//                }
//
//                binding.monthField.addTextChangedListener(this)
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//        })
//
//        binding.yearField.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                binding.yearField.removeTextChangedListener(this)
//                var formattedYear = ""
//
//                s?.toString()?.let {
////                    formattedYear = formatCardExpiryYear(it)
//                    if (formattedYear != it) {
//                        binding.yearField.setText(formattedYear)
//                        binding.yearField.setSelection(formattedYear.length)
//                    }
//                }
//
//                expiryYear = if (formattedYear.isNotEmpty()) {
//
//                    formattedYear.toInt()
//                } else {
//                    -1
//                }
//                setExpiryDateFilled()
//                binding.yearField.addTextChangedListener(this)
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//        })
//
//    }

    private fun showExpiryDialog() =
        fragmentManager?.beginTransaction()?.let { transaction ->
//            ExpiryBottomDialog(this).show(transaction, null)
        }

    private fun createRequestMakeCard() {
        numberCard = binding.etNumberCard.rawText.toString()
        checkEnableVisa = true
        cvv = binding.etCvv.text.toString()
        nameOnCard = binding.etCardName.text.toString()
    }

    private fun showInfoCvvDialog() {
        activity?.let { activity ->
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setContentView(R.layout.dialog_security_code)
//            val btnClose = dialog.findViewById(R.id.button_close) as TextView
//            btnClose.setOnClickListener {
//                dialog.dismiss()
//            }

            dialog.show()
        }
    }
}