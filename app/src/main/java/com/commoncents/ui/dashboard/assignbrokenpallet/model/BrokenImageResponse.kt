package com.commoncents.ui.dashboard.assignbrokenpallet.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class BrokenImageResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: BrokenImageData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

fun BrokenImageResponse.nullCheck(): BrokenImageResponse {
	return this.copy(
		code = code ?: 0,
		status = status ?: "",
		data = this.data?.nullCheck() ?: BrokenImageData()
	)
}


data class BrokenImageData(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("previous")
	val previous: String? = null,

	@field:SerializedName("num_pages")
	val numPages: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("results")
	val results: List<BrokenResultsItem>? = null
)


fun BrokenImageData.nullCheck(): BrokenImageData {
	return this.copy(
		next = next ?: "",
		previous = previous ?: "",
		numPages = numPages ?: 0,
		count = count ?: 0,
		results = results!!.map { it?.nullCheck() }
	)
}

data class BrokenResultsItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("quick_scan")
	val quickScan: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	var uri: String?= null,

	var bitmap: Bitmap?= null,

	var isCamera: Boolean?= null
)

fun BrokenResultsItem.nullCheck(): BrokenResultsItem {
	return this.copy(
		quickScan = quickScan ?:0,
		id = id ?: 0,
		image = image ?: "",
		uri = uri ?: "",
		bitmap = bitmap ?: null,
		isCamera = isCamera ?: false,
	)
}
