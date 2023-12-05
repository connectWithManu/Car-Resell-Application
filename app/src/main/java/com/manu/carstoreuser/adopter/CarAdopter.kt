package com.manu.carstoreuser.adopter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity.DetailsActivity
import com.manu.carstoreuser.databinding.ItemCarBinding
import com.manu.carstoreuser.model.CarModel
import com.manu.carstoreuser.utils.Utils

class CarAdopter(
    val context: Context, private val carList: ArrayList<CarModel>,
    private val favoriteList: ArrayList<String>,

):
RecyclerView.Adapter<CarAdopter.CarVH>(){

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    inner class CarVH(val binding: ItemCarBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarVH {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(context), parent, false)
        return CarVH(binding)
    }

    override fun getItemCount(): Int {
        return carList.size
    }



    override fun onBindViewHolder(holder: CarVH, position: Int) {
        holder.binding.ivCarImg.load(carList[position].coverImg){
            placeholder(R.drawable.placeholder)
        }

        if(favoriteList.contains(carList[position].carId)) {
            holder.binding.btFavorite.setImageResource(R.drawable.ic_fav_filled_red)
        } else {
            holder.binding.btFavorite.setImageResource(R.drawable.ic_fav_outline)
        }

        holder.binding.btFavorite.setOnClickListener {
            if(favoriteList.contains(carList[position].carId)) {
                favoriteList.remove(carList[position].carId!!)
                removeFavorite(favoriteList)
                notifyItemRemoved(favoriteList.size)
                holder.binding.btFavorite.setImageResource(R.drawable.ic_fav_outline)
            } else {
                favoriteList.add(carList[position].carId!!)
                addFavorite(favoriteList)
                notifyItemInserted(favoriteList.size)
                holder.binding.btFavorite.setImageResource(R.drawable.ic_fav_filled_red)
            }


        }

        val title = "${carList[position].year} ${carList[position].title}"
        holder.binding.tvTitle.text = title
        val subTitle = "${carList[position].runKm} km • ${carList[position].engineType} • ${carList[position].gearType}"
        holder.binding.tvSubDetails.text = subTitle
        holder.binding.tvLocation.text = carList[position].location
        if(carList[position].tag.isNullOrEmpty()) {
            holder.binding.tvTag.visibility = View.GONE
        } else {
            holder.binding.tvTag.text = carList[position].tag

        }
        holder.binding.tvPrice.text = carList[position].price

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(Utils.CAR_ID, carList[position].carId)
            intent.putExtra(Utils.CATEGORY_NAME, carList[position].selectCategory)
            context.startActivity(intent)
        }
    }

    fun updateData(newData: List<CarModel>, newFavList: List<String>) {
        carList.clear()
        carList.addAll(newData)
        favoriteList.clear()
        favoriteList.addAll(newFavList)
        notifyDataSetChanged()
    }

    private fun addFavorite(favoriteList: ArrayList<String>) {
        db.collection(Utils.FS_WISHLIST).document(auth.currentUser!!.uid).set(mapOf("favorite" to favoriteList))

    }


    private fun removeFavorite(favoriteList: ArrayList<String>) {
        db.collection(Utils.FS_WISHLIST).document(auth.currentUser!!.uid).set(mapOf("favorite" to favoriteList))

    }

}