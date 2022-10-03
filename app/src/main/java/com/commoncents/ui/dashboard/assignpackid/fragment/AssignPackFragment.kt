package com.commoncents.ui.dashboard.assignpackid.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentPacklocationBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.assignbrokenpallet.adapter.PrintPackIDAdapter
import com.commoncents.ui.dashboard.assignbrokenpallet.model.ResultsItem
import com.commoncents.ui.dashboard.assignbrokenpallet.viewmodel.PrintPackIDViewModel
import com.commoncents.ui.dashboard.packidoperation.adapter.PackIDAdapter
import com.commoncents.utils.Constants
import com.commoncents.utils.Logger
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.commoncents.view.dialog.SuccessDialog
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.io.StringReader


class AssignPackFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentPacklocationBinding
    private val viewModel: PrintPackIDViewModel by viewModels()
    private lateinit var adapter: PrintPackIDAdapter
    private lateinit var activity: DashboardActivity
    private lateinit var packIDAdapter: PackIdAdapter
    private val packIdList = ArrayList<String>()
    private var packID: String = ""
    private var fileName: String = ""
    private var isSingle: Boolean = false
    private var isUnKnow: Boolean = true


    private var locationID: String = ""
    private var poName: String = ""
    private var poNumber: String = ""

    private var isLocation: Boolean = true
    private var resultsList: ArrayList<ResultsItem?> = ArrayList()

    private var packIDText: StringBuilder = StringBuilder()

    private var isFirst = true

    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigation.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.PrefKeys.qrCode)
            ?.observe(viewLifecycleOwner) {
                if (isLocation) {
                    if (it.toString().isNotEmpty()) {
                        if (!checkValueExits(it.toString())) {
                            locationID = it.toString()
                            binding.includeAssignLocation.includePackID.edLocation.setText(it.toString())
                        }else{
                            ViewUtils.snackBar(activity, context!!.getString(R.string.already), view)
                        }
                    }
                } else {
                    if (it.toString().isNotEmpty()) {
                        if (!checkValueExits(it.toString())) {
                            packID = it.toString()
                            setupPackIDList()
                            binding.includeAssignLocation.includePackID.edPackID.setText(packIDText)
                        }else{
                            ViewUtils.snackBar(activity, context!!.getString(R.string.already), view)
                        }
                    }
                }

                if (isSingle) {
                    binding.includeAssignLocation.includePackID.llPackID.visibility = View.GONE
                }else{
                    binding.includeAssignLocation.includePackID.llPackID.visibility = View.VISIBLE
                }
            }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPacklocationBinding.inflate(inflater, container, false)
        setPackIDListingObserver()
        setAssignObserver()
        setValidateLocationObserver()
        setListeners()
        readPackID()
        activity.hideUnHideUnKnowPO(false)

        return binding.root
    }


    private fun readPackID() {
        arguments?.getBoolean(Constants.IS_SINGLE)?.let {
            isSingle = it
        }

        arguments?.getBoolean(Constants.IS_UN_KNOW)?.let {
            isUnKnow = it
        }

        arguments?.getString(Constants.PO_NAME)?.let {
            poName = it
        }


        arguments?.getString(Constants.FILE_NAME)?.let {
            fileName = it
        }



        arguments?.getString(Constants.PO_NUMBER)?.let {
            poNumber = it
        }

        arguments?.getString(Constants.apiKeys.PACK_ID)?.let {
            packID = it
            if(isFirst) {
                packIDText.append(packID)
            }
            if (false) {
                binding.includeAssignLocation.includePackID.txtTitle.text =
                    context!!.getString(R.string.inputLocation) + " " + packID
            } else {
                //Log.e("myTag","packID==> "+packID)
                //layout_packid_sv
                binding.includeAssignLocation.includePackID.layoutPackidSv.visibility = View.VISIBLE
                binding.includeAssignLocation.includePackID.txtTitle.text =
                    context!!.getString(R.string.choosePackIDandLocation)

                binding.includeAssignLocation.includePackID.layoutPackidSvTv.text = packID.dropLast(1)
                //
                //Log.e("myTag"," === "+packID.dropLast(1).split(",").toString())
                //
                packIDAdapter = PackIdAdapter(
                    activity,
                    packID.dropLast(1).split(","),
                    object : PackIdAdapter.PackIdHolderClickEvent {
                        override fun onClickItem(packId: String) {
                            if(packIdList.contains(packId)){
                                packIdList.remove(packId)
                            }else{
                                packIdList.add(packId)
                            }
                        }
                    })
                binding.packIdLocationRv.layoutManager = LinearLayoutManager(activity)
                binding.packIdLocationRv.adapter = packIDAdapter
                //
            }
        }

        if (arguments != null) {
            if (isFirst) {
                isFirst = false
                if (false) {
                    binding.includeAssignLocation.includePackID.llPackID.visibility = View.GONE
                    val msg = context!!.getString(R.string.packIDGenerated)
                    SuccessDialog(
                        context!!,
                        Constants.Single,
                        "",
                        "$packID $msg",
                        false
                    ) { _: Boolean, _: String ->
                        openPDF()
                    }.show()
                } else {
                    val substring = packID.substring(0, packID.length - 1)
                    val split = substring.split(",")

                    for (i in split.indices) {
                        val resultsItem = ResultsItem()
                        resultsItem.packId = split[i]
                        resultsList.add(resultsItem)
                    }

                    val msg =
                        context!!.getString(R.string.wehave) + " " + split.size + " " + context!!.getString(
                            R.string.starting
                        ) +
                                " " +
                                split[0] + " to " + split[split.size - 1] + " " +
                                context!!.getString(
                                    R.string
                                        .view
                                )
                    //binding.includeAssignLocation.includePackID.llPackID.visibility = View.VISIBLE
                    SuccessDialog(context!!, Constants.All, "",msg, false) {
                            _: Boolean, _: String ->
                        openPDF()
                    }.show()
                    setupAutoCompletePackIDListing()
                    textChangeListener()
                }
            }
        } else {
            loadApis()
        }
    }


    private fun openPDF() {
        try {
            val file = File(fileName)
            if (file.exists()) {
                val path = FileProvider.getUriForFile(
                    context!!,
                    context!!.packageName + ".provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(path, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                try {
                    context!!.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    ViewUtils.snackBar(activity, e.message.toString(), view!!)
                }
            } else {
                ViewUtils.snackBar(
                    activity,
                    "PDF file is not existing in storage. Your Generated path is" + file.path,
                    view!!
                )
            }
        } catch (exception: Exception) {
            ViewUtils.snackBar(
                activity,
                "Error occurred while opening the PDF. Error message : " + exception.message,
                view!!
            )
        }
    }

    private fun textChangeListener() {
        binding.includeAssignLocation.includePackID.edPackID.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.includeAssignLocation.includePackID.edPackID.text.toString()
                        .isNotEmpty()
                ) {
                    if (!checkValueExits(binding.includeAssignLocation.includePackID.edPackID.text.toString())) {
                        packIDText.append(binding.includeAssignLocation.includePackID.edPackID.text.toString())
                            .append(",")
                        setupPackIDList()
                    }else{
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
    }


    private fun checkValueExits(text: String): Boolean {
        val split = packIDText.split(",".toRegex()).toTypedArray()
        var isExits = false
        for (i in split.indices) {
            if (split[i] != text) continue
            isExits = true
            break
        }
        return isExits
    }


    private fun setupPackIDList() {
        if (packIDText.isNotEmpty()) {

            binding.includeAssignLocation.includePackID.chipsSearch.removeAllViews()

            binding.includeAssignLocation.includePackID.scrollview.visibility = View.VISIBLE
            binding.includeAssignLocation.includePackID.chipsSearch.visibility = View.VISIBLE
            binding.includeAssignLocation.includePackID.spacerTop.visibility = View.VISIBLE
            binding.includeAssignLocation.includePackID.spacerBottom.visibility = View.VISIBLE


            val substring =
                packIDText.substring(0, packIDText.length - 1)
            val split = substring.split(",".toRegex()).toTypedArray()

            for (i in split.indices) {
                val inflater = LayoutInflater.from(context)
                val newChip = inflater.inflate(
                    R.layout.row_assignitem,
                    null, false
                ) as Chip
                newChip.text = split[i]
                newChip.setTextAppearanceResource(R.style.ChipStyle)
                binding.includeAssignLocation.includePackID.chipsSearch.addView(newChip)

                newChip.setOnCloseIconClickListener {
                    binding.includeAssignLocation.includePackID.chipsSearch.removeView(newChip)
                    if (binding.includeAssignLocation.includePackID.chipsSearch.childCount == 0) {
                        clearChipView()
                    } else {
                        deleteSpannable(split[i] + ",")
                    }
                }
            }
        }
        binding.includeAssignLocation.includePackID.edPackID.setText("")
    }


    private fun clearChipView() {
        binding.includeAssignLocation.includePackID.spacerTop.visibility = View.GONE
        binding.includeAssignLocation.includePackID.spacerBottom.visibility = View.GONE
        binding.includeAssignLocation.includePackID.scrollview.visibility = View.GONE
        binding.includeAssignLocation.includePackID.chipsSearch.visibility = View.GONE
        binding.includeAssignLocation.includePackID.chipsSearch.removeAllViews()
    }


    private fun deleteSpannable(string: String) {
        val start = packIDText.indexOf(string)
        if (start < 0) return
        packIDText = packIDText.delete(start, start + string.length)

        val substring = packIDText.substring(0, packIDText.length - 1)
        packIDText = StringBuilder(substring)
    }


    /**
     * Call api for pack id listing
     */

    private fun loadApis() {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.apiKeys.ASC] = true.toString()
        viewModel.packIDListing(params,true)
    }

    /**
     * Set Click listeners
     */

    private fun setListeners() {
//        binding.includeAssignLocation.includePackID.rlLocationQrCode.setOnClickListener(this)
//        binding.includeAssignLocation.includePackID.rlPackIDQrCode.setOnClickListener(this)

//        binding.includeAssignLocation.includeBottom.btnAssign.setOnClickListener(this)
//        binding.includeAssignLocation.includeBottom.txtAssignLater.setOnClickListener(this)

        binding.packIdLocationBottomInclude.btnAssign.setOnClickListener(this)
        binding.packIdLocationBottomInclude.txtAssignLater.setOnClickListener(this)
        binding.packIdLocationScanRl.setOnClickListener(this)
        //
        binding.packIdLocationAcd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.packIdLocationTl.error = null
                locationID = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
//        binding.includeAssignLocation.includePackID.edLocation.addTextChangedListener(object :
//            TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?, start: Int, count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence?, start: Int, before: Int,
//                count: Int
//            ) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                if (s.isNotEmpty()) {
//                    locationID = s.toString()
//                }
//            }
//        })


//        binding.includeAssignLocation.includePackID.edPackID.addTextChangedListener(object :
//            TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?, start: Int, count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence?, start: Int, before: Int,
//                count: Int
//            ) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                if (s.isNotEmpty()) {
//                    packID = s.toString()
//                }
//            }
//        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AssignPackFragment().apply {}
    }

    /**
     * Click listeners
     */


    override fun onClick(v: View?) {
        when (v?.id) {

//            binding.includeAssignLocation.includePackID.rlLocationQrCode.id -> {
//                isLocation = true
//                navigation.navigate(R.id.qrcodeScanFragment)
//            }
//            binding.includeAssignLocation.includePackID.rlPackIDQrCode.id -> {
//                isLocation = false
//                navigation.navigate(R.id.qrcodeScanFragment)
//            }

            binding.packIdLocationScanRl.id ->{
                isLocation = true
                navigation.navigate(R.id.qrcodeScanFragment)
            }

//            binding.includeAssignLocation.includeBottom.txtAssignLater.id -> {
//                navigation.popBackStack()
//            }
//            binding.includeAssignLocation.includeBottom.btnAssign.id -> {
//                if (checkValidation()) {
//                    viewModel.validateLocation(locationID)
//                }
//            }
            binding.packIdLocationBottomInclude.txtAssignLater.id -> {
                navigation.popBackStack()
            }
            binding.packIdLocationBottomInclude.btnAssign.id -> {
                if(packIdList.size > 0 ){
                    viewModel.validateLocation(locationID)
                }else{
                    Toast.makeText(activity,"Please Select pack ID",Toast.LENGTH_SHORT).show()
                }

//                if (checkValidation()) {
//                    viewModel.validateLocation(locationID)
//                }
            }
        }
    }

    private fun assignLocation(){
        val jsonArray = JsonArray()
        val jsonObject = JsonObject()


        packIdList.forEach {
            jsonArray.add(it)
        }

//        if(isSingle){
//            jsonArray.add(packIDText.toString())
//        }else{
//            val substring = packIDText.substring(0, packIDText.length - 1)
//            val split = substring.split(",")
//            for (i in split.indices) {
//                jsonArray.add(split[i])
//            }
//        }
        jsonObject.add("pack_ids", jsonArray)
        jsonObject.addProperty("location_id",locationID)
        viewModel.assignLocation(jsonObject)
    }


    /**
     *  Check validation on login button click
     */

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(locationID.trim())) {
            ViewUtils.snackBar(activity,getString(R.string.enter_enter_location), view!!)
            return false
        }
        if (TextUtils.isEmpty(packID.trim())) {
            ViewUtils.snackBar(activity,getString(R.string.enter_packID), view!!)
            return false
        }
        return true
    }


    /**
     * Set ValidateLocationObserver
     */
    private fun setValidateLocationObserver() {
        viewModel.validateLocationResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                       if(user.data!!.valid!!){
                           assignLocation()
                       }else{
                           binding.packIdLocationTl.error =  getString(R.string.valid_location)
                           binding.packIdLocationTl.errorIconDrawable = null
                           //ViewUtils.snackBar(activity,getString(R.string.valid_location), view!!)
                       }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity,getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }


    /**
     * Set PackIDListingObserver
     */
    private fun setPackIDListingObserver() {
        viewModel.packIDListingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                        user.data?.results?.let {
                            resultsList = ArrayList(user.data.results!!)
                            setupAutoCompletePackIDListing()
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity,getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }

    private fun setupAutoCompletePackIDListing() {
        adapter = PrintPackIDAdapter(
            activity,
            resultsList
        )
        binding.includeAssignLocation.includePackID.edPackID.setAdapter(adapter)
        binding.includeAssignLocation.includePackID.edPackID.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position) as ResultsItem
                packID = selected.packId!!

                if(!isSingle){
                    packIDText.append(packID).append(",")
                }else{
                    packIDText.append(packID)
                }

                Logger.e("selected -->>", "-->>$packIDText")
                setupPackIDList()
            }
    }


    /**
     * Set assign location observer
     */
    private fun setAssignObserver() {
        viewModel.packIDResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                        user.data?.let {
                            SuccessDialog(
                                context!!, Constants.Location,
                                "",
                                context!!.getString(R.string.locationAssign),
                                true
                            ) { _: Boolean, _: String ->
                                navigation.popBackStack()
                            }.show()
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it?.let { it1 ->
                        ViewUtils.snackBar(activity,it1.message!!, view!!)
                    }
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity,getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }
}