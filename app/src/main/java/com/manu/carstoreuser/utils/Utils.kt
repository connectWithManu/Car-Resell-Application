package com.manu.carstoreuser.utils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import com.manu.carstoreuser.R
import com.manu.carstoreuser.databinding.DialogDesignBinding
import com.manu.carstoreuser.databinding.DialogDeveloperBinding
import com.manu.carstoreuser.databinding.DialogLoadingBinding

class Utils {
    companion object {
        const val FS_CATEGORY_COLLECTION = "Categories"
        const val FS_CATEGORY_IMG = "Categories"
        const val FS_CAR_LIST = "CarList"
        const val FS_CAR_COVER_IMG = "CoverImg"
        const val FS_CARS_IMG = "CarImg"
        const val FS_SLIDER_IMG = "Slider"
        const val FS_SLIDER = "Slider"
        const val CATEGORY_NAME = "categoryName"
        const val CAR_ID = "categoryId"
        const val FS_USERS = "users"
        const val FS_WISHLIST = "WishList"
        const val FS_NOTIFICATION_COLLECTION = "Notification"
        const val APP_SHARE = "This car at great Price\nDownload the Car store app on PlayStore\nhttps://play.google.com/store/apps/details?id=com.manu.appname"


        fun shareText(context: Context, text: String) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Car Store")
            context.startActivity(Intent.createChooser(shareIntent, "Share Text"))
        }

        fun launchLocation(context: Context) {

            val latitude = 13.332611111111111
            val longitude = 75.77641666666666
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude"));
            context.startActivity(mapIntent)

        }

        fun launchContact(context: Context) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:7204774056")
            context.startActivity(intent)
        }

        fun contactDeveloper(context: Context) {
            val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val dialogLayout = DialogDeveloperBinding.inflate(LayoutInflater.from(context))
            dialog.setView(dialogLayout.root)
            dialog.setCancelable(true)

            dialogLayout.btCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogLayout.btWhatsApp.setOnClickListener {
                openWhatsAppChat(context, "7204774056")
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun openWhatsAppChat(context: Context, phoneNumber: String) {
            val number = if (phoneNumber.startsWith("+")) phoneNumber else "+$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$number")

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {

                Toast.makeText(context, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show()
            }
        }


        fun loading(context: Context) {
            val progress = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val processLayout = DialogLoadingBinding.inflate(LayoutInflater.from(context))
            progress.setView(processLayout.root)
            progress.setCancelable(false)
            progress.show()
        }

        fun showDialog(
            context: Context, title: String, description: String,
            confirmButtonText: String, confirmButtonAction: () -> Unit
        ) {
            val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val dialogLayout = DialogDesignBinding.inflate(LayoutInflater.from(context))
            dialog.setView(dialogLayout.root)
            dialog.setCancelable(true)

            dialogLayout.dialogBody.text = description
            dialogLayout.dialogTitle.text = title
            dialogLayout.btConfirm.text = confirmButtonText

            dialogLayout.btCancle.setOnClickListener {
                dialog.dismiss()
            }
            dialogLayout.btConfirm.setOnClickListener {
                confirmButtonAction()
                dialog.dismiss()
            }

            dialog.show()
        }

    }



}