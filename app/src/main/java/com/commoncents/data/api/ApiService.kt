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
import com.commoncents.utils.Constants
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST(Constants.apis.LOGIN)
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(Constants.apis.REFRESH_TOKEN)
    suspend fun refreshToken(
        @Field("refresh") refresh: String
    ): Response<LoginResponse>


    @GET(Constants.apis.PURCHASE_ORDER)
    suspend fun searchPO(
        @QueryMap(encoded = true) options: Map<String, String>
    ): Response<SearchPOModel>


    @FormUrlEncoded
    @POST(Constants.apis.PRINT_PACK_ID)
    suspend fun printPackId(
        @Path(value = "apiName", encoded = true) apiName: String?,
        @FieldMap(encoded = true) params: Map<String, String>
    ): Response<QuickScanDetails>


    @GET(Constants.apis.QUICK_SCAN)
    suspend fun getScanDetails(
        @Path(value = "quick_scan_id", encoded = true) apiName: String?
    ): Response<QuickScanDetails>


    @DELETE(Constants.apis.QUICK_SCAN_WITH_ID)
    suspend fun deleteScanPackID(
        @Path(value = "pack_id", encoded = true) packID: String?
    ): Response<QuickScanDetails>


    @POST(Constants.apis.ASSIGN_LOCATION)
    suspend fun assignLocation(
        @Body jsonObject: JsonObject,
    ): Response<QuickScanDetails>


    @FormUrlEncoded
    @POST(Constants.apis.VALIDATE_LOCATION)
    suspend fun validateLocation(
        @Field("location_id") locationId: String
    ): Response<ValidateLocationResponse>


    @FormUrlEncoded
    @POST(Constants.apis.ASSIGN_PO_UNKnowPO)
    suspend fun assignPoToUnKnowPO(
        @Field("pack_id") packID: String,
        @Field("purchase_order") purchaseOrder: String
    ): Response<QuickScanDetails>


    @GET(Constants.apis.QUICK_SCAN)
    suspend fun packIDListing(
        @QueryMap(encoded = true) params: Map<String, String>
    ): Response<ListPackIDResponse>

    @GET(Constants.apis.QUICK_SCAN)
    suspend fun packIDOperationListing(
        @QueryMap(encoded = true) params: Map<String, String>
    ): Response<com.commoncents.ui.dashboard.packidoperation.model.ListPackIDResponse>

    @GET(Constants.apis.QUICK_SCAN_PHOTO)
    suspend fun getQuickScanPhoto(
        @Path(value = "quick_scan_id", encoded = true) scanId: String?,
        @QueryMap(encoded = true) params: Map<String, String>
    ): Response<QiuckScanPhotoResponse>

    @Multipart
    @POST(Constants.apis.UPLOAD_IMAGE)
    suspend fun uploadBrokenImages(
        @Part("pack_id") packID: RequestBody,
        @Part("location_id") locationID: RequestBody,
        @Part("is_damaged") isDamage: RequestBody,
        @Part file: Array<MultipartBody.Part?>?
    ): Response<QuickScanDetails>

    @GET(Constants.apis.BROKEN_IMAGES)
    suspend fun getBrokenPalletImages(@Path(value = "quick_scan_id", encoded = true) quickScanID: String?): Response<BrokenImageResponse>

    @GET(Constants.apis.PURCHASE_ORDER)
    suspend fun getReceivingPO(
        @QueryMap(encoded = true) options: Map<String, String>
    ): Response<PoModelResponse>

    @GET(Constants.apis.CHECK_PACK_ID_AVAILABLE)
    suspend fun checkPackIDAvailable(
        @Path(value = "pack_id", encoded = true) packId: String?,
    ): Response<SearchPackIDResponse>


    @POST(Constants.apis.QUICK_SCAN_ASSIGN_LOCATION)
    suspend fun updateAssignLocation(
        @Body params:RequestBody
    ): Response<AssignPackagingModel>


    @DELETE(Constants.apis.QUICK_SCAN_PHOTO_DELETE)
    suspend fun deleteImageData(
        @Body params:RequestBody
    ): Response<DeleteImagReponse>


    @Multipart
    @POST(Constants.apis.UPLOAD_SINGLE_IMAGE)
    suspend fun uploadSingleImage(
        @Part("pack_id") packID: RequestBody,
        @Part file: Array<MultipartBody.Part?>?
    ): Response<SingleImageUploadModel>

    //@DELETE(Constants.apis.DELETE_PACK_ID)
    @HTTP(method = "DELETE", path = Constants.apis.DELETE_PACK_ID, hasBody = true)
    suspend fun deletePackId(
        @Body params:RequestBody
    ): Response<DeletePackIdResponse>


    @POST(Constants.apis.BREAK_DOWN)
    suspend fun breakDownData(
        @Body params:RequestBody
    ): Response<BreakDownModel>
}
