package com.manu.carstoreuser.adopter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.manu.carstoreuser.R
import com.manu.carstoreuser.databinding.ItemCarImagesBinding

class CarImageAdapter(
    val context: Context,
    private val imgList: ArrayList<String>,
    private val itemClickListener: OnItemClickListener
):
RecyclerView.Adapter<CarImageAdapter.ImageVH>(){
    inner class ImageVH(val binding: ItemCarImagesBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val binding = ItemCarImagesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageVH(binding)
    }

    override fun getItemCount(): Int {
        return imgList.size
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        holder.binding.ivCarImage.load(imgList[position]){
            placeholder(R.drawable.placeholder)
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(imgList[position])
        }

    }

    interface OnItemClickListener {
        fun onItemClick(img: String)
    }
}