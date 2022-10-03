package com.commoncents.utils

import android.content.Context
import android.content.Intent
import com.commoncents.preferences.Preferences
import com.commoncents.ui.auth.activity.LoginSignupActivity


object Constants {

    const val BAR_CODE_WIDTH = 100
    const val BAR_CODE_HEIGHT = 30
    const val DELAY = 1000L
    const val ORDER_BY_PACKID = "pack_id"
    const val API_NAME = "apiName"
    const val ID = "ID"
    const val FILE_NAME = "fileName"
    const val FROM_DRAWER = "fromDrawer"
    const val IS_SINGLE = "isSingle"
    const val IS_UN_KNOW = "isUnKnow"
    const val IS_DAMAGED = "damaged"
    const val PO_NUMBER = "poNumber"
    const val PO_NAME = "poName"

    const val printPackID = ""
    const val printPackIDALL = "all/"
    const val printPackIDUnKnowSingle = "unknown/"
    const val printPackIDUnKnowAll = "unknown/all/"


    const val UnAuthorize = 401

    const val Camera = "Camera"
    var REQUEST_CODE = "REQUEST_CODE"
    const val Gallery = "Gallery"
    const val Drive = "Drive"
    const val LOCATION_VALUE = "19-B-11"



    const val Single = "single"
    const val All = "all"

    const val Location = "Location"
    const val Pallet = "Pallet"
    const val Delete = "Delete"
    const val SuccessCode = 200
    const val InternalServerError = 500
    const val MSG_NO_INTERNET_CONNECTION = "The internet connection appears to be offline"
    const val MSG_SOMETHING_WENT_WRONG = "Something went wrong"

    object PrefKeys {
        var qrCode = "qrCode"
        var userid = "userid"
        var user = "user"
        var FCM_TOKEN = "fcmToken"
    }


    object apiKeys {
        const val DEVICE_ID = "Deviceid"
        const val DEVICE_TOKEN = "Devicetoken"
        const val DEVICE_TYPE = "Devicetype"
        const val AUTHORIZATION = "Authorization"
        const val RECENT = "recent"
        const val PACK_ID = "packID"
        const val PO = "purchase_order"
        const val P_NAME = "po_number"
        const val UPC = "upc"

        const val PACK_IDS = "pack_ids"
        const val CREATED_DATE = "created_at"
        const val ORDER_BY = "order_by"
        const val PO_DATE = "po_receiving_date"
        const val PO_NAME = "poName"
        const val ASC = "ascending"
        const val UN_KNOW = "unknown"
        const val DAMAGED = "damaged"
        const val PAGE = "page"
        const val TOTAL_PACK = "total_pack"
        const val SHIPMENT_ID = "shipment_id"
        const val LOCATION_ID = "location_id"
    }

    object apis {
        const val LOGIN = "login/"
        const val REFRESH_TOKEN = "token/refresh/"
        const val PURCHASE_ORDER = "purchase-order"
        const val PRINT_PACK_ID = "print-pack-id/{apiName}"
        const val QUICK_SCAN_WITH_ID = "quick-scan/{pack_id}/delete/"


        const val BROKEN_IMAGES = "quick-scan/{quick_scan_id}/photos/"

        const val QUICK_SCAN = "quick-scan"
        const val UPLOAD_IMAGE = "quick-scan/uploads/"
        const val ASSIGN_LOCATION = "quick-scan/assign-location/"
        const val VALIDATE_LOCATION = "validate-location/"
        const val ASSIGN_PO_UNKnowPO = "quick-scan/assign-purchase-order-to-unknown-po/"

        const val QUICK_SCAN_PHOTO = "quick-scan/{quick_scan_id}/photos"

        const val CHECK_PACK_ID_AVAILABLE = "quick-scan/{pack_id}/details"
        const val QUICK_SCAN_ASSIGN_LOCATION = "quick-scan/assign-location/"
        const val QUICK_SCAN_PHOTO_DELETE = "quick-scan/photos/delete/"
        const val UPLOAD_SINGLE_IMAGE = "quick-scan/upload-image/"

        const val DELETE_PACK_ID = "quick-scan/delete-pack-ids/"

        const val BREAK_DOWN = "break-down/"
    }



    fun logout(
        appContext: Context,
        userPreferences: Preferences
    ) {
        userPreferences.removePreference(
            appContext,
            PrefKeys.user
        )

        userPreferences.removePreference(
            appContext,
            PrefKeys.userid
        )


        val intent = Intent(appContext, LoginSignupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        appContext.startActivity(intent)
    }
}
