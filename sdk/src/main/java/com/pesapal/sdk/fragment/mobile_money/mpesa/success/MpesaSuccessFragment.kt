package com.pesapal.sdk.fragment.mobile_money.mpesa.success
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.sdk.R
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.databinding.FragmentMpesaPaymentSuccessBinding
import com.pesapal.sdk.fragment.card.viewmodel.CardViewModel
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.GeneralUtil
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdk.utils.TimeUtils
import com.pesapal.sdk.viewmodel.AppViewModel
class MpesaSuccessFragment : Fragment() {
    private lateinit var binding: FragmentMpesaPaymentSuccessBinding
    private lateinit var transactionStatusResponse: TransactionStatusResponse

    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()
    private var isTxnSuccessful: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMpesaPaymentSuccessBinding.inflate(layoutInflater)

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
                    returnPaymentStatus()
                }
            })
    }

    private fun initData(){
        handleDisplay()
        handleClicks()
    }

    private fun handleClicks(){

        binding.btnDone.setOnClickListener {
            returnPaymentStatus()
        }

        binding.imageViewCopy.setOnClickListener {
            setClipboard(requireContext(),transactionStatusResponse.confirmationCode!!)
        }

    }

    private fun setClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun handleDisplay(){
        var header = ""
        if(isTxnSuccessful){
            binding.imgTxnStatus.setImageResource(R.drawable.ic_checked)
            binding.layoutHeader.background = resources.getDrawable(R.color.txn_success)
            header = getString(R.string.payment_succesful)

            binding.btnDone.background = resources.getDrawable(R.color.blue_pesapal_light)
            binding.btnDone.text = getString(R.string.proceed)
        }
        else{
            header = getString(R.string.payment_failed)

            binding.linearFurtherAssistance.visibility = View.VISIBLE
            binding.btnTryAgain.visibility = View.VISIBLE
        }
        binding.tvPaymentStatus.text = header

        // todo look at created date field
        binding.tvTime.text = TimeUtils.getCurrentDateTime()

        binding.tvCurrency.text = transactionStatusResponse.currency
        binding.tvAmount.text = GeneralUtil.formatAmountText(transactionStatusResponse.amount.toDouble())
        binding.tvMerchantRef.text = transactionStatusResponse.confirmationCode
        binding.tvTrackingId.text = pesapalSdkViewModel.orderID


    }


    private fun returnPaymentStatus() {
        val returnIntent = Intent()
        returnIntent.putExtra("status", "COMPLETED")
        returnIntent.putExtra("data", transactionStatusResponse)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, returnIntent)
        requireActivity().finish()
    }

    companion object{
        fun newInstance(transactionStatusResponse: TransactionStatusResponse): MpesaSuccessFragment {
            val fragment = MpesaSuccessFragment()
            fragment.transactionStatusResponse = transactionStatusResponse
            return fragment
        }
    }

}