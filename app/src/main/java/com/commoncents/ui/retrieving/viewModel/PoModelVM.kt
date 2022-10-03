package com.commoncents.ui.retrieving.viewModel
//
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.retrieving.model.PoModelResponse
import com.commoncents.ui.retrieving.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import javax.inject.Inject
//
@HiltViewModel
class PoModelVM @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel(){
    private val _poModelResponse: MutableLiveData<Resource<PoModelResponse>> = MutableLiveData()
    val poModelResponse: LiveData<Resource<PoModelResponse>> get() = _poModelResponse

    fun getPOData(
        params: MutableMap<String, String>,
        flag: Boolean
    ) = viewModelScope.launch {
        if (flag) {
            _poModelResponse.value = Resource.loading(null)
        }
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.getReceivingPO(params).let {
                    if (it.isSuccessful) {
                        _poModelResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _poModelResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _poModelResponse.postValue(
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
                            _poModelResponse.postValue(
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
                _poModelResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _poModelResponse.postValue(
            Resource.noInternet()
        )
    }
}