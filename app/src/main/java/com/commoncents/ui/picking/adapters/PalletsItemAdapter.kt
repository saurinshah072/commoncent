package com.commoncents.ui.picking.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.R
import com.commoncents.databinding.ListItemPalletBinding
import com.commoncents.ui.picking.interfaces.PalletItemClicks

class PalletsItemAdapter(
    private val context: Context?, private val palletItemClicks: PalletItemClicks
) : RecyclerView.Adapter<PalletsItemAdapter.PalletsItemHolder>() {

    private var clickedPosition = -1

    class PalletsItemHolder(val binding: ListItemPalletBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PalletsItemHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.list_item_pallet, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PalletsItemHolder, position: Int) {
        with(holder.binding) {
            llRowItem.setOnClickListener {
                clickedPosition = holder.bindingAdapterPosition
                notifyDataSetChanged()
                palletItemClicks.getPalletItemPosition(position)
            }

            if (clickedPosition != -1) {
                if (context != null) {
                    if (clickedPosition == position) {
                        llRowItem.setBackgroundColor(
                            context.resources.getColor(
                                R.color.dark_blue, null
                            )
                        )
                    } else {
                        llRowItem.setBackgroundColor(
                            context.resources.getColor(
                                R.color.edithintcolor, null
                            )
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount() = 3

}
