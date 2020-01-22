package com.parthdesai1208.imageuniverse.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.parthdesai1208.imageuniverse.R
import com.parthdesai1208.imageuniverse.databinding.FragmentHomeBinding
import com.parthdesai1208.imageuniverse.model.repo.HomeRepository
import com.parthdesai1208.obvious.utils.calculateNoOfColumns
import com.parthdesai1208.obvious.utils.isInternetAvailable
import com.parthdesai1208.obvious.view.home.HomeViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var mAdapter: HomeAdapter? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel =
            ViewModelProviders.of(
                this,
                HomeViewModelFactory(
                    HomeRepository((this.context!!)),
                    viewLifecycleOwner
                )
            )
                .get(HomeViewModel::class.java)

        binding.lifecycleOwner = this
        binding.model = viewModel
        binding.click = this


        mAdapter =
            HomeAdapter(this.context!!, root_home)
        homeRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                context,
                calculateNoOfColumns(
                    context,
                    180F
                )
            )
            adapter = mAdapter
        }
        if (mAdapter!!.itemCount == 0) {
            viewModel.setProgressBarVisibility(View.VISIBLE)
            viewModel.iWantData(true)
        }

        viewModel.observeImageData.observe(viewLifecycleOwner, Observer {
            if (it.size > 0) {
                viewModel.setProgressBarVisibility(View.GONE)
                viewModel.setErrorText("")
                mAdapter!!.submitList(it)
            }
        })

        viewModel.observeError.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                viewModel.setProgressBarVisibility(View.GONE)
                viewModel.setErrorText(it)
            }
        })

        viewModel.observeImageList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                mAdapter!!.passDataForDetailScreen(it)
            }
        })

        swipeHome.setOnRefreshListener {
            if (context?.isInternetAvailable()!!) {
                viewModel.refresh()
            }
            swipeHome.isRefreshing = false
        }

    }

    fun onRetryClick() {
        viewModel.setProgressBarVisibility(View.VISIBLE)
        viewModel.iWantData(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}