package com.commoncents.ui.dashboard.quickscan.model

import com.google.gson.annotations.SerializedName

data class SearchPOModel(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("status")
    val status: String? = null
)

fun SearchPOModel.nullCheck(): SearchPOModel {
    return this.copy(
        code = code ?: 0,
        status = status ?: "",
        data = this.data?.nullCheck() ?: Data()
    )
}

data class ResultsItem(

    @field:SerializedName("eta_date")
    val etaDate: String? = null,

    @field:SerializedName("quantity")
    val quantity: Double? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("supplier_name")
    val supplierName: String? = null,

    @field:SerializedName("no_of_sku")
    val noOfSku: Double? = null,
)


fun ResultsItem.nullCheck(): ResultsItem {
    return this.copy(
        etaDate = etaDate ?: "",
        quantity = quantity ?: 0.0,
        updatedAt = updatedAt ?: "",
        name = name ?: "",
        createdAt = createdAt ?: "",
        id = id ?: 0,
        supplierName = supplierName ?: "",
        noOfSku = noOfSku ?: 0.0
    )
}

data class Data(

    @field:SerializedName("next")
    val next: String? = null,

    @field:SerializedName("previous")
    val previous: String? = null,

    @field:SerializedName("num_pages")
    val numPages: Int? = null,

    @field:SerializedName("count")
    val count: Double? = null,

    @field:SerializedName("results")
    val results: List<ResultsItem?> = ArrayList()
)


fun Data.nullCheck(): Data {
    return this.copy(
        next = next ?: "",
        previous = previous ?: "",
        numPages = numPages ?: 0,
        count = count ?: 0.0,
        results = results.map { it?.nullCheck() }
    )
}
