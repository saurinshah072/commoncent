package com.commoncents.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.commoncents.R
import com.commoncents.databinding.CustomPopupAddphotoLayoutBinding
import com.commoncents.utils.SelectPhotoDialogListener
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Show dialog for SelectPhoto from Application..
 */

class SelectPhotoDialog(
    context: Context,
    private val listener: SelectPhotoDialogListener
) : BottomSheetDialog(
    context, R.style.Theme_DialogPrint
), View.OnClickListener {

    private lateinit var binding: CustomPopupAddphotoLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomPopupAddphotoLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
    }


    private fun setListeners() {
        binding.imgCamera.setOnClickListener(this)
        binding.imgGallery.setOnClickListener(this)
        binding.imgDrive.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        dismiss()
        when (p0?.id) {
            R.id.imgCamera -> {
                listener.onCameraClick()
            }
            R.id.imgGallery -> {
                listener.onGalleryClick()
            }
            R.id.imgDrive -> {
                listener.onDriveClick()
            }
        }
    }
}