package com.manu.carstoreuser.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.manu.carstoreuser.R
import com.manu.carstoreuser.databinding.FragmentPrivacyBinding

class PrivacyFragment : Fragment() {
   private val  binding by lazy{ FragmentPrivacyBinding.inflate(layoutInflater)}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ivPrivacyBack.setOnClickListener {
            findNavController().navigate(R.id.action_privacyFragment_to_navigation_home)
        }

        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        val url = "https://www.google.com"
        binding.webView.loadUrl(url)

    }
}