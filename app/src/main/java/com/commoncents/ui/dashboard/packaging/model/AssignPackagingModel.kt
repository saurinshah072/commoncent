package com.commoncents.ui.dashboard.packaging.model

import com.google.gson.annotations.SerializedName

data class AssignPackagingModel(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: AssignPackagingData? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("errors")
    val errorData: ErrorAssignPackagingData? = null
)

fun AssignPackagingModel.nullCheck() : AssignPackagingModel{
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: AssignPackagingData()
    )
}


data class ErrorAssignPackagingData(
    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("detail")
    val detail: String? = null
)

fun ErrorAssignPackagingData.nullCheck():ErrorAssignPackagingData{
    return this.copy(
        source = source ?: "",
        detail = detail ?: "",
    )
}

data class AssignPackagingData(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("pack_id")
    val pack_id: String? = null,

    @field:SerializedName("purchase_order")
    val purchase_order: Int? = null,

    @field:SerializedName("location_id")
    val location_id: String? = null,

    @field:SerializedName("is_damaged")
    val is_damaged: Boolean? = null
)

fun AssignPackagingData.nullCheck() :AssignPackagingData{
    return this.copy(
        id = id ?: 0,
        pack_id = pack_id ?: "",
        purchase_order = purchase_order ?: 0,
        location_id = location_id ?: "",
        is_damaged = is_damaged ?: false
    )
}

