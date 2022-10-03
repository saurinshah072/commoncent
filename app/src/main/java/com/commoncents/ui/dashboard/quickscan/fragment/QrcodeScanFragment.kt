package com.commoncents.ui.dashboard.quickscan.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentQrcodeBinding
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.utils.Constants


class QrcodeScanFragment : BaseFragment() {
    private lateinit var binding: FragmentQrcodeBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var activity: DashboardActivity

    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        setupQRCode()
        activity.hideUnHideUnKnowPO(false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    /**
     * Init QRCode
     */

    private fun setupQRCode() {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                navigation.previousBackStackEntry?.savedStateHandle?.set(
                    Constants.PrefKeys.qrCode,
                    it.text.toString().trim()
                )
                navigation.popBackStack()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        val loadAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce)
        binding.imgLine.startAnimation(loadAnimation)
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance() = QrcodeScanFragment().apply {}
    }
}