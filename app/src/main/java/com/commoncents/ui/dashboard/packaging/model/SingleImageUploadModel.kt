package com.commoncents.ui.dashboard.packaging.model

import com.google.gson.annotations.SerializedName

data class SingleImageUploadModel(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("data")
    val data: SingleImageData? = null,
)

fun SingleImageUploadModel.nullCheck():SingleImageUploadModel{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: SingleImageData()
    )
}

data class SingleImageData(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("id")
    val quick_scan: Int? = null
)

fun SingleImageData.nullCheck():SingleImageData{
    return this.copy(
        id = id ?: 0,
        image = image ?: "",
        quick_scan = quick_scan ?: 0
    )
}