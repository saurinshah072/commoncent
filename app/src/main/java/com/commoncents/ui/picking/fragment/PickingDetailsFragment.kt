package com.commoncents.ui.picking.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.commoncents.databinding.*

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
            openDialogPalletScan()
        }
    }

    private fun openDialogPalletScan() {
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
            openDialogOverPicked()
        }

        dialog.show()
    }

    private fun openDialogOverPicked() {
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
            openDialogUnderPicked()
        }

        dialog.show()
    }

    private fun openDialogUnderPicked() {
        val dialog = Dialog(requireContext())
        val cdBinding =
            CustomDialogUnderPickedBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(cdBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cdBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        cdBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            openDialogMissingAmount()
        }

        dialog.show()
    }

    private fun openDialogMissingAmount() {
        val dialog = Dialog(requireContext())
        val cdBinding =
            CustomDialogMissingAmountBinding.inflate(LayoutInflater.from(requireContext()))
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
        }

        dialog.show()
    }
}