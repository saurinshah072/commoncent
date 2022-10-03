package com.commoncents.ui.retrieving.viewModel
//
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AssignLocationRepository
import com.commoncents.data.responses.CommonResponse
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.retrieving.model.ErrorResponse
import com.commoncents.ui.retrieving.model.SearchPackIDResponse
import com.commoncents.ui.retrieving.model.nullCheck
import com.commoncents.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
//

@HiltViewModel
class CheckPackIDVM  @Inject constructor(
    private val repository: AssignLocationRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel(){
    private val _checkPoModelVM: MutableLiveData<Resource<SearchPackIDResponse>> = MutableLiveData()
    val checkPoModelVM: LiveData<Resource<SearchPackIDResponse>> get() = _checkPoModelVM

    fun checkPOModel(
        packID:String,
        flag: Boolean
    )= viewModelScope.launch {

            if (networkHelper.isNetworkConnected()) {
                _checkPoModelVM.value = Resource.loading(null)
                try {
                    repository.checkPackIDAvailable(packID).let {
                        if (it.isSuccessful) {
                            _checkPoModelVM.postValue(Resource.success(it.body()?.nullCheck()))
                        } else {
                            try {
                                if (it.raw().code == Constants.InternalServerError) {
                                    _checkPoModelVM.postValue(
                                        Resource.error(
                                            it.raw().message,
                                            it.raw().code,
                                            null
                                        )
                                    )
                                } else {
                                    //
                                    it!!.errorBody().let {
                                        it!!.charStream().let {
                                            //val jsonData:String = it.readText()
                                            //Log.e("MyTagError",""+jsonData)
                                            val type = object : TypeToken<ErrorResponse>() {}.type
                                            val gsonData  = Gson().fromJson<ErrorResponse>(it.readText(), type)
//                                            val jsonObject = JSONObject(jsonData)
//                                            Log.e("MyTagError",""+jsonObject.toString())
                                            Log.e("myTagError","Code is  "+gsonData.code)
                                            Log.e("myTagError","Code is  "+gsonData.status)
                                            Log.e("myTagError","Code is  "+gsonData.errorItem.get(0))
                                            _checkPoModelVM.postValue(
                                                Resource.errorList(
                                                    gsonData.status!!,
                                                    gsonData.code!!,
                                                    gsonData.errorItem
                                                )
                                                    //Log.e("myTagResponse",it1!!.errorData.toString())

                                                    //Resource.errorList(it.readText())

                                            )

                                        }
                                    }
                                   /// _checkPoModelVM.postValue(Resource.errorList(it.errorBody()?.nullCheck()))
                                    //
                                   // val errorData = ApiErrorHandler.checkMultiErrorCode(it!!.errorBody())
                                   // Log.e("myTag","errorData === "+errorData)
                                    //val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
//                                    _checkPoModelVM.postValue(
//                                        errorResponse.let { it1 ->
//                                            //Log.e("myTagResponse",it1!!.errorData.toString())
//                                            Resource.error(
//                                                it1!!.error,
//                                                it1.code,
//                                               null
//                                            )
//                                        }
//                                    )
                                }
                            } catch (e: Exception) {
                                _checkPoModelVM.postValue(
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
                    _checkPoModelVM.postValue(
                        Resource.error(
                            Constants.MSG_SOMETHING_WENT_WRONG,
                            0,
                            null
                        )
                    )
                }
            } else _checkPoModelVM.postValue(
                Resource.noInternet()
            )
        }

}