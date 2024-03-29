package com.pesapal.sdk.fragment.mobile_money.mpesa.success
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pesapal.sdk.R
import com.pesapal.sdk.activity.PesapalSdkActivity
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.databinding.FragmentMpesaPaymentSuccessBinding
import com.pesapal.sdk.fragment.DialogCard
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.GeneralUtil
import com.pesapal.sdk.utils.PESAPALAPI3SDK.STATUS_COMPLETED
import com.pesapal.sdk.utils.PESAPALAPI3SDK.STATUS_PENDING
import com.pesapal.sdk.utils.TimeUtils

class MpesaSuccessFragment : Fragment() {
    private lateinit var binding: FragmentMpesaPaymentSuccessBinding
    private lateinit var transactionStatusResponse: TransactionStatusResponse
    private lateinit var btnDone: AppCompatButton

    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()
    private var isTxnSuccessful: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMpesaPaymentSuccessBinding.inflate(layoutInflater)
        btnDone = binding.btnDone
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionStatusResponse = requireArguments().getSerializable("transactionStatusResponse") as TransactionStatusResponse
        isTxnSuccessful = requireArguments().getSerializable("isTxnSuccessful") as Boolean
        initData()
        handleCustomBackPress()
    }

    private fun handleCustomBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(isTxnSuccessful)
                        returnPaymentStatus()
                    else
                        tryAgain()
                }
            })
    }

    private fun initData(){
        handleDisplay()
        handleClicks()
    }

    private fun handleClicks(){
        btnDone.setOnClickListener {
            returnPaymentStatus()
        }

//        binding.imageViewCopy.setOnClickListener {
//            setClipboard(requireContext(),transactionStatusResponse.confirmationCode!!)
//        }

    }

    private fun setClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun handleDisplay(){
        var header: String
        if(isTxnSuccessful){
            header = getString(R.string.payment_succesful)
            binding.imgTxnStatus.setImageResource(R.drawable.ic_checked_transparent)
            binding.layoutHeader.background = resources.getDrawable(R.color.txn_success)

            btnDone.background = resources.getDrawable(R.color.blue_pesapal_light)
            btnDone.text = getString(R.string.proceed)
            btnDone.setTextColor(resources.getColor(R.color.white))
        }
        else{
            header = getString(R.string.payment_failed)

            binding.imgTxnStatus.setImageResource(R.drawable.icon_error)
            binding.layoutHeader.background = resources.getDrawable(R.color.pesapal_red)

            val helpUrl = "https://www.pesapal.com/support-ticket"
            binding.tvHelpUrl.text = highlightextTextBlue(getString(R.string.help_url , helpUrl),helpUrl)

            binding.tvOpenTicket.text = highlightextTextBlue(getString(R.string.open_ticket),"Here")

            binding.tvHelpUrl.setOnClickListener {
                setClipboard(requireContext(), helpUrl)
            }

            binding.tvOpenTicket.setOnClickListener {
                DialogCard(3).show(parentFragmentManager, 3.toString())
            }

            binding.linearFurtherAssistance.visibility = View.VISIBLE

            binding.btnTryAgain.visibility = View.VISIBLE
            binding.btnTryAgain.setOnClickListener{
                tryAgain()
            }
        }

        binding.tvPaymentStatus.text = header
        binding.tvMerchantName.text = pesapalSdkViewModel.merchantName
        binding.tvTime.text = TimeUtils.getCurrentDateTime(transactionStatusResponse.createdDate)

        val paymentIcon = with(transactionStatusResponse.paymentMethod!!.lowercase()){
            when{
                isNullOrEmpty() -> R.drawable.pesapal_logo
                contains("visa")  -> R.drawable.ic_visa
                contains("mpesa") -> R.drawable.mpesa
                contains("master") -> R.drawable.ic_mastercard
                contains("ame") -> R.drawable.amex
                contains("airtel") -> R.drawable.airtel
                else -> R.drawable.pesapal_logo
            }
        }
        binding.iconPayMethod.setImageResource(paymentIcon)

        binding.tvCurrency.text = transactionStatusResponse.currency
        binding.tvAmount.text = GeneralUtil.formatAmountText(transactionStatusResponse.amount)
        binding.tvConfirmationCode.text = transactionStatusResponse.confirmationCode
        binding.tvMerchantRef.text = transactionStatusResponse.merchantReference
        binding.tvNumberOrCard.text = transactionStatusResponse.paymentAccount
        binding.tvTrackingId.text = transactionStatusResponse.orderTrackingId
    }

    private fun highlightextTextBlue(originalString: String, toBeHighLighted: String): SpannableStringBuilder {
        val openFullString = originalString
        val hereIndex = openFullString.indexOf(toBeHighLighted)
        val hereIndexLength = hereIndex + toBeHighLighted.length
        val hereSpannableStringBuilder = SpannableStringBuilder(openFullString)
        hereSpannableStringBuilder.setSpan(
            ForegroundColorSpan(Color.BLUE),
            hereIndex,
            hereIndexLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return hereSpannableStringBuilder
    }

    private fun tryAgain(){
        val action = MpesaSuccessFragmentDirections.actionTxnresultToPesapalMainFragment()
        findNavController().navigate(action)
    }


    private fun returnPaymentStatus() {
        PesapalSdkActivity.returnIntent(requireActivity(),
            if(isTxnSuccessful) STATUS_COMPLETED else STATUS_PENDING, transactionStatusResponse,
            AppCompatActivity.RESULT_OK)
    }

    private fun returnIntent(status: String, obj : Any){
        val returnIntent = Intent()
        returnIntent.putExtra("status", status)
        val data = if(obj is String){
            obj
        }
        else{
            obj as TransactionStatusResponse
        }
        returnIntent.putExtra("data", data)

        requireActivity().setResult(AppCompatActivity.RESULT_OK, returnIntent)
        requireActivity().finish()
    }

}