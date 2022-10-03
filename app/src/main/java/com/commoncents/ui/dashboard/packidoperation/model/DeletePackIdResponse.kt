package com.commoncents.ui.dashboard.packidoperation.model

import com.google.gson.annotations.SerializedName

data class DeletePackIdResponse(
    @field:SerializedName("code")
    val code: Int? = null,


    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("data")
    val data: DeletePackIdData? = null,

    )

fun DeletePackIdResponse.nullCheck():DeletePackIdResponse{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?:  DeletePackIdData()
    )
}

data class DeletePackIdData(
    @field:SerializedName("message")
    val message: String? = null
)

fun DeletePackIdData.nullCheck():DeletePackIdData{
    return this.copy(
        message = message ?: ""
    )
}