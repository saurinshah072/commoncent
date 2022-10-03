package com.commoncents.ui.dashboard.packidoperation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.packidoperation.model.ListPackIDResponse
import com.commoncents.ui.dashboard.assignbrokenpallet.model.nullCheck
import com.commoncents.ui.dashboard.packidoperation.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import javax.inject.Inject

@HiltViewModel
class PackIDOperationVM  @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _packIDListingResponse: MutableLiveData<Resource<ListPackIDResponse>> =
        MutableLiveData()
    val packIDListingResponse: LiveData<Resource<ListPackIDResponse>> get() = _packIDListingResponse

    fun packIDListing(
        params: MutableMap<String, String>
    ) = viewModelScope.launch {
        _packIDListingResponse.value = Resource.loading(null)

        if (networkHelper.isNetworkConnected()) {
            try {
                repository.packIDOperationtListing(params).let {
                    if (it.isSuccessful) {
                        _packIDListingResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _packIDListingResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _packIDListingResponse.postValue(
                                    errorResponse.let { it1 ->
                                        Resource.error(
                                            it1!!.error,
                                            it1.code,
                                            null
                                        )
                                    }
                                )
                            }
                        } catch (e: Exception) {
                            _packIDListingResponse.postValue(
                                Resource.error(
                                    Constants.MSG_SOMETHING_WENT_WRONG,
                                    0,
                                    null
                                )
                            )
                        }
                    }
                }
            } catch (e: JSONException) {
                _packIDListingResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _packIDListingResponse.postValue(
            Resource.noInternet()
        )
    }
}