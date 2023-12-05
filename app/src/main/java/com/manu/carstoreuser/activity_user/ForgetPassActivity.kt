package com.manu.carstoreuser.activity_user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manu.carstoreuser.R
import com.manu.carstoreuser.databinding.ActivityForgetPassBinding

class ForgetPassActivity : AppCompatActivity() {
    private val binding by lazy { ActivityForgetPassBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btForgetBack.setOnClickListener {
            startActivity(Intent(this@ForgetPassActivity, WelcomeActivity::class.java))
        }
    }
}