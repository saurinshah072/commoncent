package com.commoncents.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.commoncents.R
import com.commoncents.databinding.CustomPopupSuccessLayoutBinding
import com.commoncents.utils.Constants
import com.commoncents.utils.DialogListener
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Show dialog for SuccessDialog from Application..
 */

class SuccessDialog(
    context: Context,
    private val tag: String,
    private val packID: String,
    private val msg: String,
    private val fromFragment: Boolean,
    private val listener: DialogListener
) : BottomSheetDialog(
    context, R.style.Theme_DialogPrint
), View.OnClickListener {

    private lateinit var binding: CustomPopupSuccessLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomPopupSuccessLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        loadGif()
        setListeners()
    }

    private fun loadGif() {
        when (tag) {
            Constants.Location -> {
                binding.txtMessage.text = msg
                Glide.with(context).load(R.drawable.location).into(binding.imgGif)
                binding.btnOK.text = context.getString(R.string.ok)
            }
            Constants.Delete -> {
                val firstIndex: Int = msg.indexOf(packID)
                val lastCharIndex: Int = firstIndex + packID.length

                val builder = SpannableStringBuilder()
                val redSpannable = SpannableString(msg)
                redSpannable.setSpan(ForegroundColorSpan(context.getColor(R.color.orange)),
                    firstIndex, lastCharIndex, 0)
                builder.append(redSpannable)
                binding.txtMessage.setText(builder, TextView.BufferType.SPANNABLE)
                Glide.with(context).load(R.drawable.delete).into(binding.imgGif)
                binding.btnOK.text = context.getString(R.string.confirm)
            }
            Constants.Single -> {
                binding.txtMessage.text = msg
                binding.btnOK.text = context.getString(R.string.viewpdf)
                Glide.with(context).load(R.drawable.viewpdf).into(binding.imgGif)
            }
            Constants.All -> {
                binding.txtMessage.text = msg
                binding.btnOK.text = context.getString(R.string.viewpdf)
                Glide.with(context).load(R.drawable.viewpdf).into(binding.imgGif)
            }
            else -> {
                binding.txtMessage.text = msg
                Glide.with(context).load(R.drawable.pallete).into(binding.imgGif)
                binding.btnOK.text = context.getString(R.string.ok)
            }
        }
    }

    private fun setListeners() {
        binding.btnOK.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnOK -> {
                dismiss()
                if (fromFragment || binding.btnOK.text.toString()==context.getString(R.string.viewpdf)) {
                    listener.onPrintClick(true,"")
                }
            }
        }
    }
}