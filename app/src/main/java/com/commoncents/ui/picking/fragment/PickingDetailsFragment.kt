package com.commoncents.ui.picking.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.commoncents.databinding.CustomDialogOverPickedBinding
import com.commoncents.databinding.CustomDialogPalletScanBinding
import com.commoncents.databinding.FragmentPickingDetailsBinding

class PickingDetailsFragment : Fragment() {

    private var binding: FragmentPickingDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPickingDetailsBinding.inflate(inflater, container, false)

        initialization()
        setup()
        listeners()

        return binding?.root
    }

    private fun initialization() {


    }

    private fun setup() {


    }

    private fun listeners() {
        binding?.btnSubmitPick?.setOnClickListener {
            openPalletScanDialog()
        }
    }

    private fun openPalletScanDialog() {

        val dialog = Dialog(requireContext())
        val cdBinding = CustomDialogPalletScanBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(cdBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cdBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        cdBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            openOverPickedDialog()
        }

        dialog.show()
    }

    private fun openOverPickedDialog() {

        val dialog = Dialog(requireContext())
        val cdBinding = CustomDialogOverPickedBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(cdBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cdBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}