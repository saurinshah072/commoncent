package com.commoncents.view.dialog

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.commoncents.R
import com.commoncents.core.BaseBottomSheetDialogFragment
import com.commoncents.databinding.CustomPopupPrintpackidLayoutBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.quickscan.viewmodel.SearchPOViewModel
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


open class PrintPackIDDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    private var packID: String = ""
    private var packIDs: StringBuilder = StringBuilder()

    private var isSingle = false
    private var isUnKnow = true
    private var poNumber = ""
    private var poName = ""
    private var shipmentValue = ""


    private lateinit var binding: CustomPopupPrintpackidLayoutBinding
    private lateinit var activity: DashboardActivity
    private val viewModel: SearchPOViewModel by viewModels()

    private var shipment = arrayOf("S1", "S2", "S3", ".S4", "S5", "S6", "S7", "S8", "S9", ".S10")

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DialogPrint)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomPopupPrintpackidLayoutBinding.inflate(layoutInflater)
        val view = binding.root

        arguments?.getBoolean(Constants.IS_SINGLE)?.let {
            isSingle = it
        }

        arguments?.getString(Constants.apiKeys.PACK_ID)?.let {
            packID = it
        }
        arguments?.getString(Constants.PO_NUMBER)?.let {
            poNumber = it
        }

        arguments?.getString(Constants.PO_NAME)?.let {
            poName = it
        }

        arguments?.getBoolean(Constants.IS_UN_KNOW)?.let {
            isUnKnow = it
            if (isUnKnow) {
                binding.edShipment.visibility = View.VISIBLE
            } else {
                binding.edShipment.visibility = View.GONE
            }
        }

        initView()
        setPrintPackIDObserver()
        setListeners()
        setupShipmentList()
        return view
    }

    private fun setupShipmentList() {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(activity, R.layout.row_shipment, shipment)
        binding.edShipment.threshold = 1
        binding.edShipment.setAdapter(adapter)
        binding.edShipment.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position)
                shipmentValue = selected.toString()
            }
    }

    private fun initView() {
        //
        binding.included.imgView.visibility = View.VISIBLE
        //
        binding.popupPrintIdAcet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.e("myTagET", "True is called"+p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.popupPrintIdAcet.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e("myTagET", "True is called")
                 true
            }
            false
        })
    }

    private val navigation: NavController by lazy {
        findNavController()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    private fun setListeners() {
        binding.included.imgView.setOnClickListener(this)
        binding.btnPrint.setOnClickListener(this)
        binding.llAllTogether.setOnClickListener(this)
        binding.llRadioOne.setOnClickListener(this)
        binding.llRadioAllTogether.setOnClickListener(this)
        binding.included.imgBack.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgView -> {
                viewPackIDScreen()
            }
            R.id.imgBack -> {
                dismiss()
            }
            R.id.btnPrint -> {

                //Log.e("myTag","poNumber "+ isUnKnow);
                if(isUnKnow){
                  //  Log.e("myTag","poNumber "+ shipmentValue);
                    Log.e("myTag"," == "+binding.edShipment.text.toString())
                    if (binding.edShipment.text.toString().isNotEmpty()) {
                        setApiName(
                            Constants.printPackIDUnKnowAll,
                            binding.popupPrintIdAcet.text.toString(),
                            ""
                        )
                    }else{
                        Log.e("myTag","poNumber "+ isUnKnow);
                    }
                }else{
                    setApiName(
                        Constants.printPackIDALL,
                        binding.popupPrintIdAcet.text.toString(),
                        poNumber
                    )
                }
//                    setApiName(
//                        Constants.printPackIDALL,
//                        binding.popupPrintIdAcet.text.toString(),
//                        poNumber
//                    )
//                if (binding.popupPrintIdAcet.text!!.isEmpty()) {
//
//                }else{
//                    setApiName(
//                        Constants.printPackIDALL,
//                        binding.popupPrintIdAcet.text.toString(),
//                        poNumber
//                    )
//                }

//                if (isSingle) {
//                    if (isUnKnow) {
//                        if (shipmentValue.isNotEmpty()) {
//                            setApiName(
//                                Constants.printPackIDUnKnowSingle,
//                                binding.edNoOfPackID.text.toString(),
//                                ""
//                            )
//                        } else {
//                            ViewUtils.snackBar(
//                                activity,
//                                context!!.getString(R.string.pleaseselect),
//                                dialog!!.window!!.decorView,
//                            )
//                        }
//                    } else {
//                        setApiName(
//                            Constants.printPackID,
//                            binding.edNoOfPackID.text.toString(),
//                            poNumber
//                        )
//                    }
//                } else {
//                    if (binding.edNoOfPackID.text!!.isEmpty()) {
//                        ViewUtils.snackBar(
//                            activity,
//                            context!!.getString(R.string.pleaseEnterNo),
//                            dialog!!.window!!.decorView,
//                        )
//                    } else {
//                        if (isUnKnow) {
//                            if (shipmentValue.isNotEmpty()) {
//                                setApiName(
//                                    Constants.printPackIDUnKnowAll,
//                                    binding.edNoOfPackID.text.toString(),
//                                    ""
//                                )
//                            } else {
//                                ViewUtils.snackBar(
//                                    activity,
//                                    context!!.getString(R.string.pleaseselect),
//                                    dialog!!.window!!.decorView,
//                                )
//                            }
//                        } else {
//                            setApiName(
//                                Constants.printPackIDALL,
//                                binding.edNoOfPackID.text.toString(),
//                                poNumber
//                            )
//                        }
//                    }
//                }
            }
            R.id.llRadioOne -> {
                isSingle = true
                binding.imgAll.setImageResource(R.drawable.ic_radio)
                binding.imgOne.setImageResource(R.drawable.ic_radio_fill)
                binding.llAllTogether.visibility = View.GONE
                binding.spaceTop.visibility = View.VISIBLE
                binding.spaceBottom.visibility = View.GONE
            }
            R.id.llRadioAllTogether -> {
                isSingle = false
                binding.imgAll.setImageResource(R.drawable.ic_radio_fill)
                binding.imgOne.setImageResource(R.drawable.ic_radio)
                binding.llAllTogether.visibility = View.VISIBLE
                binding.spaceTop.visibility = View.GONE
                binding.spaceBottom.visibility = View.VISIBLE
            }
        }
    }

    private fun checkPermissions() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                generatePDF()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }
        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).check()
    }


    private fun generatePDF() {
        showProgress(activity)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val list: ArrayList<String> = ArrayList()


        val singlePDF: String

        if (false) {
            singlePDF = getString(R.string.onebyone)
            list.add(packID)
        } else {
            singlePDF = getString(R.string.alltogether)
            val substring = packID.substring(0, packID.length - 1)
            val split = substring.split(",")
            for (i in split.indices) {
                list.add(split[i])
            }
        }

        val viewList: MutableList<View> = ArrayList()

        for (i in 0 until list.size) {
            val content: View = inflater.inflate(R.layout.row_pdf_item, null)
            val textView = content.findViewById<TextView>(R.id.txtID)
            val imgView = content.findViewById<ImageView>(R.id.imgView)
            textView.text = list[i]
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(
                    list[i], BarcodeFormat.CODE_128,
                    Constants.BAR_CODE_WIDTH, Constants.BAR_CODE_HEIGHT
                )
               imgView.setImageBitmap(Utility.createBitmap(bitMatrix))
            } catch (e: WriterException) {
                Logger.e(Logger.TAG, "" + e.message)
            }
            viewList.add(content)
        }

        PdfGenerator.getBuilder()
            .setContext(activity)
            .fromViewSource()
            .fromViewList(viewList)
            .setFileName(System.currentTimeMillis().toString() + "_" + singlePDF)
            .setFolderNameOrPath("PackIDs/PDF/")
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse) {
                    hideProgress()
                    super.onFailure(failureResponse)
                }

                override fun onStartPDFGeneration() {

                }

                override fun onFinishPDFGeneration() {
                    hideProgress()
                }

                override fun showLog(log: String) {
                    super.showLog(log)
                }

                override fun onSuccess(response: SuccessResponse) {
                    val bundle = Bundle()
                    bundle.putString(Constants.PO_NUMBER, poNumber)
                    bundle.putString(Constants.PO_NAME, poName)
                    bundle.putBoolean(Constants.IS_UN_KNOW, isUnKnow)
                    bundle.putBoolean(Constants.IS_SINGLE, isSingle)
                    bundle.putString(Constants.apiKeys.PACK_ID, packID)
                    bundle.putString(Constants.FILE_NAME, response.path)
                    navigation.navigate(R.id.assignLocationFragment, bundle)
                    super.onSuccess(response)
                }
            })
    }


    private fun viewPackIDScreen() {
//        val bundle = Bundle()
//        bundle.putString(Constants.PO_NUMBER, poNumber)
//        bundle.putString(Constants.PO_NAME, poName)
//        bundle.putBoolean(Constants.IS_UN_KNOW, isUnKnow)
//        bundle.putBoolean(Constants.IS_SINGLE, isSingle)
//        bundle.putString(Constants.apiKeys.PACK_ID, packID)
//        bundle.putBoolean(Constants.FROM_DRAWER, false)
//        navigation.navigate(R.id.packIDOperationFragment, bundle)
    }


    private fun setApiName(apiName: String, totalPack: String, poNumber: String) {
        showProgress(activity)

        val params: MutableMap<String, String> = HashMap()

        if (poNumber.isNotEmpty()) {
            params[Constants.apiKeys.PO] = poNumber
        }
        if (totalPack.isNotEmpty()) {
            params[Constants.apiKeys.TOTAL_PACK] = totalPack
        }
        if (binding.edShipment.text.toString().isNotEmpty()) {
            //Log.e("myTag","shipmentValue === "+binding.edShipment.text.toString())
            params[Constants.apiKeys.SHIPMENT_ID] = binding.edShipment.text.toString()
        }

        params[Constants.apiKeys.LOCATION_ID] = Constants.LOCATION_VALUE

        viewModel.printPackID(apiName, params)
    }

    /**
     * Set observer
     */
    private fun setPrintPackIDObserver() {
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
//                            packID = if (isSingle) {
//                                user.data.packId!!
//                            } else {
//                                for (i in 0 until user.data.printedPackIds!!.size) {
//                                    packIDs.append(user.data.printedPackIds[i]).append(",")
//                                }
//                                packIDs.toString()
//                            }
                            for (i in 0 until user.data.printedPackIds!!.size) {
                                packIDs.append(user.data.printedPackIds[i]).append(",")
                            }
                            packID = packIDs.toString()
                            //
                            hideProgress()
                            checkPermissions()
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it?.let { it1 ->
                       // ViewUtils.snackBar(activity, it1.message!!, dialog!!.window!!.decorView)
                    }
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(
                        activity,
                        getString(R.string.internetconnection),
                        dialog!!.window!!.decorView
                    )
                }
                else -> {}
            }
        }
    }
}