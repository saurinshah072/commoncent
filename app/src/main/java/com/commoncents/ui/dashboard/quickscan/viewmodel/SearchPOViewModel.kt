package com.commoncents.ui.dashboard.quickscan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.assignbrokenpallet.model.QuickScanDetails
import com.commoncents.ui.dashboard.assignbrokenpallet.model.nullCheck
import com.commoncents.ui.dashboard.quickscan.model.SearchPOModel
import com.commoncents.ui.dashboard.quickscan.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import javax.inject.Inject


@HiltViewModel
class SearchPOViewModel @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _searchPOResponse: MutableLiveData<Resource<SearchPOModel>> = MutableLiveData()
    val searchPOResponse: LiveData<Resource<SearchPOModel>> get() = _searchPOResponse


    private val _packIDResponse: MutableLiveData<Resource<QuickScanDetails>> = MutableLiveData()
    val packIDResponse: LiveData<Resource<QuickScanDetails>> get() = _packIDResponse


    fun searchPO(
        params: MutableMap<String, String>,
        flag: Boolean
    ) = viewModelScope.launch {
        if (flag) {
            _searchPOResponse.value = Resource.loading(null)
        }
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.searchPO(params).let {
                    if (it.isSuccessful) {
                        _searchPOResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _searchPOResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _searchPOResponse.postValue(
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
                            _searchPOResponse.postValue(
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
                _searchPOResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _searchPOResponse.postValue(
            Resource.noInternet()
        )
    }

    fun printPackID(
        apiName: String,
        params: MutableMap<String, String>
    ) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.printPackID(apiName, params).let {
                    if (it.isSuccessful) {
                        _packIDResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _packIDResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _packIDResponse.postValue(
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
                            _packIDResponse.postValue(
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
                _packIDResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _packIDResponse.postValue(
            Resource.noInternet()
        )
    }

}
