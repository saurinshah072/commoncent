package com.commoncents.ui.dashboard.packidoperation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import androidx.viewpager.widget.ViewPager
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentFullImageBinding
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.packidoperation.adapter.FullImageAdapter

class FullImageFragment  : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentFullImageBinding
    private lateinit var fullImageAdapter: FullImageAdapter
    private lateinit var activity: DashboardActivity
    //
    private val navigation: NavController by lazy {
        findNavController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullImageBinding.inflate(inflater, container, false)
        initView()
        activity.hideUnHideUnKnowPO(false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MyaccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PackIDImageFragment().apply {}
    }

    private fun initView() {
        Log.e("myTagImages",arguments?.getStringArrayList("photoData").toString())
        fullImageAdapter = FullImageAdapter(
            activity!!,
            arguments?.getStringArrayList("photoData"))
        binding.fragmentFullImageVp.adapter =  fullImageAdapter
        binding.fragmentFullImageVp.addOnPageChangeListener(viewPagerPageChangeListener)
        //arguments?.getString("position")

    }
    var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(state: Int) {
            // your logic here
        }
        override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {
            // your logic here
        }
        override fun onPageSelected(position: Int) {
            // your logic here
        }
    }
    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}