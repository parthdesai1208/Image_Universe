package com.parthdesai1208.imageuniverse.view.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.parthdesai1208.imageuniverse.BR
import com.parthdesai1208.imageuniverse.R
import com.parthdesai1208.imageuniverse.databinding.ItemHomeRecyclerviewBinding
import com.parthdesai1208.imageuniverse.model.ImageResponse
import com.parthdesai1208.obvious.utils.circularProgressDrawable
import com.parthdesai1208.obvious.utils.isInternetAvailable

class HomeAdapter(private val context: Context, private val root: ConstraintLayout) :
    PagedListAdapter<ImageResponse, HomeAdapter.HomeViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageResponse>() {

            override fun areItemsTheSame(oldItem: ImageResponse, newItem: ImageResponse): Boolean {
                return oldItem.title == newItem.title

            }

            override fun areContentsTheSame(
                oldItem: ImageResponse,
                newItem: ImageResponse
            ): Boolean {
                return oldItem == newItem
            }
        }

    }

    private var list: ArrayList<ImageResponse>? = null
    private var snackBar: Snackbar? = null
    private var clickListener: ImageClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = DataBindingUtil.inflate<ItemHomeRecyclerviewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_recyclerview,
            parent,
            false
        )
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val model: ImageResponse = this.getItem(position)!!
        holder.bind(model, position, holder)
        holder.itemHomeRecyclerViewBinding?.click = this
        loadImage(model, holder)

        holder.imgView?.setOnClickListener {
            clickListener?.onImageSelected(position, holder.imgView!!)
        }
    }

    private fun loadImage(model: ImageResponse, holder: HomeViewHolder) {

        Glide.with(context)
            .load(model.url)
            .apply(
                circularProgressDrawable(
                    context
                )
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!context.isInternetAvailable()) {
                        holder.imgView?.visibility = View.VISIBLE
                        holder.imgView?.setImageResource(R.drawable.ic_nointernet)
                    } else {
                        holder.btnRetry?.visibility = View.VISIBLE
                        holder.imgView?.visibility = View.INVISIBLE
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.btnRetry?.visibility = View.GONE
                    holder.imgView?.visibility = View.VISIBLE
                    holder.imgView?.setImageDrawable(resource)
                    return true
                }

            })
            .into(holder.imgView!!)
    }

    fun passDataForDetailScreen(list: ArrayList<ImageResponse>) {
        this.list = ArrayList()
        this.list = list
    }

    inner class HomeViewHolder(private val binding: ItemHomeRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var itemHomeRecyclerViewBinding: ItemHomeRecyclerviewBinding? = null
        var imgView: ImageView? = null
        var btnRetry: Button? = null

        init {
            super.itemView.rootView
            itemHomeRecyclerViewBinding = binding
            imgView = itemView.findViewById(R.id.img_home)
            btnRetry = itemView.findViewById(R.id.btnRetryHome)
        }

        fun bind(
            model: ImageResponse
            , position: Int, holder: HomeViewHolder
        ) {
            binding.setVariable(BR.model, model)
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.holder, holder)
            binding.executePendingBindings()
        }
    }

    fun onRetryClick(position: Int, holder: HomeViewHolder) {
        if (!context.isInternetAvailable()) {
            snackBar = Snackbar.make(root, "No Internet!!", Snackbar.LENGTH_SHORT)
            snackBar!!.show()
            return
        }
        holder.imgView?.visibility = View.VISIBLE
        holder.btnRetry?.visibility = View.GONE
        loadImage(this.getItem(position)!!, holder)
    }

    fun setClickListener(clickListener: ImageClick) {
        this.clickListener = clickListener
    }

    interface ImageClick {
        fun onImageSelected(position: Int, imageView: ImageView)
    }
}