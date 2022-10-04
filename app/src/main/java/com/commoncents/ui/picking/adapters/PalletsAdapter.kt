package com.commoncents.ui.picking.adapters

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

    class PalletsHolder(val binding: ListPalletsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PalletsHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_pallets, parent, false
        )
    )

    override fun onBindViewHolder(holder: PalletsHolder, position: Int) {
        with(holder.binding) {

            rvPalletsItems.adapter = PalletsItemAdapter(context, object :
                PalletItemClicks {
                override fun getPalletItemPosition(position: Int) {

                }
            })

            crdPallet.setOnClickListener {
                palletClicks.getPalletPosition(position)
            }
        }
    }

    override fun getItemCount(): Int = 3
}