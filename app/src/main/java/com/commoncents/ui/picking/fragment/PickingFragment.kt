package com.commoncents.ui.picking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentPickingBinding
import com.commoncents.ui.picking.adapters.PalletsAdapter
import com.commoncents.ui.picking.interfaces.PalletClicks

class PickingFragment : BaseFragment() {

    private var binding: FragmentPickingBinding? = null

    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPickingBinding.inflate(inflater, container, false)

        initialization()
        setup()
        listeners()

        return binding?.root
    }

    private fun initialization() {
        binding?.btnStart?.visibility = View.GONE
    }

    private fun setup() {
        binding?.rvPallets?.adapter = PalletsAdapter(context, object :
            PalletClicks {
            override fun getPalletPosition(lastClickedPosition: Int) {
                binding?.btnStart?.visibility = View.VISIBLE
            }
        })
    }

    private fun listeners() {
        binding?.btnStart?.setOnClickListener {
            navigation.navigate(R.id.pickingDetailsFragment)
        }
    }

}