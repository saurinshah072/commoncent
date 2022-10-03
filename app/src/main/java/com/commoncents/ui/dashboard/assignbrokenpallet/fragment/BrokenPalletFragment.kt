package com.commoncents.ui.dashboard.assignbrokenpallet.fragment

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentBrokenpalletBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.assignbrokenpallet.adapter.BrokenPalletImageAdapter
import com.commoncents.ui.dashboard.assignbrokenpallet.adapter.PrintPackIDAdapter
import com.commoncents.ui.dashboard.assignbrokenpallet.model.BrokenResultsItem
import com.commoncents.ui.dashboard.assignbrokenpallet.viewmodel.PrintPackIDViewModel
import com.commoncents.utils.*
import com.commoncents.view.dialog.SelectMorePhotoDialog
import com.commoncents.view.dialog.SelectPhotoDialog
import com.commoncents.view.dialog.SuccessDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class BrokenPalletFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentBrokenpalletBinding
    private lateinit var brokenPalletImageAdapter: BrokenPalletImageAdapter
    private lateinit var printPackIDAdapter: PrintPackIDAdapter
    private val viewModel: PrintPackIDViewModel by viewModels()
    private var imagesList: ArrayList<BrokenResultsItem> = ArrayList()
    private lateinit var activity: DashboardActivity

    private var quickScanID: String = ""
    private var packID: String = ""
    private var locationID: String = ""
    private var isLocation: Boolean = true
    private var isDamage: Boolean = true
    private var fromDrawer: Boolean = true



    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrokenpalletBinding.inflate(inflater, container, false)
        setListeners()
        readArguments()
        activity.hideUnHideUnKnowPO(false)
        return binding.root
    }

    private fun readArguments() {
        arguments?.getBoolean(Constants.FROM_DRAWER)?.let {
            fromDrawer = it
        }

        if(fromDrawer){
            setPackIDListingObserver()
            setUploadImageObserver()
            setValidateLocationObserver()
            loadApis()
        }else {
            arguments?.getString(Constants.apiKeys.PACK_ID)?.let {
                packID = it
            }

            arguments?.getString(Constants.ID)?.let {
                quickScanID = it
            }

            arguments?.getString(Constants.Location)?.let {
                locationID = it
            }

            binding.includepallet.txtTitle.visibility = View.GONE

            binding.includepallet.edPackID.setText(packID)
            binding.includepallet.edLocation.setText(locationID)

            binding.includepallet.edPackID.isEnabled = false
            binding.includepallet.edLocation.isEnabled = false

            binding.includepallet.rlPackIDQrCode.visibility = View.GONE
            binding.includepallet.rlLocationQrCode.visibility = View.GONE
            binding.imgAddPhoto.visibility = View.GONE
            binding.includeBottom.btnAssign.visibility = View.GONE
            binding.includeBottom.txtAssignLater.visibility = View.GONE
            getBrokenImageListingObserver()
            getBrokenPalletImages("1")
        }
    }

    private fun getBrokenPalletImages(quickScanID: String) {
        viewModel.getBrokenPalletImages(quickScanID)
    }


    private fun loadApis() {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.apiKeys.ASC] = true.toString()
        viewModel.packIDListing(params,true)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigation.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.PrefKeys.qrCode)
            ?.observe(viewLifecycleOwner) {
                if (isLocation) {
                    if (it.toString().isNotEmpty()) {
                        locationID = it.toString()
                    }
                    binding.includepallet.edLocation.setText(it.toString())
                } else {
                    if (it.toString().isNotEmpty()) {
                        packID = it.toString()
                    }
                    binding.includepallet.edPackID.setText(it.toString())
                }
            }
        super.onViewCreated(view, savedInstanceState)
    }


    /**
     * Set Click listeners
     */


    private fun setListeners() {
        binding.includeBottom.btnAssign.setOnClickListener(this)
        binding.imgAddPhoto.setOnClickListener(this)


        binding.includepallet.rlLocationQrCode.setOnClickListener(this)
        binding.includepallet.rlPackIDQrCode.setOnClickListener(this)
        binding.includeBottom.txtAssignLater.setOnClickListener(this)

        binding.includepallet.edLocation.addTextChangedListener(object :
            TextWatcher {
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
                if (s.isNotEmpty()) {
                    locationID = s.toString()
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BrokenPalletFragment().apply {
            }
    }

    /**
     * Click listeners
     */


    override fun onClick(v: View?) {
        when (v?.id) {

            binding.includepallet.rlLocationQrCode.id -> {
                isLocation = true
                navigation.navigate(R.id.qrcodeScanFragment)
            }
            binding.includepallet.rlPackIDQrCode.id -> {
                isLocation = false
                navigation.navigate(R.id.qrcodeScanFragment)
            }

            binding.includeBottom.txtAssignLater.id -> {
                navigation.popBackStack()
            }

            binding.includeBottom.btnAssign.id -> {
                if (checkValidation()) {
                    viewModel.validateLocation(locationID)
                }
            }


            binding.imgAddPhoto.id -> {

                SelectPhotoDialog(context!!, object : SelectPhotoDialogListener {
                    override fun onCameraClick() {
                        checkPermission(Constants.Camera)
                    }

                    override fun onGalleryClick() {
                        checkPermission(Constants.Gallery)
                    }

                    override fun onDriveClick() {
                        checkPermission(Constants.Drive)
                    }
                }).show()
            }
        }
    }

    lateinit var intent: Intent
    fun checkPermission(tag: String) {
        Constants.REQUEST_CODE = tag
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                when (tag) {
                    Constants.Camera -> {
                        selectCameraIntent()
                    }
                    Constants.Gallery -> {
                        selectGalleryIntent()
                    }
                    Constants.Drive -> {
                        selectDriveIntent()
                    }
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }

        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            . setPermissions(Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).check()
    }


    private fun selectDriveIntent() {
        intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        imagePickUpResult.launch(intent)
    }

    private fun selectGalleryIntent() {
        intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imagePickUpResult.launch(intent)
    }

    private fun selectCameraIntent() {
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imagePickUpResult.launch(intent)
    }


    private fun setupBrokenImageList() {
        brokenPalletImageAdapter = BrokenPalletImageAdapter(
            activity,
            imagesList,
            object : BrokenPalletImageAdapter.ViewHolderClicks {
                override fun onClickItem(position: Int) {
                    imagesList.removeAt(position)
                    setData()
                    brokenPalletImageAdapter.setData(imagesList)
                }
            })
        binding.recycleViewPhotos.adapter = brokenPalletImageAdapter
        setData()
    }

    private fun setData() {
        if (imagesList.isNotEmpty()) {
            binding.recycleViewPhotos.visibility = View.VISIBLE
        } else {
            binding.recycleViewPhotos.visibility = View.INVISIBLE
            binding.recycleViewPhotos.visibility = View.INVISIBLE
        }
    }


    private fun prepareMultipartArray(): Array<MultipartBody.Part?> {
        val multipartTypedOutput = arrayOfNulls<MultipartBody.Part>(imagesList.size)
        for (index in 0 until imagesList.size) {
            imagesList[index].let {
                val path = Uri.parse(it.uri)
                val file = FileUtils.getFileFromUri(context!!, path)
                Logger.e("path ::", "+++$path")
                Logger.e("file ::", "+++" + file.length())

                file.let {
                    val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    multipartTypedOutput[index] =
                        MultipartBody.Part.createFormData("images", file.path, requestImageFile)
                }
            }
        }
        return multipartTypedOutput
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
                            viewModel.uploadBrokenImages(
                                packID.toRequestBody(),
                                locationID.toRequestBody(),
                                isDamage.toString().toRequestBody(),
                                prepareMultipartArray()
                            )
                        }else{
                            ViewUtils.snackBar(activity,getString(R.string.valid_location), view!!)
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



    private val imagePickUpResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (Constants.REQUEST_CODE == Constants.Gallery ||
                    Constants.REQUEST_CODE == Constants.Drive
                ) {
                    getImagesList(it.data)
                } else if (Constants.REQUEST_CODE == Constants.Camera) {
                    val bitmap = it.data!!.extras!!.get("data") as Bitmap
                    val fileFromUri =
                        FileUtils.getFileFromUri(context, FileUtils.getImageUri(context, bitmap))
                    val resultsItem = BrokenResultsItem()
                    resultsItem.uri = fileFromUri.path
                    imagesList.add(resultsItem)
                    Logger.e("fileFromUri", "fileFromUri :" + resultsItem.uri)

                    SelectMorePhotoDialog(
                        context!!
                    ) { _: Boolean, _: String ->
                        selectCameraIntent()
                    }.show()
                }
                setupBrokenImageList()
            }
        }

    /**
     * get image list from intent
     */

    private fun getImagesList(data: Intent?) {
        if (data!!.data != null) {
            val mImageUri: Uri = data.data!!
            val resultsItem = BrokenResultsItem()
            resultsItem.uri = mImageUri.toString()
            imagesList.add(resultsItem)
        } else {
            if (data.clipData != null) {
                val mClipData: ClipData = data.clipData!!
                for (i in 0 until mClipData.itemCount) {
                    val item = mClipData.getItemAt(i)
                    val resultsItem = BrokenResultsItem()
                    resultsItem.uri = item.uri.toString()
                    imagesList.add(resultsItem)
                }
            }
        }
    }


    /**
     *  Check validation on login button click
     */

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(packID.trim())) {
            ViewUtils.snackBar(activity,getString(R.string.enter_packID), view!!)
            return false
        }
        if (TextUtils.isEmpty(locationID.trim())) {
            ViewUtils.snackBar(activity,getString(R.string.enter_enter_location), view!!)
            return false
        }
        return true
    }


    /**
     * Set upload broken images
     */
    private fun setUploadImageObserver() {
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
                                context!!, Constants.Pallet,
                                "",
                                context!!.getString(R.string.palletMarket), true
                            ) { _: Boolean, _: String ->
                                navigation.popBackStack()
                            }.show()
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
                            printPackIDAdapter = PrintPackIDAdapter(
                                activity,
                                user.data.results!!
                            )
                            binding.includepallet.edPackID.setAdapter(printPackIDAdapter)
                            binding.includepallet.edPackID.onItemClickListener =
                                AdapterView.OnItemClickListener { parent, _, position, _ ->
                                    val selected =
                                        parent.getItemAtPosition(position) as com.commoncents.ui.dashboard.assignbrokenpallet.model.ResultsItem
                                    packID = selected.packId!!
                                    Logger.e("selected -->>", "-->>$packID")
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
                    ViewUtils.snackBar(activity,getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }


    /**
     * get broken imagesList
     */
    private fun getBrokenImageListingObserver() {
        viewModel.imageListingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    it.data?.let { user ->
                        user.data?.results?.let {
                            imagesList = ArrayList(user.data.results)
                            setupBrokenImageList()
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
}