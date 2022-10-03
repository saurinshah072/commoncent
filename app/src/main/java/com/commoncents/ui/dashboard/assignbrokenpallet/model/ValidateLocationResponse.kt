package com.commoncents.ui.dashboard.assignbrokenpallet.model

import com.google.gson.annotations.SerializedName

data class ValidateLocationResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: LocationData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

fun ValidateLocationResponse.nullCheck(): ValidateLocationResponse {
	return this.copy(
		code = code ?: 0,
		status = status ?: "",
		data = this.data?.nullCheck() ?: LocationData()
	)
}

data class LocationData(
	@field:SerializedName("valid")
	val valid: Boolean? = null
)

fun LocationData.nullCheck(): LocationData {
	return this.copy(
		valid = valid ?: false,
	)
}
