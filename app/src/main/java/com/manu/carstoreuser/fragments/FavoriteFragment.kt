package com.manu.carstoreuser.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.manu.carstoreuser.R
import com.manu.carstoreuser.adopter.CarAdopter
import com.manu.carstoreuser.adopter.CarMainAdopter
import com.manu.carstoreuser.databinding.FragmentFavoriteBinding
import com.manu.carstoreuser.model.CarModel
import com.manu.carstoreuser.utils.Utils


class FavoriteFragment : Fragment() {

    private val binding by lazy { FragmentFavoriteBinding.inflate(LayoutInflater.from(context)) }
    private lateinit var dbFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var carAdopter: CarMainAdopter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        carAdopter = CarMainAdopter(requireContext(), ArrayList(), ArrayList())
        binding.favoriteRv.adapter = carAdopter

        getFavoriteList()


        binding.ivFavBack.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_favorites_to_navigation_home)
        }

    }

    private fun getFavoriteList() {

        dbFireStore.collection(Utils.FS_WISHLIST).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val favList = documentSnapshot.get("favorite") as? ArrayList<String>

                    if (!favList.isNullOrEmpty()) {
                        fetchFavoriteCars(favList)
                        binding.favMainLayout.visibility = View.VISIBLE
                        binding.favLoadingLayout.visibility = View.GONE
                    } else {
                        val status = "Favorite List Empty"
                        binding.tvFavStatus.text = status
                    }
                } else {
                    val status = "Favorite List Empty"
                    binding.tvFavStatus.text = status
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch favorite list", Toast.LENGTH_SHORT).show()
            }

    }

    private fun fetchFavoriteCars(favList: ArrayList<String>) {
        dbFireStore.collection(Utils.FS_CAR_LIST)
            .whereIn("carId", favList)
            .get()
            .addOnSuccessListener { carListSnapshot ->
                val favoriteCars = carListSnapshot.toObjects<CarModel>()
                carAdopter.updateData(favoriteCars, favList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch favorite cars", Toast.LENGTH_SHORT).show()
            }
    }


}