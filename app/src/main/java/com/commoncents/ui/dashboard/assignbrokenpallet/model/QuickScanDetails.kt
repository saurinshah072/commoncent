package com.commoncents.ui.dashboard.assignbrokenpallet.model

import com.google.gson.annotations.SerializedName

data class QuickScanDetails(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: QuickScanData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

fun QuickScanDetails.nullCheck(): QuickScanDetails {
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: QuickScanData()
    )
}

data class QuickScanData(

    @field:SerializedName("pack_id")
    val packId: String? = null,

    @field:SerializedName("purchase_order")
    val purchaseOrder: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("location_id")
    val locationId: String? = null,

    @field:SerializedName("is_damaged")
    val isDamaged: Boolean? = null,

    @field:SerializedName("total_pack_ids")
    val totalPackIds: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("printed_pack_ids")
    val printedPackIds: List<String?>? = null


)

fun QuickScanData.nullCheck(): QuickScanData {
    return this.copy(
        packId = packId ?: "",
        id = id ?: 0,
        purchaseOrder = purchaseOrder ?: 0,
        locationId = locationId ?: "",
        isDamaged = isDamaged ?: false,
        totalPackIds = totalPackIds ?: 0,
        message = message ?: "",
        printedPackIds = printedPackIds ?: ArrayList()
    )
}


