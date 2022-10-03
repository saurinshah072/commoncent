package com.commoncents.data.responses

import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

open class CommonResponse(
    @field:SerializedName("status")
    open val status: String = "",
    @field:SerializedName("error")
    open val error: String = "",
    @field:SerializedName("code")
    open val code: Int,
    @field:SerializedName("errors")
    open val errorData: JSONArray
)

