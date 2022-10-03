package com.commoncents.ui.retrieving.quickScanFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.commoncents.databinding.RowSearchRetrievingBinding


class SearchAdapter(
    private var context: Context,
    private var list: List<String>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = RowSearchRetrievingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5;
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

    }

    inner class SearchViewHolder(var viewBinding: RowSearchRetrievingBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


}