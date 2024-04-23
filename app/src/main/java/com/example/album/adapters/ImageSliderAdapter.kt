package com.example.album.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.album.R
import com.example.album.databinding.FullScreenItemBinding
import com.example.album.model.Hit
import timber.log.Timber

    private val TAG= "ImageSliderAdapter"

class ImageSliderAdapter(private val items: List<Hit>): RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    private var onMoreClickListener: OnMoreClickListener? = null
    private var onSetAsClickListener: OnSetAsClickListener? = null

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

    interface OnMoreClickListener {
        fun onMoreClick(position: Int, model: Hit)
    }

    interface OnSetAsClickListener {
        fun onSetAsClick(position: Int, model: Hit)
    }

    fun setOnMoreClickListener(listener: OnMoreClickListener) {
        onMoreClickListener = listener
    }

    fun setOnSetAsClickListener(listener: OnSetAsClickListener) {
        onSetAsClickListener = listener
    }

    inner class ViewHolder(private val itemBinding:FullScreenItemBinding): RecyclerView.ViewHolder(itemBinding.root){

        fun bindItem(model: Hit){

            var dummyURL: String?= null

            model.previewURL?.let {
                dummyURL= it
            }

            model.largeImageURL?.let {
                try {

                    Glide
                        .with(itemBinding.root.context)
                        .load(it)
                        .thumbnail(Glide.with(itemBinding.root.context).load(dummyURL))
                        .fitCenter()
//                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(itemBinding.pvFullscreen)

                }catch (e: Exception){

                    Timber.tag(TAG).e("An Error occurred: %s", e)
                }
            }

            model.likes?.let {
                itemBinding.iLike.tvLike.text= it.toString()
            }

            model.downloads?.let {
                itemBinding.iDownloads.tvDownloads.text= it.toString()
            }

            itemBinding.iMore.ivMore.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClickListener?.onMoreClick(position, items[position])
                }
            }

            itemBinding.iSetAs.ivSetAs.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSetAsClickListener?.onSetAsClick(position, items[position])
                }
            }
        }
    }
}