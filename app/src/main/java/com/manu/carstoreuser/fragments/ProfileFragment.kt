package com.manu.carstoreuser.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity_user.WelcomeActivity
import com.manu.carstoreuser.databinding.DialogDesignBinding
import com.manu.carstoreuser.databinding.FragmentProfileBinding
import com.manu.carstoreuser.utils.Utils


class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(LayoutInflater.from(context)) }
    private lateinit var auth: FirebaseAuth
    private lateinit var dbFireStore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        dbFireStore = FirebaseFirestore.getInstance()

        userId = auth.currentUser!!.uid

        getUserData(userId)

        binding.tvDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        binding.layLocate.setOnClickListener {
            Utils.launchLocation(requireContext())
        }

        binding.layContact.setOnClickListener {
            Utils.launchContact(requireContext())
        }

        binding.layDev.setOnClickListener {
            Utils.contactDeveloper(requireContext())
        }

        binding.layPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_Profile_to_privacyFragment)
        }

        binding.btSignOut.setOnClickListener {
           logout()
        }

        binding.layAbout.setOnClickListener {
            val description = getString(R.string.about_us)
            val buttonText = "Close"
            val title = "About Us"
            Utils.showDialog(requireContext(), title, description, buttonText) {
                val dialogDesign = DialogDesignBinding.inflate(layoutInflater)
                dialogDesign.btConfirm.visibility = View.GONE
            }
        }
    }

    private fun logout() {
        if(auth.currentUser != null) {
            val title = "Logout Your Account"
            val description = "Do you really want to logout, We will miss you... if you still want to cancel this process else you can click logout to proceed"
            val confirmBtnText ="Logout"
            if(auth.currentUser != null) {
                Utils.showDialog(
                    requireContext(), title, description, confirmBtnText) {
                    auth.signOut()
                    Toast.makeText(requireContext(), "Account Logout Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                    }
                }
            } else {
                Toast.makeText(requireContext(), "User Not Login", Toast.LENGTH_SHORT).show()

        }
    }

    private fun deleteAccount() {
        val title = "Delete Your Account"
        val description = "Are you sure to Delete Car store account, Once you delete you cant retrieve you action."
        val confirmBtnText ="Delete"
        if(auth.currentUser != null) {
            Utils.showDialog(
                requireContext(), title, description, confirmBtnText) {
                auth.currentUser!!.delete().addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        dbFireStore.collection(Utils.FS_USERS).document(userId).delete()
                        Toast.makeText(requireContext(), "Account Deleted Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                    } else {
                        Toast.makeText(requireContext(), "Process Failed", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User Not Login", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getUserData(userId: String) {
        dbFireStore.collection(Utils.FS_USERS).document(userId).get()
            .addOnSuccessListener {

                val firstName = it.getString("firstName")
                val lastName = it.getString("lastName")
                val email = it.getString("email")
                val phoneNum = it.getString("phoneNum")

                binding.tvFirstName.text = firstName
                binding.tvLastName.text = lastName
                val address = "$phoneNum\n$email"
                binding.tvAddress.text = address
                val profileName = "${firstName!!.first()}${lastName!!.first()}"
                binding.tvProfile.text = profileName.uppercase()
            }
    }
}