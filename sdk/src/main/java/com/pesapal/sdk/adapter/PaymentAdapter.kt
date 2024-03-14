package com.pesapal.sdk.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.hbb20.CountryCodePicker
import com.pesapal.sdk.R
import com.pesapal.sdk.fragment.card.data.CardFragmentCardData
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.utils.CountryCode
import com.pesapal.sdk.utils.CountryCodeEval
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_KE
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_TZ
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_UG
import com.pesapal.sdk.utils.CountryCodeEval.CARD
import com.pesapal.sdk.utils.CountryCodeEval.MPESA
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_TZ
import com.pesapal.sdk.utils.CountryCodeEval.MTN_UG
import com.santalu.maskedittext.MaskEditText
import java.math.BigDecimal

internal class PaymentAdapter(
    val context: Context,
    val currency: String?,
    val amount: BigDecimal,
    val billingAddress: BillingAddress,
    private val paymentMethodInterface: PaymentMethodInterface,
    private val payList: MutableList<CountryCode>
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val OUT_OF_RANGE = 1010

    private var selected: Int = OUT_OF_RANGE
    private var previous: Int = OUT_OF_RANGE

    var mobileStep = 0
    private var mobileResponse: MobileMoneyResponse? = null


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
            hideExpandedView(holder.endIcon, holder.cardOuter, method.paymentMethodId)
            holder.initData(billingAddress)

            holder.endIcon.setOnClickListener {
                checkUncheckExpandingView(holder.mainCard, holder.endIcon, holder.cardOuter, method.paymentMethodId)
            }
            holder.mainCard.setOnClickListener {
                checkUncheckExpandingView(holder.mainCard, holder.endIcon, holder.cardOuter, method.paymentMethodId)
            }
        }
        else if(holder is PaymentMobileMoneyAdapterVh){
            hideExpandedView(holder.endIcon, holder.cardOuter, method.paymentMethodId)

            val phone = holder.etPhone
            holder.labelPhone.text = context.getString(R.string.enter_mobile_number , method.mobileProvider).uppercase()
            phone.hint = context.getString(R.string.enter_mobile_number , method.mobileProvider)


            holder.endIcon.setOnClickListener {
                checkUncheckExpandingView(holder.mainCard, holder.endIcon, holder.cardOuter, method.paymentMethodId)
            }
            holder.mainCard.setOnClickListener{
                checkUncheckExpandingView(holder.mainCard, holder.endIcon,holder.cardOuter, method.paymentMethodId)
            }


            holder.methodIcon.setImageResource(when(method.paymentMethodId){
                MPESA, MPESA_TZ -> R.drawable.mpesa
                AIRTEL_KE, AIRTEL_TZ, AIRTEL_UG -> R.drawable.airtel
                MTN_UG -> R.drawable.ic_mtn
                else -> R.drawable.airtel
            })


            holder.btnSend.setOnClickListener {
                if(mobileStep == 1)
                    paymentMethodInterface.handleConfirmation()
                else
                    mobileMoneyRequest(phone, method)
            }


            if(selected == method.paymentMethodId){
                when(mobileStep){
                    0 -> {
                        holder.phonelayout.visibility = View.VISIBLE
                        holder.resendlayout.visibility = View.GONE
//                        holder.btnSend.isEnabled = true
                        holder.clLipaNaMpesa.visibility = View.GONE
                    }
                    1 -> {
                        holder.phonelayout.visibility = View.GONE
                        holder.resendlayout.visibility = View.VISIBLE
//                        holder.btnSend.isEnabled = false
                        if(method.mobileProvider.contains(CountryCodeEval.MPESA_PROV_NAME) ) {
                            holder.clLipaNaMpesa.visibility = View.VISIBLE
                            holder.clLipaNaMpesa.text = context.getString(R.string.mpesa_combo, mobileResponse!!.accountNumber, mobileResponse!!.businessNumber , currency!!, amount)
                        }
                        else if(method.mobileProvider.contains(CountryCodeEval.MTN_PROV_NAME)){
                            holder.clLipaNaMpesa.visibility = View.VISIBLE
                            holder.clLipaNaMpesa.text = context.getString(R.string.mtn_combo, phone.text.toString())
                        }
                        else{
                            // Airtel and TIGO don't have backup options yet
                        }

                    }
                }
            }

            holder.resendButton.setOnClickListener {
                mobileStep = 0
                notifyDataSetChanged()
            }
        }

    }


    private fun checkUncheckExpandingView(mainOuter: ConstraintLayout, endIcon:ImageView,cardOuter: CardView, absoluteAdapterPosition: Int) {
        if(cardOuter.visibility == View.GONE){
            cardOuter.visibility = View.VISIBLE
            selected = absoluteAdapterPosition
            if(previous == OUT_OF_RANGE){
                previous = selected
            }
            mainOuter.background = context.resources.getDrawable(R.drawable.rounded_grey_selected)
            endIcon.setImageResource(R.drawable.ic_checked)
            resetOnPaymentMethodCollapsed()
        }
        else{
            cardOuter.visibility = View.GONE
            endIcon.setImageResource(R.drawable.ic_unchecked)
            mainOuter.background = context.resources.getDrawable(R.drawable.rounded_grey)

            selected = OUT_OF_RANGE
            previous = OUT_OF_RANGE
        }
        notifyDataSetChanged()
    }


    /**
     * Hides the view that was previously selected and has the card layout visible
     */
    private fun hideExpandedView(endIcon:ImageView, cardOuter: CardView, absoluteAdapterPosition: Int) {
        if(previous != selected && previous == absoluteAdapterPosition){
            cardOuter.visibility = View.GONE
            previous = selected
            endIcon.setImageResource(R.drawable.ic_unchecked)
            paymentMethodInterface.refreshRv()
        }
    }

    private fun mobileMoneyRequest(
        phone: EditText,
        method: CountryCode
    ) {
        selected = method.paymentMethodId
        if (phone.text.toString().isNotEmpty() && phone.text.toString().length > 8) {
            val mobileProvider = method.paymentMethodId
            val min = method.minimumAmount
            val canProceedMinMeet = amount >= min.toBigDecimal()
            if(canProceedMinMeet) {
                paymentMethodInterface.mobileMoneyRequest(1, phone.text.toString(), mobileProvider)
            }
            else{
                Toast.makeText(context, "Minimum amount for ${method.mobileProvider} is $min/=", Toast.LENGTH_SHORT).show()
            }
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
    fun mobileMoneyUpdate(mobileMoneyResponse: MobileMoneyResponse?) {
        mobileResponse = mobileMoneyResponse
        mobileStep = 1
        notifyDataSetChanged()
    }


    private fun isValidEmail(email: CharSequence): Boolean {
        val isEmpty = TextUtils.isEmpty(email)
        val isCorrectly = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return !isEmpty && isCorrectly
    }


    /**
     * 1 = mobile send request
     */
    interface PaymentMethodInterface{
        fun mobileMoneyRequest(action : Int, phoneNumber: String, mobileProvider: Int)    // todo put this as enum
        fun showMessage(message: String)
        fun refreshRv()
        fun handleResend()
        fun handleConfirmation()
        fun generateCardOrderTrackingId(billingAddress: BillingAddress, tokenize: Boolean, cardNumber: String, year:Int, month: Int,cvv:String)

        /**
         * 1 -> Show card desc
         * 2 -> Show Webview
         */
        fun showDialogFrag(dialogType: Int)

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
        val endIcon = itemView.findViewById<ImageView>(R.id.icon_selected)

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
        val cvvInfoIcon = itemView.findViewById<AppCompatImageView>(R.id.cvv_info_icon)
        val iconDesc = itemView.findViewById<AppCompatImageView>(R.id.ic_desc)

        val switchAccept = itemView.findViewById<Switch>(R.id.switch_accept)
        val switchRemember = itemView.findViewById<Switch>(R.id.switch_remember)

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
        private var canCheckContinuesly = false

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

//            etFirstName.setText("Sam")
//            etSurname.setText("nyam")
//            etEmail.setText("samuel@pesapal.com")
//            etPhoneNumber.setText("0703318241")
//            etAddress.setText("nairobi")
//            etPostal.setText("00100")
//            etCity.setText("nairobi")
        }

        private fun checkFilled(){
            enable = isFirstNameFilled && isSurnameFilled && isEmailFilled  && isPhoneFilled && isAddressFilled && isPostalCodeFilled && isCityFilled
                    && isCardNumberFilled && isExpiryFilled && isCvvFilled
            if(canCheckContinuesly) {
                checkAllFields()
                btnSend.isEnabled = enable
            }
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
                isPhoneFilled = if(!it.isNullOrEmpty()){
                    val minLength = 9
                    val phoneText = it.toString()
                    phoneText.length >= minLength
                }else{
                    false
                }
                checkFilled()
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
                }else{
                    isPostalCodeFilled = false
                }
                checkFilled()
            }

            etCity.addTextChangedListener {
                isCityFilled = it.toString().isNotEmpty()
                checkFilled()
            }
        }

        private fun setCardLogoByType(cardName: Editable) {
            val typeVisa = '4'
            val mastercardSecond = '2'
            val mastercard = '5'
            val isEmpty = cardName.isBlank()

            val image = if (isEmpty) {
                R.drawable.ic_card_type_unknown
            }
            else {
                when (cardName.first()) {
                    typeVisa -> R.drawable.ic_card_type_visa
                    mastercardSecond,mastercard -> R.drawable.ic_card_type_mastercard
                    else -> R.drawable.icon_error
                }
            }
            cardLogo.setImageResource(image)
        }


        private fun handleCardChangeListener() {
            etNumberCard.addTextChangedListener {
                isCardNumberFilled = if (it != null) {
                    setCardLogoByType(it)
                    val isFilled = it.toString().length == CardFragmentCardData.cardNumberLength
                    isFilled
                } else {
                    false
                }
                if(canCheckContinuesly){
                    if(isCardNumberFilled){
                        etNumberCard.error = null
                        cardLogo.visibility = View.VISIBLE
                    }
                    else{
                        cardLogo.visibility = View.INVISIBLE

                        etNumberCard.error = "Full input required"
                    }
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
                if(canCheckContinuesly){
                    if(it.toString().length >= CardFragmentCardData.MAX_LENGTH_CVV_CODE){
                        cvvInfoIcon.visibility = View.VISIBLE
                        etNumberCard.error = null
                    }
                    else{
                        cvvInfoIcon.visibility = View.INVISIBLE
                        etNumberCard.error = "Full input required"
                    }
                }
            }

            monthField.addTextChangedListener { it ->
                checkMonth(it)
                checkFilled()  //todo uncommenting causes a loop and crash
            }

            yearField.addTextChangedListener { it ->
                checkYear(it)
                checkFilled() //todo uncommenting causes a loop and crash
            }

        }

        private fun checkMonth(it: Editable?){
            // todo there is a loop hole in this checking logic but too tired to patch it

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
            monthField.error = if(expiryMonth) null
            else
                "Input required"
        }

        private fun checkYear(it: Editable?){
            // todo there is a loop hole in this checking logic but too tired to patch it
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
            yearField.error = if(expiryYear)
                null
            else
                "Input required"
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
                canCheckContinuesly = true
                if(enable){
                    if(switchAccept.isChecked) {
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

                        paymentMethodInterface.generateCardOrderTrackingId(
                            billingAddress,
                            switchRemember.isChecked,
                            etNumberCard.rawText.toString(),
                            Integer.parseInt(yearField.text.toString()),
                            Integer.parseInt(monthField.text.toString()),
                            etCvv.text.toString()
                        )
                    }
                    else{
                        Toast.makeText(context, "Accept terms to continue", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    btnSend.isEnabled = false
                    checkAllFields()
                }
            }

            tvTerms.setOnClickListener {
                paymentMethodInterface.showDialogFrag(2)
            }

            tvPrivacy.setOnClickListener {
                paymentMethodInterface.showDialogFrag(2)
            }
            iconDesc.setOnClickListener{
                paymentMethodInterface.showDialogFrag(1)
            }
        }

        private fun checkAllFields(){
            checkInput(isFirstNameFilled ,etFirstName)
            checkInput(isSurnameFilled,etSurname)
            checkInput(isPostalCodeFilled,etPostal, "Postal Code Too Short")
            checkInput(isPhoneFilled,etPhoneNumber)
            checkInput(isCityFilled,etCity)
            checkInput(isAddressFilled, etAddress)
            if(isCardNumberFilled){
                cardLogo.visibility = View.VISIBLE
                etNumberCard.error = null
            }
            else{
                cardLogo.visibility = View.INVISIBLE
                etNumberCard.error = "Full input required"
            }

            if(isCvvFilled){
                cvvInfoIcon.visibility = View.VISIBLE
                etCvv.error = null
            }
            else{
                cvvInfoIcon.visibility = View.INVISIBLE
                etCvv.error = "Full input required"
            }

//                    if(isAddressFilled){
//                        checkInput(etAddress)
//                    }
//            checkMonth(monthField.text)
//            checkYear(yearField.text)
            checkInput(monthField.text.isNotEmpty(), monthField)
            checkInput(yearField.text.isNotEmpty() , yearField)




        }
        private fun checkInput(correctEditTextState: Boolean, editText: EditText, errorMsg:String = "Input required") {
            if(!correctEditTextState){
                editText.error = errorMsg
            }
            else{
                editText.error = null
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
        val endIcon = itemView.findViewById<ImageView>(R.id.icon_selected)


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