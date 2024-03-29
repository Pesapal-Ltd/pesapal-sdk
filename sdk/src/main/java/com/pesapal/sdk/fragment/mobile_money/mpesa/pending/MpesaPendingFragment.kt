package com.pesapal.sdk.fragment.mobile_money.mpesa.pending

import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.R
import com.pesapal.sdk.databinding.FragmentMpesaPendingBinding
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.CountryCodeEval
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_PROV_NAME
import com.pesapal.sdk.utils.Status
import java.util.Locale
import java.util.concurrent.TimeUnit

class MpesaPendingFragment : Fragment() {

    private lateinit var binding: FragmentMpesaPendingBinding
    private val viewModel: MpesaPendingViewModel by viewModels()
    private lateinit var mobileMoneyRequest: MobileMoneyRequest
    private lateinit var pDialog: ProgressDialog
    private var delayTime = 1000L
    private val timeCountInMilliSeconds = 30000L
    private var countDownTimer: CountDownTimer? = null
    private var timerStatus = TimerStatus.STOPPED
    private var timerStated = false
    private var mobileProvider = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMpesaPendingBinding.inflate(layoutInflater)
        return binding.root
    }


    //test automation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mobileMoneyRequest = requireArguments().getSerializable("mobileMoneyRequest") as MobileMoneyRequest
        initData()
        handleBackgroundConfirmation(delayTime)
    }

    private fun initData(){
        mobileProvider = CountryCodeEval.mappingAllCountries[mobileMoneyRequest.paymentMethodId]!!.mobileProvider
        binding.tvPrompt1.text = getString(R.string.prompt_2, mobileProvider)
        var phoneNumber = mobileMoneyRequest.msisdn


        binding.tvPrompt.text = getString(R.string.prompt_1, phoneNumber)

        handleClick()
        handleViewModel()
        handlePrefill()
    }

    private fun handlePrefill(){
        binding.tvInstLipa4.text = "4. Enter Account No as " + mobileMoneyRequest.accountNumber
        binding.tvInstLipa5.text = "5. Enter the Amount " + mobileMoneyRequest.currency + " " + mobileMoneyRequest.amount.setScale(2)
    }

    private fun handleClick(){
        Log.e("Mpesapending","Msisgn is " + mobileMoneyRequest.msisdn)
        if(mobileProvider.contains(MPESA_PROV_NAME)) {
            binding.btnLipab.setOnClickListener {
                showLipaNaMpesa()
            }
        }
        else{
            binding.btnLipab.visibility = View.GONE
            binding.tvPrompt3.visibility = View.GONE
        }

        binding.btnSendLipa.setOnClickListener {
            handleConfirmation()
        }

        binding.btnSendLipab.setOnClickListener{
            handleConfirmation()
        }

        binding.btnResendPrompt.setOnClickListener {
            binding.clLipaNaMpesa.visibility = View.GONE
            binding.clBackgroundCheck.visibility = View.VISIBLE
            handleResend()
        }

    }

    private fun showLipaNaMpesa(){
        if(mobileProvider.contains(MPESA_PROV_NAME)) {
            binding.clBackgroundCheck.visibility = View.GONE
            binding.clLipaNaMpesa.visibility = View.VISIBLE
        }

    }

    private fun handleBackgroundConfirmation(delayTime: Long){
        Handler().postDelayed({
            viewModel.mobileMoneyTransactionStatusBackground(mobileMoneyRequest.trackingId)
        },delayTime)
    }

    private fun handleConfirmation(){
        viewModel.mobileMoneyTransactionStatus(mobileMoneyRequest.trackingId)
    }

    private fun handleResend(){
        delayTime = 1000L
        var mobileMoneyRequest: MobileMoneyRequest = mobileMoneyRequest
        mobileMoneyRequest.trackingId =  mobileMoneyRequest.trackingId
        viewModel.sendMobileMoneyCheckOut(mobileMoneyRequest,"Resending OTP ...")
    }

    private fun handleViewModel(){
        viewModel.mobileMoneyResponse.observe(requireActivity()){
            if(::pDialog.isInitialized) {
                when (it.status) {
                    Status.LOADING -> {
                        pDialog = ProgressDialog(requireContext())
                        pDialog.setMessage(it.message)
                        pDialog.show()
                    }
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        binding.clLipaNaMpesa.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                        showMessage(it.message!!)

                    }
                }
            }
        }

        viewModel.transactionStatus.observe(requireActivity()){
                when (it.status) {
                    Status.LOADING -> {
                        pDialog = ProgressDialog(requireContext())
                        pDialog.setMessage(it.message)
                        pDialog.show()
                    }
                    Status.SUCCESS -> {
                        if(::pDialog.isInitialized) {
                            pDialog.dismiss()
                            proceedToSuccessScreen(it.data!!)
                        }
                    }
                    Status.ERROR -> {
                        if(::pDialog.isInitialized) {
                            pDialog.dismiss()
                            showMessage(it.message!!)
                        }
                    }
                }
        }


        viewModel.transactionStatusBg.observe(requireActivity()){
            when (it.status) {
                Status.LOADING -> {
                    if(!timerStated) {
                        timerStated = true
                        handleBackgroundCheck()
                    }
                }
                Status.SUCCESS -> {
                    handleTimeStop()
                    proceedToSuccessScreen(it.data!!)
                }
                Status.ERROR -> {
                    if(delayTime != 30000L){
                        delayTime += 1000
                        handleBackgroundConfirmation(delayTime)
                    }

                }
                else -> {
                }
            }
        }

    }

    companion object{
        internal fun newInstance(mobileMoneyRequest: MobileMoneyRequest): MpesaPendingFragment {
            val mpesaPendingFragment = MpesaPendingFragment()
            mpesaPendingFragment.mobileMoneyRequest = mobileMoneyRequest
            return mpesaPendingFragment
        }

        fun newInstance(): MpesaPendingFragment {
            return MpesaPendingFragment()
        }
    }

    private fun proceedToSuccessScreen(transactionStatusResponse: TransactionStatusResponse){
        var action = MpesaPendingFragmentDirections.actionMpesaPendingFragmentToMpesaSuccessFragment(transactionStatusResponse)
        findNavController().navigate(action)
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            // call to initialize the progress bar values
            setProgressBarValues(binding.progressBarCircle)
            // showing the reset icon
            timerStatus = TimerStatus.STARTED
            // call to start the count down timer
            startCountDownTimer()
        } else {

            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED
            stopCountDownTimer()
        }

    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val milis = hmsTimeFormatter(millisUntilFinished)
                binding.tvTime.text = "00:$milis"
                val progress = millisUntilFinished / 1000
                binding.progressBarCircle.progress = progress.toInt()
                //                checkMpesa();
            }

            override fun onFinish() {
                binding.tvTime.text = hmsTimeFormatter(timeCountInMilliSeconds)
                // call to initialize the progress bar values
                setProgressBarValues(binding.progressBarCircle)
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


    private fun handleBackgroundCheck(){
        startStop()
    }

    private fun handleTimeStop(){
        binding.tvTime.text = "00:00"
        if(countDownTimer != null) {
            countDownTimer!!.cancel()
        }
    }

    private fun stopCountDownTimer() {
        delayTime = 30000L
        binding.tvTime.text = "00:00"
        countDownTimer!!.cancel()
        hideDialog()
    }

    private fun hideDialog() {
        timerStated = false
        if(mobileProvider.contains(MPESA_PROV_NAME)) {
            showLipaNaMpesa()
        }
        else{
            binding.btnSendLipab.visibility = View.VISIBLE

        }
    }

    enum class TimerStatus {
        STOPPED, STARTED
    }

}