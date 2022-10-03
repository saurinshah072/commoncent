package com.commoncents.data.api

import android.util.Log
import com.commoncents.data.responses.CommonResponse
import com.commoncents.ui.retrieving.model.ErrorResponse
import com.commoncents.utils.Logger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject

object ApiErrorHandler {

    /**
     * Handle api errors
     */

    fun checkErrorCode(
        response: ResponseBody?
    ): CommonResponse? {
        val type = object : TypeToken<CommonResponse>() {}.type
        //Log.e("myTagResponseError", "Test 1 "+response!!.charStream().toString())
        return Gson().fromJson(response!!.charStream(), type)
        //Log.e("myTagResponseError", "Test 2 "+objResponse!!.errorData.getJSONObject(0).get("detail").toString())
        //return objResponse
    }

    fun checkMultiErrorCode(
        response: ResponseBody?
    ): ErrorResponse? {
        val type = object : TypeToken<ErrorResponse>() {}.type
        //Log.e("myTagResponseError", "Test 1 "+response!!.charStream().toString())
        return Gson().fromJson(response!!.charStream(), type)
        //Log.e("myTagResponseError", "Test 2 "+objResponse!!.errorData.getJSONObject(0).get("detail").toString())
        //return objResponse
    }


}
