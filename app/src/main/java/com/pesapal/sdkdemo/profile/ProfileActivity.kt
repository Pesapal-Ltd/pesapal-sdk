package com.pesapal.sdkdemo.profile
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pesapal.sdkdemo.MainActivity
import com.pesapal.sdkdemo.R
import com.pesapal.sdkdemo.databinding.ActivityProfileBinding
import com.pesapal.sdkdemo.model.UserModel
import com.pesapal.sdkdemo.utils.PrefManager
import com.pesapal.sdkdemo.utils.PrefUtil
import com.pesapal.sdkdemo.utils.PrefUtil.countriesList
import com.pesapal.sdkdemo.utils.PrefUtil.demoKeys
import com.pesapal.sdkdemo.utils.PrefUtil.otherCurrency
import com.squareup.picasso.Picasso

data class KeysSecret(val country:String, val key:String, val secret: String)
class ProfileActivity: AppCompatActivity(), OnItemSelectedListener {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var testCurrency = mutableListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(binding.root)
        setToolBar()
        initData()
        configureGoogleSign()
    }

    private fun setToolBar(){
        this.setSupportActionBar(binding.toolbar);
        this.supportActionBar!!.title = getString(R.string.handle_profile)
    }
    private fun configureGoogleSign() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.pesapal.sdkdemo.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun initData(){
        auth = FirebaseAuth.getInstance()

        binding.layoutProfile.spinnerCurrency.onItemSelectedListener = this;
        val default = PrefManager.getCurrency()
//        testCurrency.add(default)
//        otherCurrency.remove(default)
        testCurrency.addAll(otherCurrency)
        val ad = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            testCurrency
        )

        val adCountry = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            countriesList
        )
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item);
        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.layoutProfile.spinnerCurrency.adapter = ad


        adCountry.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item);
        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.layoutProfile.spinnerCountry.adapter = adCountry
        binding.layoutProfile.spinnerCountry.onItemSelectedListener = countryItemSelectedListener



        val currencySelected: Int = ad.getPosition(PrefManager.getCurrency())
        val countrySelected: Int = adCountry.getPosition(PrefManager.getCountry())

        binding.layoutProfile.spinnerCurrency.setSelection(currencySelected)
        binding.layoutProfile.spinnerCountry.setSelection(countrySelected)


        // todo for internal app make it use fire auth
//        if(auth.currentUser != null){
//            fetchUserDetails()
//        }else{
//            binding.layoutProfile.btnSignout.visibility = View.GONE
//        }

        binding.layoutProfile.toggle.isChecked = PrefManager.getIsProduction()

        binding.layoutProfile.toggle.setOnCheckedChangeListener { _, isChecked ->
            PrefManager.setIsProduction(isChecked)

        }

        handleClick()

    }

    private fun handleClick(){
        binding.layoutProfile.btnSubmit.setOnClickListener {
            if(auth.currentUser != null) {
                googleSignInClient.signOut()
                auth.signOut()
                restart()
            }else{

            }
        }
    }

    fun restart() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun fetchUserDetails(){
         var db = FirebaseFirestore.getInstance()
        val documentUser = db.collection("users").document(auth.currentUser?.email!!).get()
        documentUser.addOnCompleteListener {
            if(it.isSuccessful){
                val displayName: String? = it.result.get("displayName").toString()
                val firstName: String? = it.result.get("firstName").toString()
                val lastName: String? = it.result.get("lastName").toString()
                val email: String? = it.result.get("email").toString()
                val photoUrl: String? = it.result.get("photoUrl").toString()
                val time: String? = it.result.get("time").toString()
                var userModel = UserModel(displayName,firstName,lastName,email,photoUrl,time)
                showUserDetails(userModel)
            }else{
                showMessage("Unable to get your account ")
            }
        }
    }

    private fun showUserDetails(userModel: UserModel){
        if(userModel.photoUrl != null) {
            Picasso.get().load(userModel.photoUrl).into(binding.layoutProfile.civProfile);
        }
        binding.layoutProfile.tvUserName.setText(": "+userModel.firstName)
        binding.layoutProfile.tvLastName.setText(": "+userModel.lastName)
        binding.layoutProfile.tvEmail.setText(": "+userModel.email)

    }

    private fun showMessage(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private val countryItemSelectedListener = object : OnItemSelectedListener{
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            Log.i("ProfileActivity", "Position $p2")
            PrefUtil.setData(p2)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val currency = testCurrency[p2]
        PrefManager.setCurrency(currency)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}