package com.commoncents.core

import android.app.Activity
import android.content.Context
import com.commoncents.network.NetworkHelper
import com.commoncents.preferences.Preferences
import com.commoncents.view.dialog.CustomProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var customProgressDialog: CustomProgressDialog? = null

    private lateinit var mContext: Context

    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var networkHelper: NetworkHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    /**
     * show progress dialog
     * @param activity activity of activity
     */
    protected fun showProgress(activity: Activity) {
        try {
            if (customProgressDialog != null && customProgressDialog!!.isShowing) {
                return
            }
            customProgressDialog = CustomProgressDialog(activity)
            customProgressDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * hide progress dialog
     */
    protected fun hideProgress() {
        try {
            if (customProgressDialog != null) {
                if (customProgressDialog!!.isShowing) {
                    customProgressDialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}