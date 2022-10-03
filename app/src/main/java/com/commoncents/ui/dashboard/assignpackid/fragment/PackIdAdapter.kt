package com.commoncents.ui.dashboard.assignpackid.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.databinding.RowLocationPackidsBinding

class PackIdAdapter(
    private var context: Context,
    private var packIdList: List<String>,
    private var viewHolderClicks: PackIdHolderClickEvent
) : RecyclerView.Adapter<PackIdAdapter.PackIdAdapterHolder>() {
    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackIdAdapterHolder {
        val binding = RowLocationPackidsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackIdAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return packIdList!!.count();
    }

    override fun onBindViewHolder(holder: PackIdAdapterHolder, position: Int) {
        val resultItemObj = packIdList!!.get(position)
        holder.viewBinding.rowLocationPackidTvCb.text =  resultItemObj
        holder.viewBinding.rowLocationPackidCb.setOnCheckedChangeListener{
            view,isChecked->
            viewHolderClicks.onClickItem(resultItemObj)
        }

    }

    inner class PackIdAdapterHolder(var viewBinding: RowLocationPackidsBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface PackIdHolderClickEvent {
        fun onClickItem(packId: String)
    }
}
