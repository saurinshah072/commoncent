package com.commoncents.ui.retrieving.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("errors")
    val errorItem: List<ErrorItem> = ArrayList()
)

fun ErrorResponse.nullCheck():ErrorResponse{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        errorItem = errorItem.map { it?.nullCheck() }
    )
}

data class ErrorItem(
    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("detail")
    val detail: String? = null,
)

fun ErrorItem.nullCheck():ErrorItem{
    return this.copy(
        source = source ?: "",
        detail = detail ?: ""
    )
}

