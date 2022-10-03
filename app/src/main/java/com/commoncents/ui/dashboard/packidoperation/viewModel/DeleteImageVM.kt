package com.commoncents.ui.dashboard.packidoperation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.packidoperation.model.DeleteImagReponse
import com.commoncents.ui.dashboard.packidoperation.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONException
import javax.inject.Inject

@HiltViewModel
class DeleteImageVM @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _deleteImageResponse: MutableLiveData<Resource<DeleteImagReponse>> =
        MutableLiveData()
    val deleteImageResponse: LiveData<Resource<DeleteImagReponse>> get() = _deleteImageResponse
    fun deleteImageListing(
        params: RequestBody
    ) = viewModelScope.launch {
        _deleteImageResponse.value = Resource.loading(null)

        if (networkHelper.isNetworkConnected()) {
            try {
                repository.deleteImageData( params).let {
                    if (it.isSuccessful) {
                        _deleteImageResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _deleteImageResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _deleteImageResponse.postValue(
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
                            _deleteImageResponse.postValue(
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
                _deleteImageResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _deleteImageResponse.postValue(
            Resource.noInternet()
        )
    }
}