package com.pesapal.paymentgateway.payment.fragment.mpesa.stk

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pesapal.paymentgateway.databinding.BottomSheetCheckPaymentBinding


class CheckPaymentBsf : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCheckPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCheckPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUi() {
    }


}