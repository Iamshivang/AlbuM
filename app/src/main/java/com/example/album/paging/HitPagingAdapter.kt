package com.example.album.paging

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.album.databinding.GalleryItemBinding
import com.example.album.model.Hit
import timber.log.Timber

private val TAG= "HitPagingAdapter"

class HitPagingAdapter: PagingDataAdapter<Hit, HitPagingAdapter.ViewHolder>(COMPARATOR) {

    private var onClickListener: OnClickListener?= null

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
            GalleryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item= getItem(position)
        holder.bindItem(item!!)

        holder.itemView.setOnClickListener{
            if(onClickListener!= null)
            {
                onClickListener!!.onCLick(position, item)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onCLick(position: Int, model: Hit)
    }

    class ViewHolder(private val itemBinding: GalleryItemBinding): RecyclerView.ViewHolder(itemBinding.root){

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
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                itemBinding.ivGalleryPhoto.isGone
                                itemBinding.sflPlaceholder.isVisible
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                itemBinding.ivGalleryPhoto.visibility= View.VISIBLE
                                itemBinding.sflPlaceholder.visibility= View.GONE
                                return false
                            }
                        }).into(itemBinding.ivGalleryPhoto)

                }catch (e: Exception){

                    Timber.tag(TAG).e("An Error occurred: %s", e)
                }
            }

        }
    }
}