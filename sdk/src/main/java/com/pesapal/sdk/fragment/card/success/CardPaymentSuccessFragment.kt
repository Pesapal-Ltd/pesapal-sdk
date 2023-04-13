package com.pesapal.sdk.fragment.card.success

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.viewmodel.AppViewModel
import com.pesapal.sdk.databinding.FragmentCardTxnStatusBinding

class CardPaymentSuccessFragment : Fragment() {

    private lateinit var binding: FragmentCardTxnStatusBinding
    private lateinit var transactionStatusResponse: TransactionStatusResponse
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardTxnStatusBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionStatusResponse = requireArguments().getSerializable("transactionStatusResponse") as TransactionStatusResponse
        initData()
    }

    private fun initData(){
        handleDisplay()
        handleClicks()
    }

    private fun handleClicks(){
        binding.btnDone.setOnClickListener {
            viewModel.handlePaymentStatus("COMPLETED")
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
        binding.tvTxnId.text = "TXN ID: "+transactionStatusResponse.confirmationCode
//        binding.tvPaymentMethod.text = "PAYMENT METHOD: "+transactionStatusResponse.paymentMethod!!.uppercase()
//        binding.tvAmount.text = "AMOUNT: "+transactionStatusResponse.currency +". "+transactionStatusResponse.amount
    }


    companion object{
        fun newInstance(transactionStatusResponse: TransactionStatusResponse): CardPaymentSuccessFragment {
            val fragment = CardPaymentSuccessFragment()
            fragment.transactionStatusResponse = transactionStatusResponse
            return fragment
        }
    }

}