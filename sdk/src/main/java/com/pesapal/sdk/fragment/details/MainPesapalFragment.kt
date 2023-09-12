package com.pesapal.sdk.fragment.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pesapal.sdk.R
import com.pesapal.sdk.activity.PesapalPayActivity
import com.pesapal.sdk.databinding.FragmentPesapalMainBinding
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.TimeUtils

class MainPesapalFragment: Fragment() {
    private lateinit var binding: FragmentPesapalMainBinding

    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

    val dateTime = TimeUtils.getCurrentDateTime()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPesapalMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentDetails = requireArguments().getSerializable("paymentDetails") as PaymentDetails
        billingAddress = requireArguments().getSerializable("billingAddress") as BillingAddress

        initData()
        handlePaymentOptions()
        handleCustomBackPress()
        demoViewInform()
    }

    private fun demoViewInform(){
        val isLive = PrefManager.getBoolean(PrefManager.PREF_IS_URL_LIVE, true)
//        Log.e("Main","Is live ")
        if(!isLive){
        }
        binding.tvDemoVersion.isVisible =  !isLive

    }

    private fun handleCustomBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    returnIntent(PesapalPayActivity.STATUS_CANCELLED, "Txn Cancelled")

                }

            })
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

    private fun initData(){
        binding.tvAmount.text = "${paymentDetails.currency} ${paymentDetails.amount}"
        binding.tvOrderNumber.text = paymentDetails.order_id
        binding.tvDateTime.text = dateTime
    }

    private fun handlePaymentOptions(){
        binding.paymentOptionGroup.setOnCheckedChangeListener { chipGroup: ChipGroup, i: Int ->
            val chip = chipGroup.findViewById<Chip>(i)
            if (chip != null && chip.isChecked) {
                configureSelectedChip(chip)
            }

            when (i) {
                R.id.mpesa -> {
                    configureUnselectedChip(chipGroup.findViewById(R.id.card))
                    proceedMpesa()
                }
                R.id.card -> {
                    configureUnselectedChip(chipGroup.findViewById(R.id.mpesa))
                    proceedToCard()
                }
            }
        }

    }

    private fun proceedMpesa(){
        val action = MainPesapalFragmentDirections.actionPesapalMainFragmentToNavGraphMpesa(paymentDetails,billingAddress)
        clearSelectionAndProceed(action)
    }

    private fun proceedToCard(){
        val action = MainPesapalFragmentDirections.actionPesapalMainFragmentToPesapalCardFragment(paymentDetails, billingAddress)
        clearSelectionAndProceed(action)
    }

    /**
     * The chip group selection needs to cleared or it causes a loop and return to the selected chip navigating to the fragment
     */
    private fun clearSelectionAndProceed(action: NavDirections) {
        binding.paymentOptionGroup.clearCheck()
        findNavController().navigate(action)
    }


    private fun configureUnselectedChip(chip: Chip) {
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.un_selected_color_primary))
        chip.setChipBackgroundColorResource(R.color.colorBackgroundInactive)
        chip.setTextColor(resources.getColor(R.color.black))
        chip.chipStrokeWidth = 0.0f
    }

    private fun configureSelectedChip(chip: Chip) {
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chip.setChipBackgroundColorResource(R.color.purple_200)
        chip.setRippleColorResource(R.color.purple_200)
        chip.chipStrokeWidth = 0.0f
    }

    companion object{
        internal fun newInstance(paymentDetails: PaymentDetails):MainPesapalFragment{
            val fragment = MainPesapalFragment()
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }

}