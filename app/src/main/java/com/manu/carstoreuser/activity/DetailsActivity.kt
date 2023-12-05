package com.manu.carstoreuser.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.manu.carstoreuser.R
import com.manu.carstoreuser.adopter.CarImageAdapter
import com.manu.carstoreuser.adopter.CarMainAdopter
import com.manu.carstoreuser.databinding.ActivityDetailsBinding
import com.manu.carstoreuser.model.CarModel
import com.manu.carstoreuser.utils.Utils
import java.util.Calendar

class DetailsActivity : AppCompatActivity() {
    private val binding by lazy {ActivityDetailsBinding.inflate(layoutInflater)}
    private lateinit var dbFireStore: FirebaseFirestore
    private lateinit var carImages: ArrayList<String>
    private lateinit var auth: FirebaseAuth
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dbFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        carImages = ArrayList()
        val carId = intent.getStringExtra(Utils.CAR_ID)
        val categoryName = intent.getStringExtra(Utils.CATEGORY_NAME)

        checkIsFavorite(carId)
        getCarDetails(carId!!)
        setSimilarCars(categoryName)

        binding.btDetaisBack.setOnClickListener {
            val intent = Intent(this@DetailsActivity, CategoryActivity::class.java)
            intent.putExtra(Utils.CATEGORY_NAME, categoryName)
            startActivity(intent)

        }

    }

    private fun checkIsFavorite(carId: String?) {
        dbFireStore.collection(Utils.FS_WISHLIST).document(auth.currentUser!!.uid)
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val favList = documentSnapshot.get("favorite") as? ArrayList<String> ?: ArrayList()
                    if (favList.contains(carId)) {
                        binding.btDetailFavorite.setImageResource(R.drawable.ic_fav_filled_red)
                    } else {
                        binding.btDetailFavorite.setImageResource(R.drawable.ic_fav_outline)
                    }

                    binding.btDetailFavorite.setOnClickListener {
                        if (favList.contains(carId)) {
                            binding.btDetailFavorite.setImageResource(R.drawable.ic_fav_outline)
                            favList.remove(carId)
                        } else {
                            binding.btDetailFavorite.setImageResource(R.drawable.ic_fav_filled_red)
                            favList.add(carId!!)
                        }
                        dbFireStore.collection(Utils.FS_WISHLIST).document(auth.currentUser!!.uid)
                            .update("favorite", favList)
                    }
                } else {
                    binding.btDetailFavorite.setImageResource(R.drawable.ic_fav_outline)
                }
            }
    }

    private fun setSimilarCars(categoryName: String?) {
        val similarCarList = ArrayList<CarModel>()
        val wishlist = ArrayList<String>()
        dbFireStore.collection(Utils.FS_WISHLIST).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {documentSnapshot ->
                wishlist.clear()
                if (documentSnapshot.exists()) {
                    val favList = documentSnapshot.get("favorite") as? ArrayList<String>
                    favList?.let {
                        wishlist.addAll(it)
                    }
                }

            }

        dbFireStore.collection(Utils.FS_CAR_LIST).whereEqualTo("selectCategory", categoryName).get()
            .addOnSuccessListener {
                similarCarList.clear()
                for (doc in it.documents) {
                    val similarCar = doc.toObject<CarModel>()
                    similarCarList.add(similarCar!!)
                }
                similarCarList.shuffle()
                binding.similarCarRv.adapter = CarMainAdopter(this@DetailsActivity, similarCarList, wishlist)
            }
    }


    private fun getCarDetails(carId: String) {
        dbFireStore.collection(Utils.FS_CAR_LIST).document(carId).get()
            .addOnSuccessListener {
                binding.detailLoadingLayout.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                val carId: String? = it.getString("carId")
                val coverImg: String? = it.getString("coverImg")
                val title: String? = it.getString("title")
                val year: String? = it.getString("year")
                val rating: String? = it.getString("rating")
                val description: String? = it.getString("description")
                val price: String? = it.getString("price")
                val brand: String? = it.getString("brand")
                val insurance: String? = it.getString("insurance")
                val runKm: String? = it.getString("runKm")
                val gearType: String? = it.getString("gearType")
                val engineType: String? = it.getString("engineType")
                val rtoNum: String? = it.getString("rtoNum")

                val seating: String? = it.getString("seating")
                val available: String? = it.getString("available")
                val tag: String? = it.getString("tag")
                val location: String? = it.getString("location")
                carImages = it.get("carImages") as ArrayList<String>

                getCarImages(carImages)

                val subTitle = "$runKm km • $engineType • $gearType"
                val carTitle = "$year $title"
                binding.tvCarTitle.text = carTitle
                binding.tvYear.text = year
                binding.tvRating.text = rating
                binding.tvCarSub.text = subTitle
                binding.tvCarBrand.text= brand
                binding.tvTopLoction.text = location

                binding.tvDescription.text = description
                binding.tvCarPrice.text = price
                binding.tvInsurance.text = insurance
                binding.tvKm.text = runKm
                binding.tvGear.text = gearType
                binding.tvEngine.text = engineType
                binding.tvRto.text = rtoNum!!.uppercase()
                binding.tvCarRto.text = rtoNum.uppercase()
                binding.tvCarYear.text = year
                binding.tvAvailability.text = available
                val seats = "$seating Seats"
                binding.tvSeats.text = seats

                binding.tvBrand.text = brand

                binding.btShare.setOnClickListener {
                    val text = "$coverImg\n\n$title\nThis car at great Price\nDownload the Car store app on PlayStore\nhttps://play.google.com/store/apps/details?id=$packageName"
                    Utils.shareText(this@DetailsActivity, text)
                }

            }
    }

    private fun getCarImages(carImages: ArrayList<String>) {
        binding.imageSlider.load(carImages[0]) {
                placeholder(R.drawable.placeholder)
        }

        val imgAdopter = CarImageAdapter(this@DetailsActivity, carImages, object : CarImageAdapter.OnItemClickListener{
            override fun onItemClick(img: String) {
                binding.imageSlider.load(img) {
                    placeholder(R.drawable.placeholder)
                }
            }

        })
        binding.carImageRv.adapter = imgAdopter
    }




}