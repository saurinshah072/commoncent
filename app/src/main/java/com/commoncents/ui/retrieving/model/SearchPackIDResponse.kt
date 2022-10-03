package com.commoncents.ui.retrieving.model

import com.google.gson.annotations.SerializedName

data class SearchPackIDResponse(
    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: POData? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("errors")
    val errorData: ErrorData? = null
)

fun SearchPackIDResponse.nullCheck():SearchPackIDResponse{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: POData(),
        errorData = this.errorData?.nullCheck() ?: ErrorData()
    )
}

data class ErrorData(
    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("detail")
    val detail: String? = null
)

fun ErrorData.nullCheck():ErrorData{
    return this.copy(
        source = source ?: "",
        detail = detail ?: "",
    )
}

data class POData(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("pack_id")
    val packId: String? = null,

    @field:SerializedName("purchase_order")
    val purchaseOrder: String? = null,

    @field:SerializedName("location_id")
    val locationId: String? = null,

    @field:SerializedName("is_damaged")
    val isDamaged: Boolean? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null

)

fun POData.nullCheck(): POData{
    return this.copy(
        id = id ?: 0,
        packId = packId ?: "",
        purchaseOrder = purchaseOrder ?: "",
        locationId = locationId ?: "",
        isDamaged = isDamaged ?: false,
        createdAt = createdAt ?: ""
    )
}


