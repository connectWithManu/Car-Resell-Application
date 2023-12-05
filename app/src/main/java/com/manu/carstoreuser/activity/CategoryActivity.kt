package com.manu.carstoreuser.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.manu.carstoreuser.adopter.CarMainAdopter
import com.manu.carstoreuser.databinding.ActivityCategoryBinding
import com.manu.carstoreuser.model.CarModel
import com.manu.carstoreuser.utils.Utils

class CategoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCategoryBinding.inflate(layoutInflater) }
    private lateinit var dbFireStore: FirebaseFirestore
    private lateinit var carList: ArrayList<CarModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dbFireStore = FirebaseFirestore.getInstance()
        carList = ArrayList()

        val categoryName = intent.getStringExtra(Utils.CATEGORY_NAME)
        binding.tvCategoryName.text = categoryName

        binding.ivCategoryBack.setOnClickListener {
            startActivity(Intent(this@CategoryActivity, HomeMainActivity::class.java))
        }

        getCategoryCars(categoryName)
    }

    private fun getCategoryCars(categoryName: String?) {
        val wishlist = ArrayList<String>()
        dbFireStore.collection(Utils.FS_WISHLIST).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                wishlist.clear()
                val favList = it.get("favorite") as ArrayList<String>
                for(fav in favList) {
                    wishlist.add(fav)
                }
            }

        dbFireStore.collection(Utils.FS_CAR_LIST).whereEqualTo("selectCategory", categoryName).get()
            .addOnSuccessListener {
                carList.clear()
                for(doc in it.documents) {
                    val car = doc.toObject<CarModel>()
                    carList.add(car!!)
                }
                binding.carLoadingLayout.visibility = View.GONE
                binding.carListRv.visibility = View.VISIBLE
                binding.carListRv.adapter = CarMainAdopter(this@CategoryActivity, carList, wishlist )
            }
    }




}