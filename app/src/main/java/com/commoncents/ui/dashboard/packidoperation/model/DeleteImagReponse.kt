package com.commoncents.ui.dashboard.packidoperation.model

import com.google.gson.annotations.SerializedName

data class DeleteImagReponse(
    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: DeleteImageData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

fun DeleteImagReponse.nullCheck():DeleteImagReponse{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?:  DeleteImageData()
    )
}

data class DeleteImageData(
    @field:SerializedName("message")
    val message: String? = null,
)

fun DeleteImageData.nullCheck():DeleteImageData{
    return this.copy(
        message = message ?: "",
    )
}