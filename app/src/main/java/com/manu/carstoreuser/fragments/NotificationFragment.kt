package com.manu.carstoreuser.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.manu.carstoreuser.R
import com.manu.carstoreuser.adopter.NotificationAdopter
import com.manu.carstoreuser.databinding.FragmentNotificationBinding
import com.manu.carstoreuser.model.NotificationModel
import com.manu.carstoreuser.utils.Utils


class NotificationFragment : Fragment() {
    private val binding by lazy { FragmentNotificationBinding.inflate(LayoutInflater.from(context)) }
    private lateinit var dbFireStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbFireStore = FirebaseFirestore.getInstance()

        getNotifications()

        binding.ivNotificationBack.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_notification_to_navigation_home)
        }
    }

    private fun getNotifications() {
        val notificationList = ArrayList<NotificationModel>()
        dbFireStore.collection(Utils.FS_NOTIFICATION_COLLECTION).get()
            .addOnSuccessListener {
                notificationList.clear()
                for(doc in it.documents) {
                    val notification = doc.toObject<NotificationModel>()
                    notificationList.add(notification!!)
                }
                if(notificationList.isNotEmpty()) {
                    binding.notificationLoadingLay.visibility = View.GONE
                    binding.notificationMainLay.visibility = View.VISIBLE

                    binding.notificationRv.adapter = NotificationAdopter(requireContext(), notificationList)
                } else {
                    binding.notificationLoadingLay.visibility = View.VISIBLE
                    val status = "No Notifications"
                    binding.tvNotificationStatus.text = status
                }
            }
    }


}