package com.commoncents.ui.retrieving.quickScanFragment
//
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentRetrievingBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.retrieving.viewModel.CheckPackIDVM
import com.commoncents.utils.Utility
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

//
/**
 * A simple [Fragment] subclass.
 * Use the [PackIDOperationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RetrievingFragment : BaseFragment(),View.OnClickListener  {

    //
    private lateinit var searchAdapter: SearchAdapter
    lateinit var binding: FragmentRetrievingBinding
    private lateinit var activity: DashboardActivity
    private val viewModel: CheckPackIDVM by viewModels()
    private var packID:String = ""
    private val navigation: NavController by lazy {
        findNavController()
    }
    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRetrievingBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        setObserver()
        initView()
        setListeners()
        setupSearchData()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }
    private fun initView() {

    }
    private fun setListeners() {
//        binding.llRadioYes.setOnClickListener(this)
//        binding.llRadioNo.setOnClickListener(this)
        binding.rlLocationQrCode.setOnClickListener(this)
        binding.retrievingSubmit.setOnClickListener(this)
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.fragmentRetrievingLayout.error = null
                packID = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


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
            RetrievingFragment().apply {}
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.rlLocationQrCode -> {
                checkCameraPermission(view)
            }
            R.id.retrieving_submit->{
                //Toast.makeText(activity,"Please Enter Pack ID",Toast.LENGTH_SHORT).show()
                if(packID == "" || packID.isEmpty()){
                    Toast.makeText(activity,"Please Enter Pack ID",Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.checkPOModel(packID,true)
                }
                //navigation!!.navigate(R.id.retrievingFormFragment)
            }
        }
    }

    private fun setupSearchData() {
//        searchAdapter = SearchAdapter(
//            activity!!,
//            arrayListOf<String>("","",""))
//        binding.recycleViewSearchRetrieving.adapter = searchAdapter
    }

    /**
     * Set observer
     */
    private fun setObserver() {
        viewModel.checkPoModelVM.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                   // showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)

                    it.data?.let { packId ->
                        packId.data?.let {
                            Log.e("myTagRepsnse",packId.data?.toString())
                        }
                    }
                }
                Status.ERROR -> {
                    it.errorItem!!.forEach {
                        Log.e("myTag","Error source "+it.source)
                        Log.e("myTag","Error detail "+it.detail)
                        binding.fragmentRetrievingLayout.error =  it.detail
                        binding.fragmentRetrievingLayout.errorIconDrawable = null
                        //fragment_retrieving_layout
                    }
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                }
                else -> {

                }
            }
        }
    }
    private fun checkCameraPermission(myView: View?) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Utility.hideKeyboard(context!!, myView!!)
                /**
                 * clear the search text
                 */
                navigation.navigate(R.id.action_qrcode)
                //navigation.navigate(R.id.pacIDNotAssignedFragment)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }
        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.CAMERA).check()
    }
}