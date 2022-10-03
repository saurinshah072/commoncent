package com.commoncents.ui.dashboard.assignbrokenpallet.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.commoncents.R
import com.commoncents.databinding.RowPhotosBinding
import com.commoncents.ui.dashboard.assignbrokenpallet.model.BrokenResultsItem
import com.commoncents.utils.ViewUtils


class BrokenPalletImageAdapter(
    private var context: Context,
    private var list: List<BrokenResultsItem?>,
    private var viewHolderClicks: ViewHolderClicks
) : RecyclerView.Adapter<BrokenPalletImageAdapter.BrokenPalletViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrokenPalletViewHolder {
        val binding = RowPhotosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrokenPalletViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BrokenPalletViewHolder, position: Int) {
        val viewItem = list[position]
        //
        Log.e("myTag","viewItem!!"+viewItem!!.toString())
        //
        if(viewItem!!.isCamera!!){
            holder.viewBinding.imgClose.visibility = View.VISIBLE
            Glide.with(context)
                .load(viewItem.bitmap!!).placeholder(R.drawable.default_image)
                .into(holder.viewBinding.imgBrokenPalletImage)
        }else if (viewItem!!.uri!!?.isNotEmpty()) {
            holder.viewBinding.imgClose.visibility = View.VISIBLE
            Glide.with(context)
                .load(Uri.parse(viewItem.uri!!)).placeholder(R.drawable.default_image)
                .into(holder.viewBinding.imgBrokenPalletImage)
        }else {
            holder.viewBinding.imgClose.visibility = View.INVISIBLE
            ViewUtils.loadImage(context, viewItem.image!!, holder.viewBinding.imgBrokenPalletImage)
        }

        holder.viewBinding.imgClose.setOnClickListener{
            viewHolderClicks.onClickItem(position)
        }
    }

    fun setData(allList: ArrayList<BrokenResultsItem>) {
        this.list = allList
        notifyDataSetChanged()
    }

    inner class BrokenPalletViewHolder(var viewBinding: RowPhotosBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface ViewHolderClicks {
        fun onClickItem(position: Int)
    }
}
