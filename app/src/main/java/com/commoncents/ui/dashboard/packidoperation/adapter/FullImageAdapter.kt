package com.commoncents.ui.dashboard.packidoperation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.commoncents.R

class FullImageAdapter (
    private var context: Context,
    private var list: ArrayList<String?>?
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    //
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view =    layoutInflater!!.inflate(R.layout.row_full_image, container, false)
        //
        Glide
            .with(context)
            .load(list!!.get(position))
            .into(view.findViewById<ImageView>(R.id.row_full_image_iv))
        //
        container.addView(view)
        //
        return view
    }
    //
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        //TODO("Not yet implemented")
        return view === `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return list!!.size
    }
}