package com.pesapal.paygateway.activities.payment.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pesapal.paygateway.R
import com.pesapal.paygateway.databinding.FragmentPesapalMainBinding
import com.pesapal.paygateway.activities.payment.viewmodel.AppViewModel
import kotlin.Int
import kotlin.getValue

class MainPesapalFragment: Fragment() {

    private lateinit var binding: FragmentPesapalMainBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var orderNumber: String
    private lateinit var dateTime: String
    private lateinit var amount: String
    private lateinit var currency: String

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
        binding.tvAmount.text = "$currency $amount"
        binding.tvOrderNumber.text = orderNumber
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
        chip.setTextColor(resources.getColor(R.color.colorGreyText))
        chip.setRippleColorResource(R.color.colorGreyOutline)
        chip.chipStrokeWidth = 0.0f
    }

    private fun configureSelectedChip(chip: Chip) {
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chip.setChipBackgroundColorResource(R.color.colorPrimaryAltLight)
        chip.setRippleColorResource(R.color.colorPrimaryAltLight)
        chip.chipStrokeWidth = 0.0f
    }

    companion object{
        fun newInstance(amount: String, orderNumber: String, dateTime: String, currency: String):MainPesapalFragment{
            val fragment = MainPesapalFragment()
            fragment.amount = amount
            fragment.orderNumber = orderNumber
            fragment.dateTime = dateTime
            fragment.currency = currency
            return fragment
        }
    }

}