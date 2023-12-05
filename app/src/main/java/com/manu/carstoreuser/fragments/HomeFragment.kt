package com.manu.carstoreuser.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity.CategoryActivity
import com.manu.carstoreuser.activity.DetailsActivity

import com.manu.carstoreuser.adopter.CarMainAdopter
import com.manu.carstoreuser.adopter.CategoryAdopter
import com.manu.carstoreuser.databinding.FragmentHomeBinding
import com.manu.carstoreuser.databinding.NavigationLayoutBinding
import com.manu.carstoreuser.model.CarModel
import com.manu.carstoreuser.model.CarSliderModel
import com.manu.carstoreuser.model.CategoryModel
import com.manu.carstoreuser.utils.Utils


class HomeFragment : Fragment() {

    private val binding by lazy { FragmentHomeBinding.inflate(LayoutInflater.from(context)) }
    private lateinit var dbFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerToggle: ActionBarDrawerToggle


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

        fun closeDrawer() {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        drawerToggle = ActionBarDrawerToggle(requireActivity(), binding.drawerLayout, R.string.open, R.string.close)
        drawerToggle.syncState()
        binding.drawerLayout.addDrawerListener(drawerToggle)

        val navigation = NavigationLayoutBinding.inflate(layoutInflater)
        binding.navDrawer.addView(navigation.root)

        navigation.navPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_privacyFragment)
        }

        navigation.navAbout.setOnClickListener {
            val description = getString(R.string.about_us)
            val buttonText = "Close"
            val title = "About Us"
            closeDrawer()
            Utils.showDialog(requireContext(), title, description, buttonText) {

            }
        }

        navigation.navShare.setOnClickListener {
            closeDrawer()
            Utils.shareText(requireContext(), Utils.APP_SHARE)
        }

        navigation.navRate.setOnClickListener {
            closeDrawer()
            openAppUrl()
        }

        navigation.navDeveloper.setOnClickListener {
            Utils.contactDeveloper(requireContext())
            closeDrawer()
        }

        navigation.navUpdate.setOnClickListener {
            closeDrawer()
            openAppUrl()
        }

        binding.btHomeLocate.setOnClickListener {
            closeDrawer()
            Utils.launchLocation(requireContext())
        }

        navigation.navLocate.setOnClickListener {
            closeDrawer()
            Utils.launchLocation(requireContext())
        }

        navigation.navContact.setOnClickListener {
            closeDrawer()
            Utils.launchContact(requireContext())
        }

        binding.btNavDrawer.setOnClickListener {
            binding.drawerLayout.open()
        }

        getUserName()
        getCategories()
        getSliders()
        getCars()
    }



private fun openAppUrl() {
    val appPackageName = requireContext().packageName
    try {
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName")
            ))
    } catch (e: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            ))
    }
}


    private fun getUserName() {
       val userId = auth.currentUser!!.uid
        dbFireStore.collection(Utils.FS_USERS).document(userId).get()
            .addOnSuccessListener { user ->
                val userName = user.getString("firstName")
                val userNameText = "Hello $userName,"
                binding.tvUserName.text = userNameText

        }

    }

    private fun getCars() {
        val carList = ArrayList<CarModel>()
        val wishlist = ArrayList<String>()
        dbFireStore.collection(Utils.FS_WISHLIST).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                if(it.exists()) {
                    val favList = it.get("favorite") as? ArrayList<String>
                    favList?.let {favListCar ->
                        wishlist.addAll(favListCar)
                    }
                }

            }

        fetchCarList(carList, wishlist)
    }

    private fun fetchCarList(carList: ArrayList<CarModel>, wishlist: ArrayList<String>) {
        dbFireStore.collection(Utils.FS_CAR_LIST).get().addOnSuccessListener {
            carList.clear()
            for (doc in it.documents) {
                val car = doc.toObject<CarModel>()
                carList.add(car!!)
            }

            binding.carsRv.adapter = CarMainAdopter(requireContext(), carList, wishlist)

        }.addOnFailureListener {
            myToast(it.localizedMessage!!)
        }
    }

    private fun getCategories() {
        val categoryList = ArrayList<CategoryModel>()
        dbFireStore.collection(Utils.FS_CATEGORY_COLLECTION).get()
            .addOnSuccessListener {
                categoryList.clear()
                for(doc in it.documents) {
                    val category = doc.toObject<CategoryModel>()
                    categoryList.add(category!!)
                }
                binding.homeLoadingLayout.visibility = View.GONE
                binding.homeLayout.visibility = View.VISIBLE
                binding.categoryRv.adapter = CategoryAdopter(requireContext(), categoryList)

            }
    }

    private fun getSliders() {
        val sliderImg = ArrayList<String>()
        val sliders = ArrayList<CarSliderModel>()
        val imageList = ArrayList<SlideModel>()
        dbFireStore.collection(Utils.FS_SLIDER).get()
            .addOnSuccessListener {
                sliderImg.clear()
                for(doc in it.documents) {
                    val slider = doc.toObject<CarSliderModel>()
                    sliders.add(slider!!)
                    sliderImg.add(slider.sliderImage!!)
                }

                for(sImg in sliderImg) {
                    imageList.add(SlideModel(sImg, ScaleTypes.CENTER_CROP))
                }
                binding.imageSlider.setImageList(imageList)

                binding.imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun onItemSelected(position: Int) {
                        if (sliders[position].sliderUrl!!.isNotEmpty()) {
                            val openURL = Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(sliders[position].sliderUrl.toString())
                            startActivity(openURL)

                        } else if (sliders[position].carId!!.isNotEmpty()) {
                            val intent = Intent(context, DetailsActivity::class.java)
                            intent.putExtra(Utils.CAR_ID, sliders[position].carId.toString())
                            intent.putExtra(Utils.CATEGORY_NAME, sliders[position].categoryName.toString())
                            startActivity(intent)

                        } else if (sliders[position].categoryName!!.isNotEmpty()) {
                            val intent = Intent(context, CategoryActivity::class.java)
                            intent.putExtra(Utils.CATEGORY_NAME, sliders[position].categoryName.toString())
                            startActivity(intent)

                        } else {
                            myToast("Something Went wrong")
                        }

                    }
                    override fun doubleClick(position: Int) {
                        // Do not use onItemSelected if you are using a double click listener at the same time.
                        // Its just added for specific cases.
                        // Listen for clicks under 250 milliseconds.
                    } })
            }

    }

    private fun myToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }



}