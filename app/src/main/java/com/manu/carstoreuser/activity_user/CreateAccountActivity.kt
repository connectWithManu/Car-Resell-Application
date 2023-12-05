package com.manu.carstoreuser.activity_user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.manu.carstoreuser.activity.HomeMainActivity
import com.manu.carstoreuser.databinding.ActivityCreateAccountBinding
import com.manu.carstoreuser.model.UserModel
import com.manu.carstoreuser.utils.Utils

class CreateAccountActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCreateAccountBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var dbFireStore: FirebaseFirestore
    private var login = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbFireStore = FirebaseFirestore.getInstance()

       login = intent.getBooleanExtra("google", false)
        if(login) {
           binding.etEmailId.setText(auth.currentUser!!.email.toString() )
            binding.etEmailId.isEnabled = false
        }

        binding.btCreateAcBack.setOnClickListener {
            launchWelcomeActivity()
        }

        binding.tvAlreadyAc1.setOnClickListener {
            launchWelcomeActivity()
        }

        binding.tvAlreadyAc2.setOnClickListener {
            launchWelcomeActivity()
        }
        
        binding.btCreateAccount.setOnClickListener { 
            registerNewUser()
        }
    }

    private fun registerNewUser() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmailId.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val createPassword = binding.etCreatePass.text.toString().trim()
        val confirmPassword = binding.etConfirmPass.text.toString().trim()
        
        validateUserInput(firstName, lastName, email, phoneNumber, createPassword, confirmPassword)
    }

    private fun validateUserInput(firstName: String, lastName: String, email: String, phoneNumber: String, createPassword: String, confirmPassword: String) {
        if (firstName.isEmpty()) {
            binding.etFirstName.requestFocus()
            binding.etFirstName.error = "Empty"
        } else if (lastName.isEmpty()) {
            binding.etLastName.requestFocus()
            binding.etLastName.error = "Empty"
        } else if (email.isEmpty()) {
            binding.etEmailId.requestFocus()
            binding.etEmailId.error = "Empty"
        } else if (createPassword.isEmpty()) {
            binding.etCreatePass.requestFocus()
            binding.etCreatePass.error = "Empty"
        } else if (confirmPassword.isEmpty()) {
            binding.etConfirmPass.requestFocus()
            binding.etConfirmPass.error = "Empty"
        }  else if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.requestFocus()
            binding.etPhoneNumber.error = "Empty"
        }  else if (confirmPassword != createPassword) {
            binding.etConfirmPass.requestFocus()
            binding.etCreatePass.requestFocus()
            Toast.makeText(this@CreateAccountActivity, "Password not Matched", Toast.LENGTH_SHORT).show()
        } else {

            createUser(firstName,lastName,email,phoneNumber)
        }
    }

    private fun createUser(firstName: String, lastName: String, email: String, phoneNumber: String) {
        Utils.loading(this)
        if (login) {
            val userId = auth.currentUser!!.uid
            val user = UserModel(userId, firstName, lastName, email, phoneNumber)
            dbFireStore.collection(Utils.FS_USERS).document(userId).set(user)
                .addOnSuccessListener {

                    Toast.makeText(this@CreateAccountActivity, "User account Created", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@CreateAccountActivity, HomeMainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@CreateAccountActivity, "Something went Wrong", Toast.LENGTH_SHORT).show()
                }
        } else {
            auth.createUserWithEmailAndPassword(email, binding.etConfirmPass.text.toString())
                .addOnSuccessListener {
                    val userId = it.user!!.uid
                    val user = UserModel(userId, firstName, lastName, email, phoneNumber)
                    dbFireStore.collection(Utils.FS_USERS).document(userId).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this@CreateAccountActivity, "User account Created", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@CreateAccountActivity, HomeMainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@CreateAccountActivity, "Something went Wrong", Toast.LENGTH_SHORT).show()
                        }

                }.addOnFailureListener {
                Toast.makeText(this@CreateAccountActivity, "Account Creation Failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun launchWelcomeActivity() {
        startActivity(Intent(this@CreateAccountActivity, WelcomeActivity::class.java))
    }
}