package com.commoncents.ui.dashboard.assignbrokenpallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.assignbrokenpallet.model.*
import com.commoncents.utils.Constants
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import javax.inject.Inject


@HiltViewModel
class PrintPackIDViewModel @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _packIDResponse: MutableLiveData<Resource<QuickScanDetails>> = MutableLiveData()
    val packIDResponse: LiveData<Resource<QuickScanDetails>> get() = _packIDResponse

    private val _validateLocationResponse: MutableLiveData<Resource<ValidateLocationResponse>> = MutableLiveData()
    val validateLocationResponse: LiveData<Resource<ValidateLocationResponse>> get() = _validateLocationResponse

    private val _packIDListingResponse: MutableLiveData<Resource<ListPackIDResponse>> =
        MutableLiveData()
    val packIDListingResponse: LiveData<Resource<ListPackIDResponse>> get() = _packIDListingResponse


    private val _imageListingResponse: MutableLiveData<Resource<BrokenImageResponse>> =
        MutableLiveData()
    val imageListingResponse: LiveData<Resource<BrokenImageResponse>> get() = _imageListingResponse


    fun getBrokenPalletImages(
        quickScanID:String ) = viewModelScope.launch {
        _imageListingResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.getBrokenPalletImages(quickScanID).let {
                    if (it.isSuccessful) {
                        _imageListingResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _imageListingResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _imageListingResponse.postValue(
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
                            _imageListingResponse.postValue(
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
                _imageListingResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _imageListingResponse.postValue(
            Resource.noInternet()
        )
    }


    fun deletePackID(
        packID: String
    ) = viewModelScope.launch {
        _packIDResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.deleteScanPackID(packID).let {
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

    fun assignLocation(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _packIDResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.assignLocation(jsonObject).let {
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
    fun packIDListing(
        params: MutableMap<String, String>,
        flag:Boolean
    ) = viewModelScope.launch {
        if(flag) {
            _packIDListingResponse.value = Resource.loading(null)
        }
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.packIDListing(params).let {
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
    fun uploadBrokenImages(
        pack_id: RequestBody,
        location_id: RequestBody,
        is_damaged: RequestBody,
        multipartImageList: Array<MultipartBody.Part?>,
    ) = viewModelScope.launch {
        _packIDResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.uploadBrokenImages(pack_id, location_id, is_damaged, multipartImageList)
                    .let {
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
                                    val errorResponse =
                                        ApiErrorHandler.checkErrorCode(it.errorBody())
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
    fun validateLocation(
        locationID: String,
    ) = viewModelScope.launch {
        _validateLocationResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.validateLocation(locationID).let {
                    if (it.isSuccessful) {
                        _validateLocationResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _validateLocationResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _validateLocationResponse.postValue(
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
                            _validateLocationResponse.postValue(
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
                _validateLocationResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _validateLocationResponse.postValue(
            Resource.noInternet()
        )
    }
}
