package com.commoncents.ui.dashboard.packidoperation.adapter

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.RowPackidAdapterBinding
import com.google.gson.Gson
import com.commoncents.ui.dashboard.packidoperation.model.ResultsItem
import com.commoncents.ui.dashboard.quickscan.adapter.PurchaseOrderListAdapter

class PackIDAdapter(
    private var context: Context,
    private var list: List<ResultsItem?>?,
    private var viewHolderClicks: ViewHolderClicks
) : RecyclerView.Adapter<PackIDAdapter.PackIdViewHolder>() {
    var isRefresh = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackIdViewHolder {
        val binding = RowPackidAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackIdViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.count();
    }

    override fun onBindViewHolder(holder: PackIdViewHolder, position: Int) {
        val resultItemObj = list!!.get(position)
        holder.viewBinding.rowPackidAdapterTv.setText(resultItemObj?.packId.toString())
        //
        if ((position % 2) != 0) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.white))
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.edittext))
        }
        //
        Log.e("myTagAdapter",""+resultItemObj?.isDamaged!!)
        //
        if(resultItemObj?.isDamaged!!){
            holder.viewBinding.rowPackidAdapterImageTv.visibility = View.VISIBLE
        }else{
            holder.viewBinding.rowPackidAdapterImageTv.visibility = View.INVISIBLE
        }
        //
        holder.viewBinding.rowPackidAdapterImageTv.setOnClickListener{
            viewHolderClicks.onClickItem(list!!.get(position)!!)
        }

        holder.viewBinding.rowPackidAdapterPdfTv.setOnClickListener{
            viewHolderClicks.onPDfCLick(list!!.get(position)!!.packId!!)
        }

        holder.viewBinding.rowPackidAdapterCb.setOnCheckedChangeListener(){
            view,isChecked ->
            viewHolderClicks.onPackIdChecked(list!!.get(position)!!.packId!!)
        }

        if(isRefresh){
            holder.viewBinding.rowPackidAdapterCb.visibility = View.VISIBLE
        }else{
            holder.viewBinding.rowPackidAdapterCb.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener{
            viewHolderClicks.onLongPressClick()
            isRefresh = true
            notifyDataSetChanged()
            return@setOnLongClickListener true
        }
    }

    inner class PackIdViewHolder(var viewBinding: RowPackidAdapterBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface ViewHolderClicks {
        fun onClickItem(resultsItem: ResultsItem)
        fun onLongPressClick()
        fun onPDfCLick(packID:String)
        fun onPackIdChecked(packID:String)
    }

    fun resetAdapterData(){
        isRefresh = false
        notifyDataSetChanged()
    }

    fun setData(allList: ArrayList<ResultsItem?>) {
        list = allList
        notifyDataSetChanged()
    }
}