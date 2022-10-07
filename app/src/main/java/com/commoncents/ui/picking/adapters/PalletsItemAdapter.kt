package com.commoncents.ui.picking.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.ListItemPalletBinding
import com.commoncents.ui.picking.interfaces.PalletItemClicks

class PalletsItemAdapter(
    private val context: Context?,
    private val palletItemClicks: PalletItemClicks
) : RecyclerView.Adapter<PalletsItemAdapter.PalletsItemHolder>() {

    class PalletsItemHolder(val binding: ListItemPalletBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PalletsItemHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_pallet, parent, false
        )
    )

    override fun onBindViewHolder(holder: PalletsItemHolder, position: Int) {
        with(holder.binding) {
            llRowItem.setOnClickListener {
                palletItemClicks.getPalletItemPosition(position)
            }
        }
    }

    override fun getItemCount() = 3

}
