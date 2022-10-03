package com.commoncents.ui.dashboard.account.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentMyaccountBinding
import com.commoncents.ui.dashboard.DashboardActivity


/**
 * A simple [Fragment] subclass.
 * Use the [PackIDOperationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class MyaccountFragment : BaseFragment() {
    lateinit var binding: FragmentMyaccountBinding
    private lateinit var activity: DashboardActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyaccountBinding.inflate(inflater, container, false)
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
            MyaccountFragment().apply {}
    }
}