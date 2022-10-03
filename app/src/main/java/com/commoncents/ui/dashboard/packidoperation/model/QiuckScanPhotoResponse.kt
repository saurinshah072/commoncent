package com.commoncents.ui.dashboard.packidoperation.model

import com.google.gson.annotations.SerializedName


data class QiuckScanPhotoResponse (
    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: PhotoData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

fun QiuckScanPhotoResponse.nullCheck(): QiuckScanPhotoResponse{
    return this.copy(
        code = code ?: 0,
        data = this.data?.nullCheck() ?: PhotoData(),
        status = status ?: ""
    )
}


data class PhotoItem(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("quick_scan")
    val quick_scan: String? = null,

    @field:SerializedName("image")
    val image: String? = null,
)

fun PhotoItem.nullCheck(): PhotoItem {
    return this.copy(
        id = id ?: 0,
        quick_scan = quick_scan ?:"",
        image = image ?: "",
    )
}

data class PhotoData(

    @field:SerializedName("next")
    val next: String? = null,

    @field:SerializedName("previous")
    val previous: String? = null,

    @field:SerializedName("num_pages")
    val numPages: Int? = null,

    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("results")
    var results: List<PhotoItem?>? = null
)

fun PhotoData.nullCheck(): PhotoData {
    return this.copy(
        next = next ?: "",
        previous = previous ?: "",
        numPages = numPages ?: 0,
        count = count ?: 0,
        results = results!!.map { it?.nullCheck() }
    )
}