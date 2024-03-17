package com.pesapal.sdk.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.databinding.ActivityPaymentSdkBinding
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.sec.initializeSecurity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PesapalSdkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSdkBinding

    private val pesapalSdkViewModel: PesapalSdkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(initializeSecurity(this)) {
            binding = ActivityPaymentSdkBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Sdkapp.setContextInstance(this)
        }
    }

    companion object {

        fun returnIntent(activity: Activity, status: String, obj : Any,result:Int = RESULT_CANCELED,){
            val returnIntent = Intent()
            returnIntent.putExtra("status", status)
            val data = if(obj is String){
                obj
            }
            else{
                obj as TransactionStatusResponse
            }
            returnIntent.putExtra("data", data)

            activity.setResult(result, returnIntent)
            activity.finish()
        }

        fun returnIntentBuilder(activity: Activity,status: String, statusCode: String, errType:String, message: String) {
            val data = TransactionStatusResponse(error = TransactionError(code = statusCode, errorType = errType, message = message))
            returnIntent(activity, status, data)
        }

    }

}