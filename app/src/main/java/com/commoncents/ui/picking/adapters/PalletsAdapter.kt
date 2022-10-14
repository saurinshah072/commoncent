package com.commoncents.ui.picking.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.ListPalletsBinding
import com.commoncents.ui.picking.interfaces.PalletClicks
import com.commoncents.ui.picking.interfaces.PalletItemClicks

class PalletsAdapter(
    private val context: Context?, private val palletClicks: PalletClicks
) : RecyclerView.Adapter<PalletsAdapter.PalletsHolder>() {

    private var clickedPosition = -1
    private var childClickedPosition = -1

    class PalletsHolder(val binding: ListPalletsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PalletsHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_pallets, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PalletsHolder, position: Int) {
        with(holder.binding) {
            rvPalletsItems.adapter = PalletsItemAdapter(context, object :
                PalletItemClicks {
                override fun getPalletItemPosition(lastClickedPosition: Int) {
                    clickedPosition = holder.bindingAdapterPosition
                    childClickedPosition = lastClickedPosition
//                    notifyDataSetChanged()
                    palletClicks.getPalletPosition(clickedPosition)
                }
            })
        }
    }

    override fun getItemCount(): Int = 3
}