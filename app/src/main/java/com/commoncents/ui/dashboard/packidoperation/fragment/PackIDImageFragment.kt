package com.commoncents.ui.dashboard.packidoperation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentPackidImagesBinding
import com.commoncents.network.Status
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.packidoperation.adapter.PackIDImageAdapter
import com.commoncents.ui.dashboard.packidoperation.viewModel.DeleteImageVM
import com.commoncents.ui.dashboard.packidoperation.viewModel.QuickScanPhotoVM
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class PackIDImageFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentPackidImagesBinding
    private lateinit var packIDImageAdapter: PackIDImageAdapter
    private lateinit var activity: DashboardActivity
    private val viewModel: QuickScanPhotoVM by viewModels()
    private val deleteViewModel : DeleteImageVM by viewModels()
    var imageIDList = ArrayList<String>()
    private val navigation: NavController by lazy {
        findNavController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackidImagesBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        initView()
        setQuickScanPhotoObserver()
        setDeleteIMageObserver()
        loadApis()
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
            PackIDImageFragment().apply {}
    }


    private fun initView() {
        Log.e("MyTagScan",arguments?.getString("scan_id").toString())
        binding.packidImageRemoveAcb.setOnClickListener(this)
        binding.packidImageDeleteAcb.setOnClickListener(this)
    }

    private fun loadApis() {
        val params: MutableMap<String, String> = HashMap()
        params["page"] = "1"
        viewModel.qiuckScanPhotoListing(arguments?.getString("scan_id").toString(),params)
    }


    /**
     * Set QuickScanPhotoObeserver
     */
    private fun setQuickScanPhotoObserver() {
        viewModel.qiuckScanPhotoResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { photoData ->
                        photoData.data?.let {
                            packIDImageAdapter = PackIDImageAdapter(
                                activity!!,
                                photoData.data?.results,
                                false,
                                object : PackIDImageAdapter.ViewHolderClicks{
                                    override fun onClickItem(position: Int) {
                                        Log.e("myTagImage","Full Image open"+navigation)
                                        val images = ArrayList<String>()
                                        photoData.data.results!!.forEach{
                                            images.add(it?.image!!)
                                        }
                                        if(images.size == photoData.data.results!!.size){
                                            val bundle = Bundle()
                                            bundle.putString("position",position.toString());
                                            bundle.putStringArrayList("photoData", images);
                                            navigation.navigate(R.id.fullImageFragment,bundle)
                                        }
                                    }

                                    override fun onRemoveImage(imageId: Int) {
                                        //
                                        if(!imageIDList.contains(imageId.toString())){
                                            imageIDList.add(imageId.toString())
                                        }else{
                                            imageIDList.remove(imageId.toString())
                                        }
                                        //
                                        if(imageIDList.size > 0){
                                            binding.packidImageDeleteAcb.visibility = View.VISIBLE
                                        }else{
                                            binding.packidImageDeleteAcb.visibility = View.GONE
                                        }
                                        //
                                    }
                                }
                            )
                            binding.recycleViewPackIdImages.layoutManager = GridLayoutManager(activity,3)
                            binding.recycleViewPackIdImages.adapter = packIDImageAdapter
                            //Log.e("myTagAPI2", photoData.data.results!!.toString())
                            //  navigation.navigate(R.id.qrcodeScanFragment)
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


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.packid_image_remove_acb ->{
                packIDImageAdapter.removeImages(true)
            }
            R.id.packid_image_delete_acb ->{
                //
                val jsonObject = JSONObject()
                val jsonArrayObj = JSONArray()
                //
                jsonArrayObj.put(imageIDList)
                //
                jsonObject.put("photo_ids", jsonArrayObj)
                // Convert JSONObject to String
                val jsonObjectString = jsonObject.toString()
                //
                deleteViewModel.deleteImageListing(jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull()))
            }
        }
    }
    //

    /**
     * Set DeleteImage Observer
     */
    private fun setDeleteIMageObserver() {
        deleteViewModel.deleteImageResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { user ->
                        Log.e("myTag","message == "+user.data?.message)
                        binding.packidImageDeleteAcb.visibility = View.GONE
                        loadApis()
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
}