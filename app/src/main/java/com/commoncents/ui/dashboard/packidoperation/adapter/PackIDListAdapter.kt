package com.commoncents.ui.dashboard.packidoperation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.RowPaclkidOperationitemBinding
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ResultsItem

class PackIDListAdapter(
    private var context: Context,
    private var list: List<ResultsItem?>,
    private var viewHolderClicks: ViewHolderClicks
) : RecyclerView.Adapter<PackIDListAdapter.SupplierListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierListViewHolder {
        val binding = RowPaclkidOperationitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupplierListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SupplierListViewHolder, position: Int) {
        val viewItem = list[position]

        holder.viewBinding.txtID.text = viewItem!!.packId

        if ((position % 2) != 0) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.white))
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.edittext))
        }

        if(viewItem.isDamaged!!){
            holder.viewBinding.imgViewPdf.visibility = View.VISIBLE
        }else{
            holder.viewBinding.imgViewPdf.visibility = View.INVISIBLE
        }

        holder.viewBinding.imgViewPdf.setOnClickListener {
            viewHolderClicks.onClickBrokenImageItem(position)
        }

        holder.viewBinding.txtViewPackID.setOnClickListener {
            viewHolderClicks.onClickViewPDF(position)
        }
    }

    inner class SupplierListViewHolder(var viewBinding: RowPaclkidOperationitemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface ViewHolderClicks {
        fun onClickBrokenImageItem(position: Int)
        fun onClickViewPDF(position: Int)
    }

    fun setData(allList: ArrayList<ResultsItem?>) {
        list = allList
        notifyDataSetChanged()
    }
}
