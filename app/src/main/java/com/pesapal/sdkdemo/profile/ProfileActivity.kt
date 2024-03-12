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
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pesapal.sdkdemo.MainActivity
import com.pesapal.sdkdemo.databinding.ActivityProfileBinding
import com.pesapal.sdkdemo.model.UserModel
import com.pesapal.sdkdemo.utils.PrefManager
import com.pesapal.sdkdemo.utils.PrefManager.PREF_EMAIL
import com.pesapal.sdkdemo.utils.PrefManager.PREF_FIRST_NAME
import com.pesapal.sdkdemo.utils.PrefManager.PREF_LAST_NAME
import com.pesapal.sdkdemo.utils.PrefManager.PREF_PHONE
import com.pesapal.sdkdemo.utils.PrefUtil
import com.pesapal.sdkdemo.utils.PrefUtil.countriesList
import com.pesapal.sdkdemo.utils.PrefUtil.keotherCurrency
import com.pesapal.sdkdemo.utils.PrefUtil.tzotherCurrency
import com.pesapal.sdkdemo.utils.PrefUtil.ugotherCurrency
import com.squareup.picasso.Picasso

data class KeysSecret(val country:String, val key:String, val secret: String)
class ProfileActivity: AppCompatActivity(), OnItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityProfileBinding
    var testCurrency = listOf<String>()
    lateinit var currencyAdapter: ArrayAdapter<String>
    lateinit var adCountry: ArrayAdapter<String>
    lateinit var firstNameEt: TextInputEditText
    lateinit var lastNameEt: TextInputEditText
    lateinit var emailEt: TextInputEditText
    lateinit var phoneEt: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(binding.root)

        initData()
        configureGoogleSign()
    }


    private fun setCurrencyAdp(country: String){
        testCurrency = (changeCountryCurrency(country))

        currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, testCurrency)
        spinnerCurrency.adapter = currencyAdapter
    }

    lateinit var spinnerCurrency: AppCompatSpinner
    lateinit var spinnerCountry: AppCompatSpinner
    private fun initData(){
        auth = FirebaseAuth.getInstance()

        firstNameEt = binding.layoutProfile.tvUserName
        lastNameEt = binding.layoutProfile.tvLastName
        emailEt = binding.layoutProfile.tvEmail
        phoneEt = binding.layoutProfile.tvPhone
        spinnerCountry = binding.layoutProfile.spinnerCountry
        spinnerCurrency =   binding.layoutProfile.spinnerCurrency


        firstNameEt.setText(PrefManager.getString(PREF_FIRST_NAME, ""))
        lastNameEt.setText(PrefManager.getString(PREF_LAST_NAME, ""))
        emailEt.setText(PrefManager.getString(PREF_EMAIL, ""))
        phoneEt.setText(PrefManager.getString(PREF_PHONE, ""))


        val default = PrefManager.getCountry()

        setCurrencyAdp(default)
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adCountry = ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesList)

        adCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerCountry.adapter = adCountry

        val currencySelected: Int = currencyAdapter.getPosition(PrefManager.getCurrency())
        val countrySelected: Int = adCountry.getPosition(default)

        Log.e("Prof", "Setting selected is $countrySelected")
        spinnerCurrency.setSelection(currencySelected)
        spinnerCountry.setSelection(countrySelected)

        binding.layoutProfile.toggle.isChecked = PrefManager.getIsProduction()

        spinnerCountry.onItemSelectedListener = countryItemSelectedListener

        spinnerCurrency.onItemSelectedListener = this

        binding.layoutProfile.toggle.setOnCheckedChangeListener { _, isChecked ->
            PrefManager.setIsProduction(isChecked)
        }
        handleClick()

    }

    private fun handleClick(){
        binding.layoutProfile.btnSubmit.setOnClickListener {
            PrefManager.putString(PREF_FIRST_NAME, firstNameEt.text.toString())
            PrefManager.putString(PREF_LAST_NAME, lastNameEt.text.toString())
            PrefManager.putString(PREF_EMAIL, emailEt.text.toString())
            PrefManager.putString(PREF_PHONE, phoneEt.text.toString())
            Log.e("PROF","Setting Select pos " + spinnerCountry.selectedItemPosition)
            Log.e("PROF","Setting country " + countriesList[spinnerCountry.selectedItemPosition])
            PrefUtil.setData(spinnerCountry.selectedItemPosition)

//            PrefManager.setCountry(countriesList[spinnerCountry.selectedItemPosition])

            restart()
        }
    }

    private fun restart() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }


    private fun showMessage(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private val countryItemSelectedListener = object : OnItemSelectedListener{
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            setCurrencyAdp((countriesList[p2]))

        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

    }

    private fun changeCountryCurrency(country: String): List<String> {
        return when(country){
            "Kenya" -> {
                keotherCurrency
            }
            "Uganda" -> {
                ugotherCurrency
            }
            "Tanzania" -> {
                tzotherCurrency
            }
            else -> keotherCurrency

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val currency = testCurrency[p2]
        PrefManager.setCurrency(currency)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


    private fun configureGoogleSign() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.pesapal.sdkdemo.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }    private fun fetchUserDetails(){
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
        binding.layoutProfile.tvPhone.setText(": "+userModel.phone)

    }




}