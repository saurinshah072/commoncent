package com.commoncents.data.repository

import com.commoncents.data.api.ApiHelper
import com.commoncents.ui.dashboard.assignbrokenpallet.model.BrokenImageResponse
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ListPackIDResponse
import com.commoncents.ui.dashboard.assignbrokenpallet.model.QuickScanDetails
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ValidateLocationResponse
import com.commoncents.ui.dashboard.packidoperation.model.QiuckScanPhotoResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class AssignLocationRepository @Inject constructor(
    private val apiHelper: ApiHelper
) {
    suspend fun assignLocation(
        jsonObject: JsonObject
    ): Response<QuickScanDetails> = apiHelper.assignLocation(jsonObject)


      suspend fun validateLocation(
        locationID: String,
    ): Response<ValidateLocationResponse> = apiHelper.validateLocation(locationID)

    suspend fun printPackID( apiName: String,
                             params: MutableMap<String, String>) = apiHelper.printPackID(apiName,params)

    suspend fun getScanDetails(
        quickScanID: String,
    ): Response<QuickScanDetails> = apiHelper.getScanDetails(quickScanID)


    suspend fun deleteScanPackID(
        packID: String,
    ): Response<QuickScanDetails> = apiHelper.deleteScanPackID(packID)

    suspend fun getBrokenPalletImages(quickScanID:String):
              Response<BrokenImageResponse> = apiHelper.getBrokenPalletImages(quickScanID)

    suspend fun assignPOToUnKnowPO(
        packID: String,
        purchaseOrder: String,
    ): Response<QuickScanDetails> = apiHelper.assignPOToUnKnowPO(packID, purchaseOrder)


    suspend fun uploadBrokenImages(
        packID: RequestBody,
        locationID: RequestBody,
        isDamage : RequestBody,
        file:Array<MultipartBody.Part?>
    ): Response<QuickScanDetails> = apiHelper.uploadBrokenImages(packID, locationID,
        isDamage,file)

    suspend fun packIDListing(
        params: MutableMap<String, String>
    ): Response<ListPackIDResponse> = apiHelper.packIDListing(params)


    suspend fun packIDOperationtListing(
        params: MutableMap<String, String>
    ): Response<com.commoncents.ui.dashboard.packidoperation.model.ListPackIDResponse> = apiHelper.packIDOperationListing(params)

    suspend fun quickScanPhotoListing(
        scanId:String,
        params: MutableMap<String, String>
    ): Response<QiuckScanPhotoResponse> = apiHelper.getQuickScanPhoto(scanId,params)

    suspend fun searchPO(
        params: MutableMap<String, String>
    ) = apiHelper.searchPO(params)

    suspend fun getReceivingPO(
        params: MutableMap<String, String>
    ) = apiHelper.getReceivingPO(params)

    suspend fun checkPackIDAvailable(
        packID: String
    ) = apiHelper.checkPackIDAvailable(packID)

    suspend fun updateAssignLocation(
        requestParams:RequestBody
    ) = apiHelper.updateAssignLocation(requestParams)

    suspend fun deleteImageData(
        requestParams:RequestBody
    ) = apiHelper.deleteImageData(requestParams)

    suspend fun uploadSingleImage(
        packID: RequestBody,
        file:Array<MultipartBody.Part?>
    ) = apiHelper.uploadSingleImage(packID,file)

    suspend fun deletePackId(
        packID: RequestBody
    ) = apiHelper.deletePackId(packID)

    suspend fun breakDownData(
        breakDownData: RequestBody
    ) = apiHelper.breakDownData(breakDownData)


}
