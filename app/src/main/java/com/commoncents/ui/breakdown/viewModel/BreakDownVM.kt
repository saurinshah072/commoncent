package com.commoncents.ui.breakdown.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.breakdown.model.BreakDownModel
import com.commoncents.ui.breakdown.model.nullCheck
import com.commoncents.ui.retrieving.model.ErrorResponse
import com.commoncents.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONException
import javax.inject.Inject

@HiltViewModel
class BreakDownVM  @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _breakDownVMResponse: MutableLiveData<Resource<BreakDownModel>> =
        MutableLiveData()
    val breakDownVMResponse: LiveData<Resource<BreakDownModel>> get() = _breakDownVMResponse

    fun setBreakDownData(
        params: RequestBody
    ) = viewModelScope.launch {
        _breakDownVMResponse.value = Resource.loading(null)

        if (networkHelper.isNetworkConnected()) {
            try {
                repository.breakDownData(params).let {
                    if (it.isSuccessful) {
                        _breakDownVMResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _breakDownVMResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                it!!.errorBody().let {
                                    it!!.charStream().let {
                                        val type = object : TypeToken<ErrorResponse>() {}.type
                                        val gsonData  = Gson().fromJson<ErrorResponse>(it.readText(), type)
                                        _breakDownVMResponse.postValue(
                                            Resource.errorList(
                                                gsonData.status!!,
                                                gsonData.code!!,
                                                gsonData.errorItem
                                            )
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            _breakDownVMResponse.postValue(
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
                _breakDownVMResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _breakDownVMResponse.postValue(
            Resource.noInternet()
        )
    }
}