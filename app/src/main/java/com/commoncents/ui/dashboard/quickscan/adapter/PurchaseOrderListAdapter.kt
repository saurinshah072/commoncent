package com.commoncents.ui.dashboard.quickscan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.RowItemBinding
import com.commoncents.ui.dashboard.quickscan.model.ResultsItem

class PurchaseOrderListAdapter(
    private var context: Context,
    private var list: List<ResultsItem?>,
    private var viewHolderClicks: ViewHolderClicks
) : RecyclerView.Adapter<PurchaseOrderListAdapter.SupplierListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierListViewHolder {
        val binding = RowItemBinding.inflate(
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

        holder.viewBinding.txtID.text = viewItem!!.name
        holder.viewBinding.txtSku.text = viewItem.noOfSku!!.toInt().toString()

        if (viewItem.quantity != null) {
            holder.viewBinding.txtQty.text = viewItem.quantity.toInt().toString()
        } else {
            holder.viewBinding.txtQty.text = "0"
        }

        holder.viewBinding.txtName.text = viewItem.supplierName.toString()


        if ((position % 2) != 0) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.white))
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.edittext))
        }

        holder.viewBinding.txtID.setOnClickListener {
            viewHolderClicks.onClickItem(position)
        }
    }

    inner class SupplierListViewHolder(var viewBinding: RowItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface ViewHolderClicks {
        fun onClickItem(position: Int)
    }

    fun setData(allList: ArrayList<ResultsItem?>) {
        list = allList
        notifyDataSetChanged()
    }
}
