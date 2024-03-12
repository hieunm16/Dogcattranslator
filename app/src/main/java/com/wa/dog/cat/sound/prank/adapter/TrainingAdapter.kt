package com.wa.dog.cat.sound.prank.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wa.dog.cat.sound.prank.databinding.ItemViewTrainingBinding
import com.wa.dog.cat.sound.prank.model.TrainingItem

class TrainingAdapter(var context: Context, var listTraining:ArrayList<TrainingItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onClickItem : (Int, TrainingItem) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding = ItemViewTrainingBinding.inflate(LayoutInflater.from(context),parent,false)
        return  ViewHolderTraining(viewBinding)
    }

    inner class ViewHolderTraining(var viewBinding: ItemViewTrainingBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun getItemCount(): Int {
        return listTraining.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listTraining[position]
        val holderItem = holder as TrainingAdapter.ViewHolderTraining
        item.icon?.let { holderItem.viewBinding.imgTraining.setImageResource(it) }
        holderItem.viewBinding.titleTraining.text = item.title
        holderItem.itemView.setOnClickListener {
            onClickItem(position, item)
        }
    }

}