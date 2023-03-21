package com.pesapal.paymentgateway
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.pesapal.paygateway.activities.payment.activity.PesapalPayActivity
import com.pesapal.paygateway.activities.payment.model.txn_status.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.utils.PESAPALAPI3SDK
import com.pesapal.paymentgateway.adapter.DemoCartAdapter
import com.pesapal.paymentgateway.databinding.ActivityMainBinding
import com.pesapal.paymentgateway.model.CatalogueModel
import com.pesapal.paymentgateway.model.UserModel
import com.pesapal.paymentgateway.profile.ProfileActivity
import com.pesapal.paymentgateway.utils.PrefManager
import com.pesapal.paymentgateway.utils.TimeUtils
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity(),DemoCartAdapter.clickedListener {

    private lateinit var binding:ActivityMainBinding
    private var currency = PrefManager.getCurrency()
    private lateinit var auth: FirebaseAuth
    private var total = BigDecimal.ZERO
    private lateinit var demoCartAdapter: DemoCartAdapter
    private lateinit var catalogueModelList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var itemModelList: MutableList<CatalogueModel.ProductsBean>
    private var orderId = ""
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userModel: UserModel
    private var PAYMENT_REQUEST = 100001
    private var RC_SIGN_IN = 100002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
        initData()
        initSdk()
    }

    private fun initSdk(){
        PESAPALAPI3SDK().init(PrefManager.getConsumerKey(),PrefManager.getConsumerSecret(),PrefManager.getAccount(), PrefManager.getCallBackUrl(), "https://test.dev")
    }

    private fun initData(){
        setToolBar()
        getToken()
        initRecyclerData()
        handleClicks()
    }

    private fun setToolBar(){
        this.setSupportActionBar(binding.toolbar);
        this.supportActionBar!!.title = getString(R.string.app_name)
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("token", token)
        })
    }

    private fun initRecyclerData(){
        catalogueModelList = arrayListOf()
        itemModelList = arrayListOf()
        orderId = createTransactionID()
        catalogueModelList.addAll(
            listOf(
                CatalogueModel.ProductsBean("Chips",R.drawable.chips, BigDecimal(1).setScale(2)),
                CatalogueModel.ProductsBean("Burgers",R.drawable.burgers, BigDecimal(5).setScale(2)),
            ))
        demoCartAdapter = DemoCartAdapter(this)
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = demoCartAdapter
        demoCartAdapter.setData(catalogueModelList)
    }

    private fun handleClicks(){
        binding.btnCheckOut.setOnClickListener {
            if(auth.currentUser != null){
                startPayment()
            }else {
                handleGoogleSignIn()
            }
        }
        binding.civProfile.setOnClickListener {
            startProfile()
        }
    }

    private fun startProfile(){
        startActivity(Intent(this,ProfileActivity::class.java))
    }

    private fun configureGoogleSign() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun handleGoogleSignIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
        binding.tvOrderId.text = "Order ID $orderId"
    }

    private fun showMessage(message: String){
        Log.e(" error ", " message $message")
        Toast.makeText(this@MainActivity,message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            getProfile()
        }
        configureGoogleSign()
    }

    private fun getProfile(){
        val db = FirebaseFirestore.getInstance()
        val documentBalance = db.collection("users").document(auth.currentUser?.email!!).get()
        documentBalance.addOnCompleteListener {
            if(it.isSuccessful){
                val photoUrl: String? = it.result.get("photoUrl").toString()
                if(photoUrl != null) {
                    setImage(photoUrl)
                }
            }else{
                showMessage("Unable to get your account ")
            }
        }

    }

    private fun setImage(photoUrl: String){
        Picasso.get().load(photoUrl).into(binding.civProfile);
    }

    private fun startPayment(){
        val db = FirebaseFirestore.getInstance()
        val documentBalance = db.collection("users").document(auth.currentUser?.email!!).get()
        documentBalance.addOnCompleteListener {
            if(it.isSuccessful){
                val displayName: String = it.result.get("displayName").toString()
                val firstName: String = it.result.get("firstName").toString()
                val lastName: String = it.result.get("lastName").toString()
                val email: String? = it.result.get("email").toString()
                val photoUrl: String? = it.result.get("photoUrl").toString()
                val time: String? = it.result.get("time").toString()
                userModel = UserModel(displayName,firstName,lastName,email,photoUrl,time)
                initPayment()
            }else{
                showMessage("Unable to get your account ")
            }
        }

    }

    private fun initPayment(){
        val myIntent = Intent(this, PesapalPayActivity::class.java)
        myIntent.putExtra("amount",total.toString())
        myIntent.putExtra("order_id",orderId)
        myIntent.putExtra("currency",PrefManager.getCurrency())
        myIntent.putExtra("firstName",userModel.firstName)
        myIntent.putExtra("lastName",userModel.lastName)
        myIntent.putExtra("email",userModel.email)
        startActivityForResult(myIntent,PAYMENT_REQUEST)
    }

    override fun Clicked(isAdd: Boolean, story: CatalogueModel.ProductsBean) {
        if(isAdd){
            itemModelList.add(story)
            updateBasketList()
        }else{
            itemModelList.remove(story)
            updateBasketList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: Exception) {
                showMessage("An error occurred " + e.localizedMessage)
            }
        }else if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("status")
                orderId = createTransactionID()
                binding.tvOrderId.text = "Order ID $orderId"
                when(result){
                    "COMPLETED" -> {
                        val transactionStatusResponse = data?.getSerializableExtra("data") as TransactionStatusResponse
                        handleCompletedTxn(transactionStatusResponse)
                    }
                    "ERROR" -> {
                        val message = data.getStringExtra("data")
                        handleFailedTxn(message!!)
                    }
                    "CANCELLED" -> {
                        val message = data.getStringExtra("data")
                        handleCancelledTxn(message!!)
                    }
                    else -> {
                        handleDefaultError("An Error Occurred, Please try again later ...")
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

    private fun handleFailedTxn(message: String){
        showMessage(message)
    }

    private fun handleCancelledTxn(message: String){
        showMessage(message)
    }

    private fun handleDefaultError(message: String){
        showMessage(message)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showMessage("Login Success ...")
                    val isNew: Boolean = it.result?.additionalUserInfo!!.isNewUser
                    if(isNew) {
                        loginSuccess(account)
                    }else{
                        startPayment()
                    }
                } else {
                    showMessage("Unable to login ..")
                }
            }
    }

    private fun loginSuccess(credential: GoogleSignInAccount){
        val dateTime = TimeUtils.getCurrentDateTime()
        val db = FirebaseFirestore.getInstance()
        val email = credential.email
        val displayName = credential.displayName
        val fname = credential.givenName
        val lname = credential.familyName
        val profileUrl = credential.photoUrl.toString()

        userModel = UserModel(
            displayName,
            fname,
            lname,
            email,
            profileUrl,
            dateTime
        )

        db.collection("users")
            .document(email!!)
            .set(userModel)
            .addOnSuccessListener {
                startPayment()
            }
            .addOnFailureListener {
                showMessage(" Unable to login "+it.localizedMessage)
            }
    }


}