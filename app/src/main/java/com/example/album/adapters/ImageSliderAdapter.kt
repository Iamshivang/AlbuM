package com.example.album.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.album.R
import com.example.album.databinding.FullScreenItemBinding
import com.example.album.model.Hit

class ImageSliderAdapter(private val items: List<Hit>): RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FullScreenItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item= items.get(position)
        holder.bindItem(item)

    }

    inner class ViewHolder(private val itemBinding:FullScreenItemBinding): RecyclerView.ViewHolder(itemBinding.root){

        fun bindItem(model: Hit){

            model.previewURL?.let {
                try {
                    Glide
                        .with(itemBinding.root.context)
                        .load(it)
                        .fitCenter()
                        .placeholder(R.drawable.flag)
                        .into(itemBinding.pvFullscreen)

                }catch (e: Exception){

//                    Timber.tag(TAG).e("An Error occurred: %s", e)
                }
            }
        }
    }
}