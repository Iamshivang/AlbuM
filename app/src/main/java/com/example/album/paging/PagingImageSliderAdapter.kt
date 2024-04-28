package com.example.album.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.album.R
import com.example.album.databinding.FullScreenItemBinding
import com.example.album.model.Hit
import timber.log.Timber

private val TAG= "PagingImageSliderAdapter"

class PagingImageSliderAdapter: PagingDataAdapter<Hit, PagingImageSliderAdapter.ViewHolder>(COMPARATOR)  {

    private var onMoreClickListener: OnMoreClickListener? = null
    private var onSetAsClickListener: OnSetAsClickListener? = null

    companion object{

        private val COMPARATOR= object: DiffUtil.ItemCallback<Hit>() {
            override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return oldItem.id== newItem.id
            }

            override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return oldItem== newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FullScreenItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item= getItem(position)
        holder.bindItem(item!!)

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

    inner class ViewHolder(private val itemBinding: FullScreenItemBinding): RecyclerView.ViewHolder(itemBinding.root){

        fun bindItem(model: Hit){

            var dummyURL: String?= null

            model.previewURL?.let {
                dummyURL= it
            }

            model.largeImageURL?.let {
                try {

                    Glide.with(itemBinding.root.context)
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
                    onMoreClickListener?.onMoreClick(position, getItem(position)!!)
                }
            }

            itemBinding.iSetAs.ivSetAs.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSetAsClickListener?.onSetAsClick(position, getItem(position)!!)
                }
            }
        }
    }
}