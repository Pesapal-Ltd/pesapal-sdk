package com.pesapal.paymentgateway.payment.fragment.mpesa

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.databinding.FragmentPesapalMpesaBinding
import com.pesapal.paymentgateway.payment.model.mobile_money.BillingAddress
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.utils.PrefManager
import com.pesapal.paymentgateway.payment.utils.Status
import com.pesapal.paymentgateway.payment.viewmodel.AppViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class MpesaPesapalFragment : Fragment() {

    private lateinit var binding: FragmentPesapalMpesaBinding
    private val viewModel: AppViewModel by activityViewModels()
    private val timeCountInMilliSeconds = 30000L
    private var countDownTimer: CountDownTimer? = null
    private var alertDialog: AlertDialog? = null
    private var progressBarCircle: ProgressBar? = null
    private var timerStatus = TimerStatus.STOPPED
    private var textViewTime: TextView? = null
    private var closeMpesaDialog: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPesapalMpesaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleViewModel()
    }

    private fun initData(){
        handleClicks()
    }

    private fun handleClicks(){
        binding.btnSend.setOnClickListener {
            val request = prepareMobileMoney()
            viewModel.sendMobileMoneyCheckOut(request )
        }
    }

    private fun prepareMobileMoney(): MobileMoneyRequest {

        val billingAddress = BillingAddress()

//       val billingAddress = BillingAddress(phoneNumber = "0716210311", emailAddress = "richiekaby@gmail.com", countryCode = "KE", firstName = "Richard",
//            middleName = "Kamere",
//            lastName = "K",
//            line = "",
//            line2 = "",
//            city = "Nairobi",
//            state = "",
//            postalCode = "",
//            zipCode = "")

        return MobileMoneyRequest(
            id = "321101",
            sourceChannel = 2,
            msisdn = "0112826460",
            paymentMethodId = 1,
            accountNumber = "1000101",
            currency = "KES",
            allowedCurrencies = "",
            amount = 2,
            description = "Express Order",
            callbackUrl = "http://localhost:56522",
            cancellationUrl = "",
            notificationId = PrefManager.getIpnId(),
            language = "",
            termsAndConditionsId = "",
            billingAddress = billingAddress
        );
    }

    private fun handleViewModel(){
        viewModel.mobileMoneyResponse.observe(requireActivity()){
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(" SUCCESS ", " ====> SUCCESS")
                    createCountDownDialog()
                }
                Status.ERROR -> {
                    Log.e(" ERROR ", " ====> ERROR")
                }
                else -> {
                    Log.e(" else ", " ====> auth")
                }
            }
        }
    }

    private fun checkTransactionStatus(orderTrackingId: String){
//        viewModel.
    }




    private fun startStop(textViewTime: TextView?, progressBarCircle: ProgressBar?) {
        if (timerStatus == TimerStatus.STOPPED) {
            // call to initialize the progress bar values
            setProgressBarValues(progressBarCircle)
            // showing the reset icon
            timerStatus = TimerStatus.STARTED
            // call to start the count down timer
            startCountDownTimer(textViewTime, progressBarCircle)
        } else {

            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED
            stopCountDownTimer()
        }
        closeMpesaDialog!!.setOnClickListener { //exit dialog
            timerStatus = TimerStatus.STOPPED
            stopCountDownTimer()
        }
    }

    private fun startCountDownTimer(textViewTime: TextView?, progressBarCircle: ProgressBar?) {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                textViewTime!!.text = hmsTimeFormatter(millisUntilFinished)
                val progress = millisUntilFinished / 1000
                progressBarCircle!!.progress = progress.toInt()

            }

            override fun onFinish() {
                textViewTime!!.text = hmsTimeFormatter(timeCountInMilliSeconds)
                // call to initialize the progress bar values
                setProgressBarValues(progressBarCircle)
                timerStatus = TimerStatus.STOPPED
                stopCountDownTimer()
            }
        }.start()
    }

    private fun hmsTimeFormatter(milliSeconds: Long): String {
        return String.format(
            Locale.getDefault(),
            "%02d",
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
            )
        )
    }

    private fun setProgressBarValues(progressBarCircle: ProgressBar?) {
        progressBarCircle!!.max = timeCountInMilliSeconds.toInt() / 1000
        progressBarCircle.progress = timeCountInMilliSeconds.toInt() / 1000
    }

    private fun createCountDownDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.item_check_payment_status, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        textViewTime = dialogView.findViewById(R.id.tvTime)
        closeMpesaDialog = dialogView.findViewById(R.id.closeMpesaDialog)
        progressBarCircle = dialogView.findViewById(R.id.progressBarCircle)
    }

    private fun stopCountDownTimer() {
        if (textViewTime != null) {
            textViewTime!!.text = "0"
            countDownTimer!!.cancel()
        }
        hideDialog()
    }

    private fun hideDialog() {
        if (alertDialog != null) {
            if (alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
        }
    }

    enum class TimerStatus {
        STOPPED, STARTED
    }


}