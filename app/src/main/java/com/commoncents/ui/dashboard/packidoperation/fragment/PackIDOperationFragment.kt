package com.commoncents.ui.dashboard.packidoperation.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentPackidoperationBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.packidoperation.adapter.PackIDAdapter
import com.commoncents.ui.dashboard.packidoperation.model.ResultsItem
import com.commoncents.ui.dashboard.packidoperation.viewModel.DeleteImageVM
import com.commoncents.ui.dashboard.packidoperation.viewModel.DeletePackIDVM
import com.commoncents.ui.dashboard.packidoperation.viewModel.PackIDOperationVM
import com.commoncents.utils.Constants
import com.commoncents.utils.Logger
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.commoncents.utils.generatepdf.model.FailureResponse
import com.commoncents.utils.generatepdf.model.SuccessResponse
import com.commoncents.utils.generatepdf.pdf.PdfGenerator
import com.commoncents.utils.generatepdf.pdf.PdfGeneratorListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [PackIDOperationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class PackIDOperationFragment : BaseFragment(), View.OnClickListener  {
    lateinit var binding: FragmentPackidoperationBinding
    private lateinit var activity: DashboardActivity
    private lateinit var packIDAdapter: PackIDAdapter
    private var page = 1
    private var numPages = 1
    private val allList: java.util.ArrayList<ResultsItem?> = java.util.ArrayList()
    private val viewModel: PackIDOperationVM by viewModels()
    private val deletePackIdModel: DeletePackIDVM by viewModels()
    private val checkedPackIdList  = ArrayList<String>()
    var packIdstr:String = ""
    var poStr:String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackidoperationBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        initView()
        setListeners()
        setPackIDListingObserver()
        deletePackIdOberserver()
        loadApis("")
        searchListener(binding.fragmentPackidOperationAcet)
        searchPOListener(binding.fragmentPackidOperationPoAcet)
        return binding.root
    }

    private fun setListeners() {
        binding.fragmentPackidOperationRl.setOnClickListener(this)
        binding.packIdOprationDeleteAcb.setOnClickListener(this)
        binding.fragmentPackidOperationDateActv.setOnClickListener(this)
        binding.fragmentPackidOperationCloseIv.setOnClickListener(this)
        binding.fragmentPackidOperationRecentSc.setOnClickListener(this)
        binding.fragmentPackidOperationUnknownpoSc.setOnClickListener(this)
        binding.fragmentOperationPackidScannerRl.setOnClickListener(this)
        binding.fragmentOperationSearchpoScannerRl.setOnClickListener(this)
        //
        binding.fragmentPackidOperationUnknownpoSc.setOnCheckedChangeListener{
                buttonView,isChecked ->
            page = 1
            numPages = 1
            loadApis(binding.fragmentPackidOperationAcet.text.toString())
        }
        binding.fragmentPackidOperationRecentSc.setOnCheckedChangeListener{
            buttonView,isChecked ->
            page = 1
            numPages = 1
            loadApis(binding.fragmentPackidOperationAcet.text.toString())
        }
    }

    private fun initView() {
       // binding.fragmentPackidOperationLl.visibility = View.GONE
        //checkPermissions()
    }
    private val navigation: NavController by lazy {
        findNavController()
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
            PackIDOperationFragment().apply {}
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigation.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.PrefKeys.qrCode)
            ?.observe(viewLifecycleOwner) {
                if (it.toString().isNotEmpty()) {
                    Log.e("myTag"," "+it.toString())
                    Toast.makeText(activity,it.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        super.onViewCreated(view, savedInstanceState)
    }
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
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.CAMERA).check()
    }
    private fun setupSearchData() {

    }
    //
    private fun loadDeletePackIdAPI(){
        val jsonObject = JSONObject()
        val jsonArrayObj = JSONArray()
        //
        checkedPackIdList.forEach {
            Log.e("myTag"," = "+it)
            jsonArrayObj.put(it)
        }
        //
        jsonObject.put("pack_ids", jsonArrayObj)
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        Log.e("mYTag","jsonObjectString == "+jsonObjectString)
        deletePackIdModel.deletePackIdData(jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull()))
    }

    //
    private fun loadApis(packid:String) {
        val params: MutableMap<String, String> = HashMap()
        params["order_by"] = "pack_id"
        if(packid !== "" && !packid.isEmpty()){
            params["pack_ids"] = packid
        }
        if(binding.fragmentPackidOperationPoAcet.text.toString() !== ""
            && !binding.fragmentPackidOperationPoAcet.text!!.isEmpty()){
            params["po_name"] = binding.fragmentPackidOperationPoAcet.text.toString()
        }
        if(binding.fragmentPackidOperationDateActv.text.toString() !== ""
            && !binding.fragmentPackidOperationDateActv.text.isEmpty()){
            params["created_at"] = binding.fragmentPackidOperationDateActv.text.toString()
        }
        if(binding.fragmentPackidOperationUnknownpoSc.isChecked){
            params["unknown"] = binding.fragmentPackidOperationUnknownpoSc.isChecked.toString()
        }
        params["recent"] = binding.fragmentPackidOperationRecentSc.isChecked.toString()
        params[Constants.apiKeys.PAGE] = page.toString()
        //
        viewModel.packIDListing(params)
    }
    private fun searchListener(editText: AppCompatEditText) {
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
                Log.e("myTagET",s.toString())
                packIdstr = s.toString()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e("myTagET", "True is called"+packIdstr)
                page = 1
                numPages = 1
                loadApis(packIdstr)
                return@OnEditorActionListener true
            }
            false
        })
    }
    private fun searchPOListener(editText: AppCompatEditText) {
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
                Log.e("myTagET",s.toString())
                poStr = s.toString()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e("myTagET", "True is called"+poStr)
                page = 1
                numPages = 1
                loadApis(binding.fragmentPackidOperationAcet.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }
    private fun checkPermissions(packID:String) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                generatePDF(packID)

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }
        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).check()
    }
    private fun generatePDF(packID:String) {
        //
        val pdfName = System.currentTimeMillis().toString() + "_" + getString(R.string.onebyone)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //
        val viewList: MutableList<View> = ArrayList()
        val content: View = inflater.inflate(R.layout.row_pdf_item, null)
        val textView = content.findViewById<TextView>(R.id.txtID)
        val imgView = content.findViewById<ImageView>(R.id.imgView)
        //
        textView.text = packID
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                packID, BarcodeFormat.CODE_128,
                Constants.BAR_CODE_WIDTH, Constants.BAR_CODE_HEIGHT
            )
            imgView.setImageBitmap(Utility.createBitmap(bitMatrix))
        } catch (e: WriterException) {
            Logger.e(Logger.TAG, "" + e.message)
        }
        //
        viewList.add(content)
        //
        PdfGenerator.getBuilder()
            .setContext(activity)
            .fromViewSource()
            .fromViewList(viewList)
            .setFileName(pdfName)
            .setFolderNameOrPath("PackIDs/PDF/")
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse) {
                    Log.e("myTag","failureResponse"+failureResponse.errorMessage)
                    super.onFailure(failureResponse)
                }

                override fun onStartPDFGeneration() {
                    Log.e("myTag","onStartPDFGeneration")
                }

                override fun onFinishPDFGeneration() {
                    Log.e("myTag","onFinishPDFGeneration")
                }

                override fun showLog(log: String) {
                    super.showLog(log)
                }

                override fun onSuccess(response: SuccessResponse) {
                    Log.e("myTag","pdfName")
//                    val msg = context!!.getString(R.string.packIDGenerated)
//                    SuccessDialog(
//                        context!!,
//                        Constants.Single,
//                        "",
//                        "$packID $msg",
//                        false
//                    ) { _: Boolean, _: String ->
//                        openPDF(response.path)
//                    }.show()
                    openPDF(response.path)
//                    val msg = context!!.getString(R.string.packIDGenerated)
//                    SuccessDialog(
//                        context!!,
//                        Constants.Single,
//                        "",
//                        "$packID $msg",
//                        false
//                    ) { _: Boolean, _: String ->
//                        openPDF()
//                    }.show()
                    super.onSuccess(response)
                }
            })
    }
    private fun openPDF(pdfName:String) {
        try {
            val file = File(pdfName)
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
    /**
     * Set PackIDListingObserver
     */
    private fun setPackIDListingObserver() {
        viewModel.packIDListingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { user ->
                        user.data?.results?.let {
                            //val listOfData = user.data.results!!
                            //setupSearchData(user.data.results?)
                            Log.e("MyTagOperation",user.data?.results.toString())

                            if (page == 1) {
                                allList.clear()
                                allList.addAll(user.data.results!!)
                                numPages = user.data.numPages!!
                                //
                                packIDAdapter = PackIDAdapter(
                                    activity!!,
                                    allList,
                                    object : PackIDAdapter.ViewHolderClicks {
                                        override fun onClickItem(resultsItem: ResultsItem) {
                                            //Log.e("myTagClick","Click Event happen")
                                            //fragment_packid_operation_header
                                            page = 1
                                            numPages = 1
                                            val bundle = Bundle()
                                            bundle.putString("scan_id",resultsItem.id.toString());
                                            //bundle.putStringArrayList("photoData", images);
                                            navigation.navigate(R.id.packIdImageFragment,bundle)
                                            //arguments?.getString("")
                                        }

                                        override fun onLongPressClick() {
                                            Log.e("myTag","onLong Press Click")
                                            binding.packIdOprationDeleteLl.visibility = View.VISIBLE
                                        }

                                        override fun onPDfCLick(packID: String) {
                                            //Toast.makeText(activity,packID,Toast.LENGTH_SHORT).show()
                                            //checkPermissions()
                                            checkPermissions(packID)
                                        }

                                        override fun onPackIdChecked(packID: String) {
                                            //TODO("Not yet implemented")
                                            if(checkedPackIdList.contains(packID)){
                                                checkedPackIdList.remove(packID)
                                            }else{
                                                checkedPackIdList.add(packID)
                                            }
                                        }
                                    })
                                binding.fragmentPackidOperationHeader.visibility = View.VISIBLE
                                binding.recycleViewPackIdOpration.layoutManager = LinearLayoutManager(activity)
                                binding.recycleViewPackIdOpration.adapter = packIDAdapter
                                //
                                setLoadMoreData()
                            } else {
                                allList.addAll(user.data.results!!)
                                packIDAdapter.setData(allList)
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity, getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }

    /**
     * Delete pack Id observer
     */
    private fun deletePackIdOberserver(){
        deletePackIdModel.deletePackIdResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { user ->
                        Log.e("myTag","message ==> "+ user.data?.message)
                        page = 1
                        numPages = 1
                        binding.packIdOprationDeleteLl.visibility = View.GONE
                        allList.clear()
                        loadApis("")
                        //                binding.packIdOprationDeleteLl.visibility = View.GONE
                        //                packIDAdapter.resetAdapterData()
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity, getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
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
                binding.fragmentPackidOperationDateActv.text = sdf.format(cal.time)
                page = 1
                numPages = 1
                loadApis(binding.fragmentPackidOperationAcet.text.toString())
                binding.fragmentPackidOperationCloseIv.visibility = View.VISIBLE
            }

        val dialog = DatePickerDialog(
            activity, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }
    //
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgInfo -> {

            }
            R.id.fragment_packid_operation_rl -> {
                if (binding.fragmentPackidOperationLl.visibility == View.VISIBLE) {
                    binding.fragmentPackidOperationLl.visibility = View.GONE
                    binding.fragmentPackidOperationAciv.rotation = 180f
                } else {
                    binding.fragmentPackidOperationLl.visibility = View.VISIBLE
                    binding.fragmentPackidOperationAciv.rotation = 0f
                }
            }
            R.id.pack_id_opration_delete_acb -> {
                Log.e("myTag","checkedPackIdList ==> "+checkedPackIdList.toString())
                loadDeletePackIdAPI()

            }
            R.id.fragment_packid_operation_date_actv -> {
                showDatePicker()
            }
            R.id.fragment_packid_operation_close_iv ->{
                binding.fragmentPackidOperationDateActv.text = ""
                binding.fragmentPackidOperationCloseIv.visibility = View.GONE
            }
            R.id.fragment_packid_operation_unknownpo_sc ->{
                binding.fragmentPackidOperationUnknownpoSc.isChecked = !binding.fragmentPackidOperationUnknownpoSc.isChecked
            }
            R.id.fragment_packid_operation_recent_sc ->{
                binding.fragmentPackidOperationRecentSc.isChecked = !binding.fragmentPackidOperationRecentSc.isChecked
            }
            R.id.fragment_operation_packid_scanner_rl->{
                checkCameraPermission(view)
            }
            R.id.fragment_operation_searchpo_scanner_rl->{
                checkCameraPermission(view)
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
                    if (page <= numPages) {
                        loadApis(binding.fragmentPackidOperationAcet.text.toString())
                    } else {
                    }
                }
            }
        }
    }


}