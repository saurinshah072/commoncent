package com.commoncents.ui.dashboard.packaging.fragment

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentAssignPackagingBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.assignbrokenpallet.adapter.BrokenPalletImageAdapter
import com.commoncents.ui.dashboard.assignbrokenpallet.model.BrokenResultsItem
import com.commoncents.ui.dashboard.packaging.viewmodel.AssignPackagingVM
import com.commoncents.utils.*
import com.commoncents.view.dialog.SelectMorePhotoDialog
import com.commoncents.view.dialog.SelectPhotoDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class AssignPackagingFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentAssignPackagingBinding
    private lateinit var brokenPalletImageAdapter: BrokenPalletImageAdapter
    private lateinit var activity: DashboardActivity
    private val viewModel: AssignPackagingVM by viewModels()
    private var imagesList: ArrayList<BrokenResultsItem> = ArrayList()
    private val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadImageBG>()
    private val navigation: NavController by lazy {
        findNavController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignPackagingBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        //
        initView()
        setAssignLocationDataObserver()
        //
        return binding.root
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
            AssignPackagingFragment().apply {}
    }


    private fun initView() {
        binding.assignPackagingBrokenCb.setOnCheckedChangeListener{
                buttonView, isChecked ->
            if (isChecked){
                binding.assignPackagingImageUploadLl.visibility = View.VISIBLE
            }else{
                binding.assignPackagingImageUploadLl.visibility = View.GONE
            }
        }
        binding.assignPackagingSubmitAcb.setOnClickListener(this)
        binding.assignPackagingSubmitRl.setOnClickListener(this)
        binding.assignPackagingAddPhotoIv.setOnClickListener(this)
        //
        binding.assignPackagingBrokenInclude.rlPackIDQrCode.setOnClickListener(this)
        binding.assignPackagingBrokenInclude.rlLocationQrCode.setOnClickListener(this)
        //
    }

    private fun loadApis() {
        //
        val jsonObject = JSONObject()
        val jsonArrayObj = JSONArray()
        //
        val packIds  = binding.assignPackagingBrokenInclude.edPackID.text.toString()
        //
        jsonArrayObj.put(packIds)
        //
        jsonObject.put("pack_ids", jsonArrayObj)
        jsonObject.put("location_id", "19-B-11")//binding.assignPackagingBrokenInclude.edLocation.text.toString()
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        //
        viewModel.assignPackagingData(jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull()))
    }

    /**
     * Set Assign Location Observer
     */
    private fun setAssignLocationDataObserver() {
        viewModel.assignPackagingModelResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { user ->
                        //user.data?.id
                        Log.e("myTag","test ==>> "+it.data?.toString())
//                        var imageListArray = ArrayList<String>()
//                        imagesList.forEach {
//                            imageListArray.add(it!!.image!!)
//                        }
//                        val data =  Data.Builder()
//                        data.put("imageData",imageListArray)
//                        uploadWorkRequest.setInputData(data.build())
//                        WorkManager.getInstance(activity).enqueue(uploadWorkRequest.build())
                    }
                }
                Status.ERROR -> {
                    //Log.e("myTag","test2 ==>> "+it.code+" "+it.errorItem.toString())
                    Toast.makeText(activity,it.errorItem!!.get(0).detail,Toast.LENGTH_SHORT).show()
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

    private val imagePickUpResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (Constants.REQUEST_CODE == Constants.Gallery ||
                    Constants.REQUEST_CODE == Constants.Drive
                ) {
                    getImagesList(it.data)
                } else if (Constants.REQUEST_CODE == Constants.Camera) {
                    Log.e("myTag"," test ==> "+it.data!!.extras!!.get("data"))
                    val bitmap = it.data!!.extras!!.get("data") as Bitmap
//                    val fileFromUri =
//                        FileUtils.getFileFromUri(context, FileUtils.getImageUri(context, bitmap))
                    val resultsItem = BrokenResultsItem()
                    resultsItem.isCamera = true
                    resultsItem.bitmap = bitmap
//                    resultsItem.uri = "file:/"+fileFromUri.absolutePath
                    imagesList.add(resultsItem)
                    //Logger.e("fileFromUri", "fileFromUri :" + resultsItem.uri)

                    SelectMorePhotoDialog(
                        context!!
                    ) { _: Boolean, _: String ->
                        selectCameraIntent()
                    }.show()
                }
                setupBrokenImageList()
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
            . setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
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
        //(intent.resolveActivity(activity.packageManager) !== null)
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
        binding.assignPackagingPhotoRv.adapter = brokenPalletImageAdapter
        setData()
    }

    private fun setData() {
        if (imagesList.isNotEmpty()) {
            binding.assignPackagingPhotoRv.visibility = View.VISIBLE
        } else {
            binding.assignPackagingPhotoRv.visibility = View.INVISIBLE
            binding.assignPackagingPhotoRv.visibility = View.INVISIBLE
        }
    }

    /**
     * get image list from intent
     */

    private fun getImagesList(data: Intent?) {
        if (data!!.data != null) {
            val mImageUri: Uri = data.data!!
            val resultsItem = BrokenResultsItem()
            resultsItem.isCamera = false
            resultsItem.uri = mImageUri.toString()
            imagesList.add(resultsItem)
        } else {
            if (data.clipData != null) {
                val mClipData: ClipData = data.clipData!!
                for (i in 0 until mClipData.itemCount) {
                    val item = mClipData.getItemAt(i)
                    val resultsItem = BrokenResultsItem()
                    resultsItem.uri = item.uri.toString()
                    resultsItem.isCamera = false
                    imagesList.add(resultsItem)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.assign_packaging_submit_acb ->{
              //loadApis()
                val imageListArray = arrayOfNulls<String>(imagesList.size)
                imagesList.forEachIndexed {
                    index, element ->
                    imageListArray[index] = element!!.uri!!
                }

                val data =  Data.Builder()
                data.putStringArray("imageData",imageListArray)
                uploadWorkRequest.setInputData(data.build())
                WorkManager.getInstance(activity).enqueue(uploadWorkRequest.build())
            }

            R.id.assign_packaging_add_photo_iv ->{
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

            R.id.rlPackIDQrCode ->{
                navigation.navigate(R.id.qrcodeScanFragment)
            }
            R.id.rlLocationQrCode ->{
                navigation.navigate(R.id.qrcodeScanFragment)
            }
        }
    }
}