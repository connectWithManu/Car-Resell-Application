package com.manu.carstoreuser.adopter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity.CategoryActivity
import com.manu.carstoreuser.activity.DetailsActivity
import com.manu.carstoreuser.databinding.ItemNotificationBinding
import com.manu.carstoreuser.model.NotificationModel
import com.manu.carstoreuser.utils.Utils

class NotificationAdopter(
    val context: Context,
    private val notificationList: ArrayList<NotificationModel>, ) :
    RecyclerView.Adapter<NotificationAdopter.NotificationVH>() {

    inner class NotificationVH(val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationVH(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }



    override fun onBindViewHolder(holder: NotificationVH, position: Int) {
        val notification = notificationList[position]
        holder.binding.ivNotification.load(notification.notificationImg){
            placeholder(R.drawable.placeholder)
        }
        holder.binding.tvNotificationDescription.text = notification.notificationDescription
        holder.binding.tvNotificationHeader.text = notification.notificationTitle

        holder.itemView.setOnClickListener {
            if(notification.notificationUrl.toString().isNotEmpty()){
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(notification.notificationUrl.toString())
                context.startActivity(openURL)
            }else if(notification.notificationCar.toString().isNotEmpty()) {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(Utils.CAR_ID, notification.notificationCar.toString())
                intent.putExtra(Utils.CATEGORY_NAME, notification.notificationCategory.toString())
                context.startActivity(intent)
            } else if(notification.notificationCategory.toString().isNotEmpty()) {
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra(Utils.CATEGORY_NAME, notification.notificationCategory.toString())
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}