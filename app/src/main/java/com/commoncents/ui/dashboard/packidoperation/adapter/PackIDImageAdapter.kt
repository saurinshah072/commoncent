package com.commoncents.ui.dashboard.packidoperation.adapter
//
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.commoncents.databinding.RowPackidImagesBinding
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ResultsItem
import com.commoncents.ui.dashboard.packidoperation.model.PhotoItem

//
class PackIDImageAdapter (
    private var context: Context,
    private var list: List<PhotoItem?>?,
    private var isImageDelete: Boolean,
    private var viewHolderClicks: PackIDImageAdapter.ViewHolderClicks
) : RecyclerView.Adapter<PackIDImageAdapter.PackIDImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackIDImageViewHolder {
        val binding = RowPackidImagesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackIDImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size;
    }

    override fun onBindViewHolder(holder: PackIDImageViewHolder, position: Int) {
        val photoItemObj = list!!.get(position)
        holder.viewBinding.rowPackidIamgeIv.setOnClickListener{
            viewHolderClicks.onClickItem(position)
        }
        Log.e("myTag","photoItemObj?.image!! "+photoItemObj?.image!!)
        Glide.with(context).load(photoItemObj?.image!!).into(holder.viewBinding.rowPackidIamgeIv)
        //Log.e("myTagImage",photoItemObj?.image!!)
        holder.viewBinding.rowPackidRemoveCb.setOnCheckedChangeListener{
                buttonView, isChecked ->
            viewHolderClicks.onRemoveImage(photoItemObj.id!!)
        }
        //
        if(isImageDelete){
            holder.viewBinding.rowPackidRemoveCb.visibility = View.VISIBLE
        }else{
            holder.viewBinding.rowPackidRemoveCb.visibility = View.GONE
        }
        //
    }

    inner class PackIDImageViewHolder(var viewBinding: RowPackidImagesBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    interface ViewHolderClicks {
        fun onClickItem(position: Int)
        fun onRemoveImage(position: Int)
    }

    fun removeImages(isDeleteImage:Boolean) {
        isImageDelete = isDeleteImage
        notifyDataSetChanged()
    }
}