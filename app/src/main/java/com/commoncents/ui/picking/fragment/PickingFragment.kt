package com.commoncents.ui.picking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentPickingBinding
import com.commoncents.ui.picking.adapters.PalletsAdapter
import com.commoncents.ui.picking.interfaces.PalletClicks

class PickingFragment : BaseFragment() {

    private var binding: FragmentPickingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPickingBinding.inflate(layoutInflater, container, false)

        initView()
        setup()
        clickListeners()

        return binding?.root
    }

    private fun initView() {


    }

    private fun setup() {
        binding?.rvPallets?.adapter = PalletsAdapter(context, object :
            PalletClicks {
            override fun getPalletPosition(position: Int) {

            }
        })
    }

    private fun clickListeners() {


    }

}