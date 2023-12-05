package com.manu.carstoreuser.adopter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.manu.carstoreuser.R
import com.manu.carstoreuser.activity.CategoryActivity
import com.manu.carstoreuser.databinding.ItemCategoryBinding
import com.manu.carstoreuser.model.CategoryModel
import com.manu.carstoreuser.utils.Utils


class CategoryAdopter(val context: Context, private val categoryList: ArrayList<CategoryModel>):
RecyclerView.Adapter<CategoryAdopter.CategoryVH>(){
    inner class CategoryVH(val binding: ItemCategoryBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryVH(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.binding.ivCategoryImg.load(categoryList[position].imgUrl){
            placeholder(R.drawable.placeholder)
        }
        holder.binding.tvCategroyTitle.text = categoryList[position].categoryName
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra(Utils.CATEGORY_NAME, categoryList[position].categoryName)
            context.startActivity(intent)
        }
    }
}