package com.pesapal.sdk.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pesapal.sdk.R
import com.pesapal.sdk.fragment.details.PaymentInterModel
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_KE
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_TZ
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_UG
import com.pesapal.sdk.utils.CountryCodeEval.CARD
import com.pesapal.sdk.utils.CountryCodeEval.MPESA
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_TZ

class PaymentAdapter(val context: Context,
                     private val paymentMethodInterface: PaymentMethodInterface,
                     private val payList: MutableList<PaymentInterModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val OUT_OF_RANGE = 100

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
            else -> 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val method  = payList[position]

        if(holder is PaymentCardAdapterVh){
            hideExpandedView(holder.cardOuter, position)
            holder.mainCard.setOnClickListener{
                checkUncheckExpandingView(holder.cardOuter, position)
            }
        }
        else if(holder is PaymentMobileMoneyAdapterVh){
            hideExpandedView(holder.cardOuter, position)

            val phone = holder.etPhone
            holder.labelPhone.text = context.getString(R.string.enter_mobile_number , method.mobileProvider).uppercase()
            phone.hint = context.getString(R.string.enter_mobile_number , method.mobileProvider)

            holder.mainCard.setOnClickListener{
                checkUncheckExpandingView(holder.cardOuter, position)
            }
            holder.methodIcon.setImageResource(when(method.paymentMethodId){
                MPESA, MPESA_TZ -> R.drawable.mpesa
                AIRTEL_KE, AIRTEL_TZ, AIRTEL_UG -> R.drawable.airtel
                else -> R.drawable.airtel
            })


            holder.btnSend.setOnClickListener {
                mobileMoneyRequest(position, phone, method)
            }

            holder.resendButton.setOnClickListener {
                mobileMoneyRequest(position, phone, method)
            }

            if(selected == position){
                when(mobileStep){
                    0 -> {

                    }
                    1 -> {
                        holder.phonelayout.visibility = View.GONE
                        holder.resendlayout.visibility = View.VISIBLE
                    }
                }
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
        }
        else{
            cardOuter.visibility = View.GONE
            selected = OUT_OF_RANGE
            previous = OUT_OF_RANGE

        }
        notifyDataSetChanged()
    }


    private fun hideExpandedView(cardOuter: CardView, absoluteAdapterPosition: Int) {
        Log.e("adapt","Hide called  ------->")

        if(previous != selected && previous == absoluteAdapterPosition){
            cardOuter.visibility = View.GONE
            previous = selected
            paymentMethodInterface.refreshRv()

        }

    }

    private fun mobileMoneyRequest(
        position: Int,
        phone: EditText,
        method: PaymentInterModel
    ) {
        selected = position
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

    /**
     * 1 = mobile send request
     */
    interface PaymentMethodInterface{
        fun mobileMoneyRequest(action : Int, phoneNumber: String, mobileProvider: Int)    //        todo put this as enum
        fun showMessage(message: String)
        fun refreshRv()
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
//        val plusBtn = itemView.findViewById<ImageView>(R.id.iv_detail_plus)
//        val minusBtn = itemView.findViewById<ImageView>(R.id.iv_detail_minus)
//        val qtyNumber = itemView.findViewById<TextView>(R.id.tv_detail_qty)
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


//        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
//        val minusBtn = itemView.findViewById<ImageView>(R.id.iv_detail_minus)
//        val qtyNumber = itemView.findViewById<TextView>(R.id.tv_detail_qty)
    }
}