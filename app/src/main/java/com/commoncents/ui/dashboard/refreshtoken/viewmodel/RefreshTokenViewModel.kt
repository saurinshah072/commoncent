package com.commoncents.ui.dashboard.refreshtoken.viewmodel

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
class RefreshTokenViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _refreshResponse: MutableLiveData<Resource<LoginResponse>> =
        MutableLiveData()

    fun refreshToken(
        refreshToken: String
    ) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()) {
            try {
                repository.refreshToken(refreshToken).let {
                    if (it.isSuccessful) {
                        _refreshResponse.postValue(Resource.success(it.body()?.nullCheck()))
                    } else {
                        val errorResponse = ApiErrorHandler.checkErrorCode(it.errorBody())
                        try {
                            if (it.raw().code == Constants.InternalServerError) {
                                _refreshResponse.postValue(
                                    Resource.error(
                                        it.raw().message,
                                        it.raw().code,
                                        null
                                    )
                                )
                            } else {
                                _refreshResponse.postValue(
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
                            _refreshResponse.postValue(
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
                _refreshResponse.postValue(
                    Resource.error(
                        Constants.MSG_SOMETHING_WENT_WRONG, 0,
                        null
                    )
                )
            }
        } else _refreshResponse.postValue(
            Resource.noInternet()
        )
    }
}