package com.pesapal.paymentgateway.profile

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pesapal.paymentgateway.MainActivity
import com.pesapal.paymentgateway.databinding.FragmentProfileBinding
import com.pesapal.paymentgateway.model.UserModel
import com.pesapal.paymentgateway.utils.PrefManager
import com.squareup.picasso.Picasso

class ProfileFragment: Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    var testCurrency = arrayOf(
        "KES", "UGX", "USD"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initData()
        configureGoogleSign()
    }


    private fun configureGoogleSign() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.pesapal.paymentgateway.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    }


    private fun initData(){
        binding.layoutProfile.spinnerCurrency.onItemSelectedListener = this;
        val ad = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            testCurrency
        )

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.layoutProfile.spinnerCurrency.adapter = ad;


        if(auth.currentUser != null){
            fetchUserDetails()
        }
        handleClick()

    }

    private fun handleClick(){
        binding.layoutProfile.btnSignout.setOnClickListener {
            googleSignInClient.signOut()
           auth.signOut()
            restart()
        }
    }

    fun restart() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finishAffinity()
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
        binding.layoutProfile.tvUserName.text = ": "+userModel.firstName
        binding.layoutProfile.tvLastName.text = ": "+userModel.lastName
        binding.layoutProfile.tvEmail.text = ": "+userModel.email

    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
    }
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val currency = testCurrency[p2]
        Log.e("currency ", " ==> $currency")
        PrefManager.setCurrency(currency)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}