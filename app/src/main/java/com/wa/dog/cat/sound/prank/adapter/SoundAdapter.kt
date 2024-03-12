package com.wa.dog.cat.sound.prank.adapter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ItemViewSoundBinding
import com.wa.dog.cat.sound.prank.model.SoundItem
import java.io.IOException


class SoundAdapter(var context: Context, var listSounds:ArrayList<SoundItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onClickItem : (Int, SoundItem) -> Unit = { _, _ -> }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBinding = ItemViewSoundBinding.inflate(LayoutInflater.from(context),parent,false)
        return  ViewHolderSound(viewBinding)

    }

    inner class ViewHolderSound(var viewBinding: ItemViewSoundBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


    override fun getItemCount(): Int {
        return listSounds.size
    }










    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listSounds[position]
        val holderItem = holder as ViewHolderSound
        item.icon?.let { holderItem.viewBinding.icEmoji.setBackgroundResource(it) }
        holderItem.viewBinding.titleEmoji.text = item.title
        // Cập nhật ảnh dựa trên trạng thái hiện tại
//        if (position == currentlyPlayingPosition) {
//            // Đặt ảnh khi đang phát âm thanh
//            holder.viewBinding.icPlay.setImageResource(R.drawable.ic_pause)
//        } else {
//            // Đặt ảnh mặc định khi không phát âm thanh
//            holder.viewBinding.icPlay.setImageResource(R.drawable.ic_play)
//        }
        holderItem.itemView.setOnClickListener {
            onClickItem(position,item)
//            playSound(context, item.sound,position)
        }
    }

}