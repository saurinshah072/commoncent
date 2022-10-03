package com.commoncents.ui.dashboard.assignbrokenpallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.databinding.RowPdfItemBinding
import com.commoncents.utils.Constants
import com.commoncents.utils.Logger
import com.commoncents.utils.Utility
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException


class PackIDPdfAdapter(private val list: List<String>) :
    RecyclerView.Adapter<PackIDPdfAdapter.RowPDFViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowPDFViewHolder {
        val binding = RowPdfItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RowPDFViewHolder(binding)
    }

    inner class RowPDFViewHolder(var viewBinding: RowPdfItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onBindViewHolder(holder: RowPDFViewHolder, position: Int) {
        val packID = list[position]
        val multiFormatWriter = MultiFormatWriter()

        try {
            val bitMatrix = multiFormatWriter.encode(packID, BarcodeFormat.CODE_128, Constants.BAR_CODE_WIDTH, Constants.BAR_CODE_HEIGHT)
            holder.viewBinding.imgView.setImageBitmap(Utility.createBitmap(bitMatrix))
        } catch (e: WriterException) {
            Logger.e(Logger.TAG, "" + e.message)
        }

        holder.viewBinding.txtID.text = packID
    }

    override fun getItemCount(): Int {
        return list.size
    }
}