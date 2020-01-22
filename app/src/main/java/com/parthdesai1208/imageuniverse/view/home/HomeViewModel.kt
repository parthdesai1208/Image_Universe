package com.parthdesai1208.imageuniverse.view.home

import android.view.View
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.parthdesai1208.imageuniverse.model.ImageResponse
import com.parthdesai1208.imageuniverse.model.repo.HomeRepository
import com.parthdesai1208.imageuniverse.model.repo.RepoResponse

class HomeViewModel(
    private val homeRepo: HomeRepository,
    private val lifeCycleOwner: LifecycleOwner
) : ViewModel() {

    //region progress bar
    val progressBarVisibility: LiveData<Int> //expose it to UI
        get() = _progressBarVisibility

    private val _progressBarVisibility = MutableLiveData<Int>()

    fun setProgressBarVisibility(visibility: Int) {
        _progressBarVisibility.value = visibility
    }
    //endregion

    //region data operation
    private val _iWantData = MutableLiveData<Boolean>()

    fun iWantData(b: Boolean) {
        _iWantData.value = b
    }

    private val repoResult: LiveData<RepoResponse> = Transformations.map(_iWantData) {
        homeRepo.getImageUsingPaging()
    }

    val observeImageData: LiveData<PagedList<ImageResponse>> =
        Transformations.switchMap(repoResult) {
            it.list
        }

    val observeError: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors
    }

    val observeImageList: LiveData<ArrayList<ImageResponse>> =
        Transformations.switchMap(repoResult) {
            it.imageList
        }
    //endregion

    //region recyclerView visibility
    val recyclerViewVisibility: LiveData<Int>
        get() = _recyclerViewVisibility

    private val _recyclerViewVisibility = MutableLiveData<Int>(View.GONE)
    //endregion

    //region display error text
    val errorTextVisibility: LiveData<Int>
        get() = _errorTextVisibility

    private val _errorTextVisibility = MutableLiveData<Int>(View.GONE)

    val errorText: LiveData<String>
        get() = _errorText

    private val _errorText = MutableLiveData<String>()

    fun setErrorText(s: String) {
        if (s.isEmpty()) {
            _errorTextVisibility.value = View.GONE
            _recyclerViewVisibility.value = View.VISIBLE
        } else {
            _errorText.value = s
            _errorTextVisibility.value = View.VISIBLE
            _recyclerViewVisibility.value = View.GONE
        }
    }
    //endregion

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    override fun onCleared() {
        super.onCleared()
        if (observeImageData.hasObservers()) {
            observeImageData.removeObservers(lifeCycleOwner)
        }
        if (observeError.hasObservers()) {
            observeError.removeObservers(lifeCycleOwner)
        }
        _progressBarVisibility.value = null
        _iWantData.value = null
    }
}