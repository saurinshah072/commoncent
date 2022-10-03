package com.commoncents.ui.dashboard.packidoperation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentViewpdfBinding
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.utils.Constants
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ViewPDFFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ViewPDFFragment : BaseFragment() {
    lateinit var binding: FragmentViewpdfBinding
    private lateinit var activity: DashboardActivity
    private var fileName  = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewpdfBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        loadPDF()
        return binding.root
    }

    private fun loadPDF() {
        arguments?.getString(Constants.FILE_NAME)?.let {
            fileName = it
            binding.pdfView.fromFile(File(fileName))
                .enableAnnotationRendering(true)
                .load()
        }
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
            ViewPDFFragment().apply {}
    }
}