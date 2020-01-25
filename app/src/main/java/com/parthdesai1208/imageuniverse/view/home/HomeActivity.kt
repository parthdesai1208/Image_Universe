package com.parthdesai1208.imageuniverse.view.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchUIUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.parthdesai1208.imageuniverse.R
import com.parthdesai1208.imageuniverse.databinding.ActivityHomeBinding

import com.parthdesai1208.imageuniverse.model.ImageResponse
import com.parthdesai1208.imageuniverse.model.repo.HomeRepository
import com.parthdesai1208.imageuniverse.view.customview.ImageOverlay
import com.parthdesai1208.imageuniverse.view.detail.DetailBottomSheet
import com.parthdesai1208.obvious.utils.calculateNoOfColumns
import com.parthdesai1208.obvious.utils.isInternetAvailable
import com.parthdesai1208.obvious.view.home.HomeViewModelFactory
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeAdapter.ImageClick {


    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var mAdapter: HomeAdapter? = null

    private var list: ArrayList<ImageResponse> = ArrayList()

    private var overlayView: ImageOverlay? = null
    private var viewer: StfalconImageViewer<ImageResponse>? = null
    private var builder: StfalconImageViewer.Builder<ImageResponse>? = null
    private var isImageOverlayShown: Boolean = false
    private var isWindowLeak: Boolean = false
    private var currentPositionOnOverlayView: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        viewModel =
            ViewModelProvider(
                this,
                HomeViewModelFactory(
                    HomeRepository((baseContext)),
                    this
                )
            )
                .get(HomeViewModel::class.java)


        binding.lifecycleOwner = this
        binding.model = viewModel
        binding.click = this

        mAdapter =
            HomeAdapter(baseContext, root_home)
        mAdapter!!.setClickListener(this)
        homeRecyclerView.apply {
            setHasFixedSize(true)

            layoutManager = GridLayoutManager(
                context,
                calculateNoOfColumns(
                    context,
                    200F
                )
            )
            adapter = mAdapter
        }

        if (mAdapter!!.itemCount == 0) {
            viewModel.setProgressBarVisibility(View.VISIBLE)
            viewModel.iWantData(true)
        }

        viewModel.observeImageData.observe(this, Observer {
            if (it.size > 0) {
                viewModel.setProgressBarVisibility(View.GONE)
                viewModel.setErrorText("")
                mAdapter!!.submitList(it)
            }
        })

        viewModel.observeError.observe(this, Observer {
            if (it.isNotEmpty()) {
                viewModel.setProgressBarVisibility(View.GONE)
                viewModel.setErrorText(it)
            }
        })

        viewModel.observeImageList.observe(this, Observer {
            if (it.isNotEmpty()) {
                list = it
                mAdapter!!.passDataForDetailScreen(it)
            }
        })

        swipeHome.setOnRefreshListener {
            if (baseContext.isInternetAvailable()) {
                viewModel.refresh()
            }
            swipeHome.isRefreshing = false
        }

    }

    override fun onPause() {
        super.onPause()
        viewer?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onImageSelected(position: Int, imageView: ImageView) {
        if (currentPositionOnOverlayView == -1 && isWindowLeak) {
            currentPositionOnOverlayView = position
            isWindowLeak = false
            return
        }
        currentPositionOnOverlayView = position
        builder =
            StfalconImageViewer.Builder<ImageResponse>(this, list) { imageView1, image ->
                Glide.with(baseContext).load(image.hdurl)
                    .placeholder(R.drawable.ic_place_holder_white_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (resource != null && imageView1 != null) {
                                imageView1.setImageDrawable(resource)
                            }
                            return false
                        }

                    })
                    .into(imageView1!!)

            }.withStartPosition(position)
                .withHiddenStatusBar(false)
                .withTransitionFrom(imageView)
                .allowSwipeToDismiss(true)
                .allowZooming(true)
                .withDismissListener {
                    isImageOverlayShown = false
                    viewer = null
                }
        setupOverlayView(list, position)
        builder?.withOverlayView(overlayView)

        builder?.withImageChangeListener {
            viewer?.updateTransitionImage(
                homeRecyclerView.findViewHolderForAdapterPosition(it)?.itemView?.findViewById(
                    R.id.img_home
                )
            )
            overlayView?.updateView(list[it])
            homeRecyclerView.smoothScrollToPosition(it)
            currentPositionOnOverlayView = it
        }
        isImageOverlayShown = true

        viewer = builder?.show()
    }

    private fun setupOverlayView(list: ArrayList<ImageResponse>, startPosition: Int) {
        overlayView = ImageOverlay(this).apply {
            updateView(list[startPosition])
            onInfoClick = {
                if (list[startPosition].toString().isNotEmpty()) {
                    val fragment =
                        DetailBottomSheet.passModelClass(
                            list[startPosition]
                        )
                    fragment.show(supportFragmentManager, "")
                }
            }
        }
    }

    fun onRetryClick() {
        viewModel.setProgressBarVisibility(View.VISIBLE)
        viewModel.iWantData(true)
    }

}
