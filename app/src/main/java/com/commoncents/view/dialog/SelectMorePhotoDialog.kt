package com.commoncents.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.commoncents.R
import com.commoncents.databinding.CustomPopupCapturephotoLayoutBinding
import com.commoncents.utils.DialogListener
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Show dialog for SelectMorePhotoDialog from Application..
 */

class SelectMorePhotoDialog(
    context: Context,
    private val listener: DialogListener
) : BottomSheetDialog(
    context, R.style.Theme_DialogPrint
), View.OnClickListener {

    private lateinit var binding: CustomPopupCapturephotoLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomPopupCapturephotoLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
    }

    private fun setListeners() {
        binding.btnOK.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        dismiss()
        when (p0?.id) {
            R.id.btnOK -> {
                listener.onPrintClick(false,"")
            }
        }
    }
}