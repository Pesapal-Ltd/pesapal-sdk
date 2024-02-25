package com.pesapal.sdk.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hbb20.CountryCodePicker
import com.pesapal.paygateway.activities.payment.model.check3ds.BillingDetails
import com.pesapal.sdk.R
import com.pesapal.sdk.fragment.card.address.CardFragmentAddressDataDirections
import com.pesapal.sdk.fragment.card.data.CardFragmentCardData
import com.pesapal.sdk.fragment.details.PaymentInterModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.card.CardDetails
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.setButtonEnabled
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_KE
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_TZ
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_UG
import com.pesapal.sdk.utils.CountryCodeEval.CARD
import com.pesapal.sdk.utils.CountryCodeEval.MPESA
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_TZ
import com.pesapal.sdk.utils.FragmentExtension.hideKeyboard
import com.pesapal.sdk.utils.PrefManager
import com.santalu.maskedittext.MaskEditText

internal class PaymentAdapter(val billingAddress: BillingAddress,
                     val context: Context,
                     private val paymentMethodInterface: PaymentMethodInterface,
                     private val payList: MutableList<PaymentInterModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val OUT_OF_RANGE = 1010

    private var selected: Int = OUT_OF_RANGE
    private var previous: Int = OUT_OF_RANGE

    var mobileStep = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                PaymentCardAdapterVh(LayoutInflater.from(context).inflate(R.layout.item_pay_method,parent,false))
            }
            else -> {
                PaymentMobileMoneyAdapterVh(LayoutInflater.from(context).inflate(R.layout.item_pay_method_mobile,parent,false))
            }
        }
    }

    override fun getItemCount(): Int {
        return payList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (payList[position].paymentMethodId) {
            CARD -> 0
            else -> position
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val method  = payList[position]

        if(holder is PaymentCardAdapterVh){
            hideExpandedView(holder.cardOuter, method.paymentMethodId)
            holder.initData(billingAddress)
            holder.mainCard.setOnClickListener {
                checkUncheckExpandingView(holder.cardOuter, method.paymentMethodId)
            }
        }
        else if(holder is PaymentMobileMoneyAdapterVh){
            hideExpandedView(holder.cardOuter, method.paymentMethodId)

            val phone = holder.etPhone
            holder.labelPhone.text = context.getString(R.string.enter_mobile_number , method.mobileProvider).uppercase()
            phone.hint = context.getString(R.string.enter_mobile_number , method.mobileProvider)

            holder.mainCard.setOnClickListener{
                checkUncheckExpandingView(holder.cardOuter, method.paymentMethodId)
            }
            holder.methodIcon.setImageResource(when(method.paymentMethodId){
                MPESA, MPESA_TZ -> R.drawable.mpesa
                AIRTEL_KE, AIRTEL_TZ, AIRTEL_UG -> R.drawable.airtel
                else -> R.drawable.airtel
            })


            holder.btnSend.setOnClickListener {
                if(mobileStep == 1)
                    paymentMethodInterface.handleConfirmation()
                else
                    mobileMoneyRequest(phone, method)
            }

            holder.resendButton.setOnClickListener {
                mobileMoneyRequest(phone, method)
            }

            if(selected == method.paymentMethodId){
                when(mobileStep){
                    0 -> {
                        holder.phonelayout.visibility = View.VISIBLE
                        holder.resendlayout.visibility = View.GONE
                    }
                    1 -> {
                        holder.phonelayout.visibility = View.GONE
                        holder.resendlayout.visibility = View.VISIBLE
                    }
                }
            }

            holder.resendButton.setOnClickListener {
                holder.clLipaNaMpesa.visibility = View.GONE
                holder.clBackgroundCheck.visibility = View.VISIBLE
                paymentMethodInterface.handleResend()
            }
        }

    }


    private fun checkUncheckExpandingView(cardOuter: CardView, absoluteAdapterPosition: Int) {
        if(cardOuter.visibility == View.GONE){
            cardOuter.visibility = View.VISIBLE
            selected = absoluteAdapterPosition
            if(previous == OUT_OF_RANGE){
                previous = selected
            }
            resetOnPaymentMethodCollapsed()
        }
        else{
            cardOuter.visibility = View.GONE
            selected = OUT_OF_RANGE
            previous = OUT_OF_RANGE
        }
        notifyDataSetChanged()
    }


    /**
     * Hides the view that was previously selected and has the card layout visible
     */
    private fun hideExpandedView(cardOuter: CardView, absoluteAdapterPosition: Int) {
        if(previous != selected && previous == absoluteAdapterPosition){
            cardOuter.visibility = View.GONE
            previous = selected
            paymentMethodInterface.refreshRv()
        }
    }

    private fun mobileMoneyRequest(
        phone: EditText,
        method: PaymentInterModel
    ) {
        selected = method.paymentMethodId
        if (phone.text.toString().isNotEmpty() && phone.text.toString().length == 10) {
            val mobileProvider = method.paymentMethodId
            paymentMethodInterface.mobileMoneyRequest(1, phone.text.toString(), mobileProvider)
        } else {
            paymentMethodInterface.showMessage("All inputs required ...")
        }
    }

    fun resetOnPaymentMethodCollapsed(){
        mobileStep = 0
    }


    /**
     * To
     */
    fun mobileMoneyUpdate() {
        mobileStep = 1
        notifyDataSetChanged()
    }


    private fun isValidEmail(email: CharSequence): Boolean {
        val isEmpty = TextUtils.isEmpty(email)
        val isCorrectly = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return !isEmpty && isCorrectly
    }

//    private fun checkValidEmail(email: String, etmail: EditText):Boolean {
//        val isValidEmail = isValidEmail(email)
//        if (!isValidEmail) {
//            etEmail.error = context.getString(R.string.new_card_invalidEmail)
//        }
//        return isValidEmail
//    }


    /**
     * 1 = mobile send request
     */
    interface PaymentMethodInterface{
        fun mobileMoneyRequest(action : Int, phoneNumber: String, mobileProvider: Int)    // todo put this as enum
        fun showMessage(message: String)
        fun refreshRv()
        fun handleResend()
        fun handleConfirmation()
        fun generateCardOrderTrackingId(billingAddress: BillingAddress, cardNumber: String, year:Int, month: Int,cvv:String)

    }

    /**
     * TODO make and abstract class with the common views so that common methods are not called twice
     *   var mainCard = itemView.findViewById<ConstraintLayout>(R.id.linear_card_pay)
     *         val cardOuter = itemView.findViewById<CardView>(R.id.card_outer)
     */
    inner class PaymentCardAdapterVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainCard = itemView.findViewById<ConstraintLayout>(R.id.linear_card_pay)
        val cardOuter = itemView.findViewById<CardView>(R.id.card_outer)
        val btnSend = itemView.findViewById<TextView>(R.id.btn_proceed)

        val etFirstName = itemView.findViewById<EditText>(R.id.et_first_name)
        val etSurname = itemView.findViewById<EditText>(R.id.et_surname)
        val etEmail = itemView.findViewById<EditText>(R.id.et_email)
        val etPhoneNumber = itemView.findViewById<EditText>(R.id.et_phone_number)
        val etAddress = itemView.findViewById<EditText>(R.id.et_address)
        val etPostal = itemView.findViewById<EditText>(R.id.et_postal)
        val etCity = itemView.findViewById<EditText>(R.id.et_city)

        val countryCodePicker = itemView.findViewById<CountryCodePicker>(R.id.countryCode_picker)
        val etNumberCard = itemView.findViewById<MaskEditText>(R.id.et_number_card)
        val yearField = itemView.findViewById<EditText>(R.id.year_field)
        val monthField = itemView.findViewById<EditText>(R.id.month_field)
        val etCvv = itemView.findViewById<EditText>(R.id.et_cvv)

        val tvPrivacy = itemView.findViewById<TextView>(R.id.privacy_policy)
        val tvTerms = itemView.findViewById<TextView>(R.id.terms_of_service)
        val cardLogo = itemView.findViewById<AppCompatImageView>(R.id.card_logo)

        private var isFirstNameFilled = false
        private var isSurnameFilled = false
        private var isEmailFilled = false
        private var isPhoneFilled = false
        private var isAddressFilled = false
        private var isPostalCodeFilled = false
        private var isCityFilled = false

        private var isCardNumberFilled = false
        private var isExpiryFilled = false
        private var isCvvFilled = false
        private var expiryMonth = false
        private var expiryYear = false
        private var enable = false

        init {
            handleClickListener()
            handleChangeListener()
            handleCardChangeListener()
        }


        // todo transfer over all the other logic


        fun initData(billingAddress: BillingAddress){
            etFirstName.setText(billingAddress.firstName)
            etSurname.setText(billingAddress.lastName)
            etEmail.setText(billingAddress.emailAddress)
            etPhoneNumber.setText(billingAddress.phoneNumber)
            etAddress.setText(billingAddress.zipCode)
            etPostal.setText(billingAddress.postalCode)
            etCity.setText(billingAddress.city)
        }

        private fun checkFilled(){
            enable = isFirstNameFilled && isSurnameFilled && isEmailFilled  && isPhoneFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
                    && isCardNumberFilled && isExpiryFilled && isCvvFilled
            btnSend.isEnabled = enable
        }


        private fun checkValidEmail(email: String):Boolean {
            val isValidEmail = isValidEmail(email)
            if (!isValidEmail) {
                etEmail.error = context.resources.getString(R.string.new_card_invalidEmail)
            }
            return isValidEmail
        }

        private fun handleChangeListener(){
            etFirstName.addTextChangedListener {
                isFirstNameFilled = it.toString().isNotEmpty()
                checkFilled()

            }

            etSurname.addTextChangedListener {
                isSurnameFilled = it.toString().isNotEmpty()
                checkFilled()

            }

            etEmail.addTextChangedListener {
                if(!it.isNullOrEmpty()) {
                    isEmailFilled = checkValidEmail(it.toString())
                    checkFilled()
                }else{
                    isEmailFilled = false
                }
            }


            etPhoneNumber.addTextChangedListener {
                if(!it.isNullOrEmpty()){
                    val minLength = 10
                    val phoneText = it.toString()
                    isPhoneFilled = phoneText.length == minLength
                    checkFilled()
                }else{
                    isPhoneFilled = false
                }
            }
            etAddress.addTextChangedListener {
                isAddressFilled = it.toString().isNotEmpty()
                checkFilled()

            }
            etPostal.addTextChangedListener {
                if(!it.isNullOrEmpty()){
                    val minPostalCodeLength = 2
                    val postalCodeText = it.toString()
                    isPostalCodeFilled = postalCodeText.length > minPostalCodeLength

                    var postalCodeError = ""
                    if (!isPostalCodeFilled) {
                        postalCodeError = "Postal Code Too Short"
                    }
                    etPostal.error = postalCodeError.ifEmpty { null }
                    isPostalCodeFilled = it.toString().isNotEmpty()
                    checkFilled()
                }else{
                    isPostalCodeFilled = false
                }


            }
            etCity.addTextChangedListener {
                isCityFilled = it.toString().isNotEmpty()
                checkFilled()

            }
        }

        private fun setCardLogoByType(cardName: Editable) {
            val typeVisa = '4'
            val mastercard = '5'
            val isEmpty = cardName.isBlank()

            val image = if (isEmpty) {
                R.drawable.ic_card_type_unknown
            }
            else {
                when (cardName.first()) {
                    typeVisa -> R.drawable.ic_card_type_visa
                    mastercard -> R.drawable.ic_card_type_mastercard
                    else -> R.drawable.ic_card_type_unknown
                }
            }
            cardLogo.setImageResource(image)

        }


        private fun handleCardChangeListener() {
            etNumberCard.addTextChangedListener {
                isCardNumberFilled = if (it != null && it.isNotEmpty()) {
                    setCardLogoByType(it)
                    val isFilled = it.toString().length == CardFragmentCardData.cardNumberLength
                    isFilled
                } else {
                    false
                }
                checkFilled()
            }

            etCvv.addTextChangedListener {
                isCvvFilled = if (!it.isNullOrEmpty()) {
                    it.toString().length >= CardFragmentCardData.MAX_LENGTH_CVV_CODE
                } else {
                    false
                }
                checkFilled()
            }

            monthField.addTextChangedListener { it ->
                if (!it.isNullOrEmpty()) {
                    var formattedMonth: String

                    it.toString().let {
                        formattedMonth = formatCardExpiryMonth(it)
                        if (formattedMonth != it) {
                            monthField.setText(formattedMonth)
                            monthField.setSelection(formattedMonth.length)
                        }
                    }

                    expiryMonth = formattedMonth.isNotEmpty()

                    if (formattedMonth.length == 2) {
                        yearField.requestFocus()
                    }
                    setExpiryDateFilled()
                } else {
                    expiryMonth = false
                }
                checkFilled()
            }
            yearField.addTextChangedListener { it ->
                if (!it.isNullOrEmpty()) {
                    var formattedYear = ""
                    it.toString().let {
                        formattedYear = formatCardExpiryYear(it)
                        if (formattedYear != it) {
                            yearField.setText(formattedYear)
                            yearField.setSelection(formattedYear.length)
                        }
                    }
                    expiryYear = formattedYear.isNotEmpty()
                    if (expiryYear && it.toString().length >= 2) {
                        etCvv.requestFocus()
                    }
                    setExpiryDateFilled()
                } else {
                    expiryYear = false
                }
                checkFilled()
            }

        }


        private fun formatCardExpiryMonth(month: String): String {
            var formattedString = month

            if (formattedString.isEmpty()) {
                return ""
            }

            if (formattedString.take(1).toInt() > 1) {
                formattedString = ("0$month").take(2)
            }

            if (formattedString.toInt() > 12) {
                formattedString = "1"
            }

            return formattedString
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


        private fun setExpiryDateFilled() {
            isExpiryFilled = expiryMonth && expiryYear
            checkFilled()
        }



        private fun handleClickListener(){
            btnSend.setOnClickListener {
                val billingAddress = BillingAddress(
                    phoneNumber = etPhoneNumber.text.toString(),
                    emailAddress = etEmail.text.toString(),
                    countryCode = countryCodePicker.selectedCountryNameCode,
                    firstName = etFirstName.text.toString(),
                    middleName = etSurname.text.toString(),
                    lastName = etSurname.text.toString(),
                    line = etAddress.text.toString(),
                    line2 = etAddress.text.toString(),
                    city = etCity.text.toString(),
                    state = " ",
                    postalCode = etPostal.text.toString(),
                    zipCode = etAddress.text.toString(),
                )

                paymentMethodInterface.generateCardOrderTrackingId(billingAddress,etNumberCard.rawText.toString(), Integer.parseInt(yearField.text.toString()),Integer.parseInt(monthField.text.toString()),etCvv.text.toString())
            }

            tvTerms.setOnClickListener {
                Toast.makeText(context,"Terms clicked", Toast.LENGTH_SHORT).show()
            }

            tvPrivacy.setOnClickListener {
                Toast.makeText(context,"Privacy clicked", Toast.LENGTH_SHORT).show()
            }
        }


    }


    /**
     * TODO make and abstract class with the common views so that common methods are not called twice
     *   var mainCard = itemView.findViewById<ConstraintLayout>(R.id.linear_card_pay)
     *         val cardOuter = itemView.findViewById<CardView>(R.id.card_outer)
     */
    inner class PaymentMobileMoneyAdapterVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mainCard = itemView.findViewById<ConstraintLayout>(R.id.linear_card_pay)
        val cardOuter = itemView.findViewById<CardView>(R.id.card_outer)

        val methodIcon = itemView.findViewById<ImageView>(R.id.icon_payment_method)

        val phonelayout = itemView.findViewById<LinearLayout>(R.id.layout_phone)
        var labelPhone = itemView.findViewById<TextView>(R.id.label_phone)
        var etPhone = itemView.findViewById<EditText>(R.id.phone)

        val btnSend = itemView.findViewById<TextView>(R.id.btn_proceed)
        val resendlayout = itemView.findViewById<ConstraintLayout>(R.id.layout_resend_prompt)
        val resendButton = itemView.findViewById<AppCompatButton>(R.id.btn_resend)

        val clLipaNaMpesa = itemView.findViewById<TextView>(R.id.tv_manual)
        val clBackgroundCheck = itemView.findViewById<ConstraintLayout>(R.id.clWaiting)
        
        
    }
}