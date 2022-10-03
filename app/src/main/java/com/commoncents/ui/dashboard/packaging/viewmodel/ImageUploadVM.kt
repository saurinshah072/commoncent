package com.commoncents.ui.dashboard.packaging.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.packaging.model.SingleImageUploadModel
import com.commoncents.ui.dashboard.packaging.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import javax.inject.Inject

@HiltViewModel
class ImageUploadVM @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _singleImageModelResponse: MutableLiveData<Resource<SingleImageUploadModel>> =
        MutableLiveData()
    val singleImageModelResponse: LiveData<Resource<SingleImageUploadModel>> get() = _singleImageModelResponse

    fun uploadSingleImage(
        pack_id: RequestBody,
        multipartImageList: Array<MultipartBody.Part?>,
    ) = viewModelScope.launch {
        _singleImageModelResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.uploadSingleImage(pack_id, multipartImageList)
                    .let {
                        if (it.isSuccessful) {
                            _singleImageModelResponse.postValue(Resource.success(it.body()?.nullCheck()))
                        } else {
                            try {
                                if (it.raw().code == Constants.InternalServerError) {
                                    _singleImageModelResponse.postValue(
                                        Resource.error(
                                            it.raw().message,
                                            it.raw().code,
                                            null
                                        )
                                    )
                                } else {
                                    val errorResponse =
                                        ApiErrorHandler.checkErrorCode(it.errorBody())
                                    _singleImageModelResponse.postValue(
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
                                _singleImageModelResponse.postValue(
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
                _singleImageModelResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _singleImageModelResponse.postValue(
            Resource.noInternet()
        )
    }
}