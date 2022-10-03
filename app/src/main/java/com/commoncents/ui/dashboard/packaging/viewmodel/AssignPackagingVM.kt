package com.commoncents.ui.dashboard.packaging.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.dashboard.packaging.model.AssignPackagingModel
import com.commoncents.ui.dashboard.packaging.model.nullCheck
import com.commoncents.ui.retrieving.model.ErrorResponse
import com.commoncents.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONException
import javax.inject.Inject

@HiltViewModel
class AssignPackagingVM  @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _assignPackagingModelResponse: MutableLiveData<Resource<AssignPackagingModel>> =
        MutableLiveData()
    val assignPackagingModelResponse: LiveData<Resource<AssignPackagingModel>> get() = _assignPackagingModelResponse
    fun assignPackagingData(
        params:RequestBody
    ) = viewModelScope.launch {
        _assignPackagingModelResponse.value = Resource.loading(null)

        if (networkHelper.isNetworkConnected()) {
            try {
                repository.updateAssignLocation(params).let {
                    Log.e("myTag","Error Message "+it.toString())
                    if (it.isSuccessful) {
                        _assignPackagingModelResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _assignPackagingModelResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                it!!.errorBody().let {
                                    it!!.charStream().let {
                                        //val jsonData:String = it.readText()
                                        //Log.e("MyTagError",""+jsonData)
                                        val type = object : TypeToken<ErrorResponse>() {}.type
                                        val gsonData  = Gson().fromJson<ErrorResponse>(it.readText(), type)
//                                            val jsonObject = JSONObject(jsonData)
//                                            Log.e("MyTagError",""+jsonObject.toString())
//                                        Log.e("myTagError","Code is  "+gsonData.code)
//                                        Log.e("myTagError","Code is  "+gsonData.status)
//                                        Log.e("myTagError","Code is  "+gsonData.errorItem.get(0))
                                        _assignPackagingModelResponse.postValue(
                                            Resource.errorList(
                                                gsonData.status!!,
                                                gsonData.code!!,
                                                gsonData.errorItem
                                            )
                                        )
//                                            //Log.e("myTagResponse",it1!!.errorData.toString())
//
//                                            //Resource.errorList(it.readText())
//
//                                        )

                                    }
                                }
//                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
//                                Log.e("myTag"," === "+errorResponse.toString())
//                                _assignPackagingModelResponse.postValue(
//                                    errorResponse.let { it1 ->
//                                        Resource.error(
//                                            it1!!.error,
//                                            it1.code,
//                                            null
//                                        )
//                                    }
//                                )
                            }
                        } catch (e: Exception) {
                            _assignPackagingModelResponse.postValue(
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
                _assignPackagingModelResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG,
                        0,
                        null
                    )
                )
            }
        } else _assignPackagingModelResponse.postValue(
            Resource.noInternet()
        )
    }
}