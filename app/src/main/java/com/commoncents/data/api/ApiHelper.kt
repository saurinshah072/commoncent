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


interface ApiHelper {

    /**
     * Call login api
     */

    suspend fun login(
        userName: String,
        password: String
    ): Response<LoginResponse>


    /**
     * Call search-po api
     */


    suspend fun searchPO(
        params: MutableMap<String, String>
    ): Response<SearchPOModel>

    suspend fun refreshToken(
        refreshToken: String
    ): Response<LoginResponse>

     suspend fun printPackID(
        apiName: String,
        params: MutableMap<String, String>
    ): Response<QuickScanDetails>


    suspend fun getScanDetails(
        quickScanID: String,
    ): Response<QuickScanDetails>


    suspend fun deleteScanPackID(
        packID: String,
    ): Response<QuickScanDetails>

     suspend fun getBrokenPalletImages(quickScanID: String): Response<BrokenImageResponse>


    suspend fun assignLocation(
        packID: JsonObject
    ): Response<QuickScanDetails>



 suspend fun validateLocation(
        locationID: String,
    ): Response<ValidateLocationResponse>



    suspend fun assignPOToUnKnowPO(
        packID: String,
        purchaseOrder: String,
    ): Response<QuickScanDetails>

    suspend fun packIDListing(
        params: MutableMap<String, String>
    ): Response<ListPackIDResponse>

    suspend fun packIDOperationListing(
        params: MutableMap<String, String>
    ): Response<com.commoncents.ui.dashboard.packidoperation.model.ListPackIDResponse>


    suspend fun getQuickScanPhoto(
        scanId: String?,
        params: MutableMap<String, String>
    ): Response<QiuckScanPhotoResponse>

    suspend fun uploadBrokenImages(
        packID: RequestBody,
        locationID: RequestBody,
        isDamage : RequestBody,
        file:Array<MultipartBody.Part?>
    ): Response<QuickScanDetails>


    suspend fun getReceivingPO(
        params: MutableMap<String, String>
    ): Response<PoModelResponse>

    suspend fun checkPackIDAvailable(PackID:String): Response<SearchPackIDResponse>

    suspend fun updateAssignLocation(
        requestParams: RequestBody
    ) :Response<AssignPackagingModel>

    suspend fun deleteImageData(
        requestParams: RequestBody
    ) :Response<DeleteImagReponse>


    suspend fun uploadSingleImage(
        packID: RequestBody,
        file:Array<MultipartBody.Part?>
    ):Response<SingleImageUploadModel>

    suspend fun deletePackId(
        packID: RequestBody
    ):Response<DeletePackIdResponse>

    suspend fun breakDownData(
        breakDownData: RequestBody
    ):Response<BreakDownModel>

}
