package com.commoncents.ui.dashboard.quickscan.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatEditText
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
import com.commoncents.ui.dashboard.quickscan.adapter.PurchaseOrderListAdapter
import com.commoncents.ui.dashboard.quickscan.model.ResultsItem
import com.commoncents.ui.dashboard.quickscan.viewmodel.SearchPOViewModel
import com.commoncents.utils.Constants
import com.commoncents.utils.Logger
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.google.android.material.chip.Chip
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.text.SimpleDateFormat
import java.util.*


class QuickscanFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentQuickscanBinding
    private val viewModel: SearchPOViewModel by viewModels()
    private lateinit var activity: DashboardActivity
    private lateinit var adapter: PurchaseOrderListAdapter
    private var sortVal = false

    private var searchText: StringBuilder = StringBuilder()

    private var fromScan = false
    private var poNumber = ""
    private var page = 1
    private var numPages = 1

    private var sortName = false
    private var sortSku = false
    private var sortQty = false
    private var sortSName = false

    private var orderBy = ""
    private val allList: ArrayList<ResultsItem?> = ArrayList()
    var vertical: LinearLayoutManager? = null


    private var isSingle = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuickscanBinding.inflate(inflater, container, false)
        setupLayoutManager()
        setObserver()
        loadApis(true)
        setGenericSearch()
        searchListener(binding.filterView.edipo)
        setListeners()
        setupSearchList()
        return binding.root
    }

    private fun setupLayoutManager() {
        vertical = LinearLayoutManager(activity)
        vertical!!.orientation = LinearLayoutManager.VERTICAL
        binding.recycleView.layoutManager = vertical
        binding.recycleView.isNestedScrollingEnabled = false
    }

    private fun setGenericSearch() {
        binding.filterView.edSearch.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.filterView.edSearch.text.toString().isNotEmpty()) {
                    if (!checkValueExits(binding.filterView.edSearch.text.toString())) {
                        searchText.append(binding.filterView.edSearch.text.toString()).append(",")
                        page = 1
                        fromScan = true
                        setupSearchList()
                        loadApis(true)
                    } else {
                        ViewUtils.snackBar(
                            activity,
                            context!!.getString(R.string.already),
                            view!!
                        )
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.filterView.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (searchText.toString().isEmpty()) {
                    fromScan = false
                    binding.filterView.chipsSearch.removeAllViews()
                    page = 1
                }
            }
        })
    }

    private fun checkValueExits(text: String): Boolean {
        val split = searchText.split(",".toRegex()).toTypedArray()
        var isExits = false
        for (i in split.indices) {
            if (split[i] != text) continue
            isExits = true
            break
        }
        return isExits
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigation.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.PrefKeys.qrCode)
            ?.observe(viewLifecycleOwner) {
                if (it.toString().isNotEmpty()) {
                    if (!checkValueExits(it.toString())) {
                        searchText.append(it.toString()).append(",")
                        page = 1
                        fromScan = true
                        setupSearchList()
                        loadApis(true)
                    } else {
                        ViewUtils.snackBar(activity, context!!.getString(R.string.already), view)
                    }
                }
            }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setLoadMoreData() {
        /*binding.recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var pastVisiblesItems = 0
            var visibleItemCount = 0
            var totalItemCount = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = vertical!!.childCount
                totalItemCount = vertical!!.itemCount
                pastVisiblesItems = vertical!!.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if (visibleItemCount + pastVisiblesItems == allList.size && page < numPages) {
                        if (binding.progressBar.visibility == View.GONE) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount - 1 && page < numPages) {
                        page += 1
                        if (page < numPages) {
                            loadApis(false)
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        })
*/

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


    private fun searchListener(editText: AppCompatEditText) {
        val handler = Handler(Looper.getMainLooper())
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int,
                count: Int
            ) {
                handler.removeCallbacksAndMessages(null)
            }

            override fun afterTextChanged(s: Editable) {
                poNumber = s.toString()

                if (poNumber.isEmpty()) {
                    page = 1
                }
                handler.postDelayed({
                    loadApis(true)
                }, Constants.DELAY)
            }
        })

        editText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                page = 1
                Utility.hideKeyboard(activity, editText)
                loadApis(true)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun setListeners() {
        binding.filterView.llFilter.setOnClickListener(this)
        binding.filterView.rlQrCode.setOnClickListener(this)
        binding.filterView.scrollview.setOnClickListener(this)
        binding.filterView.txtSelectDate.setOnClickListener(this)
        binding.filterView.imgClose.setOnClickListener(this)

        binding.txtSkuNo.setOnClickListener(this)
        binding.txtQty.setOnClickListener(this)
        binding.txtPoNo.setOnClickListener(this)
        binding.txtSupplierName.setOnClickListener(this)

        activity.hideUnHideUnKnowPO(false)
        activity.getUnKnowTextView().setOnClickListener {
            movePrintPackID(true, "", "")
        }
    }

    private fun movePrintPackID(flag: Boolean, poNumber: String, poName: String) {
        val bundle = Bundle()
        bundle.putString(Constants.PO_NUMBER, poNumber)
        bundle.putString(Constants.PO_NAME, poName)
        bundle.putBoolean(Constants.IS_UN_KNOW, flag)
        bundle.putBoolean(Constants.IS_SINGLE, isSingle)
        navigation.navigate(R.id.bottomSheetPrintPackID, bundle)
    }

    private fun setupPurchaseOrderList() {
        adapter = PurchaseOrderListAdapter(
            activity,
            allList,
            object : PurchaseOrderListAdapter.ViewHolderClicks {
                override fun onClickItem(position: Int) {
                    movePrintPackID(
                        false, allList[position]!!.id!!.toString(),
                        allList[position]!!.name.toString()
                    )
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment QuickscanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            QuickscanFragment().apply {}
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.llFilter -> {
                if (binding.filterView.llSearchview.visibility == View.VISIBLE) {
                    binding.filterView.llSearchview.visibility = View.GONE
                    binding.filterView.imgArrow.rotation = 180f
                } else {
                    binding.filterView.llSearchview.visibility = View.VISIBLE
                    binding.filterView.imgArrow.rotation = 0f
                }
            }

            R.id.imgClose -> {
                binding.filterView.txtSelectDate.text = ""
                binding.filterView.imgClose.visibility = View.GONE
                loadApis(true)
            }
            R.id.txtSelectDate -> {
                showDatePicker()
            }
            R.id.scrollview -> {
                // binding.scrollview.visibility = View.GONE
            }
            R.id.rlQrCode -> {
                checkCameraPermission(view)
            }
            R.id.txtPoNo -> {
                if (::adapter.isInitialized) {
                    changeDrawableIcon(sortName, binding.txtPoNo)
                    sortName = !sortName
                    sortVal = sortName
                    orderBy = getString(R.string.name)
                    page = 1
                    loadApis(true)
                }
            }
            R.id.txtSkuNo -> {
                if (::adapter.isInitialized) {
                    changeDrawableIcon(sortSku, binding.txtSkuNo)
                    sortSku = !sortSku
                    sortVal = sortSku
                    orderBy = ""
                    page = 1
                    loadApis(true)
                }
            }
            R.id.txtQty -> {
                if (::adapter.isInitialized) {
                    changeDrawableIcon(sortQty, binding.txtQty)
                    sortQty = !sortQty
                    sortVal = sortQty
                    orderBy = ""
                    page = 1
                    loadApis(true)
                }
            }
            R.id.txtSupplierName -> {
                if (::adapter.isInitialized) {
                    changeDrawableIcon(sortSName, binding.txtSupplierName)
                    sortSName = !sortSName
                    sortVal = sortSName
                    orderBy = getString(R.string.sname)
                    page = 1
                    loadApis(true)
                }
            }
        }
    }

    private fun changeDrawableIcon(flag: Boolean, textView: TextView) {
        if (flag) {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_ascending,
                0
            )
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_descending,
                0
            )
        }
    }

    /**
     * open camera for scan QR code
     */

    private fun checkCameraPermission(myView: View?) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Utility.hideKeyboard(context!!, myView!!)
                /**
                 * clear the search text
                 */
                //clearSearch()
                navigation.navigate(R.id.action_qrcode)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }
        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(Manifest.permission.CAMERA,
                Manifest.permission.CAMERA).check()
    }

    private fun clearSearch() {
        navigation.currentBackStackEntry?.savedStateHandle?.set(
            Constants.PrefKeys.qrCode,
            ""
        )
        binding.filterView.spacerTop.visibility = View.GONE
        binding.filterView.spacerBottom.visibility = View.GONE
        binding.filterView.scrollview.visibility = View.GONE
        binding.filterView.chipsSearch.visibility = View.GONE
        binding.filterView.chipsSearch.removeAllViews()
        fromScan = false
        searchText.clear().append("")
        binding.filterView.edSearch.setText("")
    }

    private fun setupSearchList() {
        if (searchText.isNotEmpty()) {
            binding.filterView.chipsSearch.removeAllViews()

            binding.filterView.llSearchview.visibility = View.VISIBLE
            binding.filterView.scrollview.visibility = View.VISIBLE
            binding.filterView.chipsSearch.visibility = View.VISIBLE
            binding.filterView.spacerTop.visibility = View.VISIBLE
            binding.filterView.spacerBottom.visibility = View.VISIBLE


            var substring = searchText.substring(0, searchText.length - 1)
            var split = substring.split(",".toRegex()).toTypedArray()
            val clearAllText: String

            if(split.size>1){
                clearAllText = getString(R.string.clearAll) + "," + searchText
                searchText = StringBuilder(clearAllText)
                substring = searchText.substring(0, searchText.length - 1)
                split = substring.split(",".toRegex()).toTypedArray()
            }
            val result: Array<String> =
                Arrays.stream(split).distinct().toArray { arrayOfNulls<String>(it) }

            split = result
            searchText.clear()

            for (i in split.indices) {
                searchText.append(split[i]).append(",")
            }

            substring = searchText.substring(0, searchText.length - 1)
            split = substring.split(",".toRegex()).toTypedArray()


            if (split.isNotEmpty()) {
                for (i in split.indices) {
                    if (split[i] == getString(R.string.clearAll)) {
                        val inflater = LayoutInflater.from(context)
                        val newChip = inflater.inflate(
                            R.layout.row_clearall,
                            null, false
                        ) as Chip

                        newChip.setTextAppearanceResource(R.style.ChipStyleOrange)
                        binding.filterView.chipsSearch.addView(newChip)
                        newChip.setOnClickListener {
                            clearSearch()
                            loadApis(true)
                        }
                    } else {
                        val inflater = LayoutInflater.from(context)
                        val newChip = inflater.inflate(
                            R.layout.row_searchitem,
                            null, false
                        ) as Chip
                        newChip.text = split[i]
                        newChip.setTextAppearanceResource(R.style.ChipStyle)
                        binding.filterView.chipsSearch.addView(newChip)

                        newChip.setOnCloseIconClickListener {
                            Logger.e("+++", "+++" + newChip.text)
                            page = 1
                            binding.filterView.chipsSearch.removeView(newChip)
                            if (binding.filterView.chipsSearch.childCount == 0) {
                                clearSearch()
                                loadApis(true)
                            } else {
                                deleteSpannable(split[i] + ",")
                                loadApis(true)
                            }
                        }
                    }
                }
            }
        }
        binding.filterView.edSearch.setText("")
    }


    private fun deleteSpannable(string: String) {
        val start = searchText.indexOf(string)
        if (start < 0) {
            clearSearch()
            loadApis(true)
            return
        }
        searchText = searchText.delete(start, start + string.length)

        if (searchText.isNotEmpty()) {
            if(searchText.toString()==getString(R.string.clearAll)+","){
                clearSearch()
                loadApis(true)
            }else{
                val substring = searchText.substring(0, searchText.length - 1)
                searchText = StringBuilder(substring)
            }
        } else {
            clearSearch()
            loadApis(true)
        }
    }

    /**
     * Show date-picker
     */

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.filterView.txtSelectDate.text = sdf.format(cal.time)

                loadApis(true)
                binding.filterView.imgClose.visibility = View.VISIBLE
            }

        val dialog = DatePickerDialog(
            activity, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }


    /**
     * Set observer
     */
    private fun setObserver() {
        viewModel.searchPOResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                        user.data?.results?.let {
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

    /**
     * API calling
     */


    fun loadApis(flag: Boolean) {
        /*  var substring = ""

          substring = if (searchText.isNotEmpty()) {
              searchText.substring(0, searchText.length - 1)
          } else {
              searchText.toString()
          }*/

        val params: MutableMap<String, String> = HashMap()

        if (poNumber.isNotEmpty()) {
            params[Constants.apiKeys.P_NAME] = poNumber
        }
        if (searchText.isNotEmpty()) {
            params[Constants.apiKeys.UPC] = searchText.toString()
        }
        if (orderBy.isNotEmpty()) {
            params[Constants.apiKeys.ORDER_BY] = orderBy
        }
        if (binding.filterView.txtSelectDate.text.toString().isNotEmpty()) {
            params[Constants.apiKeys.PO_DATE] = binding.filterView.txtSelectDate.text.toString()
        }

        params[Constants.apiKeys.PAGE] = page.toString()
        params[Constants.apiKeys.ASC] = sortVal.toString()
        //params[Constants.apiKeys.RECENT] = true.toString()
        //
        viewModel.searchPO(params, flag)
    }
}