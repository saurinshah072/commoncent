package com.commoncents.ui.breakdown.model

import com.google.gson.annotations.SerializedName


data class BreakDownModel(
    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: BreakDownData? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("errors")
    val errorData: ErrorBreakDown? = null
)


fun BreakDownModel.nullCheck() : BreakDownModel {
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: BreakDownData()
    )
}

data class BreakDownData(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,


)

fun BreakDownData.nullCheck():BreakDownData{
    return this.copy(
        id = id ?: 0,
        created_at = created_at ?: "",
    )
}

data class ErrorBreakDown(
    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("detail")
    val detail: String? = null
)

fun ErrorBreakDown.nullCheck():ErrorBreakDown{
    return this.copy(
        source = source ?: "",
        detail = detail ?: "",
    )
}
