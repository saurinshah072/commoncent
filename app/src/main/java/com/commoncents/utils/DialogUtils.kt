package com.commoncents.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.view.View
import android.widget.TextView
import com.commoncents.R


object DialogUtils {
    private var maintenanceDialog: Dialog? = null
    private var dialog: Dialog? = null
    private var videoDialog: Dialog? = null
    private var privacySmallDialog: Dialog? = null
    private var privacyDialog: Dialog? = null
    private var alertDialog: AlertDialog? = null

    fun showAlertDialog(activity: Activity, message: String?) {

        if (dialog != null && dialog!!.isShowing())
            dialog!!.dismiss()

        dialog = Dialog(activity, R.style.Theme_Dialog)
        val view = View.inflate(activity, R.layout.dialog_common, null)

        val txtMessage = view.findViewById<TextView>(R.id.txtMessage)
        val txtYes = view.findViewById<TextView>(R.id.txtYes)
        val txtNo = view.findViewById<TextView>(R.id.txtNo)
        val txtOk = view.findViewById<TextView>(R.id.txtOk)

        txtMessage.text = message

        txtOk.setOnClickListener {
            dialog!!.dismiss()
        }

        dialog!!.setContentView(view)
        dialog!!.show()
    }

}
