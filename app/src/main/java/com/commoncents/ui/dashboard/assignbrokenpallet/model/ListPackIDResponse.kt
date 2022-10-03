package com.commoncents.ui.dashboard.assignbrokenpallet.model

import com.google.gson.annotations.SerializedName

data class ListPackIDResponse(
	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
)

fun ListPackIDResponse.nullCheck(): ListPackIDResponse {
	return this.copy(
		code = code ?: 0,
		status = status ?: "",
		data = this.data?.nullCheck() ?: Data()
	)
}

data class ResultsItem(
	@field:SerializedName("pack_id")
	var packId: String? = null,

	@field:SerializedName("purchase_order")
	val purchaseOrder: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("location_id")
	val locationId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("is_damaged")
	var isDamaged: Boolean? = false


)


fun ResultsItem.nullCheck(): ResultsItem {
	return this.copy(
		packId = packId ?:"",
		id = id ?: 0,
		purchaseOrder = purchaseOrder ?: 0,
		locationId = locationId ?: "",
		createdAt = createdAt ?: "",
		isDamaged = isDamaged ?: false,
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
	val count: Int? = null,

	@field:SerializedName("results")
	var results: List<ResultsItem?>? = null
)

fun Data.nullCheck(): Data {
	return this.copy(
		next = next ?: "",
		previous = previous ?: "",
		numPages = numPages ?: 0,
		count = count ?: 0,
		results = results!!.map { it?.nullCheck() }
	)
}
