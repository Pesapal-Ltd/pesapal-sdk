package com.pesapal.sdk.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pesapal.sdk.R
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.utils.TimeUtils
import com.pesapal.sdk.databinding.FragmentPesapalMainBinding
import com.pesapal.sdk.viewmodel.AppViewModel
import kotlin.Int
import kotlin.getValue

class MainPesapalFragment: Fragment() {
    private lateinit var binding: FragmentPesapalMainBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var paymentDetails: PaymentDetails
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
        initData()
        handlePaymentOptions()
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
        viewModel.loadFragment("mpesa")
    }

    private fun proceedToCard(){
        viewModel.loadFragment("card")
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
        fun newInstance(paymentDetails: PaymentDetails):MainPesapalFragment{
            val fragment = MainPesapalFragment()
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }

}