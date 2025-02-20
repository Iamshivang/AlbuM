package com.example.album.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.album.R
import com.example.album.databinding.GalleryItemBinding
import com.example.album.model.Hit
import timber.log.Timber

private val TAG= "GalleryAdapter"

class HiltAdapter(): RecyclerView.Adapter<HiltAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener?= null

    private val differCallBack= object: DiffUtil.ItemCallback<Hit>() {
        override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem.id== newItem.id
        }

        override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem== newItem
        }

    }

    // Async list differ - Tool take two list and compare
    val differ= AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item= differ.currentList.get(position)
        holder.bindItem(item)

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

    class ViewHolder(private val itemBinding:GalleryItemBinding): RecyclerView.ViewHolder(itemBinding.root){
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
                        .into(itemBinding.ivGalleryPhoto)

                }catch (e: Exception){

                    Timber.tag(TAG).e("An Error occurred: %s", e)
                }
            }

//            model.title?.let {
//                itemBinding.tvSimilarAppName1.text= it
//            }
//
//            model.rating?.let {
//                itemBinding.tvSimilarAppRating.text= it
//            }
        }
    }
}