package com.pesapal.sdk.fragment.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pesapal.sdk.R
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.activity.PesapalPayActivity
import com.pesapal.sdk.activity.PesapalSdkViewModel
import com.pesapal.sdk.databinding.FragmentPesapalMainBinding
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.*
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_KE
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_TZ
import com.pesapal.sdk.utils.CountryCodeEval.AIRTEL_UG
import com.pesapal.sdk.utils.CountryCodeEval.CARD
import com.pesapal.sdk.utils.CountryCodeEval.MPESA
import com.pesapal.sdk.utils.CountryCodeEval.MPESA_TZ
import com.pesapal.sdk.utils.CountryCodeEval.MTN_UG
import com.pesapal.sdk.utils.CountryCodeEval.TIGO_TANZANIA
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class MainPesapalFragment: Fragment() {
    private lateinit var binding: FragmentPesapalMainBinding

    private lateinit var paymentDetails: PaymentDetails
    private lateinit var billingAddress: BillingAddress

    private val pesapalSdkViewModel: PesapalSdkViewModel by activityViewModels()

    private var mobileProviders = listOf<Int>()
    private var selectedChip: Int = -1

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
        handleCustomBackPress()
        demoViewInform()
        mobileProviders = evaluateRegionProvider()
        addDynamicChipGroupViews(mobileProviders)
        handlePaymentOptions()

    }

    private fun evaluateRegionProvider(): List<Int> {
         return when(paymentDetails.country){
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_KE ->{
                 CountryCodeEval.kenyaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_TZ -> {
                 CountryCodeEval.tanzaniaProvider
            }
            PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_UG -> {
                 CountryCodeEval.ugandaProvider
            }
            else -> {
                listOf()
            }
        }
    }

    /**
     * If the application is in demo show text view indicating so
     */
    private fun demoViewInform(){
        val isLive = PrefManager.getBoolean(Sdkapp.getInstance(), PrefManager.PREF_IS_URL_LIVE, true)
        binding.tvDemoVersion.isVisible =  !isLive
    }


    private fun handleCustomBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    returnIntent(PesapalPayActivity.STATUS_CANCELLED, "Payment cancelled")
                }
            })
    }

    /**
     * Add mobile money dynamically depending on regions
     */
    private fun addDynamicChipGroupViews(mobileProviders: List<Int>) {

        val newContext = ContextThemeWrapper(requireContext(), R.style.chip_style_custom)
        val chip = Chip(newContext)
        chip.id = CARD
        chip.text = "Card"
        chip.isCheckable = true
        chip.setChipBackgroundColorResource(R.color.background_color_chip_state_list_changer)
        binding.paymentOptionGroup.addView(chip)

        mobileProviders.forEach{ providerInt ->
            val newContext = ContextThemeWrapper(requireContext(), R.style.chip_style_custom)
            val chip = Chip(newContext)
            val provider = CountryCodeEval.mappingAllCountries[providerInt]
            chip.id = provider!!.paymentMethodId
            chip.text = provider.mobileProvider
            chip.isCheckable = true
            chip.setChipBackgroundColorResource(R.color.background_color_chip_state_list_changer)

            binding.paymentOptionGroup.addView(chip)
        }
    }


    private fun initData(){
        binding.tvAmount.text = "${paymentDetails.currency} ${paymentDetails.amount}"
//        binding.tvOrderNumber.text = paymentDetails.order_id
        binding.tvOrderNumber.text = pesapalSdkViewModel.orderID
        binding.tvDateTime.text = TimeUtils.getCurrentDateTime()
    }

    private fun handlePaymentOptions(){
        binding.paymentOptionGroup.setOnCheckedChangeListener { chipGroup: ChipGroup, i: Int ->
            val chip = chipGroup.findViewById<Chip>(i)
            if (chip != null && chip.isChecked) {
                configureSelectedChip(chip)
                if(selectedChip != null && selectedChip>0) {
                    Log.e("MainPF"," Selected chip is $selectedChip")

                    configureUnselectedChip(chipGroup.findViewById(selectedChip!!))
                }
            }
            selectedChip = i
        }

        binding.btnProceedPayment.setOnClickListener{
            if(selectedChip >0)
                proceedSelected(selectedChip!!)
            else{
                Toast.makeText(requireContext(),"Please select a method", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun proceedSelected(i : Int){
        when (i) {
            CARD -> {
                // todo work on unselecting all other chips
                //    configureUnselectedChip(chipGroup.findViewById(R.id.mpesa))
                proceedToCard()
            }
            MPESA,AIRTEL_KE, MTN_UG, AIRTEL_UG, TIGO_TANZANIA, MPESA_TZ, AIRTEL_TZ -> {
//                    configureUnselectedChip(chipGroup.findViewById(R.id.card))
                proceedMpesa(i)
            }
//                R.id.mpesa -> {
//                    configureUnselectedChip(chipGroup.findViewById(R.id.card))
//                    proceedMpesa()
//                }
        }
    }

    private fun proceedMpesa(i: Int) {
        val mobileProvider = CountryCodeEval.mappingAllCountries[i]
        val min = mobileProvider!!.minimumAmount
        val canProceedMinMeet = paymentDetails.amount >= min.toBigDecimal()
        if(canProceedMinMeet) {
            paymentDetails.mobile_provider = BigDecimal(i)
            val action = MainPesapalFragmentDirections.actionPesapalMainFragmentToNavGraphMpesa(
                paymentDetails,
                billingAddress
            )
            clearSelectionAndProceed(action)
        }
        else{
            Toast.makeText(requireContext(), "Minimum amount for ${mobileProvider.mobileProvider} is $min/=", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMinMaxAmount(mobileProvider: Int){
    }

    private fun proceedToCard(){
        val action = MainPesapalFragmentDirections.actionPesapalMainFragmentToPesapalCardFragment(paymentDetails, billingAddress)
        clearSelectionAndProceed(action)
    }

    /**
     * The chip group selection needs to cleared or it causes a loop and return to the selected chip navigating to the fragment
     */
    private fun clearSelectionAndProceed(action: NavDirections) {
//        binding.paymentOptionGroup.clearCheck()
        findNavController().navigate(action)
    }


    /**
     * todo delete. modifications to the style affect the color scheme
     */
    private fun configureUnselectedChip(chip: Chip) {
//        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.un_selected_color_primary))
////        chip.setChipBackgroundColorResource(R.color.colorBackgroundInactive)
//        chip.setTextColor(resources.getColor(R.color.black))
//        chip.chipStrokeWidth = 0.0f
    }

    /**
     * todo delete. modifications to the style affect the color scheme
     */
    private fun configureSelectedChip(chip: Chip) {
//        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
////        chip.setChipBackgroundColorResource(R.color.background_color_chip_state_list)
//        chip.setRippleColorResource(R.color.blue_pesapal)
//        chip.chipStrokeWidth = 0.0f
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

    companion object{
        internal fun newInstance(paymentDetails: PaymentDetails):MainPesapalFragment{
            val fragment = MainPesapalFragment()
            fragment.paymentDetails = paymentDetails
            return fragment
        }
    }

}