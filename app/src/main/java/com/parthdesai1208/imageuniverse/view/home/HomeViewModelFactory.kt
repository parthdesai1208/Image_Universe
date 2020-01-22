package com.parthdesai1208.obvious.view.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parthdesai1208.imageuniverse.view.home.HomeViewModel
import com.parthdesai1208.imageuniverse.model.repo.HomeRepository

class HomeViewModelFactory(private val homeRepo : HomeRepository, private val lifeCycleOwner : LifecycleOwner):
ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(
            homeRepo,
            lifeCycleOwner
        ) as T
    }
}