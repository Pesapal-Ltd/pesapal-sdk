package com.pesapal.paymentgateway.basket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.pesapal.paygateway.activities.payment.activity.PesapalPayActivity
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.model.BasketRecyclerItemTouchHelper
import com.pesapal.paymentgateway.model.CatalogueModel
import com.pesapal.paymentgateway.model.UserModel
import com.pesapal.paymentgateway.utils.TimeUtils
import com.pesapal.paymentgateway.viewmodel.AppViewModel
import java.math.BigDecimal
import java.util.*


class BasketFragment: Fragment(), BucketListAdapter.clickedListener, BasketRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var basketList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var noData: TextView
    private lateinit var totalPrice: TextView
    private lateinit var tvOrderId: TextView
    private lateinit var llCheckOut: CardView
    private lateinit var btnCheckOut: Button
    private var total = BigDecimal.ZERO
    private lateinit var recyclerView: RecyclerView
    private lateinit var bucketListAdapter: BucketListAdapter
    private var PAYMENT_REQUEST = 100001
    private var RC_SIGN_IN = 100002
    private var orderId = ""
    private var currency = "KES :"
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userModel: UserModel

    //2
    companion object {
        fun newInstance(basketList: MutableList<CatalogueModel.ProductsBean>): BasketFragment {
            val fragment = BasketFragment();
            fragment.basketList = basketList
            return fragment;
        }
    }

    //3
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.frag_basket, container, false)
        recyclerView = view.findViewById(R.id.rvRecyclerV)
        totalPrice = view.findViewById(R.id.totalPrice)
        tvOrderId = view.findViewById(R.id.tvOrderId)
        llCheckOut = view.findViewById(R.id.llCheckOut)
        noData = view.findViewById(R.id.noData)
        btnCheckOut = view.findViewById(R.id.btnCheckOut)
        initData()
        clickListener()
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignInData()
    }

    private fun initSignInData(){
        configureGoogleSign()
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
                Log.e(" An error ", " ==> "+e.localizedMessage)
                showMessage("An error occurred " + e.localizedMessage)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showMessage("Login Success ...")
                    loginSuccess(account)
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

        userModel = UserModel(
            displayName,
            fname,
            lname,
            email,
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


    private fun configureGoogleSign() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun handleGoogleSignIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun clickListener(){
        btnCheckOut.setOnClickListener {
            if(mAuth.currentUser != null){
                startPayment()
            }else {
                handleGoogleSignIn()
            }
//               startPayment()
//            basketList.clear()
//            viewModel.checkOut()
//            bucketListAdapter.notifyDataSetChanged()
//            checkViews()
        }
    }

    private fun initData(){
        bucketListAdapter = BucketListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = bucketListAdapter
        bucketListAdapter.setData(basketList)

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            BasketRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        orderId = createTransactionID()
        checkViews()
    }

    override fun Clicked(isSwipe: Boolean, product: CatalogueModel.ProductsBean) {

        if(isSwipe) {
            basketList.remove(product)
            viewModel.removeToBucket(product)
            bucketListAdapter.notifyDataSetChanged()
            checkViews()
        }else{
            basketList.remove(product)
            viewModel.removeToBucket(product)
            bucketListAdapter.notifyDataSetChanged()
            checkViews()
        }
    }

    private fun checkViews(){
        if(basketList.size > 0){
            noData.visibility = View.GONE
            llCheckOut.visibility = View.VISIBLE
            setCartPrice()
        }else{
            noData.visibility = View.VISIBLE
            llCheckOut.visibility = View.GONE
        }
    }

    private fun setCartPrice(){
        for(catelog in basketList){
            total += catelog.price
        }
        totalPrice.text = currency+" ${total.setScale(2)}"
        tvOrderId.text = "Order ID $orderId"
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        val position = viewHolder!!.adapterPosition
        viewModel.removeToBucket(basketList[position])
        basketList.removeAt(position)
        bucketListAdapter.notifyDataSetChanged()
        checkViews()
    }

    private fun startPayment(){
        val db = FirebaseFirestore.getInstance()
        val documentBalance = db.collection("users").document(mAuth.currentUser?.email!!).get()
        documentBalance.addOnCompleteListener {
            if(it.isSuccessful){
                val displayName: String? = it.result.get("displayName").toString()
                val firstName: String? = it.result.get("firstName").toString()
                val lastName: String? = it.result.get("lastName").toString()
                val email: String? = it.result.get("email").toString()
                val time: String? = it.result.get("time").toString()
                userModel = UserModel(displayName,firstName,lastName,email,time)
                initPayment()
            }else{
                showMessage("Unable to get your account ")
            }
        }

            initPayment()
    }

    private fun createTransactionID(): String {
        return UUID.randomUUID().toString().uppercase().substring(0,8)
    }

    override fun onResume() {
        super.onResume()
        mAuth = FirebaseAuth.getInstance()
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
    }

    private fun initPayment(){
        val myIntent = Intent(requireContext(), PesapalPayActivity::class.java)
        myIntent.putExtra("consumer_key","qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW")
        myIntent.putExtra("consumer_secret","osGQ364R49cXKeOYSpaOnT++rHs=")
        myIntent.putExtra("amount",total)
        myIntent.putExtra("order_id",orderId)
        myIntent.putExtra("currency","KES")
        myIntent.putExtra("accountNumber","1000101")
        myIntent.putExtra("callbackUrl","http://localhost:56522")
        myIntent.putExtra("firstName",userModel.firstName)
        myIntent.putExtra("lastName",userModel.lastName)
        myIntent.putExtra("email",userModel.email)
        startActivityForResult(myIntent,PAYMENT_REQUEST)
    }
}