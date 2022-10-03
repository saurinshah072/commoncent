package com.commoncents.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commoncents.data.api.ApiErrorHandler
import com.commoncents.data.repository.AuthRepository
import com.commoncents.network.NetworkHelper
import com.commoncents.network.Resource
import com.commoncents.ui.auth.model.LoginResponse
import com.commoncents.ui.auth.model.nullCheck
import com.commoncents.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    fun login(
        userName: String,
        password: String,
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.loading(null)
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.login(userName, password).let {
                    if (it.isSuccessful) {
                        _loginResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _loginResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                                _loginResponse.postValue(
                                    errorResponse.let { it1 ->
                                        Resource.error(
                                            it1!!.error,
                                            it1.code,
                                            null
                                        )
                                    }
                                )
                            }
                        } catch (e: JSONException) {
                            _loginResponse.postValue(
                                Resource.error(
                                    Constants.MSG_SOMETHING_WENT_WRONG, 0,
                                    null
                                )
                            )
                        }
                    }
                }
            } catch (e: JSONException) {
                _loginResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG, 0,
                        null
                    )
                )
            }
        } else _loginResponse.postValue(
            Resource.noInternet()
        )
    }
}
