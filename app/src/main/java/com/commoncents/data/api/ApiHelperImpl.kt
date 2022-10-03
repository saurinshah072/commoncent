package com.commoncents.data.api

import com.commoncents.ui.auth.model.LoginResponse
import com.commoncents.ui.breakdown.model.BreakDownModel
import com.commoncents.ui.dashboard.assignbrokenpallet.model.BrokenImageResponse
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ListPackIDResponse
import com.commoncents.ui.dashboard.assignbrokenpallet.model.QuickScanDetails
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ValidateLocationResponse
import com.commoncents.ui.dashboard.packaging.model.AssignPackagingModel
import com.commoncents.ui.dashboard.packaging.model.SingleImageUploadModel
import com.commoncents.ui.dashboard.packidoperation.model.DeleteImagReponse
import com.commoncents.ui.dashboard.packidoperation.model.DeletePackIdResponse
import com.commoncents.ui.dashboard.packidoperation.model.QiuckScanPhotoResponse
import com.commoncents.ui.dashboard.quickscan.model.SearchPOModel
import com.commoncents.ui.retrieving.model.PoModelResponse
import com.commoncents.ui.retrieving.model.SearchPackIDResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun login(
        userName: String,
        password: String
    ): Response<LoginResponse> = apiService.login(
        userName,
        password
    )

    override suspend fun searchPO(
        params: MutableMap<String, String>
    ): Response<SearchPOModel> = apiService.searchPO(params)

    override suspend fun refreshToken(refreshToken: String): Response<LoginResponse> =
        apiService.refreshToken(refreshToken)


  override suspend fun validateLocation(locationID: String): Response<ValidateLocationResponse> =
        apiService.validateLocation(locationID)



    override suspend fun printPackID(
        apiName: String,
        params: MutableMap<String, String>
    ): Response<QuickScanDetails> =
        apiService.printPackId(apiName, params)


    override suspend fun getScanDetails(
        quickScanID: String,
    ): Response<QuickScanDetails> = apiService.getScanDetails(quickScanID)


    override suspend fun deleteScanPackID(
        packID: String,
    ): Response<QuickScanDetails> = apiService.deleteScanPackID(packID)

    override suspend fun getBrokenPalletImages(quickScanID:String):
            Response<BrokenImageResponse> = apiService.getBrokenPalletImages(quickScanID)



    override suspend fun assignLocation(
        packID: JsonObject
    ): Response<QuickScanDetails> = apiService.assignLocation(packID)

    override suspend fun assignPOToUnKnowPO(
        packID: String,
        purchaseOrder: String,
    ): Response<QuickScanDetails> = apiService.assignPoToUnKnowPO(packID, purchaseOrder)

    override suspend fun packIDListing(
        params: MutableMap<String, String>
    ): Response<ListPackIDResponse> = apiService.packIDListing(params)

    override suspend fun packIDOperationListing(
        params: MutableMap<String, String>
    ): Response<com.commoncents.ui.dashboard.packidoperation.model.ListPackIDResponse> = apiService.packIDOperationListing(params)

    override suspend fun getQuickScanPhoto(
        scanId: String?,
        params: MutableMap<String, String>
    ): Response<QiuckScanPhotoResponse> = apiService.getQuickScanPhoto(scanId,params)

    override suspend fun uploadBrokenImages(
        packID: RequestBody,
        locationID: RequestBody,
        isDamage : RequestBody,
        file:Array<MultipartBody.Part?>
    ): Response<QuickScanDetails> = apiService.uploadBrokenImages(packID, locationID,
        isDamage,file)

    override suspend fun getReceivingPO(params: MutableMap<String, String>): Response<PoModelResponse> = apiService.getReceivingPO(params)



    override suspend fun checkPackIDAvailable(packID: String): Response<SearchPackIDResponse> = apiService.checkPackIDAvailable(packID)

    override suspend fun updateAssignLocation(requestParams:RequestBody) :Response<AssignPackagingModel> = apiService.updateAssignLocation(requestParams)

    override suspend fun deleteImageData(requestParams:RequestBody) :Response<DeleteImagReponse> = apiService.deleteImageData(requestParams)

    override suspend fun uploadSingleImage (packID: RequestBody,file:Array<MultipartBody.Part?>) :Response<SingleImageUploadModel> = apiService.uploadSingleImage(packID,file)

    override suspend fun deletePackId (packID: RequestBody) :Response<DeletePackIdResponse> = apiService.deletePackId(packID)
    override suspend fun breakDownData (breakDownData: RequestBody) :Response<BreakDownModel> = apiService.breakDownData(breakDownData)
}
