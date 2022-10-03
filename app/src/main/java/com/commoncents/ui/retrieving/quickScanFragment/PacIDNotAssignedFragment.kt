package com.commoncents.ui.retrieving.quickScanFragment
//
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentQuickscanBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.retrieving.formFragment.PoModelAdapter
import com.commoncents.ui.retrieving.model.ResultsItem
import com.commoncents.ui.retrieving.viewModel.PoModelVM
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.ArrayList
import java.util.HashMap
//
class PacIDNotAssignedFragment  : BaseFragment(), View.OnClickListener  {
    //
    private lateinit var binding: FragmentQuickscanBinding
    private lateinit var activity: DashboardActivity
    private val viewModel: PoModelVM by viewModels()
    private var poNumber = ""
    private var page = 1
    private var numPages = 1
    private val allList: ArrayList<ResultsItem?> = ArrayList()
    private lateinit var adapter: PoModelAdapter
    var vertical: LinearLayoutManager? = null
    //
    private val navigation: NavController by lazy {
        findNavController()
    }
    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuickscanBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        //
        setupLayoutManager()
        setObserver()
        initView()
        setListeners()
        //
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }
    private fun initView() {
        loadApis(false)
    }
    private fun setListeners() {

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
        }

    }

    private fun setupLayoutManager() {
        vertical = LinearLayoutManager(activity)
        vertical!!.orientation = LinearLayoutManager.VERTICAL
        binding.recycleView.layoutManager = vertical
        binding.recycleView.isNestedScrollingEnabled = false
    }
    /**
     * Set observer
     */
    private fun setObserver() {
        viewModel.poModelResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                        user.data?.results?.let {
                            Log.e("myTagRepsnse",user.data?.results?.toString())
                            if (page == 1) {
                                allList.clear()
                                allList.addAll(user.data.results)
                                numPages = user.data.numPages!!
                                setupPurchaseOrderList()
                                setLoadMoreData()
                            } else {
                                allList.addAll(user.data.results)
                                binding.progressBar.visibility = View.GONE
                                adapter.setData(allList)
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                    binding.progressBar.visibility = View.GONE
                    Utility.hideKeyboard(activity, view!!)
                    it?.let { it1 ->
                        ViewUtils.snackBar(activity, it1.message!!, view!!)
                    }
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    binding.progressBar.visibility = View.GONE
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity, getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }

    private fun setLoadMoreData() {
        binding.stickyScrollview.setOnScrollChangeListener { v: NestedScrollView,
                                                             _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1)
                        .measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    page += 1
                    if (page < numPages) {
                        binding.progressBar.visibility = View.VISIBLE
                        loadApis(false)
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupPurchaseOrderList() {
        adapter = PoModelAdapter(
            activity,
            allList,
            object : PoModelAdapter.ViewHolderClicks {
                override fun onClickItem(position: Int) {
                }
            })

        binding.recycleView.adapter = adapter
        binding.progressBar.visibility = View.GONE


        if (allList.isNotEmpty()) {
            binding.txtNoRecord.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
        } else {
            binding.txtNoRecord.visibility = View.VISIBLE
            binding.recycleView.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    fun loadApis(flag: Boolean) {
        val params: MutableMap<String, String> = HashMap()
        viewModel.getPOData(params, flag)
    }
    private fun checkCameraPermission(myView: View?) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Utility.hideKeyboard(context!!, myView!!)
                /**
                 * clear the search text
                 */
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