package com.pesapal.sdkdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pesapal.sdk.activity.PesapalSdkActivity
import com.pesapal.sdk.model.card.CustomerData
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdkdemo.adapter.DemoCartAdapter
import com.pesapal.sdkdemo.databinding.ActivityMainBinding
import com.pesapal.sdkdemo.model.CatalogueModel
import com.pesapal.sdkdemo.model.UserModel
import com.pesapal.sdkdemo.profile.ProfileActivity
import com.pesapal.sdkdemo.utils.PrefManager
import com.pesapal.sdkdemo.utils.PrefUtil
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity(),DemoCartAdapter.clickedListener {

    private lateinit var binding:ActivityMainBinding
    private var currency = ""
    private var total = BigDecimal.ZERO
    private lateinit var demoCartAdapter: DemoCartAdapter
    private lateinit var catalogueModelList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var itemModelList: MutableList<CatalogueModel.ProductsBean>
    private var orderId = ""
    private lateinit var userModel: UserModel
    private var PAYMENT_REQUEST = 100001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
    }

    private fun initSdk(){
        if(PrefManager.getConsumerKey() == ""){
            PrefUtil.setData(0)
        }
        PESAPALAPI3SDK.init(
            this,
            PrefManager.getConsumerKey(),
            PrefManager.getConsumerSecret(),
            PrefManager.getAccount(),
            PrefManager.getCallBackUrl(),
            "https://test.dev",
            PrefManager.getIsProduction()
        )

//        val consumerKey = "qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW"
//        val consumerSecret = "osGQ364R49cXKeOYSpaOnT++rHs="
//        val account = "1000101"
//
//        val callBack = "http://localhost:56522"
//        val ipn = "https://test.dev"

    }

    private fun initData(){
//        getToken()
        initRecyclerData()
        handleClicks()
    }


    private fun initRecyclerData(){
        catalogueModelList = arrayListOf()
        itemModelList = arrayListOf()
        orderId = createTransactionID()
        catalogueModelList.addAll(
            listOf(
                CatalogueModel.ProductsBean("Chips",R.drawable.chips, BigDecimal(1).setScale(2)),
                CatalogueModel.ProductsBean("Burgers",R.drawable.burgers, BigDecimal(5).setScale(2)),
                CatalogueModel.ProductsBean("Milkshakes",R.drawable.burgers, BigDecimal(500).setScale(2)),
            ))
        demoCartAdapter = DemoCartAdapter(this)
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = demoCartAdapter
        demoCartAdapter.setData(catalogueModelList)
    }

    private fun handleClicks(){
        binding.btnCheckOut.setOnClickListener {
            startPayment()
        }
        binding.civProfile.setOnClickListener {
            startProfile()
        }
    }

    private fun startProfile(){
        startActivity(Intent(this,ProfileActivity::class.java))
    }


    private fun createTransactionID(): String {
        return UUID.randomUUID().toString().uppercase().substring(0,8)
    }

    private fun updateBasketList(){
        total = BigDecimal.ZERO
        for(catelog in itemModelList){
            total += catelog.price
        }
        binding.totalPrice.text = currency+" ${total.setScale(2)}"
        binding.tvOrderId.text = orderId
    }

    private fun showMessage(message: String){
        Log.e(" error ", " message $message")
        Toast.makeText(this@MainActivity,message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        currency = PrefManager.getCurrency()
        initData()
        initSdk()
        updateBasketList()
    }

//    private fun getProfile(){
//        val db = FirebaseFirestore.getInstance()
//        val documentBalance = db.collection("users").document(auth.currentUser?.email!!).get()
//        documentBalance.addOnCompleteListener {
//            if(it.isSuccessful){
//                val photoUrl: String? = it.result.get("photoUrl").toString()
//                if(photoUrl != null) {
//                    setImage(photoUrl)
//                }
//            }else{
////                showMessage("Unable to get your account ")
//            }
//        }
//
//    }

    private fun setImage(photoUrl: String){
        Picasso.get().load(photoUrl).into(binding.civProfile);
    }

    private fun startPayment(){
        hardCodedInfo()
    }

    private fun hardCodedInfo(){
        val displayName = ""
        val firstName   = PrefManager.getString(PrefManager.PREF_FIRST_NAME, "")
        val lastName    = PrefManager.getString(PrefManager.PREF_LAST_NAME, "")
        val email       = PrefManager.getString(PrefManager.PREF_EMAIL, "")
        val phone       = PrefManager.getString(PrefManager.PREF_PHONE, "")

        val photoUrl: String? = null
        val time: String? = null
        userModel = UserModel(displayName,firstName,lastName,email,photoUrl,time,phone)

        initPayment()
    }

    private fun initPayment(){
        val line = "a"
        val countryCode = "b"
        val line2 = "c"
        val emailAddress = "d"
        val city= "e"
        val lastName= "d"
        val phoneNumber= "703318241"
        val state= "d"
        val middleName= "d"
        val postalCode= "00111"
        val firstName= "dd"
        val zipCode= "00100"

        val customerData = CustomerData(line, countryCode, line2, emailAddress, city, lastName, phoneNumber, state, middleName,postalCode, firstName, zipCode)
//        val customerData = CustomerData(phoneNumber = phoneNumber) // For mobile money

        val myIntent = Intent(this, PesapalSdkActivity::class.java)
        myIntent.putExtra(PESAPALAPI3SDK.AMOUNT     , total.toString())
        myIntent.putExtra(PESAPALAPI3SDK.ORDER_ID   ,orderId)
        myIntent.putExtra(PESAPALAPI3SDK.CURRENCY   ,currency)
        myIntent.putExtra(PESAPALAPI3SDK.COUNTRY    ,translateCountryToEnum())
        myIntent.putExtra(PESAPALAPI3SDK.USER_DATA  ,customerData)
        startActivityForResult(myIntent             ,PAYMENT_REQUEST)
    }


//    //import com.pesapal.sdk.utils.PESAPALAPI3SDK   // TODO LOOKS OK
//    import com.pesapal.sdk.utils.Status                 // HIDE IT
//
//    import com.pesapal.sdk.fragment.card.data.CardFragmentCardData
//    import com.pesapal.sdk.fragment.DialogCard
//    import com.pesapal.sdk.fragment.auth.AuthFragment
//    import com.pesapal.sdk.fragment.details.MainPesapalFragment
//import com.pesapal.sdk.activity.PesapalSdkActivity                // HIDE IT
//    import com.pesapal.sdk.activity.PesapalPayActivity

//import com.pesapal.sdk.model.txn_status.TransactionStatusResponse                     TODO TRANSFER IT
//import com.pesapal.sdk.model.txn_status.TransactionError

//import com.pesapal.sdk.model.card.       todo all of them
//import  com.pesapal.sdk.R.layout.item_pay_method     todo layout files too

//    import com.pesapal.sdk.model.card.



    /**
     * Converts the country chosen to an to the relevant ENUM
     */
    private fun translateCountryToEnum(): PESAPALAPI3SDK.COUNTRIES_ENUM{
        return when(PrefManager.getCountry()){
            "Uganda" -> {
                PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_UG
            }
            "Tanzania" ->{
                PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_TZ
            }
            else -> {
                PESAPALAPI3SDK.COUNTRIES_ENUM.COUNTRY_KE
            }
        }
    }

    override fun Clicked(isAdd: Boolean, catalogueModel: CatalogueModel.ProductsBean) {
        if(isAdd){
            itemModelList.add(catalogueModel)
            updateBasketList()
        }else{
            itemModelList.remove(catalogueModel)
            updateBasketList()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == PAYMENT_REQUEST) {
             orderId = createTransactionID()
             binding.tvOrderId.text = "Order ID $orderId"
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("status")
                val transactionStatusResponse = data?.getSerializableExtra("data") as TransactionStatusResponse?

                Log.e("MainsUCCESS" , "Error message is " + transactionStatusResponse?.error?.message)
                Log.e("Main" , "Error code is " + transactionStatusResponse?.error?.code)
                Log.e("Main" , "Error type is " + transactionStatusResponse?.error?.errorType)


                transactionStatusResponse?.let {
                    val statusCode = transactionStatusResponse.statusCode
                    Log.e("Main" , "Status code type is $statusCode")
                    when(statusCode){
                        1 -> {
                            handleCompletedTxn(transactionStatusResponse)
                        }
                        0,2,4-> {

                            handleCompletedTxn(transactionStatusResponse)
                        }
                        else -> {
                            //
                        }
                    }
                }
            }
             else {

                 // A failed payment
                val status = data?.getStringExtra("status")
                val transactionStatusResponse = data?.getSerializableExtra("data") as TransactionStatusResponse?

                Log.e("Main FAILED" , "status message is $status")
                Log.e("Main FAILED" , "Error message is " + transactionStatusResponse?.error?.message)
                Log.e("Main FAILED" , "Error code is " + transactionStatusResponse?.error?.code)
                Log.e("Main FAILED" , "Error type is " + transactionStatusResponse?.error?.errorType)


                transactionStatusResponse?.let {response ->
                    val message = response.error?.message?:"An Error Occurred, Please try again later ..."
                    response.error?.errorType.let {errorType ->

                        // todo make them one liners  PESAPALAPI3SDK.ERR_SECURITY -> { // Handle security concerns from the sdk }
                        // todo refer user to error page for description on the error

                        when(errorType){
                            PESAPALAPI3SDK.ERR_INIT -> {
                                handleCancelledTxn("Init $message")
                            }
                            PESAPALAPI3SDK.ERR_SECURITY -> {
                                handleCancelledTxn("Sec $message")
                            }
                            PESAPALAPI3SDK.ERR_NETWORK -> {
                                handleCancelledTxn("Network $message")
                            }
                            PESAPALAPI3SDK.ERR_GENERAL ->{
                                handleCancelledTxn("User cancelled the transaction.")
                            }
                            else -> {
                                handleCancelledTxn(message)
                            }
                        }

                    }
                }
            }

         }
    }



    private fun handleCompletedTxn(transactionStatusResponse: TransactionStatusResponse){
        itemModelList.clear()
        demoCartAdapter.notifyDataSetChanged()
        if(transactionStatusResponse.description != null) {
            showMessage(transactionStatusResponse.description!!)
        }else{
            showMessage(transactionStatusResponse.message!!)
        }
    }


    private fun handleCancelledTxn(message: String){
        showMessage(message)
    }

    private fun handleDefaultError(message: String){
        showMessage(message)
    }

}