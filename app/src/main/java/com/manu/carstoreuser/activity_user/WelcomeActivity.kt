package com.manu.carstoreuser.activity_user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity.HomeMainActivity
import com.manu.carstoreuser.databinding.ActivityWelcomeBinding
import com.manu.carstoreuser.utils.Utils

class WelcomeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityWelcomeBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this@WelcomeActivity, gso)


        binding.tvForgetPass.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, ForgetPassActivity::class.java))
        }

        binding.btLaunchCreateAc.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, CreateAccountActivity::class.java))
        }

        binding.btLogin.setOnClickListener {
            login()
        }

        binding.btGoogleLogins.setOnClickListener {
            signInWithGoogle()
        }

    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.etEmail.requestFocus()
                binding.etEmail.error = "Empty"
                false
            }

            password.isEmpty() -> {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Empty"
                false
            }

            else -> {
                true
            }
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString().trim()
        if (validateForm(email, password)) {
            Utils.loading(this)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@WelcomeActivity, HomeMainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@WelcomeActivity, it.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }

        }
    }

    //sign in with google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Utils.loading(this@WelcomeActivity)
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {

            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this@WelcomeActivity, "Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this@WelcomeActivity, CreateAccountActivity::class.java)
                intent.putExtra("google", true)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@WelcomeActivity, "Something went Wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}