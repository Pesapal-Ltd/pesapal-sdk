package com.pesapal.sdk.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.ybq.android.spinkit.SpinKitView
import com.pesapal.sdk.R

class DialogCard (private val dialogCard: Int): DialogFragment() {

    private lateinit var webView : WebView
    lateinit var progress : SpinKitView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val layoutType = when(dialogCard){
                2 -> {
                    R.layout.dialog_webview
                }
                else -> R.layout.dialog_desc
            }
            val customDialog = requireActivity().layoutInflater.inflate(layoutType, null)
            if(dialogCard == 2){
                setUpWebView(customDialog)
            }

            val iconCancel = customDialog.findViewById<ImageView>(R.id.icon_cancel)
            iconCancel.setOnClickListener{
                dialog!!.cancel()
            }

            builder.setView(customDialog)

            val dialog = builder.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog


        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setUpWebView(customDialog:View) {
//        val sharedPrefs: SharedPreferences = getSharedPreferences(Globals.PREF_KEY, Context.MODE_PRIVATE)
        webView = customDialog.findViewById(R.id.web_view)
        progress = customDialog.findViewById(R.id.progress)
        val settings: WebSettings = webView.settings
        progress.visibility = View.VISIBLE
        settings.javaScriptEnabled = true
        webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView, url: String) {
                super.onPageCommitVisible(view, url)
                progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // Page has finished loading
                progress.visibility = View.INVISIBLE
    //                if (title == getString(R.string.terms_amp_condition)) {
    //                    binding.linearButton.setVisibility(View.VISIBLE)
    //                    setListeners()
    //                }
            }
        }
        val url = "https://www.pesapal.com/support/?c=openfloat"
        if (true) {
            Toast.makeText(context,"Hardcoded url", Toast.LENGTH_SHORT).show()
            webView.loadUrl(url)
        } else {
//            val extraHeaders: MutableMap<String, String> = HashMap()
//            val token = sharedPrefs.getString(Globals.USER_TOKEN_CASHIER, "") +
//                    ":" + sharedPrefs.getString(Globals.USER_TOKEN, "")
//            extraHeaders[getString(R.string.jwt_auth)] = token
            webView.loadUrl(url)
        }
    }

}